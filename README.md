# 📖 Markdown Reader

A simple, lightweight Android app for reading Markdown files — just like a PDF reader, but for `.md` files.

## Features

- **Open .md files** from any file manager — registers as a handler for Markdown files
- **Full Markdown rendering** — headings, bold, italic, code blocks, tables, lists, blockquotes, links, strikethrough, task lists, and more
- **Text selection & copy** — long-press to select and copy text
- **Dark/Light theme** — follows your system theme, with Material You dynamic colors on Android 12+
- **File picker** — built-in file browser to open Markdown files
- **Smooth scrolling** — native rendering (no WebView) for fast, smooth reading

## Screenshots

_Coming soon_

## Tech Stack

| Component | Technology |
|---|---|
| Language | Kotlin |
| UI Framework | Jetpack Compose + Material 3 |
| Markdown Engine | [Markwon](https://github.com/noties/markwon) |
| Min SDK | API 26 (Android 8.0) |
| Target SDK | API 34 (Android 14) |
| Build System | Gradle (Kotlin DSL) |
| CI/CD | GitHub Actions |

## Building

### Via GitHub Actions (recommended)

1. Push this repository to GitHub
2. GitHub Actions will automatically build debug and release APKs
3. Download the APKs from the workflow's **Artifacts** section

### Locally

Requires Android SDK and Java 17.

```bash
# Generate Gradle wrapper (first time only)
gradle wrapper --gradle-version 8.6

# Build debug APK
./gradlew assembleDebug

# Build release APK
./gradlew assembleRelease
```

The APKs will be in `app/build/outputs/apk/`.

## Supported Markdown Features

- [x] Headings (H1–H6)
- [x] Bold, Italic, Bold+Italic
- [x] Ordered and unordered lists
- [x] Nested lists
- [x] Blockquotes
- [x] Code (inline and fenced blocks)
- [x] Links (auto-linked URLs and emails)
- [x] Horizontal rules
- [x] Tables (GFM)
- [x] Strikethrough
- [x] Task lists / checkboxes
- [x] HTML (basic)

## Project Structure

```
MarkdownReader/
├── .github/workflows/build.yml    # GitHub Actions CI
├── app/
│   ├── build.gradle.kts            # App module build config
│   ├── proguard-rules.pro
│   └── src/main/
│       ├── AndroidManifest.xml      # Intent filters for .md files
│       ├── java/com/markdownreader/app/
│       │   ├── MainActivity.kt          # Entry point, file handling
│       │   ├── MarkdownViewerScreen.kt  # Markdown rendering UI
│       │   ├── WelcomeScreen.kt         # Empty state / welcome
│       │   └── ui/theme/               # Material 3 theming
│       └── res/                        # Android resources
├── build.gradle.kts                # Root build config
├── settings.gradle.kts
├── gradle.properties
└── README.md
```

## License

MIT License
