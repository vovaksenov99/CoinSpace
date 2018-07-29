package com.example.alexmelnikov.coinspace.ui.statistics

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo
import com.example.alexmelnikov.coinspace.R
import com.example.alexmelnikov.coinspace.di.component.DaggerFragmentComponent
import com.example.alexmelnikov.coinspace.ui.RevealCircleAnimatorHelper
import com.example.alexmelnikov.coinspace.ui.accounts.AccountsFragment
import com.example.alexmelnikov.coinspace.ui.add_new_account.AddAccountFragment
import com.example.alexmelnikov.coinspace.ui.main.MainActivity
import kotlinx.android.synthetic.main.fragment_accounts.*
import kotlinx.android.synthetic.main.fragment_add_account.*
import kotlinx.android.synthetic.main.fragment_statistics.*
import javax.inject.Inject

/**
 *  Created by Alexander Melnikov on 29.07.18.
 *  TODO: Edit class header comment
 */

class StatisticsFragment: Fragment(), StatisticsContract.View {

    @Inject
    override lateinit var presenter: StatisticsContract.Presenter

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        DaggerFragmentComponent.builder().build().inject(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        presenter.attach(this)
        val root = inflater.inflate(R.layout.fragment_statistics, container, false)
        RevealCircleAnimatorHelper
                .create(this, container)
                .start(root)
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //Setup toolbar
        statistics_toolbar.overflowIcon = (activity as MainActivity).getDrawable(R.drawable.ic_more_vert_white_24dp)
        (activity as MainActivity).setSupportActionBar(statistics_toolbar)
        setHasOptionsMenu(true)
        statistics_toolbar.title = resources.getString(R.string.accounts_fragment_title)
        statistics_toolbar.navigationIcon = resources.getDrawable(R.drawable.ic_arrow_back_white_24dp)
        statistics_toolbar.setNavigationOnClickListener { fragmentManager?.popBackStack() }

        view.postDelayed({
            chart_container.visibility = View.VISIBLE
            YoYo.with(Techniques.SlideInUp)
                    .duration(400)
                    .playOn(chart_container)
        }, 370)

        presenter.chartDataRequest()
    }


    override fun setupChartData() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    companion object {

        fun newInstance(sourceView: View? = null) = StatisticsFragment().apply {
            arguments = Bundle()
            RevealCircleAnimatorHelper.addBundleValues(arguments!!, sourceView)
        }
    }
}