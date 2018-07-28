package com.example.alexmelnikov.coinspace

import android.app.Activity
import android.app.Application
import android.support.v4.app.Fragment
import com.example.alexmelnikov.coinspace.di.component.*
import com.example.alexmelnikov.coinspace.di.module.ApplicationModule
import uk.co.chrisjenx.calligraphy.CalligraphyConfig

class BaseApp : Application() {

    val BASE_FONT = "fonts/Roboto-Regular.ttf"

    lateinit var component: ApplicationComponent

    override fun onCreate() {
        super.onCreate()

        instance = this
        setup()

        CalligraphyConfig.initDefault(CalligraphyConfig.Builder()
                .setDefaultFontPath(BASE_FONT)
                .setFontAttrId(R.attr.fontPath)
                .build())

        component.inject(this)
    }

    private fun setup() {
        component = DaggerApplicationComponent.builder()
                .applicationModule(ApplicationModule(this))
                .build()
    }


    companion object {
        lateinit var instance: BaseApp private set
    }

}