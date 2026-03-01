package LeagueOfThreads;

/**
 * Schermata delle regole del gioco.
 *
 * Mostra il logo ({@code Logo.png}), un'area di testo con la spiegazione
 * delle meccaniche di gioco e un'immagine animata {@code Gioca.gif} che
 * funge da pulsante di avvio.
 *
 * Al click su {@code lbl_Gioca}:
 * 
 *   Crea e rende visibile la finestra di gioco {@link Frm_Gara}.
 *   Chiama {@link Frm_Gara#startGara()} per inizializzare e avviare la gara.
 *   Chiude questa finestra.
 * 
 */
public class Frm_Regole extends javax.swing.JFrame {

    /**
     * Costruisce la schermata delle regole e inizializza i componenti del form.
     */
    public Frm_Regole() {
        initComponents();
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pnl_Sfondo4 = new javax.swing.JPanel();
        lbl_Titolo = new javax.swing.JLabel();
        pnl_Sfondo3 = new javax.swing.JPanel();
        pnl_Sfondo2 = new javax.swing.JPanel();
        spl_SfondoRegole = new javax.swing.JScrollPane();
        txa_Regole = new javax.swing.JTextArea();
        lbl_Gioca = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        pnl_Sfondo4.setBackground(new java.awt.Color(0, 102, 153));
        lbl_Titolo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/immagini/Logo.png"))); // NOI18N
        pnl_Sfondo3.setBackground(new java.awt.Color(0, 102, 102));
        pnl_Sfondo2.setBackground(new java.awt.Color(0, 0, 51));

        txa_Regole.setEditable(false);
        txa_Regole.setBackground(new java.awt.Color(0, 0, 51));
        txa_Regole.setColumns(20);
        txa_Regole.setFont(new java.awt.Font("Copperplate Gothic Bold", 0, 14)); // NOI18N
        txa_Regole.setForeground(new java.awt.Color(153, 153, 0));
        txa_Regole.setRows(5);
        txa_Regole.setText("Funzionamento del Gioco:\nOgni corsia avrà una torre da Distruggere !\nLa torre subirà danni grazie ai minion e al Campione\nOgni ondata è composta da 3 minion più il Campione\nLa torre attacca in un ordine specifico Soldato-Mago-Cannone-Campione\nQuando muore il campione ci sarà un attesa e ricomincerà l'ondata\nI minion verranno sempre randomizzati\nBuon divertimento!!!");
        spl_SfondoRegole.setViewportView(txa_Regole);

        javax.swing.GroupLayout pnl_Sfondo2Layout = new javax.swing.GroupLayout(pnl_Sfondo2);
        pnl_Sfondo2.setLayout(pnl_Sfondo2Layout);
        pnl_Sfondo2Layout.setHorizontalGroup(
            pnl_Sfondo2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnl_Sfondo2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(spl_SfondoRegole, javax.swing.GroupLayout.DEFAULT_SIZE, 722, Short.MAX_VALUE)
                .addContainerGap())
        );
        pnl_Sfondo2Layout.setVerticalGroup(
            pnl_Sfondo2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnl_Sfondo2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(spl_SfondoRegole, javax.swing.GroupLayout.DEFAULT_SIZE, 155, Short.MAX_VALUE)
                .addContainerGap())
        );

        javax.swing.GroupLayout pnl_Sfondo3Layout = new javax.swing.GroupLayout(pnl_Sfondo3);
        pnl_Sfondo3.setLayout(pnl_Sfondo3Layout);
        pnl_Sfondo3Layout.setHorizontalGroup(
            pnl_Sfondo3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnl_Sfondo3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(pnl_Sfondo2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        pnl_Sfondo3Layout.setVerticalGroup(
            pnl_Sfondo3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnl_Sfondo3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(pnl_Sfondo2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        lbl_Gioca.setIcon(new javax.swing.ImageIcon(getClass().getResource("/immagini/Gioca.gif"))); // NOI18N
        lbl_Gioca.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lbl_GiocaMouseClicked(evt);
            }
        });

        javax.swing.GroupLayout pnl_Sfondo4Layout = new javax.swing.GroupLayout(pnl_Sfondo4);
        pnl_Sfondo4.setLayout(pnl_Sfondo4Layout);
        pnl_Sfondo4Layout.setHorizontalGroup(
            pnl_Sfondo4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnl_Sfondo4Layout.createSequentialGroup()
                .addGroup(pnl_Sfondo4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnl_Sfondo4Layout.createSequentialGroup()
                        .addGap(23, 23, 23)
                        .addGroup(pnl_Sfondo4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lbl_Titolo, javax.swing.GroupLayout.PREFERRED_SIZE, 720, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(pnl_Sfondo3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(pnl_Sfondo4Layout.createSequentialGroup()
                        .addGap(314, 314, 314)
                        .addComponent(lbl_Gioca)))
                .addContainerGap(43, Short.MAX_VALUE))
        );
        pnl_Sfondo4Layout.setVerticalGroup(
            pnl_Sfondo4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnl_Sfondo4Layout.createSequentialGroup()
                .addComponent(lbl_Titolo, javax.swing.GroupLayout.PREFERRED_SIZE, 281, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pnl_Sfondo3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(lbl_Gioca)
                .addContainerGap(19, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(pnl_Sfondo4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(pnl_Sfondo4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * Gestisce il click sull'immagine animata {@code lbl_Gioca}.
     * Crea e avvia la finestra di gioco {@link Frm_Gara}, poi chiude questa schermata.
     */
    private void lbl_GiocaMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lbl_GiocaMouseClicked
        Frm_Gara frmGara = new Frm_Gara();
        frmGara.setVisible(true);
        frmGara.startGara();
        this.dispose();
    }//GEN-LAST:event_lbl_GiocaMouseClicked

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel lbl_Gioca;
    private javax.swing.JLabel lbl_Titolo;
    private javax.swing.JPanel pnl_Sfondo2;
    private javax.swing.JPanel pnl_Sfondo3;
    private javax.swing.JPanel pnl_Sfondo4;
    private javax.swing.JScrollPane spl_SfondoRegole;
    private javax.swing.JTextArea txa_Regole;
    // End of variables declaration//GEN-END:variables
}
