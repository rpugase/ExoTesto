package com.hridin.exotesto

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.hridin.exotesto.view.StreamListFragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope

class MainActivity : AppCompatActivity(), CoroutineScope by MainScope() {

    /**
     * TODO 1: Сделать UI для модификации: manfiest, token, licenseUrl, drmSystem
     * TODO 2: Поддержка интента, чтобы запускать по ADB
     * TODO 3: Логгирование ошибок на отдельном экране
     */

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportFragmentManager.beginTransaction()
            .replace(R.id.container,
                StreamListFragment()
            )
            .commit()
    }
}
