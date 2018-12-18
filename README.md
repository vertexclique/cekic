
![](img/cekic.png)

-----
This application creates:
* Approximately system that can be classified under automotive domain
    * With that, it also creates runnable listing.
* Bare OSEK style RTS system mappings (which is by default)

## Installation and Development
You need to have `sbt` and `JDK` installed and appended to path.
You can assemble überjar with:
```bash
$ ./assemble.sh
``` 

There are two ways of running:
### Running überjar
You can run the überjar after assembly phase with:
```bash
$ ./run.sh
```
You can tweak the generation process parameters inside the `run.sh`.

### Running native image
for native executable generation you need to have `GraalVM`.
Currently it is problematic because of AWT dependencies.
If you have `GraalVM` you can run:
```bash
$ ./genimage.sh
```

## Usage (commands & arguments)

```
Usage: cekic [automotive|osek] [options]

Command: automotive
Generates automotive industry conforming application
Command: osek
Generates bare OSEK style application (default)
  -o <file> | --out <file>
        out is the path to which the testcases are to be stored
  -f <value> | --fout <value>
        fout is the generic name of the test cases
  -a <value> | --sysCount <value>
        a is the count of the systems that will be generated from the specification
  -k <value> | --appCount <value>
        k is the amount of the application that will be generated for the system
  -s <value> | --seed <value>
        s is seed for system generation
  -p <value> | --processors <value>
        p is processor count for the underlying system
  -c <value> | --commRes <value>
        c is communication resource count
  -t <value> | --taskCnt <value>
        t is task count inside of a task chain
  -m <value> | --taskMapper <value>
        m maps task chains such that only tasks that are adjacent in the task graph are mapped to the same processor
  -r <value> | --assignRandomPrio <value>
        r assigns priorities randomly but ensures that tasks in the front of a task chain receives higher priorities
  --minu <value>
        minu is minimum resource utilization
  --maxu <value>
        maxu is maximum resource utilization
  --minap <value>
        minap is minimum activation period
  --maxap <value>
        maxap is maximum activation period
  --bcet <value>
        bcet is set as percentage of WCET
  --minlax <value>
        minlax is minimum constraint laxity
  --maxlax <value>
        maxlax is minimum constraint laxity
```
