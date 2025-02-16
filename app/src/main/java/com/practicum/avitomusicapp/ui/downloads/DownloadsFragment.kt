package com.practicum.avitomusicapp.ui.search

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.provider.Settings
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.markodevcic.peko.PermissionRequester
import com.markodevcic.peko.PermissionResult
import com.practicum.avitomusicapp.R
import com.practicum.avitomusicapp.databinding.FragmentDownloadsBinding
import com.practicum.avitomusicapp.databinding.FragmentSearchBinding
import com.practicum.avitomusicapp.domain.models.Album
import com.practicum.avitomusicapp.domain.models.Artist
import com.practicum.avitomusicapp.domain.models.Track
import com.practicum.avitomusicapp.ui.downloads.DownloadsViewModel
import com.practicum.avitomusicapp.ui.state.SearchState
import com.practicum.avitomusicapp.util.debounce
import com.practicum.playlistmaker.search.ui.track.TrackAdapter
import com.practicum.playlistmaker.search.ui.track.TrackViewHolder
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class DownloadsFragment : Fragment() {
    private var editTextValue: String = NAME_DEF

    private lateinit var tracksListView: RecyclerView
    private lateinit var placeholderMessage: TextView
    private lateinit var placeholderErrorImage: ImageView
    private lateinit var inputEditText: EditText
    private lateinit var searchTextWatcher: TextWatcher
    private lateinit var progressBar: ProgressBar

    private val tracksList = arrayListOf<Track>()
    private val trackAdapter = TrackAdapter(tracksList)

    private val viewModel: DownloadsViewModel by viewModel() // Используйте Koin для внедрения нового ViewModel
    private lateinit var binding: FragmentDownloadsBinding

    val requester = PermissionRequester.instance()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDownloadsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        val clearButton = binding.clearIcon

        inputEditText.setText(editTextValue)

        searchTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                clearButton.isVisible = clearButtonVisibility(s)
                viewModel.searchDebounce(s?.toString() ?: "", false)
            }

            override fun afterTextChanged(s: Editable?) {}
        }
        searchTextWatcher.let { inputEditText.addTextChangedListener(it) }

        clearButton.setOnClickListener {
            inputEditText.setText("")
            deleteAllInformation()
        }

        inputEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                viewModel.searchDebounce(inputEditText.text.toString(), true)
                true
            }
            false
        }

        //запрашиваем разрешение для просмотра аудиофайлов на телефоне
        lifecycleScope.launch {
            val permission =
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                    android.Manifest.permission.READ_MEDIA_AUDIO
                } else {
                    android.Manifest.permission.READ_EXTERNAL_STORAGE
                }
            requester.request(permission).collect { result ->
                when (result) {
                    is PermissionResult.Granted -> {
                        viewModel.initAfterPermission()
                    }

                    is PermissionResult.Denied.NeedsRationale -> {
                        Toast.makeText(
                            requireContext(),
                            "Для просмотра загруженных треков необходимо предоставить разрешение .",
                            Toast.LENGTH_LONG
                        ).show()
                    }

                    is PermissionResult.Denied.DeniedPermanently -> {
                        Toast.makeText(
                            requireContext(),
                            "Доступ к трекам заблокирован. Разрешите доступ в настройках приложения.",
                            Toast.LENGTH_LONG
                        ).show()
                        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        intent.data = Uri.fromParts("package", context?.packageName ?: "", null)
                        context?.startActivity(intent)
                    }


                    else -> {

                    }
                }
            }
        }


        viewModel.observeState().observe(viewLifecycleOwner) { render(it) }
    }

    private fun initViews() {
        placeholderMessage = binding.placeholderMessage
        placeholderErrorImage = binding.errorCover
        inputEditText = binding.inputEditText
        progressBar = binding.progressBar
        tracksListView = binding.rvTrack
        tracksListView.adapter = trackAdapter
    }

    private fun render(state: SearchState) {
        when (state) {
            is SearchState.Content -> showContent(state.tracks)
            is SearchState.Empty -> showEmpty(state.message)
            is SearchState.Error -> showError(state.errorMessage)
            is SearchState.Loading -> showLoading()
        }
    }

    private fun showLoading() {
        tracksListView.isVisible = false
        placeholderMessage.isVisible = false
        progressBar.isVisible = true
        placeholderErrorImage.isVisible = false
    }

    private fun showError(errorMessage: String) {
        placeholderErrorImage.setImageResource(R.drawable.connection_problems)
        tracksListView.isVisible = false
        placeholderMessage.isVisible = true
        progressBar.isVisible = false
        placeholderErrorImage.isVisible = true
        placeholderMessage.text = errorMessage
    }

    private fun showEmpty(emptyMessage: String) {
        showError(emptyMessage)
        placeholderErrorImage.setImageResource(R.drawable.ic_empty)
    }

    private fun deleteAllInformation() {
        tracksListView.isVisible = false
        placeholderMessage.isVisible = false
        progressBar.isVisible = false
        placeholderErrorImage.isVisible = false
    }

    private fun showContent(tracks: List<Track>) {
        tracksListView.isVisible = true
        placeholderMessage.isVisible = false
        binding.progressBar.isVisible = false
        tracksList.clear()
        tracksList.addAll(tracks)
        trackAdapter.notifyDataSetChanged()
    }

    private fun clearButtonVisibility(s: CharSequence?): Boolean {
        return !s.isNullOrEmpty()

    }

    companion object {
        const val TAG = "DownloadsFragment"
        const val NAME_DEF = ""
        private const val REQUEST_CODE_OPEN_DIRECTORY = 1001
    }
}
