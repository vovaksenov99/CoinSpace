package com.example.alexmelnikov.coinspace.ui.home

import com.example.alexmelnikov.coinspace.model.entities.Account
import com.example.alexmelnikov.coinspace.model.entities.Operation
import com.example.alexmelnikov.coinspace.model.entities.UserBalance
import com.example.alexmelnikov.coinspace.ui.BaseContract

class HomeContract {

    interface HomeView : BaseContract.View {

        var presenter: HomeContract.Presenter

        fun openOperationFragment()

        fun closeOperationFragment()

        fun updateUserBalancePagerView(mainBalance: String, additionalBalance: String)

        fun setupViewPager(balance: UserBalance, accounts: List<Account>)

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

        fun viewPagerSetupRequest()

        fun newOperationButtonClick()

        fun newExpenseButtonClick()

        fun newIncomeButtonClick()

        fun clearButtonClick()

        fun newOperationRequest(sum: Float, currency: String)

        fun animateOperationAddButtonRequest()

        fun openSettingsActivityRequest()

        fun showAboutDialogRequest()

        fun accountsButtonClick()

        fun getMainCurrency(): String

    }
}
