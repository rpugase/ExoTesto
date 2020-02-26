package com.hridin.exotesto.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.hridin.exotesto.R
import com.hridin.exotesto.player.PlayerManager
import com.hridin.exotesto.repository.PreferencesRepository
import com.hridin.exotesto.repository.StreamRepository
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel

class MainActivity : AppCompatActivity(), CoroutineScope by MainScope() {

    /**
     * TODO 1: Вынести в json инфу про стримы и обрабатывать их на нажатии на клавиатуре (1..9)
     * TODO 2: Сделать UI для модификации: manfiest, token, licenseUrl, drmSystem. Вывод списка снизу
     * TODO 3: Поддержка интента, чтобы запускать по ADB
     * TODO 4: Логгирование ошибок на отдельном экране
     */

    private val mPlayer by lazy { PlayerManager(this, PreferencesRepository(this)) }

    private val mStreamRepository by lazy { StreamRepository(PreferencesRepository(this), applicationContext.assets) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val stream = mStreamRepository.getStreamList().first()

        mPlayer.initExoPlayer(viewPlayer,
            stream.drmInfo.licenseUrl,
            stream.drmInfo.token,
            stream.drmInfo.drmSystem
        )
        mPlayer.play(stream.manifestUrl)
    }

    override fun onDestroy() {
        cancel()
        mPlayer.release()
        super.onDestroy()
    }
}
