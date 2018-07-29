package com.example.alexmelnikov.coinspace.ui.statistics

import com.example.alexmelnikov.coinspace.ui.BaseContract

class StatisticsContract {

    interface View : BaseContract.View {

        var presenter: StatisticsContract.Presenter

        fun setupChartData()

    }

    interface Presenter : BaseContract.Presenter<StatisticsContract.View> {

        fun chartDataRequest()

    }
}