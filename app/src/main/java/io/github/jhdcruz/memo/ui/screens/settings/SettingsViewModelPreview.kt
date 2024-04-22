package io.github.jhdcruz.memo.ui.screens.settings

import io.github.jhdcruz.memo.data.model.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class SettingsViewModelPreview : SettingsViewModel() {
    override val user: Flow<User> = flowOf(User())
    override val tags: Flow<List<String>> = flowOf(emptyList())
    override val categories: Flow<List<String>> = flowOf(emptyList())

    override fun onGetUser() {
    }

    override fun onGetTags() {
    }

    override fun onGetCategories() {
    }
}
