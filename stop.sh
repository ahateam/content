PID=$(cat /var/run/content-0.0.1-fat.pid)
echo $PID
kill -s 9 $PID