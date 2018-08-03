package com.example.alexmelnikov.coinspace.ui.home

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.Context
import android.graphics.drawable.AnimatedVectorDrawable
import android.os.Bundle
import android.support.v4.app.Fragment
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo
import com.example.alexmelnikov.coinspace.R
import com.example.alexmelnikov.coinspace.ui.RevealCircleAnimatorHelper
import java.util.*
import android.text.Editable
import android.view.inputmethod.InputMethodManager
import com.example.alexmelnikov.coinspace.model.entities.Account
import com.example.alexmelnikov.coinspace.model.entities.Operation
import com.example.alexmelnikov.coinspace.model.interactors.Category
import com.example.alexmelnikov.coinspace.ui.main.MainActivity
import kotlinx.android.synthetic.main.fragment_operation.*


class OperationFragment : Fragment(), HomeContract.OperationView {

    override lateinit var presenter: HomeContract.Presenter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.fragment_operation, container, false)
        RevealCircleAnimatorHelper
                .create(this, container)
                .start(root)

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupEventListeners()

        //animate operation card sliding up
        rl_expense_card.postDelayed({
            rl_expense_card.visibility = View.VISIBLE
            YoYo.with(Techniques.SlideInUp)
                    .duration(300)
                    .playOn(rl_expense_card)
        }, 350)

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

        /*et_sum.setOnEditorActionListener { p0, p1, p2 ->
            presenter.newOperationButtonClick()
            false
        }*/

        et_sum.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {}

            override fun beforeTextChanged(s: CharSequence, start: Int,
                                           count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int,
                                       before: Int, count: Int) {
                if (input_layout_sum.isErrorEnabled) input_layout_sum.error = ""
            }
        })
    }

    override fun setupNewOperationLayout(type: Operation.OperationType, accounts: List<Account>) {
        when (type) {
            Operation.OperationType.EXPENSE -> tv_label.text = getString(R.string.label_new_expense)
            Operation.OperationType.INCOME -> tv_label.text = getString(R.string.label_new_income)
        }

        accounts_spinner.adapter = AccountsSpinnerAdapter(activity as MainActivity, ArrayList(accounts))
        category_spinner.adapter = ArrayAdapter<String>(activity,android.R.layout.simple_spinner_dropdown_item,
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
        val spinnerArrayAdapter = ArrayAdapter<String>(activity, android.R.layout.simple_spinner_item, currencies)
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        currency_spinner.adapter = spinnerArrayAdapter
        currency_spinner.setSelection(currencies.indexOf(presenter.getMainCurrency().toString()))
    }

    override fun onStop() {
        super.onStop()
        presenter.detachOperationView()
    }

    /**
     * If user input is suitable, animate operation card sliding out
     * and call popbackstack when animation ends or canceled
     *
     */
    override fun confirmOperationAndCloseSelf(){
        if (et_sum.text.toString().trim().isEmpty()) {
            input_layout_sum.error = getString(R.string.empty_sum_error)
        } else if (et_sum.text.toString().trim().toFloat() <= 0f) {
            input_layout_sum.error = getString(R.string.zero_sum_error)
        } else {
            presenter.newOperationRequest(et_sum.text.toString().trim().toFloat(),
                    accounts_spinner.selectedItem as Account,
                    category_spinner.selectedItem.toString(),
                    currency_spinner.selectedItem.toString())

            YoYo.with(Techniques.SlideOutUp)
                    .duration(300)
                    .withListener(object : AnimatorListenerAdapter() {
                        override fun onAnimationCancel(animation: Animator) {
                            super.onAnimationCancel(animation)
                            rl_expense_card.postDelayed({presenter.detachOperationView()}, 50)
                        }

                        override fun onAnimationEnd(animation: Animator) {
                            super.onAnimationEnd(animation)
                            rl_expense_card.postDelayed({presenter.detachOperationView()}, 50)
                        }
                    })
                    .playOn(rl_expense_card)
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
        fun newInstance(sourceView: View? = null) = OperationFragment().apply {
            arguments = Bundle()
            RevealCircleAnimatorHelper.addBundleValues(arguments!!, sourceView)
        }
    }

}