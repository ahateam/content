nohup java -jar content-0.0.1-fat.jar &
echo $! > /var/run/content-0.0.1-fat.pid
echo "server started"