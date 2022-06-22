

docker stop ims
docker rm ims

DOCKER_BUILDKIT=1 docker build --rm -f docker/Dockerfile -t ims-test .

docker create --name ims \
--link bioformat:bioformat \
-e IMS_STORAGE_PATH=/data/images \
-v /data/images:/data/images \
-v /data/images/_buffer:/tmp/uploaded \
--restart=unless-stopped \
ims-test > /dev/null

docker cp ./config/ims-config.groovy ims:/usr/share/tomcat9/.grails/ims-config.groovy
docker cp ./config/addHosts.sh ims:/tmp/addHosts.sh
docker start ims
