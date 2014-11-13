#!/bin/bash
set -eu
: ${1:? Usage: $0 DESCRIPTION}
DESCRIPTION="$1"
set -x

mvn nexus-staging:deploy-staged-repository \
    --errors \
    -DrepositoryDirectory=staging \
    -DstagingDescription="$DESCRIPTION"

# assumes 'staging/*.properties' under altStagingDirectory
mvn nexus-staging:release \
    --errors \
    -DaltStagingDirectory=. \
    -DstagingDescription="$DESCRIPTION"

git push origin HEAD
git push origin --tags
