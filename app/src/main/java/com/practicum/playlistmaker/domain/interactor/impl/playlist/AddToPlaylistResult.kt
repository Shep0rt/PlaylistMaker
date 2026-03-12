package com.practicum.playlistmaker.domain.interactor.impl.playlist

sealed class AddToPlaylistResult {
    object Added : AddToPlaylistResult()
    object AlreadyExists : AddToPlaylistResult()
}