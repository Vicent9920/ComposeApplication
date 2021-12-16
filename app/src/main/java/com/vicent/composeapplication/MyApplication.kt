package com.vicent.composeapplication

import android.app.Application

/**
 * <pre>
 *     author: Vincent
 *     date  :  2021/11/29 18:39
 *     desc  :
 * </pre>
 */
class MyApplication:Application() {

    companion object{
        lateinit var application: MyApplication
        fun getInstance() = application
    }

    override fun onCreate() {
        super.onCreate()
        application = this
    }
}