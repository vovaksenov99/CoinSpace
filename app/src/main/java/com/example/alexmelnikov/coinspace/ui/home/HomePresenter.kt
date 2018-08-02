package com.example.alexmelnikov.coinspace.ui.home

import android.util.Log
import android.view.View
import com.example.alexmelnikov.coinspace.BaseApp
import com.example.alexmelnikov.coinspace.model.entities.Account
import com.example.alexmelnikov.coinspace.model.entities.Operation
import com.example.alexmelnikov.coinspace.model.interactors.*
import com.example.alexmelnikov.coinspace.model.interactors.Currency
import com.example.alexmelnikov.coinspace.model.repositories.AccountsRepository
import com.example.alexmelnikov.coinspace.util.formatToMoneyString
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList

/**
 * HomePresenter handles HomeFragment and OperationFragment
 */
class HomePresenter : HomeContract.Presenter {

    @Inject
    lateinit var accountsRepository: AccountsRepository

    @Inject
    lateinit var userBalanceInteractor: IUserBalanceInteractor

    @Inject
    lateinit var currencyConverter: CurrencyConverter

    private lateinit var mHomeView: HomeContract.HomeView
    private var mOperationView: HomeContract.OperationView? = null
    private var currentNewOperation: Operation.OperationType? = null

    private lateinit var userBalance: Money

    private var accounts: List<Account> = ArrayList()
    private var operations: MutableList<Operation> = mutableListOf()

    override fun attach(view: HomeContract.HomeView) {
        mHomeView = view
        BaseApp.instance.component.inject(this)
        if (mOperationView != null) mHomeView.openOperationFragment()

        userBalance = userBalanceInteractor.getUserBalance()
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

    override fun getMainCurrency(): Currency {
        return userBalance.currency
    }


    override fun viewPagerSetupRequest() {
        accountsRepository.getAccountsOffline()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ accountsList -> handleSuccessAccountsRequest(accountsList) },
                { handleErrorAccountsRequest() })

    }

    private fun handleSuccessAccountsRequest(accounts: List<Account>) {
        this.accounts = accounts
        mHomeView.setupViewPager(userBalance, accounts)

        this.operations.clear()
        for(account in accounts)
        {
            this.operations.addAll(account.operations)
        }
        initOperationsRV(operations)
    }

    fun initOperationsRV(operations: List<Operation>)
    {
        mHomeView.setupOperationsAdapter(operations)
    }

    private fun handleErrorAccountsRequest() {
        Log.d("mytag", "cant't get data from db")
    }


    override fun newOperationButtonClick() {
        if (mOperationView == null) {
            mHomeView.openOperationFragment()
            mHomeView.animateNewOperationButtonToCheck()
        }
        else {
            mOperationView!!.confirmOperationAndCloseSelf()
        }
    }

    override fun newExpenseButtonClick() {
        currentNewOperation = Operation.OperationType.EXPENSE
        mOperationView?.setupNewOperationLayout(Operation.OperationType.EXPENSE, accounts)
        mOperationView?.animateCloseButtonCloseToBack()
    }

    override fun newIncomeButtonClick() {
        currentNewOperation = Operation.OperationType.INCOME
        mOperationView?.setupNewOperationLayout(Operation.OperationType.INCOME, accounts)
        mOperationView?.animateCloseButtonCloseToBack()
    }

    override fun clearButtonClick() {
        if (currentNewOperation == null) {
            detachOperationView()
        }
        else {
            mOperationView?.resetLayout()
            mOperationView?.animateCloseButtonBackToClose()
            currentNewOperation = null
        }
    }

    override fun newOperationRequest(sum: Float, account: Account, category: String,
                                     currency: String) {
        Log.d("mytag", "new operation:\n" +
                "account.name = ${account.name}\ncategory = $category\ncurrency = $currency")

        //Create operation and add it to accountOperationsList
        val operation = Operation(currentNewOperation!!, sum, currency, category, Date())
        val updatedAccountOperations: ArrayList<Operation> = ArrayList(account.operations)
        updatedAccountOperations.add(operation)
        account.operations = updatedAccountOperations

        val money = currencyConverter.convertCurrency(Money(sum, getCurrencyByString(currency)),
            defaultCurrency)
        val accountMoney = currencyConverter.convertCurrency(Money(account.balance,
            getCurrencyByString(account.currency)),
            defaultCurrency)

        if (currentNewOperation == Operation.OperationType.INCOME)
            accountMoney.count += money.count
        else
            accountMoney.count -= money.count

        account.balance = currencyConverter.convertCurrency(accountMoney, getCurrencyByString(account.currency)).count
        accountsRepository.updateAccountOfflineAsync(account)

        userBalance =
                userBalanceInteractor.executeNewOperation(currentNewOperation, money)
        updateTextViews()
        initOperationsRV(account.operations)
        updateAccountItemOnPagerView(account)

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

    override fun statisticsButtonClick(animationCenter: View) {
        mHomeView.openStatisticsFragmentRequest(animationCenter)
    }

    private fun updateTextViews() {
        mHomeView.updateUserBalanceItemPagerView(
            formatToMoneyString(userBalance),
            formatToMoneyString(currencyConverter.convertCurrency(userBalance, Currency.RUR)))
    }

    private fun updateAccountItemOnPagerView(account: Account) {
        mHomeView.updateAccountItemPagerView(account)
    }
}