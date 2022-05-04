

CONTAINER="528dae8d65e3"

docker cp ./target/latest.war  $CONTAINER:/var/lib/tomcat7/webapps/ROOT.war
docker restart $CONTAINER