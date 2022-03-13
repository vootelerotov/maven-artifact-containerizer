package io.github.vootelerotov.artifact.testcontainer

import io.github.vootelerotov.artifact.testcontainer.resolver.DefaultResolverProvider
import io.github.vootelerotov.artifact.testcontainer.resolver.ResolverProvider
import org.jboss.shrinkwrap.resolver.api.maven.MavenResolvedArtifact

class ArtfifactTestContainer(private val resolver: ResolverProvider) {

  companion object ArtfifactTestContainer {

    fun fromArtifact(fullyQualifiedName: String): JavaContainerBuilder =
      ArtfifactTestContainer(DefaultResolverProvider()).fromArtifact(fullyQualifiedName)

  }

  fun fromArtifact(fullyQualifiedName: String): JavaContainerBuilder {
    val artifact = resolver.resolver()
      .resolve(fullyQualifiedName)
      .withoutTransitivity()
      .asSingle(MavenResolvedArtifact::class.java)

    val artifactFile = artifact.asFile()

    val dependencyCoordinates = artifact.dependencies.map { it.coordinate.toCanonicalForm() }

    val transitiveDependencies = if (dependencyCoordinates.isEmpty()) arrayOf() else resolver.resolver()
      .resolve(dependencyCoordinates)
      .withoutTransitivity()
      .asResolvedArtifact()

    val dependencyFiles = transitiveDependencies.map { it.asFile() }

    return JavaContainerBuilder(artifactFile, dependencyFiles)
  }
}
