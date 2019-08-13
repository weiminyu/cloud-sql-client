#!/bin/bash

# Expected env:
#  - SCHEMA_RELEASES, URL prefix to per-environment schema release file
#  - SERVER_RELEASES, URL prefix to per-environment schema release file

SCHEMA_JAR_REPO=https://storage.googleapis.com/oval-cyclist-maven-repo
SCHEMA_RELEASES=https://storage.googleapis.com/oval-cyclist-maven-repo/nomulus_schema_
SERVER_RELEASES=https://storage.googleapis.com/oval-cyclist-maven-repo/nomulus_server_

TEST_SCHEMA_REPO="${TRAVIS_BUILD_DIR}/test-schema-maven-repo"
TEST_SCHEMA_VERSION=travis-test

getDeployedVersion() {
  if [[ $# -ne 1 ]]; then
    echo "getDeployedVersionByEnv requires exactly one parameter: url to file"
    return 1
  fi

  value=$(curl -s "$1" | tr -d '\n')
  echo ${value}

  errorCount=$(echo ${value} | grep "Error" | wc -l)
  [[ ${errorCount} -gt 0 ]] && return 1
}

# Checks if the commit under test consists changes in the given folder(s).
# Must run from git repo's root directory, i.e., ${TRAVIS_BUILD_DIR}.
countChangesInFolders() {
  value=$(git diff --name-only origin/master $@ | wc -l)
  gitStatus=${PIPESTATUS[0]}
  echo ${value}
  return ${gitStatus}
}

testNewAppWithSchemaWithVersion() {
  if [[ $# -ne 1 ]]; then
    echo "testNewSchemaWithAppAtCommit requires exactly one parameter: version of the schema jar"
    return 1
  fi

  echo "Testing app against schema version $1"

  # Run 'test' instead of build.
  # TODO(weiminyu): group tests that need DB in a dedicated suite
  ./gradlew clean :demo_app:test -Pschema_repo=${SCHEMA_JAR_REPO} -Pschema_version=$1
}

testNewAppWithSchema() {
  prodVersion=$(getDeployedVersion "${SCHEMA_RELEASES}production")
  [[ $? -eq 0 ]] || (echo ${prodVersion} && exit 1)

  testNewAppWithSchemaWithVersion ${prodVersion}
  [[ $? -eq 0 ]] || (echo "Failed against schema version ${prodVersion}" && exit 1)

  sandboxVersion=$(getDeployedVersion "${SCHEMA_RELEASES}sandbox")
  [[ $? -eq 0 ]] || (echo ${sandboxVersion} && exit 1)

  if [[ ${sandboxVersion} != ${prodVersion} ]]; then
    testNewAppWithSchemaWithVersion ${sandboxVersion}
  fi

}

# Test the application at a given commit against the new schema. It ends up in a
# head detached branch. Caller should clean up.
testNewSchemaWithAppAtCommit() {
  if [[ $# -ne 1 ]]; then
    echo "testNewSchemaWithAppAtCommit requires exactly one parameter: commit hash on master branch"
    return 1
  fi

  # Checkout the application at the given commit.
  # This assumes that release is always from master, which may not be correct for cherry-picked
  # releases.
  git checkout -qbf master
  git checkout -qbf $1
  echo "Testing demo_app with schema at ${TEST_SCHEMA_VERSION}, server at $1"
  ./gradlew clean :demo_app:test -Pschema_repo=${TEST_SCHEMA_REPO} -Pschema_version=${TEST_SCHEMA_VERSION}
}

testNewSchemaWithApp() {
  mkdir -p ${TEST_SCHEMA_REPO}
  rm -r -f ${TEST_SCHEMA_REPO}/*
  ./gradlew :demo_schema:publish -Pschema_repo=${TEST_SCHEMA_REPO} -Pschema_version=${TEST_SCHEMA_VERSION}

  prodCommit=$(getDeployedVersion "${SERVER_RELEASES}production")
  [[ $? -eq 0 ]] || (echo ${prodCommit} && exit 1)

  if [[ ${prodCommit} != ${TRAVIS_COMMIT} ]]; then
    testNewSchemaWithAppAtCommit ${prodCommit}
  fi

  sandboxCommit=$(getDeployedVersion "${SERVER_RELEASES}sandbox")
  [[ $? -eq 0 ]] || (echo ${sandboxCommit} && exit 1)

  if [[ ${sandboxCommit} != ${TRAVIS_COMMIT} && ${sandboxCommit} != ${prodCommit} ]]; then
    testNewSchemaWithAppAtCommit ${sandboxCommit}
  fi
}



#badNoEnv=$(getSchemaVersionByEnv)
#[[ $? -ne 0 ]] && echo ${badNoEnv} && exit 1

#badWrongEnv=$(getSchemaVersionByEnv alpha)

#[[ $? -ne 0 ]] && echo ${badWrongEnv} && exit 1

#prod=$(getSchemaVersionByEnv production)

#echo "production release is $prod"

#sandbox=$(getSchemaVersionByEnv sandbox)

#echo "sandbox release is $sandbox"

# echo $(countChangesInFolders subprojects/demo_app subprojects/demo_schema)

# getDeployedVersion "${SCHEMA_RELEASES}production"
