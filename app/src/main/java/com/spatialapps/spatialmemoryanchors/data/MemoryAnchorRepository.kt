package com.spatialapps.spatialmemoryanchors.data

import com.spatialapps.spatialmemoryanchors.domain.MemoryAnchor
import kotlinx.coroutines.flow.StateFlow

/** Storage boundary. Swap this implementation for Room without changing the UI or spatial layers. */
interface MemoryAnchorRepository {
    val anchors: StateFlow<List<MemoryAnchor>>
    fun upsert(anchor: MemoryAnchor)
    fun delete(anchorId: String)
}
