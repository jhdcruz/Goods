package io.github.jhdcruz.memo.di

import com.google.firebase.Firebase
import com.google.firebase.analytics.analytics
import com.google.firebase.auth.auth
import com.google.firebase.crashlytics.crashlytics
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.storage
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ServiceComponent
import dagger.hilt.components.SingletonComponent

@InstallIn(SingletonComponent::class, ServiceComponent::class)
@Module
object FirebaseModule {
    @Provides
    fun provideFirestore() = Firebase.firestore

    @Provides
    fun provideAuth() = Firebase.auth

    @Provides
    fun provideStorage() = Firebase.storage

    @Provides
    fun provideAnalytics() = Firebase.analytics

    @Provides
    fun provideCrashlytics() = Firebase.crashlytics
}
