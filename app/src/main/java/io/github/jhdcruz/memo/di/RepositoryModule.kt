package io.github.jhdcruz.memo.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import io.github.jhdcruz.memo.data.auth.AuthenticationRepository
import io.github.jhdcruz.memo.data.auth.AuthenticationRepositoryImpl

@InstallIn(ViewModelComponent::class)
@Module
abstract class RepositoryModule {

    @Binds
    abstract fun bindAuthenticationRepository(
        authenticationRepositoryImpl: AuthenticationRepositoryImpl
    ): AuthenticationRepository

}
