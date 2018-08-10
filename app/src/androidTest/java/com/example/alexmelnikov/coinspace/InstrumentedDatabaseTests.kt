package com.example.alexmelnikov.coinspace

import android.arch.persistence.room.Room
import android.support.test.InstrumentationRegistry
import android.support.test.runner.AndroidJUnit4
import android.util.Log
import com.example.alexmelnikov.coinspace.model.Category
import com.example.alexmelnikov.coinspace.model.entities.Account
import com.example.alexmelnikov.coinspace.model.entities.Operation
import com.example.alexmelnikov.coinspace.model.persistance.Database
import com.example.alexmelnikov.coinspace.model.repositories.DefaultAccountsRepository
import com.example.alexmelnikov.coinspace.model.repositories.IDeferOperationsRepositoryRepository
import io.reactivex.schedulers.Schedulers
import org.junit.After
import org.junit.Assert
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.util.*


/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class InstrumentedDatabaseTests {
    /**
     * Данные тесты не являются достаточным минимум покрытия кода. Я просто не успел добавить больше
     * TODO
     */
    private val DATABASE_NAME = "room.db"

    lateinit var db: Database

    lateinit var accountsRepository: DefaultAccountsRepository
    lateinit var deferOperationsRepository: IDeferOperationsRepositoryRepository

    @Before
    fun setup() {
        db = Room.databaseBuilder(InstrumentationRegistry.getTargetContext(),
            Database::class.java,
            DATABASE_NAME).build()

        accountsRepository = DefaultAccountsRepository(db.accountDao())
        deferOperationsRepository = IDeferOperationsRepositoryRepository(db.deferOperationsDao())

        val accounts = getStartAccounts()
        for (account in accounts)
            accountsRepository.database.insert(account)

    }

    @After
    fun clear() {
        accountsRepository.deleteAll()
        deferOperationsRepository.removeAllOperation()
    }

    private fun getStartAccounts(): List<Account> {

        val operations = listOf(Operation(OperationType.INCOME,
            12f,
            "USD",
            InstrumentationRegistry.getTargetContext().getString(
                Category.FOOD.getStringResource()),
            Date(1533475542852)))
        val account1 = Account(0, "Account1", "USD", 1000f, 10, operations)


        val operations2 = listOf(Operation(OperationType.INCOME,
            12f,
            "USD",
            InstrumentationRegistry.getTargetContext().getString(
                Category.FOOD.getStringResource()),
            Calendar.getInstance().time.time),
            Operation(OperationType.EXPENSE,
                123f,
                "EUR",
                InstrumentationRegistry.getTargetContext().getString(
                    Category.TRANSPORT.getStringResource()),
                Date(1533475542852)),
            Operation(OperationType.EXPENSE,
                200f,
                "RUB",
                InstrumentationRegistry.getTargetContext().getString(
                    Category.TRAVEL.getStringResource()),
                Date(1533475542852)))
        val account2 = Account(1, "Account2", "RUB", 200000f, 1011, operations2)

        return listOf(account1, account2)
    }


    @Test
    fun shouldReturnAccountById() {
        val repository = DefaultAccountsRepository(db)

        repository.findAccountById(0).subscribeOn(Schedulers.io())
            .subscribe({ account ->
                assertEquals(0L, account.id)
            }, {
                Log.e("shouldReturnAccountById", it.toString())
                //for fail test
            })
    }

    @Test
    fun shouldReturnNullByNoneId() {
        val repository = DefaultAccountsRepository(db)

        repository.findAccountById(-1).subscribeOn(Schedulers.io())
            .subscribe({ account ->
                Assert.fail()
            }, {})
    }

    @Test
    fun shouldFindAccountByName() {
        val repository = DefaultAccountsRepository(db)

        repository.findAccountByName("Account1").subscribeOn(Schedulers.io())
            .subscribe({ account ->
                assertEquals("Account1", account.name)
            }, {
                Assert.fail(it.toString())
            })
    }

    @Test
    fun shouldFindAccountByNoneName() {
        val repository = DefaultAccountsRepository(db)

        repository.findAccountByName(" ").subscribeOn(Schedulers.io())
            .subscribe({ account ->
                Assert.fail()
            }, {
            })
    }
}
