package com.mindguard.ai

import android.app.Application
import com.google.firebase.FirebaseApp
import com.google.firebase.appcheck.FirebaseAppCheck
import com.google.firebase.appcheck.playintegrity.PlayIntegrityAppCheckProviderFactory
import com.mindguard.ai.di.AppContainer
import com.mindguard.ai.di.DefaultAppContainer

class MindGuardApp : Application() {

    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        
        // Initialize AppContainer
        container = DefaultAppContainer(applicationContext)

        // Initialize Firebase
        try {
            FirebaseApp.initializeApp(this)
            val firebaseAppCheck = FirebaseAppCheck.getInstance()
            firebaseAppCheck.installAppCheckProviderFactory(
                PlayIntegrityAppCheckProviderFactory.getInstance()
            )
        } catch (e: Exception) {
            // Handled for offline/testing scenarios
        }
    }
}
