package LeagueOfThreads;

public class Avvia {
    public static void main(String[] args) {
        java.awt.EventQueue.invokeLater(() -> {
            try {
                for (javax.swing.UIManager.LookAndFeelInfo info :
                        javax.swing.UIManager.getInstalledLookAndFeels()) {
                    if ("Nimbus".equals(info.getName())) {
                        javax.swing.UIManager.setLookAndFeel(info.getClassName());
                        break;
                    }
                }
            } catch (Exception ignored) { }
            new Frm_Start().setVisible(true);
        });
    }
}
