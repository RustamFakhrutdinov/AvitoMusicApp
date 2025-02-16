package com.practicum.avitomusicapp.ui.player

import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.Group
import androidx.fragment.app.Fragment
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.practicum.avitomusicapp.R
import com.practicum.avitomusicapp.databinding.FragmentPlayerBinding
import com.practicum.avitomusicapp.domain.models.Track
import com.practicum.avitomusicapp.ui.player.state.PlayStatus
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerFragment : Fragment() {
    companion object {
        private const val CLICK_DEBOUNCE_DELAY = 1000L
        private const val TRACK_PREVIEW_DURATION = 30000
    }

    private var _binding: FragmentPlayerBinding? = null
    private val binding get() = _binding!!

    private lateinit var cover: ImageView
    private lateinit var trackTitle: TextView
    private lateinit var trackPerformer: TextView
    private lateinit var trackTime: TextView
    private lateinit var playButton: ImageButton
    private lateinit var albumTitle: TextView
    private lateinit var seekBar: SeekBar

    private lateinit var track: Track

    private val viewModel: PlayerViewModel by viewModel()
    private val args: PlayerFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlayerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.preparePlayerScreen(args.track)
        viewModel.jsonToList(args.trackList ?: "")

        initializeViews()

        viewModel.getPlayStatusLiveData().observe(viewLifecycleOwner) { playStatus ->
            playButtonChange(playStatus)
        }
        viewModel.getPlayScreenLiveData().observe(viewLifecycleOwner) { track ->
            if(args.isFromDownloads) {
                val time = viewModel.timeToMilliseconds(track.duration)
                seekBar.max = time
            } else {
                seekBar.max = TRACK_PREVIEW_DURATION
            }
            addInformation(track)

        }
        viewModel.getSeekBarProgressLiveData().observe(viewLifecycleOwner) { progress ->
            seekBar.progress = progress
            trackTime.text = formatTime(progress)
        }

        playButton.setOnClickListener {
            val currentPlayStatus = viewModel.getPlayStatusLiveData().value
            if (currentPlayStatus?.isPlaying == true) {
                viewModel.pause()
            } else {
                viewModel.play()
            }
        }

        binding.backButtonPlayer.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.fastForwardButton.setOnClickListener {
            viewModel.fastForwardPressed()
        }

        binding.rewindButton.setOnClickListener {
            viewModel.rewindPressed()
        }

        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    viewModel.seekTo(progress)
                    trackTime.text = formatTime(progress)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
               // viewModel.pause()
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                viewModel.play()
            }
        })
    }

    private fun formatTime(progress: Int): String {
        return SimpleDateFormat("mm:ss", Locale.getDefault()).format(progress)
    }

    private fun playButtonChange(playStatus: PlayStatus) {
        if (playStatus.isPlaying) {
            playButton.setImageResource(R.drawable.pause)
        } else {
            playButton.setImageResource(R.drawable.play)
        }
    }

    private fun initializeViews() {
        cover = binding.cover
        trackTitle = binding.trackName
        trackPerformer = binding.performerName
        playButton = binding.playButton
        trackTime = binding.trackTime
        albumTitle = binding.albumName
        seekBar = binding.seekBar
    }

    private fun addInformation(track: Track) {
        trackTitle.text = track.title
        trackPerformer.text = track.artist.name
        trackTime.text = "00:00"
        if (track.album.title != null) {
            albumTitle.text = track.album.title
        } else {
            albumTitle.visibility = View.GONE
        }
        Glide.with(requireContext())
            .load(track.image_url)
            .placeholder(R.drawable.image_placeholder)
            .centerCrop()
            .transform(RoundedCorners(dpToPx(8f, requireContext())))
            .into(cover)
    }

    private fun dpToPx(dp: Float, context: Context): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp,
            context.resources.displayMetrics
        ).toInt()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}