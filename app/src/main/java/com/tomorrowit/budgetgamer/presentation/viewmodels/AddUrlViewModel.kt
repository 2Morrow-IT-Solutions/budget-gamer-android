package com.tomorrowit.budgetgamer.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tomorrowit.budgetgamer.R
import com.tomorrowit.budgetgamer.domain.remote_repo.CallResult
import com.tomorrowit.budgetgamer.domain.remote_repo.RemoteRepo
import com.tomorrowit.budgetgamer.domain.usecases.GetStringResourceForIdUseCase
import com.tomorrowit.budgetgamer.domain.usecases.IsNewUrlUseCase
import com.tomorrowit.budgetgamer.domain.usecases.UrlCheckResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddUrlViewModel @Inject constructor(
    private val getStringResourceForIdUseCase: GetStringResourceForIdUseCase,
    private val isNewUrlUseCase: IsNewUrlUseCase,
    private val remoteRepo: RemoteRepo,
) : ViewModel() {

    private val _stateUrl =
        MutableStateFlow<AddUrlState>(AddUrlState.Init)
    val stateUrl: StateFlow<AddUrlState> get() = _stateUrl

    fun addLink(isGame: Boolean, url: String) {
        viewModelScope.launch {
            _stateUrl.value = AddUrlState.Init
            if (url.isEmpty()) {
                _stateUrl.value =
                    AddUrlState.ShowWarning(getStringResourceForIdUseCase(R.string.empty_field))
            } else {
                _stateUrl.value = AddUrlState.IsLoading

                val result = isNewUrlUseCase.invoke(isGame, url)
                when (result) {
                    UrlCheckResult.IsAccepted -> {
                        if (isGame) {
                            _stateUrl.value =
                                AddUrlState.IsAllowed(getStringResourceForIdUseCase.invoke(R.string.game_accepted_list))
                        } else {
                            _stateUrl.value =
                                AddUrlState.IsAllowed(getStringResourceForIdUseCase.invoke(R.string.article_accepted_list))
                        }
                    }

                    UrlCheckResult.IsDenied -> {
                        if (isGame) {
                            _stateUrl.value =
                                AddUrlState.IsDenied(getStringResourceForIdUseCase.invoke(R.string.game_denied_list))
                        } else {
                            _stateUrl.value =
                                AddUrlState.IsDenied(getStringResourceForIdUseCase.invoke(R.string.article_denied_list))
                        }
                    }

                    UrlCheckResult.IsNew -> {
                        if (isGame) {
                            addGameLinkUrl(url)
                        } else {
                            addArticleLinkUrl(url)
                        }
                    }

                    is UrlCheckResult.Error -> {
                        _stateUrl.value =
                            AddUrlState.ShowWarning(getStringResourceForIdUseCase.invoke(R.string.error_try_again))
                    }
                }
            }
        }
    }

    private suspend fun addGameLinkUrl(url: String) {
        val resultAddGame = remoteRepo.addGameLinkUrl(url)
        if (resultAddGame is CallResult.IsSuccess) {
            _stateUrl.value =
                AddUrlState.IsSuccess(getStringResourceForIdUseCase.invoke(R.string.add_game_success))
        } else if (resultAddGame is CallResult.IsFailure) {
            _stateUrl.value =
                AddUrlState.ShowWarning(getStringResourceForIdUseCase.invoke(R.string.add_game_failure))
        }
    }

    private suspend fun addArticleLinkUrl(url: String) {
        val resultAddGame = remoteRepo.addArticleLinkUrl(url)
        if (resultAddGame is CallResult.IsSuccess) {
            _stateUrl.value =
                AddUrlState.IsSuccess(getStringResourceForIdUseCase.invoke(R.string.add_article_success))
        } else if (resultAddGame is CallResult.IsFailure) {
            _stateUrl.value =
                AddUrlState.ShowWarning(getStringResourceForIdUseCase.invoke(R.string.add_article_failure))
        }
    }
}

sealed class AddUrlState {
    data object Init : AddUrlState()
    data object IsLoading : AddUrlState()
    data class IsSuccess(val message: String) : AddUrlState()
    data class IsDenied(val message: String) : AddUrlState()
    data class IsAllowed(val message: String) : AddUrlState()
    data class ShowWarning(val message: String) : AddUrlState()
}