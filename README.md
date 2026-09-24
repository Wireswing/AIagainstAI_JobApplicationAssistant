# 51job AI 面试助手（@yujun.aiassist）

给前程无忧 51job Android App 用的悬浮窗助手：**当你投递简历后触发 AI 面试问答时，实时抓取问题文本，结合你的简历，调用你自己配置的 LLM 生成回答草稿**，悬浮展示供你参考。

## 功能

- 使用无障碍服务监听 51job App（包名 `com.job51`）的文本变化，识别 AI 面试问题
- 基于你内置的简历数据（`CvData.kt`）与当前问题拼接，生成针对性的中文回答
- 悬浮窗展示问题与答案，点击可收起
- 内置配置页：手动填写你可用的 LLM API（OpenAI 兼容接口，例如 DeepSeek、通义、OpenRouter、Groq 等，填自己的 key 和 endpoint）
- 完全离线执行（除了调用你配置的 LLM API），不联网发送简历到第三方

## 使用

1. 安装 APK→打开"51job面试助手"
2. 按引导开启两项安卓特殊权限：**无障碍服务**（本应用）、**显示悬浮窗**
3. 进入"LLM 设置"：填入 API Key（必）、Base URL（可选，默认 DeepSeek）、模型（可选）
4. 点"测试生成"验证接口是否通
5. 在前程无忧 App 正常投递简历，触发 AI 面试/问答时答案会自动出现在悬浮窗上

## 编译

### APK（推荐：云端编译）
本仓库带 GitHub Actions workflow，push 后自动编译，在 Actions 页面的 Artifacts 里下载 APK。一次编译大约 3–5 分钟。

### 本地编译（需要 Android Studio 或 SDK）
```bash
cd android
export ANDROID_HOME=your_sdk_path
export JAVA_HOME=your_jdk17
./gradlew :app:assembleDebug
```

输出：`android/app/build/outputs/apk/debug/app-debug.apk`

## 数据结构
- `CvData.kt`：内置简历数据（可自行替换为你自己的）
- `AppPrefs`：本地存储 API Key 等设置
- `LlmClient`：OpenAI-compatible chat API 调用
- `AssistService`：无障碍监听+问题检测+悬浮显示

## 注意
- 51job/前程无忧、各企业品牌归各自所有，本应用仅为求职辅助工具
- 请勿在任何页面输入你 51job 的账号密码（App 从不要求也无此功能）
- 回答为草稿，请最终由你确认后使用
