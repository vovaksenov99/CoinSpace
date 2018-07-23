package com.example.alexmelnikov.coinspace.di.component

import com.example.alexmelnikov.coinspace.di.module.ModelModule
import com.example.alexmelnikov.coinspace.ui.home.HomePresenter
import dagger.Component

@Component(modules = arrayOf(ModelModule::class))
interface ModelComponent {

    fun inject(homePresenter: HomePresenter)

}