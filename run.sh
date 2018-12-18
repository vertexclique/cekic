#!/usr/bin/env bash

java -jar target/scala-2.12/cekic-assembly-0.1.jar \
automotive \
-o ./systems \
-f system \
-k 1 \
-a 10 \
-c 5 \
-p 4 \
-s 0
