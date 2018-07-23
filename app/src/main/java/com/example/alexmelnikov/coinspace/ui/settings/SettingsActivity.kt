package com.example.alexmelnikov.coinspace.ui.settings

import android.content.Context
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.View
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper
import com.example.alexmelnikov.coinspace.R.id.toolbar
import android.view.ViewGroup
import com.example.alexmelnikov.coinspace.R
import android.util.TypedValue


class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        supportActionBar?.title = getString(R.string.preferences_activity_title)

        supportFragmentManager.beginTransaction()
                .replace(android.R.id.content, SettingsFragment())
                .commit()
    }

    override fun attachBaseContext(newBase: Context?) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase))
    }

}