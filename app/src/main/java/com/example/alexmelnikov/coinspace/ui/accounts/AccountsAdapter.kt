package com.example.alexmelnikov.coinspace.ui.accounts

import android.content.Context
import android.graphics.PorterDuff
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import com.example.alexmelnikov.coinspace.R
import com.example.alexmelnikov.coinspace.model.entities.Account
import com.example.alexmelnikov.coinspace.model.interactors.Money
import com.example.alexmelnikov.coinspace.model.interactors.getCurrencyByString
import com.example.alexmelnikov.coinspace.util.formatToMoneyString
import kotlinx.android.synthetic.main.item_accounts_list.view.*


class AccountsAdapter(private val mContext: Context,
                      private val mData: ArrayList<Account>,
                      private val mEventsListener: AccountsAdapter.AccountsAdapterEventsListener
                      ) : RecyclerView.Adapter<AccountsAdapter.AccountsViewHolder>() {


    class AccountsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tvAccountName: TextView = itemView.tv_account_name
        var tvBalance: TextView = itemView.tv_balance
        var ivCard: ImageView = itemView.iv_card
        var cbSelect: CheckBox = itemView.cb_select

    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AccountsViewHolder {
        //TODO: check how to implement view init in view holder (Ошибки и советы в лекции)
        return AccountsViewHolder(LayoutInflater.from(mContext)
                .inflate(R.layout.item_accounts_list, parent, false))
    }

    override fun onBindViewHolder(holder: AccountsViewHolder, position: Int) {
        val account = mData[position]
        holder.tvAccountName.text = account.name
        holder.tvBalance.text = formatToMoneyString(Money(account.balance, getCurrencyByString(account.currency)))

        //Set colored credit card image view
        if (account.name != mContext.resources.getString(R.string.cash_account_name)) {
            holder.ivCard.setImageResource(R.drawable.ic_credit_card_primary_24dp)
            holder.ivCard.setColorFilter(account.color, PorterDuff.Mode.SRC_ATOP)
        } else {
            holder.ivCard.setImageResource(R.drawable.ic_account_balance_wallet_primary_24dp)
        }
    }

    //TODO: implement checkbox with payload
    /*override funy onBindViewHolder(holder: AccountsViewHolder, position: Int, payloads: MutableList<Any>) {
        if (payloads.isEmpty()) onBindViewHolder(holder, position)
        else //обновление holder.tv_text.text = payloads[0] as String
    }*/

    override fun getItemCount() = mData.size

    override fun getItemId(position: Int): Long {
        return mData[position].hashCode().toLong()
    }

    fun replaceData(accounts: List<Account>) {
        mData.clear()
        mData.addAll(accounts)
        notifyDataSetChanged()
    }

    interface AccountsAdapterEventsListener {}
}