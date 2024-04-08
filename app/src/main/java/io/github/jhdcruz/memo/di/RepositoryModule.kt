package io.github.jhdcruz.memo.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import io.github.jhdcruz.memo.data.auth.AuthenticationRepository
import io.github.jhdcruz.memo.data.auth.AuthenticationRepositoryImpl
import io.github.jhdcruz.memo.data.task.AttachmentsRepository
import io.github.jhdcruz.memo.data.task.AttachmentsRepositoryImpl
import io.github.jhdcruz.memo.data.task.TasksRepository
import io.github.jhdcruz.memo.data.task.TasksRepositoryImpl

@InstallIn(ViewModelComponent::class)
@Module
abstract class RepositoryModule {

    @Binds
    abstract fun bindAuthenticationRepository(
        authenticationRepositoryImpl: AuthenticationRepositoryImpl
    ): AuthenticationRepository

    @Binds
    abstract fun bindTasksRepository(
        tasksRepositoryImpl: TasksRepositoryImpl
    ): TasksRepository

    @Binds
    abstract fun bindAttachmentsRepository(
        attachmentsRepositoryImpl: AttachmentsRepositoryImpl
    ): AttachmentsRepository

}
