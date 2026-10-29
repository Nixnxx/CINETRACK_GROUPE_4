module cinetrack {
    requires java.desktop;          // Swing (ton ancienne appli)
    requires java.sql;              // JDBC
    requires org.postgresql.jdbc;   // pilote PostgreSQL (via Maven)
    requires javafx.controls;       // JavaFX

    exports main;                   // permet à JavaFX de lancer MainFx
}