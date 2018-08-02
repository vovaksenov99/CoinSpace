package com.example.alexmelnikov.coinspace.ui.home

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.example.alexmelnikov.coinspace.R
import com.example.alexmelnikov.coinspace.model.entities.Operation
import com.example.alexmelnikov.coinspace.model.interactors.Money
import com.example.alexmelnikov.coinspace.model.interactors.getCategoryByString
import com.example.alexmelnikov.coinspace.model.interactors.getCurrencyByString
import kotlinx.android.synthetic.main.operation_rv_item.view.*

/**
 * Created by AksCorp on 03.04.2018.
 * akscorp2014@gmail.com
 * web site aksenov-vladimir.herokuapp.com
 */
class OperationAdapter(private val operations: List<Operation>) :
    RecyclerView.Adapter<OperationAdapter.OperationHolder>() {


    override fun getItemCount(): Int {
        return operations.size
    }

    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int): OperationAdapter.OperationHolder {
        val context = parent.context
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.operation_rv_item, parent, false)
        return OperationHolder(view)
    }

    override fun onBindViewHolder(holder: OperationAdapter.OperationHolder, position: Int) {
        val operation = operations[position]

        holder.sum.text = getSign(operation)+
                Money(operation.sum, getCurrencyByString(operation.currency)).normalizeCountString()
        holder.currency.text = operation.currency
        holder.icon.setImageResource(getCategoryByString(holder.itemView.context,
            operation.category).getIconResource())
        holder.date.text = operation.date.toString()
    }

    fun getSign(operation: Operation): String {
        return if (operation.type == Operation.OperationType.INCOME)
            ""
        else
            "-"
    }

    inner class OperationHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val date: TextView = itemView.date as TextView
        val sum: TextView = itemView.sum as TextView
        val currency: TextView = itemView.currency as TextView
        val icon: ImageView = itemView.icon as ImageView
    }

}