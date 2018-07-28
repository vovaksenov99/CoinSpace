package com.example.alexmelnikov.coinspace.ui.add_new_account

import com.example.alexmelnikov.coinspace.ui.BaseContract
import java.util.*

class AddAccountContract {

    interface View : BaseContract.View {

        var presenter: AddAccountContract.Presenter

        fun showColorPickerDialog()

        fun updateLayoutBgColor(color: Int)

        fun closeSelf()

    }

    //interface View
    interface Presenter : BaseContract.Presenter<View> {

        var selectedColor: Int

        fun mainCurrencyRequest(): String

        fun showColorPickerRequest()

        fun updateSelectedColor(color: Int)

        fun addNewAccountButtonClick(name: String, currency: String)

    }
}