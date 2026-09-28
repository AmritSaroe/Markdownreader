# Objective: Polish Markdown Reader Typography & Visual Styling (Markwon)

Refactor our Markwon configuration, document layout, and window styling to eliminate visual bugs, improve reading ergonomics, and modernize the system UI.

---

### Step 1: Fix Markwon Theming (`MarkwonTheme.Builder`)
Locate where our `Markwon` instance is built and inject an `AbstractMarkwonPlugin` to override default styling:

1. **Remove Duplicate Heading Borders:**
   - Markwon renders a bottom rule beneath `H1`/`H2` by default. Set `.headingBreakHeight(0)` to prevent double lines when a heading is followed by a standard Markdown horizontal break (`---`).
   2. **Subtle Thematic Breaks (`---`):**
      - Set `.thematicBreakHeight(1dp)` and use an outline/divider color with ~12% opacity (e.g., `Color.parseColor("#1F000000")` in light mode or dynamic Material 3 `colorOutlineVariant`).
      3. **Typography Spacing & Scale:**
         - Adjust `.headingTextSizeMultipliers(floatArrayOf(1.5f, 1.3f, 1.15f, 1.0f, 0.9f, 0.8f))` for clear visual hierarchy.
            - Set `.blockMargin(14dp)` (or convert from dp to px) to give comfortable breathing room between questions, paragraphs, and list items.
            4. **Code & Monospace Box Styling:**
               - Adjust `.codeBackgroundColor(...)` and `.codeBlockBackgroundColor(...)` to use a subtle 4–6% background tint instead of flat dark gray.
                  - Adjust inline code text size proportionally so single-character or series blocks blend cleanly with surrounding body text.

                  ---

                  ### Step 2: Container Layout & Typography Padding
                  Inspect the layout housing our Markdown `TextView` / `NestedScrollView`:

                  1. **Horizontal Viewport Padding:**
                     - Apply `paddingHorizontal="22dp"` and `paddingVertical="16dp"` to the scrollable container or `TextView`. Ensure content is not flush against the display boundaries.
                     2. **Line Spacing & Color:**
                        - Set `android:lineSpacingMultiplier="1.35"` on the Markdown `TextView` to improve legibility.
                           - Use high-contrast primary text color (e.g., `#1E1E1E` or `MaterialTheme.colorScheme.onSurface`) rather than pure black `#000000`.

                           ---

                           ### Step 3: Modern Edge-to-Edge & Status Bar Integration
                           Eliminate the solid black system bar block at the top of the reader:

                           1. **Enable Edge-to-Edge:**
                              - In the host `Activity.onCreate()`, invoke `enableEdgeToEdge()`.
                              2. **Status Bar Icon Tint:**
                                 - Configure `WindowCompat.getInsetsController(window, window.decorView).isAppearanceLightStatusBars = true` (or evaluate dynamic dark/light mode state) so status bar icons remain legible against the white surface.
                                 3. **Window Insets:**
                                    - Apply window insets (`WindowInsetsCompat.Type.systemBars()`) as padding to the root container to prevent document titles from rendering behind the camera cutout or system status icons.

                                    ---

                                    ### Step 4: Verification & Acceptance Criteria
                                    - [ ] Headings followed by `---` render exactly one slim horizontal divider.
                                    - [ ] Monospace blocks (e.g., letter series) have subtle contrast and do not clip horizontally.
                                    - [ ] Questions and numbered options have consistent vertical spacing and do not merge into a wall of text.
                                    - [ ] Status bar flows seamlessly into the reading background without black letterboxing.
                                    - [ ] Scrolling remains smooth (60/120Hz) without layout recalculation stutters.
                                    
