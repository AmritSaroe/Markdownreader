# Bug Fix: Markdown Title Cut Off by Camera Notch / Display Cutout

The Markdown H1 title is rendering at y=0 behind the status bar and camera cutout.

---

### Changes in `MarkdownViewerScreen.kt`:
1. Check the root layout of `MarkdownViewerScreen.kt`.
2. Apply `Modifier.statusBarsPadding()` (or `Modifier.safeDrawingPadding()`) to the root container hosting the Markdown view.
3. If using `Scaffold`:
   - Pass `contentWindowInsets = WindowInsets.safeDrawing` to the `Scaffold`.
      - Ensure the content composable uses `Modifier.padding(innerPadding)` so the document starts safely below the physical camera cutout and status bar.
      4. Verify that the top toolbar (if present) sits below the notch, or if reading in full-screen mode, the document text begins with comfortable clearance below the camera hole-punch.
      
