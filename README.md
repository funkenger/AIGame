# Survivors Arena (Kotlin + libGDX)

A no-asset 2D survivors-like project with shared `core` logic and Android/Desktop launchers.

## Run instructions

### Android Studio
1. Open the root folder in Android Studio.
2. Let Gradle sync.
3. Select `android` app run configuration and run on an emulator/device (API 24+).

### Desktop
Run:

```bash
./gradlew :desktop:run
```

## Controls
- Desktop: WASD move, SPACE dash, ESC pause, F1 debug.
- Android: touch/drag left side to steer, tap right side to dash.

## Notes
- All visuals are drawn with `ShapeRenderer` and `BitmapFont()`.
- No external assets are used.

## Self-check
- Gradle plugin: `8.5.2`, Kotlin `1.9.24`, libGDX `1.12.1`.
- Android SDK: compile/target `34`, min `24`.
- Scene2D skin avoidance: no skin JSON or skin atlas used; manual UI rectangles + BitmapFont labels.
- External asset avoidance: no texture/font/sound/shader files loaded from `assets`; project draws primitives only.
- Performance approach: pooled entity lists reuse inactive objects; `SpatialGrid` is a uniform fixed-capacity structure reset with array fill.
