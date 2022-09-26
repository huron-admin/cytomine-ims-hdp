

docker cp ./docker/IMS.war  ims:/var/lib/tomcat9/webapps/ROOT.war
docker cp ./docker/ims-server.xml ims:/usr/local/tomcat/conf/server.xml
docker cp ./docker/deploy.sh ims:/tmp/deploy.sh
docker cp ./docker/setenv.sh ims:/usr/share/tomcat7/bin/setenv.sh

sleep 5
docker restart ims