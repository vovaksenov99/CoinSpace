package com.example.alexmelnikov.coinspace.ui.home

import com.example.alexmelnikov.coinspace.di.component.DaggerModelComponent
import com.example.alexmelnikov.coinspace.model.Accountant
import com.example.alexmelnikov.coinspace.model.Operation
import com.example.alexmelnikov.coinspace.util.TextUtils
import javax.inject.Inject

/**
 * HomePresenter handles HomeFragment and OperationFragment
 */
class HomePresenter : HomeContract.Presenter {

    @Inject
    lateinit var mAccountant: Accountant

    private lateinit var mHomeView: HomeContract.HomeView
    private var mOperationView: HomeContract.OperationView? = null
    private var currentNewOperation: Operation.OperationType? = null

    override lateinit var mainCurrency: Operation.Currency
    override var balanceUsd: Float = 0.00f

    override fun attach(view: HomeContract.HomeView) {
        mHomeView = view
        DaggerModelComponent.builder().build().inject(this)
    }

    override fun attachOperationView(view: HomeContract.OperationView) {
        mOperationView = view
        mOperationView?.presenter = this
    }

    override fun detachOperationView() {
        mOperationView = null
    }

    override fun textViewsSetupRequest(mainCurrency: Operation.Currency, balanceUsd: Float) {
        this.mainCurrency = mainCurrency
        this.balanceUsd = balanceUsd
        mAccountant.updateBalance(balanceUsd, Operation.Currency.USD)

        mHomeView.setupTextViews(
                TextUtils.formatToMoneyString(mAccountant.convertCurrencyFromTo(balanceUsd, Operation.Currency.USD, mainCurrency), mainCurrency),
                TextUtils.formatToMoneyString(balanceUsd, Operation.Currency.USD))
    }

    override fun newOperationButtonClick() {
        if (mOperationView == null) {
            mHomeView.openOperationFragmentRequest()
            mHomeView.animateNewOperationButtonToCheck()
        } else {
            if (mOperationView!!.confirmOperationAndCloseSelf()) {
                mHomeView.animateNewOperationButtonToAdd()
            }
        }
    }

    override fun newExpenseButtonClick() {
        currentNewOperation = Operation.OperationType.EXPENSE
        mOperationView?.setupNewOperationLayout(Operation.OperationType.EXPENSE)
    }

    override fun newIncomeButtonClick() {
        currentNewOperation = Operation.OperationType.INCOME
        mOperationView?.setupNewOperationLayout(Operation.OperationType.INCOME)
    }

    override fun newOperationRequest(sum: Float, currency: Operation.Currency) {
        when (currentNewOperation) {
            Operation.OperationType.EXPENSE -> mAccountant.addExpense(sum, currency)
            Operation.OperationType.INCOME -> mAccountant.addIncome(sum, currency)
        }
        balanceUsd = mAccountant.getBalanceUsd().also {
            mHomeView.saveNewBalance(it)
            mHomeView.setupTextViews(
                    TextUtils.formatToMoneyString(mAccountant.convertCurrencyFromTo(it, Operation.Currency.USD, mainCurrency), mainCurrency),
                    TextUtils.formatToMoneyString(it, Operation.Currency.USD))
        }
        currentNewOperation = null
    }
}