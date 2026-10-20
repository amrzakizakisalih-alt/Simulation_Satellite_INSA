package modele;
import Trajectoires.Trajectoire;
import physique.ForceGravitationnelle;
import reseau.Antenne;
import java.awt.geom.Point2D;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Représente un satellite capable de se déplace de détecter des objets spatiaux
 * de communiquer via une antenne et de gérer un stock de carburant
 * Gérance également des opérations orbitales de la propulsion et du déplacement suivant
 * la trajectoire.
 */

public class Satellites extends ObjetSpatial implements Serializable {
    /**Identifiant unique pour vérifier comptabilité entre sérialisation et désérialisation*/
    private static final long serialVersionUID = 1L;
    /** Antenne permettant communication avec serveur*/
    private Antenne antenne;
    /** Trajectoire suivie par le satellite lors de son déplacement*/
    private Trajectoire trajectoire;
    /** Quantité de carburant restante en kg*/
    private double carburant;
    /** Point de la destination du satellite*/
    private Point2D destination;
    /**Portée maximale de la caméra */
    private double porteeCamera;
    /**Durée estimée pour atteindre la destination */
    private double dureeEstimee;
    /**Distance totale parcourue depuis début du déplacement */
    private double distanceparcourue;
    /**Liste des objets spatiaux détéctés */
    private List<ObjetSpatial> astresDetectes;
    /**Position finale à atteindre */
    private Point2D positionFinale;
    /**Impulsion spécifique */
    private double isp;
    /**Accélération gravitationnelle*/
    private double go= 9.80665;
    /**Index du point ciblé dans la trajectoire */
    private int indexWaypointActuel = 0;
    /**Set contenant les objets déjà envoyés */
    private Set<Integer> objetsDejaEnvoyes = new HashSet<>();
    /**Objet spatial sur lequel le satellite orbite */
    private ObjetSpatial astreOrbite;
    /**Rayon de l'orbite circulaire */
    private double rayonOrbital;
    /**Indique si satellite est en orbite */
    private boolean enOrbite = false;

    /**
     * Construit un satellite avec sa position initiale, sa masse, son rayon, sa vitesse,
     * son identifiant, son carburant et sa portée caméra.
     * @param positionFinale position finale à atteindre
     * @param masse masse du satellite
     * @param rayon rayon du satellite
     * @param velociteX vitesse initiale selon X
     * @param velociteY vitesse initiale selon Y
     * @param position position actuelle du satellite
     * @param identifiant identifiant unique
     * @param carburant quantité de carburant
     * @param porteeCamera portée  de la caméra
     */

    public Satellites(Point2D positionFinale,double masse, double rayon, double velociteX, double velociteY, Point2D position, int identifiant, double carburant,double porteeCamera) {
        super(position, masse, rayon, identifiant, velociteX, velociteY);
        this.carburant = carburant;
        this.porteeCamera = porteeCamera;
        this.dureeEstimee = 0;
        this.distanceparcourue = 0;
        this.astresDetectes = new ArrayList<>();
        this.positionFinale = positionFinale;
    }

    public Antenne getAntenne() {
        return antenne;
    }
    public double getIsp() {
        return isp;
    }
    public void setIsp(double isp) {
        this.isp = isp;
    }
    public void setAntenne(Antenne antenne) {
        this.antenne = antenne;
    }
    public double getDureeEstimee() {
        return dureeEstimee;
    }
    public void setDureeEstimee(double dureeEstimee) {
        this.dureeEstimee = dureeEstimee;
    }
    public double getGo() {
        return go;
    }
    public void setGo(double go) {
        this.go = go;
    }



    public double getCarburant() {
        return carburant;
    }

    public Point2D getDestination() {
        return destination;
    }

    public Trajectoire getTrajectoire() {
        return trajectoire;
    }

    public void setCarburant(double carburant) {
        this.carburant = carburant;
    }

    public void setDestination(Point2D destination) {
        this.destination = destination;
    }

    public void setDureeEstimee(int dureeEstimee) {
        this.dureeEstimee = dureeEstimee;
    }

    public void setAstresDetectes(List<ObjetSpatial> astresDetectes) {
        this.astresDetectes = astresDetectes;
    }

    public List<ObjetSpatial> getAstresDetectes() {
        return astresDetectes;
    }

    public void setTrajectoire(Trajectoire trajectoire) {
        this.trajectoire = trajectoire;
    }
    public double getPorteeCamera() {
        return porteeCamera;
    }
    public void setPorteeCamera(double porteeCamera) {
        this.porteeCamera = porteeCamera;
    }
    public double getDistanceparcourue() {
        return distanceparcourue;
    }
    public void setDistanceparcourue(double distanceparcourue) {
        this.distanceparcourue = distanceparcourue;
    }
    public List<ObjetSpatial> getAstresLus() {
        return getAntenne().demanderObjets();
    }

    public Point2D getPositionFinale() {
        return positionFinale;
    }
    public void setPositionFinale(Point2D positionFinale) {
        this.positionFinale = positionFinale;
    }
    public void reinitialiserTrajectoire() {
        distanceparcourue = 0.0;
    }

    public ObjetSpatial getAstreOrbite() {
        return astreOrbite;
    }

    public void setAstreOrbite(ObjetSpatial astreOrbite) {
        this.astreOrbite = astreOrbite;
    }

    public double getRayonOrbital() {
        return rayonOrbital;
    }

    public void setRayonOrbital(double rayonOrbital) {
        this.rayonOrbital = rayonOrbital;
    }

    public boolean isEnOrbite() {
        return enOrbite;
    }

    public void setEnOrbite(boolean enOrbite) {
        this.enOrbite = enOrbite;

    }

    /**
     * Détecte les objets spatiaux présents dans la portée de la caméra du satellite.
     * @param astresDisponibles liste des objets spatiaux présents dans l'environnement
     * @return liste mise à jour des objets spatiaux détectés
     */

    public List<ObjetSpatial> detecterAstres(List<ObjetSpatial> astresDisponibles) {

        for (ObjetSpatial astre : astresDisponibles) {
            if (astre.getPosition()!= null && !objetsDejaEnvoyes.contains(astre.getIdentifiant())) {

                if (getPosition().distance(astre.getPosition()) <= porteeCamera) {
                    astresDetectes.add(astre);
                    objetsDejaEnvoyes.add(astre.getIdentifiant());
                }
            }
        }
        return astresDetectes;
    }


    /**
     * Vérifie si le satellite est en état d'opération (carburant et destination définis).
     *
     * @return true si le satellite peut opérer, false sinon
     */
    public boolean estOperation(){
        if (destination != null && carburant > 0) {
            return true;
        }
        return false;
    }
    /**
     * Calcule le delta-V disponible selon l'équation de Tsiolkovski.
     *
     * @return valeur du delta-V disponible
     */
    public double deltaVDisponible() {
        return isp * go * Math.log((getMasse()+ carburant) / getMasse());
    }
    /**
     * Calcule la quantité de carburant nécessaire pour produire un delta-V donné.
     *
     * @param deltaV delta-V souhaité
     * @return carburant nécessaire
     */
    public double carburantNecessaire(double deltaV) {
        return (getMasse()+carburant) * (1 - Math.exp(-deltaV / (isp*go)));

    }
    /**
     * Place le satellite en orbite circulaire autour d'un objet spatial donné.
     *
     * @param astre objet spatial
     * @param rayonOrbital rayon de l'orbite circulaire
     */
    public void placerEnOrbite(ObjetSpatial astre, double rayonOrbital) {
        double angle,x,y;
        double[] vitesseOrbitale;
        this.astreOrbite = astre;
        this.rayonOrbital = rayonOrbital;
        this.enOrbite = true;
        angle = Math.random() * 2 * Math.PI;
        x = astre.getPosition().getX() + rayonOrbital * Math.cos(angle);
        y = astre.getPosition().getY() + rayonOrbital * Math.sin(angle);

        this.setPosition(new Point2D.Double(x, y));
        vitesseOrbitale = ForceGravitationnelle.calculerVitesseOrbitale(astre, this.getPosition(), rayonOrbital);
        this.setVelocite(vitesseOrbitale[0], vitesseOrbitale[1]);

    }


    /** Renvoie la norme
     * @param v liste double
     * @return norme des points
     * */
    public double norme (double[] v){
        double norme = 0;
        for (double d : v) {
            norme += d*d;
        }
        return Math.sqrt(norme);
    }

    /**Détruit le satellite et le déconnecte*/
    public void detruire() {
        if (this.antenne != null) {
            this.antenne.deconnecter();
            this.antenne = null;
        }
        this.trajectoire = null;
        this.destination = null;
        this.astresDetectes = null;

    }


    /**
     * Déplace le satellite de façon progressive le long d'une liste de points,
     * en appliquant propulsion, corrections et consommation.
     *
     * @param trajectoire liste des points à suivre
     * @param dt pas de temps de simulation
     * @return true si le déplacement est terminé, false sinon
     */

    public boolean deplacerSatelliteProgressif(List<Point2D> trajectoire, double dt) {
       double manoeuvreX,manoeuvreY,propulsionX,propulsionY;
        if (!validerConditionsDeplacement(trajectoire)) {
            return true;
        }
        Point2D waypointCible = trajectoire.get(indexWaypointActuel);
        Point2D positionActuelle = getPosition();

        if (estArriveAuWaypoint(waypointCible, positionActuelle)) {
            return gererArriveeWaypoint(dt);
        }
        double[] forces = calculerForcesDeDeplacement(waypointCible, positionActuelle);
        manoeuvreX = forces[0];
        manoeuvreY = forces[1];
        propulsionX = forces[2];
        propulsionY = forces[3];

        appliquerForcesAvecConsommation(manoeuvreX, manoeuvreY, propulsionX, propulsionY, dt);

        mettreAJourPosition(dt);

        return false;
    }
    /**
     * Valide les conditions nécessaires au déplacement du satellite :
     * — présence d’une trajectoire,
     * — carburant disponible,
     * — destination définie.
     *
     * @param trajectoire liste des points à suivre
     * @return true si les conditions sont valides, {@code false} sinon
     */
    private boolean validerConditionsDeplacement(List<Point2D> trajectoire) {
        if (trajectoire == null || trajectoire.isEmpty() || !estOperation()) {
            return false;
        }
        return indexWaypointActuel < trajectoire.size();
    }

    private boolean estArriveAuWaypoint(Point2D waypointCible, Point2D positionActuelle) {
        return norme(new double[]{waypointCible.getX() - positionActuelle.getX(),waypointCible.getY() - positionActuelle.getY()}) < getTrajectoire().getTOLERANCE_ARRIVEE();
    }
    /**
     * Gère l'arrivée au prochain point :
     * — passage au point suivant
     * — si dernier point : amorce du freinage d’arrêt
     *
     * @param dt pas de temps
     * @return true si le déplacement global est terminé
     */
    private boolean gererArriveeWaypoint(double dt) {
        if (indexWaypointActuel == getTrajectoire().getPoints().size() - 1) {
            return appliquerFreinageArrêt(dt);
        } else {
            indexWaypointActuel++;
            return false;
        }
    }
    /**
     * Calcule les forces appliquées lors du déplacement :
     * — corrections de trajectoire par manœuvres
     * — propulsion principale
     *
     * @param waypointCible point à atteindre
     * @param positionActuelle position du satellite
     * @return tableau [manoeuvreX, manoeuvreY, propulsionX, propulsionY]
     */
    private double[] calculerForcesDeDeplacement(Point2D waypointCible, Point2D positionActuelle) {
        double dx,dy,distance,vitesseX,vitesseY,vitesseActuelle;
        double[] forces, manoeuvres, propulsion;

        forces = new double[4]; // [manoeuvreX, manoeuvreY, propulsionX, propulsionY]

        dx = waypointCible.getX() - positionActuelle.getX();
        dy = waypointCible.getY() - positionActuelle.getY();
        distance = norme(new double[]{dx,dy});

        vitesseX = getVelociteX();
        vitesseY = getVelociteY();
        vitesseActuelle = norme(new double[]{vitesseX,vitesseY});


        manoeuvres = calculerManoeuvresCorrection(dx, dy, distance, vitesseX, vitesseY, vitesseActuelle);
        forces[0] = manoeuvres[0];
        forces[1] = manoeuvres[1];

        propulsion = calculerPropulsionPrincipale(dx, dy, distance, vitesseActuelle, (indexWaypointActuel == getTrajectoire().getPoints().size() - 1));
        forces[2] = propulsion[0];
        forces[3] = propulsion[1];

        return forces;
    }

    /**
     * Calcule les manœuvres de correction pour réaligner la direction réelle
     * avec la direction souhaitée vers le prochain waypoint.
     *
     * @return tableau contenant les composantes de correction
     */
    private double[] calculerManoeuvresCorrection(double dx, double dy, double distance, double vitesseX, double vitesseY, double vitesseActuelle) {
        double manoeuvreX,manoeuvreY,directionSouhaiteeX,directionSouhaiteeY,directionActuelleX,directionActuelleY,agressiviteManoeuvre;

        manoeuvreX = 0;
        manoeuvreY = 0;

        if (vitesseActuelle > 0.1) {
            directionSouhaiteeX = dx / distance;
            directionSouhaiteeY = dy / distance;
            directionActuelleX = vitesseX / vitesseActuelle;
            directionActuelleY = vitesseY / vitesseActuelle;

            double produitScalaire = directionActuelleX * directionSouhaiteeX + directionActuelleY * directionSouhaiteeY;
            double angle = Math.acos(Math.max(-1, Math.min(1, produitScalaire)));//à voir


            if (angle>Math.PI/4){
                agressiviteManoeuvre=2.5;
            }
            else {agressiviteManoeuvre=1;}

            if (angle > Math.PI / 180) {
                double intensiteManoeuvre = Math.min(angle * 3.0 * agressiviteManoeuvre, 2.0);

                double normePerpX = -directionActuelleY;
                double normePerpY = directionActuelleX;

                double determinant = directionActuelleX * directionSouhaiteeY - directionActuelleY * directionSouhaiteeX;
                if (determinant < 0) {
                    normePerpX = -normePerpX;
                    normePerpY = -normePerpY;
                }

                manoeuvreX = normePerpX * intensiteManoeuvre * 25.0 * agressiviteManoeuvre;
                manoeuvreY = normePerpY * intensiteManoeuvre * 25.0 * agressiviteManoeuvre;
            }

            if (angle > Math.PI / 180 && vitesseActuelle > 50) {
                appliquerRalentissementVirage();
            }
        }

        return new double[]{manoeuvreX, manoeuvreY};
    }
    /**
     * Calcule la propulsion principale permettant de progresser vers le waypoint.
     *
     * @param dx décalage en X
     * @param dy décalage en Y
     * @param distance distance restante
     * @param vitesseActuelle vitesse du satellite
     * @param estDernierWaypoint indique si le waypoint est le dernier de la trajectoire
     * @return propulsion appliquée en X et Y
     */
    private double[] calculerPropulsionPrincipale(double dx, double dy, double distance, double vitesseActuelle, boolean estDernierWaypoint) {
        double propulsionX,propulsionY,intensitePropulsion;
        

        propulsionX = 0;
        propulsionY = 0;

        if (estDernierWaypoint && distance < 300) {
            intensitePropulsion = Math.min(distance / 300.0, 1.0) * 0.3;
        } else {
            double facteurVirage = calculerFacteurVirage(dx, dy, distance, vitesseActuelle);
            intensitePropulsion = Math.min(distance / 100.0, 1.0) * 0.5 * facteurVirage;
        }

        if (distance > 30 || vitesseActuelle < 25) {
            double directionX = dx / distance;
            double directionY = dy / distance;

            propulsionX = directionX * intensitePropulsion * 12.0;
            propulsionY = directionY * intensitePropulsion * 12.0;
        }

        return new double[]{propulsionX, propulsionY};
    }
    /**
     * Calcule un facteur réduisant la propulsion dans les virages serrés
     * pour améliorer la stabilité du déplacement.
     *
     * @return facteur de réduction (entre 0.6 et 1.0)
     */
    private double calculerFacteurVirage(double dx, double dy, double distance, double vitesseActuelle) {
        double directionSouhaiteeX,directionSouhaiteeY,directionActuelleX,directionActuelleY;
        if (vitesseActuelle <= 0.1) return 1.0;

            directionSouhaiteeX = dx / distance;
            directionSouhaiteeY = dy / distance;
            directionActuelleX = getVelociteX() / vitesseActuelle;
            directionActuelleY = getVelociteY() / vitesseActuelle;

        double angle = Math.acos(Math.max(-1, Math.min(1, directionActuelleX * directionSouhaiteeX + directionActuelleY * directionSouhaiteeY)));

        return (angle > Math.PI / 6) ? 0.6 : 1.0;
    }
    /**
     * Réduit la vitesse du satellite pour améliorer la maniabilité
     * lors d’un virage serré.
     */
    private void appliquerRalentissementVirage() {
        setVelociteX(getVelociteX() * 0.7);
        setVelociteY(getVelociteY() * 0.7);
    }
    /**
     * Applique les forces (manœuvres + propulsion) en tenant compte de la
     * consommation de carburant et met à jour la vitesse.
     *
     * @param manoeuvreX composante X de correction
     * @param manoeuvreY composante Y de correction
     * @param propulsionX composante X de propulsion
     * @param propulsionY composante Y de propulsion
     * @param dt pas de temps de simulation
     */
    private void appliquerForcesAvecConsommation(double manoeuvreX, double manoeuvreY, double propulsionX, double propulsionY, double dt) {
        double accelerationTotaleX,accelerationTotaleY,accelerationTotale,deltaV,consommation;

        accelerationTotaleX = manoeuvreX + propulsionX;
        accelerationTotaleY = manoeuvreY + propulsionY;
        accelerationTotale = norme(new double[]{accelerationTotaleX,accelerationTotaleY });

        if (accelerationTotale > 0 && carburant > 0) {
            deltaV = accelerationTotale*dt;
            consommation = carburantNecessaire(deltaV);


            if (carburant >= consommation) {
                setVelociteX(getVelociteX() + accelerationTotaleX * dt);
                setVelociteY(getVelociteY() + accelerationTotaleY * dt);
                carburant -= consommation;
            } else {
                setVelociteX(getVelociteX() + accelerationTotaleX * dt * carburant / consommation);
                setVelociteY(getVelociteY() + accelerationTotaleY * dt * carburant / consommation);
                carburant = 0;
            }
        }
    }
    /**
     * Met à jour la position du satellite en fonction de la vitesse
     * et du pas de temps.
     *
     * @param dt pas de temps
     */
    private void mettreAJourPosition(double dt) {
        Point2D positionActuelle = getPosition();
        double deplacementX = getVelociteX() * dt;
        double deplacementY = getVelociteY() * dt;

        setPosition(new Point2D.Double(positionActuelle.getX() + deplacementX, positionActuelle.getY() + deplacementY));
        distanceparcourue += norme(new double[]{deplacementX,deplacementY});
    }
    /**
     * Met complètement à l’arrêt la vitesse du satellite.
     */
    private void arreterSatellite() {
        setVelociteX(0);
        setVelociteY(0);
    }
    /**
     * Applique un freinage progressif lors de l’arrivée au dernier waypoint,
     * en consommant le carburant nécessaire selon l’équation de Tsiolkovski.
     *
     * @param dt pas de temps
     * @return {@code true} lorsque l’arrêt complet est atteint
     */
    private boolean appliquerFreinageArrêt(double dt) {
        double vitesseActuelle, directionFreinX,directionFreinY;

        vitesseActuelle = norme(new double[]{getVelociteX(),getVelociteY()} );

        if (vitesseActuelle < 0.1) {
            arreterSatellite();
            indexWaypointActuel++;
            return true;
        }
        directionFreinX = -getVelociteX() / vitesseActuelle;
        directionFreinY = -getVelociteY()/ vitesseActuelle;

        // Intensité du freinage
        double accelerationFrein = Math.min(vitesseActuelle / dt, 30.0);
        double accelerationX = directionFreinX * accelerationFrein;
        double accelerationY = directionFreinY * accelerationFrein;

        // 🆕 CALCUL DE CONSOMMATION RÉALISTE
        double deltaV = accelerationFrein*dt;
        double consommation = carburantNecessaire(deltaV);

        if (carburant >= consommation) {
            setVelociteX(getVelociteX() + accelerationX * dt);
            setVelociteY(getVelociteY() + accelerationY * dt);
            carburant -= consommation;
        } else if (carburant > 0) {
            // Freinage partiel avec le carburant restant
            double ratio = carburant / consommation;
            setVelociteX(getVelociteX() + accelerationX * dt * ratio);
            setVelociteY(getVelociteY() + accelerationY * dt * ratio);
            carburant = 0;
        }

        // Mise à jour de la position même pendant le freinage
        Point2D positionActuelle = getPosition();
        setPosition(new Point2D.Double(positionActuelle.getX() + getVelociteX() * dt, positionActuelle.getY() + getVelociteY() * dt));

        return false;
    }




















}
