package LeagueOfThreads;

/**
 * Campione: l'unità speciale e più potente di ogni corsia.
 *
 * Le statistiche sono identiche per tutti e 3 i campioni;
 * la differenza è esclusivamente estetica (nome e icona grafica nel form).
 *
 * Il campione è sempre il quarto e ultimo minion dell'ondata e
 * parte con lo stagger massimo (3 × {@code STAGGER_MS}).
 * È l'ultimo bersaglio della torre (SOLDATO → MAGO → CANNONE → CAMPIONE).
 * La sua morte segna la fine dell'ondata in {@link Frm_Gara}.
 *
 *   Vita:     1200
 *   Attacco:    55
 *   Velocità:   2.0
 *
 * I campioni si creano esclusivamente tramite i factory method statici:
 *   {@link #creaGaren()}    – Corsia Top (corsia 1)
 *   {@link #creaJinx()}     – Corsia Mid (corsia 2)
 *   {@link #creaMalphite()} – Corsia Bot (corsia 3)
 */
public class Campione extends Minion {

    /**
     * Costruttore privato: l'istanziazione avviene solo tramite i factory method,
     * garantendo che ogni corsia usi sempre il proprio campione corretto.
     *
     * @param nome nome del campione (es. "Garen")
     */
    private Campione(String nome) {
        super(nome, 1200, 55, 2.0, TipoMinion.CAMPIONE);
    }

    /**
     * Crea il campione Garen, assegnato alla Corsia Top (corsia 1).
     * @return nuova istanza di Garen
     */
    public static Campione creaGaren() {
        return new Campione("Garen");
    }

    /**
     * Crea il campione Jinx, assegnato alla Corsia Mid (corsia 2).
     * @return nuova istanza di Jinx
     */
    public static Campione creaJinx() {
        return new Campione("Jinx");
    }

    /**
     * Crea il campione Malphite, assegnato alla Corsia Bot (corsia 3).
     * @return nuova istanza di Malphite
     */
    public static Campione creaMalphite() {
        return new Campione("Malphite");
    }
}
