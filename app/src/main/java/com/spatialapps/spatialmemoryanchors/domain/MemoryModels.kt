package com.spatialapps.spatialmemoryanchors.domain

import java.util.UUID

enum class SubjectCategory(val label: String) {
    ENGLISH("英语"), MATH("数学"), HISTORY("历史"), SCIENCE("科学"), CUSTOM("自定义")
}

enum class MasteryLevel(val label: String) {
    NEW("未学"), LEARNING("学习中"), MASTERED("已掌握")
}

enum class StudyMode(val label: String) {
    BROWSE("浏览"), REVIEW("复习"), QUIZ("测验")
}

data class SpatialPose(
    val x: Float,
    val y: Float,
    val z: Float,
)

data class MemoryCard(
    val title: String,
    val definition: String,
    val example: String = "",
    val imageUri: String? = null,
    val mnemonic: String = "",
)

data class ReviewSchedule(
    val repetitions: Int = 0,
    val intervalDays: Int = 0,
    val easeFactor: Double = 2.5,
    val nextReviewAtMillis: Long = 0L,
)

data class MemoryAnchor(
    val id: String = UUID.randomUUID().toString(),
    val persistentAnchorId: String? = null,
    val category: SubjectCategory,
    val card: MemoryCard,
    val fallbackPose: SpatialPose,
    val mastery: MasteryLevel = MasteryLevel.NEW,
    val schedule: ReviewSchedule = ReviewSchedule(),
    val createdAtMillis: Long = System.currentTimeMillis(),
)

data class LearningStatistics(
    val totalAnchors: Int,
    val dueForReview: Int,
    val mastered: Int,
    val reviewedToday: Int,
)
