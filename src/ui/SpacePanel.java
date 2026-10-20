package ui;

import modele.*;
import physique.ForceGravitationnelle;
import physique.collision.*;
import javax.swing.*;
import javax.swing.Timer;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 * Panneau de visualisation et de simulation spatiale.
 * <p>
 * Cette classe gère l'affichage graphique et la simulation physique d'objets spatiaux
 * (planètes, lunes, satellites, astéroïdes). Elle permet le zoom, le déplacement de la vue,
 * et l'interaction avec les objets via la souris.
 * </p>
 * <p>
 * La simulation physique inclut :
 * <ul>
 *   <li>Calcul des forces gravitationnelles entre objets</li>
 *   <li>Gestion des collisions</li>
 *   <li>Déplacement des satellites selon leur trajectoire</li>
 *   <li>Orbites des lunes autour des planètes</li>
 * </ul>
 * </p>
 */
 public class SpacePanel extends JPanel implements ActionListener, MouseWheelListener, MouseMotionListener{
    /** Liste des objets spatiaux présents dans la simulation */
     private List<ObjetSpatial> objets = new ArrayList<>();
    /** Association entre objets spatiaux et leurs couleurs personnalisées */
    private Map<ObjetSpatial, Color> couleurs = new HashMap<>();
    /** Gestionnaire responsable de la détection et résolution des collisions */
    private GestionnaireCollisions GestionnaireCollisions;
    /** Facteur de zoom actuel de la vue (1.0 = zoom normal) */
    private double zoom = 1.0;
    /** Décalage horizontal de la vue en pixels */
    private double offsetX = 0;
    /** Décalage vertical de la vue en pixels */
    private double offsetY = 0;
    /** Timer gérant la boucle de simulation à ~60 FPS */
    private Timer timer;
    /** Dernière position de la souris pour le drag */
    private Point lastMouse;
    /** Coordonnées de la souris dans le système de coordonnées du monde */
    private Point2D coordoneesSouris;
    /** Indicateur d'activation de la simulation physique */
    private boolean physiqueEnMarche = true;
    /** Intervalle de temps entre chaque frame (~16ms pour 60 FPS) */
    private double deltaTime = 0.016; // ~60 FPS (16ms)
    /** Référence au panneau de contrôle pour synchronisation */
    private ControlPanel controlPanel;

    /**
     * Constructeur du panneau spatial.
     * <p>
     * Initialise le panneau avec un fond noir, démarre le timer de simulation,
     * et configure les listeners pour l'interaction utilisateur (souris, molette, drag).
     * </p>
     */
    public SpacePanel() {
        setBackground(Color.BLACK);
        timer = new Timer(16, this); // ~60 FPS
        timer.start();
        addMouseWheelListener(this);
        addMouseMotionListener(this);
        this.GestionnaireCollisions = new GestionnaireCollisions(objets);


        //Deepseek
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                lastMouse = e.getPoint();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                lastMouse = null;
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                // Clic pour remplir les coordonnées
                if (coordoneesSouris != null && controlPanel != null) {
                    controlPanel.setCoordSouris(coordoneesSouris);
                }
            }
        });
        //Fin Deepseek
    }

    /**
     * Définit la référence au panneau de contrôle.
     * <p>
     * Permet la synchronisation entre le panneau spatial et le panneau de contrôle,
     * notamment pour remplir automatiquement les champs de coordonnées lors d'un clic.
     * </p>
     *
     * @param cp le panneau de contrôle à associer
     */
    public void setControlPanel(ControlPanel cp) {
        this.controlPanel = cp;
    }
    /**
     * Ajoute un objet spatial à la simulation.
     *
     * @param o l'objet spatial à ajouter
     */
    public void addObjet(ObjetSpatial o) {
        objets.add(o);
        repaint();
    }
    /**
     * Retire un objet spatial de la simulation.
     * <p>
     * Supprime également sa couleur personnalisée si elle existe.
     * </p>
     *
     * @param o l'objet spatial à retirer
     */
    public void removeObjet(ObjetSpatial o) {
        objets.remove(o);
        couleurs.remove(o);
        repaint();
    }
    /**
            * Retourne la liste des objets spatiaux présents dans la simulation.
            *
            * @return la liste des objets spatiaux
     */
    public List<ObjetSpatial> getObjets() {
        return objets;
    }


    /**
     * Supprime tous les satellites de la simulation.
     * <p>
     * Conserve les autres types d'objets spatiaux (planètes, lunes, astéroïdes).
     * </p>
     */
    public void clearSatellites() {
        objets.removeIf(o -> o instanceof Satellites);
    }

    /**
     * Efface toutes les couleurs personnalisées.
     * <p>
     * Les objets reviendront à leurs couleurs par défaut.
     * </p>
     */    public void setCouleursObjets(ObjetSpatial o, Color c) {
        couleurs.put(o, c);
    }

    /**
     * Efface toutes les couleurs personnalisées.
     * <p>
     * Les objets reviendront à leurs couleurs par défaut.
     * </p>
     */
    public void clearColors() {
        couleurs.clear();
    }

    /**
     * Centre la vue sur un objet spatial spécifique.
     *
     * @param o l'objet spatial sur lequel centrer la vue
     */
    //Deepseek
    public void centerOnObject(ObjetSpatial o) {
        Point2D pos = o.getPosition();
        offsetX = getWidth() / 2.0 - pos.getX() * zoom;
        offsetY = getHeight() / 2.0 - pos.getY() * zoom;
        repaint();
    }
    //Deepseek

        /**
            * Réinitialise la vue à sa position et zoom par défaut.
            * <p>
            * Centre la vue sur l'origine (0, 0) avec un zoom de 1.0.
            * </p>
         */
    public void resetView() {
        offsetX = 0;
        offsetY = 0;
        zoom = 1.0;
        repaint();
    }

    /**
     * Active ou désactive la simulation physique.
     *
     * @param marche true pour activer, false pour désactiver
     */
    public void setphysiqueEnMarche(boolean marche) {
        this.physiqueEnMarche = marche;
    }
    /**
     * Indique si la simulation physique est actuellement activée.
     *
     * @return true si la physique est activée, false sinon
     */
    public boolean isphysiqueEnMarche() {
        return physiqueEnMarche;
    }
    /**
     * Retourne le facteur de zoom actuel.
     *
     * @return le niveau de zoom (1.0 = normal)
     */
    public double getZoom() {
        return zoom;
    }
    /**
     * Définit le facteur de zoom.
     * <p>
     * La valeur est limitée entre 0.1 et 10.0 pour des raisons de stabilité.
     * </p>
     *
     * @param zoom le nouveau niveau de zoom
     */
    public void setZoom(double zoom) {
        this.zoom = Math.max(0.1, Math.min(zoom, 10.0));
        repaint();
    }
    /**
     * Dessine les orbites des lunes autour de leurs planètes parentes.
     * <p>
     * Les orbites sont représentées par des cercles en pointillés bleus.
     * </p>
     *
     * @param g2 le contexte graphique 2D
     */
    public void dessinerOrbites(Graphics2D g2){
        for (ObjetSpatial o : objets) {
            if (o instanceof Lune) {
                Lune lune = (Lune) o;
                if (lune.getPlaneteParente() != null) {
                    Point2D posPlanete = lune.getPlaneteParente().getPosition();
                    double rayonOrbital = lune.getRayonOrbital();

                    // Dessiner l'orbite en pointillés
                    g2.setColor(new Color(100, 100, 255, 100));
                    g2.setStroke(new BasicStroke(1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL,
                            0, new float[]{5, 5}, 0));
                    g2.draw(new Ellipse2D.Double(
                            posPlanete.getX() - rayonOrbital,
                            posPlanete.getY() - rayonOrbital,
                            2 * rayonOrbital,
                            2 * rayonOrbital
                    ));
                    g2.setStroke(new BasicStroke(1.0f));

                    // Afficher le rayon orbital
                    g2.setColor(Color.CYAN);
                    g2.setFont(new Font("Arial", Font.PLAIN, (int)(10 / zoom)));
                }
            }
        }

    }
    /**
     * Dessine tous les objets spatiaux présents dans la simulation.
     * <p>
     * Affiche également les noms des planètes, lunes et identifiants des satellites.
     * Pour les satellites, affiche aussi leur niveau de carburant.
     * </p>
     *
     * @param g2 le contexte graphique 2D
     */
    public void dessinerPorteeCamera(Graphics2D g2){
        for (ObjetSpatial o : objets) {
            if (o instanceof Satellites) {
                Satellites sat = (Satellites) o;
                Point2D posSat = sat.getPosition();
                double portee = sat.getPorteeCamera();

                // Cercle de portée en jaune transparent
                g2.setColor(new Color(255, 255, 0, 30));
                g2.fill(new Ellipse2D.Double(
                        posSat.getX() - portee,
                        posSat.getY() - portee,
                        2 * portee,
                        2 * portee
                ));

                // Bordure du cercle de portée
                g2.setColor(new Color(255, 255, 0, 100));
                g2.setStroke(new BasicStroke((float)(1.5 / zoom)));
                g2.draw(new Ellipse2D.Double(
                        posSat.getX() - portee,
                        posSat.getY() - portee,
                        2 * portee,
                        2 * portee
                ));
                g2.setStroke(new BasicStroke(1.0f));
            }
        }
    }
    /**
     * Affiche les coordonnées mondiales de la souris en haut à droite de l'écran.
     *
     * @param g2 le contexte graphique 2D
     */
    public void dessinerObjets(Graphics2D g2){
        for (ObjetSpatial o : objets) {
            Point2D p = o.getPosition();
            double r = o.getRayon();

            // Utiliser couleur personnalisée ou couleur automatique
            Color couleur = couleurs.getOrDefault(o, getCouleurs(o));
            g2.setColor(couleur);
            g2.fill(new Ellipse2D.Double(p.getX() - r, p.getY() - r, 2 * r, 2 * r));

            // Bordure blanche
            g2.setColor(Color.WHITE);
            g2.draw(new Ellipse2D.Double(p.getX() - r, p.getY() - r, 2 * r, 2 * r));

            // Afficher le nom seulement pour les planètes et lunes
            String nom = "";
            if (o instanceof Planete) {
                nom = ((Planete) o).getNom();
            } else if (o instanceof Lune) {
                nom = ((Lune) o).getNom();
            }
            else if ((o instanceof Satellites)){
                nom= String.valueOf(((Satellites) o ).getIdentifiant());
            }

            if (!nom.isEmpty()) {
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("Arial", Font.PLAIN, (int)(12 / zoom)));
                g2.drawString(nom, (float)(p.getX() + r + 5), (float)(p.getY()));
            }

            if (o instanceof Satellites){
                g2.drawString(String.valueOf(((Satellites) o ).getCarburant()), (float)(p.getX() + r + 5), (float)(p.getY() + 15));
            }
        }
    }

    /**
     * Dessine la trajectoire planifiée des satellites non en orbite.
     * <p>
     * La trajectoire est représentée par une ligne en pointillés jaune.
     * </p>
     *
     * @param g2 le contexte graphique 2D
     */
    public void afficherCoordonnees(Graphics2D g2){
        if (coordoneesSouris != null) {
            // Revenir aux coordonnées écran pour l'affichage
            g2.setTransform(new java.awt.geom.AffineTransform());
            g2.setColor(Color.CYAN);
            g2.setFont(new Font("Monospaced", Font.BOLD, 14));
            String coords = String.format("X: %.0f, Y: %.0f", coordoneesSouris.getX(), coordoneesSouris.getY());
            g2.drawString(coords, getWidth() - 180, 30);
        }
    }
    /**
     * Dessine les orbites circulaires des satellites en orbite autour d'un astre.
     * <p>
     * Les orbites sont représentées par des cercles en pointillés verts.
     * </p>
     *
     * @param g2 le contexte graphique 2D
     */
    public void dessinerTrajectoire(Graphics2D g2){
        for (ObjetSpatial o : objets) {
            if (o instanceof Satellites) {
                Satellites satellite = (Satellites) o;
                if (satellite.isEnOrbite()) continue;
                List<Point2D> points=((Satellites) o).getTrajectoire().getPoints();
                g2.setColor(new Color(243, 236, 113, 187));
                g2.setStroke(new BasicStroke(1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL,
                        0, new float[]{5, 5}, 0));
                for (int i = 0; i < points.size() - 1; i++) {
                    Point2D p1 = points.get(i);
                    Point2D p2 = points.get(i + 1);
                    g2.drawLine((int)p1.getX(), (int)p1.getY(),
                            (int)p2.getX(), (int)p2.getY());
                }
                g2.setStroke(new BasicStroke(1.0f));
                g2.setColor(Color.CYAN);
                g2.setFont(new Font("Arial", Font.PLAIN, (int)(10 / zoom)));
            }
        }
    }
    /**
     * Dessine les orbites circulaires des satellites en orbite autour d'un astre.
     * <p>
     * Les orbites sont représentées par des cercles en pointillés verts.
     * </p>
     *
     * @param g2 le contexte graphique 2D
     */
    public void dessinerOrbitesSatellites(Graphics2D g2) {
        for (ObjetSpatial o : objets) {
            if (o instanceof Satellites) {
                Satellites satellite = (Satellites) o;
                if (satellite.isEnOrbite() && satellite.getAstreOrbite() != null) {
                    Point2D posAstree = satellite.getAstreOrbite().getPosition();
                    double rayonOrbital = satellite.getRayonOrbital();

                    // Dessiner l'orbite du satellite en pointillés verts
                    g2.setColor(new Color(0, 255, 0, 100));
                    g2.setStroke(new BasicStroke(1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL,
                            0, new float[]{3, 3}, 0));
                    g2.draw(new Ellipse2D.Double(
                            posAstree.getX() - rayonOrbital,
                            posAstree.getY() - rayonOrbital,
                            2 * rayonOrbital,
                            2 * rayonOrbital
                    ));
                    g2.setStroke(new BasicStroke(1.0f));

                    // Afficher les informations orbitales
                    g2.setColor(Color.GREEN);
                    g2.setFont(new Font("Arial", Font.PLAIN, (int)(10 / zoom)));
                }
            }
        }
    }



    /**
     * Méthode de rendu principal du panneau.
     * <p>
     * Gère l'application du zoom, de la translation, et l'affichage de tous
     * les éléments visuels (objets, orbites, trajectoires, etc.).
     * </p>
     *
     * @param g le contexte graphique
     */
    //Claude
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Appliquer la translation et le zoom
        g2.translate(getWidth() / 2.0 + offsetX, getHeight() / 2.0 + offsetY);
        g2.scale(zoom, zoom);

        // Dessiner un repère central
        drawCrossHair(g2);
    //Fin claude
        // Dessiner les orbites des lunes
        dessinerOrbites(g2);
        // Dessiner la portée de la caméra des satellites
        dessinerPorteeCamera(g2);
        //afficher orbite des satellites
        dessinerOrbitesSatellites(g2);
        // Dessiner chaque objet
        dessinerObjets(g2);
        // Dessiner la trajectoire des satellites
        dessinerTrajectoire(g2);
        // Afficher les coordonnées de la souris en haut à droite
        afficherCoordonnees(g2);



    }
    /**
     * Dessine une croix au centre du repère (origine 0,0).
     *
     * @param g2 le contexte graphique 2D
     */
    private void drawCrossHair(Graphics2D g2) {
        g2.setColor(new Color(100, 100, 100, 100));
        g2.drawLine(-20, 0, 20, 0);
        g2.drawLine(0, -20, 0, 20);
    }
    /**
     * Détermine la couleur d'affichage d'un objet spatial.
     * <p>
     * Priorité : couleur personnalisée > couleur spécifique (astéroïdes gris)
     * > couleur générée à partir de l'identifiant.
     * </p>
     *
     * @param o l'objet spatial
     * @return la couleur à utiliser pour cet objet
     */
    private Color getCouleurs(ObjetSpatial o) {
        // Vérifier d'abord si une couleur personnalisée existe
        Color customColor = couleurs.get(o);
        if (customColor != null) {
            return customColor;
        }

        // Si c'est un astéroïde sans couleur personnalisée, retourner gris
        if (o instanceof Asteroides) {
            return Color.GRAY;
        }

        // Pour les autres objets, générer une couleur basée sur l'identifiant
        int id = o.getIdentifiant();
        return new Color(
                Math.abs((id * 127) % 156) + 100,
                Math.abs((id * 231) % 156) + 100,
                Math.abs((id * 179) % 156) + 100
        );
    }
    /**
     * Gère la boucle de simulation physique appelée à chaque frame.
     * <p>
     * Cette méthode :
     * <ul>
     *   <li>Calcule les forces gravitationnelles</li>
     *   <li>Met à jour les positions des objets</li>
     *   <li>Déplace les satellites selon leur trajectoire</li>
     *   <li>Détecte les collisions</li>
     *   <li>Gère la détection d'objets par les satellites</li>
     * </ul>
     * </p>
     *
     * @param e l'événement d'action du timer
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        if (!physiqueEnMarche || objets.isEmpty()) {
            repaint();
            return;
        }
        try {
            for (ObjetSpatial objet : objets) {
                if (objet instanceof Satellites) {
                    Satellites satellite = (Satellites) objet;
                    if (satellite.isEnOrbite()) {
                        // Satellite en orbite : la gravité s'occupe naturellement du mouvement
                        double[] forceTotale = ForceGravitationnelle.calculerForceTotale(satellite, objets);
                        ForceGravitationnelle.appliquerForce(satellite, forceTotale, deltaTime);
                        satellite.updatePosition(deltaTime);
                        satellite.getAntenne().envoyerObjetsDetectes(satellite.detecterAstres(objets));
                    } else {

                        boolean trajectoireTerminee = satellite.deplacerSatelliteProgressif(satellite.getTrajectoire().getPoints(), deltaTime);
                        double[] forceTotale = ForceGravitationnelle.calculerForceTotale(satellite, objets);
                        ForceGravitationnelle.appliquerForce(satellite, forceTotale, deltaTime);
                        satellite.updatePosition(deltaTime);

                        satellite.getAntenne().envoyerObjetsDetectes(satellite.detecterAstres(objets));

                        if (trajectoireTerminee) {
                           System.out.println("Satellite " + satellite.getIdentifiant() + " a terminé sa trajectoire");
                        }
                    }
                } else {
                    double[] forceTotale = ForceGravitationnelle.calculerForceTotale(objet, objets);
                    ForceGravitationnelle.appliquerForce(objet, forceTotale, deltaTime);
                    objet.updatePosition(deltaTime);
                    if (objet instanceof Lune) {
                        Lune lune = (Lune) objet;
                        if (lune.getPlaneteParente() != null) {
                            Point2D posLune = lune.getPosition();
                            Point2D posPlanete = lune.getPlaneteParente().getPosition();
                            double distanceActuelle = posLune.distance(posPlanete);
                            double rayonOrbital = lune.getRayonOrbital();

                            if (Math.abs(distanceActuelle - rayonOrbital) > rayonOrbital * 0.1) {
                                System.out.println("⚠️  Lune " + lune.getNom() +
                                        " - Distance actuelle: " + String.format("%.1f", distanceActuelle) +
                                        " vs Rayon orbital: " + String.format("%.1f", rayonOrbital) +
                                        " (écart: " + String.format("%.1f", distanceActuelle - rayonOrbital) + ")");
                            }
                        }
                    }
                }
            }
        } catch (Exception ex) {
            System.err.println("Erreur dans le calcul gravitationnel: " + ex.getMessage());
            ex.printStackTrace();
        }


        try {
            if (GestionnaireCollisions != null) {
                GestionnaireCollisions.setObjets(objets);
                GestionnaireCollisions.gererCollisions();

            }
        } catch (Exception ex) {
            System.err.println("Erreur dans la gestion des collisions: " + ex.getMessage());
            ex.printStackTrace();
        }

        repaint();
    }
    //Claude
    /**
     * Gère le zoom via la molette de la souris.
     *
     * @param e l'événement de la molette
     * */
    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        double factor = (e.getPreciseWheelRotation() < 0) ? 1.1 : 0.9;
        zoom *= factor;
        zoom = Math.max(0.1, Math.min(zoom, 10.0));
        repaint();
    }
    /**
     * Gère le déplacement de la vue par glisser-déposer.
     *
     * @param e l'événement de déplacement de la souris
     */
    @Override
    public void mouseDragged(MouseEvent e) {
        if (lastMouse != null) {
            offsetX += e.getX() - lastMouse.x;
            offsetY += e.getY() - lastMouse.y;
            repaint();
        }
        lastMouse = e.getPoint();
    }
    /**
     * Met à jour les coordonnées mondiales de la souris lors de son déplacement.
     *
     * @param e l'événement de mouvement de la souris
     */
    @Override
    public void mouseMoved(MouseEvent e) {
        lastMouse = e.getPoint();
        // Convertir les coordonnées écran en coordonnées monde
        updatecoordoneesSouris(e.getX(), e.getY());
        repaint();
    }
    //FinClaude

    /**
     * Convertit les coordonnées écran en coordonnées du système monde.
     * <p>
     * Prend en compte le zoom et les décalages de vue pour effectuer la conversion.
     * </p>
     *
     * @param ecranX coordonnée X à l'écran
     * @param ecranY coordonnée Y à l'écran
     */
    private void updatecoordoneesSouris(int ecranX, int ecranY) {
        coordoneesSouris = new Point2D.Double((ecranX - getWidth() / 2.0 - offsetX) / zoom, (ecranY - getHeight() / 2.0 - offsetY) / zoom);
    }
    /**
     * Retourne une copie de la map des couleurs personnalisées.
     *
     * @return une nouvelle map contenant les associations objet-couleur
     */
    public Map<ObjetSpatial, Color> getcouleursMap() {
        return new HashMap<>(couleurs);
    }

    /**
     * Définit la map des couleurs personnalisées depuis une sauvegarde.
     * <p>
     * Remplace complètement les couleurs existantes par celles fournies.
     * </p>
     *
     * @param colors la map des couleurs à restaurer
     */
    public void setcouleursMap(Map<ObjetSpatial, Color> colors) {
        couleurs.clear();
        couleurs.putAll(colors);
    }
}
