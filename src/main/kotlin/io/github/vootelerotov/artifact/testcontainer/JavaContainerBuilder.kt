package io.github.vootelerotov.artifact.testcontainer

import org.testcontainers.containers.GenericContainer
import org.testcontainers.images.builder.ImageFromDockerfile
import java.io.File

class JavaContainerBuilder(private val artifact: File, private val dependencies: List<File>) {

  fun build(): GenericContainer<*> {

    val image = ImageFromDockerfile()
    image.withFileFromFile(artifact.name, artifact)
    dependencies.forEach { image.withFileFromFile("libs/${it.name}", it) }

    val completeImage = image.withDockerfileFromBuilder { builder ->
      builder
        .from("openjdk:17")
        .workDir("java-artifact")
        .copy(".", ".")
        .cmd("java", "-jar", artifact.name)
    }

    return GenericContainer(completeImage)
  }

}
