package com.example.alexmelnikov.coinspace.ui.home

import android.content.Context
import android.graphics.PorterDuff
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.SpinnerAdapter
import com.example.alexmelnikov.coinspace.R
import com.example.alexmelnikov.coinspace.model.entities.Account
import kotlinx.android.synthetic.main.spinner_item_account.view.*

/**
 *  Created by Alexander Melnikov on 29.07.18.
 */

class AccountsSpinnerAdapter(private val mContext: Context,
                             private val mData: List<Account>) : BaseAdapter(), SpinnerAdapter {

    override fun getView(i: Int, convertView: View?, viewGroup: ViewGroup?): View {
        val view: View = convertView ?: LayoutInflater.from(mContext)
                .inflate(R.layout.spinner_item_account, viewGroup, false)
        val account = mData[i]
        view.tv_account_name.text = account.name

        if (account.name != mContext.resources.getString(R.string.cash_account_name)) {
            view.iv_account_icon.setImageResource(R.drawable.ic_credit_card_primary_24dp)
            view.iv_account_icon.setColorFilter(account.color, PorterDuff.Mode.SRC_ATOP)
        } else {
            view.iv_account_icon.setImageResource(R.drawable.ic_account_balance_wallet_primary_24dp)
        }
        return view
    }

    override fun getItem(p0: Int): Any {
        return mData[p0]
    }

    override fun getItemId(p0: Int): Long {
        return mData[p0].hashCode().toLong()
    }

    override fun getCount(): Int {
        return mData.size
    }
}