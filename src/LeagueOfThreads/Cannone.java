package LeagueOfThreads;

/**
 * Minion di tipo Cannone: l'unità d'assedio della corsia.
 *
 * Bilancia vita e attacco in modo intermedio rispetto a Soldato e Mago. È il
 * terzo bersaglio della torre (ordine SOLDATO → MAGO → CANNONE → CAMPIONE
 * definito in {@link TorreThreads}).
 *
 * Vita: 350 Attacco: 25 Velocità: 2.0 → ~150 ms per step → ~14 s per
 * attraversare la corsia
 */
public class Cannone extends Minion {

    /**
     * Crea un nuovo Cannone con statistiche fisse.
     */
    public Cannone() {
        super("Cannone", 350, 25, 2.0, TipoMinion.CANNONE);
    }
}
