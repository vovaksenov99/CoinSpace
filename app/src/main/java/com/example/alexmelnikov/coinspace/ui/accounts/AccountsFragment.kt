package com.example.alexmelnikov.coinspace.ui.accounts

import android.content.Context
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.Toolbar
import android.transition.ChangeBounds
import android.transition.TransitionInflater
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.alexmelnikov.coinspace.BaseApp
import com.example.alexmelnikov.coinspace.R
import com.example.alexmelnikov.coinspace.di.component.DaggerFragmentComponent
import com.example.alexmelnikov.coinspace.di.module.FragmentModule
import com.example.alexmelnikov.coinspace.model.entities.Account
import com.example.alexmelnikov.coinspace.ui.add_new_account.AddAccountFragment
import com.example.alexmelnikov.coinspace.ui.main.MainActivity
import kotlinx.android.synthetic.main.fragment_accounts.*
import javax.inject.Inject


class AccountsFragment : Fragment(), AccountsContract.AccountsView {

    @Inject
    override lateinit var presenter: AccountsContract.Presenter

    private lateinit var accountsAdapter: AccountsAdapter

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        DaggerFragmentComponent.builder()
            .fragmentModule(FragmentModule(activity!!.applicationContext as BaseApp)).build()
            .inject(this)    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        presenter.attach(this)
        return inflater.inflate(R.layout.fragment_accounts, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupEventListeners()

        //Setup toolbar
        accounts_toolbar.overflowIcon = (activity as MainActivity).getDrawable(R.drawable.ic_more_vert_white_24dp)
        (activity as MainActivity).setSupportActionBar(accounts_toolbar)
        setHasOptionsMenu(true)
        accounts_toolbar.title = resources.getString(R.string.accounts_fragment_title)
        accounts_toolbar.navigationIcon = resources.getDrawable(R.drawable.ic_arrow_back_white_24dp)
        accounts_toolbar.setNavigationOnClickListener { (activity as MainActivity).onBackPressed() }

        //Setup Accounts Recycler View
        accountsAdapter = AccountsAdapter(activity as MainActivity, ArrayList(), presenter)
        accountsAdapter.setHasStableIds(true)
        rv_accounts.setHasFixedSize(true)
        rv_accounts.adapter = accountsAdapter
        rv_accounts.layoutManager = AccountsLinearLayoutManager(activity as MainActivity)
    }

    override fun onStart() {
        super.onStart()
        presenter.accountsDataRequest()
    }

    private fun setupEventListeners() {
        fab_new_account.setOnClickListener {
            presenter.addNewAccountButtonClick()
        }

    }

    override fun replaceAccountsRecyclerData(accounts: List<Account>) {
        accountsAdapter.replaceData(accounts)
    }

    override fun openAddAccountFragment() {
        val fragment = AddAccountFragment.newInstance(sourceView = fab_new_account)
        val changeBoundsTransition = ChangeBounds()
        changeBoundsTransition.duration = 370
        exitTransition = TransitionInflater.from(activity).inflateTransition(android.R.transition.fade)
        fragment.sharedElementReturnTransition = changeBoundsTransition

        fragmentManager?.beginTransaction()
                ?.replace(R.id.contentFrame, fragment)
                ?.addToBackStack(null)
                ?.addSharedElement(fab_new_account, "fabAdd")
                ?.commit()
    }

    companion object {

        fun newInstance() = AccountsFragment()

    }
}