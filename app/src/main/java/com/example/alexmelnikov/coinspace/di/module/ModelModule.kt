package com.example.alexmelnikov.coinspace.di.module

import android.arch.persistence.room.Room
import android.content.Context
import com.example.alexmelnikov.coinspace.BaseApp
import com.example.alexmelnikov.coinspace.model.Accountant
import com.example.alexmelnikov.coinspace.model.persistance.AccountsDatabase
import com.example.alexmelnikov.coinspace.ui.home.HomeContract
import com.example.alexmelnikov.coinspace.ui.home.HomePresenter
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class ModelModule {

    @Provides
    fun provideAccountant(): Accountant {
        return Accountant()
    }

    @Provides
    @Singleton
    fun provideContext(application: BaseApp): Context = application.applicationContext

    @Provides
    @Singleton
    fun provideAccountsDatabase(context: Context): AccountsDatabase =
            Room.databaseBuilder(context, AccountsDatabase::class.java, "room.db").build()

}