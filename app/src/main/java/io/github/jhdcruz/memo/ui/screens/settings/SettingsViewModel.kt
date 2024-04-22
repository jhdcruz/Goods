package io.github.jhdcruz.memo.ui.screens.settings

import androidx.lifecycle.ViewModel
import io.github.jhdcruz.memo.data.model.User
import kotlinx.coroutines.flow.Flow

abstract class SettingsViewModel : ViewModel() {
    abstract val user: Flow<User>
    abstract val tags: Flow<List<String>>
    abstract val categories: Flow<List<String>>

    abstract fun onGetUser()

    abstract fun onGetTags()

    abstract fun onGetCategories()
}
