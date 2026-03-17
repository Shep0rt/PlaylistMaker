package com.practicum.playlistmaker.presentation.media.playlist

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.domain.interactor.playlist.CreatePlaylistInteractor
import com.practicum.playlistmaker.domain.interactor.playlist.UpdatePlaylistInteractor
import com.practicum.playlistmaker.presentation.models.PlaylistEditUiDto
import kotlinx.coroutines.launch

class CreatePlaylistViewModel(
    private val playlist: PlaylistEditUiDto?,
    private val createPlaylistInteractor: CreatePlaylistInteractor,
    private val updatePlaylistInteractor: UpdatePlaylistInteractor
) : ViewModel() {

    private val isCreateEnabled = MutableLiveData(false)
    fun isCreateEnabled(): LiveData<Boolean> = isCreateEnabled

    private val saveEvent = MutableLiveData<PlaylistSaveEvent?>()
    fun saveEvent(): LiveData<PlaylistSaveEvent?> = saveEvent

    private val editData = MutableLiveData<PlaylistEditUiDto>()
    fun editData(): LiveData<PlaylistEditUiDto> = editData

    private val editPlaylistId: Long = playlist?.id ?: 0L
    val isEditMode: Boolean = playlist != null

    private var name: String = ""
    private var description: String = ""
    private var coverPath: String? = null

    init {
        if (isEditMode) {
            initEditData()
        }
    }

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

    fun savePlaylist() {
        if (name.isBlank()) return

        viewModelScope.launch {
            if (isEditMode) {
                updatePlaylistInteractor.execute(
                    playlistId = editPlaylistId,
                    name = name,
                    description = description.ifBlank { null },
                    coverPath = coverPath
                )
                saveEvent.postValue(PlaylistSaveEvent.Updated)
            } else {
                createPlaylistInteractor.execute(
                    name = name,
                    description = description.ifBlank { null },
                    coverPath = coverPath
                )
                saveEvent.postValue(PlaylistSaveEvent.Created(name))
            }
        }
    }

    fun onSaveEventHandled() {
        saveEvent.value = null
    }

    private fun initEditData() {
        val playlist = playlist ?: return
        name = playlist.name
        description = playlist.description.orEmpty()
        coverPath = playlist.coverPath
        isCreateEnabled.value = name.isNotBlank()
        editData.value = playlist
    }
}
