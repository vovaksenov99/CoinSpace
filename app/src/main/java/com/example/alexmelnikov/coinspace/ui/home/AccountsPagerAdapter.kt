package com.example.alexmelnikov.coinspace.ui.home

import android.content.Context
import android.graphics.PorterDuff
import android.support.v4.app.FragmentActivity
import android.support.v4.view.PagerAdapter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.alexmelnikov.coinspace.R
import com.example.alexmelnikov.coinspace.model.Currency
import com.example.alexmelnikov.coinspace.model.entities.Account
import com.example.alexmelnikov.coinspace.model.getCurrencyByString
import com.example.alexmelnikov.coinspace.model.interactors.CurrencyConverter
import com.example.alexmelnikov.coinspace.model.interactors.Money
import com.example.alexmelnikov.coinspace.ui.OPERATION_SEARCH_DIALOG
import com.example.alexmelnikov.coinspace.ui.OperationSearchDialog
import com.example.alexmelnikov.coinspace.ui.PERIODIC_DIALOG_TAG
import com.example.alexmelnikov.coinspace.ui.PeriodicDialog
import com.example.alexmelnikov.coinspace.util.formatToMoneyString
import kotlinx.android.synthetic.main.card_account_balance.view.*
import kotlinx.android.synthetic.main.card_current_budget.view.*

/**
 *  Created by Alexander Melnikov on 28.07.18.
 */

class AccountsPagerAdapter(private val mContext: Context,
                           private val mUserBalance: Money,
                           private val mAccounts: ArrayList<Account>,
                            val presenter: HomeContract.Presenter) : PagerAdapter() {

    var currencyConverter = CurrencyConverter()

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        if (position == 0) {
            val layout = LayoutInflater.from(mContext).inflate(
                mContext.resources.getLayout(R.layout.card_current_budget), container, false)

            layout.tv_balance_lbl.text = mContext.getText(R.string.tv_balance_lbl)
            layout.tv_main_cur_balance.text = formatToMoneyString(mUserBalance)

            layout.tv_additional_cur_balance.text =
                    formatToMoneyString(currencyConverter.convertCurrency(mUserBalance,
                        Currency.RUR))

            layout.tag = BALANCE_VIEW_TAG
            container.addView(layout)
            layout.show_periodic_transaction.setOnClickListener {
                val dialog = PeriodicDialog()
                dialog.showNow((mContext as FragmentActivity).supportFragmentManager, PERIODIC_DIALOG_TAG)

            }

            layout.search.setOnClickListener {
                val dialog = OperationSearchDialog()
                OperationSearchDialog.presenter = presenter
                dialog.showNow((mContext as FragmentActivity).supportFragmentManager, OPERATION_SEARCH_DIALOG)

            }

            return layout
        }
        else {
            val account = mAccounts[position - 1]
            val layout = LayoutInflater.from(mContext).inflate(
                mContext.resources.getLayout(R.layout.card_account_balance), container, false)
            layout.tv_account_name.text = account.name
            var money = Money(account.balance, getCurrencyByString(account.currency))

            money = currencyConverter.convertCurrency(money,
                getCurrencyByString(account.currency))
            layout.tv_account_balance.text = formatToMoneyString(money)



            if (account.name != mContext.resources.getString(R.string.cash_account_name)) {
                layout.iv_account_icon.setImageResource(R.drawable.ic_credit_card_primary_24dp)
                layout.iv_account_icon.setColorFilter(account.color, PorterDuff.Mode.SRC_ATOP)
            }
            else {
                layout.iv_account_icon.setImageResource(R.drawable.ic_account_balance_wallet_primary_24dp)
            }

            layout.tag = account.id
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

    companion object {
        val BALANCE_VIEW_TAG = "BALANCE_VIEW_TAG"
    }
}