# syntax=docker/dockerfile:1-labs
# Note: This Dockerfile is used by the integration tests for compiling the app when there was no image was supplied
ARG JAVA_VERSION=25
FROM eclipse-temurin:${JAVA_VERSION}-jre-alpine AS jre-base


# Force upgrade to get rid of CVEs
# See also https://stackoverflow.com/a/76440791
FROM alpine:3 AS alpine-upgraded

RUN apk upgrade --no-cache


# Build the JRE ourself and exclude stuff from Eclipse-Temurin that we don't need
#
# Derived from https://github.com/adoptium/containers/blob/91ea190c462741d2c64ed2f8f0a0efdb3e77c49d/21/jre/alpine/3.21/Dockerfile
FROM scratch AS jre-minimized

COPY --from=alpine-upgraded / /
CMD ["/bin/sh"]

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

# Force upgrade so that there are no conflicts between node's and java's dependencies
RUN apk upgrade --no-cache
RUN apk add --no-cache git nodejs npm

# Check if node and npm work
RUN node --version \
	&& npm --version

WORKDIR /builder

# Copy & Cache wrapper
COPY --parents mvnw .mvn/** ./
RUN ./mvnw --version

# Copy & Cache poms/dependencies
COPY --parents **/pom.xml ./
# Resolve jars so that they can be cached and don't need to be downloaded when a Java file changes
ARG MAVEN_GO_OFFLINE_COMMAND='./mvnw -B dependency:go-offline -pl demo/webapp-vaadin -am -Pprod,dev-log -DincludeScope=runtime -T2C'
RUN echo "Executing '$MAVEN_GO_OFFLINE_COMMAND'"
RUN ${MAVEN_GO_OFFLINE_COMMAND}

# Copying all other files
COPY . ./

# A valid Git repo is required for the build
RUN git config --global user.email "dynamic@build.local" \
  && git config --global user.name "Dynamic Build" \
  && git init --initial-branch=dynamically-built-tcst \
  && git add . \
  && git commit -m "Init commit"

ARG MAVEN_BUILD_COMMAND='./mvnw -B package -pl demo/webapp-vaadin -am -Pprod,dev-log -T2C -Dmaven.test.skip'
RUN echo "Executing '$MAVEN_BUILD_COMMAND'"
RUN ${MAVEN_BUILD_COMMAND}

# See also https://docs.spring.io/spring-boot/reference/packaging/container-images/dockerfiles.html for further information
RUN mv demo/webapp-vaadin/target/webapp-vaadin.jar app.jar \
  && java -Djarmode=tools -jar app.jar extract --layers --destination extracted


FROM jre-minimized

ARG ENABLE_AOT

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

# RUN AOT and boot with dummy configuration
RUN if [[ ! -z "$ENABLE_AOT" ]]; then \
  java \
    -XX:+UseG1GC \
    -XX:+UseCompactObjectHeaders \
    -XX:+UseStringDeduplication \
    -XX:AOTCacheOutput=app.aot \
    -Djava.awt.headless=true \
    -Dspring.context.exit=onRefresh \
    # Disable Flyway
    -Dspring.flyway.enabled=false \
    # Disabled DB actuator endpoint
    -Dmanagement.health.db.enabled=false \
    # Disbale DB instant connection
    -Dspring.jpa.properties.jakarta.persistence.database-product-name=MariaDB \
    -Dspring.jpa.properties.jakarta.persistence.database-major-version=11 \
    -Dspring.jpa.properties.jakarta.persistence.database-minor-version=8 \
    -Dspring.jpa.properties.hibernate.boot.allow.jdbc.metadata.access=false \
    # Fake DB
    -Dspring.datasource.url=jdbc:mariadb://localhost:3306/dummy \
    -Dspring.datasource.username=dummy \
    -Dspring.datasource.password=dummy \
    # Fake Auth
    -Dspring.security.oauth2.client.registration.local.authorization-grant-type=authorization_code \
    -Dspring.security.oauth2.client.registration.local.redirect-uri="{baseUrl}/{action}/oauth2/code/{registrationId}" \
    -Dspring.security.oauth2.client.provider.local.authorization-uri=http://localhost/connect/authorize \
    -Dspring.security.oauth2.client.provider.local.token-uri=http://localhost/connect/token \
    -Dspring.security.oauth2.client.provider.local.jwk-set-uri=http://localhost/.well-known/openid-configuration/jwks \
    -Dspring.security.oauth2.client.provider.local.user-info-uri=http://localhost/connect/userinfo \
    -Dspring.security.oauth2.client.provider.local.user-info-authentication-method=header \
    -Dspring.security.oauth2.client.provider.local.user-name-attribute=sub \
    -Dspring.security.oauth2.client.registration.local.client-name=Local \
    -Dspring.security.oauth2.client.registration.local.client-id=dummy \
    -Dspring.security.oauth2.client.registration.local.client-secret=dummy \
    -Dspring.security.oauth2.client.registration.local.scope="openid,profile,email,offline_access" \
    -jar app.jar; \
  fi

ENV JAVA_OPTS="-XX:+UseG1GC -XX:MaxRAMPercentage=75 -XX:MinHeapFreeRatio=30 -XX:MaxHeapFreeRatio=50 -XX:+UseCompactObjectHeaders -XX:+UseStringDeduplication -Djava.awt.headless=true"
ENV JAVA_AOT_OPTS=${ENABLE_AOT:+"-XX:AOTCache=app.aot"}

EXPOSE 8080

CMD [ "/bin/sh", "-c", "java $JAVA_OPTS $JAVA_AOT_OPTS -jar /opt/app/app.jar" ]
