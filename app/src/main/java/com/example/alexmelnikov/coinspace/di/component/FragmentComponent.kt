package com.example.alexmelnikov.coinspace.di.component

import com.example.alexmelnikov.coinspace.di.module.FragmentModule
import com.example.alexmelnikov.coinspace.ui.accounts.AccountsFragment
import com.example.alexmelnikov.coinspace.ui.add_new_account.AddAccountFragment
import com.example.alexmelnikov.coinspace.ui.home.OperationFragment
import com.example.alexmelnikov.coinspace.ui.home.HomeFragment
import dagger.Component

@Component(modules = arrayOf(FragmentModule::class))
interface FragmentComponent {

    fun inject(homeFragment: HomeFragment)

    fun inject(operationFragment: OperationFragment)

    fun inject(accountsFragment: AccountsFragment)

    fun inject(addAccountFragment: AddAccountFragment)
}
