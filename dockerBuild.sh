set -o xtrace
set -o errexit

echo "************************************** Publish docker ******************************************"

docker login git.hurondigitalpathology.com:5005

#DOCKER_REPO="git.hurondigitalpathology.com:5005/pine/cytomine-ims"
DOCKER_REPO="huronalex/ims"
VERSION_NUMBER="2.1"

docker build --rm -f docker/Dockerfile -t  $DOCKER_REPO:v$VERSION_NUMBER ./docker

docker push $DOCKER_REPO:v$VERSION_NUMBER
docker rmi $DOCKER_REPO:v$VERSION_NUMBER
