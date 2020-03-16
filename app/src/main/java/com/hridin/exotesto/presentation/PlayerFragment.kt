package com.hridin.exotesto.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.hridin.exotesto.R
import com.hridin.exotesto.data.Stream
import com.hridin.exotesto.player.PlayerManager
import com.hridin.exotesto.repository.PreferencesRepository
import kotlinx.android.synthetic.main.fragment_player.*

class PlayerFragment : Fragment() {

    companion object {
        private const val ARG_STREAM = "ARG_STREAM"

        fun newInstance(stream: Stream) = PlayerFragment().apply {
            arguments = Bundle().apply {
                putParcelable(ARG_STREAM, stream)
            }
        }
    }

    private val mPlayer by lazy { PlayerManager(context!!, PreferencesRepository(context!!.applicationContext)) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_player, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val stream = arguments!!.getParcelable<Stream>(ARG_STREAM)!!

        mPlayer.initExoPlayer(viewPlayer)
        mPlayer.play(stream.manifestUrl, stream.drmInfo)
    }

    override fun onDestroy() {
        mPlayer.release()
        super.onDestroy()
    }
}