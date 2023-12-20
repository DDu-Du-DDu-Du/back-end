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

docker build -t "$IMAGE_NAME" .

LOCAL_PASSWORD='ddudulocal'
LOCAL_DATABASE='ddudu'
TEST_PASSWORD='ddudutest'
TEST_DATABASE='ddudu_test'
COMMON_ROOT_PASSWORD='ddudu-local-root'
HEALTH_CHECK_CMD="mysqladmin ping -h localhost -uroot -p$COMMON_ROOT_PASSWORD"
HEALTHY="healthy"

# Local DB Container
docker run --name "${CONTAINER_NAMES[0]}" \
  --health-cmd="$HEALTH_CHECK_CMD" \
  -e MYSQL_PASSWORD="$LOCAL_PASSWORD" \
  -e MYSQL_DATABASE="$LOCAL_DATABASE" \
  -d -p 13306:3306 "$IMAGE_NAME" \
  && echo "${CONTAINER_NAMES[0]} created!" \


# Test DB Container
docker run --name "${CONTAINER_NAMES[1]}" \
  --health-cmd="$HEALTH_CHECK_CMD" \
  -e MYSQL_PASSWORD="$TEST_PASSWORD" \
  -e MYSQL_DATABASE="$TEST_DATABASE" \
  -d -p 13307:3306 "$IMAGE_NAME" \
  && echo "${CONTAINER_NAMES[1]} created!"

# Health Check
for CONTAINER_NAME in "${CONTAINER_NAMES[@]}"; do
  echo "Connecting to $CONTAINER_NAME..."

  count=0
  while [ "$(docker inspect --format "{{ .State.Health.Status }}" "$CONTAINER_NAME")" != "$HEALTHY" -a "$count" -lt 120 ]; do
    echo "Waiting for connection..$count"
    ((count += 5))
    sleep 5
  done

  if [ "$(docker inspect --format "{{ .State.Health.Status }}" "$CONTAINER_NAME")" == "$HEALTHY" ]; then
    echo "$CONTAINER_NAME is connected successfully!"
  else
    echo "Connection failed for $CONTAINER_NAME"
  fi
done
