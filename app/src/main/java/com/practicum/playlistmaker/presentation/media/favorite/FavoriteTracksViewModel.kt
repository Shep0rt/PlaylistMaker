package com.practicum.playlistmaker.presentation.media.favorite

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.domain.interactor.favorite.GetFavoriteTracksInteractor
import com.practicum.playlistmaker.presentation.mappers.DomainToUiMapper
import kotlinx.coroutines.launch

class FavoriteTracksViewModel(
    private val getFavoriteTracksInteractor: GetFavoriteTracksInteractor
) : ViewModel() {

    private val state = MutableLiveData<FavoriteTracksState>()
    val favoriteTracksState: LiveData<FavoriteTracksState> = state

    init {
        fillData()
    }

    fun fillData() {
        state.value = FavoriteTracksState.Loading
        viewModelScope.launch {
            getFavoriteTracksInteractor.execute().collect { tracks ->
                if (tracks.isEmpty()) {
                    state.postValue(FavoriteTracksState.Empty)
                } else {
                    val uiTracks = tracks.map { DomainToUiMapper.trackToUi(it) }
                    state.postValue(FavoriteTracksState.Content(uiTracks))
                }
            }
        }
    }
}