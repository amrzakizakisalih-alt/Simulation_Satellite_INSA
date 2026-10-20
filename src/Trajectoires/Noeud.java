package Trajectoires;

import java.awt.geom.Point2D;
import java.util.Objects;

/**
 * Représente un nœud utilisé dans les calculs de trajectoire et d'algorithme de type A*.
 *
 * <p>Chaque nœud contient sa position, son parent, et les coûts associés :</p>
 * <ul>
 *     <li>g : coût réel depuis le départ</li>
 *     <li>h : coût estimé jusqu'à l'arrivée (heuristique)</li>
 *     <li>f : coût total f = g + h</li>
 * </ul>
 *
 * <p>Des poids statiques (alpha, beta, gamma) peuvent influencer le calcul des coûts pour la distance,
 * l'énergie et le risque. La taille des pas et la tolérance d'arrivée sont également configurables.</p>
 */
public class Noeud implements Comparable<Noeud>{
    /** Position du nœud dans l'espace */
    Point2D position;
    /** Nœud parent pour retracer le chemin */
    Noeud parent;
    /** Coût réel depuis le nœud de départ */
    double g;
    /** Heuristique : coût estimé jusqu'au nœud d'arrivée */
    double h;
    /** Coût total f = g + h */
    double f;
    /** Poids pour la distance dans le calcul de coût */
    public static double alpha = 2;
    /** Poids pour l'énergie dans le calcul de coût */
    public static double beta = 4;
    /** Poids pour le risque dans le calcul de coût */
    public static double gamma = 5;
    /** Taille d'un pas de déplacement */
    public static double PAS_DEPLACEMENT = 10;
    /** Tolérance pour considérer qu'on est arrivé au point */
    public static double TOLERANCE_ARRIVEE = 12;

    /**
     * Construit un nœud avec une position et un parent.
     *
     * @param position position du nœud
     * @param parent nœud parent (peut être null si c'est le point de départ)
     */
    Noeud(Point2D position, Noeud parent) {
        this.position = position;
        this.parent = parent;
        this.g = 0;
        this.h = 0;
        this.f = 0;
    }

     //CLaude AI
    /**
     * Compare ce nœud à un autre en fonction de la valeur f (coût total).
     *
     * @param autre nœud à comparer
     * @return un entier négatif, zéro, ou positif selon que ce nœud est moins, égal ou plus coûteux
     */
    @Override
    public int compareTo(Noeud autre) {
        return Double.compare(this.f, autre.f);
    }

    /**
     * Vérifie l'égalité de deux nœuds en comparant leurs positions avec la tolérance définie.
     *
     * @param obj objet à comparer
     * @return true si les positions sont proches dans la tolérance, false sinon
     */
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Noeud)) return false;
        Noeud autre = (Noeud) obj;
        return Math.abs(position.getX() - autre.position.getX()) < TOLERANCE_ARRIVEE && Math.abs(position.getY() - autre.position.getY()) < TOLERANCE_ARRIVEE;
    }
    /**
     * Génère le code de hachage du nœud basé sur sa position et la taille des pas.
     *
     * @return code de hachage
     */
    @Override
    public int hashCode() {
        return Objects.hash(Math.round(position.getX() / PAS_DEPLACEMENT), Math.round(position.getY() / PAS_DEPLACEMENT)
        );
    }
    //Fin Claude AI
}


