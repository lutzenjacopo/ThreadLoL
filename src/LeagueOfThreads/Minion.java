package LeagueOfThreads;

/**
 * Classe astratta che rappresenta l'unità base del gioco.
 *
 * Ogni minion (e campione) possiede: posizione – valore 0–100 che indica quanta
 * strada ha percorso nella corsia vita / vitaMax– punti vita attuali e massimi
 * attacco – danno inflitto ad ogni colpo velocita – determina la frequenza
 * degli step di avanzamento in {@link MinionThreads} tipo – usato da
 * {@link TorreThreads} per la priorità di bersaglio: SOLDATO &gt; MAGO &gt;
 * CANNONE &gt; CAMPIONE
 *
 *
 * Il metodo {@link #subisciDanno(double)} è {@code synchronized} per garantire
 * la correttezza quando più thread accedono contemporaneamente alla stessa
 * istanza.
 */
public abstract class Minion {

    /**
     * Tipo del minion, usato da {@link TorreThreads} per scegliere il
     * bersaglio. Ordine di priorità: SOLDATO → MAGO → CANNONE → CAMPIONE.
     */
    public enum TipoMinion {
        SOLDATO, MAGO, CANNONE, CAMPIONE
    }

    /**
     * Nome visualizzato (es. "Soldato", "Garen").
     */
    protected String nome;

    /**
     * Punti vita massimi, usati per il reset tra un'ondata e l'altra.
     */
    protected int vitaMax;

    /**
     * Punti vita attuali. Scende a 0 quando il minion muore.
     */
    protected int vita;

    /**
     * Danno inflitto alla torre ad ogni tick di attacco (Fase 2 di
     * {@link MinionThreads}).
     */
    protected double attacco;

    /**
     * Velocità di avanzamento: più alto = delay per step più breve. Formula in
     * {@link MinionThreads}: {@code delayStep = SPEED_MULT / velocita} ms.
     */
    protected double velocita;

    /**
     * Posizione nella corsia, da 0 (partenza) a 100 (davanti alla torre).
     */
    protected int posizione;

    /**
     * {@code true} se il minion ha raggiunto la torre e sta attaccando (Fase
     * 2).
     */
    protected boolean inAttacco;

    /**
     * {@code false} quando i punti vita scendono a zero.
     */
    protected boolean vivo;

    /**
     * Tipo immutabile del minion, assegnato una sola volta nel costruttore.
     */
    protected final TipoMinion tipo;

    /**
     * Costruttore richiamato da tutte le sottoclassi concrete.
     *
     * @param nome nome del minion
     * @param vita punti vita iniziali (coincide con vitaMax)
     * @param attacco danno per colpo alla torre
     * @param velocita velocità di avanzamento nella corsia
     * @param tipo tipo del minion ({@link TipoMinion})
     */
    public Minion(String nome, int vita, double attacco, double velocita, TipoMinion tipo) {
        this.nome = nome;
        this.vitaMax = vita;
        this.vita = vita;
        this.attacco = attacco;
        this.velocita = velocita;
        this.posizione = 0;
        this.inAttacco = false;
        this.vivo = true;
        this.tipo = tipo;
    }

    /**
     * Ripristina il minion alle condizioni iniziali. Usato tra un'ondata e la
     * successiva se si vuole riutilizzare l'istanza.
     */
    public void reset() {
        this.vita = vitaMax;
        this.posizione = 0;
        this.inAttacco = false;
        this.vivo = true;
    }

    /**
     * Applica il danno ricevuto, riducendo i punti vita. Se la vita scende a
     * zero (o sotto) il minion viene dichiarato morto.
     *
     * Metodo {@code synchronized} per gestire attacchi concorrenti da
     * {@link TorreThreads} su istanze condivise.
     *
     * @param danno quantità di danno da sottrarre
     */
    public synchronized void subisciDanno(double danno) {
        vita -= (int) danno;
        if (vita < 0) {
            vita = 0;
        }
        if (vita == 0) {
            vivo = false;
        }
    }

    // ── Getter ───────────────────────────────────────────────────────────
    /**
     * @return nome del minion
     */
    public String getNome() {
        return nome;
    }

    /**
     * @return punti vita attuali
     */
    public int getVita() {
        return vita;
    }

    /**
     * @return punti vita massimi
     */
    public int getVitaMax() {
        return vitaMax;
    }

    /**
     * @return danno per colpo
     */
    public double getAttacco() {
        return attacco;
    }

    /**
     * @return velocità di avanzamento
     */
    public double getVelocita() {
        return velocita;
    }

    /**
     * @return posizione corrente nella corsia (0–100)
     */
    public int getPosizione() {
        return posizione;
    }

    /**
     * @return {@code true} se il minion sta attaccando la torre
     */
    public boolean isInAttacco() {
        return inAttacco;
    }

    /**
     * @return {@code true} se il minion è ancora vivo
     */
    public boolean isVivo() {
        return vivo;
    }

    /**
     * @return tipo del minion
     */
    public TipoMinion getTipo() {
        return tipo;
    }

    // ── Setter ───────────────────────────────────────────────────────────
    /**
     * Aggiorna la posizione del minion nella corsia.
     *
     * @param p nuova posizione (0–100)
     */
    public void setPosizione(int p) {
        this.posizione = p;
    }

    /**
     * Indica se il minion è entrato in fase di attacco alla torre.
     *
     * @param b {@code true} per attivare la fase di attacco
     */
    public void setInAttacco(boolean b) {
        this.inAttacco = b;
    }

    /**
     * Modifica la velocità di avanzamento del minion.
     *
     * @param v nuova velocità
     */
    public void setVelocita(double v) {
        this.velocita = v;
    }
}
