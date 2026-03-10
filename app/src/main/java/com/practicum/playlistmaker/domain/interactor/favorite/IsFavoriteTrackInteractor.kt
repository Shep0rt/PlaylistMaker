package com.practicum.playlistmaker.domain.interactor.favorite

interface IsFavoriteTrackInteractor {
    suspend fun execute(trackId: Long): Boolean
}
