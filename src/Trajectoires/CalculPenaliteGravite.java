package Trajectoires;

import physique.ForceGravitationnelle;
import modele.*;
import java.util.List;

/**
 * Classe utilitaire pour calculer des pénalités liées aux effets gravitationnels
 * subis par un satellite dans un environnement spatial donné.
 *
 *>Les pénalités sont basées sur la magnitude de l'accélération gravitationnelle
 * exercée par les objets spatiaux à proximité. Des seuils définissent des zones
 * d'attention, de danger moyen et de danger critique.
 */
public class CalculPenaliteGravite {

    /** Seuil à partir duquel il faut commencer à faire attention à la gravité */
    private static final double SEUIL_ATTENTION = 0.01;
    /** Seuil d'accélération correspondant à un danger moyen */
    private static final double SEUIL_DANGER_MOYEN = 0.1;
    /** Seuil d'accélération correspondant à un danger critique */
    private static final double SEUIL_DANGER_CRITIQUE = 0.5;
    /** Pénalité associée à une accélération négligeable */
    private static final double PENALITE_NULLE = 0.0;
    /** Pénalité faible */
    private static final double PENALITE_FAIBLE = 500.0;
    /** Pénalité moyenne */
    private static final double PENALITE_MOYENNE = 2000.0;
    /** Pénalité forte */
    private static final double PENALITE_FORTE = 5000.0;
    /** Pénalité critique */
    private static final double PENALITE_CRITIQUE = 20000.0;

    /**
     * Calcule l'accélération gravitationnelle totale subie par un satellite à une
     * position donnée en tenant compte de tous les objets spatiaux autour.
     *
     * @param masseSatellite masse du satellite en kg
     * @param objetsSpatiaux liste des objets spatiaux exerçant une force gravitationnelle
     * @param satellite satellite concerné par le calcul
     * @return norme de l'accélération totale en m/s²
     */
    public static double calculerAccelerationTotale(double masseSatellite, List<ObjetSpatial> objetsSpatiaux,Satellites satellite) {
        double forceXTotale = 0.0;
        double forceYTotale = 0.0;

        for (ObjetSpatial objet : objetsSpatiaux) {
            double[] force = ForceGravitationnelle.calculerForce(satellite, objet);
            forceXTotale += force[0];
            forceYTotale += force[1];
        }

        double ax = forceXTotale / masseSatellite;
        double ay = forceYTotale / masseSatellite;

        return Math.sqrt(ax * ax + ay * ay);
    }

    /**
     * Calcule une pénalité en fonction de l'accélération gravitationnelle subie
     * par le satellite. Plus l'accélération est élevée, plus la pénalité est importante.
     *
     * @param masseSatellite masse du satellite en kg
     * @param objetsSpatiaux liste des objets spatiaux exerçant une force gravitationnelle
     * @param satellite satellite concerné par le calcul
     * @return valeur de la pénalité
     */
    public static double calculerPenalite(double masseSatellite, List<ObjetSpatial> objetsSpatiaux, Satellites satellite) {
        double acceleration = calculerAccelerationTotale(masseSatellite, objetsSpatiaux,satellite);


        if (acceleration < SEUIL_ATTENTION) {
            return PENALITE_NULLE;
        } else if (acceleration < SEUIL_DANGER_MOYEN) {
            double ratio = (acceleration - SEUIL_ATTENTION) / (SEUIL_DANGER_MOYEN - SEUIL_ATTENTION);
            return PENALITE_FAIBLE + ratio * (PENALITE_MOYENNE - PENALITE_FAIBLE);
        } else if (acceleration < SEUIL_DANGER_CRITIQUE) {
            double ratio = (acceleration - SEUIL_DANGER_MOYEN) / (SEUIL_DANGER_CRITIQUE - SEUIL_DANGER_MOYEN);
            return PENALITE_MOYENNE + ratio * (PENALITE_FORTE - PENALITE_MOYENNE);
        } else {
            double exces = acceleration - SEUIL_DANGER_CRITIQUE;
            return PENALITE_CRITIQUE * (1.0 + exces / SEUIL_DANGER_CRITIQUE);
        }
    }
    /**
     * Vérifie si la position donnée se situe dans une zone considérée comme interdite
     * en raison d'une accélération gravitationnelle trop élevée.
     *
     * @param masseSatellite masse du satellite en kg
     * @param objetsSpatiaux liste des objets spatiaux exerçant une force gravitationnelle
     * @param satellite satellite concerné par le calcul
     * @return {@code true} si l'accélération dépasse le seuil critique, {@code false} sinon
     */
    public static boolean estZoneInterdite(double masseSatellite, List<ObjetSpatial> objetsSpatiaux,Satellites satellite) {
        double acceleration = calculerAccelerationTotale(masseSatellite, objetsSpatiaux,satellite);
        return acceleration >= SEUIL_DANGER_CRITIQUE;
    }
}