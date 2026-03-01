package LeagueOfThreads;

/**
 * Punto di ingresso dell'applicazione LeagueOfThreads.
 *
 * Imposta il Look &amp; Feel Nimbus (se disponibile sulla JVM corrente) per uno
 * stile grafico moderno e uniforme, poi crea e mostra la schermata iniziale
 * {@link Frm_Start} sull'Event Dispatch Thread (EDT).
 *
 * Tutte le operazioni Swing devono essere eseguite sull'EDT per garantire la
 * thread-safety dell'interfaccia grafica; per questo si utilizza
 * {@link java.awt.EventQueue#invokeLater}.
 */
public class Avvia {

    /**
     * Metodo main: avvia l'applicazione sull'EDT.
     *
     * @param args argomenti da riga di comando (non utilizzati)
     */
    public static void main(String[] args) {
        java.awt.EventQueue.invokeLater(() -> {
            // Imposta Nimbus come Look & Feel se disponibile sulla JVM
            try {
                for (javax.swing.UIManager.LookAndFeelInfo info
                        : javax.swing.UIManager.getInstalledLookAndFeels()) {
                    if ("Nimbus".equals(info.getName())) {
                        javax.swing.UIManager.setLookAndFeel(info.getClassName());
                        break;
                    }
                }
            } catch (Exception ignored) {
                // Se Nimbus non è disponibile si usa il L&F predefinito della piattaforma
            }

            // Apre la schermata di avvio
            new Frm_Start().setVisible(true);
        });
    }
}
