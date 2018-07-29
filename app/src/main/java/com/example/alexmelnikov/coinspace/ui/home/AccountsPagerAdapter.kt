package com.example.alexmelnikov.coinspace.ui.home

import android.content.Context
import android.support.v4.view.PagerAdapter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.alexmelnikov.coinspace.R
import com.example.alexmelnikov.coinspace.model.entities.Account

/**
 *  Created by Alexander Melnikov on 28.07.18.
 *  TODO: Edit class header comment
 */

class AccountsPagerAdapter(private val mContext: Context,
                         private val mAccounts: ArrayList<Account>,
                           private val mPresenter: HomeContract.Presenter) : PagerAdapter() {

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        if (position == 0) {
            val layout = LayoutInflater.from(mContext).inflate(
                    mContext.resources.getLayout(R.layout.card_current_budget), container, false)
            container.addView(layout)
            return layout
        } else {
            //mAccounts[position + 1]
            val layout = LayoutInflater.from(mContext).inflate(
                    mContext.resources.getLayout(R.layout.card_account_balance), container, false)
            container.addView(layout)
            return layout
        }


    }

    override fun destroyItem(container: ViewGroup, position: Int, view: Any) {
        container.removeView(view as View)
    }

    override fun isViewFromObject(view: View, obj: Any): Boolean {
        return view == obj
    }

    override fun getCount(): Int {
        return mAccounts.size + 1
    }

    //fun addAccounts
}