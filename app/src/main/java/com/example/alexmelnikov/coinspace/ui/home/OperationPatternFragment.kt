package com.example.alexmelnikov.coinspace.ui.home

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.Context
import android.graphics.drawable.AnimatedVectorDrawable
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo
import com.example.alexmelnikov.coinspace.R
import com.example.alexmelnikov.coinspace.model.Category
import com.example.alexmelnikov.coinspace.model.entities.Account
import com.example.alexmelnikov.coinspace.model.entities.OperationType
import com.example.alexmelnikov.coinspace.model.entities.Pattern
import com.example.alexmelnikov.coinspace.model.getCategoryByString
import com.example.alexmelnikov.coinspace.model.getCurrencyByString
import com.example.alexmelnikov.coinspace.ui.RevealCircleAnimatorHelper
import com.example.alexmelnikov.coinspace.ui.home.RepeatedPeriod.DAY
import com.example.alexmelnikov.coinspace.ui.home.RepeatedPeriod.MONTH
import com.example.alexmelnikov.coinspace.ui.home.RepeatedPeriod.NONE
import com.example.alexmelnikov.coinspace.ui.home.RepeatedPeriod.WEEK
import com.example.alexmelnikov.coinspace.ui.main.MainActivity
import kotlinx.android.synthetic.main.fragment_operation.*

class OperationPatternFragment : Fragment(), HomeContract.OperationView {


    override lateinit var presenter: HomeContract.Presenter

    var selectedRepeat = RepeatedPeriod.NONE

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.fragment_operation_pattern, container, false)
        RevealCircleAnimatorHelper
            .create(this, container)
            .start(root)

        return root
    }

    override fun showPeriodicDialog() {
        val builder = AlertDialog.Builder(context!!)
        builder.setTitle(getString(R.string.choose_periodic))

        val periods = arrayOf(getString(R.string.none),
            getString(R.string.every_day),
            getString(R.string.every_week),
            getString(R.string.every_month))
        builder.setItems(periods) { dialog, which ->
            when (which) {
                0 -> selectedRepeat = NONE
                1 -> selectedRepeat = DAY
                2 -> selectedRepeat = WEEK
                3 -> selectedRepeat = MONTH
            }

        }

        val dialog = builder.create()
        dialog.show()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupEventListeners()

        //animate operation card sliding up
        try {
            rl_expense_card.postDelayed({
                rl_expense_card.visibility = View.VISIBLE
                YoYo.with(Techniques.SlideInUp)
                    .duration(300)
                    .playOn(rl_expense_card)
            }, 350)
        } catch (e: Exception) {

        }

    }

    private fun setupEventListeners() {
        btn_expense.setOnClickListener {
            presenter.newExpenseButtonClick()
        }

        btn_income.setOnClickListener {
            presenter.newIncomeButtonClick()
        }

        btn_back.setOnClickListener {
            presenter.clearButtonClick()
        }

    }

    override fun setupNewOperationLayout(type: OperationType, accounts: MutableMap<Long, Account>) {
        when (type) {
            OperationType.EXPENSE -> tv_label.text =
                    getString(R.string.label_new_expense_pattern)
            OperationType.INCOME -> tv_label.text =
                    getString(R.string.label_new_income_pattern)
        }

        accounts_spinner.adapter =
                AccountsSpinnerAdapter(activity as MainActivity, accounts.map { it.value })
        category_spinner.adapter =
                ArrayAdapter<String>(activity, android.R.layout.simple_spinner_dropdown_item,
                    Category.values().map { context!!.getString(it.getStringResource()) })
        ll_action_type_btns.visibility = View.GONE
        rl_new_action.visibility = View.VISIBLE
    }

    override fun resetLayout() {
        ll_action_type_btns.visibility = View.VISIBLE
        rl_new_action.visibility = View.GONE

        //Close keyboard
        val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view?.windowToken, 0)
    }

    override fun onStart() {
        super.onStart()
        presenter = (fragmentManager?.findFragmentById(R.id.contentFrame) as HomeFragment).presenter
        presenter.attachOperationView(this)

        //setupSpinner
        val currencies = resources.getStringArray(R.array.main_currency_values_array)
        val spinnerArrayAdapter =
            ArrayAdapter<String>(activity, android.R.layout.simple_spinner_item, currencies)
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        currency_spinner.adapter = spinnerArrayAdapter
        currency_spinner.setSelection(currencies.indexOf(presenter.getMainCurrency().toString()))
    }

    override fun onStop() {
        super.onStop()
        presenter.detachOperationView()
    }


    override fun confirmOperationAndCloseSelf() {
        try {
            val account = accounts_spinner.selectedItem as Account
            val currency = getCurrencyByString(currency_spinner.selectedItem.toString())
            val category =
                getCategoryByString(Category.values()[category_spinner.selectedItemPosition].toString())

            val pattern = Pattern(account.id!!.toInt(),
                null,
                null,
                et_desc.text.toString(),
                currency,
                category)
            presenter.addNewPattern(pattern, account)

            YoYo.with(Techniques.SlideOutUp)
                .duration(300)
                .withListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationCancel(animation: Animator) {
                        super.onAnimationCancel(animation)
                        rl_expense_card.postDelayed({
                            presenter.detachOperationView()
                        }, 50)
                    }

                    override fun onAnimationEnd(animation: Animator) {
                        super.onAnimationEnd(animation)
                        rl_expense_card.postDelayed({
                            presenter.detachOperationView()
                        }, 50)
                    }
                })
                .playOn(rl_expense_card)
        } catch (e: Exception) {
            presenter.detachOperationView()
        }
    }


    override fun animateCloseButtonCloseToBack() {
        val drawable = activity?.getDrawable(R.drawable.anim_ic_clear_to_back_primary_light_24dp)
                as AnimatedVectorDrawable
        btn_back.setImageDrawable(drawable)
        drawable.start()
    }

    override fun animateCloseButtonBackToClose() {
        val drawable = activity?.getDrawable(R.drawable.anim_ic_back_to_clear_primary_light_24dp)
                as AnimatedVectorDrawable
        btn_back.setImageDrawable(drawable)
        drawable.start()
    }

    companion object {

        /**
         * @sourceView – circle animation starting point center
         */
        fun newInstance(sourceView: View? = null) = OperationPatternFragment().apply {
            arguments = Bundle()
            RevealCircleAnimatorHelper.addBundleValues(arguments!!, sourceView)
        }
    }

}