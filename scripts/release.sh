#!/bin/bash
set -eu
: ${1:? Usage: $0 RELEASE_VERSION}
SCRIPTS=`dirname "$0"`

RELEASE_VERSION="$1"
if [[ ! "$RELEASE_VERSION" =~ ^[0-9]+\.[0-9]+\.[0-9]+$ ]]; then
    echo "Error: RELEASE_VERSION must be in X.Y.Z format, but was $RELEASE_VERSION"
    exit 1
fi

function contains-line() {
    grep --line-regexp --quiet --fixed-strings -e "$1"
}

function demand-file-contains-line() {
    local file="$1"
    local expected="$2"
    cat "$file" | contains-line "$expected" || (echo "Add this line to $file and try again:"; echo "$expected"; exit 1)
}

function assert-file-contains-substring() {
    local file="$1"
    local expected="$2"
    cat "$file" | grep --quiet --fixed-strings -e "$expected" || (echo "Error: file $file did not contain $expected"; exit 1)
}

function set-project-version()
{
    local file="pom.xml"
    local version="$1"
    mvn versions:set \
        -DgenerateBackupPoms=false \
        -DnewVersion="$version" \
        --file "$file"
    assert-file-contains-substring "$file" "<version>$version</version>"
}

function set-documentation-version()
{
    local file="README.md"
    local version="$1"
    sed -i -r -e "s/^(\\s*<version>).+(<\\/version>)\$/\1$version\2/" "$file"
    assert-file-contains-substring "$file" "<version>$version</version>"
}

function next-snapshot-version()
{
    local prefix=`echo $1 | sed -n -r 's/([0-9]+\.[0-9]+\.)[0-9]+/\1/p'`
    local suffix=`echo $1 | sed -n -r 's/[0-9]+\.[0-9]+\.([0-9]+)/\1/p'`
    ((suffix++))
    echo "$prefix$suffix-SNAPSHOT"
}

APP_NAME="BuildTest"
NEXT_VERSION=`next-snapshot-version $RELEASE_VERSION`

demand-file-contains-line README.md "### $APP_NAME $RELEASE_VERSION (`date --iso-8601`)"

set -x

set-project-version "$RELEASE_VERSION"
set-documentation-version "$RELEASE_VERSION"
git add -u
git commit -m "Release $RELEASE_VERSION"
git tag -s -m "$APP_NAME $RELEASE_VERSION" "v$RELEASE_VERSION"

$SCRIPTS/stage.sh "$APP_NAME $RELEASE_VERSION"

set-project-version "$NEXT_VERSION"
git add -u
git commit -m "Prepare for next development iteration"

$SCRIPTS/publish.sh "$APP_NAME $RELEASE_VERSION"
