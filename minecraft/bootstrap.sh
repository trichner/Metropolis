#!/usr/bin/env bash

# Config MySQL
#######################
echo mysql-server mysql-server/root_password password root | sudo debconf-set-selections
echo mysql-server mysql-server/root_password_again password root | sudo debconf-set-selections

# Install Dependencies
#######################
apt-get update
apt-get -y install \
	screen \
	python-cherrypy3 \
	git \
	openjdk-7-jre-headless \
	mysql-server

# Install Spigot
#######################
cd /vagrant/

# Setup Plugins
#######################
./rebuild.sh