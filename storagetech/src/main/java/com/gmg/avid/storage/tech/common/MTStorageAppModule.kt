package com.gmg.avid.storage.tech.common

import android.content.Context
import java.lang.ref.WeakReference

/**
 * This class contain context application level handling data
 */
object MTStorageAppModule {

    private var mContextReference: WeakReference<Context?> = WeakReference(null)

    /**
     * This method to initialize the application context to library
     */
    fun initializeApp(context: Context) {
        mContextReference = WeakReference(context)
    }

    /**
     * This method called to get application context
     */
    fun getAppTechContext(): Context? {
        return mContextReference.get()
    }
}