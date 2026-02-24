package threadlol;

import LeagueOfThreads.Torre;
import LeagueOfThreads.Campione;
import LeagueOfThreads.Cannone;
import LeagueOfThreads.Mago;
import LeagueOfThreads.Minion;
import LeagueOfThreads.Soldato;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Una corsia di gioco.
 *
 * VELOCITÀ: tutte le unità della corsia si muovono alla stessa velocità
 * (campo {@code velocitaCorsia}), che varia leggermente tra le 3 corsie
 * per rendere la gara imprevedibile.
 *
 * THREAD TORRE: viene tenuto un riferimento al ThreadTorre corrente;
 * ad ogni nuova ondata quello vecchio viene interrotto PRIMA di avviarne uno
 * nuovo, evitando l'accumulo di thread torre che causava l'accelerazione.
 */
public class Corsia {

    public static final int NUM_MINION_PER_ONDATA = 7;

    // Velocità per corsia: leggermente diverse ma ravvicinate
    private static final double[] VELOCITA_CORSIE = { 2.1, 2.3, 1.9 };

    public final int             index;
    public final String          nomeCorsia;
    public final java.awt.Color  colore;
    public final Campione        campione;
    public final Torre           torre;
    public final double          velocitaCorsia; // uguale per tutti in questa corsia

    public final List<Minion>               minions  = new ArrayList<>();
    public final CopyOnWriteArrayList<Minion> bersagli = new CopyOnWriteArrayList<>();

    public volatile int            currentWave = 0;
    public volatile boolean        vinta       = false;

    // Riferimento al ThreadTorre attivo → interrotto prima di ogni nuova ondata
    private volatile ThreadTorre threadTorreAttivo = null;

    private static final Random RNG = new Random();

    public Corsia(int index) {
        this.index          = index;
        this.velocitaCorsia = VELOCITA_CORSIE[index - 1];
        switch (index) {
            case 1:
                nomeCorsia = "CORSIA BLU";
                colore     = new java.awt.Color(60, 120, 255);
                campione   = Campione.creaGaren();
                break;
            case 2:
                nomeCorsia = "CORSIA ROSSA";
                colore     = new java.awt.Color(220, 60, 60);
                campione   = Campione.creaJinx();
                break;
            default:
                nomeCorsia = "CORSIA VERDE";
                colore     = new java.awt.Color(50, 190, 80);
                campione   = Campione.creaMalphite();
                break;
        }
        torre = new Torre();
    }

    /**
     * Interrompe il vecchio ThreadTorre (se esiste) e ne avvia uno nuovo.
     * Chiamato da FRM_Simulazione.startWave() prima di lanciare i thread unità.
     */
    public void avviaThreadTorre(FRM_Simulazione gui) {
        if (threadTorreAttivo != null) {
            threadTorreAttivo.interrupt();
        }
        threadTorreAttivo = new ThreadTorre(this, gui);
        threadTorreAttivo.start();
    }

    /**
     * Resetta le unità per una nuova ondata.
     * La torre mantiene gli HP. Tutte le unità ricevono la velocità della corsia.
     */
    public synchronized void resetOndata() {
        currentWave++;
        minions.clear();
        bersagli.clear();

        // Almeno 1 per tipo, i rimanenti 4 casuali
        List<Minion> pool = new ArrayList<>();
        pool.add(new Soldato());
        pool.add(new Mago());
        pool.add(new Cannone());
        for (int i = 3; i < NUM_MINION_PER_ONDATA; i++) {
            switch (RNG.nextInt(3)) {
                case 0: pool.add(new Soldato()); break;
                case 1: pool.add(new Mago());    break;
                case 2: pool.add(new Cannone()); break;
            }
        }
        Collections.shuffle(pool, RNG);

        // Imposta la velocità della corsia su ogni unità
        for (Minion m : pool) {
            m.setVelocita(velocitaCorsia);
        }
        minions.addAll(pool);

        campione.reset();
        campione.setVelocita(velocitaCorsia);

        bersagli.addAll(minions);
        bersagli.add(campione);
    }

    public List<Minion> tutteLUnita() {
        List<Minion> all = new ArrayList<>(minions);
        all.add(campione);
        return all;
    }

    public String composizioneOndata() {
        int s = 0, m = 0, c = 0;
        for (Minion mn : minions) {
            switch (mn.getTipo()) {
                case SOLDATO: s++; break;
                case MAGO:    m++; break;
                case CANNONE: c++; break;
                default: break;
            }
        }
        return s + "xSoldato " + m + "xMago " + c + "xCannone + " + campione.getNome()
                + "  [vel:" + velocitaCorsia + "]";
    }
}
