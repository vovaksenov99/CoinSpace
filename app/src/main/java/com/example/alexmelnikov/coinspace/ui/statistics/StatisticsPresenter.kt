package com.example.alexmelnikov.coinspace.ui.statistics

import android.util.Log
import com.example.alexmelnikov.coinspace.BaseApp
import com.example.alexmelnikov.coinspace.model.entities.Account
import com.example.alexmelnikov.coinspace.model.entities.OperationType
import com.example.alexmelnikov.coinspace.model.getCategoryByString
import com.example.alexmelnikov.coinspace.model.getCurrencyByString
import com.example.alexmelnikov.coinspace.model.interactors.CurrencyConverter
import com.example.alexmelnikov.coinspace.model.interactors.IUserBalanceInteractor
import com.example.alexmelnikov.coinspace.model.interactors.Money
import com.example.alexmelnikov.coinspace.model.repositories.AccountsRepository
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject


class StatisticsPresenter : StatisticsContract.Presenter {

    private lateinit var view: StatisticsContract.View

    @Inject
    lateinit var accountsRepository: AccountsRepository

    @Inject
    lateinit var userBalanceInteractor: IUserBalanceInteractor

    @Inject
    lateinit var currencyConverter: CurrencyConverter

    private var accounts: List<Account> = ArrayList()

    override fun attach(view: StatisticsContract.View) {
        this.view = view
        BaseApp.instance.component.inject(this)
    }

    override fun chartDataRequest() {
        accountsDataRequest()
    }

    private fun accountsDataRequest() {
        accountsRepository.getAccountsOffline()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ accountsList -> handleSuccessAccountsRequest(accountsList) },
                { handleErrorAccountsRequest() })
    }

    private fun handleSuccessAccountsRequest(accounts: List<Account>) {
        this.accounts = accounts
        updateCategoryChart()
    }

    private fun handleErrorAccountsRequest() {
        Log.d("mytag", "cant't get data from db")
    }

    private fun updateCategoryChart() {
        val chartEntries: ArrayList<PieEntry> = ArrayList()
        val categorySums: HashMap<String, Float> = HashMap()
        var operationSum = 0f
        var overallSum = 0f

        val mainCurrency = userBalanceInteractor.getUserBalance().currency
        accounts.forEach {
            it.operations.forEach {

                val from = view.getFromDate().time.time
                val to = view.getToDate().time.time

                if (it.date in from..to) {
                    if (it.type == OperationType.EXPENSE.toString()) {

                        operationSum = currencyConverter.convertCurrency(Money(it.sum,
                            getCurrencyByString(it.currency)), mainCurrency).count

                        overallSum += operationSum

                        if (!categorySums.contains(it.category)) {
                            categorySums[it.category] = operationSum
                        }
                        else {
                            var prevSum = categorySums[it.category]!!
                            prevSum += operationSum
                            categorySums[it.category] = prevSum
                        }
                    }
                }
            }
        }

        categorySums.forEach {
            val name = BaseApp.instance.getString(getCategoryByString(it.key).getStringResource())
            chartEntries.add(PieEntry(it.value, name))
        }

        val dataSet = PieDataSet(chartEntries, "")
        view.setupChartData(dataSet)

    }
}