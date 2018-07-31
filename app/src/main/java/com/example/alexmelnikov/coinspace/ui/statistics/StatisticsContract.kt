package com.example.alexmelnikov.coinspace.ui.statistics

import com.example.alexmelnikov.coinspace.ui.BaseContract
import com.github.mikephil.charting.data.PieDataSet

class StatisticsContract {

    interface View : BaseContract.View {

        var presenter: StatisticsContract.Presenter

        fun setupChartData(dataSet: PieDataSet)

    }

    interface Presenter : BaseContract.Presenter<StatisticsContract.View> {

        fun chartDataRequest()

    }
}