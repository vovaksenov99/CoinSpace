package com.example.alexmelnikov.coinspace.di.module

import android.app.Application
import android.arch.persistence.room.Room
import android.content.Context
import com.example.alexmelnikov.coinspace.BaseApp
import com.example.alexmelnikov.coinspace.model.Accountant
import com.example.alexmelnikov.coinspace.model.persistance.AccountsDatabase
import com.example.alexmelnikov.coinspace.model.repositories.AccountsRepository
import com.example.alexmelnikov.coinspace.model.repositories.DefaultAccountsRepository
import com.example.alexmelnikov.coinspace.util.PreferencesHelper
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class ApplicationModule(private val baseApp: BaseApp) {

    private val DATABASE_NAME = "room.db"

    @Provides
    @Singleton
    fun provideApplication(): Application {
        return baseApp
    }

    @Provides
    @Singleton
    fun provideAccountant(): Accountant {
        return Accountant()
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
    fun providePreferencesHelper(context: Context): PreferencesHelper =
            PreferencesHelper(context)

    @Provides
    @Singleton
    fun provideAccountsRepository(accountsDatabase: AccountsDatabase): AccountsRepository =
            DefaultAccountsRepository(accountsDatabase.accountDao())

}