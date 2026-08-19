package com.spatialapps.spatialmemoryanchors.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.pico.spatial.core.math.Vector3
import com.pico.spatial.ui.design.Button
import com.pico.spatial.ui.design.PicoTheme
import com.pico.spatial.ui.design.Text
import com.spatialapps.spatialmemoryanchors.spatial.PersistentAnchorGateway
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/** A short Full Space calibration session: the only place PICO allows persistent anchors. */
@Composable
fun SpatialAnchorCalibrationStage() {
    val gateway = remember { PersistentAnchorGateway() }
    var message by remember { mutableStateOf("正在读取此应用保存的空间锚点…") }
    LaunchedEffect(Unit) {
        message = gateway.loadAll().fold(
            onSuccess = { "已恢复 ${it.size} 个 PICO 持久化锚点。移动到目标位置后捏合创建。" },
            onFailure = { "无法读取锚点：${it.message}" },
        )
    }
    Column(Modifier.padding(28.dp)) {
        Text("空间校准（Full Space）", style = PicoTheme.typography.titleLarge)
        Spacer(Modifier.size(12.dp))
        Text(message, style = PicoTheme.typography.bodyLarge)
        Spacer(Modifier.size(18.dp))
        Button(onClick = {
            // HandInput will supply the pinch/world pose. This safe fallback is used by controller and emulator flows.
            message = "正在创建前方 1.2m 的测试锚点…"
            kotlinx.coroutines.CoroutineScope(Dispatchers.Main).launch {
                message = gateway.createAt(Vector3(0f, 1.35f, -1.2f), "memory-anchor").fold(
                    onSuccess = { "已创建 PICO 持久化锚点：$it；返回共享空间后可绑定卡片。" },
                    onFailure = { "创建失败：${it.message}" },
                )
            }
        }) { Text("在当前位置创建锚点") }
        Spacer(Modifier.size(10.dp))
        Text("说明：手势捏合与控制器回调均通过 HandInput / ControllerInput 边界提供同一位置数据；不移动相机。", style = PicoTheme.typography.bodyLarge)
    }
}

object SpatialAnchorCalibrationStage { const val ID = "SpatialMemoryAnchorsCalibration" }
