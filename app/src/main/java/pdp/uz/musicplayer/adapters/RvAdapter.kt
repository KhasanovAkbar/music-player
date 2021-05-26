package pdp.uz.musicplayer.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import pdp.uz.musicplayer.R
import pdp.uz.musicplayer.databinding.ItemViewBinding
import pdp.uz.musicplayer.models.Music

class RvAdapter(var onMyItemClick: OnMyItemClick) : RecyclerView.Adapter<RvAdapter.Vh>() {
    var musicData = arrayListOf<Music>()
        set(value) {
            field.clear()
            field = value
            notifyDataSetChanged()
        }

    @Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
    inner class Vh(var itemViewBinding: ItemViewBinding) :
        RecyclerView.ViewHolder(itemViewBinding.root) {
        fun onBind(music: Music, position: Int) {
            itemViewBinding.musicName.text = music.musicName
            itemViewBinding.musicArtist.text = music.musicArtist
//            if (music.musicImage == (null)){
            itemViewBinding.musicImage.setImageResource(R.drawable.ic_launcher_background)
//            }
//            else {
//                itemViewBinding.musicImage.setImageBitmap(music.musicImage)
//
//            }
            itemViewBinding.root.setOnClickListener {
                onMyItemClick.onItem(music, position)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Vh {
        return Vh(ItemViewBinding.inflate(LayoutInflater.from(parent.context), null, false))
    }

    override fun onBindViewHolder(holder: Vh, position: Int) {
        holder.onBind(musicData[position], position)
    }

    override fun getItemCount(): Int = musicData.size

    interface OnMyItemClick {
        fun onItem(music: Music, position: Int)
    }
}