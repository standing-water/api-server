#!/bin/bash

if [[ "JVM_XMS" = "" ]]
then
  JVM_XMS=1024
fi

if [[ "JVM_XMX" = "" ]]
then
  JVM_XMX=1024
fi

set -e
export API_SERVER_OPTS="\
  -Xms${JVM_XMS}m \
  -Xmx${JVM_XMX}m

/app/bin/api-server

exec "$@"