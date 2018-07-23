package com.example.alexmelnikov.coinspace.ui.home

import com.example.alexmelnikov.coinspace.model.Operation
import com.example.alexmelnikov.coinspace.ui.BaseContract

class HomeContract {

    interface HomeView : BaseContract.View {

        var presenter: HomeContract.Presenter

        fun openOperationFragmentRequest()

        fun setupTextViews(mainBalance: String, additionalBalance: String)

        fun animateNewOperationButtonToCheck()

        fun animateNewOperationButtonToAdd()

        fun saveNewBalance(sum: Float)
    }

    interface OperationView : BaseContract.View {

        var presenter: HomeContract.Presenter

        fun confirmOperationAndCloseSelf(): Boolean

        fun setupNewOperationLayout(type: Operation.OperationType)
    }

    interface Presenter : BaseContract.Presenter<HomeContract.HomeView> {

        var mainCurrency: String
        var balanceUsd: Float

        fun attachOperationView(view: HomeContract.OperationView)

        fun detachOperationView()

        fun textViewsSetupRequest(mainCurrency: String, balanceUsd: Float)

        fun newOperationButtonClick()

        fun newExpenseButtonClick()

        fun newIncomeButtonClick()

        fun newOperationRequest(sum: Float, currency: String)
    }
}
