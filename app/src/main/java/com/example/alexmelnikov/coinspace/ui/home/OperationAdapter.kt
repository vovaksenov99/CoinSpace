package com.example.alexmelnikov.coinspace.ui.home

import android.content.DialogInterface
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.example.alexmelnikov.coinspace.R
import com.example.alexmelnikov.coinspace.model.entities.Operation
import com.example.alexmelnikov.coinspace.model.entities.OperationType
import com.example.alexmelnikov.coinspace.model.getCategoryByString
import com.example.alexmelnikov.coinspace.model.getCurrencyByString
import com.example.alexmelnikov.coinspace.model.interactors.Money
import kotlinx.android.synthetic.main.operation_rv_item.view.*

/**
 * Created by AksCorp on 03.04.2018.
 * akscorp2014@gmail.com
 * web site aksenov-vladimir.herokuapp.com
 */
class OperationAdapter(private var operations: MutableList<Operation>,
                       val presenter: HomeContract.Presenter) :
    RecyclerView.Adapter<OperationAdapter.OperationHolder>() {


    override fun getItemCount(): Int {
        return operations.size
    }

    fun updateData(operations: MutableList<Operation>)
    {
        this.operations = operations
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int): OperationAdapter.OperationHolder {
        val context = parent.context
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.operation_rv_item, parent, false)
        val holder = OperationHolder(view)
        view.setOnLongClickListener {
            if (holder.adapterPosition != RecyclerView.NO_POSITION) {
                holder.onLongClicked(holder.adapterPosition)
            }
            true
        }
        return holder
    }

    override fun onBindViewHolder(holder: OperationAdapter.OperationHolder, position: Int) {
        val operation = operations[position]
        holder.sum.text = getSign(operation) +
                Money(operation.sum, getCurrencyByString(operation.currency)).normalizeCountString()
        holder.currency.text = operation.currency
        holder.icon.setImageResource(getCategoryByString(operation.category).getIconResource())
        holder.date.text = operation.date
    }

    fun getSign(operation: Operation): String {
        if (operation.type == OperationType.EXPENSE.toString())
            return "-"
        return ""
    }

    inner class OperationHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val date: TextView = itemView.date as TextView
        val sum: TextView = itemView.sum as TextView
        val currency: TextView = itemView.currency as TextView
        val icon: ImageView = itemView.icon as ImageView

        fun onLongClicked(position: Int) {

            val builder = android.app.AlertDialog.Builder(itemView.context)
            builder.setMessage(itemView.context.getString(R.string.do_you_want_to_delete_pattern))
            builder.setPositiveButton(itemView.context.getString(R.string.yes)) { dialogInterface: DialogInterface, i: Int ->
                val id = operations[position].id!!
                val accountId = operations[position].accountId!!
                presenter.newRemoveOperationRequest(id, accountId)
                operations.removeAt(position)
                notifyItemRemoved(position)
                dialogInterface.cancel()
            }
            builder.setNegativeButton(itemView.context.getString(R.string.no)) { dialogInterface: DialogInterface, i: Int ->
                dialogInterface.cancel()
            }

            builder.show()

        }
    }

}