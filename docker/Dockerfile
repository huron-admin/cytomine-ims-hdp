#
# Copyright (c) 2009-2022. Authors: see NOTICE file.
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#      http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#
ARG VERSION_NUMBER=v0.0.0-alpha.0

ARG GRAILS_VERSION=2.4.4
ARG LOGROTATE_VERSION=3.20.1
ARG VIPS_VERSION=8.12.2
ARG OPENSLIDE_VERSION=3.4.2-huron
ARG OPENSLIDE_JAVA_VERSION=0.12.2
ARG IMAGE_MAGICK_VERSION=7.1.0-46

FROM openjdk:8u292-slim AS war-builder
ARG GRAILS_VERSION
WORKDIR /app

RUN apt-get update -y && apt-get install wget unzip -y
RUN mkdir /tools && wget https://github.com/grails/grails-core/releases/download/v${GRAILS_VERSION}/grails-${GRAILS_VERSION}.zip -O /tools/grails.zip
RUN unzip /tools/grails.zip -d /tools

COPY ./grails-app/conf/BuildConfig.groovy /app/grails-app/conf/BuildConfig.groovy

RUN /tools/grails-${GRAILS_VERSION}/bin/grails compile --stacktrace

COPY grails-app/ /app/grails-app/
COPY lib/ /app/lib/
COPY src/ /app/src/
COPY web-app/ /app/web-app/
COPY wrapper/ /app/wrapper/
COPY application.properties /app/

ARG VERSION_NUMBER
ENV VERSION_NUMBER_ENV=$VERSION_NUMBER

RUN sed -i -- 's/app.version=0.0.0/app.version='$VERSION_NUMBER'/g' /app/application.properties
ENV GRAILS_OPTS="-Xmx2G -Xms512m -XX:MaxPermSize=512m"

RUN /tools/grails-${GRAILS_VERSION}/bin/grails war
RUN mkdir bin && mv /app/IMS.war /app/bin/ROOT.war

RUN ls /app

FROM tomcat:9.0-jdk8-openjdk AS log-builder

RUN apt-get update -y && apt-get -y install \
      build-essential \
      locate \
      logrotate \
      net-tools \
      unzip \
      wget

WORKDIR /tmp

ARG LOGROTATE_VERSION
RUN apt-get -y update && apt-get install -y autoconf automake libpopt-dev libtool make xz-utils
RUN wget "https://github.com/logrotate/logrotate/releases/download/${LOGROTATE_VERSION}/logrotate-${LOGROTATE_VERSION}.tar.xz" && tar -xJf "logrotate-${LOGROTATE_VERSION}.tar.xz"
RUN cd "/tmp/logrotate-${LOGROTATE_VERSION}" && autoreconf -fiv && ./configure && make


FROM tomcat:9.0-jdk8-openjdk

ARG LOGROTATE_VERSION

# base librairies and configuration
RUN apt-get update -y && apt-get -y install \
      locate \
      logrotate \
      net-tools

RUN sed -i "/su root syslog/c\su root root" /etc/logrotate.conf
ENV LANG C.UTF-8
ENV DEBIAN_FRONTEND noninteractive

RUN apt-get -y update && apt-get install --no-install-recommends --no-install-suggests -y ant \
      automake \
      dnsutils \
      ffmpeg \
      git \
      gobject-introspection \
      gtk-doc-tools \
      libcairo2-dev \
      libfftw3-dev \
      libgdk-pixbuf2.0-dev \
      libgsf-1-dev \
      libglib2.0-dev \
      libimage-exiftool-perl \
      libjpeg62-turbo-dev \
      libopenexr-dev \
      libopenjp2-7-dev \
      liborc-0.4-0 \
      liborc-0.4-dev \
      libsqlite3-dev \
      libtiff5-dev \
      libtiff-tools \
      libtool \
      libxml2-dev \
      software-properties-common \
      swig && \
    rm -rf /var/lib/apt/lists/*

# openslide
ARG OPENSLIDE_VERSION
RUN cd /tmp && \
    wget https://github.com/101x4/openslide/releases/download/v${OPENSLIDE_VERSION}/openslide-${OPENSLIDE_VERSION}.tar.xz && \
    tar xf ./openslide-${OPENSLIDE_VERSION}.tar.xz && \
    cd ./openslide && \
    autoreconf -i && \
    ./configure && \
    make && \
    make install && \
    ldconfig

# openslide-java
ARG OPENSLIDE_JAVA_VERSION
RUN export JAVA_HOME="/usr/local/openjdk-8" && \
    export CFLAGS="-I/usr/local/openjdk-8" && \
    cd /tmp && \
    wget https://github.com/openslide/openslide-java/releases/download/v${OPENSLIDE_JAVA_VERSION}/openslide-java-${OPENSLIDE_JAVA_VERSION}.tar.gz && \
    tar -zxvf ./openslide-java-${OPENSLIDE_JAVA_VERSION}.tar.gz && \
    cd ./openslide-java-${OPENSLIDE_JAVA_VERSION} && \
    autoreconf -i && \
    ./configure && \
    make && \
    make install && \
    ldconfig
ENV LD_LIBRARY_PATH=/usr/local/lib/openslide-java

# imagemagick (Was 6.8.9-10) (fix problem with DICOM conversion by vips)
ARG IMAGE_MAGICK_VERSION=6.9.12-66
RUN cd /tmp && \
    wget https://imagemagick.org/archive/ImageMagick-${IMAGE_MAGICK_VERSION}.tar.xz && \
    tar xf ImageMagick-${IMAGE_MAGICK_VERSION}.tar.xz && \
    cd ImageMagick-${IMAGE_MAGICK_VERSION} && \
    ./configure && \
    make && \
    make install && \
    ldconfig /usr/local/lib

# vips
ARG VIPS_VERSION
RUN cd /tmp && \
    wget https://github.com/libvips/libvips/releases/download/v${VIPS_VERSION}/vips-${VIPS_VERSION}.tar.gz && \
    tar -zxvf ./vips-${VIPS_VERSION}.tar.gz && \
    cd ./vips-${VIPS_VERSION} && \
    LDFLAGS="-L/usr/local/lib -lopenslide" CPPFLAGS="-I/usr/local/include/openslide" ./configure && \
    make && \
    make install && \
    ldconfig

# gdal
RUN apt-get update -y && \
    apt-get install --no-install-recommends --no-install-suggests -y gdal-bin && \
    rm -rf /var/lib/apt/lists/*

RUN mkdir -p /usr/share/tomcat9/.grails

# ims specificities
RUN ln -s /usr/local/tomcat /var/lib/tomcat9 #for backward compatibility
RUN ln -s /usr/share/tomcat9/.grails /root/.grails #for backward compatibility

COPY --from=log-builder --chmod=0755 "/tmp/logrotate-${LOGROTATE_VERSION}/logrotate" /usr/sbin/
COPY --from=war-builder --chmod=0755 /app/bin/ROOT.war /var/lib/tomcat9/webapps/

RUN ln -s /usr/local/bin/vips /usr/bin/vips && ln -s /usr/local/bin/identify /usr/bin/identify

RUN touch /tmp/addHosts.sh

COPY --chmod=0755 docker/setenv.sh /usr/share/tomcat7/bin/setenv.sh
COPY --chmod=0755 docker/deploy.sh /tmp/deploy.sh

ENTRYPOINT ["/tmp/deploy.sh"]
