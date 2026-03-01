package LeagueOfThreads;

/**
 * Minion di tipo Soldato: il guerriero corazzato della corsia.
 *
 * È il bersaglio prioritario della torre (primo nell'ordine
 * SOLDATO → MAGO → CANNONE → CAMPIONE definito in {@link TorreThreads}).
 *
 * 
 *   Vita:     400
 *   Attacco:   15 (danno per tick alla torre)
 *   Velocità:  2.0 → ~150 ms per step → ~14 s per attraversare la corsia
 * 
 */
public class Soldato extends Minion {

    /**
     * Crea un nuovo Soldato con statistiche fisse.
     */
    public Soldato() {
        super("Soldato", 400, 15, 2.0, TipoMinion.SOLDATO);
    }
}
