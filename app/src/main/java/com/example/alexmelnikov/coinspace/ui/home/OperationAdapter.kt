package com.example.alexmelnikov.coinspace.ui.home

import android.support.v7.widget.RecyclerView
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.example.alexmelnikov.coinspace.R
import com.example.alexmelnikov.coinspace.model.entities.Operation
import com.example.alexmelnikov.coinspace.model.getCategoryByString
import com.example.alexmelnikov.coinspace.model.getCurrencyByString
import com.example.alexmelnikov.coinspace.model.interactors.Money
import kotlinx.android.synthetic.main.operation_rv_item.view.*

/**
 * Created by AksCorp on 03.04.2018.
 * akscorp2014@gmail.com
 * web site aksenov-vladimir.herokuapp.com
 */
class OperationAdapter(private val operations: MutableList<Operation>,val presenter: HomeContract.Presenter) :
    RecyclerView.Adapter<OperationAdapter.OperationHolder>() {


    override fun getItemCount(): Int {
        return operations.size
    }

    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int): OperationAdapter.OperationHolder {
        val context = parent.context
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.operation_rv_item, parent, false)
        val holder = OperationHolder(view)
        view.setOnLongClickListener {
            if(holder.adapterPosition !=RecyclerView.NO_POSITION)
            {
                holder.onLongClicked(holder.adapterPosition)
            }
            true
        }
        return holder
    }

    override fun onBindViewHolder(holder: OperationAdapter.OperationHolder, position: Int) {
        val operation = operations[position]
        normalize(operation)
        holder.sum.text = Money(operation.sum, getCurrencyByString(operation.currency)).normalizeCountString()
        holder.currency.text = operation.currency
        holder.icon.setImageResource(getCategoryByString(operation.category).getIconResource())
        holder.date.text = DateFormat.format("dd-MM-yyyy hh:mm", operation.date)
    }

    fun normalize(operation: Operation) {
        if (operation.type == Operation.OperationType.EXPENSE)
            operation.sum *= -1


    }

    inner class OperationHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val date: TextView = itemView.date as TextView
        val sum: TextView = itemView.sum as TextView
        val currency: TextView = itemView.currency as TextView
        val icon: ImageView = itemView.icon as ImageView

        fun onLongClicked(position: Int)
        {
            operations.removeAt(position)
            notifyItemRemoved(position)
            presenter.newRemoveOperationRequest(operations[position])
        }
    }

}