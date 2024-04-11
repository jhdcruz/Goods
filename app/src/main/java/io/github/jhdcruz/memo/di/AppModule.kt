package io.github.jhdcruz.memo.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import io.github.jhdcruz.memo.ui.login.LoginViewModel
import io.github.jhdcruz.memo.ui.login.LoginViewModelImpl
import io.github.jhdcruz.memo.ui.tasks.TasksViewModel
import io.github.jhdcruz.memo.ui.tasks.TasksViewModelImpl
import javax.inject.Singleton

@InstallIn(ViewModelComponent::class)
@Module
abstract class AppModule {

    @Binds
    @Singleton
    abstract fun bindLoginViewModel(
        loginViewModel: LoginViewModelImpl,
    ): LoginViewModel

    @Binds
    @Singleton
    abstract fun bindTasksViewModel(
        tasksViewModel: TasksViewModelImpl,
    ): TasksViewModel
}
