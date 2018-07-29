package com.example.alexmelnikov.coinspace.ui.home

import com.example.alexmelnikov.coinspace.model.entities.Operation
import com.example.alexmelnikov.coinspace.ui.BaseContract

class HomeContract {

    interface HomeView : BaseContract.View {

        var presenter: HomeContract.Presenter

        fun openOperationFragment()

        fun closeOperationFragment()

        fun setupTextViews(mainBalance: String, additionalBalance: String)

        fun animateNewOperationButtonToCheck()

        fun animateNewOperationButtonToAdd()

        fun openSettingsActivity()

        fun openAccountsFragmentRequest()

        fun showAboutDialog()

    }

    interface OperationView : BaseContract.View {

        var presenter: HomeContract.Presenter

        fun confirmOperationAndCloseSelf()

        fun animateCloseButtonCloseToBack()

        fun animateCloseButtonBackToClose()

        fun resetLayout()

        fun setupNewOperationLayout(type: Operation.OperationType)
    }

    interface Presenter : BaseContract.Presenter<HomeView> {

        fun attachOperationView(view: OperationView)

        fun detachOperationView()

        fun textViewsSetupRequest()

        fun newOperationButtonClick()

        fun newExpenseButtonClick()

        fun newIncomeButtonClick()

        fun clearButtonClick()

        fun newOperationRequest(sum: Float, currency: String)

        fun animateOperationAddButtonRequest()

        fun openSettingsActivityRequest()

        fun showAboutDialogRequest()

        fun accountsButtonClick()

        fun accountsDataRequest()

        fun getMainCurrency(): String

    }
}
