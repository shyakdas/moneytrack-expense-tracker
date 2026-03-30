// Copyright (c) 2026 shyakdas

package com.moneytrack.profile.presentation

import android.content.Context
import android.net.Uri
import java.io.IOException

internal const val EXPORT_CSV_MIME_TYPE = "text/csv"

internal fun writeCsvToUri(
    context: Context,
    uri: Uri,
    content: String,
): Boolean {
    return runCatching {
        context.contentResolver.openOutputStream(uri, "wt")?.bufferedWriter()?.use { writer ->
            writer.write(content)
        } ?: throw IOException("Unable to open export destination.")
    }.isSuccess
}
