#!/usr/bin/env bash
mvn clean install
# shellcheck disable=SC1001
# possible values for profile: bing (default), google, it uses different precalculated coordinates
java -jar target\dron-delivery-1.0-SNAPSHOT.jar -Dspring-boot.run.profiles=bing
echo "Check the output file"