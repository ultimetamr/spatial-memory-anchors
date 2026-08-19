# 空间单词记忆锚点

基于 PICO Spatial SDK 的空间学习应用。将单词与知识点绑定到房间中的物理位置，在走近、凝视或从列表选择锚点时弹出记忆卡片，让走动成为轻量化复习。

## 核心能力

- 在 Shared Space 中浏览记忆锚点与卡片。
- 支持单词/知识点、释义、例句、图片和记忆技巧。
- 支持浏览、复习与测验模式，以及分类、CSV 导入和学习统计。
- 依据艾宾浩斯遗忘曲线安排复习时间。
- 使用 PICO OS 6 的 Full Space 校准阶段处理 Persistent Spatial Anchor。

## 技术栈

- Kotlin、Jetpack Compose 与 PICO SpatialUI。
- PICO Spatial SDK 6.0。
- 空间锚点 ECS 场景、`WindowContainer` 与 `WorldTrackingManager`。

## 构建

准备 Java 21、Android SDK 与 PICO Spatial SDK 后，在项目根目录执行：

```powershell
./gradlew.bat :app:testDebugUnitTest :app:assembleDebug
```

Debug APK 输出路径：

```text
app/build/outputs/apk/debug/app-debug.apk
```

## 运行

包名：`com.spatialapps.spatialmemoryanchors`

启动 Activity：`.platform.LaunchActivity`

使用已联机的 PICO 模拟器或真机安装 APK 后启动应用。空间容器中的凝视、捏合和拖拽应通过 PICO 设备侧交互完成；普通 2D 点击自动化无法可靠驱动这些空间手势。

## 验证状态

- 单元测试与 Debug 构建已通过。
- 已在 PICO OS 6.0 模拟器中完成安装、启动与截图验证。
