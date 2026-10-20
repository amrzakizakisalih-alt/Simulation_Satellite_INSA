package physique.collision;

import modele.*;
import java.util.*;

/**
 * Gestionnaire principal des collisions dans la simulation
 */
public class GestionnaireCollisions {
    /** Liste des objets spatiaux */
    private List<ObjetSpatial> objets;

    /** Resolveur pour les collisions physiques (non-satellites) */
    private ResolveurCollisionsPhysiques resolveurPhysique;

    /** Gestionnaire collisions satellites */
    private GestionnaireSatellites gestionnaireSatellites;

    /**
     * Constructeur
     *
     */
    public GestionnaireCollisions(List<ObjetSpatial> objets) {
        this.objets = objets;
        this.resolveurPhysique = new ResolveurCollisionsPhysiques();
        this.gestionnaireSatellites = new GestionnaireSatellites();
    }

    /**
     * Gere toutes les collisions detectees dans la simulation
     * Detecte les collisions, les resout et met à jour la liste des objets
     *
     * @return true si au moins une collision a ete detectee et geree, false sinon
     */
    public boolean gererCollisions() {
        List<Collisionn> collisions = Collisionn.detecterCollisions(objets);

        if (collisions.isEmpty()) {
            return false;
        }

        List<ObjetSpatial> nouveauxObjets = new ArrayList<>();
        Set<ObjetSpatial> objetsASupprimer = new HashSet<>();
        Set<Satellites> satellitesADeconnecter = new HashSet<>();

        for (Collisionn collision : collisions) {
            resoudreCollisionIndividuelle(collision, nouveauxObjets, objetsASupprimer, satellitesADeconnecter);
        }

        nettoyerObjets(objetsASupprimer, nouveauxObjets);

        if (!satellitesADeconnecter.isEmpty()) {
            gestionnaireSatellites.deconnecterSatellites(satellitesADeconnecter);
        }

        return true;
    }

    /**
     * Resout une collision individuelle entre deux objets
     * Determine si un satellite est implique et utilise le gestionnaire approprie
     * Accumule les resultats dans les listes fournies
     *
     * @param collision la collision à resoudre
     * @param nouveauxObjets liste ou ajouter les nouveaux objets crees
     * @param objetsASupprimer ensemble ou ajouter les objets à supprimer
     * @param satellitesADeconnecter ensemble ou ajouter les satellites à deconnecter
     */
    public void resoudreCollisionIndividuelle(Collisionn collision, List<ObjetSpatial> nouveauxObjets, Set<ObjetSpatial> objetsASupprimer, Set<Satellites> satellitesADeconnecter) {
        ObjetSpatial obj1 = collision.obj1;
        ObjetSpatial obj2 = collision.obj2;

        boolean collisionAvecSatellite = obj1 instanceof Satellites || obj2 instanceof Satellites;

        if (collisionAvecSatellite) {
            if (gestionnaireSatellites.gererCollisionAvecSatellite(obj1, obj2)) {
                nouveauxObjets.addAll(gestionnaireSatellites.getNouveauxObjets());
                objetsASupprimer.addAll(gestionnaireSatellites.getObjetsASupprimer());
                satellitesADeconnecter.addAll(gestionnaireSatellites.getSatellitesADeconnecter());
            }
        } else {
            resolveurPhysique.resoudreCollision(obj1, obj2);
            nouveauxObjets.addAll(resolveurPhysique.getNouveauxObjets());
            objetsASupprimer.addAll(resolveurPhysique.getObjetsASupprimer());
        }
    }

    /**
     * Nettoie la liste des objets apres les collisions
     * Supprime les objets detruits et ajoute les nouveaux objets crees
     * Gere la destruction propre des satellites (deconnexion)
     *
     * @param objetsASupprimer ensemble des objets à supprimer de la simulation
     * @param nouveauxObjets liste des nouveaux objets à ajouter à la simulation
     */
    private void nettoyerObjets(Set<ObjetSpatial> objetsASupprimer, List<ObjetSpatial> nouveauxObjets) {
        Iterator<ObjetSpatial> iterator = objets.iterator();
        while (iterator.hasNext()) {
            ObjetSpatial obj = iterator.next();
            if (objetsASupprimer.contains(obj)) {
                if (obj instanceof Satellites) {
                    Satellites satellite = (Satellites) obj;
                    satellite.detruire();
                }
                iterator.remove();
            }
        }

        objets.addAll(nouveauxObjets);
    }

    /**
     * Retourne la liste des objets spatiaux geres
     *
     * @return la liste des objets spatiaux
     */
    public List<ObjetSpatial> getObjets() {
        return objets;
    }

    /**
     * Definit la liste des objets spatiaux à gerer
     *
     * @param objets la nouvelle liste des objets spatiaux
     */
    public void setObjets(List<ObjetSpatial> objets) {
        this.objets = objets;
    }
}