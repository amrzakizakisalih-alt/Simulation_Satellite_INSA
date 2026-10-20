package ui;
import reseau.ReseauSatellites;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

public class Interface2D extends JFrame {
    private int largeur;
    private int hauteur;
    private double zoomNiveau = 1.0;

    // 🆕 Ajouter l'environnement et le serveur comme champs de la classe
    private ReseauSatellites serveur;

    // Constructeur par défaut
    public Interface2D() {
        this(1000, 1000, 1.0);
    }

    // Constructeur avec paramètres
    public Interface2D(int largeur, int hauteur, double zoomNiveau) {
        this.largeur = largeur;
        this.hauteur = hauteur;
        this.zoomNiveau = zoomNiveau;

        initializeUI();
    }

    private void initializeUI() {
        setTitle("Simulation Spatiale");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(largeur, hauteur);
        setLayout(new BorderLayout());


        SpacePanel spacePanel = new SpacePanel();
        ControlPanel controlPanel = new ControlPanel(spacePanel);
        // Démarrer le serveur avec l'environnement
        serveur = new ReseauSatellites(5000);
        try {
            serveur.demarrer();


        } catch (IOException e) {
            System.out.println("Erreur non lancement du serveur");

            // Afficher un message d'avertissement à l'utilisateur
            JOptionPane.showMessageDialog(
                    this,
                    "Le serveur n'a pas pu démarrer sur le port 5000.\n" +
                            "Les satellites ne pourront pas communiquer entre eux.\n\n" +
                            "Erreur: " + e.getMessage(),
                    "Erreur serveur",
                    JOptionPane.WARNING_MESSAGE
            );
        }

        add(spacePanel, BorderLayout.CENTER);
        add(controlPanel, BorderLayout.EAST);


        setLocationRelativeTo(null);
        setVisible(true);

        // 🆕 Arrêter proprement le serveur à la fermeture
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                if (serveur != null) {
                    serveur.arreter();
                    System.out.println("✅ Serveur arrêté proprement");
                }
            }
        });
    }

    // Getters et Setters
    public int getLargeur() {
        return largeur;
    }

    public void setLargeur(int largeur) {
        this.largeur = largeur;
        setSize(largeur, hauteur);
    }

    public int getHauteur() {
        return hauteur;
    }

    public void setHauteur(int hauteur) {
        this.hauteur = hauteur;
        setSize(largeur, hauteur);
    }

    public double getZoomNiveau() {
        return zoomNiveau;
    }

    public void setZoomNiveau(double zoomNiveau) {
        this.zoomNiveau = zoomNiveau;
    }

    // Getter pour le serveur
    public ReseauSatellites getServeur() {
        return serveur;
    }


}
