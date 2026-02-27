package LeagueOfThreads;

/**
 * Thread di un singolo Minion.
 *
 * Fase 0 – attende {@code startDelayMs} (spaziatura visiva tra minion). Fase 1
 * – sposta il JLabel da X_START a X_TORRE (posizione 0→100). Tempi di
 * traversata con SPEED_MULT = 300: Mago (vel 2.8) → ~107 ms/step → ~11 s
 * Campione (vel 2.2) → ~136 ms/step → ~14 s Soldato (vel 2.0) → 150 ms/step →
 * ~15 s Cannone (vel 1.5) → 200 ms/step → ~20 s Fase 2 – attacca la torre ogni
 * secondo.
 *
 * La torre spara solo quando il minion è a posizione >= 85.
 */
public class MinionThreads implements Runnable {

    public static final int X_START = 0;
    public static final int X_TORRE = 1070;

    private static final double SPEED_MULT = 300.0;

    private final Minion minion;
    private final Torre torre;
    private final javax.swing.JLabel label;
    private final int laneY;
    private final long startDelayMs;

    private volatile boolean attivo = true;

    public MinionThreads(Minion minion, Torre torre,
            javax.swing.JLabel label,
            long startDelayMs) {
        this.minion = minion;
        this.torre = torre;
        this.label = label;
        this.laneY = label.getY();
        this.startDelayMs = startDelayMs;
    }

    public void ferma() {
        attivo = false;
    }

    private int toScreenX(int pos) {
        return X_START + pos * (X_TORRE - X_START) / 100;
    }

    @Override
    public void run() {

        /* ── Fase 0: staggering ───────────────────────────────────────── */
        if (startDelayMs > 0) {
            try {
                Thread.sleep(startDelayMs);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
        }
        if (!attivo) {
            return;
        }

        javax.swing.SwingUtilities.invokeLater(() -> {
            label.setLocation(toScreenX(0), laneY);
            label.setVisible(true);
        });

        /* ── Fase 1: avanzamento ──────────────────────────────────────── */
        long delayStep = Math.max(15L, (long) (SPEED_MULT / minion.getVelocita()));

        while (attivo && minion.isVivo() && minion.getPosizione() < 95) {
            minion.setPosizione(minion.getPosizione() + 1);
            final int x = toScreenX(minion.getPosizione());
            javax.swing.SwingUtilities.invokeLater(() -> label.setLocation(x, laneY));
            try {
                Thread.sleep(delayStep);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                nascondi();
                return;
            }
        }

        if (!attivo || !minion.isVivo()) {
            nascondi();
            return;
        }

        /* ── Fase 2: attacco alla torre ───────────────────────────────── */
        minion.setInAttacco(true);

        while (attivo && minion.isVivo() && torre.isViva()) {
            torre.subisciDanno(minion.getAttacco());
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                nascondi();
                return;
            }
        }

        nascondi();
    }

    private void nascondi() {
        javax.swing.SwingUtilities.invokeLater(() -> label.setVisible(false));
    }
}
