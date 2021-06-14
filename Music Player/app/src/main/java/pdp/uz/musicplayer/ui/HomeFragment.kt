package pdp.uz.musicplayer.ui

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import pdp.uz.musicplayer.BuildConfig
import pdp.uz.musicplayer.R
import pdp.uz.musicplayer.adapters.RvAdapter
import pdp.uz.musicplayer.databinding.FragmentHomeBinding
import pdp.uz.musicplayer.models.Music
import pdp.uz.musicplayer.models.MusicId


class HomeFragment : Fragment() {
    lateinit var rvAdapter: RvAdapter
    lateinit var binding: FragmentHomeBinding

    lateinit var musics: ArrayList<Music>
    var position = 0
    lateinit var musicsId: ArrayList<MusicId>
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(layoutInflater)
        permission()
        return binding.root
    }

    private fun permission() {
        Dexter.withContext(requireContext())
            .withPermissions(
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                    report?.let {
                        if (report.areAllPermissionsGranted()) {
                            getMusic()
                            rvAdapter = RvAdapter(object : RvAdapter.OnMyItemClick {
                                override fun onItem(music: Music, position: Int) {
                                    val bundle = Bundle()
                                    bundle.putInt("size", musics.size)
                                    bundle.putInt("position", position)
                                    bundle.putSerializable("musicsId", musicsId)
                                    findNavController().navigate(R.id.mediaPlayerFragment, bundle)
                                }
                            })
                            rvAdapter.musicData = musics
                            binding.rv.adapter = rvAdapter
                        } else if (report.isAnyPermissionPermanentlyDenied) {
                            val intent =
                                Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                            val uri =
                                Uri.fromParts("package", BuildConfig.APPLICATION_ID, null)
                            intent.data = uri
                            startActivityForResult(intent, 0)
                        } else {
                            AlertDialog.Builder(requireContext())
                                .setMessage("Permission must be granted get musics from external storage")
                                .setPositiveButton("yes") { dialog, which ->
                                    permission()
                                }
                                .setNegativeButton("no") { dialog, which ->

                                }
                                .show()
                        }
                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    permissions: MutableList<PermissionRequest>?,
                    token: PermissionToken?
                ) {
                    token?.continuePermissionRequest()

                }
            })
            .withErrorListener {
                toast(it.name)
            }
            .check()
    }

    private fun toast(t: String) {
        Toast.makeText(requireContext(), t, Toast.LENGTH_SHORT).show()
    }

    private fun getMusic(): ArrayList<Music> {
        musics = ArrayList()
        musicsId = ArrayList()

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
                    musicsId.add(MusicId(cursor.getLong(idColumn), position))
                    position++
                } catch (e: Exception) {
                    println(e)
                }
            } while (cursor.moveToNext())
        }
        return musics
    }
}