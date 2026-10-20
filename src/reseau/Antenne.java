package reseau;

import modele.*;


import java.awt.geom.Point2D;
import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
/**
 * Représente une antenne embarquée sur un satellite capable de
 * se connecter à un serveur via TCP, envoyer les objets spatiaux détectés, récupérer la liste des objets connus du serveur.
 *sérialiser / désérialiser
 */
public class Antenne {
    /** Identifiant du satellite auquel l'antenne est associée */
    private final String idSatellite;
    /** Adresse du serveur à contacter */
    private final String hostServeur;
    /** Port du serveur à contacter */
    private final int portServeur;
    /** Socket TCP utilisé pour la communication */
    private Socket socket;
    /** Flux de sortie pour envoyer des données au serveur */
    private PrintWriter fluxsortie; // Deepseek
    /** Flux d'entrée pour recevoir des données du serveur */
    private BufferedReader fluxentree; //Deepseek
    /** Indique si l'antenne est connectée */
    private final AtomicBoolean connecte = new AtomicBoolean(false); //Deepseek

    /**
     * Construit une antenne associée à un satellite et préparée pour
     * se connecter à un serveur.
     *
     * @param idSatellite identifiant du satellite
     * @param hostServeur adresse du serveur
     * @param portServeur port du serveur
     */
    public Antenne(String idSatellite, String hostServeur, int portServeur) {
        this.idSatellite = idSatellite;
        this.hostServeur = hostServeur;
        this.portServeur = portServeur;
    }

    /**
     * Établit la connexion TCP avec le serveur
     */
    public void connecter() throws IOException {
        if (connecte.get()) {
            return;
        }
        this.socket = new Socket(hostServeur, portServeur);
        this.fluxsortie = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true); //Deepseek
        this.fluxentree = new BufferedReader(new InputStreamReader(socket.getInputStream())); //Deepseek
        connecte.set(true);
        System.out.println("[" + idSatellite + "] Antenne connectée au serveur");
    }

    /**
     * Envoie une liste d'objets spatiaux détectés au serveur.
     *
     * @param objets liste des objets spatiaux à envoyer
     * @return true si l'envoi a réussi
     */
    public boolean envoyerObjetsDetectes(List<ObjetSpatial> objets) {
        if (!connecte.get()) {
            System.err.println("[" + idSatellite + "] Antenne non connectée");
            return false;
        }

        if (objets == null || objets.isEmpty()) {
            return true;
        }

        try {
            String donnees = serialiserObjets(objets);
            String commande = "DATA " + idSatellite + " " + donnees;
            fluxsortie.println(commande);
            String reponse = fluxentree.readLine();//Deepseek

            if (reponse != null && reponse.startsWith("OK:")) {
                System.out.println("SUCCESS[" + idSatellite + "] Objets envoyés: " + reponse);
                return true;
            } else {
                System.err.println("[" + idSatellite + "] Erreur envoi: " + reponse);
                return false;
            }

        } catch (IOException e) {
            System.err.println("[" + idSatellite + "] Erreur d'envoi: " + e.getMessage());
            return false;
        }
    }

    /**
     * Demande tous les objets spatiaux détectés au serveur
     * @return Liste des objets connus
     */
    public List<ObjetSpatial> demanderObjets() {
        if (!connecte.get()) {
            System.err.println("[" + idSatellite + "] Antenne non connectée");
            return new ArrayList<>();
        }

        try {
            fluxsortie.println("GET_OBSTACLES");
            String reponse = fluxentree.readLine(); //Deepseek

            if (reponse == null) {
                System.err.println("[" + idSatellite + "] Pas de réponse");
                return new ArrayList<>();
            }

            if (reponse.equals("OK:AUCUN_OBSTACLE")) {
                return new ArrayList<>();
            }

            if (reponse.startsWith("OK:OBSTACLES:")) {
                String donnees = reponse.substring("OK:OBSTACLES:".length());
                List<ObjetSpatial> objets = deserialiserObjets(donnees);
                System.out.println("[" + idSatellite + "] Reçu " + objets.size() + " objets");
                return objets;
            }

            if (reponse.startsWith("ERR:")) {
                System.err.println("[" + idSatellite + "] Erreur: " + reponse);
            }

            return new ArrayList<>();

        } catch (IOException e) {
            System.err.println("[" + idSatellite + "] Erreur communication: " + e.getMessage());
            return new ArrayList<>();
        }
    }
    /**
     * Sérialise une liste d'objets spatiaux dans une chaîne de caractères
     * au format : id,x,y,vx,vy,masse,rayon
     *
     * @param objets liste d'objets à sérialiser
     * @return chaîne représentant les objets
     */
    private String serialiserObjets(List<ObjetSpatial> objets) {
        StringBuilder sb = new StringBuilder(); //Deepseek
        for (int i = 0; i < objets.size(); i++) {
            ObjetSpatial obj = objets.get(i);
            sb.append(obj.getIdentifiant()).append(",").append(obj.getPosition().getX()).append(",").append(obj.getPosition().getY()).append(",").append(obj.getVelociteX()).append(",").append(obj.getVelociteY()).append(",").append(obj.getMasse()).append(",").append(obj.getRayon());
            if (i < objets.size() - 1) {
                sb.append(";");
            }
        }
        return sb.toString();
    }

    /**
     * Désérialise une chaîne de caractères représentant une liste d'objets spatiaux
     * dans le format attendu
     *
     * @param donnees chaîne à désérialiser
     * @return liste des objets spatiaux reconstruit à partir de la chaîne
     */
    private List<ObjetSpatial> deserialiserObjets(String donnees) {
        int id;
        double x,y,vx,vy,masse,rayon;
        List<ObjetSpatial> objets = new ArrayList<>();

        if (donnees == null || donnees.trim().isEmpty()) {
            return objets;
        }

        String[] objetsStr = donnees.split(";");

        for (String objetStr : objetsStr) {
            try {
                String[] parts = objetStr.split(",");

                if (parts.length < 6) {
                    System.err.println("[" + idSatellite + "] Format invalide: " + objetStr);
                    continue;
                }

                id = Integer.parseInt(parts[0]);
                x = Double.parseDouble(parts[1]);
                y = Double.parseDouble(parts[2]);
                vx = Double.parseDouble(parts[3]);
                vy = Double.parseDouble(parts[4]);
                masse = Double.parseDouble(parts[5]);
                rayon = Double.parseDouble(parts[6]);
                // Créer un objet spatial générique
                ObjetSpatial obj = new ObjetSpatial(new Point2D.Double(x,y), masse, rayon, id,vx,vy);
                objets.add(obj);

            } catch (NumberFormatException e) {
                System.err.println("[" + idSatellite + "] Erreur parsing nombre: " + objetStr);
            } catch (Exception e) {
                System.err.println("[" + idSatellite + "] Erreur parsing objet: " + objetStr);
            }
        }

        return objets;
    }

    /**
     * Déconnecte l'antenne du serveur et ferme la connexion TCP.
     * Si l'antenne n'était pas connectée, la méthode ne fait rien.
     */
    public void deconnecter() {
        if (!connecte.compareAndSet(true, false)) {
            return;
        }
        try {
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        } catch (IOException ignored) {
        }
        System.out.println("[" + idSatellite + "] Antenne déconnectée");
    }
    /**
     * Indique si l'antenne est actuellement connectée au serveur.
     *
     * @return true si connectée, {@code false} sinon
     */
    public boolean estConnectee() {
        return connecte.get();
    }
}
