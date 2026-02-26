package LeagueOfThreads;

import java.util.List;

/**
 * Thread della Torre nemica.
 * Attacca solo minion con posizione >= 85 (entro il 15% dalla torre).
 * Priorità: SOLDATO → MAGO → CANNONE → CAMPIONE.
 */
public class TorreThreads implements Runnable {

    public static final int SOGLIA_ATTACCO = 85;

    private static final Minion.TipoMinion[] PRIORITA = {
        Minion.TipoMinion.SOLDATO,
        Minion.TipoMinion.MAGO,
        Minion.TipoMinion.CANNONE,
        Minion.TipoMinion.CAMPIONE
    };

    private final Torre        torre;
    private final List<Minion> minion;
    private volatile boolean   attivo = true;

    public TorreThreads(Torre torre, List<Minion> minion) {
        this.torre  = torre;
        this.minion = minion;
    }

    public void ferma() { attivo = false; }

    @Override
    public void run() {
        while (attivo && torre.isViva()) {
            Minion bersaglio = trovaBersaglio();
            if (bersaglio != null) {
                bersaglio.subisciDanno(torre.getAttacco());
            }
            try { Thread.sleep(torre.getIntervalloAttacco()); }
            catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
        }
    }

    private Minion trovaBersaglio() {
        for (Minion.TipoMinion tipo : PRIORITA) {
            for (Minion m : minion) {
                if (m.isVivo() && m.getTipo() == tipo && m.getPosizione() >= SOGLIA_ATTACCO)
                    return m;
            }
        }
        return null;
    }
}
