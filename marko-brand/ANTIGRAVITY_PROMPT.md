# Marko brand assets: instructions for the coding agent

This folder is the official brand kit for **Marko**, a lightweight Markdown reader.
Use these files as-is. Do NOT redraw, restyle, or "improve" the logo.

## Files
| File | Use |
|---|---|
| `marko-icon.svg` | Master app icon (dark rounded tile + white M). iOS/desktop icon source. |
| `marko-mark.svg` | Glyph only, transparent, uses `currentColor`. Use inside the UI (navbar, splash, empty states, About screen). |
| `marko-maskable.svg` | Android / PWA maskable icon (full-bleed, glyph inside safe zone). |
| `favicon.svg` | Browser tab / small-size icon. Simplified geometry for 16-32px. |
| `png/` | Pre-rendered sizes: 1024, 512, 192, 180, 152, 120, 64, 48, 32, 16, plus `maskable-512.png`. |

## Brand tokens
- Tile / background: `#18181B`
- Mark: `#FFFFFF`
- Tile corner radius: 116 on a 512 grid (about 22.6%)
- The logo has NO text, NO gradients, NO shadows. Keep it flat.

## Construction (for reference only, do not modify)
Canvas 512x512. Two vertical pillars (52x212, r=10) at x=132 and x=328, y=150.
A downward chevron stroke (width 52, round caps/joins) from (184,190) to (256,288) to (328,190).

## Tasks
1. Copy this folder to `assets/brand/` (or the project's assets dir).
2. Set the app icon:
   - Web/PWA: link `favicon.svg` as the favicon; add `png/icon-192.png`, `png/icon-512.png` and `png/maskable-512.png` to `manifest.json` (maskable one with `"purpose": "maskable"`); add `png/icon-180.png` as `apple-touch-icon`.
   - iOS: use `png/icon-1024.png` as the App Store icon source.
   - Android: use `marko-maskable.svg` / `png/maskable-512.png` for the adaptive icon foreground/background.
   - Electron/Tauri: generate platform icons from `png/icon-1024.png`.
3. Use `marko-mark.svg` in the UI wherever the logo appears, colored via CSS `color:`
   (white on dark surfaces, `#18181B` on light surfaces).
4. Set theme color / splash background to `#18181B`.
5. Do not add a text wordmark unless asked. If one is needed later, use the app's UI font in weight 600, lowercase "marko", with clear space equal to the pillar width (52 units) on all sides.

## Minimum sizes
- Full icon: 16px
- Glyph-only in UI: 20px height
- Clear space around the mark: at least one pillar width.
