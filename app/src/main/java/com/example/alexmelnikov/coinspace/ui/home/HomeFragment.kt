package com.example.alexmelnikov.coinspace.ui.home

import android.content.Context
import android.graphics.drawable.AnimatedVectorDrawable
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.view.ViewPager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.method.LinkMovementMethod
import android.util.Log
import android.view.*
import android.widget.TextView
import com.afollestad.materialdialogs.MaterialDialog
import com.example.alexmelnikov.coinspace.BaseApp
import com.example.alexmelnikov.coinspace.R
import com.example.alexmelnikov.coinspace.di.component.DaggerFragmentComponent
import com.example.alexmelnikov.coinspace.di.module.FragmentModule
import com.example.alexmelnikov.coinspace.model.entities.Account
import com.example.alexmelnikov.coinspace.model.entities.Operation
import com.example.alexmelnikov.coinspace.model.entities.Pattern
import com.example.alexmelnikov.coinspace.model.getCurrencyByString
import com.example.alexmelnikov.coinspace.model.interactors.Money
import com.example.alexmelnikov.coinspace.ui.home.AccountsPagerAdapter.Companion.BALANCE_VIEW_TAG
import com.example.alexmelnikov.coinspace.ui.main.MainActivity
import com.example.alexmelnikov.coinspace.util.formatToMoneyString
import kotlinx.android.synthetic.main.card_account_balance.view.*
import kotlinx.android.synthetic.main.card_current_budget.view.*
import kotlinx.android.synthetic.main.fragment_home.*
import javax.inject.Inject


class HomeFragment : Fragment(), HomeContract.HomeView {

    @Inject
    override lateinit var presenter: HomeContract.Presenter

    private var appInfoDialog: MaterialDialog? = null

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        DaggerFragmentComponent.builder()
            .fragmentModule(FragmentModule(activity!!.applicationContext as BaseApp)).build()
            .inject(this)
    }

    override fun getViewPagerPosition(): Int {
        return accounts_viewpager.currentItem
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        presenter.attach(this)
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupEventListeners()

        //Setup toolbar
        home_toolbar.overflowIcon =
                (activity as MainActivity).getDrawable(R.drawable.ic_more_vert_white_24dp)
        (activity as MainActivity).setSupportActionBar(home_toolbar)
        setHasOptionsMenu(true)

    }

    override fun onStart() {
        super.onStart()
        presenter.viewPagerSetupRequest()
        presenter.initPatternsRV()
    }

    private fun setupEventListeners() {
        fab_new_action.setOnClickListener {
            presenter.newOperationButtonClick()
        }
    }

    override fun setupViewPager(balance: Money, accounts: List<Account>, startPage: Int) {
        accounts_viewpager.adapter =
                AccountsPagerAdapter(activity as MainActivity,
                    balance,
                    ArrayList(accounts),
                    presenter)
        accounts_tabDots.setupWithViewPager(accounts_viewpager, true)

        accounts_viewpager.currentItem = startPage

        accounts_viewpager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(p0: Int) {

            }

            override fun onPageScrolled(p0: Int, p1: Float, p2: Int) {
            }

            override fun onPageSelected(p0: Int) {
                if (p0 == 0) {
                    (operation_rv.adapter as OperationAdapter).updateData(accounts.flatMap { it.operations }.toMutableList())
                    return
                }
                (operation_rv.adapter as OperationAdapter).updateData(accounts[p0 - 1].operations)
            }
        })
    }

    override fun setupOperationsAdapter(operations: List<Operation>) {
        val layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        operation_rv.setHasFixedSize(true)
        operation_rv.layoutManager = layoutManager
        operation_rv.isNestedScrollingEnabled = true

        operation_rv.adapter = OperationAdapter(operations.toMutableList(), presenter)

        if (operations.isNotEmpty()) {
            lbl_empty_operation_history.visibility = View.INVISIBLE
        }
        //accounts_tabDots.setupWithViewPager(accounts_viewpager, true)
    }

    override fun updateUserBalanceItemPagerView(mainBalance: String, additionalBalance: String) {
        val balanceView = accounts_viewpager.findViewWithTag<View>(BALANCE_VIEW_TAG)
        if (balanceView != null) {
            balanceView.tv_main_cur_balance.text = mainBalance
            balanceView.tv_additional_cur_balance.text = additionalBalance
        }
    }

    override fun updateAccountItemPagerView(account: Account) {
        val balanceView = accounts_viewpager.findViewWithTag<View>(account.id)
        if (balanceView != null)
            balanceView.tv_account_balance.text = formatToMoneyString(Money(account.balance,
                getCurrencyByString(account.currency)))
    }

    override fun openOperationFragment() {
        closeOperationFragment()
        val fragment = OperationFragment.newInstance(fab_new_action)
        fragmentManager?.beginTransaction()
            ?.replace(R.id.actionFrame, fragment)
            ?.commit()
    }

    override fun openOperationPatternFragment() {
        closeOperationFragment()
        val fragment = OperationPatternFragment.newInstance(fab_new_action)
        fragmentManager?.beginTransaction()
            ?.replace(R.id.actionFrame, fragment)
            ?.commit()
    }

    override fun closeOperationFragment() {
        val fragment = fragmentManager?.findFragmentById(R.id.actionFrame)
        try {
            if (fragment != null && fragment is HomeContract.OperationView) {
                fragmentManager?.beginTransaction()
                    ?.remove(fragment)
                    ?.commit()
            }
        } catch (exp: IllegalStateException) {
            Log.e("exception", "can't commit remove fragment transaction after onSaveInstanceState")
        }
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
                .build()
            appInfoDialog!!.view.findViewById<TextView>(R.id.tv_content).movementMethod =
                    LinkMovementMethod.getInstance()
        }
        appInfoDialog?.show()
    }

    override fun openSettingsActivity() {
        (activity as MainActivity).openSettingsActivityRequest()
    }

    override fun openAccountsFragmentRequest() {
        (activity as MainActivity).openAccountsFragmentRequest()
    }

    override fun openStatisticsFragmentRequest(animationCenter: View) {
        (activity as MainActivity).openStatisticsFragmentRequest(animationCenter)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.about -> {
                presenter.showAboutDialogRequest()
                return true
            }
            R.id.accounts -> {
                presenter.accountsButtonClick()
                return true
            }
            R.id.statistics -> {
                presenter.statisticsButtonClick(home_toolbar)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun animateNewOperationButtonToCheck() {
        val drawable =
            activity?.getDrawable(R.drawable.anim_ic_add_to_check_white) as AnimatedVectorDrawable
        fab_new_action.setImageDrawable(drawable)
        drawable.start()
    }

    override fun animateNewOperationButtonToAdd() {
        try {


            val drawable =
                activity?.getDrawable(R.drawable.anim_ic_check_to_add_white) as AnimatedVectorDrawable
            fab_new_action.setImageDrawable(drawable)
            drawable.start()
        } catch (e: Exception) {

        }
    }

    override fun initPatternsRv(patterns: List<Pattern>) {
        try {
            val layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
            patterns_rv.setHasFixedSize(true)
            patterns_rv.layoutManager = layoutManager
            patterns_rv.isNestedScrollingEnabled = true

            patterns_rv.adapter = PatternsAdapter(context!!, presenter, patterns.toMutableList())
        }
        catch (e:Exception)
        {
            Log.i("",e.toString())
        }
    }

    companion object {
        fun newInstance() = HomeFragment()
    }

}