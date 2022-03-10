package io.github.vootelerotov.artifact.testcontainer

import org.jboss.shrinkwrap.resolver.api.maven.embedded.EmbeddedMaven
import org.junit.jupiter.api.Assertions.fail
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS
import org.testcontainers.containers.ContainerLaunchException
import org.testcontainers.containers.GenericContainer
import org.testcontainers.containers.wait.strategy.Wait
import java.io.File
import java.nio.file.Path
import java.time.Duration

internal class ArtfifactTestContainerTest {

  @Nested
  @TestInstance(PER_CLASS)
  inner class RunnableJar {

    @BeforeAll
    fun publishRunnableJarToMavenLocal() { // io.github.vootelerotov.test.projects:main-class-jar:1.0-SNAPSHOT
      publishToMavenLocal(Path.of("test-projects", "main-class-jar", "pom.xml").toFile())
    }

    @Test
    fun fromArtifactInLocalMaven() {
      val container = ArtfifactTestContainer.fromArtifact(
        "io.github.vootelerotov.test.projects:main-class-jar:1.0-SNAPSHOT"
      )
        .build().withLogConsumer { println(it.utf8String) }

      assertThatContainerStarts(container, ".*Started: nothing!.*")
    }

    @Test
    fun withArguments() {
      val container = ArtfifactTestContainer.fromArtifact(
        "io.github.vootelerotov.test.projects:main-class-jar:1.0-SNAPSHOT"
      ).withArgs("to", "test", "this")
        .build().withLogConsumer { println(it.utf8String) }

      assertThatContainerStarts(container, ".*Started: to test this!.*")
    }

  }

  @Nested
  @TestInstance(PER_CLASS)
  inner class JarWithDependencies {

    @BeforeAll
    fun publishJarWithDependenciesToMavenLocal() { // io.github.vootelerotov.test.projects:jar-with-dependencies:1.0-SNAPSHOT
      publishToMavenLocal(Path.of("test-projects", "jar-with-dependencies", "pom.xml").toFile())
    }

    @Test
    fun fromArtifactInLocalMaven() {
      val container = ArtfifactTestContainer.fromArtifact(
        "io.github.vootelerotov.test.projects:jar-with-dependencies:1.0-SNAPSHOT"
      )
        .withClassName("io.github.vootelerotov.jar.with.dependencies.Main")
        .build().withLogConsumer { println(it.utf8String) }

      assertThatContainerStarts(container, ".*Started: nothing!.*")
    }

    @Test
    fun withArguments() {
      val builder = ArtfifactTestContainer.fromArtifact(
        "io.github.vootelerotov.test.projects:jar-with-dependencies:1.0-SNAPSHOT"
      )
        .withClassName("io.github.vootelerotov.jar.with.dependencies.Main")
        .withArgs("to", "test")

      val container = builder.build().withLogConsumer { println(it.utf8String) }

      assertThatContainerStarts(container, ".*Started: to test!.*")
    }

  }

  private fun publishToMavenLocal(pom: File) {
    EmbeddedMaven.forProject(pom).setGoals("clean", "install").build()
  }

  private fun  assertThatContainerStarts(container: GenericContainer<*>, expectedLine: String) {
    try {
      container
        .waitingFor(Wait.forLogMessage(expectedLine, 1).withStartupTimeout(Duration.ofSeconds(1)))
        .use {
          container.start()
        }
    } catch(e: ContainerLaunchException) {
      fail("$expectedLine was not found produced by app under test", e)
    }
  }
}
