package LeagueOfThreads;

/**
 * Thread che gestisce il ciclo di vita grafico e combattivo di un singolo {@link Minion}.
 *
 * Il thread esegue tre fasi sequenziali:
 * 
 *   Fase 0 – Staggering: attende {@code startDelayMs} millisecondi prima
 *       di comparire sullo schermo. Ogni minion dell'ondata parte con 1500 ms di ritardo
 *       rispetto al precedente, creando una distanza visiva leggibile.
 *   Fase 1 – Avanzamento: sposta il {@link javax.swing.JLabel} associato
 *       da sinistra ({@link #X_START}) verso la torre ({@link #X_TORRE}),
 *       step per step (posizione 0 → 95). Il delay tra uno step e il successivo
 *       vale {@code SPEED_MULT / velocita} ms.
 *   Fase 2 – Attacco: il minion è davanti alla torre e la colpisce
 *       ogni 1000 ms finché la torre o il minion stesso non muoiono.
 * 
 *
 * Tutti gli aggiornamenti grafici (posizione e visibilità del JLabel) vengono
 * inviati sull'Event Dispatch Thread (EDT) tramite
 * {@link javax.swing.SwingUtilities#invokeLater}.
 *
 * Il flag {@code volatile attivo} consente a {@link #ferma()} di interrompere
 * il ciclo in modo cooperativo senza usare {@link Thread#stop()}.
 */
public class MinionThreads implements Runnable {

    /** Coordinata X di schermo corrispondente alla posizione logica 0 (partenza). */
    public static final int X_START = 0;

    /** Coordinata X di schermo corrispondente alla posizione logica 95 (davanti alla torre). */
    public static final int X_TORRE = 1070;

    /**
     * Moltiplicatore usato per calcolare il delay per step.
     * {@code delayStep = SPEED_MULT / velocita} ms.
     * Con velocità 2.0 → 150 ms/step → circa 14 s per attraversare la corsia.
     */
    private static final double SPEED_MULT = 300.0;

    /** Modello del minion: dati, stato e statistiche. */
    private final Minion minion;

    /** Torre nemica della corsia, bersaglio dell'attacco in Fase 2. */
    private final Torre torre;

    /** JLabel del form a cui è associata l'icona di questo minion. */
    private final javax.swing.JLabel label;

    /**
     * Coordinata Y fissa del label nella corsia.
     * Viene prelevata una sola volta dal form per evitare letture ripetute sull'EDT.
     */
    private final int laneY;

    /** Millisecondi di attesa iniziale prima che il minion compaia (staggering). */
    private final long startDelayMs;

    /**
     * Flag di controllo del loop principale.
     * Impostato a {@code false} da {@link #ferma()} per interrompere il thread
     * in modo cooperativo a fine ondata o a torre distrutta.
     */
    private volatile boolean attivo = true;

    /**
     * Crea un nuovo thread per il minion indicato.
     *
     * @param minion       modello del minion
     * @param torre        torre nemica della corsia
     * @param label        JLabel del form associato a questo minion
     * @param startDelayMs ms di attesa prima della partenza (staggering)
     */
    public MinionThreads(Minion minion, Torre torre,
                         javax.swing.JLabel label,
                         long startDelayMs) {
        this.minion       = minion;
        this.torre        = torre;
        this.label        = label;
        this.laneY        = label.getY();
        this.startDelayMs = startDelayMs;
    }

    /**
     * Segnala al thread di fermarsi al prossimo controllo del flag {@code attivo}.
     * Chiamato da {@link Frm_Gara} a fine ondata o quando la torre viene distrutta.
     */
    public void ferma() { attivo = false; }

    /**
     * Mappa la posizione logica (0–100) alla coordinata X di schermo in pixel.
     *
     * @param pos posizione logica (0–100)
     * @return coordinata X corrispondente in pixel
     */
    private int toScreenX(int pos) {
        return X_START + pos * (X_TORRE - X_START) / 100;
    }

    /**
     * Esegue le tre fasi: staggering → avanzamento → attacco.
     * Al termine (per morte del minion, torre distrutta o chiamata a {@link #ferma()})
     * nasconde il label sull'EDT.
     */
    @Override
    public void run() {

        /* ── Fase 0: staggering ─────────────────────────────────────────── */
        if (startDelayMs > 0) {
            try { Thread.sleep(startDelayMs); }
            catch (InterruptedException e) { Thread.currentThread().interrupt(); return; }
        }
        if (!attivo) return;

        // Rende visibile il label nella posizione di partenza
        javax.swing.SwingUtilities.invokeLater(() -> {
            label.setLocation(toScreenX(0), laneY);
            label.setVisible(true);
        });

        /* ── Fase 1: avanzamento ────────────────────────────────────────── */
        long delayStep = Math.max(15L, (long)(SPEED_MULT / minion.getVelocita()));

        while (attivo && minion.isVivo() && minion.getPosizione() < 95) {
            minion.setPosizione(minion.getPosizione() + 1);
            final int x = toScreenX(minion.getPosizione());
            javax.swing.SwingUtilities.invokeLater(() -> label.setLocation(x, laneY));
            try { Thread.sleep(delayStep); }
            catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                nascondi();
                return;
            }
        }

        if (!attivo || !minion.isVivo()) { nascondi(); return; }

        /* ── Fase 2: attacco alla torre ─────────────────────────────────── */
        minion.setInAttacco(true);

        while (attivo && minion.isVivo() && torre.isViva()) {
            torre.subisciDanno(minion.getAttacco());
            try { Thread.sleep(1000); }
            catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                nascondi();
                return;
            }
        }

        nascondi();
    }

    /**
     * Nasconde il JLabel sull'EDT al termine del thread.
     */
    private void nascondi() {
        javax.swing.SwingUtilities.invokeLater(() -> label.setVisible(false));
    }
}
