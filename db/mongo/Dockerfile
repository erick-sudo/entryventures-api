FROM mongo:latest
LABEL authors="slade001"

# Environment variables
ENV MONGO_INITDB_DATABASE=entry_ventures
ENV MONGO_INITDB_ROOT_USERNAME=entry_ventures
ENV MONGO_INITDB_ROOT_PASSWORD=entry_ventures

# Copy setup script
COPY setup.js /docker-entrypoint-initdb.d/

# Copy custom entrypoint script
COPY entrypoint.sh /usr/local/bin/entrypoint.sh
# Make the script executable
RUN chmod +x /usr/local/bin/entrypoint.sh

ENTRYPOINT ["entrypoint.sh"]

EXPOSE 27017

CMD ["mongod"]