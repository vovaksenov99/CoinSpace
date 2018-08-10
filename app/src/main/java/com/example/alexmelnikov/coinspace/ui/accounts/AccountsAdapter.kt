package com.example.alexmelnikov.coinspace.ui.accounts

import android.content.Context
import android.graphics.PorterDuff
import android.support.v7.app.AlertDialog
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.example.alexmelnikov.coinspace.R
import com.example.alexmelnikov.coinspace.model.entities.Account
import com.example.alexmelnikov.coinspace.model.getCurrencyByString
import com.example.alexmelnikov.coinspace.model.interactors.Money
import com.example.alexmelnikov.coinspace.util.formatToMoneyString
import kotlinx.android.synthetic.main.item_accounts_list.view.*


class AccountsAdapter(private val context: Context,
                      private val accounts: ArrayList<Account>,
                      private val presenter: AccountsContract.Presenter
) : RecyclerView.Adapter<AccountsAdapter.AccountsViewHolder>() {


    inner class AccountsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tvAccountName: TextView = itemView.tv_account_name
        var tvBalance: TextView = itemView.tv_balance
        var ivCard: ImageView = itemView.iv_card

        fun onLongClicked(position: Int) {
            var dialog: AlertDialog? = null
            val builder = AlertDialog.Builder(context)
            val animals =
                arrayOf(context.getString(R.string.delete))
            builder.setItems(animals) { _, which ->
                when (which) {
                    0 -> {
                        presenter.removeAccount(accounts[position])
                        accounts.removeAt(position)
                        notifyItemRemoved(position)
                    }
                }
            }

            dialog = builder.create()
            dialog.show()
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AccountsViewHolder {
        val view = LayoutInflater.from(context)
            .inflate(R.layout.item_accounts_list, parent, false)
        val holder = AccountsViewHolder(view)

        holder.itemView.setOnLongClickListener {
            if(holder.adapterPosition != RecyclerView.NO_POSITION)
            {
                holder.onLongClicked(holder.adapterPosition)
            }
            true
        }
        return holder
    }

    override fun onBindViewHolder(holder: AccountsViewHolder, position: Int) {
        val account = accounts[position]
        holder.tvAccountName.text = account.name
        holder.tvBalance.text =
                formatToMoneyString(Money(account.balance, getCurrencyByString(account.currency)))

        //Set colored credit card image view
        if (account.name != context.resources.getString(R.string.cash_account_name)) {
            holder.ivCard.setImageResource(R.drawable.ic_credit_card_primary_24dp)
            holder.ivCard.setColorFilter(account.color, PorterDuff.Mode.SRC_ATOP)
        }
        else {
            holder.ivCard.setImageResource(R.drawable.ic_account_balance_wallet_primary_24dp)
        }
    }

    override fun getItemCount() = accounts.size

    override fun getItemId(position: Int): Long {
        return accounts[position].hashCode().toLong()
    }

    fun replaceData(accounts: List<Account>) {
        this.accounts.clear()
        this.accounts.addAll(accounts)
        notifyDataSetChanged()
    }

    interface AccountsAdapterEventsListener {}
}