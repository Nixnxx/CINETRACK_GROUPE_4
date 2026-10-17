package view;

import controleur.UtilisateurControleur;
import javax.swing.*;
import java.awt.*;

public class DialogueLogin extends JDialog {

    // ── Palette dark ──────────────────────────────────────────
    private static final Color BG_DARK      = new Color(18,  20,  28);
    private static final Color BG_PANEL     = new Color(28,  32,  44);
    private static final Color BG_FIELD     = new Color(38,  43,  58);
    private static final Color BORDER_COLOR = new Color(55,  62,  80);
    private static final Color BLUE_ACCENT  = new Color(59, 130, 246);
    private static final Color TEXT_PRIMARY = new Color(220, 225, 240);
    private static final Color TEXT_MUTED   = new Color(120, 130, 155);
    private static final Color RED_ERROR    = new Color(239,  68,  68);
    // ──────────────────────────────────────────────────────────

    private JTextField    champLogin;
    private JPasswordField champMotDePasse;
    private JLabel        labelErreur;
    private boolean       connecte = false;

    private final UtilisateurControleur controleur = new UtilisateurControleur();

    public DialogueLogin(JFrame parent) {
        super(parent, "CinéTrack — Connexion", true);
        initialiserComposants();
        setSize(400, 310);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setResizable(false);
        getContentPane().setBackground(BG_DARK);
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override public void windowClosing(java.awt.event.WindowEvent e) { System.exit(0); }
        });
        setVisible(true);
    }

    private void initialiserComposants() {
        JPanel panneau = new JPanel(new GridBagLayout());
        panneau.setBackground(BG_PANEL);
        panneau.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1),
            BorderFactory.createEmptyBorder(28, 32, 20, 32)
        ));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(7, 5, 7, 5);
        gbc.fill   = GridBagConstraints.HORIZONTAL;

        // Titre
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        JLabel titre = new JLabel("CinéTrack", SwingConstants.CENTER);
        titre.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titre.setForeground(TEXT_PRIMARY);
        panneau.add(titre, gbc);

        // Sous-titre
        gbc.gridy = 1;
        JLabel sousTitre = new JLabel("Connexion à l'application", SwingConstants.CENTER);
        sousTitre.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        sousTitre.setForeground(TEXT_MUTED);
        panneau.add(sousTitre, gbc);

        // Login
        gbc.gridwidth = 1; gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0;
        panneau.add(creerLabel("Identifiant"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        champLogin = creerChamp(15);
        panneau.add(champLogin, gbc);

        // Mot de passe
        gbc.gridx = 0; gbc.gridy = 3; gbc.weightx = 0;
        panneau.add(creerLabel("Mot de passe"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        champMotDePasse = new JPasswordField(15);
        styleChamp(champMotDePasse);
        panneau.add(champMotDePasse, gbc);

        // Erreur
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        labelErreur = new JLabel("", SwingConstants.CENTER);
        labelErreur.setForeground(RED_ERROR);
        labelErreur.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        panneau.add(labelErreur, gbc);

        // Bouton
        gbc.gridy = 5;
        JButton btn = new JButton("Se connecter");
        btn.setBackground(BLUE_ACCENT);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(0, 36));
        btn.addActionListener(e -> tenterConnexion());
        champMotDePasse.addActionListener(e -> tenterConnexion());
        panneau.add(btn, gbc);

        setLayout(new GridBagLayout());
        getContentPane().setBackground(BG_DARK);
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(20, 20, 20, 20);
        c.fill   = GridBagConstraints.BOTH;
        c.weightx = 1; c.weighty = 1;
        add(panneau, c);
    }

    private JLabel creerLabel(String texte) {
        JLabel l = new JLabel(texte + " :");
        l.setForeground(TEXT_MUTED);
        l.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        return l;
    }

    private JTextField creerChamp(int cols) {
        JTextField f = new JTextField(cols);
        styleChamp(f);
        return f;
    }

    private void styleChamp(JTextField f) {
        f.setBackground(BG_FIELD);
        f.setForeground(TEXT_PRIMARY);
        f.setCaretColor(TEXT_PRIMARY);
        f.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR),
            BorderFactory.createEmptyBorder(4, 8, 4, 8)
        ));
        f.setFont(new Font("Segoe UI", Font.PLAIN, 12));
    }

    private void tenterConnexion() {
        String login = champLogin.getText().trim();
        String mdp   = new String(champMotDePasse.getPassword());

        // Le contrôleur vérifie en base, définit le rôle et écrit le log de connexion
        String erreur = controleur.connecter(login, mdp);
        if (erreur == null) {
            connecte = true;
            dispose();
        } else {
            labelErreur.setText(erreur);
            champMotDePasse.setText("");
        }
    }

    public boolean estConnecte() { return connecte; }
}
