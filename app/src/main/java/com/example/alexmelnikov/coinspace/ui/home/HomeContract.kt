package com.example.alexmelnikov.coinspace.ui.home

import android.view.View
import com.example.alexmelnikov.coinspace.model.entities.Account
import com.example.alexmelnikov.coinspace.model.entities.Operation
import com.example.alexmelnikov.coinspace.model.interactors.Currency
import com.example.alexmelnikov.coinspace.model.interactors.Money
import com.example.alexmelnikov.coinspace.ui.BaseContract

class HomeContract {

    interface HomeView : BaseContract.View {

        var presenter: HomeContract.Presenter

        fun openOperationFragment()

        fun closeOperationFragment()

        fun updateUserBalanceItemPagerView(mainBalance: String, additionalBalance: String)

        fun updateAccountItemPagerView(account: Account)

        fun setupViewPager(balance: Money, accounts: List<Account>)

        fun animateNewOperationButtonToCheck()

        fun animateNewOperationButtonToAdd()

        fun openSettingsActivity()

        fun openAccountsFragmentRequest()

        fun openStatisticsFragmentRequest(animationCenter: View)

        fun showAboutDialog()

        fun setupOperationsAdapter(operations: List<Operation>)

    }

    interface OperationView : BaseContract.View {

        var presenter: HomeContract.Presenter

        fun confirmOperationAndCloseSelf()

        fun animateCloseButtonCloseToBack()

        fun animateCloseButtonBackToClose()

        fun resetLayout()

        fun setupNewOperationLayout(type: Operation.OperationType, accounts: List<Account>)
    }

    interface Presenter : BaseContract.Presenter<HomeView> {

        fun attachOperationView(view: OperationView)

        fun detachOperationView()

        fun viewPagerSetupRequest()

        fun newOperationButtonClick()

        fun newExpenseButtonClick()

        fun newIncomeButtonClick()

        fun clearButtonClick()

        fun newOperationRequest(sum: Float, account: Account, category: String, currency: String)

        fun animateOperationAddButtonRequest()

        fun openSettingsActivityRequest()

        fun showAboutDialogRequest()

        fun accountsButtonClick()

        fun statisticsButtonClick(animationCenter: View)

        fun getMainCurrency(): Currency

    }
}
