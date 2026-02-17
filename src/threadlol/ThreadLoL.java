package threadlol;

/**
 * Thread per una singola unità in una corsia.
 *
 * Fase 1 – Avanzata: incrementa posizione da 0 a 100.
 * Fase 2 – Attacco torre: colpisce ogni ATTACK_DELAY ms.
 *
 * Il waveId garantisce che thread di ondate vecchie non interferiscano
 * con l'ondata corrente quando si riavvia la corsia.
 */
public class ThreadLoL extends Thread {

    private static final int ATTACK_DELAY = 900;

    private final Minion          minion;
    private final Corsia          corsia;
    private final FRM_Simulazione gui;
    private final int             waveId;
    private final int             baseTickDelay;

    public ThreadLoL(Minion minion, Corsia corsia, FRM_Simulazione gui, int waveId) {
        this.minion        = minion;
        this.corsia        = corsia;
        this.gui           = gui;
        this.waveId        = waveId;
        this.baseTickDelay = (int) (75.0 / minion.getVelocita());
        setDaemon(true);
    }

    @Override
    public void run() {
        // ── Fase 1: Avanzata ──────────────────────────────────────────────────
        while (isActive() && minion.getPosizione() < 100) {
            int jitter = (int)(Math.random() * 18);
            try { Thread.sleep(baseTickDelay + jitter); }
            catch (InterruptedException e) { return; }
            if (!isActive()) return;
            minion.setPosizione(Math.min(minion.getPosizione() + 1, 100));
            gui.aggiornaMinion(corsia, minion);
        }
        if (!isActive()) return;

        // ── Fase 2: Attacco torre ─────────────────────────────────────────────
        minion.setInAttacco(true);
        gui.logMessage("[C" + corsia.index + "] " + minion.getNome()
                + " [" + minion.getTipo().name() + "] attacca la torre!");

        while (isActive()) {
            try { Thread.sleep(ATTACK_DELAY); }
            catch (InterruptedException e) { return; }
            if (!isActive()) return;

            corsia.torre.subisciDanno(minion.getAttacco());
            gui.aggiornaTorre(corsia);
            gui.logMessage("[C" + corsia.index + "] " + minion.getNome()
                    + " −" + (int) minion.getAttacco()
                    + " Torre HP:" + corsia.torre.getVita());

            if (!corsia.torre.isViva()) {
                gui.torraDistrutta(corsia);
                return;
            }
        }
    }

    private boolean isActive() {
        return minion.isVivo()
                && corsia.torre.isViva()
                && !corsia.vinta
                && !gui.isTournamentOver()
                && corsia.currentWave == waveId;
    }
}
