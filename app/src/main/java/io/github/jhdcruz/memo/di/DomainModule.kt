package io.github.jhdcruz.memo.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import io.github.jhdcruz.memo.domain.signup.SignUpViewModel
import io.github.jhdcruz.memo.domain.signup.SignUpViewModelImpl

@InstallIn(ViewModelComponent::class)
@Module
abstract class DomainModule {

    @Binds
    abstract fun bindSignUpViewModel(
        signUpViewModelImpl: SignUpViewModelImpl
    ): SignUpViewModel
}
