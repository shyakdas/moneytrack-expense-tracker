// Copyright (c) 2026 shyakdas

package com.moneytrack.settings.domain.model

enum class AppThemeMode {
    SYSTEM,
    LIGHT,
    DARK,
}

internal fun String?.toAppThemeMode(): AppThemeMode {
    return AppThemeMode.entries.firstOrNull { mode ->
        mode.name == this
    } ?: AppThemeMode.SYSTEM
}
