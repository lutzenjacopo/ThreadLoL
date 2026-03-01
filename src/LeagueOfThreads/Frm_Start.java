package LeagueOfThreads;

/**
 * Schermata di avvio dell'applicazione.
 *
 * Mostra il logo del gioco ({@code Icona.png}) e un unico pulsante Avvia. Al
 * click, apre la finestra delle regole {@link Frm_Regole} e chiude se stessa.
 *
 * Questa è la prima finestra creata da {@link Avvia#main}.
 */
public class Frm_Start extends javax.swing.JFrame {

    /**
     * Costruisce la schermata di avvio e inizializza i componenti del form.
     */
    public Frm_Start() {
        initComponents();
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        lbl_Logo = new javax.swing.JLabel();
        pnl_SfondoStart = new javax.swing.JPanel();
        btn_Avvia = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setBounds(new java.awt.Rectangle(0, 0, 415, 589));
        setMaximumSize(new java.awt.Dimension(440, 620));
        setMinimumSize(new java.awt.Dimension(440, 620));
        setPreferredSize(new java.awt.Dimension(440, 620));
        getContentPane().setLayout(null);

        lbl_Logo.setBackground(new java.awt.Color(0, 102, 153));
        lbl_Logo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/immagini/Icona.png"))); // NOI18N
        getContentPane().add(lbl_Logo);
        lbl_Logo.setBounds(-90, -20, 599, 480);

        pnl_SfondoStart.setBackground(new java.awt.Color(0, 102, 153));

        btn_Avvia.setBackground(new java.awt.Color(0, 204, 204));
        btn_Avvia.setFont(new java.awt.Font("Copperplate Gothic Bold", 0, 36)); // NOI18N
        btn_Avvia.setForeground(new java.awt.Color(153, 153, 0));
        btn_Avvia.setText("Avvia");
        btn_Avvia.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_AvviaActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pnl_SfondoStartLayout = new javax.swing.GroupLayout(pnl_SfondoStart);
        pnl_SfondoStart.setLayout(pnl_SfondoStartLayout);
        pnl_SfondoStartLayout.setHorizontalGroup(
            pnl_SfondoStartLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnl_SfondoStartLayout.createSequentialGroup()
                .addGap(40, 40, 40)
                .addComponent(btn_Avvia, javax.swing.GroupLayout.PREFERRED_SIZE, 340, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(70, Short.MAX_VALUE))
        );
        pnl_SfondoStartLayout.setVerticalGroup(
            pnl_SfondoStartLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnl_SfondoStartLayout.createSequentialGroup()
                .addContainerGap(482, Short.MAX_VALUE)
                .addComponent(btn_Avvia, javax.swing.GroupLayout.PREFERRED_SIZE, 86, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(22, 22, 22))
        );

        getContentPane().add(pnl_SfondoStart);
        pnl_SfondoStart.setBounds(0, 0, 450, 590);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * Gestisce il click sul pulsante Avvia. Apre la finestra delle regole
     * ({@link Frm_Regole}) e chiude questa schermata.
     */
    private void btn_AvviaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_AvviaActionPerformed
        new Frm_Regole().setVisible(true);
        this.dispose();
    }//GEN-LAST:event_btn_AvviaActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btn_Avvia;
    private javax.swing.JLabel lbl_Logo;
    private javax.swing.JPanel pnl_SfondoStart;
    // End of variables declaration//GEN-END:variables
}
