package modele;

import java.awt.geom.Point2D;
import java.io.Serializable;

/**
 * Classe représentant un astéroïde
 * Hérite de la classe ObjetSpatial et implémente l'interface Serializable
 */
public class Asteroides extends ObjetSpatial implements Serializable {
    private static final long serialVersionUID = 1L;
    
    /** Nom de l'astéroïde */
    private String nom;
    
    /** Taille de l'astéroïde, représente le diamètre de ce dernier */
    private double taille;

    /**  
     * Constructeur de la classe Asteroides
     * Utilise le constructeur de la classe mère ObjetSpatial
     * Transforme le nom en une série de chiffres grâce au hachage (hashCode)
     * 
     * @param nom le nom de l'astéroïde
     * @param position la position initiale de l'astéroïde
     * @param masse la masse de l'astéroïde
     * @param rayon le rayon de l'astéroïde
     * @param velociteX la vitesse horizontale de l'astéroïde
     * @param velociteY la vitesse verticale de l'astéroïde
     * @param taille le diamètre de l'astéroïde
     */
    public Asteroides(String nom, Point2D position, double masse, double rayon, double velociteX, double velociteY, double taille) {
        super(position, masse, rayon, nom.hashCode(), velociteX, velociteY);
        this.nom = nom;
        this.taille = taille;
    }

    /**
     * Retourne le nom de l'astéroïde
     * 
     * @return le nom de l'astéroïde
     */
    public String getNom() {
        return nom;
    }
    
    /**
     * Définit le nom de l'astéroïde
     * 
     * @param nom le nouveau nom de l'astéroïde
     */
    public void setNom(String nom) {
        this.nom = nom;
    }
    
    /**
     * Retourne la taille (diamètre) de l'astéroïde
     * 
     * @return la taille de l'astéroïde
     */
    public double getTaille() {
        return taille;
    }
    
    /**
     * Définit la taille de l'astéroïde et met à jour son rayon
     * Le rayon est calculé comme la moitié de la taille
     * 
     * @param taille la nouvelle taille de l'astéroïde
     */
    public void setTaille(double taille) {
        this.taille = taille;
        this.setRayon(taille / 2);
    }

    @Override
    public String toString() {
        return "Asteroides : " + "nom='" + nom + '\'' +", position=" + getPosition() +", taille=" + taille +", vitesseX=" + getVelociteX() +", vitesseY=" + getVelociteY();
    }
}