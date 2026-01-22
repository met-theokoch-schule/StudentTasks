# Codex Overview

## UI Updates
- Added Font Awesome to the Code Mainia Python task view and replaced text action buttons with reload/home icons.

## New H5P Task View
- Added `taskviews/h5p` template with an iframe that loads a URL from JSON provided via the hidden `#description` element.
- Added `h5p-task-view.js` to parse config, set iframe `src`, and listen for `postMessage` xAPI events to log matches.
- Added minimal `h5p-task-view.css` layout styling for the iframe container.
- Registered the new task view in `DataInitializer`.
- Added test config fields in `taskviews/h5p.html` for xAPI `verb.id` matching and a `passScoreScaled` threshold to treat completions as passed (currently logged only).
- Added debug logging for `result.score.scaled` against `passScoreScaled` when a matching xAPI verb is received.
- Limited debug output to xAPI statements that include a `result` field to make score-related logs easier to spot.

## External H5P Standalone Update (to apply on the embedded site)
- Added a `postMessage` bridge for H5P xAPI events to notify the parent frame.
