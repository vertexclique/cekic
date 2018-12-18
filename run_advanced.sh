#!/usr/bin/env bash

java -jar target/scala-2.12/cekic-assembly-0.1.jar \
automotive \
-o ./systems \
-f system \
-k 2 \
-a 10 \
-t 1000 \
-c 350 # Forward and backward intra-task communication occurs in 25% and 35% of all cases
