# Snowbird

```
Beneath this snowy mantle cold and clean
The unborn grass lies waiting
For its coat to turn to green
The snowbird sings the song he always sings
And speaks to me of flowers
That will bloom again in spring

-- Loretta Lynn, *Snowbird*
```

Snowbird is a command-line tool for high-level static analysis insights. It's based on the popular open-source PMD tool.

Where PMD is great for low-level static analysis, Snowbird helps you track the overall health of your code base over time. Its main output is a *technical debt ratio*: the percentage of files-with-PMD-violations / total-file-in-code-base. Snowbird is strict: **any** PMD violation pushes that file into the ratio's numerator.

## Usage

Basic use:

`java -jar snowbird.jar`

Snowbird searches for a file named `snowbird_config.edn` in the current working directory. An error will be raised if no config file is found, or if the config file can't be read. An example config file can be found at the root of this repository. Clojure specs for the config file can be found in `src/snowbird/specs.clj`.

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
