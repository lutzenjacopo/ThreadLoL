package threadlol;

import LeagueOfThreads.Torre;
import LeagueOfThreads.Minion;
import java.util.ArrayList;
import java.util.List;

/**
 * Thread della torre di una corsia.
 *
 * REGOLE DI INGAGGIO:
 *  - La torre attacca SOLO unità che hanno superato il 75% della progress bar.
 *  - Priorità bersaglio: SOLDATO → MAGO → CANNONE → CAMPIONE.
 *  - A parità di tipo colpisce il più avanzato.
 *
 * CADENZA FISSA: Torre#INTERVALLO_ATTACCO ms tra un colpo e l'altro,
 * senza alcun accumulo tra ondate (il vecchio thread viene interrotto
 * da Corsia#avviaThreadTorre prima di avviarne uno nuovo).
 */
public class ThreadTorre extends Thread {

    /** Soglia minima di avanzata per diventare bersaglio della torre. */
    public static final int SOGLIA_ATTACCO = 75;

    private static final Minion.TipoMinion[] PRIORITA = {
        Minion.TipoMinion.SOLDATO,
        Minion.TipoMinion.MAGO,
        Minion.TipoMinion.CANNONE,
        Minion.TipoMinion.CAMPIONE
    };

    private final Corsia          corsia;
    private final FRM_Simulazione gui;

    public ThreadTorre(Corsia corsia, FRM_Simulazione gui) {
        this.corsia = corsia;
        this.gui    = gui;
        setDaemon(true);
    }

    @Override
    public void run() {
        while (corsia.torre.isViva() && !corsia.vinta && !gui.isTournamentOver()) {
            try { Thread.sleep(Torre.INTERVALLO_ATTACCO); }
            catch (InterruptedException e) { return; } // interrotto da avviaThreadTorre()

            if (!corsia.torre.isViva() || corsia.vinta || gui.isTournamentOver()) return;

            Minion target = trovaBersaglio();
            if (target == null) continue; // nessuno ancora in range (>75%)

            target.subisciDanno(Torre.ATTACCO);
            gui.aggiornaMinion(corsia, target);
            gui.logMessage("[C" + corsia.index + "] 🏰 Torre → "
                    + target.getNome() + " [" + target.getTipo().name() + "]"
                    + "  −" + (int) Torre.ATTACCO
                    + "  HP rimasti: " + target.getVita()
                    + "  (pos: " + target.getPosizione() + "%)");

            if (!target.isVivo()) {
                gui.logMessage("[C" + corsia.index + "] 💀 " + target.getNome() + " eliminato!");
                gui.unitaMorta(corsia, target);
            }
        }
    }

    /**
     * Scorre i tier di priorità.
     * Considera SOLO i bersagli vivi con posizione >= SOGLIA_ATTACCO (75%).
     * Restituisce il più avanzato del primo tier non vuoto.
     */
    private Minion trovaBersaglio() {
        for (Minion.TipoMinion tier : PRIORITA) {
            Minion best   = null;
            int    maxPos = -1;
            for (Minion m : corsia.bersagli) {
                if (m.isVivo()
                        && m.getTipo() == tier
                        && m.getPosizione() >= SOGLIA_ATTACCO) {
                    if (m.getPosizione() > maxPos) {
                        maxPos = m.getPosizione();
                        best   = m;
                    }
                }
            }
            if (best != null) return best;
        }
        return null;
    }
}
