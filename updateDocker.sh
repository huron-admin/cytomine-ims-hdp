
CONTAINER="ims"

docker cp ./IMS.war  $CONTAINER:/var/lib/tomcat9/webapps/ROOT.war
docker restart $CONTAINER