package com.example.alexmelnikov.coinspace.di.component

import com.example.alexmelnikov.coinspace.di.module.FragmentModule
import com.example.alexmelnikov.coinspace.ui.home.OperationFragment
import com.example.alexmelnikov.coinspace.ui.home.HomeFragment
import dagger.Component

@Component(modules = arrayOf(FragmentModule::class))
interface FragmentComponent {

    fun inject(homeFragment: HomeFragment)

    fun inject(operationFragment: OperationFragment)
}