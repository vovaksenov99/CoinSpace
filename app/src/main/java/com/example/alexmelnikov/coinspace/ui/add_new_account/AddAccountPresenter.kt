package com.example.alexmelnikov.coinspace.ui.add_new_account

import android.util.Log
import com.example.alexmelnikov.coinspace.BaseApp
import com.example.alexmelnikov.coinspace.di.component.DaggerApplicationComponent
import com.example.alexmelnikov.coinspace.model.repositories.AccountsRepository
import com.example.alexmelnikov.coinspace.model.repositories.DefaultAccountsRepository
import com.example.alexmelnikov.coinspace.util.PreferencesHelper
import com.example.alexmelnikov.coinspace.util.PreferencesHelper.Companion.MAIN_CURRENCY
import javax.inject.Inject

class AddAccountPresenter : AddAccountContract.Presenter {

    lateinit var view: AddAccountContract.View

    @Inject
    lateinit var preferencesHelper: PreferencesHelper

    @Inject
    lateinit var accountsRepository: AccountsRepository

    override var selectedColor: Int = 0

    override fun attach(view: AddAccountContract.View) {
        this.view = view
        BaseApp.instance.component.inject(this)
    }

    override fun mainCurrencyRequest(): String = preferencesHelper.loadString(MAIN_CURRENCY)

    override fun showColorPickerRequest() {
        view.showColorPickerDialog()
    }

    override fun updateSelectedColor(color: Int) {
        selectedColor = color
        view.updateLayoutBgColor(selectedColor)
    }

    override fun addNewAccountButtonClick(name: String, currency: String) {
        accountsRepository.insertAccountOffline(name = name, currency = currency,
                color = selectedColor)
        view.closeSelf()

    }
}