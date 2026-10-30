package view;

import controleur.SeanceControleur;
import modele.Film;
import modele.Salle;
import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class DialogueSeance extends JDialog {

    // ── Palette dark ──────────────────────────────────────────
    private static final Color BG_DARK      = new Color(18,  20,  28);
    private static final Color BG_PANEL     = new Color(26,  30,  42);
    private static final Color BG_FIELD     = new Color(35,  40,  55);
    private static final Color BORDER_COLOR = new Color(50,  57,  78);
    private static final Color BLUE_ACCENT  = new Color(59, 130, 246);
    private static final Color GREY_BTN     = new Color(60,  65,  85);
    private static final Color TEXT_PRIMARY = new Color(220, 225, 240);
    private static final Color TEXT_MUTED   = new Color(120, 130, 155);
    // ──────────────────────────────────────────────────────────

    private JComboBox<String>  comboFilm;
    private JComboBox<String>  comboSalle;
    private JTextField         champDate;
    private JTextField         champHeure;
    private JTextField         champPlaces;
    private boolean            confirme = false;
    private SeanceControleur   controleur;

    public DialogueSeance(JFrame parent, SeanceControleur controleur) {
        super(parent, "Ajouter une séance", true);
        this.controleur = controleur;
        initialiserComposants();
        chargerListesCombo();
        setSize(460, 370);
        setLocationRelativeTo(parent);
        setResizable(false);
        getContentPane().setBackground(BG_DARK);
        setVisible(true);
    }

    private void initialiserComposants() {
        JPanel panneau = new JPanel(new GridBagLayout());
        panneau.setBackground(BG_PANEL);
        panneau.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_COLOR),
            BorderFactory.createEmptyBorder(18, 22, 12, 22)
        ));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(7, 5, 7, 5);
        gbc.fill   = GridBagConstraints.HORIZONTAL;

        // Film
        gbc.gridx = 0; gbc.gridy = 0;
        panneau.add(creerLabel("Film"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        comboFilm = new JComboBox<>();
        styleCombo(comboFilm);
        panneau.add(comboFilm, gbc);

        // Salle
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
        panneau.add(creerLabel("Salle"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        comboSalle = new JComboBox<>();
        styleCombo(comboSalle);
        panneau.add(comboSalle, gbc);

        // Date
        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0;
        panneau.add(creerLabel("Date (AAAA-MM-JJ)"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        champDate = creerChamp(LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE));
        panneau.add(champDate, gbc);

        // Heure
        gbc.gridx = 0; gbc.gridy = 3; gbc.weightx = 0;
        panneau.add(creerLabel("Heure (HH:MM)"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        champHeure = creerChamp("20:00");
        panneau.add(champHeure, gbc);

        // Places
        gbc.gridx = 0; gbc.gridy = 4; gbc.weightx = 0;
        panneau.add(creerLabel("Places disponibles"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        champPlaces = creerChamp("100");
        panneau.add(champPlaces, gbc);

        // Boutons
        JPanel panneauBoutons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 10));
        panneauBoutons.setBackground(BG_DARK);

        JButton btnAnnuler = new JButton("Annuler");
        btnAnnuler.setBackground(GREY_BTN);
        btnAnnuler.setForeground(TEXT_PRIMARY);
        btnAnnuler.setFocusPainted(false);
        btnAnnuler.setBorderPainted(false);
        btnAnnuler.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btnAnnuler.setPreferredSize(new Dimension(90, 32));
        btnAnnuler.addActionListener(e -> dispose());

        JButton btnValider = new JButton("Valider");
        btnValider.setBackground(BLUE_ACCENT);
        btnValider.setForeground(Color.WHITE);
        btnValider.setFocusPainted(false);
        btnValider.setBorderPainted(false);
        btnValider.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnValider.setPreferredSize(new Dimension(90, 32));
        btnValider.addActionListener(e -> { confirme = true; dispose(); });

        panneauBoutons.add(btnAnnuler);
        panneauBoutons.add(btnValider);

        setLayout(new BorderLayout());
        add(panneau,        BorderLayout.CENTER);
        add(panneauBoutons, BorderLayout.SOUTH);
    }

    private JLabel creerLabel(String texte) {
        JLabel l = new JLabel(texte + " :");
        l.setForeground(TEXT_MUTED);
        l.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        return l;
    }

    private JTextField creerChamp(String valeurDefaut) {
        JTextField f = new JTextField(valeurDefaut);
        f.setBackground(BG_FIELD);
        f.setForeground(TEXT_PRIMARY);
        f.setCaretColor(TEXT_PRIMARY);
        f.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR),
            BorderFactory.createEmptyBorder(4, 8, 4, 8)
        ));
        f.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        return f;
    }

    private void styleCombo(JComboBox<String> combo) {
        combo.setBackground(BG_FIELD);
        combo.setForeground(TEXT_PRIMARY);
        combo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        combo.setBorder(BorderFactory.createLineBorder(BORDER_COLOR));
    }

    private void chargerListesCombo() {
        List<Film> films = controleur.getTousFilms();
        for (Film f : films) comboFilm.addItem(f.getIdFilm() + " - " + f.getTitre());
        List<Salle> salles = controleur.getToutesSalles();
        for (Salle s : salles) comboSalle.addItem(s.getIdSalle() + " - " + s.getNom());
    }

    public boolean estConfirme() { return confirme; }

    public int getIdFilm() {
        int idx = comboFilm.getSelectedIndex();
        List<Film> films = controleur.getTousFilms();
        return (idx >= 0 && idx < films.size()) ? films.get(idx).getIdFilm() : -1;
    }

    public int getIdSalle() {
        int idx = comboSalle.getSelectedIndex();
        List<Salle> salles = controleur.getToutesSalles();
        return (idx >= 0 && idx < salles.size()) ? salles.get(idx).getIdSalle() : -1;
    }

    public String getDate()   { return champDate.getText().trim(); }
    public String getHeure()  { return champHeure.getText().trim(); }
    public String getPlaces() { return champPlaces.getText().trim(); }
}
