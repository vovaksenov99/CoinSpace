package com.example.alexmelnikov.coinspace.di.module

import android.app.Application
import android.arch.persistence.room.Room
import android.content.Context
import com.example.alexmelnikov.coinspace.BaseApp
import com.example.alexmelnikov.coinspace.BuildConfig
import com.example.alexmelnikov.coinspace.model.api.ApiService
import com.example.alexmelnikov.coinspace.model.interactors.CurrencyConverter
import com.example.alexmelnikov.coinspace.model.interactors.IUserBalanceInteractor
import com.example.alexmelnikov.coinspace.model.interactors.UserBalanceInteractor
import com.example.alexmelnikov.coinspace.model.persistance.AccountsDatabase
import com.example.alexmelnikov.coinspace.model.repositories.AccountsRepository
import com.example.alexmelnikov.coinspace.model.repositories.DefaultAccountsRepository
import com.example.alexmelnikov.coinspace.util.ConnectionHelper
import com.example.alexmelnikov.coinspace.util.PreferencesHelper
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
class ApplicationModule(private val baseApp: BaseApp) {

    private val DATABASE_NAME = "room.db"
    private val BASE_URL = "https://openexchangerates.org/api/"

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
    fun provideAccountsDatabase(context: Context): AccountsDatabase =
            Room.databaseBuilder(context, AccountsDatabase::class.java, DATABASE_NAME).build()

    @Provides
    @Singleton
    fun provideAccountsRepository(accountsDatabase: AccountsDatabase): AccountsRepository =
            DefaultAccountsRepository(accountsDatabase.accountDao())

    @Provides
    @Singleton
    fun providePreferencesHelper(context: Context): PreferencesHelper =
            PreferencesHelper(context)

    @Provides
    @Singleton
    fun provideConnectionHelper(context: Context): ConnectionHelper =
            ConnectionHelper(context)

    @Provides
    @Singleton
    fun provideCurrencyConverter(): CurrencyConverter = CurrencyConverter()


    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(BASE_URL)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()

    @Provides
    @Singleton
    fun provideUserBalanceInteractor(preferencesHelper: PreferencesHelper,
                                     connectionHelper: ConnectionHelper,
                                     accountsRepository: AccountsRepository,
                                     retrofit: Retrofit): IUserBalanceInteractor =
            UserBalanceInteractor(preferencesHelper, connectionHelper, accountsRepository, retrofit.create(ApiService::class.java))
}