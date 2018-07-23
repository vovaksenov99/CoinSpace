package com.example.alexmelnikov.coinspace.di.module

import com.example.alexmelnikov.coinspace.ui.home.HomeContract
import com.example.alexmelnikov.coinspace.ui.home.HomePresenter
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class FragmentModule {

    @Provides
    fun provideHomePresenter(): HomeContract.Presenter {
        return HomePresenter()
    }

}