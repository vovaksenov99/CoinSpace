package com.example.alexmelnikov.coinspace.di.component

import com.example.alexmelnikov.coinspace.BaseApp
import com.example.alexmelnikov.coinspace.di.module.ApplicationModule
import dagger.Component

@Component(modules = arrayOf(ApplicationModule::class))
interface ApplicationComponent {

    fun inject(application: BaseApp)

}