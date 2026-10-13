package view;

import modele.Film;
import controleur.FilmControleur;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

public class FenetreFilms extends JPanel {

    // ── Palette dark (reprise de FenetrePrincipale) ───────────
    private static final Color BG_DARK      = new Color(18,  20,  28);
    private static final Color BG_PANEL     = new Color(26,  30,  42);
    private static final Color BG_MID       = new Color(32,  37,  52);
    private static final Color BG_ROW_ALT   = new Color(30,  35,  50);
    private static final Color BORDER_COLOR = new Color(50,  57,  78);
    private static final Color BLUE_ACCENT  = new Color(59, 130, 246);
    private static final Color GREEN_BTN    = new Color(34, 120,  60);
    private static final Color RED_BTN      = new Color(180,  40,  40);
    private static final Color GREY_BTN     = new Color(60,  65,  85);
    private static final Color TEXT_PRIMARY = new Color(220, 225, 240);
    private static final Color TEXT_MUTED   = new Color(120, 130, 155);
    private static final Color HEADER_BG    = new Color(22,  26,  38);
    private static final Color SEL_BG       = new Color(40,  70, 140);
    // ──────────────────────────────────────────────────────────

    private FilmControleur   controleur;
    private JTable           tableFilms;
    private DefaultTableModel modeleTable;
    private JComboBox<String> comboFiltreGenre;
    private JButton          btnAjouter, btnModifier, btnSupprimer, btnRafraichir;
    private JLabel           labelNbFilms;
    private JTextArea        textSynopsis;

    private static final String[] COLONNES = {"ID", "Titre", "Genre", "Durée", "Note", "Synopsis"};
    private static final String[] GENRES_FILTRE = {
        "Tous","Action","Comédie","Drame","Animation",
        "Horreur","Science-Fiction","Thriller","Romance","Documentaire"
    };

    public FenetreFilms(JFrame fenetrePrincipale) {
        setLayout(new BorderLayout());
        setBackground(BG_DARK);
        creerComposants();
        controleur = new FilmControleur(this);
        controleur.chargerFilms();
    }

    private void creerComposants() {
        add(creerBandeauNord(), BorderLayout.NORTH);
        JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
            creerTableCentrale(), creerPanneauSynopsis());
        split.setResizeWeight(0.75);
        split.setDividerSize(4);
        split.setBackground(BG_DARK);
        split.setBorder(null);
        add(split, BorderLayout.CENTER);
        add(creerPanneauBoutons(), BorderLayout.SOUTH);
    }

    private JPanel creerBandeauNord() {
        JPanel panneau = new JPanel(new BorderLayout());
        panneau.setBackground(BG_PANEL);
        panneau.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_COLOR),
            BorderFactory.createEmptyBorder(10, 16, 10, 16)
        ));

        labelNbFilms = new JLabel("Films");
        labelNbFilms.setFont(new Font("Segoe UI", Font.BOLD, 15));
        labelNbFilms.setForeground(TEXT_PRIMARY);

        JPanel filtrePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        filtrePanel.setOpaque(false);
        JLabel lbl = new JLabel("Genre :");
        lbl.setForeground(TEXT_MUTED);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        comboFiltreGenre = new JComboBox<>(GENRES_FILTRE);
        styleCombo(comboFiltreGenre);
        comboFiltreGenre.addActionListener(e -> filtrerParGenre());
        filtrePanel.add(lbl);
        filtrePanel.add(comboFiltreGenre);

        panneau.add(labelNbFilms, BorderLayout.WEST);
        panneau.add(filtrePanel,  BorderLayout.EAST);
        return panneau;
    }

    private JScrollPane creerTableCentrale() {
        modeleTable = new DefaultTableModel(COLONNES, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tableFilms = new JTable(modeleTable);
        tableFilms.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tableFilms.setRowHeight(30);
        tableFilms.setBackground(BG_DARK);
        tableFilms.setForeground(TEXT_PRIMARY);
        tableFilms.setSelectionBackground(SEL_BG);
        tableFilms.setSelectionForeground(Color.WHITE);
        tableFilms.setGridColor(BORDER_COLOR);
        tableFilms.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tableFilms.setShowHorizontalLines(true);
        tableFilms.setShowVerticalLines(false);
        tableFilms.setIntercellSpacing(new Dimension(0, 1));

        JTableHeader header = tableFilms.getTableHeader();
        header.setBackground(HEADER_BG);
        header.setForeground(TEXT_MUTED);
        header.setFont(new Font("Segoe UI", Font.BOLD, 11));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_COLOR));
        header.setReorderingAllowed(false);

        tableFilms.getColumnModel().getColumn(0).setPreferredWidth(40);
        tableFilms.getColumnModel().getColumn(1).setPreferredWidth(220);
        tableFilms.getColumnModel().getColumn(2).setPreferredWidth(110);
        tableFilms.getColumnModel().getColumn(3).setPreferredWidth(80);
        tableFilms.getColumnModel().getColumn(4).setPreferredWidth(60);

        TableColumn colSynopsis = tableFilms.getColumnModel().getColumn(5);
        colSynopsis.setMinWidth(0); colSynopsis.setMaxWidth(0);
        colSynopsis.setPreferredWidth(0); colSynopsis.setResizable(false);

        // Renderer lignes alternées
        tableFilms.setDefaultRenderer(Object.class, new DarkStripedRenderer());

        tableFilms.getSelectionModel().addListSelectionListener(e -> {
            if (e.getValueIsAdjusting()) return;
            int ligne = tableFilms.getSelectedRow();
            boolean sel = ligne >= 0;
            btnModifier.setEnabled(sel);
            btnSupprimer.setEnabled(sel);
            if (sel) {
                String syn = (String) modeleTable.getValueAt(ligne, 5);
                textSynopsis.setText(syn != null && !syn.isBlank() ? syn : "(Aucun synopsis disponible)");
                textSynopsis.setCaretPosition(0);
            } else {
                textSynopsis.setText("");
            }
        });

        JScrollPane scroll = new JScrollPane(tableFilms);
        scroll.setBackground(BG_DARK);
        scroll.getViewport().setBackground(BG_DARK);
        scroll.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_COLOR));
        return scroll;
    }

    private JPanel creerPanneauSynopsis() {
        JPanel panneau = new JPanel(new BorderLayout());
        panneau.setBackground(BG_MID);
        panneau.setBorder(BorderFactory.createEmptyBorder(8, 14, 8, 14));

        JLabel labelTitre = new JLabel("Synopsis");
        labelTitre.setFont(new Font("Segoe UI", Font.BOLD, 11));
        labelTitre.setForeground(TEXT_MUTED);
        labelTitre.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));

        textSynopsis = new JTextArea();
        textSynopsis.setEditable(false);
        textSynopsis.setLineWrap(true);
        textSynopsis.setWrapStyleWord(true);
        textSynopsis.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        textSynopsis.setBackground(BG_MID);
        textSynopsis.setForeground(TEXT_PRIMARY);
        textSynopsis.setCaretColor(TEXT_PRIMARY);
        textSynopsis.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));

        JScrollPane scroll = new JScrollPane(textSynopsis);
        scroll.setBackground(BG_MID);
        scroll.getViewport().setBackground(BG_MID);
        scroll.setBorder(BorderFactory.createLineBorder(BORDER_COLOR));

        panneau.add(labelTitre, BorderLayout.NORTH);
        panneau.add(scroll,     BorderLayout.CENTER);
        return panneau;
    }

    private JPanel creerPanneauBoutons() {
        JPanel panneau = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 10));
        panneau.setBackground(BG_PANEL);
        panneau.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, BORDER_COLOR));

        btnAjouter    = creerBouton("＋ Ajouter",    GREEN_BTN);
        btnModifier   = creerBouton("✎ Modifier",    BLUE_ACCENT);
        btnSupprimer  = creerBouton("✕ Supprimer",   RED_BTN);
        btnRafraichir = creerBouton("↺ Rafraîchir",  GREY_BTN);

        btnModifier.setEnabled(false);
        btnSupprimer.setEnabled(false);

        btnAjouter.addActionListener(e   -> actionAjouter());
        btnModifier.addActionListener(e  -> actionModifier());
        btnSupprimer.addActionListener(e -> actionSupprimer());
        btnRafraichir.addActionListener(e -> controleur.chargerFilms());

        panneau.add(btnAjouter);
        panneau.add(btnModifier);
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

    private void styleCombo(JComboBox<String> combo) {
        combo.setBackground(BG_MID);
        combo.setForeground(TEXT_PRIMARY);
        combo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        combo.setBorder(BorderFactory.createLineBorder(BORDER_COLOR));
    }

    // ── Actions ───────────────────────────────────────────────
    private void actionAjouter() {
        DialogueFilm d = new DialogueFilm((JFrame) SwingUtilities.getWindowAncestor(this), null);
        if (d.estConfirme())
            controleur.ajouterFilm(d.getTitre(), d.getGenre(), d.getDuree(), d.getNote(), d.getSynopsis());
    }

    private void actionModifier() {
        int ligne = tableFilms.getSelectedRow();
        if (ligne < 0) return;
        int id = (int) modeleTable.getValueAt(ligne, 0);
        Film f = new Film();
        f.setIdFilm(id);
        f.setTitre((String) modeleTable.getValueAt(ligne, 1));
        f.setGenre((String) modeleTable.getValueAt(ligne, 2));
        f.setDuree(parseDureeFormatee((String) modeleTable.getValueAt(ligne, 3)));
        f.setNote(Double.parseDouble(modeleTable.getValueAt(ligne, 4).toString().replace(",", ".")));
        f.setSynopsis((String) modeleTable.getValueAt(ligne, 5));
        DialogueFilm d = new DialogueFilm((JFrame) SwingUtilities.getWindowAncestor(this), f);
        if (d.estConfirme())
            controleur.modifierFilm(id, d.getTitre(), d.getGenre(), d.getDuree(), d.getNote(), d.getSynopsis());
    }

    private void actionSupprimer() {
        int ligne = tableFilms.getSelectedRow();
        if (ligne < 0) return;
        controleur.supprimerFilm((int) modeleTable.getValueAt(ligne, 0),
                                  (String) modeleTable.getValueAt(ligne, 1));
    }

    private void filtrerParGenre() {
        String genre = (String) comboFiltreGenre.getSelectedItem();
        if ("Tous".equals(genre)) controleur.chargerFilms();
        else controleur.rechercherParGenre(genre);
    }

    // ── API publique (appelée par le contrôleur) ──────────────
    public void afficherFilms(List<Film> films) {
        modeleTable.setRowCount(0);
        for (Film f : films)
            modeleTable.addRow(new Object[]{
                f.getIdFilm(), f.getTitre(), f.getGenre(),
                f.getDureeFormatee(), String.format("%.1f", f.getNote()), f.getSynopsis()
            });
        textSynopsis.setText("");
        mettreAJourTitre(films.size());
    }

    public void afficherSucces(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Succès", JOptionPane.INFORMATION_MESSAGE);
    }
    public void afficherErreur(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Erreur", JOptionPane.ERROR_MESSAGE);
    }
    public void mettreAJourTitre(int n) { labelNbFilms.setText("Films (" + n + ")"); }
    public boolean demanderConfirmation(String msg) {
        return JOptionPane.showConfirmDialog(this, msg, "Confirmation",
            JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;
    }
    public void ouvrirRechercheGenre() { comboFiltreGenre.requestFocus(); comboFiltreGenre.showPopup(); }

    private int parseDureeFormatee(String s) {
        try {
            int h = 0, m = 0;
            if (s.contains("h")) {
                String[] p = s.split("h");
                h = Integer.parseInt(p[0].trim());
                if (p.length > 1 && p[1].contains("min"))
                    m = Integer.parseInt(p[1].replace("min","").trim());
            } else if (s.contains("min"))
                m = Integer.parseInt(s.replace("min","").trim());
            return h * 60 + m;
        } catch (NumberFormatException e) { return 0; }
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
