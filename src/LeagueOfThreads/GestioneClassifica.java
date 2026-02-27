package LeagueOfThreads;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Gestisce la classifica della gara registrando l'ordine in cui
 * le torri vengono abbattute.
 *
 * È thread-safe: più corsie possono chiamare {@link #registraVittoria}
 * contemporaneamente senza problemi di concorrenza.
 */
public class GestioneClassifica {

    // ── Dati di un singolo piazzamento ────────────────────────────────────
    public static class Voce {
        public final int    posizione;      // 1 = primo, 2 = secondo, 3 = terzo
        public final int    corsia;         // 1, 2 o 3
        public final String nomeCampione;   // es. "Garen"
        public final long   tempoMs;        // ms dall'inizio della gara

        Voce(int posizione, int corsia, String nomeCampione, long tempoMs) {
            this.posizione    = posizione;
            this.corsia       = corsia;
            this.nomeCampione = nomeCampione;
            this.tempoMs      = tempoMs;
        }

        /** Tempo formattato come "m:ss.d" */
        public String tempoFormattato() {
            long min  = tempoMs / 60_000;
            long sec  = (tempoMs % 60_000) / 1000;
            long dec  = (tempoMs % 1000)   / 100;
            return String.format("%d:%02d.%d", min, sec, dec);
        }
    }

    private final long          inizioMs;
    private final List<Voce>    classifica = new ArrayList<>();

    public GestioneClassifica() {
        this.inizioMs = System.currentTimeMillis();
    }

    /**
     * Chiamato quando una torre cade.
     * Thread-safe: usa synchronized su this.
     *
     * @param corsia       numero della corsia (1–3)
     * @param nomeCampione nome del campione della corsia
     * @return posizione assegnata (1 = primo a cadere)
     */
    public synchronized int registraVittoria(int corsia, String nomeCampione) {
        long tempo     = System.currentTimeMillis() - inizioMs;
        int  posizione = classifica.size() + 1;
        classifica.add(new Voce(posizione, corsia, nomeCampione, tempo));
        return posizione;
    }

    /**
     * Restituisce la classifica come lista immutabile, ordinata per posizione.
     */
    public synchronized List<Voce> getClassifica() {
        return Collections.unmodifiableList(new ArrayList<>(classifica));
    }

    /** Quante torri sono già cadute. */
    public synchronized int getTorreCadute() {
        return classifica.size();
    }
}
