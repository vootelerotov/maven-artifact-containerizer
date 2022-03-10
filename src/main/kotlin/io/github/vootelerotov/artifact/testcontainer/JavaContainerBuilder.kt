package io.github.vootelerotov.artifact.testcontainer

import org.testcontainers.containers.GenericContainer
import org.testcontainers.images.builder.ImageFromDockerfile
import java.io.File

class JavaContainerBuilder(private val artifact: File, private val dependencies: List<File>) {

  private var className: String? = null
  private var args : Array<out String> = emptyArray()

  fun withClassName(fullyQualifiedClassName: String): JavaContainerBuilder {
    this.className = fullyQualifiedClassName
    return this
  }

  fun withArgs(vararg args: String): JavaContainerBuilder {
    this.args = args
    return this
  }

  fun build(): GenericContainer<*> {

    val image = ImageFromDockerfile()
    image.withFileFromFile(artifact.name, artifact)
    dependencies.forEach { image.withFileFromFile("libs/${it.name}", it) }

    val completeImage = image.withDockerfileFromBuilder { builder ->
      builder
        .from("openjdk:17")
        .workDir("java-artifact")
        .copy(".", ".")
        .cmd(*buildCommand(className, args))
    }

    return GenericContainer(completeImage)
  }


  private fun buildCommand(className: String?, args: Array<out String>): Array<String> {
    val baseCommand = if (className != null) {
      arrayOf("java", "-cp", "*:libs/*", className)
    } else {
      arrayOf("java", "-jar", artifact.name)
    }
    return baseCommand + args
  }


}
