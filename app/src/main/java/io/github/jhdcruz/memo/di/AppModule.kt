package io.github.jhdcruz.memo.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import io.github.jhdcruz.memo.ui.screens.container.ContainerViewModel
import io.github.jhdcruz.memo.ui.screens.container.ContainerViewModelImpl
import io.github.jhdcruz.memo.ui.screens.login.LoginViewModel
import io.github.jhdcruz.memo.ui.screens.login.LoginViewModelImpl
import io.github.jhdcruz.memo.ui.screens.tasks.TasksViewModel
import io.github.jhdcruz.memo.ui.screens.tasks.TasksViewModelImpl

@InstallIn(ViewModelComponent::class)
@Module
abstract class AppModule {
    @Binds
    abstract fun bindLoginViewModel(loginViewModel: LoginViewModelImpl): LoginViewModel

    @Binds
    abstract fun bindContainerViewModel(
        containerViewModel: ContainerViewModelImpl,
    ): ContainerViewModel

    @Binds
    abstract fun bindTasksViewModel(tasksViewModel: TasksViewModelImpl): TasksViewModel
}
