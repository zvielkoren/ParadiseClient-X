package net.paradise_client.wallpaper;

public enum Theme {
  MATRIX("Matrix"), PARTICLE("Particle"), LEGACY("Legacy");

  private final String name;

  Theme(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }
}
