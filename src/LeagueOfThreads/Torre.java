package LeagueOfThreads;

/**
 * Torre nemica per una singola corsia.
 *
 *  Vita:     12 000  (aumentata per resistere alle ondate più a lungo)
 *  Attacco:  160     (aumentato per essere una minaccia reale)
 *  Cadenza:  1 500 ms
 */
public class Torre {

    public static final int    VITA_MAX           = 12000;
    public static final double ATTACCO            = 160.0;
    public static final int    INTERVALLO_ATTACCO = 1500;

    private volatile int vita;

    public Torre() { this.vita = VITA_MAX; }

    public synchronized void subisciDanno(double danno) {
        vita -= (int) danno;
        if (vita < 0) vita = 0;
    }

    public int     getVita()              { return vita;               }
    public int     getVitaMax()           { return VITA_MAX;           }
    public double  getAttacco()           { return ATTACCO;            }
    public int     getIntervalloAttacco() { return INTERVALLO_ATTACCO; }
    public boolean isViva()              { return vita > 0;           }
}
