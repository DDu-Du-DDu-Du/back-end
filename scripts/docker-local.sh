#!/bin/bash

IMAGE_NAME='ddudu-local'
CONTAINER_NAMES=("ddudu-local-container" "ddudu-test-container")

for CONTAINER_NAME in "${CONTAINER_NAMES[@]}"; do
  OLD_CONTAINER="$(docker ps -aq --filter "name=$CONTAINER_NAME")"
  if [ -n "$OLD_CONTAINER" ]; then
    docker rm -f "$CONTAINER_NAME" && echo 'Old container has been removed'
  else
    echo "There is no existing container with name $CONTAINER_NAME"
  fi
done

cd local
docker build -t "$IMAGE_NAME" .

LOCAL_PASSWORD='ddudulocal'
LOCAL_DATABASE='ddudu'
TEST_PASSWORD='ddudutest'
TEST_DATABASE='ddudu_test'

# Local DB Container
docker run --name "${CONTAINER_NAMES[0]}" \
  -e MYSQL_PASSWORD="$LOCAL_PASSWORD" \
  -e MYSQL_DATABASE="$LOCAL_DATABASE" \
  -d -p 13306:3306 "$IMAGE_NAME" \
  && echo "${CONTAINER_NAMES[0]} created!"

# Test DB Container
docker run --name "${CONTAINER_NAMES[1]}" \
  -e MYSQL_PASSWORD="$TEST_PASSWORD" \
  -e MYSQL_DATABASE="$TEST_DATABASE" \
  -d -p 13307:3306 "$IMAGE_NAME" \
  && echo "${CONTAINER_NAMES[1]} created!"

