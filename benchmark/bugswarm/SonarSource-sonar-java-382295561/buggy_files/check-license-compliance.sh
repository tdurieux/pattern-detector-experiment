#!/bin/sh
set -euo pipefail

mvn org.codehaus.mojo:license-maven-plugin:aggregate-add-third-party -Dlicense.aggregateMissingLicensesFile=$(pwd)/missing-dep-licenses.properties -DuseMissingFile
