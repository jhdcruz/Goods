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
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object AppModule {

    @Provides
    @Singleton
    fun provideFirestore() = Firebase.firestore

    @Provides
    @Singleton
    fun provideAuth() = Firebase.auth

    @Provides
    @Singleton
    fun provideStorage() = Firebase.storage

    @Provides
    @Singleton
    fun provideAnalytics() = Firebase.analytics

    @Provides
    @Singleton
    fun provideCrashlytics() = Firebase.crashlytics

}
