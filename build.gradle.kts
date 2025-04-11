plugins {
    java
    `maven-publish`
    signing
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

group = "io.github.shroman"
version = "1.0.4-SNAPSHOT"

repositories {
    mavenCentral()
}

val fluencyVersion = "2.7.3"
val junitVersion = "5.10.3"
val jacksonVersion = "2.18.2"

dependencies {
    implementation("org.apache.logging.log4j:log4j-core:2.24.2")

    implementation("org.komamitsu:fluency-core:$fluencyVersion")
    implementation("org.komamitsu:fluency-fluentd:$fluencyVersion")
    implementation("org.komamitsu:fluency-treasuredata:$fluencyVersion")

    testImplementation("org.junit.jupiter:junit-jupiter-api:$junitVersion")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:$junitVersion")
    testImplementation("com.fasterxml.jackson.core:jackson-databind:$jacksonVersion")
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11

    withJavadocJar()
    withSourcesJar()
}

tasks.withType<Test> {
    useJUnitPlatform()
}

buildscript {
    repositories {
        mavenCentral()
    }
}

val ossrhUsername = project.findProperty("ossrhUsername").toString()
val ossrhPassword = project.findProperty("ossrhPassword").toString()

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])

            pom {
                name.set("log4j-fluency")
                description.set("Log4j Fluency Appender")
                url.set("https://github.com/shroman/log4j-fluency")
                licenses {
                    license {
                        name.set("The Apache License, Version 2.0")
                        url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                    }
                }
                developers {
                    developer {
                        id.set("shroman")
                        name.set("Roman Shtykh")
                    }
                }
                scm {
                    connection.set("scm:git:git://github.com/shroman/log4j-fluency.git")
                    developerConnection.set("scm:git:git@github.com:shroman/log4j-fluency.git")
                    url.set("https://github.com/shroman/log4j-fluency")
                }
            }
        }
    }
    repositories {
        maven {
            name = "OSSRH"

            val releasesRepoUrl = uri("https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/")
            val snapshotsRepoUrl = uri("https://s01.oss.sonatype.org/content/repositories/snapshots/")
            url = if (version.toString().endsWith("SNAPSHOT")) snapshotsRepoUrl else releasesRepoUrl
            credentials {
                username = ossrhUsername
                password = ossrhPassword
            }
        }
    }
}

signing {
    sign(publishing.publications["maven"])
    sign(configurations.archives.get())
}
