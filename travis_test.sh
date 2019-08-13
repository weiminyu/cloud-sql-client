#!/bin/bash

LOCAL_BRANCH_UNDER_TEST="commit_under_test"
SCHEMA_PROJECT="subprojects/demo_schema"
APP_PROJECT="subprojects/demo_schema"

echo "Testing ${TRAVIS_BRANCH} at ${TRAVIS_COMMIT}"
echo "TRAVIS_BUILD_DIR is ${TRAVIS_BUILD_DIR}"

# Run all tests at head, including the check for simultaneous changes
# to both schema and app projects. Abort if build fails.
./gradlew build || exit 1

echo "Starting cross-release tests."

source ./test_utils.sh

if [[ ${TRAVIS_BRANCH} == "master" ]]; then
  # Assuming this is CI test. Perform cross-branch test for
  # both schema and app. Note that app should be tested first,
  # since schema test will end up at another commit.
  echo "Testing application changes..."
  testNewAppWithSchema

  echo "Testing schema changes..."
  testNewSchemaWithApp
else
  # Assuming this is Presubmit test
  schemaChanges=$(countChangesInFolders subprojects/demo_schema)
  if [[ ${schemaChanges} -gt 0 ]]; then
    echo "Testing schema changes..."
    testNewSchemaWithApp
  else
    echo "Testing application changes..."
    testNewAppWithSchema
  fi
fi
