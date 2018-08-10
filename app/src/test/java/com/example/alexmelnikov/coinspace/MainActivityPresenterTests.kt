package com.example.alexmelnikov.coinspace

import android.content.Context
import android.os.Build
import android.transition.Transition
import android.transition.TransitionInflater
import android.view.View
import com.example.alexmelnikov.coinspace.di.component.ActivityComponent
import com.example.alexmelnikov.coinspace.ui.main.MainActivity
import com.example.alexmelnikov.coinspace.ui.main.MainContract
import com.example.alexmelnikov.coinspace.ui.main.MainPresenter
import com.nhaarman.mockito_kotlin.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.anyObject
import org.mockito.Mockito
import javax.inject.Inject

class MainActivityPresenterTests {

    val presenter = Mockito.mock(MainContract.Presenter::class.java)
    val view = MainActivity()
    val context =  Mockito.mock(Context::class.java)

    @Before
    fun before()
    {
        presenter.attach(view)
    }

    @Test
    fun OpenMainContacPresenter() {

        val v = Mockito.mock(View::class.java)
        presenter.openStatisticsFragmentRequest(v)
        verify(presenter,atLeastOnce()).openStatisticsFragmentRequest(v)
    }

    @Test
    fun openAccountsFragmentRequest() {

        presenter.openAccountsFragmentRequest()
        verify(presenter,atLeastOnce()).openAccountsFragmentRequest()
    }

    @Test
    fun openHomeFragmentRequest(){

        presenter.openHomeFragmentRequest()
        verify(presenter,atLeastOnce()).openHomeFragmentRequest()
    }

    @Test
    fun openStatisticsFragmentRequest(){

        presenter.openIntroductionDialog(context)
        verify(presenter,atLeastOnce()).openIntroductionDialog(context)
    }
}