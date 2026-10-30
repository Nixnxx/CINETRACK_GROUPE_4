package viewfx;

import javafx.scene.Scene;

public final class Theme {

    public static final String CSS =
            Theme.class.getResource("/css/theme.css").toExternalForm();

    private Theme() {}

    public static void appliquer(Scene scene) {
        scene.getStylesheets().add(CSS);
    }
}