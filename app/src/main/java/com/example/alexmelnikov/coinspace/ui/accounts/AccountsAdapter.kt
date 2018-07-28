package com.example.alexmelnikov.coinspace.ui.accounts

import android.content.Context
import android.graphics.PorterDuff
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.alexmelnikov.coinspace.R
import com.example.alexmelnikov.coinspace.model.entities.Account
import com.example.alexmelnikov.coinspace.util.TextUtils
import kotlinx.android.synthetic.main.item_accounts_list.view.*

/**
 *  Created by Alexander Melnikov on 27.07.18.
 *  TODO: Edit class header comment
 */

class AccountsAdapter(private val mContext: Context,
                      private val mData: ArrayList<Account>,
                      private val presenter: AccountsContract.Presenter
                      ) : RecyclerView.Adapter<AccountsAdapter.AccountsViewHolder>() {


    class AccountsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tvAccountName = itemView.tv_account_name
        var tvBalance = itemView.tv_balance
        var ivCard = itemView.iv_card
        var cbSelect = itemView.cb_select
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AccountsViewHolder {
        return AccountsViewHolder(LayoutInflater.from(mContext)
                .inflate(R.layout.item_accounts_list, parent, false))
    }

    override fun onBindViewHolder(holder: AccountsViewHolder, position: Int) {
        val account = mData[position]
        holder.tvAccountName.text = account.name
        holder.tvBalance.text = TextUtils.formatToMoneyString(account.balance,
                TextUtils.stringToCurrency(account.currency))

        //Set colored credit card image view
        holder.ivCard.setImageResource(R.drawable.ic_credit_card_primary_24dp)
        holder.ivCard.setColorFilter(account.color, PorterDuff.Mode.SRC_ATOP)
    }

    override fun getItemCount() = mData.size

    fun replaceData(accounts: List<Account>) {
        mData.clear()
        mData.addAll(accounts)
        notifyDataSetChanged()
    }

    override fun getItemId(position: Int): Long {
        return mData[position].hashCode().toLong()
    }
}