#!/usr/bin/env bash
cd refresh-build
sudo git pull
sudo ./mvnw clean package
sudo service refresh stop
sudo rm ../refresh.jar
sudo mv target/refresh-dev-1.0.jar ../refresh.jar
sudo service refresh start
echo ""
echo ""
echo ""
echo "UPDATING REFRESH FINISHED."
