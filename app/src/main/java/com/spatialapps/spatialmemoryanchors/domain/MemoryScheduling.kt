package com.spatialapps.spatialmemoryanchors.domain

/** SM-2 style schedule: a practical Ebbinghaus-inspired review cadence. */
object MemoryScheduling {
    private const val DAY_MILLIS = 24L * 60L * 60L * 1000L

    fun schedule(previous: ReviewSchedule, quality: Int, nowMillis: Long): ReviewSchedule {
        require(quality in 0..5)
        val ease = (previous.easeFactor + 0.1 - (5 - quality) * (0.08 + (5 - quality) * 0.02))
            .coerceAtLeast(1.3)
        val repetitions = if (quality < 3) 0 else previous.repetitions + 1
        val interval = when {
            quality < 3 -> 1
            repetitions == 1 -> 1
            repetitions == 2 -> 6
            else -> (previous.intervalDays * ease).toInt().coerceAtLeast(previous.intervalDays + 1)
        }
        return ReviewSchedule(
            repetitions = repetitions,
            intervalDays = interval,
            easeFactor = ease,
            nextReviewAtMillis = nowMillis + interval * DAY_MILLIS,
        )
    }

    fun isDue(schedule: ReviewSchedule, nowMillis: Long): Boolean =
        schedule.nextReviewAtMillis == 0L || schedule.nextReviewAtMillis <= nowMillis
}
