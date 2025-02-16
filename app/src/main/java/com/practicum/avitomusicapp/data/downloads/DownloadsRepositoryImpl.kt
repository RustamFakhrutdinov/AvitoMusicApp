package com.practicum.avitomusicapp.data.downloads

import android.app.Application
import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import com.practicum.avitomusicapp.domain.downloads.DownloadsRepository
import com.practicum.avitomusicapp.domain.models.Album
import com.practicum.avitomusicapp.domain.models.Artist
import com.practicum.avitomusicapp.domain.models.Track

class DownloadsRepositoryImpl(private val context: Context): DownloadsRepository {

    override fun getAllTracks(): List<Track> {//получаем список всех треков
        val trackList = mutableListOf<Track>()
        val contentResolver: ContentResolver = context.contentResolver

        val projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.DATA
        )

        val sortOrder = "${MediaStore.Audio.Media.TITLE} ASC"

        // Получаем треки из MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val mediaUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        queryAndAddTracks(contentResolver, mediaUri, projection, sortOrder, trackList)

        return trackList
    }

    override fun findTracksFromLocalStorage(query: String): List<Track> {// ищем треки по query
        val tracks = mutableListOf<Track>()

        val contentResolver: ContentResolver = context.contentResolver
        val uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val projection = arrayOf(
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.DATA,
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.DURATION
        )

        val selection = "${MediaStore.Audio.Media.TITLE} LIKE ?"
        val selectionArgs = arrayOf("%$query%")

        val cursor = contentResolver.query(uri, projection, selection, selectionArgs, null)

        cursor?.use {
            while (it.moveToNext()) {
                val title = it.getString(it.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE))
                val artistName = it.getString(it.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST))
                val path = it.getString(it.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA))
                val albumName = it.getString(it.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM))
                val duration = it.getLong(it.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)).toString()

                val artist = Artist(name = artistName)
                val album = Album(title = albumName, cover = null)

                val track = Track(
                    title = title,
                    duration = duration,
                    image_url = "",
                    preview = path,
                    artist = artist,
                    album = album
                )

                tracks.add(track)
            }
        }

        return tracks
    }

    private fun queryAndAddTracks(
        contentResolver: ContentResolver,
        uri: Uri,
        projection: Array<String>,
        sortOrder: String,
        trackList: MutableList<Track>
    ) {
        val cursor = contentResolver.query(
            uri,
            projection,
            null,
            null,
            sortOrder
        )

        cursor?.use {
            val idColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
            val titleColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
            val artistColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
            val albumColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM)
            val durationColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)
            val dataColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)

            while (it.moveToNext()) {
                val id = it.getLong(idColumn)
                val title = it.getString(titleColumn)
                val artistName = it.getString(artistColumn) ?: "Unknown Artist"
                val albumName = it.getString(albumColumn) ?: "Unknown Album"
                val duration = it.getLong(durationColumn).toString()
                val filePath = it.getString(dataColumn)

                val artist = Artist(name = artistName)
                val album = Album(title = albumName, cover = null)

                val track = Track(
                    id = id,
                    title = title,
                    duration = duration,
                    image_url = "",
                    preview = filePath,
                    artist = artist,
                    album = album
                )
                trackList.add(track)
            }
        }
    }
}