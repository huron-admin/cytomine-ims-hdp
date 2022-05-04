set -o xtrace
set -o errexit

echo "************************************** Publish docker ******************************************"

VERSION_NUMBER="1.1"

docker build --no-cache --rm -f docker/Dockerfile -t  huronalex/ims:v$VERSION_NUMBER ./docker

docker push huronalex/ims:v$VERSION_NUMBER
docker rmi huronalex/ims:v$VERSION_NUMBER
