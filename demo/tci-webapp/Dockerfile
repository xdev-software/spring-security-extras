# Note: This Dockerfile is used by the selenium tests for compiling Timeline when there was no Image supplied
ARG JAVA_VERSION=21
FROM eclipse-temurin:${JAVA_VERSION}-jre-alpine AS jre-base


# Build the JRE ourself and exclude stuff from Eclipse-Temurin that we don't need
#
# Derived from https://github.com/adoptium/containers/blob/91ea190c462741d2c64ed2f8f0a0efdb3e77c49d/21/jre/alpine/3.21/Dockerfile
FROM alpine:3 AS jre-minimized

ENV JAVA_HOME=/opt/java/openjdk
ENV PATH=$JAVA_HOME/bin:$PATH

ENV LANG='en_US.UTF-8' LANGUAGE='en_US:en' LC_ALL='en_US.UTF-8'

RUN set -eux; \
    # DO NOT INSTALL:
    # gnupg - only required to verify download of jre from eclipse-temurin
    # fontconfig ttf-dejavu - No fonts are needed (we also don't use AWT)
    # everything that works with certificates (ca-certificates p11-kit-trust coreutils openssl) - as we don't do stuff with certificates
    # tzdata - We are using UTC (and everyone should do that)
    apk add --no-cache \
        musl-locales musl-locales-lang

COPY --from=jre-base /opt/java/openjdk /opt/java/openjdk

RUN set -eux; \
    echo "Verifying install ..."; \
    echo "java --version"; java --version; \
    echo "Complete."

# Renamed as cacerts functionality is disabled
COPY --from=jre-base /__cacert_entrypoint.sh /entrypoint.sh
RUN chmod 775 /entrypoint.sh
ENTRYPOINT ["/entrypoint.sh"]


FROM eclipse-temurin:${JAVA_VERSION}-jdk-alpine AS builder

RUN apk add --no-cache git

WORKDIR /builder

# Copying is prepared by ITC
COPY . ./

# A valid Git repo is required for the build
RUN git config --global user.email "dynamic@build.local" \
  && git config --global user.name "Dynamic Build" \
  && git init --initial-branch=dynamically-built-tcst \
  && git add . \
  && git commit -m "Init commit"

ARG mavenbuildcmd='./mvnw -B clean package -pl "webapp" -am -Pprod,dev-log -T2C -Dmaven.test.skip'

RUN echo "Executing '$mavenbuildcmd'"
RUN ${mavenbuildcmd}
# See also https://docs.spring.io/spring-boot/reference/packaging/container-images/dockerfiles.html for further information
RUN mv webapp/target/webapp.jar app.jar \
  && java -Djarmode=tools -jar app.jar extract --layers --destination extracted


FROM jre-minimized

ARG user=app
ARG group=app
ARG uid=1000
ARG gid=1000
ARG APP_DIR=/opt/app

# Create user + group + home
RUN mkdir -p ${APP_DIR} \
  && chown ${uid}:${gid} ${APP_DIR} \
  && addgroup -g ${gid} ${group} \
  && adduser -h "$APP_DIR" -u ${uid} -G ${group} -s /bin/bash -D ${user}

WORKDIR ${APP_DIR}

USER ${user}

COPY --from=builder --chown=${user}:${group} /builder/extracted/dependencies/ ./
# https://github.com/moby/moby/issues/37965
RUN true
COPY --from=builder --chown=${user}:${group} /builder/extracted/spring-boot-loader/ ./
# https://github.com/moby/moby/issues/37965
RUN true
COPY --from=builder --chown=${user}:${group} /builder/extracted/snapshot-dependencies/ ./
# https://github.com/moby/moby/issues/37965
RUN true
COPY --from=builder --chown=${user}:${group} /builder/extracted/application/ ./

ENV JAVA_OPTS="-XX:MaxRAMPercentage=75 -XX:MinHeapFreeRatio=20 -XX:MaxHeapFreeRatio=30 -Djava.awt.headless=true"

EXPOSE 8080

CMD [ "/bin/sh", "-c", "java $JAVA_OPTS -jar /opt/app/app.jar" ]
