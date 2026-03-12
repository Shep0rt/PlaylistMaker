package com.practicum.playlistmaker.presentation.media.playlist

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.domain.interactor.playlist.CreatePlaylistInteractor
import kotlinx.coroutines.launch

class CreatePlaylistViewModel(
    private val createPlaylistInteractor: CreatePlaylistInteractor
) : ViewModel() {

    private val isCreateEnabled = MutableLiveData(false)
    fun isCreateEnabled(): LiveData<Boolean> = isCreateEnabled

    private val createdEvent = MutableLiveData<String?>()
    fun createdEvent(): LiveData<String?> = createdEvent

    private var name: String = ""
    private var description: String = ""
    private var coverPath: String? = null

    fun onNameChanged(value: String) {
        name = value
        isCreateEnabled.value = value.isNotBlank()
    }

    fun onDescriptionChanged(value: String) {
        description = value
    }

    fun setCoverPath(path: String?) {
        coverPath = path
    }

    fun getName(): String = name

    fun getDescription(): String = description

    fun getCoverPath(): String? = coverPath

    fun hasUnsavedChanges(): Boolean {
        return name.isNotBlank() || description.isNotBlank() || coverPath != null
    }

    fun createPlaylist() {
        if (name.isBlank()) return

        viewModelScope.launch {
            createPlaylistInteractor.execute(
                name = name,
                description = description.ifBlank { null },
                coverPath = coverPath
            )
            createdEvent.postValue(name)
        }
    }

    fun onCreatedEventHandled() {
        createdEvent.value = null
    }
}