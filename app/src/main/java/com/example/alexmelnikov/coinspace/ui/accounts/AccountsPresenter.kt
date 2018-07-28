package com.example.alexmelnikov.coinspace.ui.accounts

import android.util.Log
import com.example.alexmelnikov.coinspace.BaseApp
import com.example.alexmelnikov.coinspace.model.entities.Account
import com.example.alexmelnikov.coinspace.model.repositories.AccountsRepository
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class AccountsPresenter : AccountsContract.Presenter {

    private lateinit var view: AccountsContract.AccountsView

    @Inject
    lateinit var accountsRepository: AccountsRepository

    private lateinit var accounts: List<Account>

    override fun attach(view: AccountsContract.AccountsView) {
        this.view = view
        BaseApp.instance.component.inject(this)
    }

    override fun addNewAccountButtonClick() {
        view.openAddAccountFragment()
    }

    override fun accountsDataRequest() {
        Log.d("mytag", "request")

        accountsRepository.getAccountsOffline()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({acs -> handleSuccessAccountsRequest(acs)},
                        {handleErrorAccountsRequest()})
    }

    private fun handleSuccessAccountsRequest(accounts: List<Account>) {
        this.accounts = accounts
        view.replaceAccountsRecyclerData(accounts)
        Log.d("mytag", "success :: $accounts")
    }

    private fun handleErrorAccountsRequest() {
        Log.d("mytag", "cant't get data from db")
    }
}