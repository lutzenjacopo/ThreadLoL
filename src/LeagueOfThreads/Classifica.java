package LeagueOfThreads;

import java.util.List;

/**
 * Finestra della classifica finale.
 *
 * Flusso di utilizzo tipico:
 *
 * {@link Frm_Gara} rileva che tutte e 3 le torri sono cadute. Chiama il factory
 * method statico {@link #apri(GestioneClassifica)}. {@code apri} crea la
 * finestra sull'EDT, chiama {@link #mostraClassifica} e la rende visibile.
 *
 * Ogni textbox mostra: {@code NomeCampione  –  Corsia N  –  m:ss.d}
 */
public class Classifica extends javax.swing.JFrame {

    /**
     * Costruisce la finestra e centra la schermata.
     */
    public Classifica() {
        initComponents();
        setLocationRelativeTo(null);
    }

    // ════════════════════════════════════════════════════════════════════
    //  LOGICA – unica parte aggiunta, tutto il resto è GEN invariato
    // ════════════════════════════════════════════════════════════════════
    /**
     * Popola le 3 textbox con i dati della classifica.
     *
     * Per ogni posizione scrive nel campo corrispondente:
     * {@code NomeCampione  –  Corsia N  –  m:ss.d}. Se per qualsiasi motivo ci
     * sono meno di 3 voci registrate, le textbox rimanenti mostrano "—".
     *
     * Deve essere chiamato sull'EDT (garantito da {@link #apri}).
     *
     * @param gc gestore classifica da cui leggere i dati
     */
    public void mostraClassifica(GestioneClassifica gc) {
        List<GestioneClassifica.Voce> voci = gc.getClassifica();
        javax.swing.JTextField[] campi = {txt_Pos1, txt_Pos2, txt_Pos3};

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
     * Factory method: crea, popola e mostra la classifica sull'EDT.
     *
     * Questo metodo può essere chiamato da qualsiasi thread (inclusi i thread
     * corsia di {@link Frm_Gara}): l'operazione viene delegata sull'EDT tramite
     * {@link javax.swing.SwingUtilities#invokeLater}.
     *
     * @param gc gestore classifica con i dati della gara appena conclusa
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
