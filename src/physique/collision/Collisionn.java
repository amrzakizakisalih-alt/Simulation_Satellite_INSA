package physique.collision;

import java.awt.geom.Point2D;
import modele.*;

import java.util.*;

/**
 * Classe représentant une collision entre deux objets spatiaux
 * Fournit des méthodes de classe pour les calculs physiques et la détection de collisions et la création de débris
 *
 */
public class Collisionn {
    /** Premier objet impliqué dans la collision */
    public final ObjetSpatial obj1;

    /** Second objet impliqué dans la collision */
    public final ObjetSpatial obj2;

    /** Seuil de vitesse relative pour considérer une collision mou ( choix arbitraire ) */
    public static final double VITESSE_CHOC_MOU = 5.0;

    /** Seuil de vitesse relative pour considérer une collision catastrophique ( choix arbitraire ) */
    public static final double VITESSE_CHOC_CATASTROPHIQUE = 40.0;

    /** Ratio de masse au-dessus duquel un objet est considéré comme négligeable par rapport à l'autre ( choix arbitraire ) */
    public static final double RATIO_MASSE_NEGLIGEABLE = 50.0;

    /**
     * Constructeur de la classe Collisionn
     *
     * @param o1 premier objet spatial
     * @param o2 second objet spatial
     */
    public Collisionn(ObjetSpatial o1, ObjetSpatial o2) {
        this.obj1 = o1;
        this.obj2 = o2;
    }

    /**
     * Calcule la vitesse relative entre deux objets spatiaux
     *
     * @param obj1 premier objet spatial
     * @param obj2 second objet spatial
     * @return la norme de la vitesse relative
     */
    public static double calculerVitesseRelative(ObjetSpatial obj1, ObjetSpatial obj2) {
        double vxRel = obj1.getVelociteX() - obj2.getVelociteX();
        double vyRel = obj1.getVelociteY() - obj2.getVelociteY();
        return Math.sqrt(vxRel * vxRel + vyRel * vyRel);
    }

    /**
     * Calcule le centre de masse inertie ou centre de masse de deux objets spatiaux
     *
     * @param obj1 premier objet spatial
     * @param obj2 second objet spatial
     * @return la position du centre de masse en coordonnées 2D
     */
    public static Point2D calculerCentreInertie(ObjetSpatial obj1, ObjetSpatial obj2) {
        double m1 = obj1.getMasse();
        double m2 = obj2.getMasse();
        double masseTotale = m1 + m2;

        Point2D pos1 = obj1.getPosition();
        Point2D pos2 = obj2.getPosition();

        double x = (pos1.getX() * m1 + pos2.getX() * m2) / masseTotale;
        double y = (pos1.getY() * m1 + pos2.getY() * m2) / masseTotale;

        return new Point2D.Double(x, y);
    }

    /**
     * Calcule la normale de collision entre deux objets spatiaux
     * La normale est un vecteur dirigé de obj1 vers obj2
     * Utilisée pour déterminer la direction de l'impact lors d'une collision
     *
     * @param obj1 premier objet spatial
     * @param obj2 second objet spatial
     * @return un vecteur représentant la normale de collision
     */
    public static Point2D calculerNormaleCollision(ObjetSpatial obj1, ObjetSpatial obj2) {
        double dx = obj2.getPosition().getX() - obj1.getPosition().getX();
        double dy = obj2.getPosition().getY() - obj1.getPosition().getY();
        double distance = Math.sqrt(dx*dx + dy*dy);

        if (distance < 0.001) distance = 0.001;

        return new Point2D.Double(dx/distance, dy/distance);
    }

    /**
     * Calcule le ratio de masse entre deux objets spatiaux
     *
     * @param obj1 premier objet spatial
     * @param obj2 second objet spatial
     * @return le ratio plus lourd / moins lourd
     */
    public static double calculerRatioMasse(ObjetSpatial obj1, ObjetSpatial obj2) {
        return Math.max(obj1.getMasse(), obj2.getMasse()) / Math.min(obj1.getMasse(), obj2.getMasse());
    }

    /**
     * Répartit une masse totale en plusieurs fragments de manière aléatoire
     * Chaque fragment a une masse minimale de 0.5 kg
     *
     * @param masseTotal la masse totale à répartir en kilogrammes
     * @param nombre le nombre de fragments à créer
     * @return un tableau contenant les masses de chaque fragment
     */
    public static double[] repartirMasse(double masseTotal, int nombre) {
        double[] masses = new double[nombre];
        double somme = 0;

        for (int i = 0; i < nombre; i++) {
            masses[i] = Math.random();
            somme += masses[i];
        }

        for (int i = 0; i < nombre; i++) {
            masses[i] = (masses[i] / somme) * masseTotal;
            masses[i] = Math.max(0.5, masses[i]);
        }

        return masses;
    }

    /**
     * Détecte toutes les collisions entre objets spatiaux dans une liste
     * Chaque objet ne peut être impliqué que dans une seule collision
     *
     * @param objets liste des objets spatiaux à vérifier
     * @return liste des collisions détectées
     */
    public static List<Collisionn> detecterCollisions(List<ObjetSpatial> objets) {
        List<Collisionn> collisions = new ArrayList<>();
        Set<ObjetSpatial> objetsDejaEnCollision = new HashSet<>();

        for (int i = 0; i < objets.size(); i++) {
            ObjetSpatial obj1 = objets.get(i);
            if (objetsDejaEnCollision.contains(obj1) || !estCollisionnable(obj1)){
                continue;
            }

            for (int j = i + 1; j < objets.size(); j++) {
                ObjetSpatial obj2 = objets.get(j);
                if (objetsDejaEnCollision.contains(obj2) || !estCollisionnable(obj2)){
                    continue;
                }

                if (estEnCollision(obj1, obj2)) {
                    collisions.add(new Collisionn(obj1, obj2));
                    objetsDejaEnCollision.add(obj1);
                    objetsDejaEnCollision.add(obj2);
                    break;
                }
            }
        }

        return collisions;
    }

    /**
     * Vérifie si un objet peut entrer en collision
     * Un objet est collisionnable s'il a un rayon >= 2 et une masse >= 1 ( choix arbitraire )
     *
     * @param obj l'objet spatial à vérifier
     * @return true si l'objet est collisionnable, false sinon
     */
    private static boolean estCollisionnable(ObjetSpatial obj) {
        return obj.getRayon() >= 2 && obj.getMasse() >= 1;
    }

    /**
     * Vérifie si deux objets sont en collision
     * Utilise la distance euclidienne
     * Deux objets sont en collision si leurs sphères de collision se chevauchent
     *
     * @param a premier objet spatial
     * @param b second objet spatial
     * @return true si les objets sont en collision, false sinon
     */
    private static boolean estEnCollision(ObjetSpatial a, ObjetSpatial b) {
        Point2D posA = a.getPosition();
        Point2D posB = b.getPosition();

        double dx = posA.getX() - posB.getX();
        double dy = posA.getY() - posB.getY();
        double distance = Math.sqrt(dx * dx + dy * dy);
        double distanceMin = a.getRayon() + b.getRayon();

        return distance < distanceMin;
    }

    /**
     * Crée une liste de débris avec des paramètres physiques spécifiés
     * Calcule les vitesses d'éjection et les angles de dispersion pour chaque débris
     *
     * @param position la position de création des débris
     * @param masses tableau des masses de chaque débris
     * @param tailleMin taille minimale d'un débris
     * @param tailleMax taille maximale d'un débris
     * @param vitesseMin vitesse minimale d'éjection
     * @param vitesseMax vitesse maximale d'éjection
     * @param angleCentral angle central de dispersion
     * @param variationAngleMax variation maximale de l'angle
     * @param Nom préfixe pour nommer les débris
     * @param vitesseHeriteeX vitesse héritée sur l'axe X
     * @param vitesseHeriteeY vitesse héritée sur l'axe Y
     * @return liste des débris créés
     */
    private static List<Asteroides> creerDebris(Point2D position, double[] masses, double tailleMin, double tailleMax,double vitesseMin, double vitesseMax, double angleCentral,double variationAngleMax, String  Nom, double vitesseHeriteeX, double vitesseHeriteeY) {
        List<Asteroides> debrisList = new ArrayList<>();

        for (int i = 0; i < masses.length; i++) {
            double masseDebris = masses[i];

            double tailleDebris = tailleMin + Math.random() * (tailleMax - tailleMin);
            double rayonDebris = tailleDebris / 2;

            double vitesseEjection = vitesseMin + Math.random() * (vitesseMax - vitesseMin);

            double angleVariation = (Math.random() - 0.5) * variationAngleMax;
            double angleEjection = angleCentral + angleVariation ;

            double vx = Math.cos(angleEjection) * vitesseEjection + vitesseHeriteeX;
            double vy = Math.sin(angleEjection) * vitesseEjection + vitesseHeriteeY;

            String nom = Nom + "_" + i;

            Asteroides debris = new Asteroides(nom, position, masseDebris, rayonDebris, vx, vy, tailleDebris);
            debrisList.add(debris);
        }

        return debrisList;
    }


    /**
     * Calcule l'angle d'impact entre deux objets spatiaux
     * angle du vecteur d'impact
     *
     * @param obj1 le premier objet spatial
     * @param obj2 le second objet spatial
     * @return l'angle d'impact en radians
     */
    private static double calculerAngleImpact(ObjetSpatial obj1, ObjetSpatial obj2) {
        double dx = obj2.getPosition().getX() - obj1.getPosition().getX();
        double dy = obj2.getPosition().getY() - obj1.getPosition().getY();
        return Math.atan2(dy, dx);
    }

    /**
     * Applique la conservation de la quantité de mouvement aux débris
     * Ajuste les vitesses des débris pour conserver la quantité de mouvement totale
     *
     * @param debrisList liste des débris à corriger
     * @param QmxTotal quantité de mouvement totale sur l'axe X
     * @param QmyTotal quantité de mouvement totale sur l'axe Y
     */
    private static void appliquerConservationMecanique(List<Asteroides> debrisList, double QmxTotal, double QmyTotal) {
        double QmxDebris = 0;
        double QmyDebris = 0;
        double masseTotaleDebris = 0;

        for (Asteroides debris : debrisList) {
            QmxDebris += debris.getMasse() * debris.getVelociteX();
            QmyDebris += debris.getMasse() * debris.getVelociteY();
            masseTotaleDebris += debris.getMasse();
        }

        double correctionVx = (QmxTotal - QmxDebris) / masseTotaleDebris;  // on calcule le delta vitesse selon X
        double correctionVy = (QmyTotal - QmyDebris) / masseTotaleDebris;   // on calcule le delta vitesse selon Y

        for (Asteroides debris : debrisList) {
            debris.setVelocite(
                    debris.getVelociteX() + correctionVx,
                    debris.getVelociteY() + correctionVy
            );
        }
    }

    /**
     * Crée des débris suite à une collision inélastique
     * Les débris ont des vitesses modérées pour permettre une simulation fluide et conservent la quantité de mouvement
     *
     * @param position position de la collision
     * @param obj1 premier objet en collision
     * @param obj2 second objet en collision
     * @param massesDebris tableau des masses des débris à créer
     * @param vitesseRelative vitesse relative entre les deux objets
     * @return liste des débris créés
     */
    public static List<Asteroides> creerDebrisInelastique(Point2D position, ObjetSpatial obj1, ObjetSpatial obj2, double[] massesDebris, double vitesseRelative) {
        double qxTotal = obj1.getMasse() * obj1.getVelociteX() + obj2.getMasse() * obj2.getVelociteX();
        double qyTotal = obj1.getMasse() * obj1.getVelociteY() + obj2.getMasse() * obj2.getVelociteY();
        double masseTotale = obj1.getMasse() + obj2.getMasse();
        double vxCM = qxTotal / masseTotale;
        double vyCM = qyTotal / masseTotale;

        double angleImpact = calculerAngleImpact(obj1, obj2);

        List<Asteroides> debrisList = creerDebris(position, massesDebris, 1.0, 3.0, vitesseRelative * 0.2, vitesseRelative * 0.4, angleImpact, Math.PI, "DebrisInelastique", vxCM, vyCM);

        appliquerConservationMecanique(debrisList, qxTotal, qyTotal);

        return debrisList;
    }

    /**
     * Crée des débris suite à une fragmentation catastrophique
     * Les débris ont des vitesses élevées et dispersés dans toutes les directions
     *
     * @param position position de la collision
     * @param obj1 premier objet en collision
     * @param obj2 second objet en collision
     * @param massesDebris tableau des masses des débris à créer
     * @param vitesseRelative vitesse relative entre les deux objets
     * @return liste des débris créés
     */
    public static List<Asteroides> creerDebrisFragmentation(Point2D position, ObjetSpatial obj1, ObjetSpatial obj2, double[] massesDebris, double vitesseRelative) {
        double qxTotal = obj1.getMasse() * obj1.getVelociteX() + obj2.getMasse() * obj2.getVelociteX();
        double qyTotal = obj1.getMasse() * obj1.getVelociteY() + obj2.getMasse() * obj2.getVelociteY();
        double masseTotale = obj1.getMasse() + obj2.getMasse();
        double vxCM = qxTotal / masseTotale;
        double vyCM = qyTotal / masseTotale;

        double angleImpact = calculerAngleImpact(obj1, obj2);

        List<Asteroides> debrisList = creerDebris(position, massesDebris, 0.5, 2.5, vitesseRelative * 0.7, vitesseRelative, angleImpact, 2 * Math.PI, "DebrisCatastrophe", vxCM, vyCM);

        appliquerConservationMecanique(debrisList, qxTotal, qyTotal);

        return debrisList;
    }

    /**
     * Crée des débris suite à la destruction d'un satellite
     * Génère 3 à 5 débris avec une faible masse ( choix arbitraire )
     *
     * @param satellite le satellite détruit
     * @return liste des débris créés
     */
    public static List<Asteroides> creerDebrisSatellite(Satellites satellite) {
        int nombreDebris = 3 + (int)(Math.random() * 3);
        double[] masses =  repartirMasse(satellite.getMasse(), nombreDebris);

        return creerDebris(satellite.getPosition(), masses, 1.0, 3.0, 10.0, 20.0, 0.0, Math.PI, "DebrisSat_" + satellite.getIdentifiant(), satellite.getVelociteX(), satellite.getVelociteY());
    }

    /**
     * Crée des débris suite à la destruction d'un objet spatial générique
     * Génère 2 à 4 débris ( choix arbitraire )
     *
     * @param objet l'objet spatial détruit
     * @return liste des débris créés
     */
    public static List<Asteroides> creerDebrisObjetSpatial(ObjetSpatial objet) {
        int nombreDebris = 2 + (int)(Math.random() * 3);
        double[] masses =  repartirMasse(objet.getMasse(), nombreDebris);
        return creerDebris(objet.getPosition(), masses, 1.0, 4.0, 5.0, 15.0, 0.0, 2 * Math.PI, "Debris_" + objet.getIdentifiant(), objet.getVelociteX(), objet.getVelociteY());
    }

    /**
     * Crée des débris suite à une collision légère
     * Génère 2 à 4 débris avec des vitesses modérées ( choix arbitraire )
     *
     * @param position position de la collision
     * @param obj l'objet impliqué dans la collision
     * @return liste des débris créés
     */
    public static List<Asteroides> creerDebrisCollisionLegere(Point2D position, ObjetSpatial obj) {
        int nombreDebris = 2 + (int)(Math.random() * 3);
        double[] masses =  repartirMasse(obj.getMasse(), nombreDebris);

        return creerDebris(position, masses, 1.0, 2.0, 5.0, 10.0, 0.0, 2 * Math.PI, "DebrisLeger_" + System.currentTimeMillis(), obj.getVelociteX(), obj.getVelociteY());
    }


}