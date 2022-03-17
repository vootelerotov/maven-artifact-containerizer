package io.github.vootelerotov.artifact.testcontainer

import io.github.vootelerotov.artifact.testcontainer.resolver.ResolverCreator
import org.jboss.shrinkwrap.resolver.api.maven.ConfigurableMavenResolverSystem
import org.jboss.shrinkwrap.resolver.api.maven.MavenResolvedArtifact

class ArtfifactTestContainer(repositoryConfig: RepositoryConfig) {

  private val resolver: ConfigurableMavenResolverSystem;

  init {
    resolver = ResolverCreator().createResolver(repositoryConfig)
  }

  companion object ArtfifactTestContainer {

    fun fromArtifact(fullyQualifiedName: String): JavaContainerBuilder =
      ArtfifactTestContainer(RepositoryConfig.DEFAULT).fromArtifact(fullyQualifiedName)

    fun fromArtifact(repositoryConfig: RepositoryConfig, fullyQualifiedArtifactName: String) {
      ArtfifactTestContainer(repositoryConfig).fromArtifact(fullyQualifiedArtifactName)
    }
  }

  fun fromArtifact(fullyQualifiedName: String): JavaContainerBuilder {
    val artifact = resolver
      .resolve(fullyQualifiedName)
      .withoutTransitivity()
      .asSingle(MavenResolvedArtifact::class.java)

    val artifactFile = artifact.asFile()

    val dependencyCoordinates = artifact.dependencies.map { it.coordinate.toCanonicalForm() }

    val transitiveDependencies = if (dependencyCoordinates.isEmpty()) arrayOf() else resolver
      .resolve(dependencyCoordinates)
      .withoutTransitivity()
      .asResolvedArtifact()

    val dependencyFiles = transitiveDependencies.map { it.asFile() }

    return JavaContainerBuilder(artifactFile, dependencyFiles)
  }
}
