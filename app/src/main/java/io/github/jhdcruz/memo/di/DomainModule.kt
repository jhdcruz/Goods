package io.github.jhdcruz.memo.di

import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import io.github.jhdcruz.memo.domain.auth.AuthViewModel
import io.github.jhdcruz.memo.domain.auth.AuthViewModelImpl
import javax.inject.Singleton

@InstallIn(ViewModelComponent::class)
@Module
abstract class DomainModule {

    @Binds
    @Singleton
    abstract fun bindAuthViewModel(
        authViewModel: AuthViewModelImpl
    ): AuthViewModel
}
