package view;

import controleur.UtilisateurControleur;
import modele.Utilisateur;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.List;

/*
  FenetreUtilisateurs – écran réservé à l'admin : créer / supprimer les comptes gestionnaires.
  Même charte graphique que FenetreFilms.
 */
public class FenetreUtilisateurs extends JPanel {

    // ── Palette dark (reprise de FenetreFilms) ────────────────
    private static final Color BG_DARK      = new Color(18,  20,  28);
    private static final Color BG_PANEL     = new Color(26,  30,  42);
    private static final Color BG_MID       = new Color(32,  37,  52);
    private static final Color BG_FIELD     = new Color(38,  43,  58);
    private static final Color BORDER_COLOR = new Color(50,  57,  78);
    private static final Color GREEN_BTN    = new Color(34, 120,  60);
    private static final Color RED_BTN      = new Color(180,  40,  40);
    private static final Color GREY_BTN     = new Color(60,  65,  85);
    private static final Color TEXT_PRIMARY = new Color(220, 225, 240);
    private static final Color TEXT_MUTED   = new Color(120, 130, 155);
    private static final Color HEADER_BG    = new Color(22,  26,  38);
    private static final Color SEL_BG       = new Color(40,  70, 140);
    private static final Color GOLD_ADMIN   = new Color(250, 180,  50);
    // ──────────────────────────────────────────────────────────

    private static final String[] COLONNES = {"ID", "Identifiant", "Créé le"};

    private UtilisateurControleur controleur;
    private JTable                tableUtilisateurs;
    private DefaultTableModel     modeleTable;
    private JLabel                labelTitre;
    private JTextField            champLogin;
    private JPasswordField        champMotDePasse;
    private JPasswordField        champConfirmation;
    private JButton               btnAjouter, btnSupprimer, btnRafraichir;

    public FenetreUtilisateurs() {
        setLayout(new BorderLayout());
        setBackground(BG_DARK);
        creerComposants();
        controleur = new UtilisateurControleur(this);
        controleur.chargerGestionnaires();
    }

    private void creerComposants() {
        add(creerBandeauNord(),    BorderLayout.NORTH);
        add(creerTableCentrale(),  BorderLayout.CENTER);
        add(creerPanneauBas(),     BorderLayout.SOUTH);
    }

    // ── Bandeau titre ─────────────────────────────────────────
    private JPanel creerBandeauNord() {
        JPanel panneau = new JPanel(new BorderLayout());
        panneau.setBackground(BG_PANEL);
        panneau.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_COLOR),
            BorderFactory.createEmptyBorder(10, 16, 10, 16)
        ));

        labelTitre = new JLabel("Gestionnaires");
        labelTitre.setFont(new Font("Segoe UI", Font.BOLD, 15));
        labelTitre.setForeground(TEXT_PRIMARY);

        JLabel info = new JLabel("⚙  Administration");
        info.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        info.setForeground(GOLD_ADMIN);

        panneau.add(labelTitre, BorderLayout.WEST);
        panneau.add(info,       BorderLayout.EAST);
        return panneau;
    }

    // ── Table ─────────────────────────────────────────────────
    private JScrollPane creerTableCentrale() {
        modeleTable = new DefaultTableModel(COLONNES, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tableUtilisateurs = new JTable(modeleTable);
        tableUtilisateurs.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tableUtilisateurs.setRowHeight(30);
        tableUtilisateurs.setBackground(BG_DARK);
        tableUtilisateurs.setForeground(TEXT_PRIMARY);
        tableUtilisateurs.setSelectionBackground(SEL_BG);
        tableUtilisateurs.setSelectionForeground(Color.WHITE);
        tableUtilisateurs.setGridColor(BORDER_COLOR);
        tableUtilisateurs.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tableUtilisateurs.setShowHorizontalLines(true);
        tableUtilisateurs.setShowVerticalLines(false);
        tableUtilisateurs.setIntercellSpacing(new Dimension(0, 1));

        JTableHeader header = tableUtilisateurs.getTableHeader();
        header.setBackground(HEADER_BG);
        header.setForeground(TEXT_MUTED);
        header.setFont(new Font("Segoe UI", Font.BOLD, 11));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_COLOR));
        header.setReorderingAllowed(false);

        tableUtilisateurs.getColumnModel().getColumn(0).setPreferredWidth(50);
        tableUtilisateurs.getColumnModel().getColumn(1).setPreferredWidth(300);
        tableUtilisateurs.getColumnModel().getColumn(2).setPreferredWidth(180);

        tableUtilisateurs.setDefaultRenderer(Object.class, new DarkStripedRenderer());

        tableUtilisateurs.getSelectionModel().addListSelectionListener(e -> {
            if (e.getValueIsAdjusting()) return;
            btnSupprimer.setEnabled(tableUtilisateurs.getSelectedRow() >= 0);
        });

        JScrollPane scroll = new JScrollPane(tableUtilisateurs);
        scroll.setBackground(BG_DARK);
        scroll.getViewport().setBackground(BG_DARK);
        scroll.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_COLOR));
        return scroll;
    }

    // ── Bas : formulaire de création + boutons ────────────────
    private JPanel creerPanneauBas() {
        JPanel bas = new JPanel(new BorderLayout());
        bas.setBackground(BG_PANEL);
        bas.add(creerFormulaire(),      BorderLayout.NORTH);
        bas.add(creerPanneauBoutons(),  BorderLayout.SOUTH);
        return bas;
    }

    private JPanel creerFormulaire() {
        JPanel form = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 10));
        form.setBackground(BG_MID);
        form.setBorder(BorderFactory.createEmptyBorder(2, 8, 2, 8));

        champLogin        = new JTextField(10);
        champMotDePasse   = new JPasswordField(10);
        champConfirmation = new JPasswordField(10);

        styleChamp(champLogin);
        styleChamp(champMotDePasse);
        styleChamp(champConfirmation);

        btnAjouter = creerBouton("＋ Ajouter", GREEN_BTN);
        btnAjouter.addActionListener(e -> actionAjouter());
        champConfirmation.addActionListener(e -> actionAjouter());   // touche Entrée

        form.add(creerLabel("Identifiant :"));
        form.add(champLogin);
        form.add(creerLabel("Mot de passe :"));
        form.add(champMotDePasse);
        form.add(creerLabel("Confirmation :"));
        form.add(champConfirmation);
        form.add(btnAjouter);
        return form;
    }

    private JPanel creerPanneauBoutons() {
        JPanel panneau = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 10));
        panneau.setBackground(BG_PANEL);
        panneau.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, BORDER_COLOR));

        btnSupprimer  = creerBouton("✕ Supprimer",  RED_BTN);
        btnRafraichir = creerBouton("↺ Rafraîchir", GREY_BTN);
        btnSupprimer.setEnabled(false);

        btnSupprimer.addActionListener(e  -> actionSupprimer());
        btnRafraichir.addActionListener(e -> controleur.chargerGestionnaires());

        panneau.add(btnSupprimer);
        panneau.add(Box.createHorizontalStrut(16));
        panneau.add(btnRafraichir);
        return panneau;
    }

    // ── Helpers de style ──────────────────────────────────────
    private JLabel creerLabel(String texte) {
        JLabel l = new JLabel(texte);
        l.setForeground(TEXT_MUTED);
        l.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        return l;
    }

    private void styleChamp(JTextField f) {
        f.setBackground(BG_FIELD);
        f.setForeground(TEXT_PRIMARY);
        f.setCaretColor(TEXT_PRIMARY);
        f.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        f.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR),
            BorderFactory.createEmptyBorder(4, 8, 4, 8)
        ));
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

    // ── Actions ───────────────────────────────────────────────
    private void actionAjouter() {
        controleur.ajouterGestionnaire(
            champLogin.getText(),
            new String(champMotDePasse.getPassword()),
            new String(champConfirmation.getPassword()));
    }

    private void actionSupprimer() {
        int ligne = tableUtilisateurs.getSelectedRow();
        if (ligne < 0) return;
        controleur.supprimerGestionnaire(
            (int) modeleTable.getValueAt(ligne, 0),
            (String) modeleTable.getValueAt(ligne, 1));
    }

    // ── API publique (appelée par le contrôleur / la fenêtre principale) ──
    public void rafraichir() {
        controleur.chargerGestionnaires();
    }

    public void afficherGestionnaires(List<Utilisateur> utilisateurs) {
        modeleTable.setRowCount(0);
        for (Utilisateur u : utilisateurs)
            modeleTable.addRow(new Object[]{
                u.getIdUtilisateur(), u.getLogin(), u.getDateCreationFormatee()
            });
        labelTitre.setText("Gestionnaires (" + utilisateurs.size() + ")");
        btnSupprimer.setEnabled(false);
    }

    public void viderFormulaire() {
        champLogin.setText("");
        champMotDePasse.setText("");
        champConfirmation.setText("");
        champLogin.requestFocus();
    }

    public void afficherSucces(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Succès", JOptionPane.INFORMATION_MESSAGE);
    }

    public void afficherErreur(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Erreur", JOptionPane.ERROR_MESSAGE);
    }

    public boolean demanderConfirmation(String msg) {
        return JOptionPane.showConfirmDialog(this, msg, "Confirmation",
            JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;
    }

    // ── Renderer lignes alternées dark ────────────────────────
    private static class DarkStripedRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(
                JTable t, Object val, boolean sel, boolean focus, int row, int col) {
            super.getTableCellRendererComponent(t, val, sel, focus, row, col);
            setForeground(sel ? Color.WHITE : new Color(210, 215, 235));
            if (!sel) setBackground(row % 2 == 0 ? new Color(18, 20, 28) : new Color(24, 28, 40));
            else       setBackground(new Color(40, 70, 140));
            setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 8));
            return this;
        }
    }
}
