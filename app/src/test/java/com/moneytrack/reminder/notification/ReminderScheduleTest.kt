// Copyright (c) 2026 shyakdas

package com.moneytrack.reminder.notification

import org.junit.Assert.assertEquals
import org.junit.Test

class ReminderScheduleTest {

    @Test
    fun onePerDay_mapsToNightReminder() {
        assertEquals(
            listOf(ReminderTime(hour = 21, minute = 0)),
            ReminderSchedule.timesFor(1),
        )
    }

    @Test
    fun threePerDay_mapsToMorningAfternoonNightReminders() {
        assertEquals(
            listOf(
                ReminderTime(hour = 9, minute = 0),
                ReminderTime(hour = 15, minute = 0),
                ReminderTime(hour = 21, minute = 0),
            ),
            ReminderSchedule.timesFor(3),
        )
    }

    @Test
    fun fivePerDay_mapsToFullSchedule() {
        assertEquals(
            listOf(
                ReminderTime(hour = 9, minute = 0),
                ReminderTime(hour = 12, minute = 0),
                ReminderTime(hour = 15, minute = 0),
                ReminderTime(hour = 18, minute = 0),
                ReminderTime(hour = 21, minute = 0),
            ),
            ReminderSchedule.timesFor(5),
        )
    }

    @Test
    fun unsupportedCount_fallsBackToThreePerDay() {
        assertEquals(3, ReminderSchedule.normalize(2))
    }
}
