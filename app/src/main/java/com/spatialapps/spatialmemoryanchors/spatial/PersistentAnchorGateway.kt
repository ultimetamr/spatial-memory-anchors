package com.spatialapps.spatialmemoryanchors.spatial

import com.pico.spatial.core.ecs.AnchorComponent
import com.pico.spatial.core.ecs.Entity
import com.pico.spatial.core.ecs.TransformComponent
import com.pico.spatial.core.ecs.anchor.AnchorTarget
import com.pico.spatial.core.math.EulerAngles
import com.pico.spatial.core.math.Vector3
import com.pico.spatial.sense.world.WorldTrackingManager
import com.pico.spatial.sense.world.WorldTrackingResult
import java.util.UUID

/**
 * Full-Space-only gateway for PICO persistent anchors.
 * UUIDs are the durable bridge from the PICO anchor store to the app's Room repository.
 */
class PersistentAnchorGateway {
    suspend fun createAt(position: Vector3, name: String): Result<UUID> = when (
        val result = WorldTrackingManager.createAnchor(position, EulerAngles(0f, 0f, 0f), name)
    ) {
        is WorldTrackingResult.Success -> result.data?.anchorUUID?.let { Result.success(it) }
            ?: Result.failure(IllegalStateException("PICO returned an empty world anchor"))
        is WorldTrackingResult.Error -> Result.failure(IllegalStateException(result.errorMessage))
    }

    suspend fun loadAll(): Result<List<UUID>> = when (val result = WorldTrackingManager.loadAnchor()) {
        is WorldTrackingResult.Success -> Result.success(result.data?.map { it.anchorUUID }.orEmpty())
        is WorldTrackingResult.Error -> Result.failure(IllegalStateException(result.errorMessage))
    }

    suspend fun remove(uuid: UUID): Result<Unit> = when (val result = WorldTrackingManager.removeAnchor(uuid)) {
        is WorldTrackingResult.Success -> Result.success(Unit)
        is WorldTrackingResult.Error -> Result.failure(IllegalStateException(result.errorMessage))
    }

    fun attach(entity: Entity, uuid: UUID) {
        entity.components[AnchorComponent::class.java] = AnchorComponent(AnchorTarget.createWorldAnchorTarget(uuid))
    }

    fun temporaryPose(entity: Entity, position: Vector3) {
        entity.components[TransformComponent::class.java]?.setPosition(position)
    }
}
