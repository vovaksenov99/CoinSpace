package com.example.alexmelnikov.coinspace.ui.home

import android.view.View
import com.example.alexmelnikov.coinspace.model.Currency
import com.example.alexmelnikov.coinspace.model.entities.Account
import com.example.alexmelnikov.coinspace.model.entities.Operation
import com.example.alexmelnikov.coinspace.model.entities.OperationType
import com.example.alexmelnikov.coinspace.model.entities.Pattern
import com.example.alexmelnikov.coinspace.model.interactors.Money
import com.example.alexmelnikov.coinspace.ui.BaseContract

class HomeContract {

    interface HomeView : BaseContract.View {

        var presenter: HomeContract.Presenter

        fun openOperationFragment()

        fun openOperationPatternFragment()

        fun closeOperationFragment()

        fun updateUserBalanceItemPagerView(mainBalance: String, additionalBalance: String)

        fun updateAccountItemPagerView(account: Account)

        fun setupViewPager(balance: Money, accounts: List<Account>, startPage:Int = 0)

        fun animateNewOperationButtonToCheck()

        fun animateNewOperationButtonToAdd()

        fun openSettingsActivity()

        fun getViewPagerPosition(): Int

        fun openAccountsFragmentRequest()

        fun openStatisticsFragmentRequest(animationCenter: View)

        fun showAboutDialog()

        fun initPatternsRv(patterns: List<Pattern>)

        fun setupOperationsAdapter(operations: List<Operation>)

    }

    interface OperationView : BaseContract.View {

        var presenter: HomeContract.Presenter

        fun confirmOperationAndCloseSelf()

        fun animateCloseButtonCloseToBack()

        fun animateCloseButtonBackToClose()

        fun resetLayout()

        fun setupNewOperationLayout(type: OperationType, accounts: MutableMap<Long, Account>)

        fun showPeriodicDialog()
    }

    interface Presenter : BaseContract.Presenter<HomeView> {

        fun attachOperationView(view: OperationView)

        fun detachOperationView()

        fun viewPagerSetupRequest()

        fun newOperationButtonClick()

        fun newOperationPatternButtonClick()

        fun newExpenseButtonClick()

        fun addNewPattern(pattern: Pattern, account: Account)

        fun newIncomeButtonClick()

        fun clearButtonClick()

        fun newOperationRequest(sum: Float, account: Account, category: String, description: String,
                                currency: String,
                                repeat: Int)

        fun newOperationRequest(operation: Operation, accountId: Int)

        fun newRemoveOperationRequest(operationId: Long, accountId: Long)

        fun animateOperationAddButtonRequest()

        fun openSettingsActivityRequest()

        fun showAboutDialogRequest()

        fun accountsButtonClick()

        fun initPatternsRV()

        fun statisticsButtonClick(animationCenter: View)

        fun getMainCurrency(): Currency

    }
}
