#! /usr/bin/env bash

set -x

OPENFISCA_CORE_DIR=`python -c "import pkg_resources; print pkg_resources.get_distribution('OpenFisca-Core').location"`
cd "$OPENFISCA_CORE_DIR"
git branch
git checkout ""$TRAVIS_BRANCH"
