# gitgrid

## Preconditions

To run this project in production mode you need:

* Java JDK (>1.6) installed

To run this project in development mode you need:

* Java JDK (>1.6) installed
* Scala (>2.10) installed
* SBT (>0.13) installed
* NodeJS (>0.10) installed
* Grunt-CLI installed
* Bower installed

## Deployment

```bash
# package all together
$ sbt pack
```

## Running in development mode

```bash
# grab node modules and bower components
$ sbt gruntBowerInit

# start grunt development server and scala backend server
$ sbt gruntStart run
```

## Test execution

```bash
# simple test execution
$ sbt test

# test execution with code coverage
$ sbt clean scct:test printCoverage
```
