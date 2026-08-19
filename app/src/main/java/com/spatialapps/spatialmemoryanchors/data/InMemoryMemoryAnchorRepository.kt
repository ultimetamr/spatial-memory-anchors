package com.spatialapps.spatialmemoryanchors.data

import com.spatialapps.spatialmemoryanchors.domain.MemoryAnchor
import com.spatialapps.spatialmemoryanchors.domain.MemoryCard
import com.spatialapps.spatialmemoryanchors.domain.SpatialPose
import com.spatialapps.spatialmemoryanchors.domain.SubjectCategory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class InMemoryMemoryAnchorRepository : MemoryAnchorRepository {
    private val state = MutableStateFlow(sampleAnchors())
    override val anchors: StateFlow<List<MemoryAnchor>> = state

    override fun upsert(anchor: MemoryAnchor) {
        state.value = state.value.filterNot { it.id == anchor.id } + anchor
    }

    override fun delete(anchorId: String) {
        state.value = state.value.filterNot { it.id == anchorId }
    }

    private fun sampleAnchors() = listOf(
        MemoryAnchor(
            category = SubjectCategory.ENGLISH,
            card = MemoryCard("serendipity", "意外发现美好事物的能力", "A happy serendipity changed our day.", mnemonic = "serene + dip：平静地潜入惊喜"),
            fallbackPose = SpatialPose(-0.55f, 1.35f, -1.2f),
        ),
        MemoryAnchor(
            category = SubjectCategory.SCIENCE,
            card = MemoryCard("熵", "系统无序程度的度量", "孤立系统的熵不会自发减少。", mnemonic = "能量趋向分散"),
            fallbackPose = SpatialPose(0.55f, 1.45f, -1.5f),
        ),
        MemoryAnchor(
            category = SubjectCategory.HISTORY,
            card = MemoryCard("文艺复兴", "14 至 16 世纪的欧洲思想与艺术运动", "强调人的价值与理性。"),
            fallbackPose = SpatialPose(0.1f, 1.0f, -1.0f),
        ),
    )
}
