#!/bin/bash
# export GRAILS_HOME=/home/alex/grails
# export PATH=$GRAILS_HOME/bin:$PATH

# grails clean
# grails war

VERSION_NUMBER="2.0.0"

docker build --rm -f scripts/docker/Dockerfile-war.build --build-arg VERSION_NUMBER=$VERSION_NUMBER -t  cytomine/cytomine-ims-war .

containerId=$(docker create cytomine/cytomine-ims-war )
docker start -ai  $containerId
docker cp $containerId:/app/IMS.war ./docker/IMS.war

docker rm $containerId
docker rmi cytomine/cytomine-ims-war

sh update.sh