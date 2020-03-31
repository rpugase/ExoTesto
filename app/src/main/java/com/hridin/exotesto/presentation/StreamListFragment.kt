package com.hridin.exotesto.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.accessibility.AccessibilityEvent
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import com.hridin.exotesto.App
import com.hridin.exotesto.R
import com.hridin.exotesto.repository.PreferencesRepository
import com.hridin.exotesto.repository.StreamRepository
import kotlinx.android.synthetic.main.fragment_stream_list.*

class StreamListFragment : Fragment() {

    private val mPreferencesRepository by lazy { PreferencesRepository(context!!.applicationContext) }
    private val mStreamRepository by lazy { StreamRepository(mPreferencesRepository, context!!.assets) }

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
                        .add(R.id.container, PlayerFragment.newInstance(it))
                        .addToBackStack(null)
                        .commit()
                }
            }
        }

        rvStreams.post {
            rvStreams.getChildAt(0)?.apply {
                requestFocus()
                sendAccessibilityEvent(AccessibilityEvent.TYPE_VIEW_FOCUSED)
            }
        }

        ViewCompat.setNestedScrollingEnabled(rvStreams, false)

        btnCleanLicenses.setOnClickListener { App.instance.drmRepository.deleteAll() }
    }
}