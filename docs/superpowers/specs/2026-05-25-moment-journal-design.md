# 随时记 (Moment Journal) — Design Spec

> Date: 2026-05-25 | Status: Approved

## Overview

一款本地 Android 日记应用，核心理念是"随时随地记录当下发生的每一件事"。区别于一日一记的传统日记，支持一天内多次记录，内容包含文字、图片、视频、语音，精确到秒的时间戳。以日历为核心导航，按天汇总浏览。

## Tech Stack

- **Language**: Kotlin
- **UI**: Jetpack Compose + Material 3
- **Architecture**: MVVM (ViewModel + StateFlow)
- **Database**: Room (SQLite)
- **Camera**: CameraX (still + video capture)
- **Audio**: MediaRecorder
- **Image Picker**: PhotoPicker
- **Navigation**: Navigation Compose (single Activity)

## Data Model

```
Record
  id          Long         @PrimaryKey(autoGenerate)
  dateTime    Long         记录发生的时刻 (epoch seconds)
  createdAt   Long
  updatedAt   Long

Block (内容块)
  id          Long         @PrimaryKey(autoGenerate)
  recordId    Long         FK → Record
  type        Enum         TEXT | IMAGE | VIDEO | VOICE
  content     String       文字内容 / 媒体文件路径
  sortOrder   Int          排列顺序

Tag
  id          Long         @PrimaryKey(autoGenerate)
  name        String
  isPreset    Boolean      预设标签不可删除

RecordTag (多对多)
  recordId    Long
  tagId       Long
```

一条 Record 可包含多个 Block，按 sortOrder 排列。用户创作时自由插入/删除/拖拽排序 Block。

## Screens

### 1. 主页 (HomeScreen)
- **上半部分**: 月日历，左右滑切换月份
  - 日期下方小圆点 = 当天有记录
  - 选中日期显示实心圆高亮
- **下半部分**: 该日所有记录按时间倒序排列
  - 卡片左侧色条表示标签颜色
  - 显示时间、内容预览、媒体缩略图、标签
- **FAB**: 右下角 + 按钮，点击进入创作页（默认当前时间戳）
- **空状态**: 选中日期无记录时显示友好提示

### 2. 创作页 (EditorScreen)
- **顶部栏**: 取消 / 日期时间显示 / 提交按钮
- **编辑区**: 自由排版，可任意增删内容块
  - 文字块: 直接编辑文本内容
  - 图片块: 显示缩略图，点击放大
  - 视频块: 显示封面缩略图
  - 语音块: 显示时长，可试听
- **底部工具栏**: [Aa 文字] [图片] [视频] [录音]，固定悬浮
- **内容块操作**: 长按拖拽排序、左滑删除
- **媒体来源**: 图片/视频支持拍摄和相册选择
- **提交**: 点击提交后弹出标签选择弹窗
  - 预设标签排列在前（不可删除）
  - 自定义标签排列在后（可删除）
  - 底部输入框 + 添加按钮创建新标签
  - 支持多选
- **编辑模式**: 从详情页进入时回填已有内容

### 3. 记录详情页 (DetailScreen)
- 只读展示，按 sortOrder 排列所有内容块
- 媒体块可播放（语音/视频）
- 图片可点击放大浏览
- 顶部: 返回 / 日期时间 / 编辑按钮 / 删除按钮
- 删除需确认弹窗

### 4. 标签管理页 (TagManageScreen)
- 预设标签区: 显示所有预设标签（不可删除）
- 自定义标签区: 显示用户创建的标签（可删除，点击 ✕）
- 底部输入框 + 添加按钮

## Navigation

```
HomeScreen
  ├── [点击日期] → 切换时间线
  ├── [点击记录卡片] → DetailScreen
  │     ├── [编辑] → EditorScreen (回填)
  │     └── [删除] → 确认弹窗 → HomeScreen
  ├── [FAB +] → EditorScreen
  │     └── [提交] → 标签选择弹窗 → HomeScreen
  └── [设置/标签入口] → TagManageScreen
```

无底部导航栏。主页即 App 入口。

## Themes

5 种内置主题，用户可在设置中切换。默认可爱风。

| 主题 | 主色调 | 风格 |
|------|--------|------|
| 可爱风 (默认) | 樱花粉 #FF8FA3 | 大圆角、柔粉彩、emoji 图标 |
| 硬汉风 | 待定 | 暗色、棱角分明、粗犷 |
| 阳光风 | 待定 | 暖黄橙、明亮活泼 |
| 高冷风 | 待定 | 极简黑白灰、冷淡 |
| 搞怪风 | 待定 | 撞色、夸张、有趣 |

主题系统通过 Compose Theme/MaterialTheme 实现，切换后即时生效。

## Design Language (默认可爱风)

- **主色**: #FF8FA3 樱花粉
- **背景**: #FFFAFA 暖白
- **卡片**: 白色背景 + 浅彩边框 + 柔阴影
- **圆角**: 14-22px 全局大圆角
- **按钮**: 全圆角胶囊形，渐变背景
- **FAB**: 渐变樱花粉 + 柔和阴影
- **空状态**: emoji 插图 + 友好文案
- **字体**: 系统默认，文字颜色降低对比度
- **标签配色**: 六色柔粉系 (生活#A4C8F0 工作#F9C7B7 旅行#B8E0D2 学习#E8C4E0 运动#FDD9A5 美食#FFD4B2)

## Storage

- 所有数据本地 SQLite 存储
- 媒体文件存于 app-specific storage (`filesDir/media/`)
- 无网络请求，无云同步

## Out of Scope

- 云端同步 / 备份
- 账号系统 / 多用户
- 分享到社交平台
- 数据导出
- 搜索功能 (v1)
- 提醒/通知
