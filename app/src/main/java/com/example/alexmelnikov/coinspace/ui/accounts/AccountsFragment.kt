package com.example.alexmelnikov.coinspace.ui.accounts

import android.content.Context
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v4.app.Fragment
import android.transition.ChangeBounds
import android.transition.TransitionInflater
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.alexmelnikov.coinspace.R
import com.example.alexmelnikov.coinspace.di.component.DaggerFragmentComponent
import com.example.alexmelnikov.coinspace.ui.add_new_account.AddAccountFragment
import javax.inject.Inject


class AccountsFragment : Fragment(), AccountsContract.AccountsView {

    @Inject
    override lateinit var presenter: AccountsContract.Presenter

    private lateinit var mAddNewAccountBtn: FloatingActionButton

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        DaggerFragmentComponent.builder().build().inject(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        presenter.attach(this)

        val root = inflater.inflate(R.layout.fragment_accounts, container, false)

        mAddNewAccountBtn = root.findViewById(R.id.fab_new_account)
        setupEventListeners()

        return root
    }

    fun setupEventListeners() {
        mAddNewAccountBtn.setOnClickListener {
            presenter.addNewAccountButtonClick()
        }
    }

    override fun openAddAccountFragment() {
        val fragment = AddAccountFragment.newInstance(sourceView = mAddNewAccountBtn)
        val changeBoundsTransition = ChangeBounds()
        changeBoundsTransition.duration = 370
        exitTransition = TransitionInflater.from(activity).inflateTransition(android.R.transition.fade)
        fragment.sharedElementReturnTransition = changeBoundsTransition

        fragmentManager?.beginTransaction()
                ?.replace(R.id.contentFrame, fragment)
                ?.addToBackStack(null)
                ?.addSharedElement(mAddNewAccountBtn, "fabAdd")
                ?.commit()
    }

    companion object {

        fun newInstance() = AccountsFragment()

    }
}