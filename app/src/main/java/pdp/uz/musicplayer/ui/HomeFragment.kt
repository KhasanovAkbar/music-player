package pdp.uz.musicplayer.ui

import android.Manifest
import android.graphics.BitmapFactory
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import pdp.uz.musicplayer.R
import pdp.uz.musicplayer.adapters.RvAdapter
import pdp.uz.musicplayer.databinding.FragmentHomeBinding
import pdp.uz.musicplayer.models.Music
import java.io.File

class HomeFragment : Fragment() {
    lateinit var rvAdapter: RvAdapter
    lateinit var binding: FragmentHomeBinding
//    lateinit var myDatabase: MyDatabase
    lateinit var musics: ArrayList<Music>
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(layoutInflater)
//        myDatabase = MyDatabase(requireContext())
        getMusic()
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
//                            toast("OK")
                            rvAdapter = RvAdapter(object : RvAdapter.OnMyItemClick {
                                override fun onItem(music: Music, position: Int) {
                                    val bundle = Bundle()
                                    bundle.putInt("size", musics.size)
                                    bundle.putInt("position", position + 1)
                                    bundle.putSerializable("music", music)
                                    findNavController().navigate(R.id.mediaPlayerFragment, bundle)
                                }
                            })
                            rvAdapter.musicData = musics
                            binding.rv.adapter = rvAdapter
                        }
                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    permissions: MutableList<PermissionRequest>?,
                    token: PermissionToken?
                ) {
                    // Remember to invoke this method when the custom rationale is closed
                    // or just by default if you don't want to use any custom rationale.
                    token?.continuePermissionRequest()
                }
            })
            .withErrorListener {
                toast(it.name)
            }
            .check()
    }

    fun toast(t: String) {
        Toast.makeText(requireContext(), t, Toast.LENGTH_SHORT).show()
    }

    fun getMusic(): ArrayList<Music> {
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
//            val image: String =
//                cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ARTIST))
//            val file = File(image)
            val path = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Albums.ALBUM))
            val imgFile = File(path)
            val myBitmap = BitmapFactory.decodeFile(imgFile.absolutePath)
            do {
                try {
                    val music = Music()
                    music.id = cursor.getLong(idColumn)
                    music.musicName = cursor.getString(titleColumn)
                    music.musicArtist = cursor.getString(artistColumn)
                    music.musicDuration = cursor.getString(durationColumn)
                    musics.add(music)

                } catch (e: Exception) {
                    println(e)
                }
            } while (cursor.moveToNext())


        }
        return musics
    }
}