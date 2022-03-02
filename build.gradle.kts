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

dependencies {
  implementation("org.jboss.shrinkwrap.resolver:shrinkwrap-resolver-depchain:3.1.4")

  testImplementation("org.junit.jupiter:junit-jupiter-api:$junit5Version")
  testImplementation("org.junit.jupiter:junit-jupiter-engine:$junit5Version")
  testImplementation("org.junit.jupiter:junit-jupiter-params:$junit5Version")

  testImplementation("org.assertj:assertj-core:3.22.0")}
