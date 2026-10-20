package modele;

import java.awt.geom.Point2D;
import java.io.Serializable;

/**
 * Classe représentant un objet spatial
 * Implémente l'interface Serializable pour permettre la sauvegarde
 * Classe parente pour tous les objets du système
 */
public class ObjetSpatial implements Serializable {
    private static final long serialVersionUID = 1L;
    
    /** Position de l'objet dans l'espace en coordonnées (O,x,y) */
    private Point2D position;
        
    /** Masse de l'objet en kilogrammes */
    private double masse;
    
    /** Rayon de l'objet en mètres */
    private double rayon;
    
    /** Identifiant unique de l'objet */
    private int identifier;
    
    /** Vitesse horizontale de l'objet (selon l'axe X) */
    private double velociteX;
    
    /** Vitesse verticale de l'objet (selon l'axe Y) */
    private double velociteY;

    /**
     * Constructeur de la classe ObjetSpatial
     * 
     * @param position la position initiale de l'objet
     * @param masse la masse initiale de l'objet en kilogrammes
     * @param rayon le rayon initiale de l'objet en mètres
     * @param identifier l'identifiant unique de l'objet
     * @param velociteX la vitesse initiale horizontale de l'objet
     * @param velociteY la vitesse initiale verticale de l'objet
     */
    public ObjetSpatial(Point2D position, double masse, double rayon, int identifier, double velociteX, double velociteY) {
        this.position = position;
        this.masse = masse;
        this.rayon = rayon;
        this.identifier = identifier;
        this.velociteX = velociteX;
        this.velociteY = velociteY;
    }

    /**
     * Constructeur sans vélocité (vitesse initialisée à 0)
     */
    public ObjetSpatial(Point2D position, double masse, double rayon, int identifier) {
        this(position, masse, rayon, identifier, 0, 0);
    }

    /**
     * Met à jour la position de l'objet en fonction de sa vitesse et du temps écoulé
     * La formule est issue d'une intégration utilisant la méthode d'euler explicite des équations du mouvement : p(i+1) = p(i) + vi * dt 
     * 
     * @param dt le temps écoulé depuis la dernière mise à jour
     */
    public void updatePosition(double dt) {
        Point2D position = this.getPosition();

        double nouveauX = position.getX() + this.getVelociteX() * dt;
        double nouveauY = position.getY() + this.getVelociteY() * dt;

        this.setPosition(new Point2D.Double(nouveauX, nouveauY));
    }

    /**
     * Retourne la position de l'objet
     * 
     * @return la position en coordonnées 2D
     */
    public Point2D getPosition() {
        return position;
    }

    /**
     * Définit la position de l'objet
     * 
     * @param position la nouvelle position
     */
    public void setPosition(Point2D position) {
        this.position = position;
    }

    /**
     * Retourne la masse de l'objet
     * 
     * @return la masse en kilogrammes
     */
    public double getMasse() {
        return masse;
    }

    /**
     * Définit la masse de l'objet
     * 
     * @param masse la nouvelle masse en kilogrammes
     */
    public void setMasse(double masse) {
        this.masse = masse;
    }

    /**
     * Retourne le rayon de l'objet
     * 
     * @return le rayon en mètres
     */
    public double getRayon() {
        return rayon;
    }

    /**
     * Définit le rayon de l'objet
     * 
     * @param rayon le nouveau rayon en mètres
     */
    public void setRayon(double rayon) {
        this.rayon = rayon;
    }

    /**
     * Retourne l'identifiant unique de l'objet
     * 
     * @return l'identifiant
     */
    public int getIdentifiant() {
        return identifier;
    }

    /**
     * Définit l'identifiant unique de l'objet
     * 
     * @param identifier le nouvel identifiant
     */
    public void setIdentifier(int identifier) {
        this.identifier = identifier;
    }

    /**
     * Met à jour la position de l'objet
     * 
     * @param position la nouvelle position
     */
    public void mettreAjourPosition(Point2D position) {
        this.position = position;
    }

    /**
     * Retourne la vitesse horizontale de l'objet
     * 
     * @return la vitesse sur l'axe X
     */
    public double getVelociteX() {
        return velociteX;
    }

    /**
     * Retourne la vitesse verticale de l'objet
     * 
     * @return la vitesse sur l'axe Y
     */
    public double getVelociteY() {
        return velociteY;
    }

    /**
     * Définit la vitesse horizontale de l'objet
     * 
     * @param vx la nouvelle vitesse sur l'axe X
     */
    public void setVelociteX(double vx) {
        this.velociteX = vx;
    }

    /**
     * Définit la vitesse verticale de l'objet
     * 
     * @param vy la nouvelle vitesse sur l'axe Y
     */
    public void setVelociteY(double vy) {
        this.velociteY = vy;
    }

    /**
     * Définit les vitesses horizontale et verticale de l'objet
     * 
     * @param vx la nouvelle vitesse sur l'axe X
     * @param vy la nouvelle vitesse sur l'axe Y
     */
    public void setVelocite(double vx, double vy) {
        this.velociteX = vx;
        this.velociteY = vy;
    }

    @Override
    public String toString() {
        return "ObjetSpatial : " +"position=" + position +", masse=" + masse +", rayon=" + rayon +", velociteX=" + velociteX +", velociteY=" + velociteY +", identifier=" + identifier;
    }

}