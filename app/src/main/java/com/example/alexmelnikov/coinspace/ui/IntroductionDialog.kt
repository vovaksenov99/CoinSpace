package com.example.alexmelnikov.coinspace.ui

import android.content.Context
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import com.example.alexmelnikov.coinspace.BaseApp
import com.example.alexmelnikov.coinspace.PreferenceStructure
import com.example.alexmelnikov.coinspace.R
import com.example.alexmelnikov.coinspace.di.component.DaggerFragmentComponent
import com.example.alexmelnikov.coinspace.di.module.FragmentModule
import com.example.alexmelnikov.coinspace.model.interactors.defaultCurrency
import com.example.alexmelnikov.coinspace.model.repositories.AccountsRepository
import com.example.alexmelnikov.coinspace.ui.main.MainActivity
import com.example.alexmelnikov.coinspace.util.PreferencesHelper
import com.thebluealliance.spectrum.SpectrumDialog
import kotlinx.android.synthetic.main.introduction_dialog.view.*
import java.util.*
import javax.inject.Inject

val INTRODUCTION_DIALOG_TAG = "PAYMENT_DIALOG_TAG"

class IntroductionDialog : DialogFragment() {

    @Inject
    lateinit var accountsRepository: AccountsRepository


    lateinit var fragmentView: View

    var selectedColor: Int = 0


    override fun onAttach(context: Context?) {
        super.onAttach(context)

        DaggerFragmentComponent.builder()
            .fragmentModule(FragmentModule(activity!!.applicationContext as BaseApp)).build()
            .inject(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setStyle(DialogFragment.STYLE_NORMAL, R.style.FullscreenDialog)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        fragmentView = inflater.inflate(R.layout.introduction_dialog, null)

        initUI()

        return fragmentView
    }

    private fun initUI() {
        fragmentView.apply {
            done.setOnClickListener {
                val pref = PreferencesHelper(context!!)
                pref.saveString(PreferenceStructure.LAST_APP_LOGIN,
                    Calendar.getInstance().toString())
                addNewAccountButtonClick(et_account_name.text.toString(),
                    currency_spinner.selectedItem.toString())

            }

            btn_change_color.setOnClickListener {
                showColorPickerDialog()
            }

            //setup spinner
            val currencies = resources.getStringArray(R.array.main_currency_values_array)
            val spinnerArrayAdapter =
                ArrayAdapter<String>(activity,
                    android.R.layout.simple_spinner_dropdown_item,
                    currencies)

            currency_spinner.adapter = spinnerArrayAdapter
            currency_spinner.setSelection(currencies.indexOf((defaultCurrency.toString())))

            et_account_name.inputType = InputType.TYPE_CLASS_TEXT
            et_account_name.requestFocus()
            et_account_name.postDelayed({
                val imm =
                    activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.showSoftInput(et_account_name, 0)
            }, 150)
        }
    }

    fun showColorPickerDialog() {
        SpectrumDialog.Builder(context)
            .setTitle(R.string.label_account_color)
            .setColors(R.array.custom_account_colors)
            .setDismissOnColorSelected(true)
            .setSelectedColor(selectedColor)
            .setOnColorSelectedListener { pr, color -> updateSelectedColor(color) }
            .build()
            .show(fragmentManager, "color_dialog")
    }

    private fun updateSelectedColor(color: Int) {
        selectedColor = color
        fragmentView.rv_account.setBackgroundColor(selectedColor)
    }

    private fun addNewAccountButtonClick(name: String, currency: String) {
        accountsRepository.insertAccountOfflineAsync(name = name, currency = currency,
            color = selectedColor, callback = {
                (activity as MainActivity).presenter.openHomeFragmentRequest()
                dismiss()
            })
    }

}