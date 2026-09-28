# Bug Fix: Resolve Table Overflow and Clipped Right Border in Markwon

In the Markdown reader, wide tables (such as 10-column answer keys) are overflowing past the right edge of the screen and getting clipped.

---

### Tasks:
1. Locate where `TablePlugin` is configured in the Markwon setup.
2. Customize the `TableTheme` inside `TablePlugin.create()`:
   - Set `.tableCellPadding(4.dpToPx())` (or 3-4dp) to drastically reduce the default horizontal padding inside each cell.
      - Set `.tableBorderWidth(1.dpToPx())` with a subtle outline color.
      3. Check `MarkdownViewerScreen.kt` / layout host:
         - Ensure horizontal padding on the document view is balanced (`16dp` start and end) so the layout does not bias content toward the right edge.
         4. Verify that 10-column tables fit cleanly across standard mobile viewports without clipping the last column's right border.
         
