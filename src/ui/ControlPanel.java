package ui;

import Trajectoires.Trajectoire;
import data.BaseDeDonnes;
import physique.ForceGravitationnelle;
import modele.*;
import reseau.*;


import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.awt.geom.Point2D;
/**
 * Panneau de contrôle principal de l'application de simulation spatiale.
 * <p>
 * Ce panneau permet à l'utilisateur de créer et de gérer différents objets spatiaux
 * tels que les planètes, lunes, astéroïdes et satellites. Il offre des fonctionnalités
 * pour :
 * <ul>
 *     <li>Ajouter des objets avec des paramètres personnalisés (masse, rayon, position, vitesse...)</li>
 *     <li>Placer des satellites en orbite autour d'astres</li>
 *     <li>Gérer les couleurs et la visibilité des objets</li>
 *     <li>Sauvegarder et charger l'état de la simulation</li>
 *     <li>Créer automatiquement des astéroïdes aléatoires</li>
 *     <li>Activer/désactiver la physique et réinitialiser la vue</li>
 * </ul>
 *
 * <p>
 */
public class ControlPanel extends JPanel {
    /** ComboBox pour sélectionner le type d'objet à créer (Planète, Lune, Astéroïde, Satellite). */
    private JComboBox<String> choixobjet;
    /** Champs de saisie pour le nom, la masse, le rayon et la position de l'objet. */
    private JTextField nomField, masseField, rayonField, posXField, posYField,ispField;
    /** Champs de saisie pour la vitesse de l'objet. */
    private JTextField velociteXField, velociteYField;
    /** Champs spécifiques aux lunes (rayon orbital et angle initial). */
    private JTextField rayonOrbitalField, angleField;
    /** ComboBox pour sélectionner la planète parent d'une lune. */
    private JComboBox<String> planeteCombo;
    /** Boutons d'action pour ajouter, effacer, choisir couleur et gérer la physique. */
    private JButton addButton, clearButton, colorButton, togglePhysicsButton;
    /** Panneau principal d'affichage des objets spatiaux. */
    private SpacePanel spacePanel;
    /** Map pour stocker les planètes, lunes et satellites créés. */
    private Map<String, Planete> planeteMap = new HashMap<>();
    private Map<String, Lune> luneMap = new HashMap<>();
    private Map<String, Satellites> satelliteMap = new HashMap<>();
    /** Compteurs pour générer automatiquement des noms uniques. */
    private int planeteCounter = 1;
    private int luneCounter = 1;
    private int asteroideCounter = 1;
    private int satelliteCounter = 1;
    /** ComboBox pour choisir l'astre autour duquel un satellite peut être mis en orbite. */
    private JComboBox<String> astreOrbiteCombo;
    /** Champ pour le rayon orbital d'un satellite. */
    private JTextField rayonOrbitalSatelliteField;
    /** Checkbox pour activer ou non le mode orbite pour les satellites. */
    private JCheckBox modeOrbiteCheckbox;
    /** Champs supplémentaires spécifiques aux satellites. */
    private JTextField destXField, destYField, carburantField, porteeField;
    /** Couleur sélectionnée pour l'objet créé (null = couleur automatique). */
    private Color selectionne = null;
    /**
     * Crée un panneau de contrôle associé à un {@link SpacePanel}.
     * Initialise tous les composants graphiques et configure les sections
     * pour les objets, lunes, satellites, couleur et actions.
     *
     * @param spacePanel le panneau d'affichage des objets spatiaux
     */
    public ControlPanel(SpacePanel spacePanel) {
        this.spacePanel = spacePanel;
        this.spacePanel.setControlPanel(this);

        setupPaneauBasic();
        setupTitreSection();
        setupObjetTypeSection();
        setupCommuneSection();
        setupMoonSection();
        setupSatelliteSection();
        setupColorSection();
        setupActionButtonsSection();
        setupOrbitalCalculators();
        updateFieldsVisibility();
    }
    /**
     * Initialise les paramètres de base du panneau.
     */
    private void setupPaneauBasic() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setPreferredSize(new Dimension(280, 0));
        setBackground(Color.DARK_GRAY);
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    }

    /**
     * Configure le titre du panneau.
     */
    private void setupTitreSection() {
        JLabel titleLabel = new JLabel("🎮 Contrôles");
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        add(titleLabel);
        add(Box.createVerticalStrut(15));
    }
    /**
     * Configure la section de sélection du type d'objet.
     */
    private void setupObjetTypeSection() {
        add(createLabel("Type d'objet :"));
        choixobjet = new JComboBox<>(new String[]{"Planète", "Lune", "Astéroïde", "Satellite"});
        choixobjet.setMaximumSize(new Dimension(Integer.MAX_VALUE, 25));
        choixobjet.addActionListener(e -> updateFieldsVisibility());
        add(choixobjet);
        add(Box.createVerticalStrut(10));
    }
    /**
     * Configure la section des champs communs à tous les objets.
     */
    private void setupCommuneSection() {
        nomField = new JTextField("Planète_1");
        add(createLabeledField("Nom :", nomField));
        add(Box.createVerticalStrut(5));

        masseField = new JTextField("1000");
        rayonField = new JTextField("20");
        posXField = new JTextField("0");
        posYField = new JTextField("0");
        velociteXField = new JTextField("0");
        velociteYField = new JTextField("0");


        add(createLabeledField("Masse (kg) :", masseField));
        add(createLabeledField("Rayon (px) :", rayonField));
        add(createLabeledField("Position X :", posXField));
        add(createLabeledField("Position Y :", posYField));
        add(createLabeledField("Vitesse X (px/s) :", velociteXField));
        add(createLabeledField("Vitesse Y (px/s) :", velociteYField));

        add(Box.createVerticalStrut(10));
    }
    /**
     * Configure la section pour les lunes.
     */
    private void setupMoonSection() {
        JLabel luneSeparator = createLabel("--- Paramètres Lune ---");
        add(luneSeparator);

        add(createLabel("Planète parent :"));
        planeteCombo = new JComboBox<>();
        planeteCombo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 25));
        add(planeteCombo);
        add(Box.createVerticalStrut(5));

        rayonOrbitalField = new JTextField("100");
        angleField = new JTextField("0");


        add(createLabeledField("Rayon orbital :", rayonOrbitalField));
        add(createLabeledField("Angle initial (°) :", angleField));
        add(Box.createVerticalStrut(10));
    }
    /**
     * Configure la section pour les satellites.
     */
    private void setupSatelliteSection() {
        JLabel satelliteSeparator = createLabel("--- Paramètres Satellite ---");
        add(satelliteSeparator);



        destXField = new JTextField("0");
        destYField = new JTextField("0");
        carburantField = new JTextField("2000");
        porteeField = new JTextField("200");
        ispField = new JTextField("300");

        add(createLabeledField("Isp :", ispField));
        add(createLabeledField("Destination X :", destXField));
        add(createLabeledField("Destination Y :", destYField));
        add(createLabeledField("Carburant :", carburantField));
        add(createLabeledField("Portée caméra :", porteeField));
        add(Box.createVerticalStrut(10));

        add(createLabel("--- Mode Orbite ---"));

        modeOrbiteCheckbox = new JCheckBox("Placer en orbite");
        modeOrbiteCheckbox.setForeground(Color.WHITE);
        modeOrbiteCheckbox.setOpaque(false);
        modeOrbiteCheckbox.addActionListener(e -> updateSatelliteOrbiteFields());
        add(modeOrbiteCheckbox);

        add(createLabel("Astre pour orbite:"));
        astreOrbiteCombo = new JComboBox<>();
        astreOrbiteCombo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 25));
        add(astreOrbiteCombo);

        rayonOrbitalSatelliteField = new JTextField("150");
        add(createLabeledField("Rayon orbital:", rayonOrbitalSatelliteField));
        add(Box.createVerticalStrut(10));
    }
    /**
     * Configure la section de choix de couleur.
     */
    private void setupColorSection() {
        colorButton = new JButton("🎨 Choisir couleur (Auto)");
        colorButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        colorButton.addActionListener(e -> chooseColor());
        add(colorButton);
        add(Box.createVerticalStrut(10));
    }
    /**
     * Configure les boutons d'action (ajouter, sauvegarder, charger, etc.).
     */
    private void setupActionButtonsSection() {
        // Bouton d'ajout d'objet
        addButton = new JButton("➕ Ajouter objet");
        addButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        addButton.addActionListener(e -> createObject());
        add(addButton);
        add(Box.createVerticalStrut(10));

        // Bouton création astéroïdes automatique
        JButton createAsteroidsButton = new JButton("☄️ Créer Astéroïdes");
        createAsteroidsButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        createAsteroidsButton.addActionListener(e -> createMultipleAsteroids());
        add(createAsteroidsButton);
        add(Box.createVerticalStrut(10));

        JButton saveButton = new JButton("💾 Sauvegarder");
        saveButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        saveButton.addActionListener(e -> saveSimulation());
        add(saveButton);
        add(Box.createVerticalStrut(10));

        // NOUVEAU: Bouton charger simulation
        JButton loadButton = new JButton("📂 Charger");
        loadButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        loadButton.addActionListener(e -> loadSimulation());
        add(loadButton);
        add(Box.createVerticalStrut(10));

        // Bouton pause physique
        togglePhysicsButton = new JButton("⏸️ Pause Physique");
        togglePhysicsButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        togglePhysicsButton.addActionListener(e -> togglePhysics());
        add(togglePhysicsButton);
        add(Box.createVerticalStrut(10));

        // Bouton tout effacer
        clearButton = new JButton("🗑️ Tout effacer");
        clearButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        clearButton.addActionListener(e -> clearAll());
        add(clearButton);
        add(Box.createVerticalStrut(10));

        // Bouton réinitialiser vue
        JButton resetViewButton = new JButton("🔄 Réinitialiser vue");
        resetViewButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        resetViewButton.addActionListener(e -> spacePanel.resetView());
        add(resetViewButton);

        add(Box.createVerticalGlue());
    }
    /**
     * Configure les calculateurs orbitaux pour les lunes.
     */
    private void setupOrbitalCalculators() {
        // Votre méthode existante pour les calculateurs orbitaux
        addOrbitalCalculators();
    }
    /**
     * Crée un JLabel avec le texte donné.
     *
     * @param text texte à afficher
     * @return JLabel formaté
     */
    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(Color.WHITE);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }
    /**Mets à jour l'orbite satellitaire
     * */
    private void updateSatelliteOrbiteFields() {
        boolean modeOrbite = modeOrbiteCheckbox.isSelected();
        astreOrbiteCombo.setEnabled(modeOrbite);
        rayonOrbitalSatelliteField.setEnabled(modeOrbite);

        destXField.setEnabled(!modeOrbite);
        destYField.setEnabled(!modeOrbite);
    }
    /** Mets à jour l'orbite lunaire*/
    private void updateAstreOrbiteCombo() {
        astreOrbiteCombo.removeAllItems();

        // Ajouter les planètes
        for (String nomPlanete : planeteMap.keySet()) {
            astreOrbiteCombo.addItem("🪐 " + nomPlanete);
        }
    }
    /**
     * Crée un JPanel contenant un JLabel et un JTextField.
     *
     * @param labelText texte du label
     * @param field champ de saisie
     * @return JPanel contenant le label et le champ
     */
    private JPanel createLabeledField(String labelText, JTextField field) {
        JPanel panel = new JPanel(new BorderLayout(5, 0));
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        panel.setOpaque(false);

        JLabel label = new JLabel(labelText);
        label.setForeground(Color.WHITE);
        label.setPreferredSize(new Dimension(120, 25));

        panel.add(label, BorderLayout.WEST);
        panel.add(field, BorderLayout.CENTER);

        return panel;
    }

    /**
     * Met à jour la visibilité et l'activation des champs selon le type d'objet sélectionné.
     */
    private void updateFieldsVisibility() {
        String type = (String) choixobjet.getSelectedItem();
        boolean isLune = "Lune".equals(type);
        boolean isAsteroide = "Astéroïde".equals(type);
        boolean isSatellite = "Satellite".equals(type);

        posXField.setEnabled(!isLune);
        posYField.setEnabled(!isLune);

        velociteXField.setEnabled(!isLune && !isSatellite);
        velociteYField.setEnabled(!isLune && !isSatellite);

        // Champs lune : visibles seulement pour lunes
        planeteCombo.setEnabled(isLune);
        rayonOrbitalField.setEnabled(isLune);
        angleField.setEnabled(isLune);

        //Champs satellites :

        destXField.setEnabled(isSatellite);
        destYField.setEnabled(isSatellite);
        carburantField.setEnabled(isSatellite);
        porteeField.setEnabled(isSatellite);
        ispField.setEnabled(isSatellite);

        // 🆕 Masse et rayon spécifiques pour astéroïdes
        if (isAsteroide) {
            masseField.setText("50");
            rayonField.setText("5");
        }
        if (isLune) {
            nomField.setText("Lune_" + luneCounter);
        } else if (isAsteroide) {
            nomField.setText("Astéroïde_" + asteroideCounter);
        } else if (isSatellite) {
            nomField.setText("Satellite_" + satelliteCounter);
            masseField.setText("100");
            rayonField.setText("5");
        } else {
            nomField.setText("Planète_" + planeteCounter);
        }
    }
    /**
     * Active ou désactive la physique de la simulation.
     */
    private void togglePhysics() {
        boolean enabled = !spacePanel.isphysiqueEnMarche();
        spacePanel.setphysiqueEnMarche(enabled);
        togglePhysicsButton.setText(enabled ? "⏸️ Pause Physique" : "▶️ Reprendre Physique");
    }

    /**
     * Ouvre un sélecteur de couleur pour l'objet à créer.
     */
    private void chooseColor() {
        Color newColor = JColorChooser.showDialog(this, "Choisir une couleur",
                selectionne != null ? selectionne : Color.CYAN);

        if (newColor != null) {
            selectionne = newColor;
            colorButton.setText("🎨 Couleur personnalisée");
            colorButton.setBackground(newColor);
            colorButton.setForeground(getContrastColor(newColor));
        }
    }

    /**
     * Retourne une couleur contrastante pour un fond donné.
     *
     * @param background couleur de fond
     * @return couleur de texte contrastante (noir ou blanc)
     */    private Color getContrastColor(Color background) {
        double luminance = (0.299 * background.getRed() +
                0.587 * background.getGreen() +
                0.114 * background.getBlue()) / 255;
        return luminance > 0.5 ? Color.BLACK : Color.WHITE;
    }
    /**
     * Crée un objet selon le type sélectionné et ajoute à la simulation.
     */
    private void createObject() {
        try {
            String nom = nomField.getText().trim();
            if (nom.isEmpty()) {
                showError("Le nom ne peut pas être vide !");
                return;
            }
            // Champs communs
            double masse = Double.parseDouble(masseField.getText());
            double rayon = Double.parseDouble(rayonField.getText());

            // Champs Planète/Astéroïde
            double posX = Double.parseDouble(posXField.getText());
            double posY = Double.parseDouble(posYField.getText());

            // Champs vitesse
            double velociteX = Double.parseDouble(velociteXField.getText());
            double velociteY = Double.parseDouble(velociteYField.getText());

            // Champs Lune
            double rayonOrbital = Double.parseDouble(rayonOrbitalField.getText());
            double angle = Double.parseDouble(angleField.getText());


            // Validation
            if ( rayon <= 0) {
                showError("le rayon doit être positifs !");
                return;
            }
            // VALIDATION AJOUTÉE - Éviter les valeurs infinies ou NaN
            if (!Double.isFinite(masse) || !Double.isFinite(rayon)) {
                showError("La masse et le rayon doivent être des nombres valides !");
                return;
            }

            String type = (String) choixobjet.getSelectedItem();

            if ("Planète".equals(type)) {
                createPlanete(nom, masse, rayon, posX, posY, velociteX, velociteY);
            } else if ("Lune".equals(type)) {
                createLune(nom, masse, rayon, rayonOrbital, angle);
            } else if ("Astéroïde".equals(type)) {
                createAsteroide(nom, masse, rayon, posX, posY, velociteX, velociteY);
            } else if ("Satellite".equals(type)) {
                // Champs satellites
                double destX = Double.parseDouble(destXField.getText());
                double destY = Double.parseDouble(destYField.getText());
                double carburant = Double.parseDouble(carburantField.getText());
                double portee = Double.parseDouble(porteeField.getText());
                double isp= Double.parseDouble(ispField.getText());
                createSatellite(nom, masse, rayon, posX, posY, destX, destY, carburant, portee,isp);

            }

            // Message de succès
            showSuccess("Objet '" + nom + "' ajouté avec succès !");
            // Réinitialiser les champs
            resetFields();

        } catch (NumberFormatException ex) {
            showError("Veuillez entrer des nombres valides!!!");
        }
    }


    /**
     * Crée un objet Planète et l'ajoute à la simulation.
     *
     * @param nom le nom de la planète
     * @param masse la masse en kg
     * @param rayon le rayon en pixels
     * @param x position X initiale
     * @param y position Y initiale
     * @param vx vitesse X initiale
     * @param vy vitesse Y initiale
     */
    private void createPlanete(String nom, double masse, double rayon, double x, double y, double vx, double vy) {
        Point2D position = new Point2D.Double(x, y);

        Planete planete = new Planete(nom, 9.8, position, masse, rayon);
        planete.setVelocite(vx, vy);

        // Appliquer la couleur si sélectionnée
        if (selectionne != null) {
            spacePanel.setCouleursObjets(planete, selectionne);
        }

        // Stocker la planète
        planeteMap.put(nom, planete);
        spacePanel.addObjet(planete);


        // Centrer automatiquement la vue sur le nouvel objet
        spacePanel.centerOnObject(planete);

        // Ajouter au combo pour les lunes
        planeteCombo.addItem(nom);
        updateAstreOrbiteCombo();

        planeteCounter++;
    }
    /**
     * Crée un objet Astéroïde et l'ajoute à la simulation.
     *
     * @param nom nom de l'astéroïde
     * @param masse masse en kg
     * @param rayon rayon en px
     * @param x position X initiale
     * @param y position Y initiale
     * @param vx vitesse X initiale
     * @param vy vitesse Y initiale
     */
    private void createAsteroide(String nom, double masse, double rayon, double x, double y, double vx, double vy) {
        Point2D position = new Point2D.Double(x, y);
        double taille = rayon * 2; // Taille = diamètre

        Asteroides asteroide = new Asteroides(nom, position, masse, rayon, vx, vy, taille);

        // Appliquer la couleur si sélectionnée, sinon gris par défaut
        if (selectionne != null) {
            spacePanel.setCouleursObjets(asteroide, selectionne);
        } else {
            spacePanel.setCouleursObjets(asteroide, Color.GRAY);
        }

        // Ajouter au SpacePanel
        spacePanel.addObjet(asteroide);

        asteroideCounter++;
    }

    /**
     * Crée un objet Satellite et l'ajoute à la simulation.
     *
     * @param nom nom du satellite
     * @param masse masse en kg
     * @param rayon rayon en px
     * @param x position X initiale
     * @param y position Y initiale
     * @param destX position X de destination
     * @param destY position Y de destination
     * @param carburant carburant initial
     * @param portee portée de la caméra
     * @param isp impulse spécifique
     */
    private void createSatellite(String nom, double masse, double rayon, double x, double y,
                                 double destX, double destY, double carburant, double portee,double isp) {
        Point2D position = new Point2D.Double(x, y);
        Point2D destination = new Point2D.Double(destX, destY);

        Satellites satellite = new Satellites(destination,masse,rayon,0,0,position,satelliteCounter,carburant,portee);

        satellite.setIdentifier(satelliteCounter);
        satellite.setDestination(destination);
        satellite.setCarburant(carburant);
        satellite.setPorteeCamera(portee);
        satellite.setIsp(isp);
        satellite.setGo(9.81);


        try {
            String idSatellite = "S" + satelliteCounter;
            String hostServeur = "localhost";
            int portServeur = 5000;

            Antenne antenne = new Antenne(idSatellite, hostServeur, portServeur);

            antenne.connecter();
            satellite.setAntenne(antenne);

           System.out.println("Satellite " + idSatellite + " connecté au serveur");

        } catch (Exception e) {
            System.out.println("Erreur de connexion du satellite " + satelliteCounter +
                    " au serveur: " + e.getMessage());
        }


        if (modeOrbiteCheckbox.isSelected()) {
            String selectedAstre = (String) astreOrbiteCombo.getSelectedItem();
            if (selectedAstre != null) {
                // Extraire le nom de l'astre (enlever l'emoji)
                String nomAstree = selectedAstre.substring(2).trim();
                ObjetSpatial astre = planeteMap.get(nomAstree);
                if (astre == null) {
                    astre = luneMap.get(nomAstree);
                }

                if (astre != null) {
                    double rayonOrbital = Double.parseDouble(rayonOrbitalSatelliteField.getText());
                    satellite.placerEnOrbite(astre, rayonOrbital);

                    // Ne pas calculer de trajectoire pour les satellites en orbite
                    satellite.setTrajectoire(new Trajectoire());
                    System.out.println("Satellite placé en orbite autour de " + nomAstree);
                } else {
                    System.out.println("Astre '" + nomAstree + "' introuvable !");
                }
            }
            }else {


                Trajectoire trajectoire = new Trajectoire();

                trajectoire.setPointDepart(position);
                trajectoire.setPointArrivee(destination);
                satellite.setTrajectoire(trajectoire);
                satellite.getTrajectoire().determinerTrajectoire(satellite);

                Trajectoire traj = satellite.getTrajectoire();
                if (traj.getPoints() == null || traj.getPoints().isEmpty()) {
                    System.out.println("WARNING ❌ ERREUR CRITIQUE: Trajectoire non calculée pour le satellite " + nom);
                    System.out.println("   Départ: " + traj.getPointDepart());
                    System.out.println("   Arrivée: " + traj.getPointArrivee());
                    System.out.println("   Valide: " + traj.isEstValide());
                } else {
                    System.out.println("SUCCESS Trajectoire calculée avec " + traj.getPoints().size() + " points");
                }
            }



        // Appliquer la couleur
        if (selectionne != null) {
            spacePanel.setCouleursObjets(satellite, selectionne);
        } else {
            spacePanel.setCouleursObjets(satellite, Color.YELLOW);
        }

        // Stocker le satellite
        satelliteMap.put(nom, satellite);

        // 🆕 Ajouter le satellite directement (plus besoin d'ObjetSpatial séparé)
        spacePanel.addObjet(satellite);

        // 🆕 Enregistrer avec le satellite lui-même comme objetSpatial


        spacePanel.centerOnObject(satellite);
        updateAstreOrbiteCombo();

        satelliteCounter++;
    }
    /**
     * Crée un objet Lune et l'ajoute à la simulation.
     *
     * @param nom le nom de la lune
     * @param masse la masse en kg
     * @param rayon le rayon en pixels
     * @param rayonOrbital distance orbitale autour de la planète mère
     * @param angle angle initial de position
     */
    private void createLune(String nom, double masse, double rayon, double rayonOrbital, double angle) {
        String parentName = (String) planeteCombo.getSelectedItem();
        if (parentName == null || parentName.isEmpty()) {
            showWarning("Ajoutez d'abord une planète avant une lune !");
            return;
        }

        if (rayonOrbital <= 0) {
            showError("Le rayon orbital doit être positif !");
            return;
        }

        // Récupérer la planète mère
        Planete parent = planeteMap.get(parentName);

        if (parent == null) {
            showError("La planète '" + parentName + "' n'existe plus !");
            return;
        }

        Point2D posParent = parent.getPosition();

        // Calculer la position de la lune
        double angleRad = Math.toRadians(angle);
        double luneX = posParent.getX() + rayonOrbital * Math.cos(angleRad);
        double luneY = posParent.getY() + rayonOrbital * Math.sin(angleRad);
        Point2D positionLune = new Point2D.Double(luneX, luneY);

        // CORRECTION : Calculer la vitesse orbitale réaliste
        double[] vitesseOrbitale = ForceGravitationnelle.calculerVitesseOrbitale(parent, positionLune, rayonOrbital);
        double periodeCalculee = ForceGravitationnelle.calculerPeriodeOrbitale(parent, rayonOrbital);

        Lune lune = new Lune(nom, 1.6, parent, periodeCalculee, positionLune, masse, rayon,
                vitesseOrbitale[0], vitesseOrbitale[1]);

        if (selectionne != null) {
            spacePanel.setCouleursObjets(lune, selectionne);
        }

        // Stocker la lune
        luneMap.put(nom, lune);

        // Enregistrer la lune dans le SpacePanel
        spacePanel.addObjet(lune);

        // Ajouter à l'environnement

        // Afficher les informations de débogage
        System.out.println("SUCCESS Lune créée : " + nom);
        System.out.println("   Position : (" + luneX + ", " + luneY + ")");
        System.out.println("   Vitesse : (" + vitesseOrbitale[0] + ", " + vitesseOrbitale[1] + ")");
        System.out.println("   Rayon orbital : " + rayonOrbital);

        luneCounter++;
        updateAstreOrbiteCombo();
    }

    /**
     * Calcule et affiche la vitesse orbitale recommandée
     */
    private void calculerEtAfficherVitesseOrbitale() {
        String parentName = (String) planeteCombo.getSelectedItem();
        if (parentName == null || parentName.isEmpty()) return;

        try {
            Planete parent = planeteMap.get(parentName);
            double rayonOrbital = Double.parseDouble(rayonOrbitalField.getText());

            Point2D posParent = parent.getPosition();
            Point2D posLune = new Point2D.Double(posParent.getX() + rayonOrbital, posParent.getY());

            double[] vitesse = ForceGravitationnelle.calculerVitesseOrbitale(parent, posLune, rayonOrbital);
            double periode = ForceGravitationnelle.calculerPeriodeOrbitale(parent, rayonOrbital);

            // Optionnel: Afficher dans un tooltip ou label
            velociteXField.setToolTipText("Vitesse X recommandée: " + vitesse[0]);
            velociteYField.setToolTipText("Vitesse Y recommandée: " + vitesse[1]);

        } catch (Exception e) {
            // Ignorer les erreurs de calcul
        }
    }
    /**
     ajout des calculateurs d'orbites
     */
    private void addOrbitalCalculators() {
        rayonOrbitalField.addActionListener(e -> calculerEtAfficherVitesseOrbitale());
        planeteCombo.addActionListener(e -> calculerEtAfficherVitesseOrbitale());
    }

    /**
     réinitialisation des champs
     */
    private void resetFields() {
        // Réinitialiser tous les champs de saisie
        nomField.setText("Planète_1");
        masseField.setText("1000");
        rayonField.setText("20");
        posXField.setText("0");
        posYField.setText("0");
        velociteXField.setText("0");
        velociteYField.setText("0");
        rayonOrbitalField.setText("100");
        angleField.setText("0");
        destXField.setText("0");
        destYField.setText("0");
        carburantField.setText("2000");
        porteeField.setText("200");
        modeOrbiteCheckbox.setSelected(false);
        rayonOrbitalSatelliteField.setText("150");



        // Réinitialiser la couleur
        selectionne = null;
        colorButton.setText("🎨 Choisir couleur (Auto)");
        colorButton.setBackground(null);
        colorButton.setForeground(null);


        // 🆕 FORCER LA MISE À JOUR DE LA VISIBILITÉ
        updateFieldsVisibility();
        updateSatelliteOrbiteFields();

        // 🆕 REMETTRE LE TYPE PAR DÉFAUT
        choixobjet.setSelectedIndex(0);
    }

    /**
     *tout supprimer
     */
    private void clearAll() {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Êtes-vous sûr de vouloir tout effacer ?",
                "Confirmation",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            spacePanel.getObjets().clear();
            spacePanel.clearColors();
            spacePanel.clearSatellites();
            planeteCombo.removeAllItems();
            planeteMap.clear();
            luneMap.clear();
            satelliteMap.clear();
            planeteCounter = 1;
            luneCounter = 1;
            asteroideCounter = 1;
            satelliteCounter = 1;
            resetFields();
            spacePanel.repaint();
            updateAstreOrbiteCombo();
        }
    }

    /**Créer des asétroides mutliples*/

    private void createMultipleAsteroids() {
        try {
            // Demander le nombre d'astéroïdes
            String input = JOptionPane.showInputDialog(this,
                    "Nombre d'astéroïdes à créer :",
                    "Création d'astéroïdes",
                    JOptionPane.QUESTION_MESSAGE);

            if (input == null) return; // Annulé par l'utilisateur

            int nombre = Integer.parseInt(input);

            if (nombre <= 0) {
                showError("Le nombre doit être positif !");
                return;
            }

            if (nombre > 1000) {
                showError("Le nombre maximum est 1000 !");
                return;
            }

            // Créer les astéroïdes
            for (int i = 0; i < nombre; i++) {
                createRandomAsteroide();
            }

            showSuccess(nombre + " astéroïdes créés avec succès !");

        } catch (NumberFormatException ex) {
            showError("Veuillez entrer un nombre valide.");
        }
    }
    /**créer un astéroïde aléaotoirement autour du centre */
    private void createRandomAsteroide() {
        // Générer des valeurs aléatoires
        String nom = "Astéroïde_" + asteroideCounter;

        // Position aléatoire dans une zone autour du centre
        double posX = (Math.random() - 0.5) * 2000; // -1000 à +1000
        double posY = (Math.random() - 0.5) * 2000; // -1000 à +1000

        // Vitesse aléatoire
        double velociteX = (Math.random() - 0.5) * 100; // -50 à +50 px/s
        double velociteY = (Math.random() - 0.5) * 100; // -50 à +50 px/s

        // Taille et masse aléatoires
        double taille = 5 + Math.random() * 15; // 5 à 20 pixels
        double rayon = taille / 2;
        double masse = taille * 10; // Masse proportionnelle à la taille

        Point2D position = new Point2D.Double(posX, posY);
        Asteroides asteroide = new Asteroides(nom, position, masse, rayon, velociteX, velociteY, taille);

        // Couleur aléatoire (nuances de gris/brun)
        int grayValue = 100 + (int)(Math.random() * 100);
        Color asteroidColor = new Color(grayValue, grayValue, grayValue - 20);
        spacePanel.setCouleursObjets(asteroide, asteroidColor);

        // Ajouter au SpacePanel
        spacePanel.addObjet(asteroide);

        asteroideCounter++;
    }

    //Deepseek

    /**Afficher erreur */
    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Erreur", JOptionPane.ERROR_MESSAGE);
    }
    /**Affiche avertissement*/
    private void showWarning(String message) {
        JOptionPane.showMessageDialog(this, message, "Attention", JOptionPane.WARNING_MESSAGE);
    }
    /** Afficher succès*/
    private void showSuccess(String message) {
        JOptionPane.showMessageDialog(this, message, "Succès", JOptionPane.INFORMATION_MESSAGE);
    }

    //Fin Deepseek
    /**Sauvegarder la simulation */
    private void saveSimulation() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Sauvegarder la simulation");
        fileChooser.setSelectedFile(new File("simulation.dat"));

        int userSelection = fileChooser.showSaveDialog(this);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(fileToSave))) {
                // Créer un objet de sauvegarde avec toutes les données nécessaires
                BaseDeDonnes data = new BaseDeDonnes(
                        new ArrayList<>(spacePanel.getObjets()),
                        spacePanel.getcouleursMap(),
                        planeteMap,
                        luneMap,
                        satelliteMap,
                        planeteCounter,
                        luneCounter,
                        asteroideCounter,
                        satelliteCounter
                );

                oos.writeObject(data);
                showSuccess("Simulation sauvegardée avec succès !");
            } catch (IOException ex) {
                showError("Erreur lors de la sauvegarde: " + ex.getMessage());
            }
        }
    }

    /**Charger simulation */
    private void loadSimulation() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Charger une simulation");

        int userSelection = fileChooser.showOpenDialog(this);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToLoad = fileChooser.getSelectedFile();
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(fileToLoad))) {
                BaseDeDonnes data = (BaseDeDonnes) ois.readObject();

                // Confirmer le chargement
                int confirm = JOptionPane.showConfirmDialog(this,
                        "Charger cette simulation effacera la simulation actuelle. Continuer ?",
                        "Confirmation",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE);

                if (confirm == JOptionPane.YES_OPTION) {
                    // Restaurer les données
                    spacePanel.getObjets().clear();
                    spacePanel.getObjets().addAll(data.getObjets());
                    spacePanel.setcouleursMap(data.getCustomColors());

                    planeteMap.clear();
                    planeteMap.putAll(data.getPlaneteMap());
                    luneMap.clear();
                    luneMap.putAll(data.getLuneMap());
                    satelliteMap.clear();
                    satelliteMap.putAll(data.getSatelliteMap());

                    planeteCounter = data.getPlaneteCompteur();
                    luneCounter = data.getLuneCounter();
                    asteroideCounter = data.getAsteroideCompteur();
                    satelliteCounter = data.getSatelliteCompteur();

                    // Mettre à jour le combo des planètes
                    planeteCombo.removeAllItems();
                    for (String nom : planeteMap.keySet()) {
                        planeteCombo.addItem(nom);
                    }

                    spacePanel.repaint();
                    showSuccess("Simulation chargée avec succès !");
                }
            } catch (IOException | ClassNotFoundException ex) {
                showError("Erreur lors du chargement: " + ex.getMessage());
            }
        }
    }

    /**
     * Remplit les champs de coordonnées à partir des coordonnées de la souris.
     * <p>
     * Pour les satellites, les coordonnées renseignent la destination.
     * Pour les planètes et astéroïdes, elles renseignent la position initiale.
     * Les lunes ne sont pas affectées.
     * </p>
     *
     * @param coords les coordonnées (x, y) de la souris
     */
    public void setCoordSouris(Point2D coords) {
        String type = (String) choixobjet.getSelectedItem();

        if ("Satellite".equals(type)) {
            // Pour les satellites, remplir les champs de destination
            destXField.setText(String.format("%.0f", coords.getX()));
            destYField.setText(String.format("%.0f", coords.getY()));
        } else if (!"Lune".equals(type)) {
            // Pour planètes et astéroïdes, remplir les champs de position
            posXField.setText(String.format("%.0f", coords.getX()));
            posYField.setText(String.format("%.0f", coords.getY()));
        }
    }
}
