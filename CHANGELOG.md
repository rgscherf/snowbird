# Change Log
All notable changes to this project will be documented in this file. This change log follows the conventions of [keepachangelog.com](http://keepachangelog.com/).

## [0.2.12] - 2019-05.17
### Added
- `snowbird.render.summarize-tech-debt` ns, with options to render debt summary as string and/or print to stdout.
### Changed
- `snowbird.render.serialize-results` now has option (using `:as` config key) to return analysis results as data and/or spit to a file.
### Removed
- `snowbird.render.str-tech-debt` and `snowbird.render.print-tech-debt`

## [0.2.11] - 2019-05-16
### Added
- New init fn, `run-snowbird-from-json`, which takes a local snowbird_config.edn.
### Changed
- `run-snowbird-from-filemap` now defers to `run-snowbird-from-json`.

## [0.2.10] - 2019-05-16
### Added 
- pmd command is now configurable with `:pmd-command` key in `snowbird_config.edn`
