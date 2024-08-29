package com.tomorrowit.budgetgamer.presentation.viewmodels.main_menu

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tomorrowit.budgetgamer.data.entities.ArticleEntity
import com.tomorrowit.budgetgamer.domain.repo.room_repo.ArticleRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ArticlesViewModel @Inject constructor(
    private val articleRepo: ArticleRepo
) : ViewModel() {

    private val _state = MutableStateFlow<ArticlesState>(ArticlesState.Init)
    val state: StateFlow<ArticlesState> get() = _state

    init {
        _state.value = ArticlesState.IsLoading
        viewModelScope.launch {
            articleRepo.getAllArticles(System.currentTimeMillis()).collect {
                if (it.isNotEmpty()) {
                    _state.value = ArticlesState.IsSuccess(it)
                }
            }
        }
    }
}

sealed class ArticlesState {
    data object Init : ArticlesState()
    data object IsLoading : ArticlesState()
    data class IsSuccess(val data: List<ArticleEntity>) : ArticlesState()
    data class IsError(val message: String) : ArticlesState()
}