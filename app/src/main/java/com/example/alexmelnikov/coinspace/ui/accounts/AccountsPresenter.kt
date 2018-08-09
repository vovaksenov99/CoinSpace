package com.example.alexmelnikov.coinspace.ui.accounts

import android.util.Log
import com.example.alexmelnikov.coinspace.BaseApp
import com.example.alexmelnikov.coinspace.model.entities.Account
import com.example.alexmelnikov.coinspace.model.getCurrencyByString
import com.example.alexmelnikov.coinspace.model.interactors.CurrencyConverter
import com.example.alexmelnikov.coinspace.model.interactors.IUserBalanceInteractor
import com.example.alexmelnikov.coinspace.model.interactors.Money
import com.example.alexmelnikov.coinspace.model.interactors.defaultCurrency
import com.example.alexmelnikov.coinspace.model.repositories.AccountsRepository
import com.example.alexmelnikov.coinspace.model.repositories.IDeferOperationsRepository
import com.example.alexmelnikov.coinspace.model.repositories.IOperationsRepository
import com.example.alexmelnikov.coinspace.model.repositories.IPatternsRepository
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class AccountsPresenter : AccountsContract.Presenter {


    private lateinit var view: AccountsContract.AccountsView

    @Inject
    lateinit var accountsRepository: AccountsRepository

    @Inject
    lateinit var patternsRepository: IPatternsRepository

    @Inject
    lateinit var operationsRepository: IOperationsRepository

    @Inject
    lateinit var currencyConverter: CurrencyConverter

    @Inject
    lateinit var defererOperationsRepository: IDeferOperationsRepository

    @Inject
    lateinit var balanceInteractor: IUserBalanceInteractor


    override fun attach(view: AccountsContract.AccountsView) {
        this.view = view
        BaseApp.instance.component.inject(this)
    }

    override fun addNewAccountButtonClick() {
        view.openAddAccountFragment()
    }

    override fun accountsDataRequest() {
        accountsRepository.getAccountsOffline()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ accountsList -> handleSuccessAccountsRequest(accountsList) },
                { handleErrorAccountsRequest() })
    }

    private fun handleSuccessAccountsRequest(accounts: List<Account>) {
        view.replaceAccountsRecyclerData(accounts)
        Log.d("mytag", "accounts req success :: $accounts")
    }

    private fun handleErrorAccountsRequest() {
        Log.d("mytag", "cant't get data from db")
    }

    override fun removeAccount(account: Account) {
        var money = Money(account.balance, getCurrencyByString(account.currency))
        money = currencyConverter.convertCurrency(money, defaultCurrency)
        money = Money(balanceInteractor.getUserBalance().count - money.count, defaultCurrency).copy()
        Log.i("Updated balance request", "${money}")

        balanceInteractor.setBalance(money)

        val id = account.id!!
        patternsRepository.removePatternByAccountId(id)
        defererOperationsRepository.removeOperationsByAccountId(id)
        operationsRepository.removeOperationsByAccountId(id)
        accountsRepository.removeByAccountId(id)
    }
}