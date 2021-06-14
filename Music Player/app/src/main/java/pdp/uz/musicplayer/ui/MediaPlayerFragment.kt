package pdp.uz.musicplayer.ui

import android.annotation.SuppressLint
import android.content.ContentUris
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import pdp.uz.musicplayer.R
import pdp.uz.musicplayer.databinding.FragmentMediaPlayerBinding
import pdp.uz.musicplayer.models.Music
import pdp.uz.musicplayer.models.MusicId
import java.util.concurrent.TimeUnit


class MediaPlayerFragment : Fragment(), MediaPlayer.OnPreparedListener {
    var mediaPlayer: MediaPlayer? = null
    lateinit var binding: FragmentMediaPlayerBinding
    lateinit var handler: Handler

    //froward button uchun user tomonidan seekTo funksiyasini tekshiradi
    var fromUser1 = false
    private lateinit var musics: ArrayList<Music>
    private var position = 0
    private var size = 0
    var musicPosition = 0
    private var forwardTime = 30000
    private var backwardTime = 30000
    var hourString = ""
    var minuteString = ""
    var secondString = ""
    private var contentUri: Uri? = null
    private var musicId = ArrayList<MusicId>()

    @SuppressLint("DefaultLocale")
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMediaPlayerBinding.inflate(layoutInflater)
        getMusic()
        position = arguments?.getInt("position")!!
        musicId = arguments?.getSerializable("musicsId") as ArrayList<MusicId>
        size = arguments?.getInt("size")!!

        handler = Handler(requireActivity().mainLooper)
        musicPlayer()

        binding.pauseBtn.setOnClickListener {
            if (mediaPlayer?.isPlaying!!) {
                binding.pauseBtn.setImageResource(R.drawable.ic_play)
                mediaPlayer!!.pause()
            } else if (!mediaPlayer?.isPlaying!!) {
                mediaPlayer!!.start()
                binding.pauseBtn.setImageResource(R.drawable.ic_pause)

            }
        }
        info()


        binding.backwardBtn.setOnClickListener {
            if (musicPosition - backwardTime > 0) {
                mediaPlayer?.seekTo(mediaPlayer!!.currentPosition.minus(backwardTime))
                mediaPlayer!!.start()
                binding.pauseBtn.setImageResource(R.drawable.ic_pause)
            } else {
                mediaPlayer?.seekTo(0)
            }
        }

        binding.forwardBtn.setOnClickListener {
            if (musicPosition + forwardTime <= musics[position].musicDuration!!.toInt()) {
                mediaPlayer?.seekTo(mediaPlayer!!.currentPosition.plus(forwardTime))
            } else {
                mediaPlayer?.seekTo(musics[position].musicDuration!!.toInt())
                mediaPlayer!!.pause()
                binding.pauseBtn.setImageResource(R.drawable.ic_play)
                fromUser1 = true
            }
        }

        binding.nextBtn.setOnClickListener {
            if (position == size - 1) {
                if (mediaPlayer != null && mediaPlayer!!.isPlaying) {
                    mediaPlayer!!.stop()
                }
                fromUser1 = false
                position = 0
                info()
                musicPlayer()
            } else {
                if (mediaPlayer != null && mediaPlayer!!.isPlaying) {
                    mediaPlayer!!.stop()
                }
                fromUser1 = false
                position += 1
                info()
                musicPlayer()
            }
        }

        binding.previousBtn.setOnClickListener {
            if (position == 0) {
                if (mediaPlayer != null && mediaPlayer!!.isPlaying) {
                    mediaPlayer!!.stop()
                }
                fromUser1 = false
                position = size - 1
                info()
                musicPlayer()
            } else {
                if (mediaPlayer != null && mediaPlayer!!.isPlaying) {
                    mediaPlayer!!.stop()
                }
                fromUser1 = false
                position -= 1
                info()
                musicPlayer()

            }
        }
        binding.seekbar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    mediaPlayer!!.seekTo(progress)
                    seekBar?.progress = progress
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }

        })


        return binding.root
    }

    private fun musicPlayer() {
        contentUri =
            ContentUris.withAppendedId(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                musicId[position].id
            )
        mediaPlayer = MediaPlayer()
        mediaPlayer!!.setDataSource(requireContext(), contentUri!!)

        mediaPlayer!!.setOnPreparedListener(this)
        binding.seekbar.max = musics[position].musicDuration!!.toInt()
        handler.postDelayed(runnable, 100)
        musicPosition = mediaPlayer!!.currentPosition
        mediaPlayer!!.prepareAsync()
        mediaPlayer!!.start()
        updateSeekbar()

    }

    private fun updateSeekbar() {
        try {
            if (mediaPlayer != null) {
                val currentPosition = mediaPlayer!!.currentPosition
                binding.seekbar.progress = currentPosition
                handler.postDelayed(runnable, 100)


            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun info() {
        binding.count.text = "${position + 1}/$size"
        binding.musicName.text = musics[position].musicName
        binding.musicArtist.text = musics[position].musicArtist
        binding.musicImage.setImageResource(R.drawable.image)

        val seconds: Int =
            TimeUnit.MILLISECONDS.toSeconds(musics[position].musicDuration!!.toLong()).toInt() % 60
        val minutes: Int =
            TimeUnit.MILLISECONDS.toSeconds(musics[position].musicDuration!!.toLong())
                .toInt() / 60 % 60
        val hours: Int = TimeUnit.MILLISECONDS.toSeconds(musics[position].musicDuration!!.toLong())
            .toInt() / 60 / 60
        if (hours > 0) {
            hourString = "$hours :"
        }

        val format = String.format(
            "%02d:%02d",
            TimeUnit.MILLISECONDS.toMinutes(musicPosition.toLong()),
            TimeUnit.MILLISECONDS.toSeconds(musicPosition.toLong()) -
                    TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(musicPosition.toLong()))
        )
        secondString = if (seconds < 10) "0$seconds" else "$seconds"
        minuteString = if (minutes < 10) "0$minutes" else "$minutes"
        binding.duration.text =
            "$format/ $hourString$minuteString:$secondString"
    }

    private var runnable = object : Runnable {
        @SuppressLint("SetTextI18n")
        override fun run() {
            if (mediaPlayer != null) {
                binding.seekbar.progress = mediaPlayer!!.currentPosition
                handler.postDelayed(this, 100)

                musicPosition = mediaPlayer!!.currentPosition
                val format = String.format(
                    "%02d:%02d",
                    TimeUnit.MILLISECONDS.toMinutes(musicPosition.toLong()),
                    TimeUnit.MILLISECONDS.toSeconds(musicPosition.toLong()) -
                            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(musicPosition.toLong()))
                )

                if (format == "$hourString$minuteString:$secondString" && !fromUser1) {
                    Thread.sleep(200)
                    if (position == size - 1) {
                        if (mediaPlayer != null && mediaPlayer!!.isPlaying) {
                            mediaPlayer!!.stop()
                        }
                        fromUser1 = false
                        position = 0
                        info()
                        musicPlayer()
                    } else {
                        if (mediaPlayer != null && mediaPlayer!!.isPlaying) {
                            mediaPlayer!!.stop()
                        }
                        fromUser1 = false
                        position += 1
                        info()
                        musicPlayer()
                    }
                }

                binding.duration.text =
                    "$format / $hourString$minuteString:$secondString"
            }
        }
    }

    private fun releaseMP() {
        if (mediaPlayer != null) {
            try {
                mediaPlayer?.release()
                mediaPlayer = null
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun onPrepared(mp: MediaPlayer?) {
        mp?.start()
    }

    override fun onDestroy() {
        super.onDestroy()
        releaseMP()
    }

    private fun getMusic(): ArrayList<Music> {
        musics = ArrayList()

        val musicUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val cursor = activity?.contentResolver?.query(
            musicUri,
            null,
            null,
            null,
            null
        )

        if (cursor != null && cursor.moveToFirst()) {
            val titleColumn: Int = cursor.getColumnIndex(MediaStore.Audio.Media.TITLE)
            val idColumn: Int = cursor.getColumnIndex(MediaStore.Audio.Media._ID)
            val artistColumn: Int = cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST)
            val durationColumn: Int = cursor.getColumnIndex(MediaStore.Audio.Media.DURATION)


            do {
                try {
                    val music = Music()
                    music.id = cursor.getLong(idColumn)
                    music.musicName = cursor.getString(titleColumn)
                    music.musicArtist = cursor.getString(artistColumn)
                    music.musicDuration = cursor.getString(durationColumn)
                    musics.add(music)
                    position++
                } catch (e: Exception) {
                    println(e)
                }
            } while (cursor.moveToNext())
        }
        return musics
    }
}