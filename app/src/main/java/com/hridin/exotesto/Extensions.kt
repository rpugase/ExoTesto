package com.hridin.exotesto

import android.util.Base64
import java.nio.ByteBuffer

inline fun getMethodName() = Throwable().stackTrace[0].methodName

fun ByteArray.toBase64() = Base64.encodeToString(this, Base64.DEFAULT)
fun String?.fromBase64() = Base64.decode(this, Base64.DEFAULT)