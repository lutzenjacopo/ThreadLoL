package LeagueOfThreads;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Gestisce la classifica della gara registrando, in ordine cronologico, quale
 * campione (e quindi quale corsia) ha abbattuto la torre nemica.
 *
 * La classe è thread-safe: i metodi pubblici sono {@code synchronized}, quindi
 * più corsie possono chiamarli contemporaneamente senza race condition
 *
 * Il cronometro parte all'istanziazione (chiamata in
 * {@link Frm_Gara#startGara()}) e il tempo di ogni vittoria viene calcolato
 * come differenza rispetto a quel momento
 */
public class GestioneClassifica {

    // ── Classe interna Voce ───────────────────────────────────────────────
    /**
     * Record immutabile che rappresenta un singolo piazzamento nella
     * classifica.
     */
    public static class Voce {

        /**
         * Posizione finale: 1 = prima torre caduta, 2 = seconda, 3 = terza.
         */
        public final int posizione;

        /**
         * Numero della corsia (1 = Top, 2 = Mid, 3 = Bot).
         */
        public final int corsia;

        /**
         * Nome del campione che ha guidato l'attacco (es. "Garen").
         */
        public final String nomeCampione;

        /**
         * Tempo in ms dall'inizio della gara al momento della vittoria.
         */
        public final long tempoMs;

        /**
         * @param posizione piazzamento (1–3)
         * @param corsia numero della corsia (1–3)
         * @param nomeCampione nome del campione
         * @param tempoMs tempo dall'inizio gara in millisecondi
         */
        Voce(int posizione, int corsia, String nomeCampione, long tempoMs) {
            this.posizione = posizione;
            this.corsia = corsia;
            this.nomeCampione = nomeCampione;
            this.tempoMs = tempoMs;
        }

        /**
         * Restituisce il tempo formattato come {@code m:ss.d} (minuti : secondi
         * . decimi di secondo). Esempio: 93450 ms → {@code "1:33.4"}
         *
         * @return stringa formattata del tempo di vittoria
         */
        public String tempoFormattato() {
            long min = tempoMs / 60_000;
            long sec = (tempoMs % 60_000) / 1000;
            long dec = (tempoMs % 1000) / 100;
            return String.format("%d:%02d.%d", min, sec, dec);
        }
    }

    // ── Stato interno ─────────────────────────────────────────────────────
    /**
     * Timestamp di inizio gara (ms epoch), impostato nel costruttore.
     */
    private final long inizioMs;

    /**
     * Lista dei piazzamenti in ordine di inserimento (indice 0 = primo).
     */
    private final List<Voce> classifica = new ArrayList<>();

    // ── Costruttore ───────────────────────────────────────────────────────
    /**
     * Crea una nuova classifica e avvia il cronometro. Deve essere istanziata
     * all'inizio di {@link Frm_Gara#startGara()}.
     */
    public GestioneClassifica() {
        this.inizioMs = System.currentTimeMillis();
    }

    // ── Metodi pubblici ───────────────────────────────────────────────────
    /**
     * Registra la vittoria di una corsia (torre abbattuta). Assegna
     * automaticamente la posizione in base all'ordine di chiamata.
     *
     * Metodo {@code synchronized}: gestisce chiamate concorrenti dai 3 thread
     * corsia senza race condition
     *
     * @param corsia numero della corsia (1–3)
     * @param nomeCampione nome del campione della corsia
     * @return posizione assegnata (1 = prima torre caduta)
     */
    public synchronized int registraVittoria(int corsia, String nomeCampione) {
        long tempo = System.currentTimeMillis() - inizioMs;
        int posizione = classifica.size() + 1;
        classifica.add(new Voce(posizione, corsia, nomeCampione, tempo));
        return posizione;
    }

    /**
     * Restituisce la classifica come lista immutabile ordinata per posizione.
     *
     * @return lista di {@link Voce} (da 0 a 3 elementi)
     */
    public synchronized List<Voce> getClassifica() {
        return Collections.unmodifiableList(new ArrayList<>(classifica));
    }

    /**
     * Restituisce quante torri sono già cadute. Usato da {@link Frm_Gara} per
     * rilevare la fine della gara (== 3).
     *
     * @return numero di torri cadute (0–3)
     */
    public synchronized int getTorreCadute() {
        return classifica.size();
    }
}
