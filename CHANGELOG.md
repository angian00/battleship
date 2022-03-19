
# Change Log
The format is based on [Keep a Changelog](http://keepachangelog.com/).

## [0.6] - 2022-03-19

### Added
- splash screen image to LoginScreen
- hostname field to LoginScreen
- player names to CombatScreen
- New Game button to CombatScreen after game is finished
- management of other player disconnection
- saving last login options on client

- deployed Virtual Private Server on the public internet

### Fixed
- logback configuration
- instability after many connect/disconnect on server


## [0.5] - 2022-03-04

### Added
- websocket server and client code
- login ui

### Modified
- Refactored code to split common/ and core-game/ modules


## [0.4] - 2022-02-23

### Added
- Sound effects
- text messages after shots
- disabled state for placement confirm button
- winning condition
- showing sunk enemy ships

### Modified
- Refactored code introducing explicit ShipId and ShipPlacement types


## [0.3] - 2022-02-19
### Added
- combat screen:
  - player board
  - enemy board
  - miss/hit tile sprites
  - half size ship sprites

### Modified
- refactored common code in enemy and player combat board, placement board

### Fixed
- random overlaps in PlacementScreen because of Float finite precision


## [0.2] - 2022-02-18
### Added
- ship placement ui (drag and drop, rotation)
- placement validation
- confirm button


## [0.1] - 2022-02-12

### Added
- libgdx project skeleton generated
- git repository initialized
- test screen:
    - a clickable board with a background image
  
