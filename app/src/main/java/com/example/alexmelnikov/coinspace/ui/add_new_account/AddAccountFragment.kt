package com.example.alexmelnikov.coinspace.ui.add_new_account

import android.content.Context
import android.graphics.PorterDuff
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v4.app.Fragment
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo
import com.example.alexmelnikov.coinspace.R
import com.example.alexmelnikov.coinspace.di.component.DaggerFragmentComponent
import com.example.alexmelnikov.coinspace.ui.RevealCircleAnimatorHelper
import com.thebluealliance.spectrum.SpectrumDialog
import kotlinx.android.synthetic.main.fragment_add_account.*
import javax.inject.Inject


class AddAccountFragment : Fragment(), AddAccountContract.View {

    @Inject
    override lateinit var presenter: AddAccountContract.Presenter


    override fun onAttach(context: Context?) {
        super.onAttach(context)
        DaggerFragmentComponent.builder().build().inject(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        presenter.attach(this)
        val root = inflater.inflate(R.layout.fragment_add_account, container, false)

        RevealCircleAnimatorHelper
                .create(this, container)
                .start(root)

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        presenter.updateSelectedColor(resources.getColor(R.color.colorPrimary))

        //setup spinner
        val currencies = resources.getStringArray(R.array.main_currency_values_array)
        val spinnerArrayAdapter = ArrayAdapter<String>(activity, R.layout.spinner_item_white_text, currencies)
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        currency_spinner.adapter = spinnerArrayAdapter
        currency_spinner.setSelection(currencies.indexOf((presenter.mainCurrencyRequest().toString())))

        et_account_name.inputType = InputType.TYPE_CLASS_TEXT
        et_account_name.requestFocus()
        et_account_name.postDelayed({
            val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(et_account_name, 0)
        }, 150)

        setupEventListeners()

        //Prevent the soft keyboard from pushing the view above it
        activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)

        view.postDelayed({
            fab_add.show()
            YoYo.with(Techniques.SlideInUp)
                    .duration(400)
                    .playOn(fab_add)
        }, 370)

    }

    override fun closeSelf() {
        val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view?.windowToken, 0)

        view?.postDelayed({fragmentManager?.popBackStackImmediate()}, 300)
    }

    private fun setupEventListeners() {
        btn_change_color.setOnClickListener{
            presenter.showColorPickerRequest()
        }

        fab_add.setOnClickListener {
            val etText = et_account_name.text.toString().trim()
            if (!etText.isEmpty()) {
                presenter.addNewAccountButtonClick(etText, currency_spinner.selectedItem.toString())
            } else {
                showEditTextError(false)
            }
        }

        et_account_name.setOnEditorActionListener { p0, p1, p2 ->
            val etText = et_account_name.text.toString().trim()
            if (!etText.isEmpty()) {
                presenter.addNewAccountButtonClick(etText, currency_spinner.selectedItem.toString())
            } else {
                showEditTextError(false)
            }
             false
        }

        et_account_name.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {}

            override fun beforeTextChanged(s: CharSequence, start: Int,
                                           count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int,
                                       before: Int, count: Int) {
                if (input_layout_account_name.isErrorEnabled) input_layout_account_name.error = ""
            }
        })

    }

    override fun showEditTextError(nameAlreadyPresentInDb: Boolean) {
        if (nameAlreadyPresentInDb) {
            input_layout_account_name.error = resources
                    .getString(R.string.account_name_already_in_db_error)

            YoYo.with(Techniques.Shake)
                    .duration(500)
                    .playOn(et_account_name)

        }
        else input_layout_account_name.error = resources.getString(R.string.empty_account_name_error)
    }

    override fun showColorPickerDialog() {
        SpectrumDialog.Builder(context)
                .setTitle(R.string.label_account_color)
                .setColors(R.array.custom_account_colors)
                .setDismissOnColorSelected(true)
                .setSelectedColor(presenter.selectedColor)
                .setOnColorSelectedListener { pr, color ->  presenter.updateSelectedColor(color)}
                .build()
                .show(fragmentManager, "color_dialog")
    }

    override fun updateLayoutBgColor(color: Int) {
        val drawable = activity?.getDrawable(R.drawable.bg_card)
        drawable?.setColorFilter(color, PorterDuff.Mode.SRC_ATOP)
        rv_account.background = drawable
    }

    companion object {
        fun newInstance(sourceView: View? = null) = AddAccountFragment().apply {
            arguments = Bundle()
            RevealCircleAnimatorHelper.addBundleValues(arguments!!, sourceView)
        }
    }
}