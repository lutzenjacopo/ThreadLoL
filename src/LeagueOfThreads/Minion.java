package LeagueOfThreads;

/**
 * Unità base. Ogni minion/campione ha posizione (0–100), vita e attacco.
 * TipoMinion viene usato da ThreadTorre per la priorità di bersaglio:
 * SOLDATO > MAGO > CANNONE > CAMPIONE.
 */
public abstract class Minion {

    public enum TipoMinion { SOLDATO, MAGO, CANNONE, CAMPIONE }

    protected String           nome;
    protected int              vitaMax;
    protected int     vita;
    protected double           attacco;
    protected double           velocita;
    protected int     posizione;
    protected boolean inAttacco;
    protected boolean vivo;
    protected final TipoMinion tipo;

    public Minion(String nome, int vita, double attacco, double velocita, TipoMinion tipo) {
        this.nome      = nome;
        this.vitaMax   = vita;
        this.vita      = vita;
        this.attacco   = attacco;
        this.velocita  = velocita;
        this.posizione = 0;
        this.inAttacco = false;
        this.vivo      = true;
        this.tipo      = tipo;
    }

    public void reset() {
        this.vita      = vitaMax;
        this.posizione = 0;
        this.inAttacco = false;
        this.vivo      = true;
    }

    public synchronized void subisciDanno(double danno) {
        vita -= (int) danno;
        if (vita < 0) vita = 0;
        if (vita == 0) vivo = false;
    }

    public String     getNome()      { return nome;      }
    public int        getVita()      { return vita;      }
    public int        getVitaMax()   { return vitaMax;   }
    public double     getAttacco()   { return attacco;   }
    public double     getVelocita()  { return velocita;  }
    public int        getPosizione() { return posizione; }
    public boolean    isInAttacco()  { return inAttacco; }
    public boolean    isVivo()       { return vivo;      }
    public TipoMinion getTipo()      { return tipo;      }

    public void setPosizione(int p)     { this.posizione = p; }
    public void setInAttacco(boolean b) { this.inAttacco = b; }
    public void setVelocita(double v)   { this.velocita  = v; }
}
