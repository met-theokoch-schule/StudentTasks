# Struktogramm taskview debug log

## Problem
- In taskview `struktog.html` the function block shows a "+" for adding parameters, but clicking it does nothing.

## Files involved
- `src/main/resources/static/js/struktogramm.js`
- `src/main/resources/templates/taskviews/struktog.html`
- `src/main/resources/static/css/struktogramm.css`

## Findings so far
- The "+" button is created in `renderFunctionBox(...)`.
- Original click handler used a global count: `document.getElementsByClassName('function-elem').length - 1`.
- Likely bug: count should be local to the param div, not global.
- Added `type="button"` to avoid implicit form submit (if inside a form).

## Code changes made
1) Fix count + button type
- In `src/main/resources/static/js/struktogramm.js`:
  - `addParamBtn.type = 'button';`
  - `const countParam = paramDiv.getElementsByClassName('function-elem').length;`

2) Added console logs for debugging (no logs seen so far)
- In `renderFunctionBox(...)`:
  - `mouseover` / `mouseleave` on `functionBoxHeaderDiv`.
  - `pointerdown` / `mousedown` / `click` on `addParamBtn`.
  - `click` on `paramDiv` (logs target className).

## User feedback after logs
- User reports: no console logs appear at all.

## Next steps suggested
- Reload page, open console, move mouse over function header, click +, and report log order.
- If still no logs, verify that the correct JS file is loaded and not overridden by another build artifact (e.g., `target/.../struktogramm.js`).

## Patch locations
- `src/main/resources/static/js/struktogramm.js`
