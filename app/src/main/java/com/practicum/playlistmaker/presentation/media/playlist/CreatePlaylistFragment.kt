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
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentCreatePlaylistBinding
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File

class CreatePlaylistFragment : Fragment() {

    private var _binding: FragmentCreatePlaylistBinding? = null
    private val binding get() = _binding!!

    private val viewModel: CreatePlaylistViewModel by viewModel()

    private val pickMedia = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            val path = copyToPrivateStorage(uri)
            viewModel.setCoverPath(path)
            renderCover(uri)
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

    private fun setupListeners() = with(binding) {
        coverContainer.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        rootLayout.setOnClickListener {
            clearFocusAndHideKeyboard()
        }

        nameInput.addTextChangedListener(
            onTextChanged = { text, _, _, _ ->
                viewModel.onNameChanged(text?.toString().orEmpty())
                updateFieldLabel(
                    editText = nameInput,
                    label = nameLabel,
                    hintText = getString(R.string.create_playlist_name_hint)
                )
            }
        )

        descriptionInput.addTextChangedListener(
            onTextChanged = { text, _, _, _ ->
                viewModel.onDescriptionChanged(text?.toString().orEmpty())
                updateFieldLabel(
                    editText = descriptionInput,
                    label = descriptionLabel,
                    hintText = getString(R.string.create_playlist_description_hint)
                )
            }
        )

        nameInput.setOnFocusChangeListener { _, _ ->
            updateFieldLabel(
                editText = nameInput,
                label = nameLabel,
                hintText = getString(R.string.create_playlist_name_hint)
            )
        }

        descriptionInput.setOnFocusChangeListener { _, _ ->
            updateFieldLabel(
                editText = descriptionInput,
                label = descriptionLabel,
                hintText = getString(R.string.create_playlist_description_hint)
            )
        }

        createButton.setOnClickListener {
            viewModel.createPlaylist()
        }
    }

    private fun setupObservers() {
        viewModel.isCreateEnabled().observe(viewLifecycleOwner) { enabled ->
            binding.createButton.isEnabled = enabled
        }

        viewModel.createdEvent().observe(viewLifecycleOwner) { name ->
            if (name.isNullOrBlank()) return@observe

            Toast.makeText(
                requireContext(),
                getString(R.string.create_playlist_created_toast, name),
                Toast.LENGTH_SHORT
            ).show()
            viewModel.onCreatedEventHandled()
            findNavController().navigateUp()
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
        updateFieldLabel(
            editText = binding.nameInput,
            label = binding.nameLabel,
            hintText = getString(R.string.create_playlist_name_hint)
        )
        updateFieldLabel(
            editText = binding.descriptionInput,
            label = binding.descriptionLabel,
            hintText = getString(R.string.create_playlist_description_hint)
        )
        viewModel.getCoverPath()?.let { path ->
            renderCover(Uri.fromFile(File(path)))
        }
    }

    private fun handleBack() {
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

    private fun clearFocusAndHideKeyboard() {
        binding.nameInput.clearFocus()
        binding.descriptionInput.clearFocus()
        val imm = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        val token = requireActivity().currentFocus?.windowToken ?: binding.rootLayout.windowToken
        imm.hideSoftInputFromWindow(token, 0)
    }

    private fun copyToPrivateStorage(uri: Uri): String? {
        val inputStream = requireContext().contentResolver.openInputStream(uri) ?: return null
        val fileName = "playlist_cover_${System.currentTimeMillis()}.jpg"
        val file = File(requireContext().filesDir, fileName)
        inputStream.use { input ->
            file.outputStream().use { output ->
                input.copyTo(output)
            }
        }
        return file.absolutePath
    }
}