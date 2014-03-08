java -server -Xms5G -Xmx6G -XX:+UseG1GC -XX:+AggressiveOpts -XX:+UseFastAccessorMethods -XX:TargetSurvivorRatio=90 -XX:MaxGCPauseMillis=200 -XX:MaxPermSize=256m  -jar spigot.jar
wait