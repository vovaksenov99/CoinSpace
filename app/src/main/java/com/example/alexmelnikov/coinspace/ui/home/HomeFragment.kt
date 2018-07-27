package com.example.alexmelnikov.coinspace.ui.home

import android.graphics.drawable.AnimatedVectorDrawable
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v4.app.Fragment
import android.support.v7.preference.PreferenceManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.alexmelnikov.coinspace.R
import com.example.alexmelnikov.coinspace.di.component.DaggerFragmentComponent
import com.example.alexmelnikov.coinspace.ui.main.MainActivity
import com.example.alexmelnikov.coinspace.util.TextUtils
import javax.inject.Inject


class HomeFragment : Fragment(), HomeContract.HomeView {

    @Inject
    override lateinit var presenter: HomeContract.Presenter

    private lateinit var mNewOperationBtn: FloatingActionButton
    private lateinit var mMainBalanceText: TextView
    private lateinit var mAdditionalBalanceText: TextView

    private fun injectDependency() {
        val homeComponent = DaggerFragmentComponent.builder().build()
        homeComponent.inject(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        injectDependency()
        presenter.attach(this)

        val root = inflater.inflate(R.layout.fragment_home, container, false)

        // Views initialization
        mNewOperationBtn = root.findViewById(R.id.fab_new_action)
        mMainBalanceText = root.findViewById(R.id.tv_main_cur_balance)
        mAdditionalBalanceText = root.findViewById(R.id.tv_additional_cur_balance)

        setupEventListeners()

        return root
    }

    override fun onStart() {
        super.onStart()

        //Get fresh finance data from storage and give it to presenter
        with (PreferenceManager.getDefaultSharedPreferences(activity)) {
            presenter.textViewsSetupRequest(TextUtils.stringToCurrency(this.getString(getString(R.string.sp_key_main_currency), "RUB")),
                    this.getFloat(getString(R.string.sp_key_balance), 0.00f))
        }

    }

    private fun setupEventListeners() {
        mNewOperationBtn.setOnClickListener {
            presenter.newOperationButtonClick()
        }
    }

    override fun setupTextViews(mainBalance: String, additionalBalance: String) {
        mMainBalanceText.text = mainBalance
        mAdditionalBalanceText.text = additionalBalance
    }

    override fun openOperationFragmentRequest() {
        val actionFragment = (activity as MainActivity).openActionFragment(mNewOperationBtn)
        presenter.attachOperationView(actionFragment)
    }

    override fun saveNewBalance(sum: Float) {
        val prefs = PreferenceManager.getDefaultSharedPreferences(activity)
        val editor = prefs.edit()
        editor.putFloat(getString(R.string.sp_key_balance), sum)
        editor.apply()
    }

    override fun animateNewOperationButtonToCheck() {
        //TODO: look for getDrawable future proofed substitution
        val drawable = resources.getDrawable(R.drawable.anim_ic_add_to_check_white) as AnimatedVectorDrawable
        mNewOperationBtn.setImageDrawable(drawable)
        drawable.start()
    }

    override fun animateNewOperationButtonToAdd() {
        //TODO: look for getDrawable future proofed substitution
        val drawable = resources.getDrawable(R.drawable.anim_ic_check_to_add_white) as AnimatedVectorDrawable
        mNewOperationBtn.setImageDrawable(drawable)
        drawable.start()
    }

    companion object {

        fun newInstance() = HomeFragment()

    }

}