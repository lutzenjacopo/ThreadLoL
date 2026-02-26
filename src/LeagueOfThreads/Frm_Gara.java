package LeagueOfThreads;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Finestra principale della gara.
 *
 * Fix applicati rispetto alla versione precedente:
 *  1. Tutti i label minion vengono NASCOSTI subito in startGara(), prima
 *     che partano i thread, eliminando la situazione in cui le icone fisse
 *     del form designer erano visibili già avanzate nella corsia.
 *  2. La finestra viene dimensionata in modo che il contentPane misuri
 *     esattamente 1266×710 (la porzione visibile della mappa), senza
 *     margini bianchi laterali o inferiori.
 *  3. Stats dei minion tornano ai valori fissi (Soldato/Mago/Cannone).
 *  4. I 3 slot randomizzati sono i primi tre; il 4° è SEMPRE il Campione.
 *
 * Mapping label → corsia:
 *   Corsia 1: jLabel4 (slot A) · jLabel2 (slot B) · jLabel3 (slot C) · lblCampione1
 *   Corsia 2: jLabel5 (slot A) · jLabel6 (slot B) · jLabel7 (slot C) · lblCampione2
 *   Corsia 3: jLabel8 (slot A) · jLabel9 (slot B) · jLabel10(slot C) · lblCampione3
 */
public class Frm_Gara extends javax.swing.JFrame {

    // ── Torri ────────────────────────────────────────────────────────────
    private Torre torre1, torre2, torre3;
    private int corsieTerminate = 0;

    // ── Label campione (creati fuori GEN) ────────────────────────────────
    private javax.swing.JLabel lblCampione1;
    private javax.swing.JLabel lblCampione2;
    private javax.swing.JLabel lblCampione3;

    // ── Icone pre-caricate ───────────────────────────────────────────────
    private javax.swing.ImageIcon iconSoldato;
    private javax.swing.ImageIcon iconMago;
    private javax.swing.ImageIcon iconCannone;

    /** Stagger tra un minion e il successivo nello stesso gruppo (ms). */
    private static final long STAGGER_MS = 1500;

    // ════════════════════════════════════════════════════════════════════
    //  COSTRUTTORE
    // ════════════════════════════════════════════════════════════════════

    public Frm_Gara() {
        initComponents();

        // La mappa (jLabel1) è posizionata a y=-10 e ha altezza 720 →
        // la porzione effettiva visibile è 710 px di altezza, 1266 di larghezza.
        // Impostiamo il preferred size del contentPane e chiamiamo pack()
        // così i decoratori della finestra (titolo, bordi) vengono calcolati
        // automaticamente senza lasciare spazio bianco.
        getContentPane().setPreferredSize(new java.awt.Dimension(1266, 710));
        pack();
        setLocationRelativeTo(null);
        setResizable(false);
    }

    // ════════════════════════════════════════════════════════════════════
    //  LOGICA DI GIOCO
    // ════════════════════════════════════════════════════════════════════

    private javax.swing.ImageIcon caricaIcona(String path) {
        try {
            java.net.URL url = getClass().getResource(path);
            return url != null ? new javax.swing.ImageIcon(url) : null;
        } catch (Exception e) { return null; }
    }

    /**
     * Inizializza torri, progress-bar, icone e label campione,
     * NASCONDE subito tutti i label minion del form designer,
     * poi avvia le 3 corsie in parallelo.
     * Da chiamare DOPO setVisible(true).
     */
    public void startGara() {
        torre1 = new Torre();
        torre2 = new Torre();
        torre3 = new Torre();

        // Progress-bar
        for (javax.swing.JProgressBar pb :
                new javax.swing.JProgressBar[]{pb_Torre1, pb_Torre2, pb_Torre3}) {
            pb.setMinimum(0);
            pb.setMaximum(Torre.VITA_MAX);
            pb.setValue(Torre.VITA_MAX);
            pb.setStringPainted(true);
        }

        // Icone originali (no scaling, usa quelle caricate dal form designer)
        iconSoldato = caricaIcona("/immagini/Soldato.png");
        iconMago    = caricaIcona("/immagini/Mago.png");
        iconCannone = caricaIcona("/immagini/Cannone.png");

        // ── NASCONDE subito tutti i label minion del form ──────────────
        // (erano posizionati dal designer con x fissi che risultavano
        //  visivamente "già avanzati" nella corsia prima del via)
        for (javax.swing.JLabel lbl : new javax.swing.JLabel[]{
                jLabel2, jLabel3, jLabel4,
                jLabel5, jLabel6, jLabel7,
                jLabel8, jLabel9, jLabel10}) {
            lbl.setVisible(false);
        }

        // Label campione (uno per corsia, stessa dimensione del Soldato di riferimento)
        lblCampione1 = creaLblCampione(jLabel3.getY() - 15);
        lblCampione2 = creaLblCampione(jLabel7.getY() - 15);
        lblCampione3 = creaLblCampione(jLabel10.getY() - 15);

        for (javax.swing.JLabel lbl :
                new javax.swing.JLabel[]{lblCampione1, lblCampione2, lblCampione3}) {
            getContentPane().add(lbl);
        }

        // Porta i label minion sopra la mappa di sfondo (jLabel1 è in fondo)
        for (javax.swing.JLabel lbl : new javax.swing.JLabel[]{
                jLabel2, jLabel3, jLabel4, jLabel5, jLabel6,
                jLabel7, jLabel8, jLabel9, jLabel10,
                lblCampione1, lblCampione2, lblCampione3}) {
            getContentPane().setComponentZOrder(lbl, 0);
        }

        // Avvia le corsie
        avviaCorsia(1, torre1, pb_Torre1, lbl_Torre1,
                    new javax.swing.JLabel[]{jLabel4, jLabel2, jLabel3}, lblCampione1);
        avviaCorsia(2, torre2, pb_Torre2, lbl_Torre2,
                    new javax.swing.JLabel[]{jLabel5, jLabel6, jLabel7}, lblCampione2);
        avviaCorsia(3, torre3, pb_Torre3, lbl_Torre3,
                    new javax.swing.JLabel[]{jLabel8, jLabel9, jLabel10}, lblCampione3);
    }

    // ── Utilità ──────────────────────────────────────────────────────────

    private javax.swing.JLabel creaLblCampione(int y) {
        javax.swing.JLabel lbl = new javax.swing.JLabel(iconSoldato);
        lbl.setSize(jLabel3.getWidth(), jLabel3.getHeight());
        lbl.setLocation(MinionThreads.X_START, y);
        lbl.setVisible(false);
        return lbl;
    }

    private Campione campioneCasuale() {
        switch ((int)(Math.random() * 3)) {
            case 0:  return Campione.creaGaren();
            case 1:  return Campione.creaJinx();
            default: return Campione.creaMalphite();
        }
    }

    /** Icona per tipo minion (usa le istanze pre-caricate, nessun resize). */
    private javax.swing.ImageIcon iconaPer(Minion m) {
        switch (m.getTipo()) {
            case SOLDATO: return iconSoldato;
            case MAGO:    return iconMago;
            case CANNONE: return iconCannone;
            default:      return iconSoldato;
        }
    }

    // ── Gestione corsia ──────────────────────────────────────────────────

    /**
     * Ciclo di vita di una corsia.
     *
     * I 3 slot vengono RANDOMIZZATI (shuffle Soldato/Mago/Cannone) ad ogni ondata;
     * il 4° slot è FISSO come Campione (non varia).
     *
     * Stagger: slot 0 parte a t=0, slot 1 a t=1500 ms, slot 2 a t=3000 ms,
     *          Campione a t=4500 ms → distanza grafica costante e leggibile.
     */
    private void avviaCorsia(int numero, Torre torre,
                             javax.swing.JProgressBar pb,
                             javax.swing.JLabel lblTorre,
                             javax.swing.JLabel[] slotLabels,
                             javax.swing.JLabel lblCampione) {

        Thread corsiaThread = new Thread(() -> {

            while (torre.isViva()) {

                // ── Crea e mescola i 3 tipi di minion ─────────────────
                List<Minion> terzetto = new ArrayList<>(Arrays.asList(
                        new Soldato(), new Mago(), new Cannone()));
                Collections.shuffle(terzetto);

                // Il 4° slot è sempre il Campione (non varia, non si shuffla)
                Campione campione = campioneCasuale();

                List<Minion> ondata = new ArrayList<>(terzetto);
                ondata.add(campione);

                // ── Aggiorna icone slot sul EDT ────────────────────────
                java.util.concurrent.CountDownLatch latch =
                        new java.util.concurrent.CountDownLatch(1);

                javax.swing.SwingUtilities.invokeLater(() -> {
                    for (int i = 0; i < terzetto.size(); i++) {
                        slotLabels[i].setIcon(iconaPer(terzetto.get(i)));
                        slotLabels[i].setVisible(false);
                    }
                    lblCampione.setVisible(false);
                    latch.countDown();
                });

                try { latch.await(); }
                catch (InterruptedException e) {
                    Thread.currentThread().interrupt(); return;
                }

                // ── TorreThreads ──────────────────────────────────────
                TorreThreads torreLogic = new TorreThreads(torre, ondata);
                Thread tThread = new Thread(torreLogic, "Torre-" + numero);
                tThread.setDaemon(true);
                tThread.start();

                // ── MinionThreads con stagger ─────────────────────────
                MinionThreads[] mLogic   = new MinionThreads[4];
                Thread[]        mThreads = new Thread[4];

                for (int i = 0; i < 3; i++) {
                    mLogic[i] = new MinionThreads(
                            terzetto.get(i), torre, slotLabels[i], i * STAGGER_MS);
                    mThreads[i] = new Thread(mLogic[i],
                            "M-C" + numero + "-" + terzetto.get(i).getNome());
                    mThreads[i].setDaemon(true);
                    mThreads[i].start();
                }
                // 4° slot: Campione (fisso, parte per ultimo)
                mLogic[3]   = new MinionThreads(campione, torre, lblCampione, 3 * STAGGER_MS);
                mThreads[3] = new Thread(mLogic[3], "M-C" + numero + "-Campione");
                mThreads[3].setDaemon(true);
                mThreads[3].start();

                // ── Aggiorna health-bar finché il campione è vivo ─────
                while (campione.isVivo() && torre.isViva()) {
                    final int vita = torre.getVita();
                    javax.swing.SwingUtilities.invokeLater(() -> {
                        pb.setValue(vita);
                        pb.setString(vita + " / " + Torre.VITA_MAX);
                    });
                    try { Thread.sleep(100); }
                    catch (InterruptedException e) {
                        Thread.currentThread().interrupt(); return;
                    }
                }

                // ── Ferma tutti ───────────────────────────────────────
                torreLogic.ferma();
                for (MinionThreads ml : mLogic)  ml.ferma();
                for (Thread mt       : mThreads) mt.interrupt();

                javax.swing.SwingUtilities.invokeLater(() -> {
                    for (javax.swing.JLabel lbl : slotLabels) lbl.setVisible(false);
                    lblCampione.setVisible(false);
                });

                final int vitaFinale = torre.getVita();
                javax.swing.SwingUtilities.invokeLater(() -> {
                    pb.setValue(vitaFinale);
                    pb.setString(vitaFinale + " / " + Torre.VITA_MAX);
                });

                // ── Pausa 3 s tra le ondate ───────────────────────────
                if (torre.isViva()) {
                    try { Thread.sleep(3000); }
                    catch (InterruptedException e) {
                        Thread.currentThread().interrupt(); return;
                    }
                }
            }

            // ── Torre distrutta ────────────────────────────────────────
            javax.swing.SwingUtilities.invokeLater(() -> {
                pb.setValue(0);
                pb.setString("DISTRUTTA");
                lblTorre.setIcon(null);
                lblTorre.setText("💥");
                lblTorre.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
                lblTorre.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 40));

                corsieTerminate++;
                javax.swing.JOptionPane.showMessageDialog(
                    Frm_Gara.this,
                    "La Torre della Corsia " + numero + " è stata DISTRUTTA! ⚔️"
                    + (corsieTerminate == 1 ? "\nPRIMA torre caduta!" : ""),
                    "Torre Distrutta!", javax.swing.JOptionPane.WARNING_MESSAGE);

                if (corsieTerminate == 3) {
                    javax.swing.JOptionPane.showMessageDialog(
                        Frm_Gara.this,
                        "HAI VINTO! Tutte e 3 le torri sono state distrutte! 🏆",
                        "Vittoria!", javax.swing.JOptionPane.INFORMATION_MESSAGE);
                }
            });

        }, "Corsia-" + numero);

        corsiaThread.setDaemon(true);
        corsiaThread.start();
    }

    // ════════════════════════════════════════════════════════════════════
    //  CODICE GENERATO DA NETBEANS – NON MODIFICARE
    // ════════════════════════════════════════════════════════════════════

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel3 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        lbl_Torre1 = new javax.swing.JLabel();
        lbl_Torre2 = new javax.swing.JLabel();
        lbl_Torre3 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        pb_Torre1 = new javax.swing.JProgressBar();
        pb_Torre2 = new javax.swing.JProgressBar();
        pb_Torre3 = new javax.swing.JProgressBar();
        jLabel1 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        getContentPane().setLayout(null);

        jLabel3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/immagini/Soldato.png"))); // NOI18N
        getContentPane().add(jLabel3);
        jLabel3.setBounds(130, 80, 170, 92);

        jLabel2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/immagini/Mago.png"))); // NOI18N
        getContentPane().add(jLabel2);
        jLabel2.setBounds(80, 60, 150, 110);

        jLabel4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/immagini/Cannone.png"))); // NOI18N
        getContentPane().add(jLabel4);
        jLabel4.setBounds(10, 70, 160, 92);

        jLabel5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/immagini/Cannone.png"))); // NOI18N
        getContentPane().add(jLabel5);
        jLabel5.setBounds(0, 290, 160, 92);

        jLabel6.setIcon(new javax.swing.ImageIcon(getClass().getResource("/immagini/Mago.png"))); // NOI18N
        getContentPane().add(jLabel6);
        jLabel6.setBounds(70, 280, 150, 110);

        jLabel7.setIcon(new javax.swing.ImageIcon(getClass().getResource("/immagini/Soldato.png"))); // NOI18N
        getContentPane().add(jLabel7);
        jLabel7.setBounds(120, 300, 170, 92);

        jLabel8.setIcon(new javax.swing.ImageIcon(getClass().getResource("/immagini/Cannone.png"))); // NOI18N
        getContentPane().add(jLabel8);
        jLabel8.setBounds(-10, 550, 160, 92);

        jLabel9.setIcon(new javax.swing.ImageIcon(getClass().getResource("/immagini/Mago.png"))); // NOI18N
        getContentPane().add(jLabel9);
        jLabel9.setBounds(60, 540, 150, 110);

        lbl_Torre1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/immagini/Torre.png"))); // NOI18N
        getContentPane().add(lbl_Torre1);
        lbl_Torre1.setBounds(1100, 40, 100, 160);

        lbl_Torre2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/immagini/Torre.png"))); // NOI18N
        getContentPane().add(lbl_Torre2);
        lbl_Torre2.setBounds(1100, 270, 100, 160);

        lbl_Torre3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/immagini/Torre.png"))); // NOI18N
        getContentPane().add(lbl_Torre3);
        lbl_Torre3.setBounds(1100, 500, 100, 160);

        jLabel10.setIcon(new javax.swing.ImageIcon(getClass().getResource("/immagini/Soldato.png"))); // NOI18N
        getContentPane().add(jLabel10);
        jLabel10.setBounds(110, 560, 170, 92);

        getContentPane().add(pb_Torre1);
        pb_Torre1.setBounds(1080, 30, 146, 10);
        getContentPane().add(pb_Torre2);
        pb_Torre2.setBounds(1080, 260, 146, 10);
        getContentPane().add(pb_Torre3);
        pb_Torre3.setBounds(1080, 490, 146, 10);

        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/immagini/Mappa.png"))); // NOI18N
        getContentPane().add(jLabel1);
        jLabel1.setBounds(0, -10, 1266, 720);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    public static void main(String args[]) {
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info :
                    javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception ignored) { }

        java.awt.EventQueue.invokeLater(() -> {
            Frm_Gara frm = new Frm_Gara();
            frm.setVisible(true);
            frm.startGara();
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JLabel lbl_Torre1;
    private javax.swing.JLabel lbl_Torre2;
    private javax.swing.JLabel lbl_Torre3;
    private javax.swing.JProgressBar pb_Torre1;
    private javax.swing.JProgressBar pb_Torre2;
    private javax.swing.JProgressBar pb_Torre3;
    // End of variables declaration//GEN-END:variables
}
