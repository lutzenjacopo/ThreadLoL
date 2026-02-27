package LeagueOfThreads;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

/**
 * Finestra principale della gara.
 *
 * Quando una torre cade, il suo campione viene registrato in
 * {@link GestioneClassifica}. Quando tutte e 3 le torri sono cadute
 * viene aperta la finestra {@link Classifica} con il podio finale.
 *
 * Mapping label → corsia:
 *   Corsia 1: lbl_MinionCorsia1_1/2/3  ·  lbl_CampioneGaren     →  Garen
 *   Corsia 2: lbl_MinionCorsia2_1/2/3  ·  lbl_CampioneJinx      →  Jinx
 *   Corsia 3: lbl_MinionCorsia3_1/2/3  ·  lbl_CampioneMalphite  →  Malphite
 */
public class Frm_Gara extends javax.swing.JFrame {

    // ── Torri ────────────────────────────────────────────────────────────
    private Torre torre1, torre2, torre3;

    // ── Classifica ───────────────────────────────────────────────────────
    private GestioneClassifica classifica;

    // ── Icone minion pre-caricate ─────────────────────────────────────────
    private javax.swing.ImageIcon iconSoldato;
    private javax.swing.ImageIcon iconMago;
    private javax.swing.ImageIcon iconCannone;

    /** Stagger tra un minion e il successivo nella stessa ondata (ms). */
    private static final long STAGGER_MS = 1500;

    // ════════════════════════════════════════════════════════════════════
    //  COSTRUTTORE
    // ════════════════════════════════════════════════════════════════════

    public Frm_Gara() {
        initComponents();
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
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Inizializza torri, classifica, progress-bar, icone.
     * Nasconde tutti i label minion/campione, porta tutto sopra la mappa,
     * avvia le 3 corsie in parallelo.
     * Da chiamare DOPO setVisible(true).
     */
    public void startGara() {
        torre1     = new Torre();
        torre2     = new Torre();
        torre3     = new Torre();
        classifica = new GestioneClassifica();   // ← cronometro parte qui

        // Progress-bar
        for (javax.swing.JProgressBar pb :
                new javax.swing.JProgressBar[]{pb_Torre1, pb_Torre2, pb_Torre3}) {
            pb.setMinimum(0);
            pb.setMaximum(Torre.VITA_MAX);
            pb.setValue(Torre.VITA_MAX);
            pb.setStringPainted(true);
        }

        // Icone minion
        iconSoldato = caricaIcona("/immagini/Soldato.png");
        iconMago    = caricaIcona("/immagini/Mago.png");
        iconCannone = caricaIcona("/immagini/Cannone.png");

        // Nasconde tutti i label minion e campione
        for (javax.swing.JLabel lbl : new javax.swing.JLabel[]{
                lbl_MinionTop1, lbl_MinionTop2, lbl_MinionTop3,
                lbl_MinionMid1, lbl_MinionMid2, lbl_MinionMid3,
                lbl_MinionBot1, lbl_MinionBot2, lbl_MinionBot3,
                lbl_CampioneGaren, lbl_CampioneJinx, lbl_CampioneMalphite}) {
            lbl.setVisible(false);
        }

        // Porta i label sopra la mappa di sfondo
        for (javax.swing.JLabel lbl : new javax.swing.JLabel[]{
                lbl_MinionTop1, lbl_MinionTop2, lbl_MinionTop3,
                lbl_MinionMid1, lbl_MinionMid2, lbl_MinionMid3,
                lbl_MinionBot1, lbl_MinionBot2, lbl_MinionBot3,
                lbl_CampioneGaren, lbl_CampioneJinx, lbl_CampioneMalphite,
                lbl_Torre1, lbl_Torre2, lbl_Torre3}) {
            getContentPane().setComponentZOrder(lbl, 0);
        }

        // Avvia le corsie con campione FISSO
        avviaCorsia(1, torre1, pb_Torre1, lbl_Torre1,
                new javax.swing.JLabel[]{lbl_MinionTop1, lbl_MinionTop2, lbl_MinionTop3,},
                lbl_CampioneGaren,   "Garen",    Campione::creaGaren);

        avviaCorsia(2, torre2, pb_Torre2, lbl_Torre2,
                new javax.swing.JLabel[]{lbl_MinionMid1, lbl_MinionMid2, lbl_MinionMid3,},
                lbl_CampioneJinx,    "Jinx",     Campione::creaJinx);

        avviaCorsia(3, torre3, pb_Torre3, lbl_Torre3,
                new javax.swing.JLabel[]{lbl_MinionBot1, lbl_MinionBot2, lbl_MinionBot3,},
                lbl_CampioneMalphite, "Malphite", Campione::creaMalphite);
    }

    // ── Utilità ──────────────────────────────────────────────────────────

    private javax.swing.ImageIcon iconaPer(Minion m) {
        switch (m.getTipo()) {
            case SOLDATO:  return iconSoldato;
            case MAGO:     return iconMago;
            case CANNONE:  return iconCannone;
            default:       return iconSoldato;
        }
    }

    // ── Gestione corsia ──────────────────────────────────────────────────

    /**
     * Ciclo di vita di una corsia.
     *
     * Quando la torre cade:
     *  1. Registra la vittoria in {@link GestioneClassifica}.
     *  2. Aggiorna grafica torre (💥).
     *  3. Se è l'ultima torre caduta → apre {@link Classifica} e chiude Frm_Gara.
     *
     * @param nomeCampione  nome del campione fisso della corsia (es. "Garen")
     * @param fabbrica      supplier del campione fisso
     */
    private void avviaCorsia(int numero, Torre torre,
                             javax.swing.JProgressBar pb,
                             javax.swing.JLabel lblTorre,
                             javax.swing.JLabel[] slotLabels,
                             javax.swing.JLabel lblCampione,
                             String nomeCampione,
                             Supplier<Campione> fabbrica) {

        Thread corsiaThread = new Thread(() -> {

            while (torre.isViva()) {
                  
                // ── Shuffle dei 3 tipi ─────────────────────────────────
                List<Minion> terzetto = new ArrayList<>(Arrays.asList(
                        new Soldato(), new Mago(), new Cannone()));
                Collections.shuffle(terzetto);

                Campione campione = fabbrica.get();

                List<Minion> ondata = new ArrayList<>(terzetto);
                ondata.add(campione);

                // ── Aggiorna icone e nascondi sul EDT ──────────────────
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
                catch (InterruptedException e) { Thread.currentThread().interrupt(); return; }

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
                mLogic[3]   = new MinionThreads(campione, torre, lblCampione, 3 * STAGGER_MS);
                mThreads[3] = new Thread(mLogic[3], "M-C" + numero + "-" + campione.getNome());
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
                    catch (InterruptedException e) { Thread.currentThread().interrupt(); return; }
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
                    catch (InterruptedException e) { Thread.currentThread().interrupt(); return; }
                }
            }

            // ── Torre distrutta ────────────────────────────────────────
            // 1. Registra nella classifica (thread-safe)
            int posizione = classifica.registraVittoria(numero, nomeCampione);

            // 2. Aggiorna grafica torre
            javax.swing.SwingUtilities.invokeLater(() -> {
                pb.setValue(0);
                pb.setString("DISTRUTTA");
                lblTorre.setIcon(null);
                lblTorre.setText("💥");
                lblTorre.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
                lblTorre.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 40));
            });

            // 3. Se è l'ultima torre → apri classifica e chiudi gara
            if (classifica.getTorreCadute() == 3) {
                // Piccola pausa affinché l'ultima esplosione sia visibile
                try { Thread.sleep(800); }
                catch (InterruptedException ignored) { }

                Classifica.apri(classifica);

                javax.swing.SwingUtilities.invokeLater(() -> Frm_Gara.this.dispose());
            }

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

        lbl_CampioneGaren = new javax.swing.JLabel();
        lbl_CampioneJinx = new javax.swing.JLabel();
        lbl_CampioneMalphite = new javax.swing.JLabel();
        lbl_MinionTop1 = new javax.swing.JLabel();
        lbl_MinionTop2 = new javax.swing.JLabel();
        lbl_MinionTop3 = new javax.swing.JLabel();
        lbl_MinionMid1 = new javax.swing.JLabel();
        lbl_MinionMid2 = new javax.swing.JLabel();
        lbl_MinionMid3 = new javax.swing.JLabel();
        lbl_MinionBot1 = new javax.swing.JLabel();
        lbl_MinionBot2 = new javax.swing.JLabel();
        lbl_MinionBot3 = new javax.swing.JLabel();
        lbl_Torre1 = new javax.swing.JLabel();
        lbl_Torre2 = new javax.swing.JLabel();
        lbl_Torre3 = new javax.swing.JLabel();
        pb_Torre1 = new javax.swing.JProgressBar();
        pb_Torre2 = new javax.swing.JProgressBar();
        pb_Torre3 = new javax.swing.JProgressBar();
        lbl_Sfondo = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setMaximumSize(new java.awt.Dimension(1239, 700));
        setMinimumSize(new java.awt.Dimension(1239, 700));
        getContentPane().setLayout(null);

        lbl_CampioneGaren.setIcon(new javax.swing.ImageIcon(getClass().getResource("/immagini/Garen.png"))); // NOI18N
        getContentPane().add(lbl_CampioneGaren);
        lbl_CampioneGaren.setBounds(-50, 70, 170, 130);

        lbl_CampioneJinx.setIcon(new javax.swing.ImageIcon(getClass().getResource("/immagini/Jinx.png"))); // NOI18N
        getContentPane().add(lbl_CampioneJinx);
        lbl_CampioneJinx.setBounds(-60, 270, 160, 130);

        lbl_CampioneMalphite.setIcon(new javax.swing.ImageIcon(getClass().getResource("/immagini/Malphite.png"))); // NOI18N
        getContentPane().add(lbl_CampioneMalphite);
        lbl_CampioneMalphite.setBounds(-60, 520, 160, 140);

        lbl_MinionTop1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/immagini/Soldato.png"))); // NOI18N
        getContentPane().add(lbl_MinionTop1);
        lbl_MinionTop1.setBounds(130, 80, 170, 92);

        lbl_MinionTop2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/immagini/Mago.png"))); // NOI18N
        getContentPane().add(lbl_MinionTop2);
        lbl_MinionTop2.setBounds(80, 60, 200, 110);

        lbl_MinionTop3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/immagini/Cannone.png"))); // NOI18N
        getContentPane().add(lbl_MinionTop3);
        lbl_MinionTop3.setBounds(10, 70, 240, 92);

        lbl_MinionMid1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/immagini/Soldato.png"))); // NOI18N
        getContentPane().add(lbl_MinionMid1);
        lbl_MinionMid1.setBounds(120, 300, 180, 92);

        lbl_MinionMid2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/immagini/Mago.png"))); // NOI18N
        getContentPane().add(lbl_MinionMid2);
        lbl_MinionMid2.setBounds(70, 280, 200, 110);

        lbl_MinionMid3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/immagini/Cannone.png"))); // NOI18N
        getContentPane().add(lbl_MinionMid3);
        lbl_MinionMid3.setBounds(0, 290, 240, 92);

        lbl_MinionBot1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/immagini/Soldato.png"))); // NOI18N
        getContentPane().add(lbl_MinionBot1);
        lbl_MinionBot1.setBounds(110, 560, 190, 92);

        lbl_MinionBot2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/immagini/Mago.png"))); // NOI18N
        getContentPane().add(lbl_MinionBot2);
        lbl_MinionBot2.setBounds(60, 540, 210, 110);

        lbl_MinionBot3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/immagini/Cannone.png"))); // NOI18N
        getContentPane().add(lbl_MinionBot3);
        lbl_MinionBot3.setBounds(-10, 550, 200, 92);

        lbl_Torre1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/immagini/Torre.png"))); // NOI18N
        getContentPane().add(lbl_Torre1);
        lbl_Torre1.setBounds(1100, 40, 100, 160);

        lbl_Torre2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/immagini/Torre.png"))); // NOI18N
        getContentPane().add(lbl_Torre2);
        lbl_Torre2.setBounds(1100, 270, 100, 160);

        lbl_Torre3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/immagini/Torre.png"))); // NOI18N
        getContentPane().add(lbl_Torre3);
        lbl_Torre3.setBounds(1100, 500, 100, 160);
        getContentPane().add(pb_Torre1);
        pb_Torre1.setBounds(1080, 30, 146, 10);
        getContentPane().add(pb_Torre2);
        pb_Torre2.setBounds(1080, 260, 146, 10);
        getContentPane().add(pb_Torre3);
        pb_Torre3.setBounds(1080, 490, 146, 10);

        lbl_Sfondo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/immagini/Mappa.png"))); // NOI18N
        getContentPane().add(lbl_Sfondo);
        lbl_Sfondo.setBounds(0, -10, 1266, 720);

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
    private javax.swing.JLabel lbl_CampioneGaren;
    private javax.swing.JLabel lbl_CampioneJinx;
    private javax.swing.JLabel lbl_CampioneMalphite;
    private javax.swing.JLabel lbl_MinionBot1;
    private javax.swing.JLabel lbl_MinionBot2;
    private javax.swing.JLabel lbl_MinionBot3;
    private javax.swing.JLabel lbl_MinionMid1;
    private javax.swing.JLabel lbl_MinionMid2;
    private javax.swing.JLabel lbl_MinionMid3;
    private javax.swing.JLabel lbl_MinionTop1;
    private javax.swing.JLabel lbl_MinionTop2;
    private javax.swing.JLabel lbl_MinionTop3;
    private javax.swing.JLabel lbl_Sfondo;
    private javax.swing.JLabel lbl_Torre1;
    private javax.swing.JLabel lbl_Torre2;
    private javax.swing.JLabel lbl_Torre3;
    private javax.swing.JProgressBar pb_Torre1;
    private javax.swing.JProgressBar pb_Torre2;
    private javax.swing.JProgressBar pb_Torre3;
    // End of variables declaration//GEN-END:variables
}
