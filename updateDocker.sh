
docker cp ./IMS.war  ims:/var/lib/tomcat9/webapps/ROOT.war

sleep 5
docker restart ims