package com.spatialapps.spatialmemoryanchors.data

import com.spatialapps.spatialmemoryanchors.domain.MemoryAnchor
import com.spatialapps.spatialmemoryanchors.domain.MemoryCard
import com.spatialapps.spatialmemoryanchors.domain.SpatialPose
import com.spatialapps.spatialmemoryanchors.domain.SubjectCategory

/** CSV columns: category,title,definition,example,imageUri,mnemonic. */
object CsvMemoryImport {
    fun parse(csv: String): List<MemoryAnchor> = csv.lineSequence()
        .drop(1)
        .filter { it.isNotBlank() }
        .map { line -> line.split(',').map(String::trim) }
        .filter { it.size >= 3 }
        .mapIndexed { index, fields ->
            MemoryAnchor(
                category = SubjectCategory.entries.firstOrNull { it.name.equals(fields[0], true) }
                    ?: SubjectCategory.CUSTOM,
                card = MemoryCard(
                    title = fields[1], definition = fields[2], example = fields.getOrElse(3) { "" },
                    imageUri = fields.getOrNull(4)?.ifBlank { null }, mnemonic = fields.getOrElse(5) { "" },
                ),
                fallbackPose = SpatialPose((index % 4 - 1.5f) * 0.38f, 1.25f, -1.2f - index / 4 * 0.2f),
            )
        }.toList()
}
