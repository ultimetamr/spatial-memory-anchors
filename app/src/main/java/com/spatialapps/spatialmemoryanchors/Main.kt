package com.spatialapps.spatialmemoryanchors

import com.pico.spatial.ui.design.PicoTheme
import com.pico.spatial.ui.foundation.dsl.DefaultWindowContainer
import com.pico.spatial.ui.foundation.dsl.Immersion
import com.pico.spatial.ui.foundation.dsl.SpatialAppScope
import com.pico.spatial.ui.foundation.dsl.Stage
import com.spatialapps.spatialmemoryanchors.ui.MemoryAnchorsScreen
import com.spatialapps.spatialmemoryanchors.ui.SpatialAnchorCalibrationStage

fun mainApp(scope: SpatialAppScope) = with(scope) {
    DefaultWindowContainer {
        PicoTheme { MemoryAnchorsScreen() }
    }
    // PICO 6.0 only permits persistent world anchors in a Full Space Stage.
    Stage(id = SpatialAnchorCalibrationStage.ID, immersion = Immersion.Default) {
        PicoTheme { SpatialAnchorCalibrationStage() }
    }
}
