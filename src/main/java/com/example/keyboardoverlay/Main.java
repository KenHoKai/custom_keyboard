package com.example.keyboardoverlay;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class Main extends Application implements ShortcutHandler {

    private MacroManager manager;
    private Stage lockStage;
    private Label dpiLabel, ipsLabel, speedLabel;

    @Override
    public void start(Stage primaryStage) throws Exception {
        manager = new MacroManager();
        manager.start(); // registers native hook

        // simple main window for debugging/config
        BorderPane root = new BorderPane();
        Button openLock = new Button("Open Lock Overlay");
        openLock.setOnAction(e -> openLockOverlay());
        root.setCenter(openLock);
        Scene scene = new Scene(root, 400, 200);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Keyboard Overlay - Main");
        primaryStage.show();

        // build lock overlay HUD
        buildLockOverlay();
        // pass handler references
        manager.setShortcutHandler(this);
    }

    private void buildLockOverlay() {
        lockStage = new Stage(StageStyle.TRANSPARENT);
        lockStage.setAlwaysOnTop(true);

        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: rgba(0,0,0,0.25);");
        HBox hud = new HBox(12);
        dpiLabel = new Label("DPI: --");
        ipsLabel = new Label("IPS: --");
        speedLabel = new Label("px/s: --");
        hud.getChildren().addAll(dpiLabel, ipsLabel, speedLabel);

        root.setTop(hud);
        Scene s = new Scene(root, 800, 600, Color.TRANSPARENT);
        lockStage.setScene(s);

        // periodic update of HUD
        javafx.animation.AnimationTimer timer = new javafx.animation.AnimationTimer() {
            private long last=0;
            @Override
            public void handle(long now) {
                if (now - last < 100_000_000) return; // ~10Hz
                last = now;
                Platform.runLater(() -> {
                    dpiLabel.setText("DPI: " + (int) manager.getSettings().virtualDPI);
                    ipsLabel.setText(String.format("IPS: %.2f", manager.getSettings().inchesPerSecond));
                    double pxs = manager.getSettings().virtualDPI * manager.getSettings().inchesPerSecond * (manager.isSprinting() ? manager.getSettings().sprintMultiplier : 1.0);
                    speedLabel.setText(String.format("px/s: %.0f", pxs));
                });
            }
        };
        timer.start();
    }

    private void openLockOverlay() {
        if (!lockStage.isShowing()) lockStage.show();
        else lockStage.toFront();
    }

    // ShortcutHandler implementations
    @Override public void toggleEnabled() { manager.toggleEnabled(); }
    @Override public void toggleLockOverlay() { Platform.runLater(() -> { if (lockStage.isShowing()) lockStage.hide(); else lockStage.show(); }); }
    @Override public void openSettingsOverlay() { /* main app includes settings separately - not implemented in this simple zip */ }
    @Override public void minimizeLockOverlay() { Platform.runLater(() -> lockStage.hide()); }
    @Override public void changeDPI(int delta) { manager.changeDPI(delta); }
    @Override public void changeIPS(double delta) { manager.changeIPS(delta); }
    @Override public void toggleApp() { manager.toggleEnabled(); }

    public static void main(String[] args) { launch(args); }
}
