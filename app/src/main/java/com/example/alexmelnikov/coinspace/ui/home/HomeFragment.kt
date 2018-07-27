package com.example.alexmelnikov.coinspace.ui.home

import android.content.Context
import android.graphics.drawable.AnimatedVectorDrawable
import android.media.VolumeShaper
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v4.app.Fragment

import android.support.v7.preference.PreferenceManager
import android.support.v7.widget.Toolbar
import android.text.method.LinkMovementMethod
import android.util.Log
import android.view.*
import android.widget.TextView
import com.afollestad.materialdialogs.MaterialDialog
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
    private var appInfoDialog: MaterialDialog? = null

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        DaggerFragmentComponent.builder().build().inject(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        presenter.attach(this)

        val root = inflater.inflate(R.layout.fragment_home, container, false)

        // Views initialization
        mNewOperationBtn = root.findViewById(R.id.fab_new_action)
        mMainBalanceText = root.findViewById(R.id.tv_main_cur_balance)
        mAdditionalBalanceText = root.findViewById(R.id.tv_additional_cur_balance)

        //Get main currency and balance and set it into presenter
        with (PreferenceManager.getDefaultSharedPreferences(activity)) {
            presenter.textViewsSetupRequest(TextUtils.stringToCurrency(this.getString(getString(R.string.sp_key_main_currency), "RUB")),
                    this.getFloat(getString(R.string.sp_key_balance), 0.00f))
        }


        //Setup toolbar
        val mToolbar = root.findViewById<Toolbar>(R.id.home_toolbar)
        mToolbar.overflowIcon = (activity as MainActivity).getDrawable(R.drawable.ic_more_vert_white_24dp)
        (activity as MainActivity).setSupportActionBar(mToolbar)
        setHasOptionsMenu(true)

        setupEventListeners()

        return root
    }

    override fun onStart() {
        super.onStart()

        //Get fresh finance data from storage and give it to presenter in case when user
        //opens gets back to this fragment from SettingsActivity where he changed main currency
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

    override fun openOperationFragment() {
        closeOperationFragment()
        val fragment = OperationFragment.newInstance(mNewOperationBtn)
        fragmentManager?.beginTransaction()
                ?.replace(R.id.actionFrame, fragment)
                ?.commit()
    }

    override fun closeOperationFragment() {
        val fragment = fragmentManager?.findFragmentById(R.id.actionFrame)
        try {
            if (fragment != null) {
                fragmentManager?.beginTransaction()
                        ?.remove(fragment)
                        ?.commit()
            }
        } catch (exp: IllegalStateException) {
            Log.e("exception", "can't commit remove fragment transaction after onSaveInstanceState")
        }
    }

    override fun saveNewBalance(sum: Float) {
        val prefs = PreferenceManager.getDefaultSharedPreferences(activity)
        val editor = prefs.edit()
        editor.putFloat(getString(R.string.sp_key_balance), sum)
        editor.apply()
    }


    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater?.inflate(R.menu.toolbar_main, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun showAboutDialog() {
        if (appInfoDialog == null) {
            //Setup dialog with application info
            appInfoDialog = MaterialDialog.Builder(activity!!)
                    .customView(R.layout.dialog_app_info, false)
                    .positiveText(android.R.string.ok)
                    //.dismissListener()
                    .build()
            appInfoDialog!!.view.findViewById<TextView>(R.id.tv_content).movementMethod = LinkMovementMethod.getInstance()
        }
        appInfoDialog?.show()
    }

    override fun openSettingsActivity() {
        (activity as MainActivity).openSettingsActivityRequest()
    }

    override fun openAccountsFragmentRequest() {
        (activity as MainActivity).openAccountsFragment()
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when {
            item?.itemId == R.id.settings -> {
                presenter.openSettingsActivityRequest()
                return true
            }
            item?.itemId == R.id.about -> {
                presenter.showAboutDialogRequest()
                return true
            }
            item?.itemId == R.id.accounts -> {
                presenter.accountsButtonClick()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
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