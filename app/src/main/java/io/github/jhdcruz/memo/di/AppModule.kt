package io.github.jhdcruz.memo.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import io.github.jhdcruz.memo.ui.login.LoginViewModel
import io.github.jhdcruz.memo.ui.login.LoginViewModelImpl
import javax.inject.Singleton

@InstallIn(ViewModelComponent::class)
@Module
abstract class AppModule {

    @Binds
    @Singleton
    abstract fun bindLoginViewModel(
        loginViewModel: LoginViewModelImpl
    ): LoginViewModel
}
