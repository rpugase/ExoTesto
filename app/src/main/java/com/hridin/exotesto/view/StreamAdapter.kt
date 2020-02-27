package com.hridin.exotesto.view

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hridin.exotesto.R
import com.hridin.exotesto.data.Stream
import kotlinx.android.synthetic.main.holder_stream.view.*
import java.util.*

class StreamAdapter : RecyclerView.Adapter<StreamAdapter.StreamViewHolder>() {

    private val streamList = mutableListOf<Stream>()
    var onItemClick: ((Stream) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        StreamViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.holder_stream, parent, false))

    override fun getItemCount() = streamList.size

    override fun onBindViewHolder(holder: StreamViewHolder, position: Int) {
        holder.bind(streamList[position])
    }

    fun setData(streamList: Collection<Stream>) {
        this.streamList.clear()
        this.streamList.addAll(streamList)
        notifyDataSetChanged()
    }

    inner class StreamViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(stream: Stream) {
            with(itemView) {
                tvName.text = stream.channelName
                tvManifestUrl.text = stream.manifestUrl
                tvLicenseUrl.text = stream.drmInfo.licenseUrl
                tvLicenseUrl.text = stream.drmInfo.drmSystem.toString().toLowerCase(Locale.US)
                tvToken.text = stream.drmInfo.token

                setOnClickListener { onItemClick?.invoke(stream) }
                setOnFocusChangeListener { _, hasFocus ->
                    setBackgroundColor(if (hasFocus) Color.parseColor("#DADADA") else Color.WHITE)
                }
            }
        }
    }
}