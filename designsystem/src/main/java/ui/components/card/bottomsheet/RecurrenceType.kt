// Copyright (c) 2026 shyakdas

package ui.components.card.bottomsheet

enum class RecurrenceType {
    DAILY, WEEKLY, MONTHLY, YEARLY
}

enum class EndType {
    DATE, INDEFINITELY
}

data class RecurringState(
    val frequency: RecurrenceType? = null,
    val endType: EndType? = null
)
