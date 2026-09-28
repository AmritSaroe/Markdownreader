Fix the compilation error in MarkdownViewerScreen.kt:

1. In `app/src/main/java/com/markdownreader/app/MarkdownViewerScreen.kt`:
   - Replace `import androidx.compose.ui.zIndex` with `import androidx.compose.ui.layout.zIndex`.
   2. Run `./gradlew assembleDebug` to verify that the build succeeds cleanly.
   
