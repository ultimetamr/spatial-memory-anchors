package com.spatialapps.spatialmemoryanchors.spatial

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import com.pico.spatial.core.ecs.CollisionComponent
import com.pico.spatial.core.ecs.Entity
import com.pico.spatial.core.ecs.HoverEffectComponent
import com.pico.spatial.core.ecs.InteractableComponent
import com.pico.spatial.core.ecs.ModelEntity
import com.pico.spatial.core.ecs.TransformComponent
import com.pico.spatial.core.ecs.resource.BlendingMode
import com.pico.spatial.core.ecs.resource.MeshResource
import com.pico.spatial.core.ecs.resource.PhysicallyBasedMaterial
import com.pico.spatial.core.ecs.resource.PhysicsMaterialResource
import com.pico.spatial.core.ecs.resource.ShapeResource
import com.pico.spatial.core.math.Color4
import com.pico.spatial.core.math.Vector3
import com.pico.spatial.ui.foundation.content.SpatialView
import com.pico.spatial.ui.foundation.gesture.TargetEntity
import com.pico.spatial.ui.foundation.gesture.detectSpatialTapGesture
import com.spatialapps.spatialmemoryanchors.domain.MasteryLevel
import com.spatialapps.spatialmemoryanchors.domain.MemoryAnchor
import com.spatialapps.spatialmemoryanchors.domain.SubjectCategory

/** ECS-owned marker entities; limited to the closest 20 to keep the marker layer lightweight. */
@Composable
fun AnchorScene(anchors: List<MemoryAnchor>, onAnchorFocused: (String) -> Unit) {
    val context = LocalContext.current
    val root = remember { Entity() }
    val sceneAnchors = remember(anchors) {
        anchors.take(20).map { anchor -> SceneAnchor(anchor.id, markerEntity(anchor)) }
    }
    DisposableEffect(sceneAnchors) {
        root.getChildren().forEach { it.destroy(true) }
        sceneAnchors.forEach { root.addChild(it.entity) }
        onDispose { root.getChildren().forEach { it.destroy(true) } }
    }
    SpatialView(
        modifier = Modifier.pointerInput(sceneAnchors) {
            detectSpatialTapGesture(
                context = context,
                targetedToEntity = TargetEntity.any { target -> sceneAnchors.any { it.entity == target } },
            ) { tap ->
                sceneAnchors.firstOrNull { it.entity == tap.targetEntity }?.let { onAnchorFocused(it.anchorId) }
            }
        },
        initial = { content, _ -> content.addEntity(root) },
    )
}

private data class SceneAnchor(val anchorId: String, val entity: Entity)

private fun markerEntity(anchor: MemoryAnchor): Entity {
    val mesh = MeshResource.createSphere(0.05f)
    val material = PhysicallyBasedMaterial.create(BlendingMode.TRANSPARENT).apply { setBaseColor(categoryColor(anchor.category)) }
    return ModelEntity(mesh, material).apply {
        components.set(InteractableComponent())
        components.set(HoverEffectComponent())
        components.set(CollisionComponent(listOf(ShapeResource.createSphere(0.09f)), PhysicsMaterialResource()))
        components[TransformComponent::class.java]?.setPosition(Vector3(anchor.fallbackPose.x, anchor.fallbackPose.y, anchor.fallbackPose.z))
        // The ECS name accepts only a constrained identifier format; anchor UUIDs contain hyphens.
        // Keep the UUID in app data and use a stable, SDK-safe name for the scene object.
        setName("memory_anchor_${anchor.id.replace("-", "_")}")
    }
}

private fun categoryColor(category: SubjectCategory): Color4 = Color4.fromLinearHex(
    when (category) { SubjectCategory.ENGLISH -> "4E9CFFFF"; SubjectCategory.MATH -> "52C979FF"; SubjectCategory.HISTORY -> "F49B45FF"; SubjectCategory.SCIENCE -> "A875F5FF"; SubjectCategory.CUSTOM -> "9DA3AEFF" }
)
