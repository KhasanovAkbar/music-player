package pdp.uz.musicplayer.models

import android.graphics.Bitmap
import java.io.Serializable

class Music:Serializable {
    var id: Long? = null
    var musicArtist: String? = null
    var musicName: String? = null
    var musicImage: Bitmap? = null
    var musicDuration:String? = null
//    var musicsCount:Int? = null

    constructor()

    constructor(
        musicArtist: String?,
        musicName: String?,
        musicImage: Bitmap?,
        musicDuration: String?,
//        musicsCount: Int?
    ) {
        this.musicArtist = musicArtist
        this.musicName = musicName
        this.musicImage = musicImage
        this.musicDuration = musicDuration
//        this.musicsCount = musicsCount
    }

    constructor(
        id: Long?,
        musicArtist: String?,
        musicName: String?,
        musicImage: Bitmap?,
        musicDuration: String?
    ) {
        this.id = id
        this.musicArtist = musicArtist
        this.musicName = musicName
        this.musicImage = musicImage
        this.musicDuration = musicDuration
    }

}