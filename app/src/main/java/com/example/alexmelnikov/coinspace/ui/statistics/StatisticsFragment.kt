package com.example.alexmelnikov.coinspace.ui.statistics

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo
import com.example.alexmelnikov.coinspace.BaseApp
import com.example.alexmelnikov.coinspace.R
import com.example.alexmelnikov.coinspace.di.component.DaggerFragmentComponent
import com.example.alexmelnikov.coinspace.di.module.FragmentModule
import com.example.alexmelnikov.coinspace.ui.RevealCircleAnimatorHelper
import com.example.alexmelnikov.coinspace.ui.main.MainActivity
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import kotlinx.android.synthetic.main.fragment_statistics.*
import javax.inject.Inject


class StatisticsFragment : Fragment(), StatisticsContract.View {

    @Inject
    override lateinit var presenter: StatisticsContract.Presenter

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        DaggerFragmentComponent.builder()
            .fragmentModule(FragmentModule(activity!!.applicationContext as BaseApp)).build()
            .inject(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
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
        statistics_toolbar.overflowIcon =
                (activity as MainActivity).getDrawable(R.drawable.ic_more_vert_white_24dp)
        (activity as MainActivity).setSupportActionBar(statistics_toolbar)
        setHasOptionsMenu(true)
        statistics_toolbar.title = resources.getString(R.string.statistics_fragment_title)
        statistics_toolbar.navigationIcon =
                ContextCompat.getDrawable(activity!!, R.drawable.ic_arrow_back_white_24dp)
        statistics_toolbar.setNavigationOnClickListener { fragmentManager?.popBackStack() }

        //Setup chart
        expenses_by_category_chart.centerText = resources.getString(R.string.expenses_pie_chart_lbl)
        expenses_by_category_chart.setCenterTextTypeface(Typeface
            .createFromAsset((activity as MainActivity).assets, "fonts/Roboto-Regular.ttf"))
        expenses_by_category_chart.setUsePercentValues(false)
        expenses_by_category_chart.description.isEnabled = false

        expenses_by_category_chart.setDrawEntryLabels(false)

        val l = expenses_by_category_chart.legend
        l.verticalAlignment = Legend.LegendVerticalAlignment.BOTTOM
        l.horizontalAlignment = Legend.LegendHorizontalAlignment.CENTER
        l.orientation = Legend.LegendOrientation.VERTICAL
        l.setDrawInside(false)
        l.yOffset = 30f

        view.postDelayed({
            try {
                chart_container.visibility = View.VISIBLE
                YoYo.with(Techniques.SlideInUp)
                    .duration(400)
                    .playOn(chart_container)
            } catch (e: Exception) {
                Log.e("StatisticsFragment", e.toString())
            }
        }, 370)

        presenter.chartDataRequest()
    }


    override fun setupChartData(dataSet: PieDataSet) {
        val colorsArray = resources.obtainTypedArray(R.array.custom_pie_chart_colors)
        val colors = ArrayList<Int>()
        for (i in 0 until colorsArray.length()) {
            colors.add(colorsArray.getColor(i, 0))
        }
        colorsArray.recycle()

        dataSet.colors = colors
        dataSet.sliceSpace = 3f
        dataSet.selectionShift = 5f

        val data = PieData(dataSet)
        data.setValueTextSize(11f)
        data.setValueTextColor(Color.WHITE)
        data.setValueTypeface(Typeface.createFromAsset((activity as MainActivity).assets,
            "fonts/Roboto-Regular.ttf"))
        expenses_by_category_chart.data = data
        expenses_by_category_chart.invalidate()
    }

    companion object {

        fun newInstance(sourceView: View? = null) = StatisticsFragment().apply {
            arguments = Bundle()
            RevealCircleAnimatorHelper.addBundleValues(arguments!!, sourceView)
        }
    }
}