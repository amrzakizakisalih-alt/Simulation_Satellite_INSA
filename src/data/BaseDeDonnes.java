package data;

import modele.*;
import java.awt.Color;
import java.io.Serializable;
import java.util.Map;
import java.util.List;

/**
 * Classe de transfert de données (DTO) pour la sauvegarde et le chargement de simulations spatiales.
 * <p>
 * Cette classe encapsule l'ensemble de l'état d'une simulation spatiale, incluant :
 * <ul>
 *   <li>Tous les objets spatiaux présents dans la simulation</li>
 *   <li>Les couleurs personnalisées attribuées aux objets</li>
 *   <li>Les maps d'accès rapide par nom pour planètes, lunes et satellites</li>
 *   <li>Les compteurs pour générer des identifiants uniques</li>
 * </ul>
 * </p>
 * <p>
 * Cette classe implémente {@link Serializable} pour permettre la sauvegarde
 * et le chargement depuis un fichier via la sérialisation Java.
 * </p>
 */
public class BaseDeDonnes  implements Serializable {
    /** Identifiant de version pour la sérialisation */
    private static final long serialVersionUID = 1L;
    /** Liste de tous les objets spatiaux présents dans la simulation */
    private List<ObjetSpatial> objets;
    /** Map associant les objets spatiaux à leurs couleurs personnalisées */
    private Map<ObjetSpatial, Color> customColors;
    /** Map d'accès rapide aux planètes par leur nom */
    private Map<String, Planete> planeteMap;
    /** Map d'accès rapide aux lunes par leur nom */
    private Map<String, Lune> luneMap;
    /** Map d'accès rapide aux satellites par leur nom/identifiant */
    private Map<String, Satellites> satelliteMap;
    /** Compteur pour générer des identifiants uniques pour les planètes */
    private int planeteCompteur;
    /** Compteur pour générer des identifiants uniques pour les lunes */
    private int luneCompteur;
    /** Compteur pour générer des identifiants uniques pour les astéroïdes */
    private int asteroideCompteur;
    /** Compteur pour générer des identifiants uniques pour les satellites */
    private int satelliteCompteur;
    /**
     * Constructeur complet de la base de données.
     * <p>
     * Crée une instance contenant l'ensemble de l'état d'une simulation spatiale.
     * Toutes les collections passées en paramètre sont stockées par référence
     * (pas de copie défensive), donc les modifications ultérieures affecteront
     * cette instance.
     * </p>
     *
     * @param objets la liste de tous les objets spatiaux
     * @param customColors la map des couleurs personnalisées
     * @param planeteMap la map des planètes indexées par nom
     * @param luneMap la map des lunes indexées par nom
     * @param satelliteMap la map des satellites indexés par identifiant
     * @param planeteCounter le compteur actuel pour les planètes
     * @param luneCounter le compteur actuel pour les lunes
     * @param asteroideCounter le compteur actuel pour les astéroïdes
     * @param satelliteCounter le compteur actuel pour les satellites
     */
    public BaseDeDonnes(List<ObjetSpatial> objets,
                        Map<ObjetSpatial, Color> customColors,
                        Map<String, Planete> planeteMap,
                        Map<String, Lune> luneMap,
                        Map<String, Satellites> satelliteMap,
                        int planeteCounter,
                        int luneCounter,
                        int asteroideCounter,
                        int satelliteCounter) {
        this.objets = objets;
        this.customColors = customColors;
        this.planeteMap = planeteMap;
        this.luneMap = luneMap;
        this.satelliteMap = satelliteMap;
        this.planeteCompteur = planeteCounter;
        this.luneCompteur = luneCounter;
        this.asteroideCompteur = asteroideCounter;
        this.satelliteCompteur = satelliteCounter;
    }

    public List<ObjetSpatial> getObjets() { return objets; }
    public Map<ObjetSpatial, Color> getCustomColors() { return customColors; }
    public Map<String, Planete> getPlaneteMap() { return planeteMap; }
    public Map<String, Lune> getLuneMap() { return luneMap; }
    public Map<String, Satellites> getSatelliteMap() { return satelliteMap; }
    public int getPlaneteCompteur() { return planeteCompteur; }
    public int getLuneCounter() { return luneCompteur; }
    public int getAsteroideCompteur() { return asteroideCompteur; }
    public int getSatelliteCompteur() { return satelliteCompteur; }
}


