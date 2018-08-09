package com.example.alexmelnikov.coinspace.ui.home

import android.content.Context
import android.content.DialogInterface
import android.graphics.PorterDuff
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.example.alexmelnikov.coinspace.R
import com.example.alexmelnikov.coinspace.model.entities.Operation
import com.example.alexmelnikov.coinspace.model.entities.OperationType
import com.example.alexmelnikov.coinspace.model.entities.Pattern
import kotlinx.android.synthetic.main.pattern_operation_dialog.view.*
import kotlinx.android.synthetic.main.pattern_rv_element.view.*
import java.util.*


class PatternsAdapter(private val mContext: Context,
                      val presenter: HomeContract.Presenter,
                      val patterns: MutableList<Pattern>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    inner class PatternViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var currency: TextView = itemView.currency
        var type: TextView = itemView.type
        var icon: ImageView = itemView.categoty

        fun onItemClicked(position: Int) {
            showDialog(position)
        }

        fun showDialog(position: Int) {
            val builder = AlertDialog.Builder(itemView.context)
            val view = LayoutInflater.from(itemView.context)
                .inflate(R.layout.pattern_operation_dialog, null)
            builder.setView(view)
            builder.setPositiveButton(itemView.context.getString(R.string.done)) { dialogInterface: DialogInterface, i: Int ->
                val pattern = patterns[position]
                val type =
                    if (pattern.type == OperationType.INCOME.toString()) OperationType.INCOME
                    else
                        OperationType.EXPENSE

                val category = pattern.category.toString()
                val calendar = Calendar.getInstance()
                if (view.sum.text.toString().toFloatOrNull() == null) {
                    view.sum.error = itemView.context.getString(R.string.empty_sum_error)
                    return@setPositiveButton
                }
                val operation = Operation(type.toString(),
                    view.sum.text.toString().toFloat(),
                    pattern.currency.toString(),
                    pattern.description,
                    category, pattern.bill.toLong(), null, Calendar.getInstance().time.time)

                presenter.newOperationRequest(operation, pattern.bill)
                dialogInterface.cancel()
            }
            builder.setNegativeButton(itemView.context.getString(R.string.no)) { dialogInterface: DialogInterface, i: Int ->
                dialogInterface.cancel()
            }

            builder.show()
        }

        fun onItemLongClicked(position: Int) {
            val builder = android.app.AlertDialog.Builder(itemView.context)
            builder.setMessage(itemView.context.getString(R.string.do_you_want_to_delete_pattern))
            builder.setPositiveButton(itemView.context.getString(R.string.yes)) { dialogInterface: DialogInterface, i: Int ->
                (presenter as HomePresenter).patternsRepository.removePattern(patterns[position].id!!)
                patterns.removeAt(position)
                notifyItemRemoved(position)
                dialogInterface.cancel()
            }
            builder.setNegativeButton(itemView.context.getString(R.string.no)) { dialogInterface: DialogInterface, i: Int ->
                dialogInterface.cancel()
            }

            builder.show()
        }

    }

    inner class PatternViewHolderAdd(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun onItemClicked(position: Int) {
            (presenter as HomePresenter).newOperationPatternButtonClick()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == 1) {
            val view =
                LayoutInflater.from(mContext).inflate(R.layout.pattern_rv_element, parent, false)
            val holder = PatternViewHolder(view)
            holder.itemView.setOnClickListener {
                if (holder.adapterPosition != RecyclerView.NO_POSITION) {
                    holder.onItemClicked(holder.adapterPosition)
                }
            }
            holder.itemView.setOnLongClickListener {
                if (holder.adapterPosition != RecyclerView.NO_POSITION) {
                    holder.onItemLongClicked(holder.adapterPosition)
                }
                false
            }
            return holder
        }
        else {
            val view =
                LayoutInflater.from(mContext)
                    .inflate(R.layout.pattern_rv_element_add, parent, false)
            val holder = PatternViewHolderAdd(view)
            holder.itemView.setOnClickListener {
                if (holder.adapterPosition != RecyclerView.NO_POSITION) {
                    holder.onItemClicked(holder.adapterPosition)
                }
            }
            return holder
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        if (holder.itemViewType == 1) {
            val holder = holder as PatternViewHolder
            val pattern = patterns[position]

            holder.type.text =
                    if (pattern.type == OperationType.INCOME.toString()) "+" else "-"
            holder.currency.text = pattern.currency.currencySymbol
            holder.icon.setImageResource(pattern.category.getIconResource())
            holder.icon.setColorFilter(ContextCompat.getColor(holder.icon.context,
                android.R.color.white),
                PorterDuff.Mode.SRC_IN)
        }
    }

    override fun getItemCount() = patterns.size + 1

    override fun getItemViewType(position: Int): Int {
        if (position == patterns.size)
            return 2
        return 1
    }

}