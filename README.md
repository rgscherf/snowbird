# Snowbird

```
Beneath this snowy mantle cold and clean
The unborn grass lies waiting
For its coat to turn to green
The snowbird sings the song he always sings
And speaks to me of flowers
That will bloom again in spring
```
-- Loretta Lynn, [*Snowbird*](https://www.youtube.com/watch?v=TnwWKhSNwdo)

## Overview

Snowbird is a command-line tool for high-level static analysis insights. It's based on the popular open-source PMD tool.

Where PMD is great for low-level static analysis, Snowbird helps you track the overall health of your code base over time. Its main output is a *technical debt ratio*: the percentage of files-with-PMD-violations / total-file-in-code-base. Snowbird is strict: **any** PMD violation pushes that file into the ratio's numerator.

## Usage

Basic use:

`java -jar snowbird.jar`

Snowbird searches for a file named `snowbird_config.edn` in the current working directory. An error will be raised if no config file is found, or if the config file can't be read. An example config file can be found at the root of this repository. Clojure specs for the config file can be found in `src/snowbird/specs.clj`.

The Snowbird config file allows the user to select:
 
- Input Instructions, which are functions used to gather (or retrieve) files to be analyzed and the rules to analyze them with.
- Render Instructions, which are functions that will do specific things with the analysis provided by `bluebird.core`.

## Architecture

```
                               snowbird.input
+-------------------------------------------+
| Input Instructions specify                |
| source file context, e.g.:                |
|                                           |
|   Run analysis as a Git hook              |
|   Checkout branch XXX and analyze it      |
|   Run analysis against a given directory  |
|                                           |
+-------------------------------------------+
   |
   |                            snowbird.core
+--v----------------------------------------+
| Snowbird Core takes file references from  |
| input context & produces Analysis Record: |
|   * Files examined                        |
|   * Rules Applied                         |
|   * Sequence of violations found          |
|   * ... + some other metadata             |
|                                           |
+-------------------------------------------+
   |    |
   |    |
   |  +-v-----------------+
   |  |Analysis Record DB |
   |  |                   |
   |  +-^-----------------+
   |    |  |
   |    |  |                  snowbird.render
+--v-------v--------------------------------+
| Render Instructions specify               |
| what to do with a new Analysis Record     |
|                                           |
|   Send email notification                 |
|   Export analysis to Google Sheet         |
|   Determine whether to reject Git commit  |
|                                           |
+-------------------------------------------+
```


## Data Types

Essentially, an Analysis Record is a map that looks like:

```clojure
{:files-examined [file-names]
 :rules [qualified-rule-names]
 :violations [violation-records]
 :time #inst "1985-04-12T23:20:50.52Z"
 :id "a UUID"}
```
A violation record represents a single PMD violation. A given file may have several violations, even for the same rule. Violation records look like the following:

```clojure 
{:file "file-name.cls"
 :file-path "/canonical/path/to/file.cls"
 :rule "PMDnameForRule"
 :line 99
 :column 10
 :analysis "analysis-id"}
```

You can find specs for Analysis Records and Violation Records in `src/snowbird/specs.clj`. 


## On PMD

Snowbird is based on PMD, so all the regular PMD rules are open to you. You'll see space to specify ruleset XMLs in the Snowbird config file. We've bundled PMD with the Snowbird JAR so you don't have to worry about having it installed on your system.

## License

Copyright © 2019 Robert Scherf

This program and the accompanying materials are made available under the
terms of the Eclipse Public License 2.0 which is available at
http://www.eclipse.org/legal/epl-2.0.

This Source Code may also be made available under the following Secondary
Licenses when the conditions for such availability set forth in the Eclipse
Public License, v. 2.0 are satisfied: GNU General Public License as published by
the Free Software Foundation, either version 2 of the License, or (at your
option) any later version, with the GNU Classpath Exception which is available
at https://www.gnu.org/software/classpath/license.html.
