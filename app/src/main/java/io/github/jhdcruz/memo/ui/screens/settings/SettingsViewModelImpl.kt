package io.github.jhdcruz.memo.ui.screens.settings

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.jhdcruz.memo.data.auth.AuthenticationRepository
import io.github.jhdcruz.memo.data.model.User
import io.github.jhdcruz.memo.data.task.TasksRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModelImpl
    @Inject
    constructor(
        private val tasksRepository: TasksRepository,
        private val authenticationRepository: AuthenticationRepository,
    ) : SettingsViewModel() {
        private val _user = MutableStateFlow(User())
        override val user: Flow<User> = _user

        private val _tags = MutableStateFlow(emptyList<String>())
        override val tags: Flow<List<String>> = _tags

        private val _categories = MutableStateFlow(emptyList<String>())
        override val categories: Flow<List<String>> = _categories

        init {
            viewModelScope.launch {
                onGetUser()
            }
        }

        override fun onGetUser() {
            viewModelScope.launch {
                _user.value = authenticationRepository.getUser()
            }
        }

        override fun onGetTags() {
            viewModelScope.launch {
                _tags.value = tasksRepository.onGetTags()
            }
        }

        override fun onGetCategories() {
            viewModelScope.launch {
                _categories.value = tasksRepository.onGetCategories()
            }
        }
    }
