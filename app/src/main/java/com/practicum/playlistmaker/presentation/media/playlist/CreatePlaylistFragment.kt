package com.practicum.playlistmaker.presentation.media.playlist

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import android.widget.EditText
import androidx.activity.addCallback
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.StringRes
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentCreatePlaylistBinding
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

class CreatePlaylistFragment : Fragment() {

    private var _binding: FragmentCreatePlaylistBinding? = null
    private val binding get() = _binding!!

    private val args by navArgs<CreatePlaylistFragmentArgs>()
    private val viewModel: CreatePlaylistViewModel by viewModel {
        parametersOf(args.playlist)
    }

    private val pickMedia = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            viewLifecycleOwner.lifecycleScope.launch {
                val context = context ?: return@launch
                val path = withContext(Dispatchers.IO) {
                    copyToPrivateStorage(context, uri)
                } ?: return@launch
                viewModel.setCoverPath(path)
                renderCover(uri)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCreatePlaylistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupToolbar()
        setupMode()
        setupListeners()
        setupObservers()
        setupBackHandler()
        restoreState()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupToolbar() {
        binding.createPlaylistToolbar.setNavigationOnClickListener {
            handleBack()
        }
    }

    private fun setupMode() {
        if (!viewModel.isEditMode) return
        binding.createPlaylistToolbar.title = getString(R.string.playlist_edit_title)
        binding.createButton.text = getString(R.string.playlist_save_button)
    }

    private fun setupListeners() = with(binding) {
        coverContainer.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        rootLayout.setOnClickListener {
            clearFocusAndHideKeyboard()
        }

        nameInput.bindFloatingLabel(nameLabel, R.string.create_playlist_name_hint) { value ->
            viewModel.onNameChanged(value)
        }

        descriptionInput.bindFloatingLabel(descriptionLabel, R.string.create_playlist_description_hint) { value ->
            viewModel.onDescriptionChanged(value)
        }

        createButton.setOnClickListener {
            viewModel.savePlaylist()
        }
    }

    private fun setupObservers() {
        viewModel.isCreateEnabled().observe(viewLifecycleOwner) { enabled ->
            binding.createButton.isEnabled = enabled
        }

        viewModel.saveEvent().observe(viewLifecycleOwner) { event ->
            when (event) {
                is PlaylistSaveEvent.Created -> {
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.create_playlist_created_toast, event.name),
                        Toast.LENGTH_SHORT
                    ).show()
                    findNavController().navigateUp()
                }
                PlaylistSaveEvent.Updated -> {
                    findNavController().navigateUp()
                }
                null -> Unit
            }
            if (event != null) {
                viewModel.onSaveEventHandled()
            }
        }
    }

    private fun setupBackHandler() {
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            handleBack()
        }
    }

    private fun restoreState() {
        binding.nameInput.setText(viewModel.getName())
        binding.descriptionInput.setText(viewModel.getDescription())
        binding.nameInput.updateFloatingLabel(binding.nameLabel, R.string.create_playlist_name_hint)
        binding.descriptionInput.updateFloatingLabel(
            binding.descriptionLabel,
            R.string.create_playlist_description_hint
        )
        viewModel.getCoverPath()?.let { path ->
            renderCover(Uri.fromFile(File(path)))
        }
    }

    private fun handleBack() {
        if (viewModel.isEditMode) {
            findNavController().navigateUp()
            return
        }
        if (viewModel.hasUnsavedChanges()) {
            showExitDialog()
        } else {
            findNavController().navigateUp()
        }
    }

    private fun showExitDialog() {
        androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setTitle(R.string.create_playlist_exit_title)
            .setMessage(R.string.create_playlist_exit_message)
            .setNegativeButton(R.string.create_playlist_exit_cancel, null)
            .setPositiveButton(R.string.create_playlist_exit_confirm) { _, _ ->
                findNavController().navigateUp()
            }
            .show()
    }

    private fun renderCover(uri: Uri) {
        binding.coverImage.isVisible = true
        binding.coverPlaceholder.isVisible = false
        binding.coverImage.setImageURI(uri)
        binding.coverContainer.background = null
    }

    private fun updateFieldLabel(editText: EditText, label: View?, hintText: String) {
        val hasText = !editText.text.isNullOrBlank()
        val shouldShow = editText.isFocused || hasText
        label?.isVisible = shouldShow
        editText.hint = if (shouldShow) "" else hintText
    }

    private fun EditText.bindFloatingLabel(
        label: View,
        @StringRes hintRes: Int,
        onValueChanged: (String) -> Unit
    ) {
        val hintText = getString(hintRes)
        addTextChangedListener(
            onTextChanged = { text, _, _, _ ->
                onValueChanged(text?.toString().orEmpty())
                updateFieldLabel(this, label, hintText)
            }
        )
        setOnFocusChangeListener { _, _ ->
            updateFieldLabel(this, label, hintText)
        }
    }

    private fun EditText.updateFloatingLabel(label: View, @StringRes hintRes: Int) {
        updateFieldLabel(this, label, getString(hintRes))
    }

    private fun clearFocusAndHideKeyboard() {
        binding.nameInput.clearFocus()
        binding.descriptionInput.clearFocus()
        val imm = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        val token = requireActivity().currentFocus?.windowToken ?: binding.rootLayout.windowToken
        imm.hideSoftInputFromWindow(token, 0)
    }

    private fun copyToPrivateStorage(context: Context, uri: Uri): String? {
        val inputStream = context.contentResolver.openInputStream(uri) ?: return null
        val fileName = "playlist_cover_${System.currentTimeMillis()}.jpg"
        val file = File(context.filesDir, fileName)
        inputStream.use { input ->
            file.outputStream().use { output ->
                input.copyTo(output)
            }
        }
        return file.absolutePath
    }
}