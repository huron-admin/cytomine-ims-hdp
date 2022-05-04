

CONTAINER="26d022f33df3"

docker cp ./target/latest.war  $CONTAINER:/var/lib/tomcat7/webapps/ROOT.war
docker restart $CONTAINER