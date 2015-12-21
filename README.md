# play-rds [![Build Status](https://travis-ci.org/Enalmada/play-rds.svg?branch=master)](https://travis-ci.org/Enalmada/play-rds) [![Join the chat at https://gitter.im/Enalmada/play-rds](https://badges.gitter.im/Join%20Chat.svg)](https://gitter.im/Enalmada/play-rds?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge) [![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.github.enalmada/play-rds/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.github.enalmada/play-rds)


This is a collection of rds helpers.  Right now it just restores a db and that is all it does.
Hopefully it saves you a few hours of having to figure it the quirks of AWS on your own.
The code isn't pretty or idomatic scala yet, but it works. 

#### Version information
**I believe Play-RDS needs Play! Framework 2.4.x or later**

Play-RDS is built and tested with Scala 2.11.7 (from `0.1.1`)

Works fine with

* `2.4.0` to `2.4.x` (last: `0.1.1` - [master branch](https://github.com/enalmada/play-rds/tree/master))

Releases are on [mvnrepository](http://mvnrepository.com/artifact/com.github.enalmada) and snapshots can be found on [sonatype](https://oss.sonatype.org/content/repositories/snapshots/com/github/enalmada).

## Quickstart
Clone the project and go to `samples`. Edit your application.conf with your AWS master and staging db settings and run `sbt run` to see a sample application.

### Including the Dependencies

```xml
<dependency>
    <groupId>com.github.enalmada</groupId>
    <artifactId>play-rds_2.11</artifactId>
    <version>0.1.1</version>
</dependency>
```
or

```scala
val appDependencies = Seq(
  "com.github.enalmada" %% "play-rds" % "0.1.1"
)
```

## Features

* Right now it just restores the latest production snapshot to staging.  More to come...

## Versions
* **TRUNK** [not released in the repository, yet]
  * Fancy contributing something? :-)
* **0.1.1** [release on 2015-11-30]
  * Make snapshot latest out of 100.  
* **0.1.0** [release on 2015-11-30]
  * Initial release.
  
## License

Copyright (c) 2015 Adam Lane

Licensed under the Apache License, Version 2.0 (the "License"); you may not use this work except in compliance with the License. You may obtain a copy of the License in the LICENSE file, or at:

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
  