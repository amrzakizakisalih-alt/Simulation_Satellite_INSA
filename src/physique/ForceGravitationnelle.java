package physique;

import modele.ObjetSpatial;
import java.awt.geom.Point2D;

/**
 * Classe utilitaire pour les calculs de force gravitationnelle
 * Fournit des methodes statiques pour calculer les forces, vitesses orbitales et periodes
 * Utilise une constante gravitationnelle adaptee à l'echelle de la simulation
 */
public class ForceGravitationnelle {
    /** Constante gravitationnelle adaptee à l'echelle de simulation (unites normalisees) */
    private static final double G = 0.01;

    /**
     * Calcule la vitesse orbitale circulaire necessaire pour une orbite stable
     * Utilise la formule v = √(G×M/r) et retourne un vecteur tangent à l'orbite
     *
     * @param centre l'objet central autour duquel orbiter
     * @param positionOrbite la position sur l'orbite
     * @param rayonOrbital le rayon de l'orbite souhaitee
     * @return un tableau [vx, vy] representant la vitesse orbitale
     */
    public static double[] calculerVitesseOrbitale(ObjetSpatial centre, Point2D positionOrbite, double rayonOrbital) {
        Point2D posCentre = centre.getPosition();

        // Calculer la distance reelle
        double dx = positionOrbite.getX() - posCentre.getX();
        double dy = positionOrbite.getY() - posCentre.getY();
        double distance = Math.sqrt(dx * dx + dy * dy);

        // Utiliser le rayon orbital fourni ou la distance calculee
        double r = (rayonOrbital > 0) ? rayonOrbital : distance;

        // Vitesse orbitale circulaire : v = √(G×M/r)
        double vitesseOrbitale = Math.sqrt(G * centre.getMasse() / r);

        // Direction perpendiculaire au rayon (tangente au cercle)
        // Vecteur unitaire radial
        double ux = dx / distance;
        double uy = dy / distance;

        // Rotation de 90° pour obtenir la tangente
        double vx = -vitesseOrbitale * uy;
        double vy = vitesseOrbitale * ux;

        return new double[]{vx, vy};
    }

    /**
     * Calcule la periode orbitale pour une orbite circulaire
     * Utilise la troisieme loi de Kepler : T = 2π √(r³ / (G × M))
     *
     * @param centre l'objet central autour duquel orbiter
     * @param rayonOrbital le rayon de l'orbite
     * @return la periode orbitale en secondes
     */
    public static double calculerPeriodeOrbitale(ObjetSpatial centre, double rayonOrbital) {
        return 2 * Math.PI * Math.sqrt(Math.pow(rayonOrbital, 3) / (G * centre.getMasse()));
    }

    /**
     * Calcule la force gravitationnelle entre deux objets spatiaux
     * Utilise la loi de Newton : F = G × m1 × m2 / r²
     * evite la division par zero avec une distance minimale
     *
     * @param a premier objet spatial
     * @param b second objet spatial
     * @return un tableau [fx, fy] representant la force appliquee sur a par b
     */
    public static double[] calculerForce(ObjetSpatial a, ObjetSpatial b) {
        Point2D posA = a.getPosition();
        Point2D posB = b.getPosition();

        double dx = posB.getX() - posA.getX();
        double dy = posB.getY() - posA.getY();

        // eviter la division par zero
        if ((dx * dx + dy * dy) < 1.0) {
            return new double[]{0, 0};
        }

        double distance = Math.sqrt(dx * dx + dy * dy);

        // Calculer la magnitude de la force : F = G * m1 * m2 / r²
        double forceMagnitude = G * a.getMasse() * b.getMasse() / (distance * distance);

        // Calculer les composantes de la force
        double forceX = forceMagnitude * (dx / distance);
        double forceY = forceMagnitude * (dy / distance);

        return new double[]{forceX, forceY};
    }

    /**
     * Calcule la force gravitationnelle totale appliquee sur un objet
     * Somme les forces gravitationnelles de tous les autres objets
     *
     * @param objet l'objet sur lequel calculer la force totale
     * @param tousLesObjets liste de tous les objets de la simulation
     * @return un tableau [fx, fy] representant la force totale
     */
    public static double[] calculerForceTotale(ObjetSpatial objet, java.util.List<ObjetSpatial> tousLesObjets) {
        double forceXTotale = 0;
        double forceYTotale = 0;

        for (ObjetSpatial autre : tousLesObjets) {
            if (objet == autre) continue;

            double[] force = calculerForce(objet, autre);
            forceXTotale += force[0];
            forceYTotale += force[1];
        }

        return new double[]{forceXTotale, forceYTotale};
    }

    /**
     * Applique une force gravitationnelle et met à jour la vitesse d'un objet
     * Utilise la deuxieme loi de Newton : F = m × a
     * Integre l'acceleration sur l'intervalle de temps dt
     *
     * @param objet l'objet sur lequel appliquer la force
     * @param force la force à appliquer [fx, fy]
     * @param dt l'intervalle de temps pour l'integration
     */
    public static void appliquerForce(ObjetSpatial objet, double[] force, double dt) {
        double masse = objet.getMasse();

        double ax = force[0] / masse;
        double ay = force[1] / masse;

        double nouveauVx = objet.getVelociteX() + ax * dt;
        double nouveauVy = objet.getVelociteY() + ay * dt;

        objet.setVelocite(nouveauVx, nouveauVy);
    }
}