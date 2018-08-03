package com.example.alexmelnikov.coinspace.ui.main

import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.support.transition.Fade
import android.support.transition.Slide
import android.support.transition.TransitionInflater
import android.support.v7.app.AppCompatActivity
import android.transition.ChangeBounds
import android.util.Log
import android.view.Gravity
import android.view.View
import com.example.alexmelnikov.coinspace.R
import com.example.alexmelnikov.coinspace.di.component.DaggerActivityComponent
import com.example.alexmelnikov.coinspace.ui.accounts.AccountsFragment
import com.example.alexmelnikov.coinspace.ui.home.HomeFragment
import com.example.alexmelnikov.coinspace.ui.main.MainContract.Presenter.FragmentOnScreen
import com.example.alexmelnikov.coinspace.ui.settings.SettingsActivity
import com.example.alexmelnikov.coinspace.ui.statistics.StatisticsFragment
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper
import javax.inject.Inject

class MainActivity : AppCompatActivity(), MainContract.View {

    private val CURRENT_ACTIVE_FRAGMENT = "CURRENT_ACTIVE_FRAGMENT"

    @Inject
    lateinit var presenter: MainContract.Presenter


    override fun onCreate(savedInstanceState: Bundle?) {
        DaggerActivityComponent.builder().build().inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        presenter.attach(this)

        if (savedInstanceState != null) {
            presenter.start(savedInstanceState
                    .getSerializable(CURRENT_ACTIVE_FRAGMENT) as FragmentOnScreen)
        } else {
            presenter.start(FragmentOnScreen.HOME)
        }

    }

    override fun attachBaseContext(newBase: Context?) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase))
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState?.apply {
            putSerializable(CURRENT_ACTIVE_FRAGMENT, presenter.activeFragmentOnScreen)
        })
    }

    override fun openSettingsActivityRequest() {
        val intent = Intent(this, SettingsActivity::class.java)
        startActivity(intent)
    }

    override fun openAccountsFragmentRequest() {
        presenter.openAccountsFragmentRequest()
    }

    override fun openStatisticsFragmentRequest(animationCenter: View) {
        presenter.openStatisticsFragmentRequest(animationCenter)
    }

    override fun openHomeFragment(): HomeFragment {
        val fragmentOnScreen = supportFragmentManager.findFragmentById(R.id.contentFrame)
        if (fragmentOnScreen is HomeFragment)
            supportFragmentManager.beginTransaction().remove(fragmentOnScreen)
                    .commit()

        return HomeFragment.newInstance().also {
            it.exitTransition = android.transition.TransitionInflater.from(this).inflateTransition(android.R.transition.fade)
            supportFragmentManager.beginTransaction().replace(R.id.contentFrame, it)
                    .commit()
        }
    }

    override fun openAccountsFragment(): AccountsFragment {
        val fragmentOnScreen = supportFragmentManager.findFragmentById(R.id.contentFrame)
        if (fragmentOnScreen is AccountsFragment)
            supportFragmentManager.popBackStack()

        return AccountsFragment.newInstance().also {
            supportFragmentManager.beginTransaction().replace(R.id.contentFrame, it)
                    .addToBackStack(null)
                    .commit()
        }
    }

    override fun openStatisticsFragment(animationCenter: View?): StatisticsFragment {
        val fragmentOnScreen = supportFragmentManager.findFragmentById(R.id.contentFrame)
        if (fragmentOnScreen is StatisticsFragment)
            supportFragmentManager.popBackStack()

        return StatisticsFragment.newInstance(animationCenter).also {
            supportFragmentManager.beginTransaction().replace(R.id.contentFrame, it)
                    .addToBackStack(null)
                    .commit()
        }
    }
}
