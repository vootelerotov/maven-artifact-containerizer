package io.github.vootelerotov.jar.with.dependencies;

import com.google.common.base.Objects;

public class Main {

  public static void main(String[] args) {
    Objects.equal(null, null); // to have a dependency on a library
    String arrayAsString = String.join(" ", args);
    System.out.println("Started: " + (arrayAsString.isEmpty() ? "nothing" : arrayAsString) + "!");
  }
}
