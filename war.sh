#!/bin/bash
export GRAILS_HOME=/home/alex/grails
export PATH=$GRAILS_HOME/bin:$PATH

mkdir tmp
cp lib/openslide.jar tmp/openslide.jar
cp natives/openslide/linux/openslide.jar lib/openslide.jar
grails clean
grails war
cp tmp/openslide.jar lib/openslide.jar
rm -r tmp

