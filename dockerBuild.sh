set -o xtrace
set -o errexit

echo "************************************** Publish docker ******************************************"

DOCKER_REPO="git.hurondigitalpathology.com:5005/pine/ims"
VERSION_NUMBER="1.0"

docker build --no-cache --rm -f docker/Dockerfile -t  $DOCKER_REPO:v$VERSION_NUMBER ./docker

docker push $DOCKER_REPO:v$VERSION_NUMBER
# docker rmi $DOCKER_REPO:v$VERSION_NUMBER
