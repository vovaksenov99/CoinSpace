package com.example.alexmelnikov.coinspace.ui

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.alexmelnikov.coinspace.BaseApp
import com.example.alexmelnikov.coinspace.R
import com.example.alexmelnikov.coinspace.di.component.DaggerFragmentComponent
import com.example.alexmelnikov.coinspace.di.module.FragmentModule
import com.example.alexmelnikov.coinspace.model.entities.Operation
import com.example.alexmelnikov.coinspace.model.repositories.IOperationsRepository
import com.example.alexmelnikov.coinspace.ui.home.HomeContract
import com.example.alexmelnikov.coinspace.ui.home.HomePresenter
import com.example.alexmelnikov.coinspace.ui.home.OperationAdapter
import com.example.alexmelnikov.coinspace.util.find
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.search_dialog.view.*
import javax.inject.Inject

val OPERATION_SEARCH_DIALOG = "OPERATION_SEARCH_DIALOG"

class OperationSearchDialog : DialogFragment() {

    @Inject
    lateinit var database: IOperationsRepository

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
        fragmentView = inflater.inflate(R.layout.search_dialog, null)

        initUI()

        return fragmentView
    }

    var operations = mutableListOf<Operation>()

    private fun initUI() {
        database.getAllOperations()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ operationsList ->
                operations = operationsList.toMutableList()
                initOperationsRV(operationsList)

            },
                {
                })
        fragmentView.search.setOnQueryTextListener(object :
            android.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                initOperationsRV(operations.filter { find(query, it.description) })
                return true
            }

            override fun onQueryTextChange(newText: String): Boolean {
                return true
            }
        })

        fragmentView.toolbar.title = getString(R.string.search_by_description)
        fragmentView.toolbar.setTitleTextColor(Color.WHITE)
    }


    fun initOperationsRV(operations: List<Operation>) {
        val layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        fragmentView.operations.layoutManager = layoutManager
        fragmentView.operations.setHasFixedSize(true)

        fragmentView.operations.adapter =
                OperationAdapter(operations.toMutableList(), presenter)
    }

    companion object {
        lateinit var presenter: HomeContract.Presenter
    }
}