#!/bin/sh
rm -rf Metropolis \
    world \
    world_nether \
    world_the_end \
    logs \
    plugins/Metropolis/cache

mysql -uroot -proot < /vagrant/init.sql #Rebuild MySQL Database
sudo service mysql restart #Restart MySQL
./start.sh #Start the Minecraft Server