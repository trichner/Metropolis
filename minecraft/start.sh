#!/bin/bash
cd "$( dirname "$0" )"
rm -r Metropolis
rm -r world
rm -r world_nether
rm -r world_the_end
java -server -Xms5G -Xmx8G -XX:+UseG1GC -XX:+AggressiveOpts -XX:+UseFastAccessorMethods -XX:TargetSurvivorRatio=90 -XX:MaxGCPauseMillis=200 -XX:MaxPermSize=256m -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=8787  -jar craftbukkit.jar