# Keyboard Overlay (Java 21) - Maven project
This project provides a JavaFX-based keyboard remapper + WASD-to-mouse mover with virtual DPI and HUD.
Target: **Windows**, Java 21, IntelliJ IDEA.

## Run (development)
1. Open project in IntelliJ (Import Maven project).
2. Run with admin privileges (recommended for SendInput reliability).
3. From terminal:
   mvn clean javafx:run

## Build a runnable jar
mvn clean package
The assembled jar with dependencies will be in `target/keyboard-overlay-1.0-SNAPSHOT-jar-with-dependencies.jar`

## Notes
- Use conservative DPI values when testing.
- If JNA SendInput structures produce compile issues due to JNA versions, update dependency version or report the error to adjust the wrapper.
