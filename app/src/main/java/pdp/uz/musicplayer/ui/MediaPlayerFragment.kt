package pdp.uz.musicplayer.ui

import android.content.ContentUris
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import pdp.uz.musicplayer.databinding.FragmentMediaPlayerBinding
import pdp.uz.musicplayer.models.Music

class MediaPlayerFragment : Fragment(), MediaPlayer.OnPreparedListener {
    var mediaPlayer: MediaPlayer? = null
    lateinit var binding: FragmentMediaPlayerBinding
    lateinit var handler: Handler

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMediaPlayerBinding.inflate(layoutInflater)
        val music = arguments?.getSerializable("music") as Music
        val position = arguments?.getInt("position")
        val size = arguments?.getInt("size")
        val contentUri: Uri =
            ContentUris.withAppendedId(
                android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                music.id!!
            )
        handler = Handler(requireActivity().mainLooper)
        binding.pauseBtn.setOnClickListener {

            if (mediaPlayer == null) {
                mediaPlayer = MediaPlayer()
                mediaPlayer!!.setDataSource(requireContext(), contentUri)
                mediaPlayer!!.setOnPreparedListener(this)
                mediaPlayer!!.prepareAsync()
                mediaPlayer!!.start()
                binding.seekbar.max = music.musicDuration!!.toInt()
                handler.postDelayed(runnable, 100)
            } else if (mediaPlayer?.isPlaying!!) {
                mediaPlayer!!.pause()
            } else {
                mediaPlayer!!.start()
            }
        }
        binding.backwardBtn.setOnClickListener {
            mediaPlayer?.seekTo(mediaPlayer!!.currentPosition.minus(3000))
        }

        binding.forwardBtn.setOnClickListener {
            mediaPlayer?.seekTo(mediaPlayer!!.currentPosition.plus(3000))
        }
        binding.count.text = "$position/$size"
        binding.musicName.text = music.musicName
        binding.musicArtist.text = music.musicArtist
        binding.musicImage.setImageBitmap(music.musicImage)

        val i: Int = music.musicDuration?.toInt()!! / 60000
        val j: Double = (music.musicDuration?.toDouble()!! / 60000 - i) * 60
        val musicMinute: String? = if (i < 10) "0${i}" else "$i"
        val musicSecond: String? = if (j.toInt() < 10) "0${j.toInt()}" else "${j.toInt()}"
        binding.duration.text = "00:00 / $musicMinute:${musicSecond}"

        binding.seekbar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    mediaPlayer!!.seekTo(progress)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {

            }

        })

        return binding.root
    }

    private var runnable = object : Runnable {
        override fun run() {
            binding.seekbar.progress = mediaPlayer!!.currentPosition
            handler.postDelayed(this, 100)
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
}