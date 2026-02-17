package threadlol;

/**
 * Campione: statistiche identiche per tutte e 3 le corsie.
 * La differenza tra i campioni è solo estetica (nome/icona).
 *
 *  Vita:     1200
 *  Attacco:  55
 *  Velocità: 2.2
 */
public class Campione extends Minion {

    private Campione(String nome) {
        super(nome, 1200, 55, 2.2, TipoMinion.CAMPIONE);
    }

    public static Campione creaGaren()    { return new Campione("Garen");    }
    public static Campione creaJinx()     { return new Campione("Jinx");     }
    public static Campione creaMalphite() { return new Campione("Malphite"); }
}
