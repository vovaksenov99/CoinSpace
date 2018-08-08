package com.example.alexmelnikov.coinspace.di.module

import android.app.Application
import android.arch.persistence.room.Room
import android.content.Context
import com.example.alexmelnikov.coinspace.BaseApp
import com.example.alexmelnikov.coinspace.model.interactors.IUserBalanceInteractor
import com.example.alexmelnikov.coinspace.model.interactors.UserBalanceInteractor
import com.example.alexmelnikov.coinspace.model.persistance.Database
import com.example.alexmelnikov.coinspace.model.repositories.*
import com.example.alexmelnikov.coinspace.ui.accounts.AccountsContract
import com.example.alexmelnikov.coinspace.ui.accounts.AccountsPresenter
import com.example.alexmelnikov.coinspace.ui.add_new_account.AddAccountContract
import com.example.alexmelnikov.coinspace.ui.add_new_account.AddAccountPresenter
import com.example.alexmelnikov.coinspace.ui.home.HomeContract
import com.example.alexmelnikov.coinspace.ui.home.HomePresenter
import com.example.alexmelnikov.coinspace.ui.statistics.StatisticsContract
import com.example.alexmelnikov.coinspace.ui.statistics.StatisticsPresenter
import com.example.alexmelnikov.coinspace.util.PreferencesHelper
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class FragmentModule(private val baseApp: BaseApp) {

    private val DATABASE_NAME = "room.db"


    @Provides
    fun provideHomePresenter(): HomeContract.Presenter = HomePresenter()


    @Provides
    fun provideAccountsPresenter(): AccountsContract.Presenter = AccountsPresenter()


    @Provides
    fun provideAddAccountPresenter(): AddAccountContract.Presenter = AddAccountPresenter()


    @Provides
    fun provadeStatisticsPresenter(): StatisticsContract.Presenter = StatisticsPresenter()

    @Provides
    @Singleton
    fun provideApplication(): Application {
        return baseApp
    }

    @Provides
    @Singleton
    fun provideContext(application: Application): Context = application.applicationContext

    @Provides
    @Singleton
    fun provideDatabase(context: Context): Database =
        Room.databaseBuilder(context, Database::class.java, DATABASE_NAME).build()

    @Provides
    @Singleton
    fun provideDeferRepository(database: Database): IDeferOperationsRepository =
        IDeferOperationsRepositoryRepository(database.deferOperationsDao())

    @Provides
    @Singleton
    fun provideAccountRepository(database: Database): AccountsRepository =
        DefaultAccountsRepository(database)

    @Provides
    @Singleton
    fun providePatternRepository(database: Database): IPatternsRepository =
        PatternsRepository(database.patternDao())

    @Provides
    @Singleton
    fun provideOperationsRepository(database: Database): IOperationsRepository =
        OperationsRepository(database.operationsDao())

    @Provides
    @Singleton
    fun providePreferencesHelper(context: Context): PreferencesHelper =
        PreferencesHelper(context)

    @Provides
    @Singleton
    fun provideUserBalanceInteractor(preferencesHelper: PreferencesHelper): IUserBalanceInteractor =
        UserBalanceInteractor(preferencesHelper)
}