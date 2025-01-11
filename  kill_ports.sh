#!/bin/bash

for port in {8000..8012}
do
  pid=$(lsof -t -i:$port)
  if [ -n "$pid" ]; then
    echo "Killing process on port $port with PID $pid"
    kill -9 $pid
  else
    echo "No process found on port $port"
  fi
done