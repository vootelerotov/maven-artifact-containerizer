group = "io.github.vootelerotov"
version = "0.1-SNAPSHOT"

plugins {
  kotlin("jvm") version "1.6.10"
}

repositories {
  mavenCentral()
}

tasks.withType(Test::class) {
  useJUnitPlatform()
}

val junit5Version = "5.8.2"
val shrinkwrapVersion = "3.1.4"

dependencies {
  implementation("org.jboss.shrinkwrap.resolver:shrinkwrap-resolver-depchain:$shrinkwrapVersion")

  testImplementation("org.jboss.shrinkwrap.resolver:shrinkwrap-resolver-api-maven-embedded:$shrinkwrapVersion")

  testImplementation("org.junit.jupiter:junit-jupiter-api:$junit5Version")
  testImplementation("org.junit.jupiter:junit-jupiter-engine:$junit5Version")
  testImplementation("org.junit.jupiter:junit-jupiter-params:$junit5Version")

  testImplementation("org.slf4j:slf4j-simple:1.7.36")

  testImplementation("org.assertj:assertj-core:3.22.0")}
