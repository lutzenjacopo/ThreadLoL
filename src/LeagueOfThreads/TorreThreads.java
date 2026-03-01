package LeagueOfThreads;

import java.util.List;

/**
 * Thread che gestisce la logica di attacco della torre nemica per una corsia.
 *
 * La torre spara ogni {@link Torre#INTERVALLO_ATTACCO} ms al minion più
 * prioritario presente nell'ondata, ma solo se quel minion ha raggiunto
 * la soglia di avvicinamento ({@link #SOGLIA_ATTACCO} = 85/100 della corsia).
 * Se nessun minion è entro soglia, il ciclo prosegue senza infliggere danni.
 *
 * Ordine di priorità dei bersagli:
 * SOLDATO → MAGO → CANNONE → CAMPIONE
 *
 * Il flag {@code volatile attivo} consente a {@link #ferma()} di interrompere
 * il ciclo in modo cooperativo senza usare {@link Thread#stop()}.
 */
public class TorreThreads implements Runnable {

    /**
     * Soglia di posizione (0–100) oltre la quale la torre apre il fuoco.
     * Un minion deve aver percorso almeno l'85% della corsia per essere attaccabile.
     */
    public static final int SOGLIA_ATTACCO = 85;

    /**
     * Ordine con cui la torre sceglie il bersaglio.
     * Viene scandito dall'indice 0 al 3; viene attaccato il primo minion
     * vivo del tipo con la priorità più alta che è entro {@link #SOGLIA_ATTACCO}.
     */
    private static final Minion.TipoMinion[] PRIORITA = {
        Minion.TipoMinion.SOLDATO,
        Minion.TipoMinion.MAGO,
        Minion.TipoMinion.CANNONE,
        Minion.TipoMinion.CAMPIONE
    };

    /** Modello della torre: vita, attacco e cadenza di fuoco. */
    private final Torre torre;

    /** Lista dei minion dell'ondata corrente (3 normali + 1 campione). */
    private final List<Minion> minion;

    /**
     * Flag di controllo del loop principale.
     * Impostato a {@code false} da {@link #ferma()} per interrompere il thread
     * in modo cooperativo a fine ondata o a torre distrutta.
     */
    private volatile boolean attivo = true;

    /**
     * Crea il thread di attacco per la torre indicata.
     *
     * @param torre  torre nemica della corsia
     * @param minion lista dei minion dell'ondata corrente
     */
    public TorreThreads(Torre torre, List<Minion> minion) {
        this.torre  = torre;
        this.minion = minion;
    }

    /**
     * Segnala al thread di fermarsi al prossimo controllo del flag {@code attivo}.
     * Chiamato da {@link Frm_Gara} a fine ondata o quando la torre cade.
     */
    public void ferma() { attivo = false; }

    /**
     * Loop principale: ogni {@link Torre#INTERVALLO_ATTACCO} ms cerca il bersaglio
     * più prioritario e, se trovato entro soglia, gli infligge {@link Torre#ATTACCO} danni.
     */
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

    /**
     * Scansiona la lista dei minion seguendo {@link #PRIORITA} e restituisce
     * il primo bersaglio valido: vivo + tipo corrispondente + posizione ≥ {@link #SOGLIA_ATTACCO}.
     *
     * @return il minion da attaccare, oppure {@code null} se nessuno è in range
     */
    private Minion trovaBersaglio() {
        for (Minion.TipoMinion tipo : PRIORITA) {
            for (Minion m : minion) {
                if (m.isVivo() && m.getTipo() == tipo && m.getPosizione() >= SOGLIA_ATTACCO) {
                    return m;
                }
            }
        }
        return null;
    }
}
