package com.entryventures.extensions

import java.util.Base64

fun ByteArray.toBase64(): String = Base64.getEncoder().encodeToString(this)