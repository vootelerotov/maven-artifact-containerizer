package io.github.vootelerotov.artifact.testcontainer

import io.github.vootelerotov.artifact.testcontainer.JavaContainerBuilder.JavaVersion
import io.github.vootelerotov.artifact.testcontainer.resolver.SettingsXmlWriter
import org.jboss.shrinkwrap.resolver.api.maven.embedded.EmbeddedMaven
import org.junit.jupiter.api.Assertions.fail
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS
import org.testcontainers.containers.ContainerLaunchException
import org.testcontainers.containers.GenericContainer
import org.testcontainers.containers.startupcheck.OneShotStartupCheckStrategy
import org.testcontainers.containers.wait.strategy.HostPortWaitStrategy
import org.testcontainers.containers.wait.strategy.Wait
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import java.io.File
import java.io.FileInputStream
import java.net.URL
import java.nio.file.Path
import java.time.Duration
import java.util.Properties

@Testcontainers
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

  @Nested
  @TestInstance(PER_CLASS)
  inner class JavaVersionPrinter {

    @BeforeAll
    fun publishJarWithDependenciesToMavenLocal() { // io.github.vootelerotov.test.projects:java-version-printer:1.0-SNAPSHOT
      publishToMavenLocal(Path.of("test-projects", "java-version-printer", "pom.xml").toFile())
    }

    @Test
    fun withJava8() {
      val container = ArtfifactTestContainer.fromArtifact(
        "io.github.vootelerotov.test.projects:java-version-printer:1.0-SNAPSHOT"
      )
        .withJavaVersion(JavaVersion.V8)
        .build().withLogConsumer { println(it.utf8String) }

      assertThatContainerStarts(container, ".*Started test app on 1.8.0_\\d+!.*")
    }

    @Test
    fun withJava11AsDefault() {
      val container = ArtfifactTestContainer.fromArtifact(
        "io.github.vootelerotov.test.projects:java-version-printer:1.0-SNAPSHOT"
      )
        .build().withLogConsumer { println(it.utf8String) }

      assertThatContainerStarts(container, ".*Started test app on 11.[\\d\\.]+!.*")
    }

    @Test
    fun withJava17() {
      val container = ArtfifactTestContainer.fromArtifact(
        "io.github.vootelerotov.test.projects:java-version-printer:1.0-SNAPSHOT"
      )
        .withJavaVersion(JavaVersion.V17)
        .build().withLogConsumer { println(it.utf8String) }

      assertThatContainerStarts(container, ".*Started test app on 17.[\\d\\.]+!.*")
    }

  }

  @Nested
  @TestInstance(PER_CLASS)
  inner class RemoteRepository {

    @Container
    val remoteMavenRepositoryContainer = GenericContainer("dzikoysk/reposilite:3.0.0-alpha.22")
      .withExposedPorts(80)
      .waitingFor(HostPortWaitStrategy().withStartupTimeout(Duration.ofSeconds(10)))
      .withEnv("REPOSILITE_OPTS", "--token=test:test")

    @Nested
    @TestInstance(PER_CLASS)
    inner class PublicRepo {

      private val repositoryURL by lazy {
        URL("http://localhost:${remoteMavenRepositoryContainer.getMappedPort(80)}/releases")
      }

      @BeforeEach
      fun deployToPublicRepository() {
        deployToRemoteRepository(
          Path.of("test-projects", "publishable-jar", "pom.xml").toFile(),
          RepositoryConfig.PrivateRemoteRepository(
            "test",
            repositoryURL,
            "test",
            "test"
          )
        )
      }

      @Test
      fun withNonDefaultPublicRepository() {
          val container = ArtfifactTestContainer.fromArtifact(
            RepositoryConfig().withRepository("test", repositoryURL),
            "io.github.vootelerotov.test.projects:publishable-jar:1.0"
          )
            .build().withLogConsumer { println(it.utf8String) }

          assertThatContainerStarts(container, ".*Started!.*")
        }
      }
    }

  private fun publishToMavenLocal(pom: File) {
    EmbeddedMaven.forProject(pom).setGoals("clean", "install").build()
  }

  private fun deployToRemoteRepository(pom: File, repository: RepositoryConfig.RemoteRepository) {
    EmbeddedMaven.forProject(pom).setUserSettingsFile(
      SettingsXmlWriter().writeSettingsXml(RepositoryConfig().withRepository(repository)).toFile()
    )
      .addProperty("altDeploymentRepository","${repository.id}::default::${repository.url}")
      .setGoals("clean", "deploy")
      .build()
  }

  private fun  assertThatContainerStarts(container: GenericContainer<*>, expectedLine: String) {
    try {
      container
        .withStartupCheckStrategy(OneShotStartupCheckStrategy().withTimeout(Duration.ofSeconds(3)))
        .waitingFor(Wait.forLogMessage(expectedLine, 1).withStartupTimeout(Duration.ofSeconds(1)))
        .use {
          container.start()
        }
    } catch(e: ContainerLaunchException) {
      fail("$expectedLine was not found produced by app under test", e)
    }
  }
}
