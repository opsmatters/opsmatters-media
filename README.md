![opsmatters](https://i.imgur.com/VoLABc1.png)

# OpsMatters Media Utilities
[![Build Status](https://travis-ci.org/opsmatters/opsmatters-media.svg?branch=master)](https://travis-ci.org/opsmatters/opsmatters-media)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.opsmatters/opsmatters-media/badge.svg?style=blue)](https://maven-badges.herokuapp.com/maven-central/com.opsmatters/opsmatters-media)
[![Javadocs](http://javadoc.io/badge/com.opsmatters/opsmatters-media.svg)](http://javadoc.io/doc/com.opsmatters/opsmatters-media)

Java library for the OpsMatters suite to compose, persist and deliver various web media types.

## Installing

First clone the repository using:
```
>$ git clone https://github.com/opsmatters/opsmatters-media.git
>$ cd opsmatters-media
```

To compile the source code, run all tests, and generate all artefacts (including sources, javadoc, etc):
```
mvn package 
```

## Running the tests

To execute the unit tests:
```
mvn clean test 
```

The following tests are included:

* None yet

## Deployment

The build artefacts are hosted in The Maven Central Repository. 

Add the following dependency to include the artefact within your project:
```
<dependency>
  <groupId>com.opsmatters</groupId>
  <artifactId>opsmatters-media</artifactId>
  <version>1.0.0</version>
</dependency>
```

## Built With

* [docx4j](https://www.docx4java.org/trac/docx4j) - Java library for creating and manipulating Microsoft Open XML files
* [jxl](http://jexcelapi.sourceforge.net/) - Java API enabling developers to read, write, and modify Excel spreadsheets
* [opencsv](http://opencsv.sourceforge.net/) - CSV parser library for Java
* [Maven](https://maven.apache.org/) - Dependency Management
* [JUnit](http://junit.org/) - Unit testing framework

## Contributing

Please read [CONTRIBUTING.md](https://www.contributor-covenant.org/version/1/4/code-of-conduct.html) for details on our code of conduct, and the process for submitting pull requests to us.

## Versioning

This project use [SemVer](http://semver.org/) for versioning. For the versions available, see the [tags on this repository](https://github.com/opsmatters/opsmatters-media/tags). 

## Authors

* **Gerald Curley** - *Initial work* - [opsmatters](https://github.com/opsmatters)

See also the list of [contributors](https://github.com/opsmatters/opsmatters-media/contributors) who participated in this project.

## License

This project is licensed under the terms of the [Apache license 2.0](https://www.apache.org/licenses/LICENSE-2.0.html).

<sub>Copyright (c) 2020 opsmatters</sub>