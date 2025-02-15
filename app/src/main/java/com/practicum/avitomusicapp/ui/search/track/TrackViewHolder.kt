package com.practicum.playlistmaker.search.ui.track

import android.content.Context
import android.util.TypedValue
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.practicum.avitomusicapp.R
import com.practicum.avitomusicapp.databinding.TrackItemBinding
import com.practicum.avitomusicapp.domain.models.Track

class TrackViewHolder (binding: TrackItemBinding): RecyclerView.ViewHolder(binding.root) {
    private val title: TextView = binding.title
    private val artistName: TextView = binding.artistName
    private val trackCover: ImageView = binding.trackCover
    var onTrackClickListener: OnTrackClickListener? = null
    fun bind(track: Track) {
        val imageUrl:String = track.getCover56()
        artistName.requestLayout()
        title.text = track.title
        artistName.text = track.artist.name
        Glide.with(itemView)
            .load(imageUrl)
            .placeholder(R.drawable.image_placeholder)
            .centerCrop()
            .transform(RoundedCorners(dpToPx(2f,itemView.context)))
            .into(trackCover)
        itemView.setOnClickListener {
            onTrackClickListener?.onTrackClick(track)
        }
    }
    fun dpToPx(dp: Float, context: Context): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp,
            context.resources.displayMetrics).toInt()
    }
    fun interface OnTrackClickListener {
        fun onTrackClick(track: Track)
    }

}
