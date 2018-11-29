#!/usr/bin/env bash

echo "Generating native image..."

native-image --no-server \
--enable-all-security-services \
--rerun-class-initialization-at-runtime=javax.net.ssl.SSLContext \
-H:IncludeResources='.*' \
-H:Log=registerResource: \
-cp target/scala-2.12/cekic-assembly-0.1.jar \
-jar target/scala-2.12/cekic-assembly-0.1.jar \
--verbose

mv cekic{-assembly-0.1,}
