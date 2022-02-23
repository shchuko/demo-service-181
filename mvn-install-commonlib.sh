#!/bin/sh
set -ux

VERSION=${VERSION:-"0.6.0-SNAPSHOT"}
PATH_PREFIX="lib"

mvn install:install-file -Dfile="$PATH_PREFIX/common-lib-$VERSION.jar" -DpomFile="$PATH_PREFIX/common-lib-$VERSION.pom"
