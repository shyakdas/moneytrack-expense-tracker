package com.moneytrack.security.domain.usecase

internal fun String.sha256(): String {
    val bytes = java.security.MessageDigest
        .getInstance("SHA-256")
        .digest(toByteArray())
    return bytes.joinToString(separator = "") { byte ->
        "%02x".format(byte)
    }
}
