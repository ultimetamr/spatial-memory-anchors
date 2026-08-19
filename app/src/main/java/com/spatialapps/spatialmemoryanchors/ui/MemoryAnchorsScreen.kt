package com.spatialapps.spatialmemoryanchors.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.pico.spatial.ui.design.Button
import com.pico.spatial.ui.design.PicoTheme
import com.pico.spatial.ui.design.Text
import com.pico.spatial.ui.foundation.material.backgroundMaterial
import com.pico.spatial.ui.platform.Material
import com.spatialapps.spatialmemoryanchors.domain.MasteryLevel
import com.spatialapps.spatialmemoryanchors.domain.MemoryAnchor
import com.spatialapps.spatialmemoryanchors.domain.StudyMode
import com.spatialapps.spatialmemoryanchors.domain.SubjectCategory
import com.spatialapps.spatialmemoryanchors.spatial.AnchorScene
import com.pico.spatial.ui.platform.containers.openStage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun MemoryAnchorsScreen(viewModel: MemoryAnchorsViewModel = viewModel()) {
    val context = LocalContext.current
    val state by viewModel.state.collectAsState()
    val anchors = viewModel.filteredAnchors()
    val selected = viewModel.selected()
    val stats = viewModel.statistics()
    Box(Modifier.fillMaxSize().padding(20.dp)) {
        AnchorScene(anchors = anchors, onAnchorFocused = { viewModel.open(it, "凝视") })
        Column(Modifier.fillMaxSize()) {
            Text("空间单词记忆锚点", style = PicoTheme.typography.titleLarge)
            Text(state.status, style = PicoTheme.typography.bodyLarge)
            Spacer(Modifier.height(12.dp))
            ModeBar(state.mode, viewModel::selectMode)
            Spacer(Modifier.height(12.dp))
            Row(Modifier.fillMaxSize(), horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                AnchorList(
                    anchors = anchors,
                    selectedCategory = state.selectedCategory,
                    onSelectCategory = viewModel::selectCategory,
                    onOpen = { viewModel.open(it) },
                    modifier = Modifier.weight(0.43f),
                )
                LearningSummary(stats.totalAnchors, stats.dueForReview, stats.mastered, modifier = Modifier.weight(0.27f))
                CalibrationPanel(onCalibrate = { CoroutineScope(Dispatchers.Default).launch { context.openStage(SpatialAnchorCalibrationStage.ID) } }, modifier = Modifier.weight(0.30f))
            }
        }
        AnimatedVisibility(
            visible = selected != null,
            enter = fadeIn(tween(300)) + scaleIn(tween(300), initialScale = 0.82f),
            exit = fadeOut(tween(160)),
            modifier = Modifier.align(Alignment.Center),
        ) {
            selected?.let { MemoryCardPanel(it, state.mode, state.showingBack, viewModel::flip, viewModel::markReviewed, viewModel::closeCard) }
        }
    }
}

@Composable
private fun ModeBar(selected: StudyMode, onSelect: (StudyMode) -> Unit) = Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
    StudyMode.entries.forEach { mode ->
        Button(onClick = { onSelect(mode) }) { Text(if (mode == selected) "✓ ${mode.label}模式" else "${mode.label}模式") }
    }
}

@Composable
private fun AnchorList(
    anchors: List<MemoryAnchor>, selectedCategory: SubjectCategory?, onSelectCategory: (SubjectCategory?) -> Unit,
    onOpen: (String) -> Unit, modifier: Modifier,
) = Column(modifier.clip(RoundedCornerShape(16.dp)).backgroundMaterial(true, Material.Regular).padding(14.dp)) {
    Text("锚点列表", style = PicoTheme.typography.titleLarge)
    Row(horizontalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.padding(vertical = 8.dp)) {
        Button(onClick = { onSelectCategory(null) }) { Text(if (selectedCategory == null) "全部 ✓" else "全部") }
        SubjectCategory.entries.take(4).forEach { category -> Button(onClick = { onSelectCategory(category) }) { Text(category.label) } }
    }
    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        items(anchors, key = { it.id }) { anchor ->
            Column(
                Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).clickable { onOpen(anchor.id) }.padding(10.dp),
            ) {
                Text("${categoryDot(anchor.category)}  ${anchor.card.title}", style = PicoTheme.typography.bodyLarge)
                Text("${anchor.category.label} · ${anchor.mastery.label} · ${if (anchor.schedule.nextReviewAtMillis == 0L) "待学习" else "已安排复习"}", style = PicoTheme.typography.bodyLarge)
            }
        }
    }
}

@Composable
private fun LearningSummary(total: Int, due: Int, mastered: Int, modifier: Modifier) = Column(
    modifier.clip(RoundedCornerShape(16.dp)).backgroundMaterial(true, Material.Regular).padding(14.dp),
    verticalArrangement = Arrangement.spacedBy(8.dp),
) {
    Text("学习统计", style = PicoTheme.typography.titleLarge)
    Text("已创建  $total 个空间锚点", style = PicoTheme.typography.bodyLarge)
    Text("今日待复习  $due", style = PicoTheme.typography.bodyLarge)
    Text("已掌握  $mastered", style = PicoTheme.typography.bodyLarge)
    Spacer(Modifier.height(8.dp))
    Text("复习节奏", style = PicoTheme.typography.bodyLarge)
    Text("1 天 → 6 天 → 动态间隔", style = PicoTheme.typography.bodyLarge)
    Text("20 个以内使用完整球形标记；其余仅保留轻量列表索引。", style = PicoTheme.typography.bodyLarge)
}

@Composable
private fun CalibrationPanel(onCalibrate: () -> Unit, modifier: Modifier) = Column(
    modifier.clip(RoundedCornerShape(16.dp)).backgroundMaterial(true, Material.Regular).padding(14.dp),
    verticalArrangement = Arrangement.spacedBy(8.dp),
) {
    Text("空间校准", style = PicoTheme.typography.titleLarge)
    Text("捏合：在当前位置创建新锚点\n凝视 1 秒：高亮并打开卡片\n拖拽：调整卡片局部位置", style = PicoTheme.typography.bodyLarge)
    Text("持久化锚点会在“校准会话”中进入 Full Space 创建；日常复习保持 Shared Space。", style = PicoTheme.typography.bodyLarge)
    Button(onClick = onCalibrate) { Text("开始锚定校准") }
    Button(onClick = {}) { Text("导入 CSV 词表") }
}

@Composable
private fun MemoryCardPanel(
    anchor: MemoryAnchor, mode: StudyMode, showingBack: Boolean, onFlip: () -> Unit,
    onReview: (Int) -> Unit, onClose: () -> Unit,
) = Column(
    Modifier.width(540.dp).clip(RoundedCornerShape(16.dp)).backgroundMaterial(true, Material.Regular).padding(24.dp),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.spacedBy(12.dp),
) {
    Text(anchor.category.label, style = PicoTheme.typography.bodyLarge)
    Text(anchor.card.title, style = PicoTheme.typography.titleLarge)
    if (showingBack || mode != StudyMode.QUIZ) {
        Text(anchor.card.definition, style = PicoTheme.typography.bodyLarge)
        if (anchor.card.example.isNotBlank()) Text(anchor.card.example, style = PicoTheme.typography.bodyLarge)
        if (anchor.card.mnemonic.isNotBlank()) Text("记忆技巧：${anchor.card.mnemonic}", style = PicoTheme.typography.bodyLarge)
        Text(if (anchor.card.imageUri == null) "配图槽位：导入图片后显示" else "配图：${anchor.card.imageUri}", style = PicoTheme.typography.bodyLarge)
    } else {
        Text("先在心中回忆释义，再查看答案。", style = PicoTheme.typography.bodyLarge)
    }
    Button(onClick = onFlip) { Text(if (showingBack) "翻回正面" else "翻转查看答案") }
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Button(onClick = { onReview(2) }) { Text("忘记") }
        Button(onClick = { onReview(3) }) { Text("模糊") }
        Button(onClick = { onReview(5) }) { Text("掌握 ✓") }
    }
    Button(onClick = onClose) { Text("收起") }
}

private fun categoryDot(category: SubjectCategory) = when (category) {
    SubjectCategory.ENGLISH -> "●"; SubjectCategory.MATH -> "◆"; SubjectCategory.HISTORY -> "■"; SubjectCategory.SCIENCE -> "✦"; SubjectCategory.CUSTOM -> "○"
}
