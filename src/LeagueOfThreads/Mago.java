package LeagueOfThreads;

/**
 * Minion di tipo Mago: l'unità magica della corsia.
 *
 * Ha poca vita ma il danno più alto tra i minion normali. È il secondo
 * bersaglio della torre (ordine SOLDATO → MAGO → CANNONE → CAMPIONE definito in
 * {@link TorreThreads}).
 *
 *
 * Vita: 200 Attacco: 35 (il più alto tra i minion normali) Velocità: 2.0 → ~150
 * ms per step → ~14 s per attraversare la corsia
 *
 */
public class Mago extends Minion {

    /**
     * Crea un nuovo Mago con statistiche fisse.
     */
    public Mago() {
        super("Mago", 200, 35, 2.0, TipoMinion.MAGO);
    }
}
