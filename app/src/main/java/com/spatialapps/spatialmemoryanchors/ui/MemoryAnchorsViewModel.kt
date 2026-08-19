package com.spatialapps.spatialmemoryanchors.ui

import androidx.lifecycle.ViewModel
import com.spatialapps.spatialmemoryanchors.data.InMemoryMemoryAnchorRepository
import com.spatialapps.spatialmemoryanchors.data.MemoryAnchorRepository
import com.spatialapps.spatialmemoryanchors.domain.LearningStatistics
import com.spatialapps.spatialmemoryanchors.domain.MasteryLevel
import com.spatialapps.spatialmemoryanchors.domain.MemoryAnchor
import com.spatialapps.spatialmemoryanchors.domain.MemoryScheduling
import com.spatialapps.spatialmemoryanchors.domain.StudyMode
import com.spatialapps.spatialmemoryanchors.domain.SubjectCategory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class MemoryAnchorsUiState(
    val anchors: List<MemoryAnchor> = emptyList(),
    val mode: StudyMode = StudyMode.BROWSE,
    val selectedCategory: SubjectCategory? = null,
    val selectedAnchorId: String? = null,
    val showingBack: Boolean = false,
    val editingAnchorId: String? = null,
    val status: String = "共享空间已就绪：靠近、凝视或从列表打开卡片。",
)

class MemoryAnchorsViewModel(
    private val repository: MemoryAnchorRepository = InMemoryMemoryAnchorRepository(),
) : ViewModel() {
    private val _state = MutableStateFlow(MemoryAnchorsUiState(anchors = repository.anchors.value))
    val state: StateFlow<MemoryAnchorsUiState> = _state.asStateFlow()

    fun selectMode(mode: StudyMode) = update { it.copy(mode = mode, selectedAnchorId = null, showingBack = false) }
    fun selectCategory(category: SubjectCategory?) = update { it.copy(selectedCategory = category) }
    fun open(anchorId: String, reason: String = "列表") = update {
        it.copy(selectedAnchorId = anchorId, showingBack = false, status = "已通过${reason}打开记忆卡片")
    }
    fun closeCard() = update { it.copy(selectedAnchorId = null, showingBack = false) }
    fun flip() = update { it.copy(showingBack = !it.showingBack) }
    fun startEdit(anchorId: String) = update { it.copy(editingAnchorId = anchorId) }
    fun cancelEdit() = update { it.copy(editingAnchorId = null) }
    fun save(anchor: MemoryAnchor) {
        repository.upsert(anchor)
        update { it.copy(anchors = repository.anchors.value, editingAnchorId = null, status = "已保存锚点：${anchor.card.title}") }
    }
    fun delete(anchorId: String) {
        repository.delete(anchorId)
        update { it.copy(anchors = repository.anchors.value, selectedAnchorId = null, editingAnchorId = null, status = "锚点已删除") }
    }
    fun markReviewed(quality: Int) {
        val selected = selected() ?: return
        val schedule = MemoryScheduling.schedule(selected.schedule, quality, System.currentTimeMillis())
        val mastery = when { quality >= 4 && schedule.repetitions >= 3 -> MasteryLevel.MASTERED; quality >= 3 -> MasteryLevel.LEARNING; else -> MasteryLevel.NEW }
        save(selected.copy(schedule = schedule, mastery = mastery))
        update { it.copy(status = "复习已记录：下次复习 ${schedule.intervalDays} 天后") }
    }
    fun filteredAnchors(): List<MemoryAnchor> {
        val now = System.currentTimeMillis()
        return state.value.anchors.filter { anchor ->
            (state.value.selectedCategory == null || anchor.category == state.value.selectedCategory) &&
                (state.value.mode != StudyMode.REVIEW || MemoryScheduling.isDue(anchor.schedule, now))
        }
    }
    fun selected(): MemoryAnchor? = state.value.anchors.firstOrNull { it.id == state.value.selectedAnchorId }
    fun statistics(): LearningStatistics {
        val anchors = state.value.anchors
        return LearningStatistics(anchors.size, anchors.count { MemoryScheduling.isDue(it.schedule, System.currentTimeMillis()) }, anchors.count { it.mastery == MasteryLevel.MASTERED }, anchors.sumOf { it.schedule.repetitions })
    }
    private fun update(block: (MemoryAnchorsUiState) -> MemoryAnchorsUiState) { _state.value = block(_state.value) }
}
