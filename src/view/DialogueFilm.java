package view;

import modele.Film;
import javax.swing.*;
import java.awt.*;

public class DialogueFilm extends JDialog {

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

    private JTextField      champTitre;
    private JComboBox<String> comboGenre;
    private JTextField      champDuree;
    private JTextField      champNote;
    private JTextArea       champSynopsis;
    private boolean         confirme = false;

    private static final String[] GENRES = {
        "Action","Comédie","Drame","Animation",
        "Horreur","Science-Fiction","Thriller","Romance","Documentaire"
    };

    public DialogueFilm(JFrame parent, Film filmExistant) {
        super(parent, filmExistant == null ? "Ajouter un film" : "Modifier le film", true);
        initialiserComposants();
        if (filmExistant != null) preRemplir(filmExistant);
        setSize(480, 440);
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
        gbc.insets = new Insets(6, 5, 6, 5);
        gbc.fill   = GridBagConstraints.HORIZONTAL;

        // Titre
        gbc.gridx = 0; gbc.gridy = 0; gbc.anchor = GridBagConstraints.WEST;
        panneau.add(creerLabel("Titre"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        champTitre = creerChamp(20);
        panneau.add(champTitre, gbc);

        // Genre
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
        panneau.add(creerLabel("Genre"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        comboGenre = new JComboBox<>(GENRES);
        comboGenre.setBackground(BG_FIELD);
        comboGenre.setForeground(TEXT_PRIMARY);
        comboGenre.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        comboGenre.setBorder(BorderFactory.createLineBorder(BORDER_COLOR));
        panneau.add(comboGenre, gbc);

        // Durée
        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0;
        panneau.add(creerLabel("Durée (min)"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        champDuree = creerChamp(10);
        panneau.add(champDuree, gbc);

        // Note
        gbc.gridx = 0; gbc.gridy = 3; gbc.weightx = 0;
        panneau.add(creerLabel("Note (0–10)"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        champNote = creerChamp(10);
        panneau.add(champNote, gbc);

        // Synopsis
        gbc.gridx = 0; gbc.gridy = 4; gbc.weightx = 0; gbc.anchor = GridBagConstraints.NORTHWEST;
        panneau.add(creerLabel("Synopsis"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0; gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        champSynopsis = new JTextArea(4, 20);
        champSynopsis.setLineWrap(true);
        champSynopsis.setWrapStyleWord(true);
        champSynopsis.setBackground(BG_FIELD);
        champSynopsis.setForeground(TEXT_PRIMARY);
        champSynopsis.setCaretColor(TEXT_PRIMARY);
        champSynopsis.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        champSynopsis.setBorder(BorderFactory.createEmptyBorder(4, 6, 4, 6));
        JScrollPane scrollSyn = new JScrollPane(champSynopsis);
        scrollSyn.setBorder(BorderFactory.createLineBorder(BORDER_COLOR));
        scrollSyn.getViewport().setBackground(BG_FIELD);
        panneau.add(scrollSyn, gbc);

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

    private JTextField creerChamp(int cols) {
        JTextField f = new JTextField(cols);
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

    private void preRemplir(Film film) {
        champTitre.setText(film.getTitre());
        champDuree.setText(String.valueOf(film.getDuree()));
        champNote.setText(String.valueOf(film.getNote()));
        champSynopsis.setText(film.getSynopsis());
        for (int i = 0; i < GENRES.length; i++)
            if (GENRES[i].equalsIgnoreCase(film.getGenre())) { comboGenre.setSelectedIndex(i); break; }
    }

    public boolean estConfirme() { return confirme; }
    public String getTitre()     { return champTitre.getText().trim(); }
    public String getGenre()     { return (String) comboGenre.getSelectedItem(); }
    public String getDuree()     { return champDuree.getText().trim(); }
    public String getNote()      { return champNote.getText().trim(); }
    public String getSynopsis()  { return champSynopsis.getText().trim(); }
}
