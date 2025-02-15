package com.practicum.avitomusicapp.ui.search

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.practicum.avitomusicapp.R
import com.practicum.avitomusicapp.databinding.FragmentSearchBinding
import com.practicum.avitomusicapp.domain.models.Track
import com.practicum.avitomusicapp.ui.state.SearchState
import com.practicum.avitomusicapp.util.debounce
import com.practicum.playlistmaker.search.ui.track.TrackAdapter
import com.practicum.playlistmaker.search.ui.track.TrackViewHolder
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchFragment : Fragment() {
    private var editTextValue: String = NAME_DEF

    private lateinit var tracksListView: RecyclerView
    private lateinit var placeholderMessage: TextView
    private lateinit var placeholderErrorImage: ImageView
    private lateinit var inputEditText: EditText
    private lateinit var searchTextWatcher: TextWatcher
    private lateinit var progressBar: ProgressBar

    private val tracksList = arrayListOf<Track>()
    private val trackAdapter = TrackAdapter(tracksList)

    private val viewModel: SearchViewModel by viewModel()
    private lateinit var binding: FragmentSearchBinding

    private lateinit var clickDebounce: (Track) -> Unit

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        val clearButton = binding.clearIcon

        inputEditText.setText(editTextValue)

        searchTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                clearButton.isVisible = clearButtonVisibility(s)
                viewModel.searchDebounce(
                    changedText = s?.toString() ?: "",
                    false
                )
            }

            override fun afterTextChanged(s: Editable?) {

            }
        }
        searchTextWatcher.let { inputEditText.addTextChangedListener(it) }

        clickDebounce = debounce<Track>(CLICK_DEBOUNCE_DELAY, viewLifecycleOwner.lifecycleScope, false) { item ->
            val direction: NavDirections = SearchFragmentDirections.actionSearchFragmentToPlayerFragment(item)
            findNavController().navigate(direction)
        }

        trackAdapter.onTrackClickListener = TrackViewHolder.OnTrackClickListener { item->
            clickDebounce(item)
        }

        inputEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                viewModel.searchDebounce(inputEditText.text.toString(), true)
                true
            }
            false
        }

        clearButton.setOnClickListener {
            inputEditText.setText("")
            deleteAllInformation()
        }

        viewModel.observeState().observe(viewLifecycleOwner) {
            render(it)
        }

        viewModel.observeShowToast().observe(viewLifecycleOwner) {
            showToast(it)
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        searchTextWatcher.let { inputEditText.removeTextChangedListener(it) }
    }

    private fun clearButtonVisibility(s: CharSequence?): Boolean {
        return !s.isNullOrEmpty()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(SEARCH_NAME, editTextValue)
    }

    private fun initViews() {
        placeholderMessage = binding.placeholderMessage
        placeholderErrorImage = binding.errorCover
        inputEditText = binding.inputEditText
        progressBar = binding.progressBar

        tracksListView = binding.rvTrack
        tracksListView.adapter = trackAdapter
    }

    private fun showToast(additionalMessage: String) {
        Toast.makeText(requireContext(), additionalMessage, Toast.LENGTH_LONG).show()
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

    companion object {
        const val SEARCH_NAME = "TEXT_WATCHER_NAME"
        const val NAME_DEF = ""
        private const val CLICK_DEBOUNCE_DELAY = 1000L


        const val TAG = "SearchFragment"

        fun createArgs(): Bundle =
            bundleOf()
    }
}