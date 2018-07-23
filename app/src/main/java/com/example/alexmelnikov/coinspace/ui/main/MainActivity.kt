package com.example.alexmelnikov.coinspace.ui.main

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.text.method.LinkMovementMethod
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import com.afollestad.materialdialogs.MaterialDialog
import com.example.alexmelnikov.coinspace.R
import com.example.alexmelnikov.coinspace.di.component.DaggerActivityComponent
import com.example.alexmelnikov.coinspace.ui.home.OperationFragment
import com.example.alexmelnikov.coinspace.ui.home.HomeFragment
import com.example.alexmelnikov.coinspace.ui.settings.SettingsActivity
import kotlinx.android.synthetic.main.activity_main.*
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper
import javax.inject.Inject

class MainActivity : AppCompatActivity(), MainContract.View {

    @Inject
    lateinit var presenter: MainContract.Presenter

    private lateinit var appInfoDialog: MaterialDialog
    private lateinit var homeFragment: HomeFragment
    private lateinit var operationFragment: OperationFragment

    private fun injectDependency() {
        val activityComponent = DaggerActivityComponent.builder().build()
        activityComponent.inject(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        injectDependency()
        presenter.attach(this)

        // Setup toolbar
        setSupportActionBar(toolbar)
        toolbar.overflowIcon = ContextCompat.getDrawable(this, R.drawable.ic_more_vert_white_24dp)

        homeFragment = openHomeFragment()

        //Setup dialog with application info
        appInfoDialog = MaterialDialog.Builder(this)
                .customView(R.layout.dialog_app_info, false)
                .positiveText(android.R.string.ok)
                //.dismissListener()
                .build()
        appInfoDialog.view.findViewById<TextView>(R.id.tv_content).movementMethod = LinkMovementMethod.getInstance()
    }

    override fun attachBaseContext(newBase: Context?) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase))
    }

    fun openHomeFragment(): HomeFragment {
         return supportFragmentManager.findFragmentById(R.id.contentFrame)
                as HomeFragment? ?: HomeFragment.newInstance().also {
            supportFragmentManager.beginTransaction().replace(R.id.contentFrame, it)
                    .commit()
        }
    }

    fun openActionFragment(circleAnimationCenter: View): OperationFragment {
        operationFragment = supportFragmentManager.findFragmentById(R.id.actionFrame)
                as OperationFragment? ?: OperationFragment.newInstance(sourceView = circleAnimationCenter).also {
            supportFragmentManager.beginTransaction()
                    .replace(R.id.actionFrame, it)
                    .addToBackStack(null)
                    .commit()
        }
        return operationFragment
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item?.itemId == R.id.settings) {
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
            return true
        } else if (item?.itemId == R.id.about) {
            appInfoDialog.show()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}
