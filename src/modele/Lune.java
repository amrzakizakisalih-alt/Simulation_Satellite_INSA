package modele;

import java.awt.geom.Point2D;
import java.io.Serializable;

/**
 * Classe représentant une lune 
 * Hérite de la classe ObjetSpatial et implémente l'interface Serializable 
 * Une lune orbite autour d'une planète parente 
 */
public class Lune extends ObjetSpatial implements Serializable {
    private static final long serialVersionUID = 1L;

    /** Nom de la lune */
    private String nom;
    
    /** Gravité de la lune */
    private double gravite;
    
    /** Planète autour de laquelle la lune orbite */
    private Planete planeteParente;
    
    /** Période de révolution*/
    private double periode;

    /** Distance entre la lune et sa planète parent*/
    private double rayonOrbital;

    /**
     * Constructeur de la classe Lune 
     * Utilise le constructeur de la classe mère ObjetSpatial 
     * Calcule automatiquement le rayon orbital à partir de la position de la lune et de sa planète parent 
     * 
     * @param nom le nom de la lune
     * @param gravite la gravité de la lune
     * @param planeteParente la planète autour de laquelle la lune orbite
     * @param periode la période de révolution
     * @param position la position initiale de la lune
     * @param masse la masse de la lune
     * @param rayon le rayon de la lune
     * @param vx la vitesse horizontale de la lune (selon l'axe X)
     * @param vy la vitesse verticale de la lune (selon l'axe Y)
     */
    public Lune(String nom, double gravite, Planete planeteParente, double periode, Point2D position, double masse, double rayon, double vx, double vy) {
    
        super(position, masse, rayon, nom.hashCode(), vx, vy);
        this.nom = nom;
        this.gravite = gravite;
        this.planeteParente = planeteParente;
        this.periode = periode;

        // Calculer le rayon orbital en utilisant la distance euclidienne
        Point2D posParent = planeteParente.getPosition();
        double dx = position.getX() - posParent.getX();
        double dy = position.getY() - posParent.getY();
        this.rayonOrbital = Math.sqrt(dx * dx + dy * dy);
    }

    /**
     * Retourne le nom de la lune 
     * 
     * @return le nom de la lune
     */
    public String getNom() {
        return nom;
    }

    /**
     * Définit le nom de la lune 
     * 
     * @param nom le nouveau nom de la lune
     */
    public void setNom(String nom) {
        this.nom = nom;
    }

    /**
     * Retourne la gravité de la lune 
     * 
     * @return la gravité
     */
    public double getGravite() {
        return gravite;
    }

    /**
     * Définit la gravité de la lune 
     * 
     * @param gravite la nouvelle gravité
     */
    public void setGravite(double gravite) {
        this.gravite = gravite;
    }

    /**
     * Retourne la planète parent autour de laquelle orbite la lune 
     * 
     * @return la planète parent
     */
    public Planete getPlaneteParente() {
        return planeteParente;
    }

    /**
     * Définit la planète parent autour de laquelle orbite la lune 
     * 
     * @param planeteParente la nouvelle planète parent
     */
    public void setPlaneteParente(Planete planeteParente) {
        this.planeteParente = planeteParente;
    }

    /**
     * Retourne la période de révolution de la lune 
     * 
     * @return la période
     */
    public double getPeriode() {
        return periode;
    }

    /**
     * Définit la période de révolution de la lune 
     * 
     * @param periode la nouvelle période
     */
    public void setPeriode(double periode) {
        this.periode = periode;
    }

    /**
     * Retourne le rayon orbital de la lune (distance avec sa planète parente) 
     * 
     * @return le rayon orbital
     */
    public double getRayonOrbital() {
        return rayonOrbital;
    }

    /**
     * Définit le rayon orbital de la lune 
     * 
     * @return le nouveau rayon orbital
     */
    public double setRayonOrbital() {
        return rayonOrbital;
    }

    @Override
    public String toString() {
        return "Lune{" + "nom='" + getNom() + '\'' +", planeteParente=" + planeteParente.getNom()+", rayonOrbital=" + rayonOrbital +", position=" + getPosition() +", vitesse=(" + getVelociteX() + "," + getVelociteY() + ")";
    }
}