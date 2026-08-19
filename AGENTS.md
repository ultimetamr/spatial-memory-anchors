# Spatial Memory Anchors project guidance

`SpatialMemoryAnchors` is a PICO OS 6 learning app: it presents lightweight word/knowledge-card markers in a Shared Space volumetric WindowContainer and opens a short Full Space calibration Stage only for real persistent spatial-anchor operations.

- `domain/`: memory card entities, categories, mastery, and Ebbinghaus-style scheduling.
- `data/`: repository and CSV import boundary. Replace `InMemoryMemoryAnchorRepository` with Room DAO/storage without changing UI or spatial code.
- `ui/`: unidirectional screen state, card modes, statistics, CSV/action surfaces, and the Full Space calibration Stage.
- `spatial/`: ECS marker entities plus the `PersistentAnchorGateway` around `WorldTrackingManager`.
- `platform/`: Spatial Application and launcher activity.

PICO 6.0 constraint: `WorldTrackingManager` persistent anchors are Full Space/Stage only. Store each returned anchor UUID next to the card in Room, restore them with `loadAnchor()`, and update card-marker transforms from anchor update events. Do not imply that persistent anchors run in the Shared Space volume.

All 2D UI uses SpatialUI under `PicoTheme`; Material/Material3 is forbidden. Keep the DefaultWindowContainer's manifest `materialbackground="1"` and do not paint an opaque root background. Use ECS for marker transforms and sensor-frequency work, `HandInput` / `ControllerInput` boundaries for equivalent actions, and `ScreenshotExporter` for evidence.

Build with Java 21 selected:

`..\\gradlew.bat -p . :app:testDebugUnitTest :app:assembleDebug`
