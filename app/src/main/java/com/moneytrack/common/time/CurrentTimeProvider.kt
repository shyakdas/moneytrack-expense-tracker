// Copyright (c) 2026 shyakdas

package com.moneytrack.common.time

import javax.inject.Inject

class CurrentTimeProvider @Inject constructor() {
    fun now(): Long = System.currentTimeMillis()
}
