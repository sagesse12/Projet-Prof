package reservation;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * COHESION : une seule responsabilite = gerer les reservations.
 * Toute la logique metier est ici : creer, modifier, annuler, chercher.
 *
 * Cette classe stocke les reservations dans une liste locale.
 * Si votre equipe a un Singleton RestaurantDatabase, remplacez
 * "this.reservations" par "RestaurantDatabase.getInstance().getReservations()"
 */
public class ReservationManager {

    // Liste des reservations (stockage local)
    private List<Reservation> reservations;

    // Duree minimum entre deux reservations sur la meme table (en minutes)
    private static final int TAMPON_MINUTES = 90;

    // ── Constructeur ──────────────────────────────────────────────────────────
    public ReservationManager() {
        this.reservations = new ArrayList<>();
    }

    // ─────────────────────────────────────────────────────────────────────────
    // CREER
    // ─────────────────────────────────────────────────────────────────────────
    public Reservation creerReservation(String nomClient, String telephone,
                                        int nbPersonnes, int numeroTable,
                                        LocalDateTime dateHeure) {
        // 1. La date doit etre dans le futur
        if (dateHeure.isBefore(LocalDateTime.now())) {
            System.out.println("[ERREUR] La date de reservation doit etre dans le futur.");
            return null;
        }

        // 2. La table doit etre disponible a cette plage horaire
        if (!verifierDisponibilite(numeroTable, dateHeure)) {
            System.out.println("[ERREUR] Table " + numeroTable
                    + " deja reservee dans ce creneau (tampon de "
                    + TAMPON_MINUTES + " min).");
            return null;
        }

        // 3. Nombre de personnes valide
        if (nbPersonnes <= 0) {
            System.out.println("[ERREUR] Le nombre de personnes doit etre > 0.");
            return null;
        }

        // 4. Creation et enregistrement
        Reservation r = new Reservation(nomClient, telephone,
                nbPersonnes, numeroTable, dateHeure);
        reservations.add(r);
        System.out.println("[OK] Reservation #" + r.getId()
                + " creee pour " + nomClient + ".");
        return r;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // MODIFIER
    // ─────────────────────────────────────────────────────────────────────────
    public boolean modifierReservation(int id, String nouveauNom,
                                       String nouveauTel, int nouveauNbP,
                                       LocalDateTime nouvelleDateHeure) {
        Reservation r = trouverParId(id);
        if (r == null) {
            System.out.println("[ERREUR] Reservation #" + id + " introuvable.");
            return false;
        }
        if (r.getStatut() == Reservation.StatutReservation.ANNULEE) {
            System.out.println("[ERREUR] Impossible de modifier une reservation annulee.");
            return false;
        }

        // On ne modifie que les champs non vides / non nuls
        if (nouveauNom != null && !nouveauNom.isBlank())
            r.setNomClient(nouveauNom);
        if (nouveauTel != null && !nouveauTel.isBlank())
            r.setTelephone(nouveauTel);
        if (nouveauNbP > 0)
            r.setNombrePersonnes(nouveauNbP);
        if (nouvelleDateHeure != null) {
            if (nouvelleDateHeure.isBefore(LocalDateTime.now())) {
                System.out.println("[ERREUR] La nouvelle date doit etre dans le futur.");
                return false;
            }
            r.setDateHeure(nouvelleDateHeure);
        }

        System.out.println("[OK] Reservation #" + id + " modifiee.");
        return true;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // ANNULER
    // ─────────────────────────────────────────────────────────────────────────
    public boolean annulerReservation(int id) {
        Reservation r = trouverParId(id);
        if (r == null) {
            System.out.println("[ERREUR] Reservation #" + id + " introuvable.");
            return false;
        }
        if (r.getStatut() == Reservation.StatutReservation.ANNULEE) {
            System.out.println("[INFO] Reservation #" + id + " est deja annulee.");
            return false;
        }
        r.setStatut(Reservation.StatutReservation.ANNULEE);
        System.out.println("[OK] Reservation #" + id + " annulee. Table "
                + r.getNumeroTable() + " liberee.");
        return true;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // CONFIRMER
    // ─────────────────────────────────────────────────────────────────────────
    public boolean confirmerReservation(int id) {
        Reservation r = trouverParId(id);
        if (r == null) {
            System.out.println("[ERREUR] Reservation #" + id + " introuvable.");
            return false;
        }
        if (r.getStatut() == Reservation.StatutReservation.ANNULEE) {
            System.out.println("[ERREUR] Impossible de confirmer une reservation annulee.");
            return false;
        }
        r.setStatut(Reservation.StatutReservation.CONFIRMEE);
        System.out.println("[OK] Reservation #" + id + " confirmee.");
        return true;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // CLOTURER (marquer comme terminee)
    // ─────────────────────────────────────────────────────────────────────────
    public boolean cloturerReservation(int id) {
        Reservation r = trouverParId(id);
        if (r == null) return false;
        r.setStatut(Reservation.StatutReservation.TERMINEE);
        System.out.println("[OK] Reservation #" + id + " cloturee.");
        return true;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // RECHERCHES
    // ─────────────────────────────────────────────────────────────────────────

    /** Toutes les reservations */
    public List<Reservation> listerToutes() {
        return reservations;
    }

    /** Reservations a venir (non annulees, date future) */
    public List<Reservation> getReservationsAVenir() {
        LocalDateTime maintenant = LocalDateTime.now();
        return reservations.stream()
                .filter(r -> r.getDateHeure().isAfter(maintenant))
                .filter(r -> r.getStatut() != Reservation.StatutReservation.ANNULEE)
                .sorted(Comparator.comparing(Reservation::getDateHeure))
                .collect(Collectors.toList());
    }

    /** Reservations d'un jour donne */
    public List<Reservation> chercherParDate(LocalDate date) {
        return reservations.stream()
                .filter(r -> r.getDateHeure().toLocalDate().equals(date))
                .sorted(Comparator.comparing(Reservation::getDateHeure))
                .collect(Collectors.toList());
    }

    /** Recherche par nom de client (insensible a la casse) */
    public List<Reservation> chercherParClient(String nom) {
        return reservations.stream()
                .filter(r -> r.getNomClient().toLowerCase()
                        .contains(nom.toLowerCase()))
                .collect(Collectors.toList());
    }

    /** Reservations sur une table precise */
    public List<Reservation> chercherParTable(int numeroTable) {
        return reservations.stream()
                .filter(r -> r.getNumeroTable() == numeroTable)
                .collect(Collectors.toList());
    }

    /** Chercher par id */
    public Reservation trouverParId(int id) {
        return reservations.stream()
                .filter(r -> r.getId() == id)
                .findFirst()
                .orElse(null);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // DISPONIBILITE
    // ─────────────────────────────────────────────────────────────────────────
    /**
     * Retourne true si la table est libre au creneau demande.
     * On verifie qu'aucune reservation active n'est a moins de TAMPON_MINUTES
     * de la date demandee sur la meme table.
     */
    public boolean verifierDisponibilite(int numeroTable, LocalDateTime dateHeure) {
        return reservations.stream()
                .filter(r -> r.getNumeroTable() == numeroTable)
                .filter(r -> r.getStatut() != Reservation.StatutReservation.ANNULEE)
                .filter(r -> r.getStatut() != Reservation.StatutReservation.TERMINEE)
                .noneMatch(r -> {
                    long ecart = Math.abs(
                            ChronoUnit.MINUTES.between(r.getDateHeure(), dateHeure));
                    return ecart < TAMPON_MINUTES;
                });
    }

    // ─────────────────────────────────────────────────────────────────────────
    // PLANNING JOUR
    // ─────────────────────────────────────────────────────────────────────────
    public void afficherPlanningJour(LocalDate date) {
        List<Reservation> liste = chercherParDate(date);
        System.out.println("\n======= PLANNING DU " + date + " =======");
        if (liste.isEmpty()) {
            System.out.println("  Aucune reservation ce jour.");
        } else {
            liste.forEach(System.out::println);
        }
        System.out.println("=====================================\n");
    }

    // ─────────────────────────────────────────────────────────────────────────
    // ACCES A LA LISTE (pour la persistance)
    // ─────────────────────────────────────────────────────────────────────────
    public List<Reservation> getReservations() {
        return reservations;
    }

    public void setReservations(List<Reservation> reservations) {
        this.reservations = reservations;
    }
}