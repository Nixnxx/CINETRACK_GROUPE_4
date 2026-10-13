package view;

import connexion.DatabaseConnection;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.sql.*;

public class FenetreLogs extends JPanel {

    // ── Palette dark ──────────────────────────────────────────
    private static final Color BG_DARK      = new Color(18,  20,  28);
    private static final Color BG_PANEL     = new Color(26,  30,  42);
    private static final Color BG_MID       = new Color(32,  37,  52);
    private static final Color BG_FIELD     = new Color(35,  40,  55);
    private static final Color BORDER_COLOR = new Color(50,  57,  78);
    private static final Color BLUE_ACCENT  = new Color(59, 130, 246);
    private static final Color GREY_BTN     = new Color(60,  65,  85);
    private static final Color TEXT_PRIMARY = new Color(220, 225, 240);
    private static final Color TEXT_MUTED   = new Color(120, 130, 155);
    private static final Color HEADER_BG    = new Color(22,  26,  38);
    private static final Color SEL_BG       = new Color(40,  70, 140);
    // Couleurs log par action (version dark)
    private static final Color ROW_INSERT   = new Color(20,  45,  25);
    private static final Color ROW_UPDATE   = new Color(18,  35,  65);
    private static final Color ROW_DELETE   = new Color(55,  18,  18);
    private static final Color ROW_CONN     = new Color(45,  38,  12);
    // ──────────────────────────────────────────────────────────

    private JTable            tableLogs;
    private DefaultTableModel modeleTable;
    private JLabel            labelNbLogs;
    private JComboBox<String> comboFiltreAction;
    private JTextField        champFiltreTable;
    private JButton           btnRafraichir, btnEffacerFiltre;

    private static final String[] COLONNES = {
        "ID","Table","Action","ID cible","Détail","Effectué par","Date"
    };
    private static final String[] ACTIONS_FILTRE = {
        "Toutes","INSERT","UPDATE","DELETE","CONNEXION","RECHERCHE"
    };

    public FenetreLogs() {
        setLayout(new BorderLayout());
        setBackground(BG_DARK);
        creerComposants();
        chargerLogs(null, null);
    }

    private void creerComposants() {
        add(creerBandeauNord(), BorderLayout.NORTH);
        add(creerTableCentrale(), BorderLayout.CENTER);
        add(creerPanneauBas(), BorderLayout.SOUTH);
    }

    private JPanel creerBandeauNord() {
        JPanel panneau = new JPanel(new BorderLayout());
        panneau.setBackground(BG_PANEL);
        panneau.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_COLOR),
            BorderFactory.createEmptyBorder(10, 16, 10, 16)
        ));

        labelNbLogs = new JLabel("Logs d'activité");
        labelNbLogs.setFont(new Font("Segoe UI", Font.BOLD, 15));
        labelNbLogs.setForeground(TEXT_PRIMARY);

        JPanel filtresPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        filtresPanel.setOpaque(false);

        filtresPanel.add(creerLabel("Table :"));
        champFiltreTable = new JTextField(8);
        champFiltreTable.setBackground(BG_FIELD);
        champFiltreTable.setForeground(TEXT_PRIMARY);
        champFiltreTable.setCaretColor(TEXT_PRIMARY);
        champFiltreTable.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR),
            BorderFactory.createEmptyBorder(3, 7, 3, 7)
        ));
        champFiltreTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        champFiltreTable.addActionListener(e -> appliquerFiltres());
        filtresPanel.add(champFiltreTable);

        filtresPanel.add(creerLabel("Action :"));
        comboFiltreAction = new JComboBox<>(ACTIONS_FILTRE);
        comboFiltreAction.setBackground(BG_FIELD);
        comboFiltreAction.setForeground(TEXT_PRIMARY);
        comboFiltreAction.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        comboFiltreAction.setBorder(BorderFactory.createLineBorder(BORDER_COLOR));
        comboFiltreAction.addActionListener(e -> appliquerFiltres());
        filtresPanel.add(comboFiltreAction);

        btnEffacerFiltre = creerBouton("Tout afficher", GREY_BTN);
        btnEffacerFiltre.addActionListener(e -> {
            champFiltreTable.setText("");
            comboFiltreAction.setSelectedIndex(0);
            chargerLogs(null, null);
        });
        filtresPanel.add(btnEffacerFiltre);

        panneau.add(labelNbLogs,  BorderLayout.WEST);
        panneau.add(filtresPanel, BorderLayout.EAST);
        return panneau;
    }

    private JLabel creerLabel(String texte) {
        JLabel l = new JLabel(texte);
        l.setForeground(TEXT_MUTED);
        l.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        return l;
    }

    private JScrollPane creerTableCentrale() {
        modeleTable = new DefaultTableModel(COLONNES, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        tableLogs = new JTable(modeleTable);
        tableLogs.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tableLogs.setRowHeight(28);
        tableLogs.setBackground(BG_DARK);
        tableLogs.setForeground(TEXT_PRIMARY);
        tableLogs.setSelectionBackground(SEL_BG);
        tableLogs.setSelectionForeground(Color.WHITE);
        tableLogs.setGridColor(BORDER_COLOR);
        tableLogs.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tableLogs.setShowHorizontalLines(true);
        tableLogs.setShowVerticalLines(false);
        tableLogs.setIntercellSpacing(new Dimension(0, 1));

        JTableHeader header = tableLogs.getTableHeader();
        header.setBackground(HEADER_BG);
        header.setForeground(TEXT_MUTED);
        header.setFont(new Font("Segoe UI", Font.BOLD, 11));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_COLOR));
        header.setReorderingAllowed(false);

        tableLogs.getColumnModel().getColumn(0).setPreferredWidth(45);
        tableLogs.getColumnModel().getColumn(1).setPreferredWidth(70);
        tableLogs.getColumnModel().getColumn(2).setPreferredWidth(85);
        tableLogs.getColumnModel().getColumn(3).setPreferredWidth(60);
        tableLogs.getColumnModel().getColumn(4).setPreferredWidth(380);
        tableLogs.getColumnModel().getColumn(5).setPreferredWidth(110);
        tableLogs.getColumnModel().getColumn(6).setPreferredWidth(145);

        tableLogs.setDefaultRenderer(Object.class, new LogActionRenderer());

        JScrollPane scroll = new JScrollPane(tableLogs);
        scroll.setBackground(BG_DARK);
        scroll.getViewport().setBackground(BG_DARK);
        scroll.setBorder(null);
        return scroll;
    }

    private JPanel creerPanneauBas() {
        JPanel panneau = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 10));
        panneau.setBackground(BG_PANEL);
        panneau.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, BORDER_COLOR));

        btnRafraichir = creerBouton("↺ Rafraîchir", GREY_BTN);
        btnRafraichir.addActionListener(e -> appliquerFiltres());
        panneau.add(btnRafraichir);

        // Légende
        panneau.add(Box.createHorizontalStrut(20));
        panneau.add(creerBadge("INSERT", new Color(34,120,60)));
        panneau.add(creerBadge("UPDATE", BLUE_ACCENT));
        panneau.add(creerBadge("DELETE", new Color(180,40,40)));
        panneau.add(creerBadge("CONNEXION", new Color(180,140,20)));
        return panneau;
    }

    private JLabel creerBadge(String texte, Color couleur) {
        JLabel badge = new JLabel("  " + texte + "  ");
        badge.setFont(new Font("Segoe UI", Font.BOLD, 10));
        badge.setForeground(couleur);
        badge.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(couleur, 1),
            BorderFactory.createEmptyBorder(2, 4, 2, 4)
        ));
        return badge;
    }

    private JButton creerBouton(String texte, Color couleur) {
        JButton btn = new JButton(texte);
        btn.setBackground(couleur);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(btn.getPreferredSize().width + 10, 32));
        return btn;
    }

    private void appliquerFiltres() {
        String table  = champFiltreTable.getText().trim();
        String action = (String) comboFiltreAction.getSelectedItem();
        chargerLogs(table.isEmpty() ? null : table, "Toutes".equals(action) ? null : action);
    }

    private void chargerLogs(String filtreTable, String filtreAction) {
        modeleTable.setRowCount(0);
        StringBuilder sql = new StringBuilder(
            "SELECT id_log, table_cible, action, id_cible, detail, effectue_par, date_formatee " +
            "FROM v_logs_recents WHERE 1=1"
        );
        if (filtreTable  != null) sql.append(" AND LOWER(table_cible) LIKE LOWER(?)");
        if (filtreAction != null) sql.append(" AND action = ?");

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
            int idx = 1;
            if (filtreTable  != null) stmt.setString(idx++, "%" + filtreTable + "%");
            if (filtreAction != null) stmt.setString(idx,   filtreAction);
            ResultSet rs = stmt.executeQuery();
            int count = 0;
            while (rs.next()) {
                modeleTable.addRow(new Object[]{
                    rs.getInt("id_log"),
                    rs.getString("table_cible"),
                    rs.getString("action"),
                    rs.getObject("id_cible"),
                    rs.getString("detail"),
                    rs.getString("effectue_par"),
                    rs.getString("date_formatee")
                });
                count++;
            }
            labelNbLogs.setText("Logs d'activité (" + count + ")");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                "Erreur chargement des logs :\n" + e.getMessage(),
                "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ── Renderer couleur par action (dark) ────────────────────
    private static class LogActionRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(
                JTable t, Object val, boolean sel, boolean focus, int row, int col) {
            super.getTableCellRendererComponent(t, val, sel, focus, row, col);
            setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 8));
            setForeground(sel ? Color.WHITE : new Color(210, 215, 235));
            if (!sel) {
                String action = "";
                try { action = (String) t.getModel().getValueAt(row, 2); } catch (Exception ignored) {}
                if (action == null) action = "";
                switch (action) {
                    case "INSERT"    -> setBackground(new Color(20, 45, 25));
                    case "UPDATE"    -> setBackground(new Color(18, 35, 65));
                    case "DELETE"    -> setBackground(new Color(55, 18, 18));
                    case "CONNEXION" -> setBackground(new Color(45, 38, 12));
                    default          -> setBackground(row % 2 == 0
                        ? new Color(18, 20, 28) : new Color(24, 28, 40));
                }
            } else {
                setBackground(new Color(40, 70, 140));
            }
            return this;
        }
    }
}
