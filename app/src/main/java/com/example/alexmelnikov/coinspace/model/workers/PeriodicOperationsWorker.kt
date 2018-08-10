package com.example.alexmelnikov.coinspace.model.workers

import android.util.Log
import androidx.work.Worker
import com.example.alexmelnikov.coinspace.BaseApp
import com.example.alexmelnikov.coinspace.di.component.DaggerApplicationComponent
import com.example.alexmelnikov.coinspace.di.module.ApplicationModule
import com.example.alexmelnikov.coinspace.model.entities.Account
import com.example.alexmelnikov.coinspace.model.entities.DeferOperation
import com.example.alexmelnikov.coinspace.model.entities.Operation
import com.example.alexmelnikov.coinspace.model.entities.OperationType
import com.example.alexmelnikov.coinspace.model.getCurrencyByString
import com.example.alexmelnikov.coinspace.model.interactors.*
import com.example.alexmelnikov.coinspace.model.repositories.AccountsRepository
import com.example.alexmelnikov.coinspace.model.repositories.IDeferOperationsRepository
import com.example.alexmelnikov.coinspace.model.repositories.IOperationsRepository
import com.example.alexmelnikov.coinspace.ui.home.RepeatedPeriod
import io.reactivex.schedulers.Schedulers
import java.util.*
import javax.inject.Inject


class PeriodicOperationsWorker : Worker() {

    @Inject
    lateinit var userBalanceInteractor: IUserBalanceInteractor

    @Inject
    lateinit var IDeferDatabaseRepository: IDeferOperationsRepository

    @Inject
    lateinit var operationsDatabase: IOperationsRepository

    @Inject
    lateinit var accountDatabase: AccountsRepository

    @Inject
    lateinit var currencyConverter: CurrencyConverter

    val day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
    val month = Calendar.getInstance().get(Calendar.MONTH)
    val year = Calendar.getInstance().get(Calendar.YEAR)

    override fun doWork(): Result {
        DaggerApplicationComponent.builder()
            .applicationModule(ApplicationModule(applicationContext as BaseApp)).build()
            .inject(this)

        IDeferDatabaseRepository.getOperationsByDay(day, month, year)
            .subscribeOn(Schedulers.io())
            .subscribe({ operationsList -> executeOperations(operationsList) },
                { Log.w("PeriodicOperations", it.toString()) })

        return Result.SUCCESS
    }

    private fun executeOperations(operations: List<DeferOperation>) {
        for (operation in operations) {

            //calculate next execute time
            val calendar = Calendar.getInstance()
            calendar.set(year, month, day)

            when(operation.repeatDays)
            {
                RepeatedPeriod.DAY ->calendar.add(Calendar.DAY_OF_MONTH, 1)
                RepeatedPeriod.WEEK ->calendar.add(Calendar.DAY_OF_MONTH, 7)
                RepeatedPeriod.MONTH ->calendar.add(Calendar.MONTH, 1)
            }

            operation.nextRepeatDay = calendar.get(Calendar.DAY_OF_MONTH)
            operation.nextRepeatMonth = calendar.get(Calendar.MONTH)
            operation.nextRepeatYear = calendar.get(Calendar.YEAR)

            IDeferDatabaseRepository.addNewOperation(operation)

            accountDatabase.findAccountById(operation.accountId)
                .subscribeOn(Schedulers.io())
                .subscribe({ account ->
                    newOperationRequest(operation.moneyCount,
                        account,
                        operation.category,
                        operation.description,
                        operation.currency)
                }, { Log.w("Test", it) })
        }
    }


    private fun newOperationRequest(sum: Float, account: Account, category: String,description:String,
                                    currency: String) {
        var type = OperationType.INCOME
        if (sum < 0) {
            type = OperationType.EXPENSE
        }

        //Create operation and add it to accountOperationsList
        val operation = Operation(type.toString(), sum, currency,description, category, account.id, null,Date().time)
        val updatedAccountOperations: ArrayList<Operation> = ArrayList(account.operations)
        updatedAccountOperations.add(operation)
        account.operations = updatedAccountOperations

        val money = currencyConverter.convertCurrency(Money(sum, getCurrencyByString(currency)),
            defaultCurrency)
        val accountMoney = currencyConverter.convertCurrency(Money(account.balance,
            getCurrencyByString(account.currency)),
            defaultCurrency)

        if (type == OperationType.INCOME)
            accountMoney.count += money.count
        else
            accountMoney.count -= money.count

        account.balance = currencyConverter.convertCurrency(accountMoney,
            getCurrencyByString(account.currency)).count
        accountDatabase.updateAccountOfflineAsync(account)

        operationsDatabase.insertOperation(operation)

        userBalanceInteractor.executeNewOperation(type, money)
    }

    companion object {
        internal val TAG = "PeriodicOperationsWorker"
    }
}