package com.example.alexmelnikov.coinspace.di.module

import com.example.alexmelnikov.coinspace.model.Accountant
import com.example.alexmelnikov.coinspace.ui.home.HomeContract
import com.example.alexmelnikov.coinspace.ui.home.HomePresenter
import dagger.Module
import dagger.Provides

@Module
class ModelModule {

    @Provides
    fun provideAccountant(): Accountant {
        return Accountant()
    }

}