package LeagueOfThreads;

/**
 * Rappresenta la torre nemica di una singola corsia.
 *
 * Ogni corsia ha la propria istanza di {@code Torre}, completamente
 * indipendente dalle altre. La torre rimane in vita finché i minion non la
 * portano a 0 punti vita.
 *
 * VITA_MAX = 4800 – punti vita totali ATTACCO = 160 – danno inflitto ad ogni
 * tiro su un minion INTERVALLO_ATTACCO = 1500 ms – cadenza di fuoco
 *
 * Il metodo {@link #subisciDanno(double)} è {@code synchronized} per evitare
 * race condition quando più thread minion attaccano contemporaneamente la
 * stessa torre. Il campo {@code vita} è {@code volatile} per garantire la
 * visibilità aggiornata tra thread diversi senza sincronizzazione esplicita
 * nelle sole letture.
 */
public class Torre {

    /**
     * Punti vita massimi della torre.
     */
    public static final int VITA_MAX = 4800;

    /**
     * Danno inflitto ad ogni attacco della torre verso un minion.
     */
    public static final double ATTACCO = 160.0;

    /**
     * Intervallo in millisecondi tra un attacco e il successivo.
     */
    public static final int INTERVALLO_ATTACCO = 1500;

    /**
     * Punti vita correnti della torre. Dichiarato {@code volatile} affinché le
     * letture da thread diversi (es. il loop di aggiornamento della
     * progress-bar) vedano sempre il valore più recente.
     */
    private volatile int vita;

    /**
     * Crea una nuova torre con vita piena ({@link #VITA_MAX}).
     */
    public Torre() {
        this.vita = VITA_MAX;
    }

    /**
     * Applica il danno ricevuto dai minion, riducendo i punti vita. La vita non
     * scende mai sotto zero.
     *
     * Metodo {@code synchronized} per gestire attacchi simultanei da più thread
     * {@link MinionThreads}.
     *
     * @param danno quantità di danno da sottrarre
     */
    public synchronized void subisciDanno(double danno) {
        vita -= (int) danno;
        if (vita < 0) {
            vita = 0;
        }
    }

    // ── Getter ───────────────────────────────────────────────────────────
    /**
     * @return punti vita attuali della torre
     */
    public int getVita() {
        return vita;
    }

    /**
     * @return punti vita massimi della torre
     */
    public int getVitaMax() {
        return VITA_MAX;
    }

    /**
     * @return danno inflitto ad ogni attacco
     */
    public double getAttacco() {
        return ATTACCO;
    }

    /**
     * @return intervallo in ms tra un attacco e il successivo
     */
    public int getIntervalloAttacco() {
        return INTERVALLO_ATTACCO;
    }

    /**
     * Indica se la torre è ancora in piedi.
     *
     * @return {@code true} se la vita è maggiore di zero
     */
    public boolean isViva() {
        return vita > 0;
    }
}
