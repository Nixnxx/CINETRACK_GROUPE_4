module cinetrack {
    requires java.sql;              // JDBC
    requires org.postgresql.jdbc;   // pilote PostgreSQL (via Maven)
    requires javafx.controls;       // JavaFX
    requires javafx.fxml;           // chargement des fichiers FXML

    exports main;                   // permet à JavaFX de lancer MainFx

    // FXMLLoader crée les contrôleurs et remplit leurs champs @FXML
    opens controleur to javafx.fxml;
}