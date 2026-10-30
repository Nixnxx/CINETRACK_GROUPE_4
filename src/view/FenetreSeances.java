package view;

import modele.Seance;
import controleur.SeanceControleur;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class FenetreSeances extends JPanel {

    // ── Palette dark ──────────────────────────────────────────
    private static final Color BG_DARK      = new Color(18,  20,  28);
    private static final Color BG_PANEL     = new Color(26,  30,  42);
    private static final Color BG_MID       = new Color(32,  37,  52);
    private static final Color BORDER_COLOR = new Color(50,  57,  78);
    private static final Color BLUE_ACCENT  = new Color(59, 130, 246);
    private static final Color GREEN_BTN    = new Color(34, 120,  60);
    private static final Color RED_BTN      = new Color(180,  40,  40);
    private static final Color GREY_BTN     = new Color(60,  65,  85);
    private static final Color TEXT_PRIMARY = new Color(220, 225, 240);
    private static final Color TEXT_MUTED   = new Color(120, 130, 155);
    private static final Color HEADER_BG    = new Color(22,  26,  38);
    private static final Color SEL_BG       = new Color(40,  70, 140);
    // Places
    private static final Color PLACES_CRIT  = new Color(60,  20,  20);
    private static final Color PLACES_WARN  = new Color(55,  40,  10);
    // ──────────────────────────────────────────────────────────

    private SeanceControleur  controleur;
    private JTable            tableSeances;
    private DefaultTableModel modeleTable;
    private JTextField        champDate;
    private JLabel            labelNbSeances;
    private JButton           btnRechercher, btnAjouter, btnSupprimer, btnRafraichir;

    private static final String[] COLONNES = {"ID","Film","Salle","Date","Heure","Places dispo"};

    public FenetreSeances(JFrame fenetrePrincipale) {
        setLayout(new BorderLayout());
        setBackground(BG_DARK);
        creerComposants();
        controleur = new SeanceControleur(this);
        controleur.chargerToutesSeances();
    }

    private void creerComposants() {
        add(creerBandeauNord(), BorderLayout.NORTH);
        add(creerTableCentrale(), BorderLayout.CENTER);
        add(creerPanneauBoutons(), BorderLayout.SOUTH);
    }

    private JPanel creerBandeauNord() {
        JPanel panneau = new JPanel(new BorderLayout());
        panneau.setBackground(BG_PANEL);
        panneau.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_COLOR),
            BorderFactory.createEmptyBorder(10, 16, 10, 16)
        ));

        labelNbSeances = new JLabel("Séances");
        labelNbSeances.setFont(new Font("Segoe UI", Font.BOLD, 15));
        labelNbSeances.setForeground(TEXT_PRIMARY);

        JPanel filtrePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        filtrePanel.setOpaque(false);

        JLabel lbl = new JLabel("Date (AAAA-MM-JJ) :");
        lbl.setForeground(TEXT_MUTED);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        champDate = new JTextField(LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE), 12);
        champDate.setBackground(BG_MID);
        champDate.setForeground(TEXT_PRIMARY);
        champDate.setCaretColor(TEXT_PRIMARY);
        champDate.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR),
            BorderFactory.createEmptyBorder(3, 7, 3, 7)
        ));
        champDate.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        champDate.addActionListener(e -> controleur.chargerSeancesParDate(champDate.getText().trim()));

        btnRechercher = creerBouton("Rechercher", BLUE_ACCENT);
        btnRechercher.addActionListener(e -> controleur.chargerSeancesParDate(champDate.getText().trim()));

        filtrePanel.add(lbl);
        filtrePanel.add(champDate);
        filtrePanel.add(btnRechercher);

        panneau.add(labelNbSeances, BorderLayout.WEST);
        panneau.add(filtrePanel,    BorderLayout.EAST);
        return panneau;
    }

    private JScrollPane creerTableCentrale() {
        modeleTable = new DefaultTableModel(COLONNES, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        tableSeances = new JTable(modeleTable);
        tableSeances.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tableSeances.setRowHeight(30);
        tableSeances.setBackground(BG_DARK);
        tableSeances.setForeground(TEXT_PRIMARY);
        tableSeances.setSelectionBackground(SEL_BG);
        tableSeances.setSelectionForeground(Color.WHITE);
        tableSeances.setGridColor(BORDER_COLOR);
        tableSeances.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tableSeances.setShowHorizontalLines(true);
        tableSeances.setShowVerticalLines(false);
        tableSeances.setIntercellSpacing(new Dimension(0, 1));

        JTableHeader header = tableSeances.getTableHeader();
        header.setBackground(HEADER_BG);
        header.setForeground(TEXT_MUTED);
        header.setFont(new Font("Segoe UI", Font.BOLD, 11));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_COLOR));
        header.setReorderingAllowed(false);

        tableSeances.getColumnModel().getColumn(0).setPreferredWidth(40);
        tableSeances.getColumnModel().getColumn(1).setPreferredWidth(220);
        tableSeances.getColumnModel().getColumn(2).setPreferredWidth(110);
        tableSeances.getColumnModel().getColumn(3).setPreferredWidth(100);
        tableSeances.getColumnModel().getColumn(4).setPreferredWidth(75);
        tableSeances.getColumnModel().getColumn(5).setPreferredWidth(90);

        tableSeances.setDefaultRenderer(Object.class, new PlacesDarkRenderer());

        tableSeances.getSelectionModel().addListSelectionListener(e ->
            btnSupprimer.setEnabled(tableSeances.getSelectedRow() >= 0)
        );

        JScrollPane scroll = new JScrollPane(tableSeances);
        scroll.setBackground(BG_DARK);
        scroll.getViewport().setBackground(BG_DARK);
        scroll.setBorder(null);
        return scroll;
    }

    private JPanel creerPanneauBoutons() {
        JPanel panneau = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 10));
        panneau.setBackground(BG_PANEL);
        panneau.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, BORDER_COLOR));

        btnAjouter    = creerBouton("＋ Ajouter une séance", GREEN_BTN);
        btnSupprimer  = creerBouton("✕ Supprimer",           RED_BTN);
        btnRafraichir = creerBouton("↺ Rafraîchir",          GREY_BTN);

        btnSupprimer.setEnabled(false);
        btnAjouter.addActionListener(e    -> actionAjouter());
        btnSupprimer.addActionListener(e  -> actionSupprimer());
        btnRafraichir.addActionListener(e -> controleur.chargerToutesSeances());

        panneau.add(btnAjouter);
        panneau.add(btnSupprimer);
        panneau.add(Box.createHorizontalStrut(16));
        panneau.add(btnRafraichir);
        return panneau;
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

    private void actionAjouter() {
        DialogueSeance d = new DialogueSeance((JFrame) SwingUtilities.getWindowAncestor(this), controleur);
        if (d.estConfirme())
            controleur.ajouterSeance(d.getIdFilm(), d.getIdSalle(), d.getDate(), d.getHeure(), d.getPlaces());
    }

    private void actionSupprimer() {
        int ligne = tableSeances.getSelectedRow();
        if (ligne < 0) return;
        int id = (int) modeleTable.getValueAt(ligne, 0);
        String film = (String) modeleTable.getValueAt(ligne, 1);
        if (JOptionPane.showConfirmDialog(this, "Supprimer la séance de \"" + film + "\" ?",
            "Confirmation", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION)
            controleur.supprimerSeance(id);
    }

    public void chargerPlanningAujourdhui() {
        String auj = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE);
        champDate.setText(auj);
        controleur.chargerSeancesParDate(auj);
    }

    public void afficherSeances(List<Seance> seances) {
        modeleTable.setRowCount(0);
        for (Seance s : seances)
            modeleTable.addRow(new Object[]{
                s.getIdSeance(), s.getTitreFilm(), s.getNomSalle(),
                s.getDateSeance(), s.getHeure(), s.getPlacesDispo()
            });
        labelNbSeances.setText("Séances (" + seances.size() + ")");
    }

    public void afficherSucces(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Succès", JOptionPane.INFORMATION_MESSAGE);
    }
    public void afficherErreur(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Erreur", JOptionPane.ERROR_MESSAGE);
    }

    // ── Renderer places (dark) ────────────────────────────────
    private static class PlacesDarkRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(
                JTable t, Object val, boolean sel, boolean focus, int row, int col) {
            super.getTableCellRendererComponent(t, val, sel, focus, row, col);
            setForeground(sel ? Color.WHITE : new Color(210, 215, 235));
            setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 8));
            if (!sel) {
                if (col == 5) {
                    try {
                        int places = Integer.parseInt(val.toString());
                        if      (places == 0)   setBackground(new Color(60, 20, 20));
                        else if (places < 20)   setBackground(new Color(55, 40, 10));
                        else setBackground(row % 2 == 0 ? new Color(18,20,28) : new Color(24,28,40));
                    } catch (NumberFormatException e) {
                        setBackground(row % 2 == 0 ? new Color(18,20,28) : new Color(24,28,40));
                    }
                } else {
                    setBackground(row % 2 == 0 ? new Color(18,20,28) : new Color(24,28,40));
                }
            } else {
                setBackground(new Color(40, 70, 140));
            }
            return this;
        }
    }
}
