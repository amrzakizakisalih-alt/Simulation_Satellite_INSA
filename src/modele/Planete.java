package modele;
import java.awt.*;
import java.awt.geom.Point2D;
import java.io.Serializable;

/**
 * Classe représentant une planète
 * Hérite de la classe ObjetSpatial et implémente l'interface Serializable
 * Une planète possède un nom et une gravité
 */
public class Planete extends ObjetSpatial implements Serializable {
    private static final long serialVersionUID = 1L;
    
    /** Nom de la planète */
    private String nom;
    
    /** Gravité de la planète */
    private double gravite;

    /**
     * Constructeur de la classe Planete
     * La vitesse initiale est définie à 0 par défaut
     * Transforme le nom en identifiant unique grâce au hachage (hashCode)
     * 
     * @param nom le nom de la planète
     * @param gravite la gravité à la surface de la planète (en m/s²)
     * @param position la position initiale de la planète
     * @param masse la masse de la planète en kilogrammes
     * @param rayon le rayon de la planète en mètres
     */
    public Planete(String nom, double gravite, Point2D position, double masse, double rayon) {
        super(position, masse, rayon, nom.hashCode(), 0, 0);  
        this.nom = nom;
        this.gravite = gravite;
    }
    
    /**
     * Retourne le nom de la planète
     * 
     * @return le nom de la planète
     */
    public String getNom() {
        return nom;
    }

    /**
     * Définit le nom de la planète
     * 
     * @param nom le nouveau nom de la planète
     */
    public void setNom(String nom) {
        this.nom = nom;
    }

    /**
     * Retourne la gravité à la surface de la planète
     * 
     * @return la gravité en m/s²
     */
    public double getGravite() {
        return gravite;
    }

    /**
     * Définit la gravité à la surface de la planète
     * 
     * @param gravite la nouvelle gravité en m/s²
     */
    public void setGravite(double gravite) {
        this.gravite = gravite;
    }

    @Override
    public String toString() {
        return "Planete : " +"nom='" + nom + '\'' +", gravite=" + gravite +", position=" + getPosition() +", masse=" + getMasse() +", rayon=" + getRayon() ;
    }
}