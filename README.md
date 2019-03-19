# Snowbird

```
Beneath this snowy mantle cold and clean
The unborn grass lies waiting
For its coat to turn to green
The snowbird sings the song he always sings
And speaks to me of flowers
That will bloom again in spring
```
-- [Loretta Lynn](https://www.youtube.com/watch?v=TnwWKhSNwdo)

## Overview

Snowbird is a command-line tool for high-level static analysis insights. It's based on the popular open-source PMD tool.


## Usage

Basic use:

`java -jar snowbird.jar`

Snowbird searches for a file named `snowbird_config.edn` in the current working directory. An error will be raised if no config file is found, or if the config file can't be read. An example config file can be found at the root of this repository. Clojure specs for the config file can be found in `src/snowbird/specs.clj`.

The Snowbird config file allows the user to select:
 
- Input Instructions, which are functions used to gather (or retrieve) files to be analyzed and the rules to analyze them with.
- Render Instructions, which are functions that will do specific things with the analysis provided by `snowbird.analysis.core`.


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
+--+----------------------------------------+
   |
   |                        snowbird.analysis
+--v----------------------------------------+
| Snowbird takes file references from input |
| context & produces Analysis Results:      |
|                                           |
|   * Files examined                        |
|   * Rules applied                         |
|   * Sequence of violations found          |
|   * ... + some other metadata             |
|                                           |
+--+----+-----------------------------------+
   |    |
   |    |
   |  +-v-----------------+
   |  |Analysis Result DB |
   |  |                   |
   |  +-^--+--------------+
   |    |  |
   |    |  |                  snowbird.render
+--v----+--v--------------------------------+
| Render Instructions specify               |
| what to do with a new Analysis Result     |
|                                           |
|   Print a technical debt ratio            |
|   Send email notification                 |
|   Export analysis to Google Sheet         |
|   Determine whether to reject Git commit  |
|                                           |
+-------------------------------------------+
```

## Input and Render Instructions

Your config file must contain `:input` and `:render` keys, pointing to Clojure namespaces with appropriate `specify` functions. These namespaces are `require`d at runtime, and their `specify` functions called.

The input `specify` takes the config map as an argument and returns a sequence of file paths to analyze.

The render `specify` takes an Analysis Result as an argument and performs some side effect. 


## Data Types

Essentially, an Analysis Result is a map that looks like:

```clojure
{:analysis-time #inst "1985-04-12T23:20:50.52Z"
 :id #uuid "a UUID"
 :config config-map-used
 :results {:first-file-type 
            {:files-examined [file-names]
             :rules [qualified-rule-names]
             :violations [violation-records]}
           :second-file-type
            {:files-examined [file-names]
             :rules [qualified-rule-names]
             :violations [violation-records]}}}
```
A violation record represents a single PMD or custom rule violation. A given file may have several violations, even for the same rule. Violation records look like the following:

```clojure 
{:file "file-name.cls"
 :file-path "/canonical/path/to/file.cls"
 :rule "UnqualifiedRuleName"
 :line 99
 :column 10
 :analysis-id #uuid "analysis id"}
```

You can find specs for Analysis Results and Violation Records in `src/snowbird/specs/core.clj`. 


## On PMD

Snowbird is based on PMD, so all the regular PMD rules are open to you. You'll see space to specify ruleset XMLs in the Snowbird config file. We've bundled PMD with the Snowbird JAR so you don't have to worry about having it installed on your system.


## License

Copyright © 2019 Robert Scherf

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
