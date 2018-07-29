package com.example.alexmelnikov.coinspace.ui.statistics

import android.util.Log
import com.example.alexmelnikov.coinspace.BaseApp
import com.example.alexmelnikov.coinspace.model.entities.Account
import com.example.alexmelnikov.coinspace.model.interactors.IUserBalanceInteractor
import com.example.alexmelnikov.coinspace.model.repositories.AccountsRepository
import com.github.mikephil.charting.data.PieEntry
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

/**
 *  Created by Alexander Melnikov on 29.07.18.
 *  TODO: Edit class header comment
 */

class StatisticsPresenter : StatisticsContract.Presenter {

    private lateinit var view: StatisticsContract.View

    @Inject
    lateinit var accountsRepository: AccountsRepository

    @Inject
    lateinit var userBalanceInteractor: IUserBalanceInteractor

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
    }

    private fun handleErrorAccountsRequest() {
        Log.d("mytag", "cant't get data from db")
    }

    private fun updateCategoryChart() {
        //TODO: ДОДЕЛАТЬ
        /*val chartEntries: ArrayList<PieEntry> = ArrayList()
        var sum = 0f
        val mainCurrency = userBalanceInteractor.getUserBalance().currency
        accounts.forEach {
            sum = 0f
            it.operations.forEach {
                sum += if (it.currency != mainCurrency)
                    userBalanceInteractor.convertCurrencyFromTo(it.sum, it.currency, mainCurrency)
                else it.sum
            }
        }*/
    }
}