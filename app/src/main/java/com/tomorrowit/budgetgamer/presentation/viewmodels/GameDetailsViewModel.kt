package com.tomorrowit.budgetgamer.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tomorrowit.budgetgamer.data.entities.GameWithProvider
import com.tomorrowit.budgetgamer.data.mapper.PlatformMapper
import com.tomorrowit.budgetgamer.data.model.PlatformModel
import com.tomorrowit.budgetgamer.domain.repo.room_repo.GameRepo
import com.tomorrowit.budgetgamer.domain.repo.room_repo.PlatformRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GameDetailsViewModel @Inject constructor(
    private val gameRepo: GameRepo,
    private val platformRepo: PlatformRepo,
    private val platformMapper: PlatformMapper
) : ViewModel() {

    private val _state = MutableStateFlow<GameDetailsState>(GameDetailsState.Init)
    val state: StateFlow<GameDetailsState> get() = _state

    fun startLoadingGame(gameId: String?, gameFromSubscription: Boolean?) {
        if (gameId != null && gameFromSubscription != null) {
            _state.value = GameDetailsState.IsLoading

            viewModelScope.launch {
                gameRepo.getGameById(gameId).collect {
                    if (it != null) {
                        _state.value = GameDetailsState.IsSuccess(it, getPlatforms(it))
                    } else {
                        _state.value =
                            GameDetailsState.IsError("It seems that the game does not exist anymore.")
                    }
                }
            }

        } else {
            _state.value = GameDetailsState.IsError("Something went wrong, please try again later.")
        }
    }

    private suspend fun getPlatforms(gameWithProvider: GameWithProvider): List<PlatformModel> {
        val platformIds = gameWithProvider.gameEntity.platformId.split(",").map { it.trim() }
        val platformEntities = platformRepo.getPlatformsByIds(platformIds).first()
        return platformEntities.map { platformEntity ->
            platformMapper.mapToFirebaseModel(platformEntity)
        }
    }

}

sealed class GameDetailsState {
    data object Init : GameDetailsState()
    data object IsLoading : GameDetailsState()
    data class IsSuccess(val game: GameWithProvider, val platformList: List<PlatformModel>) :
        GameDetailsState()

    data class IsError(val message: String) : GameDetailsState()
}