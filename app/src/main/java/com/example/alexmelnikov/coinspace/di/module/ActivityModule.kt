package com.example.alexmelnikov.coinspace.di.module

import android.app.Activity
import com.example.alexmelnikov.coinspace.ui.main.MainContract
import com.example.alexmelnikov.coinspace.ui.main.MainPresenter
import dagger.Module
import dagger.Provides


@Module
class ActivityModule {


    @Provides
    fun provideMainPresenter(): MainContract.Presenter = MainPresenter()

}