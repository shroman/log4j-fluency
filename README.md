[Fluency](https://github.com/komamitsu/fluency) appender.

### Install

#### Gradle

```kotlin
implementation("io.github.shroman:log4j-fluency:${appenderVersion}")
```

### Configure

```
packages = org.apache.logging.log4j.core,io.github.shroman.log4j-fluency

rootLogger.appenderRef.sentry.ref = Fluency

appender.Fluency.name = Fluency
appender.Fluency.type = Fluency
appender.Fluency.tag = tag1
appender.Fluency.filter.threshold.type = ThresholdFilter
appender.Fluency.filter.threshold.level = WARN

# optionally, for Treasure Data
appender.Fluency.apikey = xxx
```
