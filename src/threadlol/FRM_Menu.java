package threadlol;

import javax.swing.*;
import java.awt.*;

/**
 * Schermata di benvenuto del torneo.
 */
public class FRM_Menu extends javax.swing.JFrame {

    public FRM_Menu() {
        buildUI();
    }

    private void buildUI() {
        setTitle("League of Threads — Menu");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(520, 480);
        setResizable(false);
        setLocationRelativeTo(null);

        JPanel root = new JPanel(new BorderLayout(0, 0));
        root.setBackground(new Color(12, 15, 24));

        // ── Titolo ─────────────────────────────────────────────────────────────
        JLabel title = new JLabel(
                "<html><center>⚔ LEAGUE OF THREADS ⚔<br>"
                + "<span style='font-size:11px;color:#888'>TORNEO 3 CORSIE</span></center></html>",
                SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 24));
        title.setForeground(new Color(200, 162, 50));
        title.setBorder(BorderFactory.createEmptyBorder(36, 20, 14, 20));

        // ── Schede campioni ───────────────────────────────────────────────────
        JPanel champPanel = new JPanel(new GridLayout(1, 3, 10, 0));
        champPanel.setBackground(new Color(12, 15, 24));
        champPanel.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 20));

        champPanel.add(champCard("⚔ Garen",    "CORSIA BLU",   new Color(60, 120, 255),
                "HP: 900", "ATK: 40", "VEL: ★★★", "Equilibrato"));
        champPanel.add(champCard("⚡ Jinx",     "CORSIA ROSSA", new Color(220, 60, 60),
                "HP: 550", "ATK: 65", "VEL: ★★★★★","Veloce & Letale"));
        champPanel.add(champCard("🪨 Malphite","CORSIA VERDE", new Color(50, 190, 80),
                "HP: 1400","ATK: 28", "VEL: ★★",   "Lento & Corazzato"));

        // ── Regole sintetiche ─────────────────────────────────────────────────
        JLabel rules = new JLabel(
                "<html><center>"
                + "Ogni corsia: <b>Soldato · Mago · Cannone · Campione</b> marciano <b>insieme</b>.<br>"
                + "La torre attacca il più avanzato ogni 1,5 s.<br>"
                + "Se tutti muoiono → nuova ondata (<b>la torre mantiene gli HP</b>).<br>"
                + "<b style='color:#f0c040'>Il primo a distruggere la propria torre vince il torneo!</b>"
                + "</center></html>",
                SwingConstants.CENTER);
        rules.setFont(new Font("Arial", Font.PLAIN, 12));
        rules.setForeground(new Color(180, 180, 210));
        rules.setBorder(BorderFactory.createEmptyBorder(16, 24, 0, 24));

        // ── Bottone ───────────────────────────────────────────────────────────
        JButton btn = new JButton("▶   INIZIA TORNEO");
        btn.setFont(new Font("Arial", Font.BOLD, 15));
        btn.setBackground(new Color(40, 165, 40));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(220, 44));
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) { btn.setBackground(new Color(55, 200, 55)); }
            public void mouseExited (java.awt.event.MouseEvent e) { btn.setBackground(new Color(40, 165, 40)); }
        });
        btn.addActionListener(e -> {
            FRM_Simulazione sim = new FRM_Simulazione();
            sim.setVisible(true);
            sim.startSimulation();
            dispose();
        });

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        btnPanel.setBackground(new Color(12, 15, 24));
        btnPanel.setBorder(BorderFactory.createEmptyBorder(16, 0, 10, 0));
        btnPanel.add(btn);

        JPanel center = new JPanel(new BorderLayout());
        center.setBackground(new Color(12, 15, 24));
        center.add(champPanel, BorderLayout.NORTH);
        center.add(rules,      BorderLayout.CENTER);
        center.add(btnPanel,   BorderLayout.SOUTH);

        root.add(title,  BorderLayout.NORTH);
        root.add(center, BorderLayout.CENTER);
        add(root);
    }

    private JPanel champCard(String name, String corsiaName, Color color,
            String hp, String atk, String vel, String role) {
        JLabel nameLbl = new JLabel(name, SwingConstants.CENTER);
        nameLbl.setFont(new Font("Arial", Font.BOLD, 13));
        nameLbl.setForeground(color);

        JLabel corsiaLbl = new JLabel(corsiaName, SwingConstants.CENTER);
        corsiaLbl.setFont(new Font("Arial", Font.PLAIN, 9));
        corsiaLbl.setForeground(color.darker());

        JLabel statsLbl = new JLabel(
                "<html><center>" + hp + "<br>" + atk + "<br>" + vel
                + "<br><i>" + role + "</i></center></html>",
                SwingConstants.CENTER);
        statsLbl.setFont(new Font("Arial", Font.PLAIN, 11));
        statsLbl.setForeground(new Color(180, 180, 210));

        JPanel card = new JPanel(new BorderLayout(0, 4));
        card.setBackground(new Color(22, 27, 42));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(color.darker(), 1),
                BorderFactory.createEmptyBorder(10, 8, 10, 8)));
        card.add(nameLbl,   BorderLayout.NORTH);
        card.add(statsLbl,  BorderLayout.CENTER);
        card.add(corsiaLbl, BorderLayout.SOUTH);
        return card;
    }

    public static void main(String[] args) {
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception ignored) {}
        java.awt.EventQueue.invokeLater(() -> new FRM_Menu().setVisible(true));
    }
}
