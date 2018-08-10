package com.example.alexmelnikov.coinspace.ui.home

import android.util.Log
import android.view.View
import com.example.alexmelnikov.coinspace.BaseApp
import com.example.alexmelnikov.coinspace.model.Currency
import com.example.alexmelnikov.coinspace.model.entities.*
import com.example.alexmelnikov.coinspace.model.getCurrencyByString
import com.example.alexmelnikov.coinspace.model.interactors.CurrencyConverter
import com.example.alexmelnikov.coinspace.model.interactors.IUserBalanceInteractor
import com.example.alexmelnikov.coinspace.model.interactors.Money
import com.example.alexmelnikov.coinspace.model.interactors.defaultCurrency
import com.example.alexmelnikov.coinspace.model.repositories.AccountsRepository
import com.example.alexmelnikov.coinspace.model.repositories.IDeferOperationsRepository
import com.example.alexmelnikov.coinspace.model.repositories.IOperationsRepository
import com.example.alexmelnikov.coinspace.model.repositories.IPatternsRepository
import com.example.alexmelnikov.coinspace.ui.home.RepeatedPeriod.DAY
import com.example.alexmelnikov.coinspace.ui.home.RepeatedPeriod.MONTH
import com.example.alexmelnikov.coinspace.ui.home.RepeatedPeriod.NONE
import com.example.alexmelnikov.coinspace.ui.home.RepeatedPeriod.WEEK
import com.example.alexmelnikov.coinspace.util.formatToMoneyString
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.util.*
import javax.inject.Inject


/**
 * HomePresenter handles HomeFragment and OperationFragment
 */
class HomePresenter : HomeContract.Presenter {

    @Inject
    lateinit var accountsRepository: AccountsRepository

    @Inject
    lateinit var IDeferRepositoryRepository: IDeferOperationsRepository

    @Inject
    lateinit var patternsRepository: IPatternsRepository

    @Inject
    lateinit var operationsRepository: IOperationsRepository

    @Inject
    lateinit var userBalanceInteractor: IUserBalanceInteractor

    @Inject
    lateinit var currencyConverter: CurrencyConverter

    lateinit var mHomeView: HomeContract.HomeView
    var mOperationView: HomeContract.OperationView? = null
    private var currentNewOperation: OperationType? = null


    private lateinit var userBalance: Money

    var accounts: MutableMap<Long, Account> = mutableMapOf()

    override fun attach(view: HomeContract.HomeView) {
        mHomeView = view
        accounts = mutableMapOf()
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
        for (account in accounts) {
            this.accounts[account.id!!] = account
        }

        mHomeView.setupViewPager(userBalance, accounts)

        val operations = mutableListOf<Operation>()
        for (account in accounts) {
            operations.addAll(account.operations)
        }

        initOperationsRV(operations)
    }

    fun initOperationsRV(operations: List<Operation>) {

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

    override fun newOperationPatternButtonClick() {
        if (mOperationView == null) {
            mHomeView.openOperationPatternFragment()
            mHomeView.animateNewOperationButtonToCheck()
        }
        else {
            mOperationView!!.confirmOperationAndCloseSelf()
        }
    }

    override fun newExpenseButtonClick() {
        currentNewOperation = OperationType.EXPENSE
        mOperationView?.setupNewOperationLayout(OperationType.EXPENSE, accounts)
        mOperationView?.animateCloseButtonCloseToBack()
    }

    override fun newIncomeButtonClick() {
        currentNewOperation = OperationType.INCOME
        mOperationView?.setupNewOperationLayout(OperationType.INCOME, accounts)
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

    override fun newOperationRequest(operation: Operation, accountId: Int) {
        operationsRepository.insertOperation(operation) {
            val account = accounts[accountId.toLong()]!!
            operation.id = it

            account.operations.add(operation)

            val money = currencyConverter.convertCurrency(Money(operation.sum,
                getCurrencyByString(operation.currency)),
                defaultCurrency)

            val accountMoney = currencyConverter.convertCurrency(Money(account.balance,
                getCurrencyByString(account.currency)),
                defaultCurrency)

            val type =
                if (operation.type == OperationType.INCOME.toString())
                    OperationType.INCOME
                else
                    OperationType.EXPENSE

            if (type == OperationType.INCOME) {
                accountMoney.count += money.count
                userBalance.count += money.count
            }
            else {
                accountMoney.count -= money.count
                userBalance.count -= money.count
            }

            account.balance = currencyConverter.convertCurrency(accountMoney,
                getCurrencyByString(account.currency)).count

            accountsRepository.updateAccountOfflineAsync(account)

            userBalanceInteractor.executeNewOperation(type, money)

            currentNewOperation = null

            updateUIAfterRequest()
        }

    }

    fun updateUIAfterRequest() {
        val accs = accounts.toList().map { it.second }

        val pos = mHomeView.getViewPagerPosition()
        if (pos == 0) {
            val operations = mutableListOf<Operation>()
            for (account in accs)
                operations.addAll(account.operations)
            initOperationsRV(operations)
        }
        else {
            initOperationsRV(accs[pos - 1].operations)
        }

        mHomeView.setupViewPager(userBalance, accs, pos)

    }

    override fun newOperationRequest(sum: Float, account: Account, category: String,
                                     description: String,
                                     currency: String, repeat: Int) {
        if (repeat != NONE) {
            if (currentNewOperation == OperationType.INCOME)
                newRepeatOperationRequest(sum, account, category, description, currency, repeat)
            else
                newRepeatOperationRequest(-sum, account, category, description, currency, repeat)
        }

        val operation = Operation(currentNewOperation!!.toString(),
            sum,
            currency,
            description,
            category,
            account.id,
            null,
            Date().time)

        newOperationRequest(operation, account.id!!.toInt())
    }

    override fun newRemoveOperationRequest(operationId: Long, accountId: Long) {
        operationsRepository.removeOperation(operationId)

        for (operation in accounts[accountId]!!.operations) {
            if (operation.id == operationId) {
                val m = currencyConverter.convertCurrency(Money(operation.sum,
                    getCurrencyByString(operation.currency)), defaultCurrency)

                if (operation.type == OperationType.EXPENSE.toString()) {
                    accounts[accountId]!!.balance += m.count
                    userBalanceInteractor.executeNewOperation(OperationType.INCOME, m)
                    userBalance.count += m.count
                }
                else {
                    accounts[accountId]!!.balance -= m.count
                    userBalanceInteractor.executeNewOperation(OperationType.EXPENSE, m)
                    userBalance.count -= m.count
                }

                accounts[accountId]!!.operations.remove(operation)

                accountsRepository.updateAccountOfflineAsync(accounts[accountId]!!.copy())

                updateUIAfterRequest()
                break
            }
        }
    }


    fun newRepeatOperationRequest(sum: Float, account: Account, category: String,
                                  description: String, currency: String, repeat: Int) {
        Log.d("mytag", "new operation:\n" +
                "account.name = ${account.name}\ncategory = $category\ncurrency = $currency")


        val calendar = Calendar.getInstance()
        when (repeat) {
            DAY -> calendar.add(Calendar.DAY_OF_MONTH, 1)
            WEEK -> calendar.add(Calendar.DAY_OF_MONTH, 7)
            MONTH -> calendar.add(Calendar.MONTH, 1)
        }


        val nextRepeatDay = calendar.get(Calendar.DAY_OF_MONTH)
        val nextRepeatMonth = calendar.get(Calendar.MONTH)
        val nextRepeatYear = calendar.get(Calendar.YEAR)

        val operation = DeferOperation(null,
            description,
            nextRepeatDay,
            currency,
            account.id!!,
            nextRepeatMonth,
            nextRepeatYear,
            repeat,
            sum,
            category)

        IDeferRepositoryRepository.addNewOperation(operation)
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
        initOperationsRV(account.operations)
    }

    override fun initPatternsRV() {
        patternsRepository.getAllPatterns().subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ patterns -> mHomeView.initPatternsRv(patterns) },
                { handleErrorAccountsRequest() })

    }

    override fun addNewPattern(pattern: Pattern, account: Account) {
        pattern.type = currentNewOperation.toString()
        currentNewOperation = null
        patternsRepository.insertPattern(pattern) {
            initOperationsRV(account.operations)
            initPatternsRV()
        }

    }

}