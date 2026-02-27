package LeagueOfThreads;

import java.util.List;

/**
 * Finestra classifica finale.
 * Il form grafico è già stato configurato nel designer.
 * Il metodo {@link #mostraClassifica(GestioneClassifica)} popola
 * le tre textbox con i dati di chi ha vinto per primo, secondo e terzo.
 *
 * Factory method: {@link #apri(GestioneClassifica)} crea, popola
 * e mostra la finestra sull'EDT.
 */
public class Classifica extends javax.swing.JFrame {

    public Classifica() {
        initComponents();
        setLocationRelativeTo(null);
    }

    // ════════════════════════════════════════════════════════════════════
    //  LOGICA – unica parte aggiunta, tutto il resto è GEN invariato
    // ════════════════════════════════════════════════════════════════════

    /**
     * Popola le textbox con i dati della classifica.
     * Deve essere chiamato sull'EDT.
     *
     * Formato cella: "NomeCampione  –  Corsia N  –  m:ss.d"
     */
    public void mostraClassifica(GestioneClassifica gc) {
        List<GestioneClassifica.Voce> voci = gc.getClassifica();
        javax.swing.JTextField[] campi = { txt_Pos1, txt_Pos2, txt_Pos3 };

        for (int i = 0; i < campi.length; i++) {
            if (i < voci.size()) {
                GestioneClassifica.Voce v = voci.get(i);
                campi[i].setText(
                    v.nomeCampione
                    + "  –  Corsia " + v.corsia
                    + "  –  " + v.tempoFormattato()
                );
            } else {
                campi[i].setText("—");
            }
        }
    }

    /**
     * Crea, popola e mostra la classifica sull'EDT.
     * Da chiamare dal thread della corsia quando tutte le torri sono cadute.
     */
    public static void apri(GestioneClassifica gc) {
        javax.swing.SwingUtilities.invokeLater(() -> {
            Classifica frm = new Classifica();
            frm.mostraClassifica(gc);
            frm.setVisible(true);
        });
    }

    // ════════════════════════════════════════════════════════════════════
    //  CODICE GENERATO DA NETBEANS – NON MODIFICARE
    // ════════════════════════════════════════════════════════════════════

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        lbl_Pos1 = new javax.swing.JLabel();
        lbl_Pos2 = new javax.swing.JLabel();
        lbl_Pos3 = new javax.swing.JLabel();
        txt_Pos1 = new javax.swing.JTextField();
        txt_Pos2 = new javax.swing.JTextField();
        txt_Pos3 = new javax.swing.JTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        lbl_Pos1.setFont(new java.awt.Font("Copperplate Gothic Light", 0, 24)); // NOI18N
        lbl_Pos1.setText("1:");

        lbl_Pos2.setFont(new java.awt.Font("Copperplate Gothic Light", 0, 24)); // NOI18N
        lbl_Pos2.setText("2:");

        lbl_Pos3.setFont(new java.awt.Font("Copperplate Gothic Light", 0, 24)); // NOI18N
        lbl_Pos3.setText("3:");

        txt_Pos3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txt_Pos3ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(lbl_Pos2, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt_Pos2, javax.swing.GroupLayout.DEFAULT_SIZE, 128, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(lbl_Pos1, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt_Pos1))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(lbl_Pos3, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt_Pos3)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(30, 30, 30)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lbl_Pos1, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_Pos1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(32, 32, 32)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lbl_Pos2, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_Pos2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(32, 32, 32)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lbl_Pos3, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_Pos3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(30, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void txt_Pos3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txt_Pos3ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txt_Pos3ActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel lbl_Pos1;
    private javax.swing.JLabel lbl_Pos2;
    private javax.swing.JLabel lbl_Pos3;
    private javax.swing.JTextField txt_Pos1;
    private javax.swing.JTextField txt_Pos2;
    private javax.swing.JTextField txt_Pos3;
    // End of variables declaration//GEN-END:variables
}
