package com.example.alexmelnikov.coinspace.ui.main

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import com.example.alexmelnikov.coinspace.R
import com.example.alexmelnikov.coinspace.di.component.DaggerActivityComponent
import com.example.alexmelnikov.coinspace.model.workers.CurrenciesRateWorker
import com.example.alexmelnikov.coinspace.model.workers.PeriodicOperationsWorker
import com.example.alexmelnikov.coinspace.ui.INTRODUCTION_DIALOG_TAG
import com.example.alexmelnikov.coinspace.ui.IntroductionDialog
import com.example.alexmelnikov.coinspace.ui.accounts.AccountsFragment
import com.example.alexmelnikov.coinspace.ui.home.HomeFragment
import com.example.alexmelnikov.coinspace.ui.settings.SettingsActivity
import com.example.alexmelnikov.coinspace.ui.statistics.StatisticsFragment
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class MainActivity : AppCompatActivity(), MainContract.View {

    private val CURRENT_ACTIVE_FRAGMENT = "CURRENT_ACTIVE_FRAGMENT"

    @Inject
    lateinit var presenter: MainContract.Presenter


    public override fun onCreate(savedInstanceState: Bundle?) {
        DaggerActivityComponent.builder().build().inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        presenter.attach(this)

        if (savedInstanceState != null) {
            presenter.start(savedInstanceState
                .getSerializable(CURRENT_ACTIVE_FRAGMENT) as MainContract.FragmentOnScreen)
        }
        else {
            presenter.start(MainContract.FragmentOnScreen.HOME)
        }

        presenter.openIntroductionDialog(this)


        initCurrenciesWorkManager()

        initPeriodicWorkManager()
    }

    override fun openIntroductionDialog() {
        val dialog = IntroductionDialog()
        dialog.show(supportFragmentManager, INTRODUCTION_DIALOG_TAG)
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
                .commitNow()

        return HomeFragment.newInstance().also {
            it.exitTransition = android.transition.TransitionInflater.from(this)
                .inflateTransition(android.R.transition.fade)
            supportFragmentManager.beginTransaction().replace(R.id.contentFrame, it)
                .commitNow()
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

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onBackPressed() {
        super.onBackPressed()
       // val fragmentOnScreen = supportFragmentManager.findFragmentById(R.id.contentFrame)
        presenter.activeFragmentOnScreen = MainContract.FragmentOnScreen.HOME

    }

    private fun initCurrenciesWorkManager() {

        WorkManager.getInstance()?.getStatusesByTag(CurrenciesRateWorker.TAG)?.observeForever {
            for (work in it!!) {
                if (!work.state.isFinished) {
                    return@observeForever
                }
            }

            val currencyUpdater = PeriodicWorkRequest
                .Builder(CurrenciesRateWorker::class.java, 8, TimeUnit.HOURS)
                .addTag(CurrenciesRateWorker.TAG)
                .build()

            Log.i("", "Currency update work manager start")
            WorkManager.getInstance()?.enqueue(currencyUpdater)

        }
    }

    private fun initPeriodicWorkManager() {

        WorkManager.getInstance()?.getStatusesByTag(PeriodicOperationsWorker.TAG)?.observeForever {
            for (work in it!!) {
                if (!work.state.isFinished) {
                    return@observeForever
                }
            }

            val periodicWorkRequest = PeriodicWorkRequest
                .Builder(PeriodicOperationsWorker::class.java, 1, TimeUnit.DAYS)
                .addTag(PeriodicOperationsWorker.TAG)
                .build()

            Log.i("", "Periodic operation work manager start")
            WorkManager.getInstance()?.enqueue(periodicWorkRequest)

        }
    }
}
