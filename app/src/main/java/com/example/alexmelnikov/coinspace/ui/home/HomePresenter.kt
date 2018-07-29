package com.example.alexmelnikov.coinspace.ui.home

import android.text.TextUtils
import android.util.Log
import com.example.alexmelnikov.coinspace.BaseApp
import com.example.alexmelnikov.coinspace.model.entities.Account
import com.example.alexmelnikov.coinspace.model.entities.Operation
import com.example.alexmelnikov.coinspace.model.interactors.IUserBalanceInteractor
import com.example.alexmelnikov.coinspace.model.repositories.AccountsRepository
import com.example.alexmelnikov.coinspace.util.formatToMoneyString
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

/**
 * HomePresenter handles HomeFragment and OperationFragment
 */
class HomePresenter : HomeContract.Presenter {

    @Inject
    lateinit var accountsRepository: AccountsRepository

    @Inject
    lateinit var userBalanceInteractor: IUserBalanceInteractor

    private lateinit var mHomeView: HomeContract.HomeView
    private var mOperationView: HomeContract.OperationView? = null
    private var currentNewOperation: Operation.OperationType? = null

    private var mainCurrency: String = ""
    private var userBalance: Float = 0.00f

    private lateinit var accounts: List<Account>

    override fun attach(view: HomeContract.HomeView) {
        mHomeView = view
        //DaggerApplicationComponent.builder().build().inject(this)
        BaseApp.instance.component.inject(this)
        if (mOperationView != null) mHomeView.openOperationFragment()
        mOperationView = null
        currentNewOperation = null
    }

    override fun attachOperationView(view: HomeContract.OperationView) {
        mOperationView = view
        mOperationView?.presenter = this
    }

    override fun detachOperationView() {
        mHomeView.closeOperationFragment()
        mOperationView = null
        currentNewOperation = null
        animateOperationAddButtonRequest()
    }

    override fun getMainCurrency(): String {
        return if (!mainCurrency.isEmpty()) mainCurrency
        else {
            val (ub, mainCur) = userBalanceInteractor.getUserBalance()
            mainCurrency = mainCur
            mainCur
        }
    }

    override fun textViewsSetupRequest() {
        val (userBalance, mainCurrency) = userBalanceInteractor.getUserBalance()
        this.userBalance = userBalance
        this.mainCurrency = mainCurrency
        Log.d("mytag", "retrieved: $userBalance :: $mainCurrency")

        updateTextViews()
    }

    override fun newOperationButtonClick() {
        if (mOperationView == null) {
            mHomeView.openOperationFragment()
            mHomeView.animateNewOperationButtonToCheck()
        } else {
            mOperationView!!.confirmOperationAndCloseSelf()
        }
    }

    override fun newExpenseButtonClick() {
        currentNewOperation = Operation.OperationType.EXPENSE
        mOperationView?.setupNewOperationLayout(Operation.OperationType.EXPENSE)
        mOperationView?.animateCloseButtonCloseToBack()
    }

    override fun newIncomeButtonClick() {
        currentNewOperation = Operation.OperationType.INCOME
        mOperationView?.setupNewOperationLayout(Operation.OperationType.INCOME)
        mOperationView?.animateCloseButtonCloseToBack()
    }

    override fun clearButtonClick() {
        if (currentNewOperation == null) {
            detachOperationView()
        } else {
            mOperationView?.resetLayout()
            mOperationView?.animateCloseButtonBackToClose()
            currentNewOperation = null
        }
    }

    override fun newOperationRequest(sum: Float, currency: String) {
        userBalance = userBalanceInteractor.executeNewOperation(currentNewOperation, sum, currency)
        updateTextViews()
        currentNewOperation = null
    }

    override fun animateOperationAddButtonRequest() {
        mHomeView.animateNewOperationButtonToAdd()
    }

    override fun openSettingsActivityRequest() {
        mHomeView.openSettingsActivity()
    }

    override fun showAboutDialogRequest() {
        mHomeView.showAboutDialog()
    }

    override fun accountsButtonClick() {
        mHomeView.openAccountsFragmentRequest()
    }

    override fun accountsDataRequest() {
        accountsRepository.getAccountsOffline()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ accountsList -> handleSuccessAccountsRequest(accountsList) },
                        { handleErrorAccountsRequest() })
    }

    private fun updateTextViews() {
        mHomeView.setupTextViews(
                formatToMoneyString(userBalance, mainCurrency),
                formatToMoneyString(userBalanceInteractor.convertCurrencyFromTo(
                        userBalance, mainCurrency, "USD"), "USD"))
    }


    private fun handleSuccessAccountsRequest(accounts: List<Account>) {
        this.accounts = accounts
        //mHomeView.replaceAccountsData(accounts)
        Log.d("mytag", "success :: $accounts")
    }

    private fun handleErrorAccountsRequest() {
        Log.d("mytag", "cant't get data from db")
    }
}