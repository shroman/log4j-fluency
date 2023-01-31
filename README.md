[![Maven Central](https://img.shields.io/maven-central/v/io.github.shroman/log4j-fluency.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:%22io.github.shroman%22%20AND%20a:%22log4j-fluency%22)

[Fluency](https://github.com/komamitsu/fluency) appender.

### Install

#### Gradle

```kotlin
implementation("io.github.shroman:log4j-fluency:${appenderVersion}")
```

### Configure

```
packages = org.apache.logging.log4j.core,io.github.shroman.log4j-fluency

rootLogger.appenderRef.Fluency.ref = Fluency

appender.Fluency.name = Fluency
appender.Fluency.type = Fluency
appender.Fluency.tag = tag1
appender.Fluency.filter.threshold.type = ThresholdFilter
appender.Fluency.filter.threshold.level = WARN

# optionally, for Treasure Data
appender.Fluency.apikey = xxx
```
