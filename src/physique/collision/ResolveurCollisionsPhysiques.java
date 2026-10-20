package physique.collision;

import modele.*;
import java.awt.geom.Point2D;
import java.util.*;

/**
 * Resolveur de collisions physiques
 * Applique les lois de conservation de la quantite de mouvement et de l'energie
 * Gere differents types de collisions selon la vitesse relative et le ratio de masse
 */
public class ResolveurCollisionsPhysiques {
    /** Liste des nouveaux objets crees suite à la collision */
    private List<ObjetSpatial> nouveauxObjets = new ArrayList<>();

    /** Ensemble des objets à supprimer */
    private Set<ObjetSpatial> objetsASupprimer = new HashSet<>();

    /**
     * Resout une collision entre deux objets spatiaux
     * Determine le type de collision et applique les consequences physiques appropriees
     *
     * @param obj1 premier objet en collision
     * @param obj2 second objet en collision
     */
    public void resoudreCollision(ObjetSpatial obj1, ObjetSpatial obj2) {
        // Reinitialiser les resultats
        nouveauxObjets.clear();
        objetsASupprimer.clear();

        double vitesseRelative = Collisionn.calculerVitesseRelative(obj1, obj2);
        double ratioMasse = Collisionn.calculerRatioMasse(obj1, obj2);

        // Determination du type de collision
        if (ratioMasse > Collisionn.RATIO_MASSE_NEGLIGEABLE) {
            collisionMasseNegligeable(obj1, obj2);
        }
        else if (vitesseRelative >= Collisionn.VITESSE_CHOC_CATASTROPHIQUE) {
            collisionCatastrophique(obj1, obj2, vitesseRelative);
        }
        else if (vitesseRelative <= Collisionn.VITESSE_CHOC_MOU) {
            collisionChocMou(obj1, obj2);
        }
        else {
            collisionInelastique(obj1, obj2, vitesseRelative);
        }
    }

    /**
     * Gere une collision à choc mou (vitesse relative faible)
     * Les deux objets fusionnent en un seul objet plus grand
     * Conserve la quantite de mouvement et combine les volumes
     *
     * @param obj1 premier objet
     * @param obj2 second objet
     */
    private void collisionChocMou(ObjetSpatial obj1, ObjetSpatial obj2) {
        double m1 = obj1.getMasse();
        double m2 = obj2.getMasse();
        double masseTotale = m1 + m2;

        // Utiliser la direction moyenne pour un mouvement plus naturel
        double directionX = (obj1.getVelociteX() + obj2.getVelociteX()) / 2;
        double directionY = (obj1.getVelociteY() + obj2.getVelociteY()) / 2;
        double vitesseMoyenne = Math.sqrt(directionX * directionX + directionY * directionY);

        // Normaliser la direction
        if (vitesseMoyenne > 0) {
            directionX /= vitesseMoyenne;
            directionY /= vitesseMoyenne;
        }

        // Calcul de la vitesse finale selon la formule: v' = (m1/(m1+m2)) * v1
        double vitesseFinale = (m1 / masseTotale) * Math.sqrt(obj1.getVelociteX() * obj1.getVelociteX() + obj1.getVelociteY() * obj1.getVelociteY())+ (m2 / masseTotale) * Math.sqrt(obj2.getVelociteX() * obj2.getVelociteX() + obj2.getVelociteY() * obj2.getVelociteY());

        // Position du centre de masse
        Point2D posFusion = Collisionn.calculerCentreInertie(obj1, obj2);

        // Calcul du nouveau rayon
        double volume1 = (4.0/3.0) * Math.PI * Math.pow(obj1.getRayon(), 3);
        double volume2 = (4.0/3.0) * Math.PI * Math.pow(obj2.getRayon(), 3);
        double volumeTotal = volume1 + volume2;
        double nouveauRayon = Math.cbrt(3 * volumeTotal / (4 * Math.PI));

        // Creation de l'objet fusionne
        String nom = "Fusion_" + System.currentTimeMillis();
        Planete planeteFusionnee = new Planete(nom, 9.8, posFusion, masseTotale, nouveauRayon);
        planeteFusionnee.setVelocite(directionX * vitesseFinale, directionY * vitesseFinale);

        nouveauxObjets.add(planeteFusionnee);
        objetsASupprimer.add(obj1);
        objetsASupprimer.add(obj2);
    }

    /**
     * Gere une collision avec un ratio de masse tres eleve
     * Le petit objet est detruit et fragmente, le gros objet survit
     *
     * @param obj1 premier objet
     * @param obj2 second objet
     */
    private void collisionMasseNegligeable(ObjetSpatial obj1, ObjetSpatial obj2) {
        ObjetSpatial petitObjet;
        ObjetSpatial grosObjet;

        if (obj1.getMasse() < obj2.getMasse()) {
            petitObjet = obj1;
            grosObjet = obj2;
        } else {
            petitObjet = obj2;
            grosObjet = obj1;
        }

        // Creation des debris
        List<Asteroides> debrisList = Collisionn.creerDebrisObjetSpatial(petitObjet);
        nouveauxObjets.addAll(debrisList);

        objetsASupprimer.add(petitObjet);
    }

    /**
     * Gere une collision inelastique
     *
     * @param obj1 premier objet
     * @param obj2 second objet
     * @param vitesseRelative vitesse relative entre les objets
     */
    private void collisionInelastique(ObjetSpatial obj1, ObjetSpatial obj2, double vitesseRelative) {
        double m1 = obj1.getMasse();
        double m2 = obj2.getMasse();

        // Coefficient de restitution = 0 pour collision inelastique
        double e = 0.0;

        Point2D normal = Collisionn.calculerNormaleCollision(obj1, obj2);
        double nx = normal.getX();
        double ny = normal.getY();

        // Vitesses relatives projetees sur la normale
        double v1n = obj1.getVelociteX() * nx + obj1.getVelociteY() * ny;
        double v2n = obj2.getVelociteX() * nx + obj2.getVelociteY() * ny;

        // Vitesses tangentielles
        double v1t = -obj1.getVelociteX() * ny + obj1.getVelociteY() * nx; // vecteur tengentielle (nx,-ny)
        double v2t = -obj2.getVelociteX() * ny + obj2.getVelociteY() * nx;

        // Nouvelles vitesses normales (formules de collision inelastique avec C_R=0)
        double v1nPrime = ((m1 - e*m2)*v1n + m2*(1+e)*v2n) / (m1 + m2);
        double v2nPrime = ((m2 - e*m1)*v2n + m1*(1+e)*v1n) / (m1 + m2);

        // Reconstruction des vitesses 2D  vitesse = (composante_normale × vecteur_normale) + (composante_tangentielle × vecteur_tangentiel)
        double v1xPrime = v1nPrime * nx - v1t * ny;
        double v1yPrime = v1nPrime * ny + v1t * nx;
        double v2xPrime = v2nPrime * nx - v2t * ny;
        double v2yPrime = v2nPrime * ny + v2t * nx;

        // Application des nouvelles vitesses
        obj1.setVelocite(v1xPrime, v1yPrime);
        obj2.setVelocite(v2xPrime, v2yPrime);

        // Creation de debris avec perte de masse
        Point2D posCollision = Collisionn.calculerCentreInertie(obj1, obj2);
        int nombreDebris = 2 + (int)(Math.random() * 3);
        double masseDebrisTotale = (m1 + m2) * 0.1; // 10% de la masse totale en debris ( choix arbitraire )

        double[] massesDebris = Collisionn.repartirMasse(masseDebrisTotale, nombreDebris);
        List<Asteroides> debrisList = Collisionn.creerDebrisInelastique(posCollision, obj1, obj2, massesDebris, vitesseRelative);

        nouveauxObjets.addAll(debrisList);

        // Ajustement des masses des objets principaux (perte de masse)
        obj1.setMasse(m1 * 0.95);
        obj2.setMasse(m2 * 0.95);

        // SePARATION RENFORCeE pour eviter les collisions repetees
        separerObjetsRenforce(obj1, obj2);
    }

    /**
     * Gere une collision catastrophique (vitesse tres elevee)
     * Les deux objets sont completement fragmentes en nombreux debris
     *
     * @param obj1 premier objet
     * @param obj2 second objet
     * @param vitesseRelative vitesse relative entre les objets
     */
    private void collisionCatastrophique(ObjetSpatial obj1, ObjetSpatial obj2, double vitesseRelative) {
        double m1 = obj1.getMasse();
        double m2 = obj2.getMasse();
        double masseTotale = m1 + m2;

        Point2D posCollision = Collisionn.calculerCentreInertie(obj1, obj2);

        // Creation de nombreux debris (fragmentation complete)
        int nombreDebris = 8 + (int)(Math.random() * 8);
        double[] massesDebris = Collisionn.repartirMasse(masseTotale, nombreDebris);

        List<Asteroides> debrisList = Collisionn.creerDebrisFragmentation(
                posCollision, obj1, obj2, massesDebris, vitesseRelative);

        nouveauxObjets.addAll(debrisList);
        objetsASupprimer.add(obj1);
        objetsASupprimer.add(obj2);
    }

    /**
     * Separe deux objets
     * Ajoute une marge de securite à la distance minimale pour eviter des collisions repetees ou des scenario
     *
     * @param obj1 premier objet
     * @param obj2 second objet
     */
    private void separerObjetsRenforce(ObjetSpatial obj1, ObjetSpatial obj2) {
        double dx = obj2.getPosition().getX() - obj1.getPosition().getX();
        double dy = obj2.getPosition().getY() - obj1.getPosition().getY();
        double distance = Math.sqrt(dx*dx + dy*dy);

        double distanceMin = obj1.getRayon() + obj2.getRayon();
        double chevauchement  = distanceMin - distance;

        if (chevauchement  > 0) {
            // Marge de securite pour s'assurer qu'ils ne se touchent plus
            double margeSecurite = 5.55555;
            double separationTotale = chevauchement  + margeSecurite;

            double separationX = (dx / distance) * separationTotale * 0.5;
            double separationY = (dy / distance) * separationTotale * 0.5;

            Point2D pos1 = obj1.getPosition();
            Point2D pos2 = obj2.getPosition();

            // Separe les objets dans des directions opposees
            obj1.setPosition(new Point2D.Double(pos1.getX() - separationX, pos1.getY() - separationY));
            obj2.setPosition(new Point2D.Double(pos2.getX() + separationX, pos2.getY() + separationY));
        }
    }


    /**
     * Retourne la liste des nouveau objets a ajoute
     *
     * @return la gravité
     */
    public List<ObjetSpatial> getNouveauxObjets() {
        return nouveauxObjets;
    }

    /**
     * Retourne l'ensemble des objets a supprimer
     *
     * @return la gravité
     */
    public Set<ObjetSpatial> getObjetsASupprimer() {
        return objetsASupprimer;
    }
}