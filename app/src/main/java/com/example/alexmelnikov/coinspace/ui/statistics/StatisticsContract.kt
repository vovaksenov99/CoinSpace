package com.example.alexmelnikov.coinspace.ui.statistics

import com.example.alexmelnikov.coinspace.ui.BaseContract
import com.github.mikephil.charting.data.PieDataSet
import java.util.*

class StatisticsContract {

    interface View : BaseContract.View {

        var presenter: StatisticsContract.Presenter

        fun setupChartData(dataSet: PieDataSet)

        fun getFromDate(): Calendar

        fun getToDate(): Calendar

    }

    interface Presenter : BaseContract.Presenter<StatisticsContract.View> {

        fun chartDataRequest()

    }
}