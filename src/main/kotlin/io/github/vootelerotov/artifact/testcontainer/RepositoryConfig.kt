package io.github.vootelerotov.artifact.testcontainer

import java.net.URL
import java.nio.file.Path

class RepositoryConfig {

  companion object {
    internal val DEFAULT = RepositoryConfig()
  }

  private var localRepositoryPath: Path? = null
  private val remoteRepositories : MutableList<RemoteRepository> = mutableListOf()

  internal fun getRemoteRepositories() : List<RemoteRepository> = remoteRepositories

  fun withRepository(id: String, url: URL) = apply {
    remoteRepositories.add(PublicRemoteRepository(id, url))
  }

  internal fun withRepository(repository: RemoteRepository) = apply {
    remoteRepositories.add(repository)
  }

  internal sealed class RemoteRepository(val id: String, val url: URL)
  internal class PublicRemoteRepository(id: String, url: URL): RemoteRepository(id, url)
  internal class PrivateRemoteRepository(id: String, url: URL, val username: String, val password: String): RemoteRepository(id, url)

}
