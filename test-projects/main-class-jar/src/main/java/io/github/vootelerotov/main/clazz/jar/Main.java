package io.github.vootelerotov.main.clazz.jar;

public class Main {

  public static void main(String[] args) {
    String arrayAsString = String.join(" ", args);
    System.out.println("Started: " + (arrayAsString.isEmpty() ? "nothing" : arrayAsString) + "!");
  }
}
