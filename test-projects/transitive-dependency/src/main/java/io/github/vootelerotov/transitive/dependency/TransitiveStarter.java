package io.github.vootelerotov.transtitive.dependency;

public class TransitiveStarter {

  public static void start(String[] args) {
    String arrayAsString = String.join(" ", args);
    System.out.println("Started: " + (arrayAsString.isEmpty() ? "nothing" : arrayAsString) + "!");
  }

}
