package LeagueOfThreads;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/**
 * Finestra principale della gara: ospita le 3 corsie animate e coordina tutti i
 * thread di gioco.
 *
 * Logica di avvio Dopo {@code setVisible(true)}, chiamare {@link #startGara()}
 * che:
 *
 * Inizializza le 3 torri e il gestore classifica (avvia il cronometro).
 * Configura le progress-bar con la vita massima. Nasconde tutti i label minion
 * e campione presenti nel form. Avvia i 3 thread corsia in parallelo.
 *
 * Ciclo di un'ondata Per ogni ondata, ciascuna corsia:
 *
 * Genera 3 minion con tipo indipendentemente casuale (ogni slot chiama
 * {@link #minionCasuale()}: possono ripetersi tipi uguali). Aggiunge il
 * campione fisso della corsia come 4° slot. Lancia {@link TorreThreads} e 4×
 * {@link MinionThreads} con stagger di {@value #STAGGER_MS} ms tra un minion e
 * il successivo. Monitora la vita della torre tramite la progress-bar ogni 100
 * ms. Alla morte del campione ferma tutti i thread e attende 3 s prima della
 * prossima ondata.
 *
 * Fine gara Quando una torre cade, la vittoria viene registrata in
 * {@link GestioneClassifica}. Quando tutte e 3 le torri sono cadute si apre
 * {@link Classifica} e questa finestra viene chiusa.
 *
 * Mapping label → corsia
 *
 * Corsia 1 (Top): lbl_MinionTop1/2/3 · lbl_CampioneGaren → Garen Corsia 2
 * (Mid): lbl_MinionMid1/2/3 · lbl_CampioneJinx → Jinx Corsia 3 (Bot):
 * lbl_MinionBot1/2/3 · lbl_CampioneMalphite → Malphite
 *
 */
public class Frm_Gara extends javax.swing.JFrame {

    // ── Torri ────────────────────────────────────────────────────────────
    /**
     * Torre nemica della corsia Top (corsia 1).
     */
    private Torre torre1;
    /**
     * Torre nemica della corsia Mid (corsia 2).
     */
    private Torre torre2;
    /**
     * Torre nemica della corsia Bot (corsia 3).
     */
    private Torre torre3;

    // ── Classifica ───────────────────────────────────────────────────────
    /**
     * Gestisce l'ordine di caduta delle torri e i relativi tempi.
     */
    private GestioneClassifica classifica;

    // ── Icone minion (pre-caricate per swap ad ogni ondata) ───────────────
    /**
     * Icona del Soldato, usata per aggiornare il label al tipo estratto.
     */
    private javax.swing.ImageIcon iconSoldato;
    /**
     * Icona del Mago, usata per aggiornare il label al tipo estratto.
     */
    private javax.swing.ImageIcon iconMago;
    /**
     * Icona del Cannone, usata per aggiornare il label al tipo estratto.
     */
    private javax.swing.ImageIcon iconCannone;

    /**
     * Millisecondi di stagger tra un minion e il successivo della stessa
     * ondata.
     */
    private static final long STAGGER_MS = 1500;

    // ════════════════════════════════════════════════════════════════════
    //  COSTRUTTORE
    // ════════════════════════════════════════════════════════════════════
    /**
     * Costruisce la finestra: inizializza i componenti del form, forza il
     * contentPane alle dimensioni esatte della mappa (eliminando spazi bianchi)
     * e centra la finestra sullo schermo.
     */
    public Frm_Gara() {
        initComponents();
        // Forza il contentPane alla dimensione esatta della mappa (1266×710)
        getContentPane().setPreferredSize(new java.awt.Dimension(1266, 710));
        pack();
        setLocationRelativeTo(null);
        setResizable(false);
    }

    // ════════════════════════════════════════════════════════════════════
    //  LOGICA DI GIOCO
    // ════════════════════════════════════════════════════════════════════
    /**
     * Carica un'icona dalla cartella risorse del progetto.
     *
     * @param path percorso relativo, es. "/immagini/Soldato.png"
     * @return {@link javax.swing.ImageIcon} oppure {@code null} se non trovata
     */
    private javax.swing.ImageIcon caricaIcona(String path) {
        try {
            java.net.URL url = getClass().getResource(path);
            return url != null ? new javax.swing.ImageIcon(url) : null;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Inizializza e avvia la gara.
     *
     * Operazioni eseguite nell'ordine:
     *
     * Crea le 3 torri e il gestore classifica (il cronometro parte qui).
     * Configura le 3 progress-bar con valore = {@link Torre#VITA_MAX}. Carica
     * le icone dei 3 tipi di minion normali per lo swap a runtime. Nasconde
     * tutti i label minion e campione del form. Imposta lo z-order affinché
     * minion e torri siano sopra la mappa. Avvia le 3 corsie in thread
     * separati.
     *
     *
     * Deve essere chiamato dopo {@code setVisible(true)}.
     */
    public void startGara() {
        torre1 = new Torre();
        torre2 = new Torre();
        torre3 = new Torre();
        classifica = new GestioneClassifica(); // cronometro parte qui

        // ── Configura le progress-bar ─────────────────────────────────────
        for (javax.swing.JProgressBar pb
                : new javax.swing.JProgressBar[]{pb_Torre1, pb_Torre2, pb_Torre3}) {
            pb.setMinimum(0);
            pb.setMaximum(Torre.VITA_MAX);
            pb.setValue(Torre.VITA_MAX);
            pb.setStringPainted(true);
        }

        // ── Carica icone minion (servono per lo swap a ogni ondata) ───────
        iconSoldato = caricaIcona("/immagini/Soldato.png");
        iconMago = caricaIcona("/immagini/Mago.png");
        iconCannone = caricaIcona("/immagini/Cannone.png");

        // ── Nasconde tutti i label minion e campione del form ─────────────
        for (javax.swing.JLabel lbl : new javax.swing.JLabel[]{
            lbl_MinionTop1, lbl_MinionTop2, lbl_MinionTop3,
            lbl_MinionMid1, lbl_MinionMid2, lbl_MinionMid3,
            lbl_MinionBot1, lbl_MinionBot2, lbl_MinionBot3,
            lbl_CampioneGaren, lbl_CampioneJinx, lbl_CampioneMalphite}) {
            lbl.setVisible(false);
        }

        // ── ZOrder: porta minion e torri sopra il label mappa di sfondo ───
        for (javax.swing.JLabel lbl : new javax.swing.JLabel[]{
            lbl_MinionTop1, lbl_MinionTop2, lbl_MinionTop3,
            lbl_MinionMid1, lbl_MinionMid2, lbl_MinionMid3,
            lbl_MinionBot1, lbl_MinionBot2, lbl_MinionBot3,
            lbl_CampioneGaren, lbl_CampioneJinx, lbl_CampioneMalphite,
            lbl_Torre1, lbl_Torre2, lbl_Torre3}) {
            getContentPane().setComponentZOrder(lbl, 0);
        }

        // ── Avvia le 3 corsie con campione FISSO ──────────────────────────
        avviaCorsia(1, torre1, pb_Torre1, lbl_Torre1,
                new javax.swing.JLabel[]{lbl_MinionTop1, lbl_MinionTop2, lbl_MinionTop3},
                lbl_CampioneGaren, "Garen", Campione::creaGaren);

        avviaCorsia(2, torre2, pb_Torre2, lbl_Torre2,
                new javax.swing.JLabel[]{lbl_MinionMid1, lbl_MinionMid2, lbl_MinionMid3},
                lbl_CampioneJinx, "Jinx", Campione::creaJinx);

        avviaCorsia(3, torre3, pb_Torre3, lbl_Torre3,
                new javax.swing.JLabel[]{lbl_MinionBot1, lbl_MinionBot2, lbl_MinionBot3},
                lbl_CampioneMalphite, "Malphite", Campione::creaMalphite);
    }

    // ── Utilità ──────────────────────────────────────────────────────────
    /**
     * Restituisce l'icona pre-caricata corrispondente al tipo del minion. Usata
     * per aggiornare il label visivo ad ogni ondata.
     *
     * @param m minion di cui si vuole l'icona
     * @return icona del tipo del minion
     */
    private javax.swing.ImageIcon iconaPer(Minion m) {
        switch (m.getTipo()) {
            case SOLDATO:
                return iconSoldato;
            case MAGO:
                return iconMago;
            case CANNONE:
                return iconCannone;
            default:
                return iconSoldato;
        }
    }

    /**
     * Genera un minion di tipo casuale indipendente tra
     * {@link Soldato}, {@link Mago} e {@link Cannone}.
     *
     * Ogni chiamata è indipendente dalle altre: lo stesso tipo può comparire in
     * più slot della stessa ondata (es. 3 Magi di fila).
     *
     * @return nuova istanza di Soldato, Mago o Cannone scelta casualmente
     */
    private Minion minionCasuale() {
        switch ((int) (Math.random() * 3)) {
            case 0:
                return new Soldato();
            case 1:
                return new Mago();
            default:
                return new Cannone();
        }
    }

    // ── Gestione corsia ──────────────────────────────────────────────────
    /**
     * Avvia il ciclo di vita di una corsia in un thread dedicato.
     *
     * Ad ogni ondata:
     *
     * Genera 3 minion con tipo indipendentemente casuale (tramite
     * {@link #minionCasuale()}) e aggiorna le icone dei label. Crea il campione
     * fisso tramite {@code campioneFabbrica}. Lancia {@link TorreThreads} e 4×
     * {@link MinionThreads} con stagger di {@value #STAGGER_MS} ms tra uno e il
     * successivo. Aggiorna la progress-bar della torre ogni 100 ms. Alla morte
     * del campione ferma tutti i thread e attende 3 s.
     *
     *
     * Quando la torre cade:
     *
     * Registra la vittoria in {@link #classifica} (thread-safe). Sostituisce
     * l'icona della torre con l'emoji "💥". Se è l'ultima torre → pausa 800 ms
     * → apre {@link Classifica} → chiude questa finestra.
     *
     *
     * @param numero numero della corsia (1–3)
     * @param torre istanza della torre di questa corsia
     * @param pb progress-bar vita torre
     * @param lblTorre label grafico della torre
     * @param slotLabels 3 label JLabel per i minion normali (slot 1/2/3)
     * @param lblCampione label del campione fisso della corsia
     * @param nomeCampione nome stringa del campione (es. "Garen")
     * @param campioneFabbrica supplier che istanzia il campione ad ogni ondata
     */
    private void avviaCorsia(int numero, Torre torre,
            javax.swing.JProgressBar pb,
            javax.swing.JLabel lblTorre,
            javax.swing.JLabel[] slotLabels,
            javax.swing.JLabel lblCampione,
            String nomeCampione,
            Supplier<Campione> campioneFabbrica) {

        Thread corsiaThread = new Thread(() -> {

            while (torre.isViva()) {

                // ── Genera 3 minion con tipo casuale indipendente ──────
                // Ogni slot chiama minionCasuale() separatamente:
                // i tipi possono ripetersi (es. Mago/Mago/Soldato).
                List<Minion> terzetto = new ArrayList<>();
                for (int i = 0; i < 3; i++) {
                    terzetto.add(minionCasuale());
                }

                // Il 4° slot è sempre il campione fisso della corsia
                Campione campione = campioneFabbrica.get();

                List<Minion> ondata = new ArrayList<>(terzetto);
                ondata.add(campione);

                // ── Aggiorna icone slot e nascondi label sul EDT ───────
                java.util.concurrent.CountDownLatch latch
                        = new java.util.concurrent.CountDownLatch(1);

                javax.swing.SwingUtilities.invokeLater(() -> {
                    for (int i = 0; i < terzetto.size(); i++) {
                        slotLabels[i].setIcon(iconaPer(terzetto.get(i)));
                        slotLabels[i].setVisible(false);
                    }
                    lblCampione.setVisible(false);
                    latch.countDown();
                });

                try {
                    latch.await();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
                }

                // ── TorreThreads: attacca i minion entro soglia ────────
                TorreThreads torreLogic = new TorreThreads(torre, ondata);
                Thread tThread = new Thread(torreLogic, "Torre-" + numero);
                tThread.setDaemon(true);
                tThread.start();

                // ── MinionThreads: un thread per minion, stagger 1500 ms
                MinionThreads[] mLogic = new MinionThreads[4];
                Thread[] mThreads = new Thread[4];

                for (int i = 0; i < 3; i++) {
                    mLogic[i] = new MinionThreads(
                            terzetto.get(i), torre, slotLabels[i], i * STAGGER_MS);
                    mThreads[i] = new Thread(mLogic[i],
                            "M-C" + numero + "-" + terzetto.get(i).getNome() + "-" + i);
                    mThreads[i].setDaemon(true);
                    mThreads[i].start();
                }
                // Campione: 4° slot, parte per ultimo (stagger massimo)
                mLogic[3] = new MinionThreads(campione, torre, lblCampione, 3 * STAGGER_MS);
                mThreads[3] = new Thread(mLogic[3], "M-C" + numero + "-" + campione.getNome());
                mThreads[3].setDaemon(true);
                mThreads[3].start();

                // ── Aggiorna health-bar ogni 100 ms ───────────────────
                while (campione.isVivo() && torre.isViva()) {
                    final int vita = torre.getVita();
                    javax.swing.SwingUtilities.invokeLater(() -> {
                        pb.setValue(vita);
                        pb.setString(vita + " / " + Torre.VITA_MAX);
                    });
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        return;
                    }
                }

                // ── Fine ondata: ferma tutti i thread ─────────────────
                torreLogic.ferma();
                for (MinionThreads ml : mLogic) {
                    ml.ferma();
                }
                for (Thread mt : mThreads) {
                    mt.interrupt();
                }

                // Nasconde i label dell'ondata conclusa
                javax.swing.SwingUtilities.invokeLater(() -> {
                    for (javax.swing.JLabel lbl : slotLabels) {
                        lbl.setVisible(false);
                    }
                    lblCampione.setVisible(false);
                });

                // Aggiorna la progress-bar con la vita residua della torre
                final int vitaFinale = torre.getVita();
                javax.swing.SwingUtilities.invokeLater(() -> {
                    pb.setValue(vitaFinale);
                    pb.setString(vitaFinale + " / " + Torre.VITA_MAX);
                });

                // ── Pausa 3 s prima della prossima ondata ─────────────
                if (torre.isViva()) {
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        return;
                    }
                }
            }

            // ── Torre distrutta ────────────────────────────────────────
            // 1. Registra la vittoria (thread-safe: synchronized in GestioneClassifica)
            classifica.registraVittoria(numero, nomeCampione);

            // 2. Aggiorna grafica: progress-bar a 0, icona torre → 💥
            javax.swing.SwingUtilities.invokeLater(() -> {
                pb.setValue(0);
                pb.setString("DISTRUTTA");
                lblTorre.setIcon(null);
                lblTorre.setText("💥");
                lblTorre.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
                lblTorre.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 40));
            });

            // 3. Ultima torre caduta → pausa breve, poi apri classifica e chiudi
            if (classifica.getTorreCadute() == 3) {
                try {
                    Thread.sleep(800);
                } // lascia vedere l'ultima esplosione
                catch (InterruptedException ignored) {
                }

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
            for (javax.swing.UIManager.LookAndFeelInfo info
                    : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception ignored) {
        }

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
