package com.spatialapps.spatialmemoryanchors.domain

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class MemorySchedulingTest {
    @Test fun failed_recall_returns_to_one_day() {
        val result = MemoryScheduling.schedule(ReviewSchedule(repetitions = 3, intervalDays = 15), quality = 2, nowMillis = 100L)
        assertEquals(0, result.repetitions)
        assertEquals(1, result.intervalDays)
    }

    @Test fun successful_recall_follows_initial_ebbinghaus_intervals() {
        val first = MemoryScheduling.schedule(ReviewSchedule(), quality = 5, nowMillis = 100L)
        val second = MemoryScheduling.schedule(first, quality = 5, nowMillis = 100L)
        assertEquals(1, first.intervalDays)
        assertEquals(6, second.intervalDays)
        assertTrue(second.nextReviewAtMillis > first.nextReviewAtMillis)
    }
}
