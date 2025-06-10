module jetbrains.integradora_ii {
    requires javafx.controls;
    requires javafx.fxml;

    requires javafx.media;

    requires org.kordamp.bootstrapfx.core;

    opens org.icesi.integradora_ii to javafx.fxml;
    exports org.icesi.integradora_ii;
}