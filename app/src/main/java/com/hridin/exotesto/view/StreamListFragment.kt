package com.hridin.exotesto.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import com.hridin.exotesto.R
import com.hridin.exotesto.repository.PreferencesRepository
import com.hridin.exotesto.repository.StreamRepository
import kotlinx.android.synthetic.main.fragment_stream_list.*

class StreamListFragment : Fragment() {

    private val mStreamRepository by lazy { StreamRepository(PreferencesRepository(context!!.applicationContext), context!!.assets) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_stream_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        rvStreams.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
        rvStreams.adapter = StreamAdapter().apply {
            setData(mStreamRepository.getStreamList())
            onItemClick = {
                fragmentManager?.apply {
                    beginTransaction()
                        .replace(R.id.container, PlayerFragment.newInstance(it))
                        .addToBackStack(null)
                        .commit()
                }
            }
        }
    }
}