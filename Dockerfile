FROM hirokimatsumoto/alpine-openjdk-11

VOLUME /tmp

ENV SERVICE_ENV=dev
ENV SERVICE_PORT=8080
ENV JVM_XMS=2048
ENV JVM_XMX=2048
ENV DEPLOY_VERSION=not_set

EXPOSE ${SERVICE_PORT}

ENTRYPOINT ["docker-entrypoint.sh"]
CMD ["bash"]

RUN apk --update --no-cache add bash curl unzip

ENV LANG en_US.UTF-8
ENV LANGUAGE en_US:en

COPY docker-entrypoint.sh /usr/local/bin/docker-entrypoint.sh

RUN chmod 700 /usr/local/bin/docker-entrypoint.sh && \
    ln -s /usr/local/bin/docker-entrypoint.sh /

COPY ./build/distributions/api-server-v1.tar /app.tar

RUN tar -xf app.tar && \
    mv api-server-v1 app && \
    chmod 700 -R /app/bin && \
    chmod 400 -R /app/lib
