package view;

import dao.LogDAO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class FenetrePrincipale extends JFrame {

    // ── Palette dark ──────────────────────────────────────────
    static final Color BG_DARK      = new Color(18,  20,  28);
    static final Color BG_PANEL     = new Color(26,  30,  42);
    static final Color BG_MID       = new Color(32,  37,  52);
    static final Color BG_SIDEBAR   = new Color(20,  23,  35);
    static final Color BORDER_COLOR = new Color(50,  57,  78);
    static final Color BLUE_ACCENT  = new Color(59, 130, 246);
    static final Color BLUE_HOVER   = new Color(37,  55, 100);
    static final Color BLUE_ACTIVE  = new Color(30,  50,  95);
    static final Color TEXT_PRIMARY = new Color(220, 225, 240);
    static final Color TEXT_MUTED   = new Color(120, 130, 155);
    static final Color GOLD_ADMIN   = new Color(250, 180,  50);
    // ──────────────────────────────────────────────────────────

    private JPanel         panneauCentral;
    private JLabel         labelStatut;
    private FenetreFilms   fenetreFilms;
    private FenetreSeances fenetreSeances;
    private FenetreLogs    fenetreLogs;
    private FenetreUtilisateurs fenetreUtilisateurs;

    // Boutons sidebar pour gérer l'état actif
    private JButton btnActif = null;

    public FenetrePrincipale() {
        DialogueLogin login = new DialogueLogin(null);
        if (!login.estConnecte()) System.exit(0);

        initialiserFenetre();
        creerMenuBar();
        creerStructure();
        setVisible(true);
    }

    private void initialiserFenetre() {
        setTitle("CinéTrack — " + LogDAO.getRoleActuel().toUpperCase());
        setSize(1150, 730);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        getContentPane().setBackground(BG_DARK);
    }

    // =========================================================
    //  TOPBAR — seulement Administration + À propos
    // =========================================================
    private void creerMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        menuBar.setBackground(BG_PANEL);
        menuBar.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_COLOR));
        menuBar.setLayout(new BorderLayout());

        // Gauche : logo/titre dans la topbar
        JPanel gauche = new JPanel(new FlowLayout(FlowLayout.LEFT, 14, 0));
        gauche.setOpaque(false);

        JLabel dot = new JLabel("●");
        dot.setForeground(BLUE_ACCENT);
        dot.setFont(new Font("Segoe UI", Font.BOLD, 13));

        JLabel appNom = new JLabel("CinéTrack");
        appNom.setFont(new Font("Segoe UI", Font.BOLD, 15));
        appNom.setForeground(TEXT_PRIMARY);

        JLabel separateur = new JLabel("|");
        separateur.setForeground(BORDER_COLOR);
        separateur.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        JLabel sousNom = new JLabel("Le Lumière");
        sousNom.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        sousNom.setForeground(TEXT_MUTED);

        gauche.add(dot);
        gauche.add(appNom);
        gauche.add(separateur);
        gauche.add(sousNom);

        // Droite : menus Administration (admin) + À propos + badge rôle
        JPanel droite = new JPanel(new FlowLayout(FlowLayout.RIGHT, 4, 0));
        droite.setOpaque(false);

        if ("admin".equals(LogDAO.getRoleActuel())) {
            JMenu menuAdmin = new JMenu("⚙  Administration");
            menuAdmin.setForeground(GOLD_ADMIN);
            menuAdmin.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            JMenuItem itemLogs = new JMenuItem("Consulter les logs");
            styleMenuItem(itemLogs);
            itemLogs.addActionListener(e -> {
                afficherVue("LOGS");
                desactiverBoutonsSidebar();
            });
            menuAdmin.add(itemLogs);

            JMenuItem itemUtilisateurs = new JMenuItem("Gérer les gestionnaires");
            styleMenuItem(itemUtilisateurs);
            itemUtilisateurs.addActionListener(e -> {
                fenetreUtilisateurs.rafraichir();
                afficherVue("UTILISATEURS");
                desactiverBoutonsSidebar();
            });
            menuAdmin.add(itemUtilisateurs);
            menuAdmin.getPopupMenu().setBackground(BG_MID);
            menuAdmin.getPopupMenu().setBorder(BorderFactory.createLineBorder(BORDER_COLOR));

            // Wrapper JMenu dans un JMenuBar temporaire pour l'ajouter à droite
            JMenuBar tmpBar = new JMenuBar();
            tmpBar.setOpaque(false);
            tmpBar.setBorder(null);
            tmpBar.add(menuAdmin);
            droite.add(tmpBar);
        }

        JMenu menuAPropos = new JMenu("À propos");
        menuAPropos.setForeground(TEXT_MUTED);
        menuAPropos.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        JMenuItem itemAP = new JMenuItem("À propos de CinéTrack");
        styleMenuItem(itemAP);
        itemAP.addActionListener(e -> JOptionPane.showMessageDialog(this,
            "CinéTrack v1.0\nApplication de gestion de cinéma\nBTS SIO SLAM — 1ère année",
            "À propos", JOptionPane.INFORMATION_MESSAGE));
        menuAPropos.add(itemAP);
        menuAPropos.getPopupMenu().setBackground(BG_MID);
        menuAPropos.getPopupMenu().setBorder(BorderFactory.createLineBorder(BORDER_COLOR));

        JMenuBar tmpBar2 = new JMenuBar();
        tmpBar2.setOpaque(false);
        tmpBar2.setBorder(null);
        tmpBar2.add(menuAPropos);
        droite.add(tmpBar2);

        // Badge rôle
        String role = LogDAO.getRoleActuel();
        Color badgeFg = "admin".equals(role) ? GOLD_ADMIN : BLUE_ACCENT;
        Color badgeBg = "admin".equals(role) ? new Color(70, 44, 8) : new Color(18, 42, 85);
        JLabel badge = new JLabel("  " + role.toUpperCase() + "  ");
        badge.setFont(new Font("Segoe UI", Font.BOLD, 10));
        badge.setForeground(badgeFg);
        badge.setBackground(badgeBg);
        badge.setOpaque(true);
        badge.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(badgeFg, 1),
            BorderFactory.createEmptyBorder(3, 6, 3, 6)
        ));
        droite.add(Box.createHorizontalStrut(8));
        droite.add(badge);
        droite.add(Box.createHorizontalStrut(10));

        menuBar.add(gauche,  BorderLayout.WEST);
        menuBar.add(droite,  BorderLayout.EAST);
        setJMenuBar(menuBar);
    }

    private void styleMenuItem(JMenuItem item) {
        item.setBackground(BG_MID);
        item.setForeground(TEXT_PRIMARY);
        item.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        item.setBorder(BorderFactory.createEmptyBorder(6, 14, 6, 14));
    }

    // =========================================================
    //  STRUCTURE PRINCIPALE : sidebar gauche + contenu central
    // =========================================================
    private void creerStructure() {
        // Panneau global qui contient sidebar + contenu
        JPanel corps = new JPanel(new BorderLayout());
        corps.setBackground(BG_DARK);

        corps.add(creerSidebar(),       BorderLayout.WEST);
        corps.add(creerZoneCentrale(),  BorderLayout.CENTER);
        corps.add(creerBarreStatut(),   BorderLayout.SOUTH);

        add(corps, BorderLayout.CENTER);
    }

    // =========================================================
    //  SIDEBAR GAUCHE
    // =========================================================
    private JPanel creerSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(BG_SIDEBAR);
        sidebar.setPreferredSize(new Dimension(200, 0));
        sidebar.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, BORDER_COLOR));

        // Section Films
        sidebar.add(creerSectionLabel("NAVIGATION"));
        JButton btnFilms   = creerBoutonSidebar("🎬  Films",   "FILMS");
        JButton btnSeances = creerBoutonSidebar("🕐  Séances", "SEANCES");
        sidebar.add(btnFilms);
        sidebar.add(btnSeances);

        // Sous-actions Films
        sidebar.add(creerSousBouton("Rechercher par genre", () -> {
            afficherVue("FILMS");
            fenetreFilms.ouvrirRechercheGenre();
            setActif(btnFilms);
        }));

        // Séparateur
        sidebar.add(Box.createVerticalStrut(10));
        JSeparator sep = new JSeparator();
        sep.setForeground(BORDER_COLOR);
        sep.setBackground(BORDER_COLOR);
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        sidebar.add(sep);
        sidebar.add(Box.createVerticalStrut(10));

        // Sous-actions Séances
        sidebar.add(creerSectionLabel("ACCÈS RAPIDE"));
        sidebar.add(creerSousBouton("Planning du jour", () -> {
            afficherVue("SEANCES");
            fenetreSeances.chargerPlanningAujourdhui();
            setActif(btnSeances);
        }));

        // Pousse le reste en bas
        sidebar.add(Box.createVerticalGlue());

        // Info version en bas
        JLabel version = new JLabel("v1.0  —  BTS SIO SLAM");
        version.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        version.setForeground(new Color(70, 78, 105));
        version.setAlignmentX(Component.CENTER_ALIGNMENT);
        version.setBorder(BorderFactory.createEmptyBorder(0, 0, 12, 0));
        sidebar.add(version);

        return sidebar;
    }

    private JLabel creerSectionLabel(String texte) {
        JLabel lbl = new JLabel(texte);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 9));
        lbl.setForeground(new Color(80, 90, 120));
        lbl.setBorder(BorderFactory.createEmptyBorder(16, 14, 4, 14));
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        return lbl;
    }

    private JButton creerBoutonSidebar(String texte, String vue) {
        JButton btn = new JButton(texte);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        btn.setForeground(TEXT_MUTED);
        btn.setBackground(BG_SIDEBAR);
        btn.setOpaque(true);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setBorder(BorderFactory.createEmptyBorder(10, 14, 10, 14));
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        btn.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) {
                if (btnActif != btn) { btn.setBackground(BLUE_HOVER); btn.setForeground(TEXT_PRIMARY); }
            }
            @Override public void mouseExited(MouseEvent e) {
                if (btnActif != btn) { btn.setBackground(BG_SIDEBAR); btn.setForeground(TEXT_MUTED); }
            }
        });

        btn.addActionListener(e -> {
            afficherVue(vue);
            setActif(btn);
        });

        return btn;
    }

    private JButton creerSousBouton(String texte, Runnable action) {
        JButton btn = new JButton("    › " + texte);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        btn.setForeground(new Color(90, 100, 130));
        btn.setBackground(BG_SIDEBAR);
        btn.setOpaque(true);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setBorder(BorderFactory.createEmptyBorder(6, 14, 6, 14));
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        btn.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { btn.setForeground(TEXT_PRIMARY); }
            @Override public void mouseExited(MouseEvent e)  { btn.setForeground(new Color(90,100,130)); }
        });

        btn.addActionListener(e -> action.run());
        return btn;
    }

    private void setActif(JButton btn) {
        // Désactiver l'ancien
        if (btnActif != null) {
            btnActif.setBackground(BG_SIDEBAR);
            btnActif.setForeground(TEXT_MUTED);
        }
        // Activer le nouveau
        btnActif = btn;
        btn.setBackground(BLUE_ACTIVE);
        btn.setForeground(TEXT_PRIMARY);
    }

    private void desactiverBoutonsSidebar() {
        if (btnActif != null) {
            btnActif.setBackground(BG_SIDEBAR);
            btnActif.setForeground(TEXT_MUTED);
            btnActif = null;
        }
    }

    // =========================================================
    //  ZONE CENTRALE avec CardLayout
    // =========================================================
    private JPanel creerZoneCentrale() {
        JPanel zone = new JPanel(new BorderLayout());
        zone.setBackground(BG_DARK);

        panneauCentral = new JPanel(new CardLayout());
        panneauCentral.setBackground(BG_DARK);

        panneauCentral.add(creerEcranAccueil(), "ACCUEIL");

        fenetreFilms = new FenetreFilms(this);
        panneauCentral.add(fenetreFilms, "FILMS");

        fenetreSeances = new FenetreSeances(this);
        panneauCentral.add(fenetreSeances, "SEANCES");

        if ("admin".equals(LogDAO.getRoleActuel())) {
            fenetreLogs = new FenetreLogs();
            panneauCentral.add(fenetreLogs, "LOGS");

            fenetreUtilisateurs = new FenetreUtilisateurs();
            panneauCentral.add(fenetreUtilisateurs, "UTILISATEURS");
        }

        zone.add(panneauCentral, BorderLayout.CENTER);
        return zone;
    }

    private JPanel creerEcranAccueil() {
        JPanel panneau = new JPanel(new GridBagLayout());
        panneau.setBackground(BG_DARK);

        String role = LogDAO.getRoleActuel();
        Color roleCouleur = "admin".equals(role) ? GOLD_ADMIN : BLUE_ACCENT;

        JLabel titre = new JLabel("Bienvenue dans CinéTrack");
        titre.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titre.setForeground(TEXT_PRIMARY);

        JLabel sousTitre = new JLabel("Connecté en tant que : " + role
            + ("admin".equals(role) ? "  —  Accès complet" : "  —  Accès standard"));
        sousTitre.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        sousTitre.setForeground(roleCouleur);

        JLabel indication = new JLabel("Sélectionnez une section dans le menu à gauche.");
        indication.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        indication.setForeground(TEXT_MUTED);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.gridy = 0; panneau.add(titre, gbc);
        gbc.gridy = 1; gbc.insets = new Insets(12, 0, 6, 0); panneau.add(sousTitre, gbc);
        gbc.gridy = 2; gbc.insets = new Insets(0, 0, 0, 0);  panneau.add(indication, gbc);
        return panneau;
    }

    // =========================================================
    //  BARRE DE STATUT
    // =========================================================
    private JPanel creerBarreStatut() {
        JPanel barre = new JPanel(new BorderLayout());
        barre.setBackground(BG_PANEL);
        barre.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, BORDER_COLOR));

        labelStatut = new JLabel("  CinéTrack prêt  |  Rôle : " + LogDAO.getRoleActuel());
        labelStatut.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        labelStatut.setForeground(TEXT_MUTED);
        labelStatut.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        barre.add(labelStatut, BorderLayout.WEST);
        return barre;
    }

    public void afficherVue(String nomVue) {
        CardLayout cl = (CardLayout) panneauCentral.getLayout();
        cl.show(panneauCentral, nomVue);
    }

    public void mettreAJourStatut(String message) {
        labelStatut.setText("  " + message);
    }
}
