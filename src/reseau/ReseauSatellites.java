package reseau;

import modele.*;

import java.awt.geom.Point2D;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Serveur réseau centralisé pour la communication entre satellites.
 * <p>
 * Cette classe implémente un serveur TCP qui permet aux satellites de :
 * <ul>
 *   <li>Partager les objets spatiaux qu'ils détectent</li>
 *   <li>Récupérer les objets détectés par les autres satellites</li>
 *   <li>Maintenir une base de données centralisée des obstacles</li>
 * </ul>
 * </p>
 * <p>
 * Le serveur traite plusieurs clients simultanément en créant un thread par connexion.
 * Les données sont stockées dans une structure thread-safe (ConcurrentHashMap) pour
 * éviter les problèmes de concurrence.
 * </p>
 *
 * <h3>Protocole de communication</h3>
 * <ul>
 *   <li><b>DATA &lt;idSatellite&gt; &lt;données&gt;</b> : Envoyer des objets détectés</li>
 *   <li><b>GET_OBSTACLES</b> : Récupérer tous les objets connus</li>
 *   <li><b>CLEAR_OBSTACLES</b> : Réinitialiser la base de données (debug)</li>
 * </ul>
 *
 * <h3>Format de sérialisation</h3>
 * Les objets sont sérialisés au format CSV : {@code id,x,y,vx,vy,masse,rayon}
 * <br>Plusieurs objets sont séparés par des points-virgules.
 */
 public class ReseauSatellites {
    /** Port TCP sur lequel le serveur écoute les connexions */
    private final int port;
    /** Indicateur d'état du serveur (volatile pour la visibilité entre threads) */
    private volatile boolean marche = false;

    /**
     * Stockage centralisé thread-safe des objets détectés.
     * Clé : identifiant de l'objet sous forme de String
     * Valeur : l'objet spatial correspondant
     */
    private final Map<String, ObjetSpatial> objetsConnus = new ConcurrentHashMap<>();

    /**
     * Constructeur du serveur réseau.
     *
     * @param port le port TCP sur lequel le serveur écoutera
     */
    public ReseauSatellites(int port) {
        this.port = port;}

    /**
     * Démarre le serveur réseau dans un thread séparé.
     * <p>
     * Le serveur commence à écouter sur le port spécifié et accepte
     * les connexions entrantes. Chaque client est géré dans son propre thread.
     * </p>
     *
     * @throws IOException si le port est déjà utilisé ou en cas d'erreur réseau
     */
    public void demarrer() throws IOException {
        marche = true;
        Thread serveurThread = new Thread(this::boucleServeur, "Thread-ReseauSatellites");
        serveurThread.start();
    }
    /**
     * Boucle principale du serveur qui accepte les connexions clientes.
     * <p>
     * Cette méthode tourne en continu tant que {@code running} est true.
     * Pour chaque connexion acceptée, un nouveau thread est créé pour gérer le client.
     * </p>
     */
    private void boucleServeur() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("ReseauSatellites écoute sur le port " + port);
            while (marche) {
                Socket clientSocket = serverSocket.accept();
                new Thread(() -> gererClient(clientSocket)).start();
            }
        } catch (IOException e) {
            if (marche) {
                e.printStackTrace();
            }
        }
    }
    /**
     * Gère la communication avec un client connecté.
     * <p>
     * Lit les commandes ligne par ligne et envoie les réponses appropriées.
     * La connexion reste ouverte jusqu'à ce que le client se déconnecte.
     * </p>
     *
     * @param clientSocket le socket de connexion du client
     */
    private void gererClient(Socket clientSocket) {
        try (BufferedReader in = new BufferedReader(
                new InputStreamReader(clientSocket.getInputStream()));
             PrintWriter out = new PrintWriter(
                     new OutputStreamWriter(clientSocket.getOutputStream()), true)) {

            String ligne;
            while ((ligne = in.readLine()) != null) {
                String reponse = traiterCommande(ligne);
                if (reponse != null) {
                    out.println(reponse);
                }
            }
        } catch (IOException e) {
            // log si besoin
        } finally {
            try { clientSocket.close(); } catch (IOException ignored) {}
        }
    }

    /**
     * Traite une commande reçue d'un satellite et retourne une réponse.
     * <p>
     * Commandes supportées :
     * <ul>
     *   <li><b>DATA &lt;idSatellite&gt; &lt;données&gt;</b> : Stocke les objets détectés</li>
     *   <li><b>GET_OBSTACLES</b> : Retourne tous les objets connus</li>
     *   <li><b>CLEAR_OBSTACLES</b> : Efface tous les objets (debug)</li>
     * </ul>
     * </p>
     *
     * @param commande la ligne de commande reçue
     * @return la réponse à envoyer au client (OK/ERR suivi de détails)
     */

    private String traiterCommande(String commande) {
        String[] parties = commande.split(" ", 3);

        if (parties.length == 0) {
            return "ERR:COMMANDE_VIDE";
        }

        String cmd = parties[0];

        switch (cmd) {
            case "DATA":
                if (parties.length >= 3) {
                    String idSatellite = parties[1];
                    String donnees = parties[2];
                    return traiterReceptionObjets(donnees);
                }
                return "ERR:FORMAT_DATA_INVALIDE";

            case "GET_OBSTACLES":
                return traiterDemandeObjets();

            case "CLEAR_OBSTACLES":
                objetsConnus.clear();
                return "OK:OBSTACLES_CLEARED";

            default:
                return "ERR:COMMANDE_INCONNUE";
        }
    }

    /**
     * Traite la réception d'objets détectés par un satellite.
     * <p>
     * Les objets sont désérialisés puis stockés dans la base centralisée.
     * Si un objet avec le même ID existe déjà, il est écrasé (mise à jour).
     * </p>
     *
     * @param donneesSerialisees les données au format CSV
     * @return "OK:STOCKE:nouveaux:miseAJour" en cas de succès, "ERR:..." sinon
     */
    private String traiterReceptionObjets(String donneesSerialisees) {
        try {
            List<ObjetSpatial> objets = deserialiserObjets(donneesSerialisees);

            if (objets.isEmpty()) {
                return "OK:AUCUNE_DONNEE";
            }

            // Stocker dans la map (écrase les anciens si même ID)
            int nouveaux = 0;
            int miseAJour = 0;

            for (ObjetSpatial objet : objets) {
                if (objetsConnus.containsKey(objet.getIdentifiant())) {
                    miseAJour++;
                } else {
                    nouveaux++;
                }
                objetsConnus.put(String.valueOf(objet.getIdentifiant()), objet);
            }

            return "OK:STOCKE:" + nouveaux + ":" + miseAJour;

        } catch (Exception e) {
            e.printStackTrace();
            return "ERR:ECHEC_STOCKAGE:" + e.getMessage();
        }
    }

    /**
     * Traite une demande de récupération de tous les objets connus.
     * <p>
     * Sérialise tous les objets stockés et les retourne au format CSV.
     * </p>
     *
     * @return "OK:OBSTACLES:données" si des objets existent, "OK:AUCUN_OBSTACLE" sinon,
     *         ou "ERR:..." en cas d'erreur
     */
    private String traiterDemandeObjets() {
        try {
            if (objetsConnus.isEmpty()) {
                return "OK:AUCUN_OBSTACLE";
            }

            // Sérialiser tous les objets
            String donnees = serialiserObjets(new ArrayList<>(objetsConnus.values()));
            return "OK:OBSTACLES:" + donnees;

        } catch (Exception e) {
            e.printStackTrace();
            return "ERR:ECHEC_LECTURE:" + e.getMessage();
        }
    }

    /**
     * Sérialise une liste d'objets spatiaux au format CSV.
     * <p>
     * Format de sortie : {@code id,x,y,vx,vy,masse,rayon;id,x,y,vx,vy,masse,rayon;...}
     * </p>
     *
     * @param objets la liste des objets à sérialiser
     * @return la chaîne sérialisée
     */
    private String serialiserObjets(List<ObjetSpatial> objets) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < objets.size(); i++) {
            ObjetSpatial obj = objets.get(i);

            // Format : id,x,y,vx,vy,masse
            sb.append(obj.getIdentifiant()).append(",")
                    .append(obj.getPosition().getX()).append(",")
                    .append(obj.getPosition().getY()).append(",")
                    .append(obj.getVelociteX()).append(",")
                    .append(obj.getVelociteY()).append(",")
                    .append(obj.getMasse()).append(",")
                    .append(obj.getRayon());

            if (i < objets.size() - 1) {
                sb.append(";");
            }
        }
        return sb.toString();
    }

    /**
     * Désérialise une chaîne CSV en liste d'objets spatiaux.
     * <p>
     * Format attendu : {@code id,x,y,vx,vy,masse,rayon;id,x,y,vx,vy,masse,rayon;...}
     * </p>
     * <p>
     * Les objets mal formés sont ignorés et un message d'erreur est affiché.
     * </p>
     *
     * @param donnees la chaîne CSV à désérialiser
     * @return la liste des objets reconstruits (peut être vide si erreurs)
     */
    private List<ObjetSpatial> deserialiserObjets(String donnees) {
        List<ObjetSpatial> objets = new ArrayList<>();

        if (donnees == null || donnees.trim().isEmpty()) {
            return objets;
        }

        String[] objetsStr = donnees.split(";");

        for (String objetStr : objetsStr) {
            try {
                String[] parts = objetStr.split(",");

                if (parts.length < 6) {
                    System.err.println("[Serveur] Format invalide: " + objetStr);
                    continue;
                }

                int id = Integer.parseInt(parts[0]);
                double x = Double.parseDouble(parts[1]);
                double y = Double.parseDouble(parts[2]);
                double vx = Double.parseDouble(parts[3]);
                double vy = Double.parseDouble(parts[4]);
                double masse = Double.parseDouble(parts[5]);
                double rayon= Double.parseDouble(parts[6]);

                // Créer un objet spatial générique
                ObjetSpatial obj = new ObjetSpatial(new Point2D.Double(x,y), masse, rayon, id,vx,vy);
                objets.add(obj);

            } catch (NumberFormatException e) {
                System.err.println("[Serveur] Erreur parsing nombre: " + objetStr);
            } catch (Exception e) {
                System.err.println("[Serveur] Erreur parsing objet: " + objetStr);
            }
        }

        return objets;
    }



    /**
     * Arrête le serveur réseau.
     * <p>
     * Le serveur cessera d'accepter de nouvelles connexions.
     * Les connexions existantes seront terminées proprement.
     * </p>
     */
    public void arreter() {
        marche = false;
    }

    /**
     * Retourne une copie de la map des objets connus.
     * <p>
     * Cette copie est thread-safe et peut être utilisée sans risque
     * de modification concurrente.
     * </p>
     *
     * @return une nouvelle HashMap contenant tous les objets connus
     */
    public Map<String, ObjetSpatial> getObjetsConnus() {
        return new HashMap<>(objetsConnus);
    }

    /**
     * Retourne le nombre total d'objets stockés dans la base.
     *
     * @return le nombre d'objets connus
     */
    public int getNombreObjets() {
        return objetsConnus.size();
    }

}
