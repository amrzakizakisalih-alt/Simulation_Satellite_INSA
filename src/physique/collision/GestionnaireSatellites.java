package physique.collision;

import modele.*;
import java.awt.geom.Point2D;
import java.util.*;

/**
 * Gestionnaire des collisions impliquant des satellites
 * Gere les differents types de collisions selon la masse relative des objets
 * Prend en charge la deconnexion des satellites detruits
 */
public class GestionnaireSatellites {

    /** Ratio de masse en dessous duquel les objets sont consideres comme comparables ( choix arbitraire ) */
    private static final double RATIO_MASSE_SATELLITE_COMPARABLE = 5.0;

    /** Liste avec tout les nouveaux objets issues d'une collision */
    private List<ObjetSpatial> nouveauxObjets = new ArrayList<>();

    /** Ensemble compose de tout les objets à supprimer */
    private Set<ObjetSpatial> objetsASupprimer = new HashSet<>();

    /** Ensemble avec tout les satellites qui faut deconnecter du reseau */
    private Set<Satellites> satellitesADeconnecter = new HashSet<>();


    /**
     * Gere une collision impliquant au moins un satellite
     * Determine le type de collision en fonction du ratio de masse et applique la collision
     *
     * @param obj1 premier objet en collision
     * @param obj2 second objet en collision
     */
    public boolean gererCollisionAvecSatellite(ObjetSpatial obj1, ObjetSpatial obj2) {
        // Reinitialiser les resultats
        nouveauxObjets.clear();
        objetsASupprimer.clear();
        satellitesADeconnecter.clear();

        Satellites satellite = null;
        ObjetSpatial autreObjet = null;

        if (obj1 instanceof Satellites) {
            satellite = (Satellites) obj1;
            autreObjet = obj2;
        } else if (obj2 instanceof Satellites) {
            satellite = (Satellites) obj2;
            autreObjet = obj1;
        }

        if (satellite == null) return false;

        Point2D posCollision = Collisionn.calculerCentreInertie(obj1, obj2);
        double ratioMasse = autreObjet.getMasse() / satellite.getMasse();

        if (ratioMasse <= RATIO_MASSE_SATELLITE_COMPARABLE) {
            gererCollisionObjetComparable(satellite, autreObjet, ratioMasse);
        } else {
            gererCollisionObjetMassif(satellite, autreObjet, ratioMasse);
        }

        return true;
    }

    /**
     * Gere la collision avec un objet de taille comparable (0.1 < ratio < 5) ( choix arbitraire )
     * Le satellite et l'objet sont tous deux detruits et fragmentes en debris
     *
     * @param satellite le satellite implique
     * @param autreObjet l'objet en collision avec le satellite
     * @param ratioMasse le ratio de masse entre les objets
     */
    private void gererCollisionObjetComparable(Satellites satellite, ObjetSpatial autreObjet,double ratioMasse) {
        nouveauxObjets.addAll(Collisionn.creerDebrisSatellite(satellite));
        nouveauxObjets.addAll(Collisionn.creerDebrisObjetSpatial(autreObjet));

        objetsASupprimer.add(satellite);
        objetsASupprimer.add(autreObjet);
        satellitesADeconnecter.add(satellite);
    }

    /**
     * Gere la collision avec un objet massif (ratio > 5) ( choix arbitraire )
     * Le satellite est detruit, l'objet massif survit
     *
     * @param satellite le satellite implique
     * @param autreObjet l'objet massif en collision avec le satellite
     * @param ratioMasse le ratio de masse entre les objets
     */
    private void gererCollisionObjetMassif(Satellites satellite, ObjetSpatial autreObjet,double ratioMasse) {
        nouveauxObjets.addAll(Collisionn.creerDebrisSatellite(satellite));

        objetsASupprimer.add(satellite);
        satellitesADeconnecter.add(satellite);
    }

    /**
     * Deconnecte les satellites du reseau suite à leur destruction
     *
     * @param satellitesADeconnecter ensemble des satellites à deconnecter
     */
    public void deconnecterSatellites(Set<Satellites> satellitesADeconnecter) {
        for (Satellites satellite : satellitesADeconnecter) {
            try {
                if (satellite.getAntenne() != null) {
                    System.out.println("Deconnexion du satellite " + satellite.getIdentifiant() + " du serveur");
                    satellite.getAntenne().deconnecter();
                }
            } catch (Exception e) {
                System.err.println("Erreur lors de la deconnexion du satellite " +
                        satellite.getIdentifiant() + ": " + e.getMessage());
            }
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

    /**
     * Retourne l'ensemble des satellites a deconnecter
     *
     * @return la gravité
     */
    public Set<Satellites> getSatellitesADeconnecter() {
        return satellitesADeconnecter;
    }

}