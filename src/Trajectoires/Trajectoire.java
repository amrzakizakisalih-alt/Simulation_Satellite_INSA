package Trajectoires;
import java.awt.geom.Point2D;
import java.util.*;
import modele.*;

/**
 * Représente une trajectoire calculée pour un satellite.
 * Cette classe utilise un algorithme de type A* pour déterminer un chemin optimal
 * entre un point de départ et un point d'arrivée, en tenant compte de l'énergie,
 * de la gravité et des collisions avec des objets spatiaux.
 */
public class Trajectoire {
    /** Longueur totale de la trajectoire */
    private double longueur;
    /** Durée estimée pour parcourir la trajectoire */
    private double dureeEstimee;
    /** Coût énergétique pour suivre la trajectoire */
    private double coutEnergetique;
    /** Indique si la trajectoire est valide */
    private boolean estValide;
    /** Liste des points formant la trajectoire */
    private List<Point2D> points;
    /** Point de départ de la trajectoire */
    private Point2D pointDepart;
    /** Point d'arrivée de la trajectoire */
    private Point2D pointArrivee;
    /** Nœud utilisé pour le calcul */
    private Noeud noeud;



    public boolean isEstValide() {
        return estValide; }
    public double getCoutEnergetique() {
        return coutEnergetique; }
    public double getDureeEstimee() {
        return dureeEstimee; }
    public double getLongueur() {
        return longueur; }
    public void setCoutEnergetique(double coutEnergetique) {
        this.coutEnergetique = coutEnergetique; }
    public void setDureeEstimee(double dureeEstimee) {
        this.dureeEstimee = dureeEstimee; }
    public void setEstValide(boolean estValide) {
        this.estValide = estValide; }
    public void setLongueur(double longueur) {
        this.longueur = longueur; }
    public List<Point2D> getPoints() {
        return points; }
    public void setPoints(List<Point2D> points) {
        this.points = points; }
    public Point2D getPointDepart() {
        return pointDepart; }
    public void setPointDepart(Point2D pointDepart) {
        this.pointDepart = pointDepart; }
    public Point2D getPointArrivee() {
        return pointArrivee; }
    public void setPointArrivee(Point2D pointArrivee) {
        this.pointArrivee = pointArrivee; }
    public double getPAS_DEPLACEMENT() {
        return noeud.PAS_DEPLACEMENT;
    }
    public double getTOLERANCE_ARRIVEE() {
        return noeud.TOLERANCE_ARRIVEE;
    }

    /**
     * Détermine la trajectoire optimale pour un satellite donné.
     * en utilise un algorithme A* avec pondérations sur la distance, l'énergie et le risque gravitationnel.
     * Ajuste également la taille des pas en fonction de la distance entre départ et arrivée.
     *
     * @param satellites le satellite pour lequel la trajectoire est calculée
     */
    public void determinerTrajectoire(Satellites satellites) {
        if (pointDepart == null || pointArrivee == null) {
            estValide = false;
            setPoints(new ArrayList<>());
        }

        if (distanceEuclidienne(pointDepart, pointArrivee)>8000){
            noeud.PAS_DEPLACEMENT=320;
            noeud.TOLERANCE_ARRIVEE=350;
        }
        else if (distanceEuclidienne(pointDepart, pointArrivee) > 5000) {
            noeud.PAS_DEPLACEMENT = 220;
            noeud.TOLERANCE_ARRIVEE = 240;
        }

        else if (distanceEuclidienne(pointDepart, pointArrivee) > 2000) {
            noeud.PAS_DEPLACEMENT = 100;
            noeud.TOLERANCE_ARRIVEE = 110;
        }
        else if (distanceEuclidienne(pointDepart, pointArrivee) > 1000) {
            noeud.PAS_DEPLACEMENT = 50;
            noeud.TOLERANCE_ARRIVEE = 60;
        }
        else if (distanceEuclidienne(pointDepart, pointArrivee)>500){
            noeud.PAS_DEPLACEMENT = 28;
            noeud.TOLERANCE_ARRIVEE = 36;
        }
        else if (distanceEuclidienne(pointDepart,pointArrivee)>200){
            noeud.PAS_DEPLACEMENT=18;
            noeud.TOLERANCE_ARRIVEE=20;
        }
        else{
            noeud.PAS_DEPLACEMENT=10;
            noeud.TOLERANCE_ARRIVEE=12;
        }

        PriorityQueue<Noeud> listeOuverte = new PriorityQueue<>();
        Set<Noeud> listeFermee = new HashSet<>();
        Map<Point2D, Double> meilleursScoresG = new HashMap<>();

        Noeud noeudDepart = new Noeud(pointDepart, null);
        noeudDepart.g = 0;
        noeudDepart.h = calculerH(pointDepart);
        noeudDepart.f = noeudDepart.h;

        listeOuverte.add(noeudDepart);
        meilleursScoresG.put(pointDepart, 0.0);

        Noeud noeudFinal = null;
        int iterations = 0;
        int maxIterations = (int) Math.max(10000, distanceEuclidienne(pointDepart, pointArrivee) * 10);

        while (!listeOuverte.isEmpty() && iterations < maxIterations) {
            iterations++;
            Noeud noeudCourant = listeOuverte.poll();
            if (distanceEuclidienne(noeudCourant.position, pointArrivee) < noeud.TOLERANCE_ARRIVEE) {
                noeudFinal = noeudCourant;
                break;
            }

            listeFermee.add(noeudCourant);

            for (Noeud voisin : obtenirVoisins(noeudCourant, satellites)) {
                if (listeFermee.contains(voisin)) {
                    continue;
                }

                double G1 = noeudCourant.g + noeud.alpha * distanceEuclidienne(noeudCourant.position, voisin.position) + noeud.beta * calculerCoutEnergetique(satellites,noeudCourant.position, voisin.position) + noeud.gamma * calculerPenalite(satellites);
                Double G0 = meilleursScoresG.get(voisin.position);

                if (G0 == null || G1 < G0) {
                    voisin.parent = noeudCourant;
                    voisin.g = G1;
                    voisin.h = calculerH(voisin.position);
                    voisin.f = voisin.g + voisin.h;
                    meilleursScoresG.put(voisin.position, G1);
                    if (!listeOuverte.contains(voisin)) {
                        listeOuverte.add(voisin);
                    }
                }
            }
        }
        if (noeudFinal != null) {
            List<Point2D> chemin = reconstruireChemin(noeudFinal);
            this.points = chemin;
            calculerStatistiques(chemin, satellites);
            this.estValide = true;
            setPoints(chemin);
        } else {
            this.estValide = false;
            setPoints(new ArrayList<>());
        }
    }

    /**
     * Calcul de l'heuristique (distance euclidienne jusqu'à l'arrivée)
     * @param position prends la position de l'objet
     * @return distance euclidienne entre la postion de l'objet et le point d'arrivé
     */
    private double calculerH(Point2D position) {
        return distanceEuclidienne(position, pointArrivee);
    }

    /**
     * Calcule la distance euclidienne entre deux points.
     * @param p1 position 1
     * @param p2 position 2
     * @return distance cuclienne entre point 1 et point 2
     */
    private double distanceEuclidienne(Point2D p1, Point2D p2) {
        double dx = p2.getX() - p1.getX();
        double dy = p2.getY() - p1.getY();
        return Math.sqrt(dx * dx + dy * dy);
    }

    /**
     * Génère la liste des nœuds voisins pour un nœud donné.
     * @param noeud prend le noeud sur lequel est le satellite
     * @param satellites
     * @return liste de noueds faisant un maillage
     */
    private List<Noeud> obtenirVoisins(Noeud noeud, Satellites satellites) {
        int nombreDirections;
        double angle, nouveauX, nouveauY, angleVersCible;


        List<Noeud> voisins = new ArrayList<>();
        nombreDirections=16;

        for (int i = 0; i < nombreDirections; i++) {
            angle = 2 * Math.PI * i / nombreDirections;
            nouveauX = noeud.position.getX() + Math.cos(angle) * noeud.PAS_DEPLACEMENT;
            nouveauY = noeud.position.getY() + Math.sin(angle) * noeud.PAS_DEPLACEMENT;
            Point2D nouvellePosition = new Point2D.Double(nouveauX, nouveauY);

            if (!estEnCollision(nouvellePosition, satellites)) {
                voisins.add(new Noeud(nouvellePosition, noeud));
            }
        }

        angleVersCible = Math.atan2(pointArrivee.getY() - noeud.position.getY(), pointArrivee.getX() - noeud.position.getX());
        nouveauX = noeud.position.getX() + Math.cos(angleVersCible) * noeud.PAS_DEPLACEMENT;
        nouveauY = noeud.position.getY() + Math.sin(angleVersCible) * noeud.PAS_DEPLACEMENT;
        Point2D positionVersCible = new Point2D.Double(nouveauX, nouveauY);

        if (!estEnCollision(positionVersCible, satellites)) {
            voisins.add(new Noeud(positionVersCible, noeud));
        }

        return voisins;
    }
    /**
     * Vérifie si la position est en collision avec un objet spatial.
     *
     * @param position La position à vérifier
     * @param satellites Le satellite pour accéder aux objets détectés
     * @return true si collision, false sinon
     */
    private boolean estEnCollision(Point2D position, Satellites satellites) {
        if (CalculPenaliteGravite.estZoneInterdite(satellites.getMasse() + satellites.getCarburant(), satellites.getAstresLus(),satellites)) {
            return true;
        }
        for (ObjetSpatial objet : satellites.getAstresLus()) {
            if (distanceEuclidienne(position, objet.getPosition())<  objet.getRayon()+satellites.getRayon()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Calcule la pénalité gravitationnelle à une position donnée.
     *
     * @param satellites Le satellite pour accéder aux objets détectés et masse
     * @return La valeur de pénalité
     */
    private double calculerPenalite(Satellites satellites) {
        return CalculPenaliteGravite.calculerPenalite(satellites.getMasse() + satellites.getCarburant(), satellites.getAstresLus(),satellites);
    }



    /**
    * Calcule le coût énergétique pour se déplacer entre deux points.
    *
    * @param satellites Le satellite pour accéder aux fonctions de norme et deltaV
     *@param p1 Point de départ
     * @param p2 Point d'arrivée
    * @return Le coût énergétique pour ce déplacement
     */
    private double calculerCoutEnergetique(Satellites satellites,Point2D p1, Point2D p2) {
        return satellites.norme(new double[] {satellites.norme(new double[]{p1.getX(),p2.getX()}), satellites.norme(new double[]{p1.getY(),p2.getY()})})*satellites.carburantNecessaire(satellites.deltaVDisponible());
    }

    /**
     * Reconstruit le chemin complet depuis le nœud final jusqu'au départ.
     *
     * @param noeudFinal Le nœud final
     * @return Liste des positions formant le chemin
     */
    private List<Point2D> reconstruireChemin(Noeud noeudFinal) {
        LinkedList<Point2D> chemin = new LinkedList<>();
        Noeud noeudCourant = noeudFinal;
        while (noeudCourant != null) {
            chemin.addFirst(noeudCourant.position);
            noeudCourant = noeudCourant.parent;
        }
        return chemin;
    }

    /**
     * Calcule les statistiques de la trajectoire : longueur, coût énergétique et durée estimée.
     *
     * @param chemin Liste des points formant la trajectoire
     * @param satellites Satellite pour le calcul du coût énergétique
     */
    private void calculerStatistiques(List<Point2D> chemin, Satellites satellites) {
        double vitesseMoyenne;
        this.longueur = 0.0;
        this.coutEnergetique = 0.0;

        for (int i = 0; i < chemin.size() - 1; i++) {
            Point2D p1 = chemin.get(i);
            Point2D p2 = chemin.get(i + 1);
            this.longueur += distanceEuclidienne(p1, p2);
            this.coutEnergetique += calculerCoutEnergetique(satellites,p1, p2);
        }
        vitesseMoyenne = 100.0;
        this.dureeEstimee = this.longueur / vitesseMoyenne;
    }


}