#!/usr/bin/env bash

# Install Dependencies
#######################
apt-get update
apt-get -y install \
	screen \
	python-cherrypy3 \
	git \
	openjdk-7-jre-headless \
	rubygems
	
gem install bukkit

# Install Spigot
#######################
mkdir /minecraft
cp /vagrant/minecraft/* /minecraft
cd /minecraft
curl http://ci.md-5.net/job/Spigot/lastSuccessfulBuild/artifact/Spigot-Server/target/spigot.jar > craftbukkit.jar

# Setup Plugins
#######################
./setup.sh

# Start Server
#######################
./start.sh