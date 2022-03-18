package io.github.vootelerotov.maven.artifact.containerizer

import java.net.URL
import java.nio.file.Path

class RepositoryConfig {

  companion object {
    internal val DEFAULT = RepositoryConfig()
  }

  internal var localRepositoryPath: Path? = null
  private val remoteRepositories : MutableList<RemoteRepository> = mutableListOf()

  internal fun getRemoteRepositories() : List<RemoteRepository> = remoteRepositories

  fun withLocalRepository(path: Path) = apply {
    localRepositoryPath = path
  }

  fun withRepository(id: String, url: URL) = apply {
    remoteRepositories.add(PublicRemoteRepository(id, url))
  }

  fun withRepository(id: String, url: URL, username: String, password: String) = apply {
    remoteRepositories.add(PrivateRemoteRepository(id, url, username, password))
  }

  internal fun withRepository(repository: RemoteRepository) = apply {
    remoteRepositories.add(repository)
  }

  internal sealed class RemoteRepository(val id: String, val url: URL)
  internal class PublicRemoteRepository(id: String, url: URL): RemoteRepository(id, url)
  internal class PrivateRemoteRepository(id: String, url: URL, val username: String, val password: String): RemoteRepository(id, url)

}
