#!/usr/bin/env bash

echo "Generating native image..."

# -H:Log=registerResource: \
# --delay-class-initialization-to-runtime=sun.awt.AWTAutoShutdown,sun.java2d.opengl.OGLRenderQueue \

native-image --no-server \
-H:IncludeResources='.*' \
-H:Log=registerResource: \
-cp target/scala-2.12/cekic-assembly-0.1.jar \
-jar target/scala-2.12/cekic-assembly-0.1.jar \
--verbose

mv cekic{-assembly-0.1,-bin}
