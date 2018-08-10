package com.example.alexmelnikov.coinspace.ui

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.example.alexmelnikov.coinspace.BaseApp
import com.example.alexmelnikov.coinspace.R
import com.example.alexmelnikov.coinspace.di.component.DaggerFragmentComponent
import com.example.alexmelnikov.coinspace.di.module.FragmentModule
import com.example.alexmelnikov.coinspace.model.entities.DeferOperation
import com.example.alexmelnikov.coinspace.model.getCategoryByString
import com.example.alexmelnikov.coinspace.model.repositories.IDeferOperationsRepository
import com.example.alexmelnikov.coinspace.ui.home.RepeatedPeriod.DAY
import com.example.alexmelnikov.coinspace.ui.home.RepeatedPeriod.MONTH
import com.example.alexmelnikov.coinspace.ui.home.RepeatedPeriod.WEEK
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.periodic_transaction_dialog.view.*
import javax.inject.Inject

val PERIODIC_DIALOG_TAG = "PAYMENT_DIALOG_TAG"

class PeriodicDialog : DialogFragment() {

    @Inject
    lateinit var database: IDeferOperationsRepository

    lateinit var fragmentView: View

    override fun onAttach(context: Context?) {
        super.onAttach(context)

        DaggerFragmentComponent.builder()
            .fragmentModule(FragmentModule(activity!!.applicationContext as BaseApp)).build()
            .inject(this)

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setStyle(DialogFragment.STYLE_NORMAL, R.style.FullscreenDialog)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        fragmentView = inflater.inflate(R.layout.periodic_transaction_dialog, null)

        initUI()

        return fragmentView
    }

    private fun initUI() {
        database.getAllOperations()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ operationsList ->
                initOperationsRV(operationsList)
                if (operationsList.isNotEmpty()) {
                    fragmentView.empty_operation_history.visibility = View.INVISIBLE
                }
            },
                {
                })

        fragmentView.toolbar.title = getString(R.string.periodic_operations)
        fragmentView.toolbar.setTitleTextColor(ContextCompat.getColor(context!!, R.color.white))
    }


    fun initOperationsRV(operations: List<DeferOperation>) {
        val layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        fragmentView.periodic_operations_rv.setHasFixedSize(true)
        fragmentView.periodic_operations_rv.layoutManager = layoutManager

        fragmentView.periodic_operations_rv.adapter = PeriodicOperations(operations.toMutableList())
    }

    inner class PeriodicOperations(val operations: MutableList<DeferOperation>) :
        RecyclerView.Adapter<PeriodicOperations.CurrencyHolder>() {

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getItemCount(): Int {
            return operations.size
        }

        override fun onCreateViewHolder(
            parent: ViewGroup, viewType: Int): CurrencyHolder {
            val context = parent.context
            val inflater = LayoutInflater.from(context)
            val view = inflater.inflate(R.layout.operation_rv_item, parent, false)
            val holder = CurrencyHolder(view)
            holder.itemView.setOnLongClickListener {
                if (holder.adapterPosition != RecyclerView.NO_POSITION) {
                    holder.onItemClick(holder.adapterPosition)
                }
                false
            }
            return holder
        }

        override fun onBindViewHolder(holder: CurrencyHolder, position: Int) {
            val operation = operations[position]
            holder.currency.text = operation.currency
            holder.icon.setImageResource(getCategoryByString(operation.category).getIconResource())

            holder.description.text = "${getString(R.string.description)}: ${operation.description}\n${getString(R.string.next_repeat)} ${getStringByRepeat(
                        operation.repeatDays)}"

            holder.sum.text = operation.moneyCount.toString()
            holder.date.text =
                    "${operation.nextRepeatDay}.${operation.nextRepeatMonth + 1}.${operation.nextRepeatYear}"

        }

        private fun getStringByRepeat(repeat: Int): String {
            return when (repeat) {
                DAY -> getString(R.string.every_day)
                WEEK -> getString(R.string.every_week)
                MONTH -> getString(R.string.every_month)
                else -> {
                    ""
                }
            }
        }

        inner class CurrencyHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val icon: ImageView = itemView.findViewById(R.id.icon)
            val description: TextView = itemView.findViewById(R.id.description)
            val date: TextView = itemView.findViewById(R.id.date)
            val sum: TextView = itemView.findViewById(R.id.sum)
            val currency: TextView = itemView.findViewById(R.id.currency)

            fun onItemClick(position: Int) {
                val builder = AlertDialog.Builder(context)
                builder.setMessage(getString(R.string.do_you_want_to_delete_periodic_payment))
                builder.setPositiveButton(getString(R.string.yes)) { dialogInterface: DialogInterface, i: Int ->
                    database.removeOperation(operations[position])
                    operations.removeAt(position)
                    notifyItemRemoved(position)
                    dialogInterface.cancel()
                }
                builder.setNegativeButton(getString(R.string.no)) { dialogInterface: DialogInterface, i: Int ->
                    dialogInterface.cancel()
                }

                builder.show()
            }
        }
    }
}