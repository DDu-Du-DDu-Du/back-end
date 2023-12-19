#!/bin/bash

IMAGE_NAME='ddudu-local'
CONTAINER_NAME='ddudu-test-container'

OLD_CONTAINER="$(docker ps -aq --filter "name=$CONTAINER_NAME")"

if [ -n "$OLD_CONTAINER" ]; then
  docker rm -f "$CONTAINER_NAME" && echo 'Old container has been removed'
else
  echo "There is no existing container with name $CONTAINER_NAME"
fi

cd local
docker build -t "$IMAGE_NAME" .

TEST_PASSWORD='ddudutest'
TEST_DATABASE='ddudu_test'

docker run --name "$CONTAINER_NAME" \
  -e MYSQL_PASSWORD="$TEST_PASSWORD" \
  -e MYSQL_DATABASE="$TEST_DATABASE" \
  -d -p 13307:3306 "$IMAGE_NAME" \
  && echo "$CONTAINER_NAME created!"
