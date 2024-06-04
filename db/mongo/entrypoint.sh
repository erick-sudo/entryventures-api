#!/bin/bash

set -e

# Start MongoDB
mongod --fork --logpath /var/log/mongodb.log

## Wait for MongoDB to be ready
#until mongo --eval "print(\"waited for connection\")"
#do
#    sleep 1
#done

# Run MongoDB setup script
mongosh < /docker-entrypoint-initdb.d/setup.js

# Stop MongoDB
mongod --shutdown

# Start MongoDB with authentication
mongod --auth --bind_ip_all