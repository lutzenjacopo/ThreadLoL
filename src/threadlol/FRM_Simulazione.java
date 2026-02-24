package threadlol;

import LeagueOfThreads.Torre;
import LeagueOfThreads.Minion;
import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * GUI principale del torneo: 3 corsie affiancate.
 *
 * Ogni corsia mostra righe dinamiche per i 7 minion casuali + il Campione.
 * Le righe vengono ricostruite all'inizio di ogni ondata.
 *
 * Priorità torre visibile nel log: Soldato > Mago > Cannone > Campione.
 */
public class FRM_Simulazione extends javax.swing.JFrame {

    // ── Corsie ────────────────────────────────────────────────────────────────
    private final Corsia[] corsie = { new Corsia(1), new Corsia(2), new Corsia(3) };

    // ── Stato torneo ──────────────────────────────────────────────────────────
    private volatile boolean tournamentOver = false;

    // ── GUI per corsia ────────────────────────────────────────────────────────
    /** Pannello unità scrollabile per corsia (svuotato e ricostruito ogni ondata). */
    private final JPanel[]  unitsPanel   = new JPanel[3];
    private final JProgressBar[] pbTorre    = new JProgressBar[3];
    private final JLabel[]       lblTorreHP = new JLabel[3];
    private final JLabel[]       lblStatus  = new JLabel[3];

    /**
     * Per ogni corsia: mappa Minion → componenti GUI della sua riga.
     * Usata da aggiornaMinion() per aggiornare la barra giusta.
     */
    private final Map<Minion, UnitRow>[] rowMaps;

    // ── Log ───────────────────────────────────────────────────────────────────
    private JTextArea txtLog;
    private JLabel    lblWinner;

    // ── Colori ────────────────────────────────────────────────────────────────
    private static final Color BG_DARK  = new Color(14, 17, 26);
    private static final Color BG_CARD  = new Color(22, 27, 42);
    private static final Color COL_GOLD = new Color(200, 162, 50);

    // ── Colori per tipo unità ─────────────────────────────────────────────────
    private static Color coloreTipo(Minion.TipoMinion t) {
        switch (t) {
            case SOLDATO:  return new Color(80,  140, 255);
            case MAGO:     return new Color(190,  70, 230);
            case CANNONE:  return new Color(255, 140,  30);
            case CAMPIONE: return new Color(200, 162,  50);
            default:       return Color.WHITE;
        }
    }

    // ── Icona per tipo ────────────────────────────────────────────────────────
    private static String iconaTipo(Minion.TipoMinion t) {
        switch (t) {
            case SOLDATO:  return "⚔";
            case MAGO:     return "✦";
            case CANNONE:  return "💣";
            case CAMPIONE: return "👑";
            default:       return "•";
        }
    }

    // ══════════════════════════════════════════════════════════════════════════
    @SuppressWarnings("unchecked")
    public FRM_Simulazione() {
        rowMaps = new HashMap[3];
        for (int i = 0; i < 3; i++) rowMaps[i] = new HashMap<>();
        buildUI();
    }

    // ══════════════════════════════════════════════════════════════════════════
    // Costruzione GUI
    // ══════════════════════════════════════════════════════════════════════════

    private void buildUI() {
        setTitle("League of Threads — Torneo 3 Corsie");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1080, 780);
        setMinimumSize(new Dimension(900, 680));
        setLocationRelativeTo(null);
        getContentPane().setBackground(BG_DARK);

        JPanel root = new JPanel(new BorderLayout(6, 6));
        root.setBackground(BG_DARK);
        root.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));
        root.add(buildHeader(),     BorderLayout.NORTH);
        root.add(buildLanesGrid(),  BorderLayout.CENTER);
        root.add(buildLogPanel(),   BorderLayout.SOUTH);
        add(root);
    }

    private JPanel buildHeader() {
        JLabel title = new JLabel("⚔  LEAGUE OF THREADS  —  TORNEO 3 CORSIE",
                SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 20));
        title.setForeground(COL_GOLD);

        lblWinner = new JLabel(" ", SwingConstants.CENTER);
        lblWinner.setFont(new Font("Arial", Font.BOLD, 15));
        lblWinner.setForeground(new Color(255, 220, 60));

        JPanel p = new JPanel(new BorderLayout(0, 3));
        p.setBackground(BG_DARK);
        p.setBorder(BorderFactory.createEmptyBorder(0, 0, 8, 0));
        p.add(title,     BorderLayout.NORTH);
        p.add(lblWinner, BorderLayout.SOUTH);
        return p;
    }

    private JPanel buildLanesGrid() {
        JPanel grid = new JPanel(new GridLayout(1, 3, 8, 0));
        grid.setBackground(BG_DARK);
        for (int i = 0; i < 3; i++) grid.add(buildLaneShell(i));
        return grid;
    }

    /**
     * Costruisce la "shell" fissa di una corsia:
     * header (titolo + status) + pannello unità scrollabile + sezione torre.
     * Il pannello unità sarà popolato dinamicamente da populateLane().
     */
    private JPanel buildLaneShell(int ci) {
        Corsia c = corsie[ci];

        // ── Header corsia ─────────────────────────────────────────────────────
        JLabel laneTitle = new JLabel(c.nomeCorsia, SwingConstants.CENTER);
        laneTitle.setFont(new Font("Arial", Font.BOLD, 13));
        laneTitle.setForeground(c.colore);

        lblStatus[ci] = new JLabel("In attesa...", SwingConstants.CENTER);
        lblStatus[ci].setFont(new Font("Arial", Font.PLAIN, 10));
        lblStatus[ci].setForeground(new Color(140, 140, 180));

        JPanel header = new JPanel(new BorderLayout(0, 2));
        header.setBackground(BG_CARD);
        header.setBorder(BorderFactory.createEmptyBorder(5, 4, 5, 4));
        header.add(laneTitle,    BorderLayout.NORTH);
        header.add(lblStatus[ci], BorderLayout.SOUTH);

        // ── Pannello unità (scrollabile) ──────────────────────────────────────
        unitsPanel[ci] = new JPanel();
        unitsPanel[ci].setLayout(new BoxLayout(unitsPanel[ci], BoxLayout.Y_AXIS));
        unitsPanel[ci].setBackground(BG_CARD);

        JScrollPane scroll = new JScrollPane(unitsPanel[ci],
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.setBackground(BG_CARD);
        scroll.getViewport().setBackground(BG_CARD);
        scroll.setBorder(null);

        // ── Torre ─────────────────────────────────────────────────────────────
        pbTorre[ci] = makeBar(new Color(210, 50, 50), Torre.VITA_MAX);
        pbTorre[ci].setValue(Torre.VITA_MAX);
        pbTorre[ci].setPreferredSize(new Dimension(0, 18));

        lblTorreHP[ci] = new JLabel(
                "HP: " + Torre.VITA_MAX + " / " + Torre.VITA_MAX, SwingConstants.CENTER);
        lblTorreHP[ci].setFont(new Font("Arial", Font.PLAIN, 10));
        lblTorreHP[ci].setForeground(new Color(255, 110, 110));

        JLabel torreTit = new JLabel("🏰 TORRE  —  priorità: Soldato › Mago › Cannone › Campione",
                SwingConstants.CENTER);
        torreTit.setFont(new Font("Arial", Font.BOLD, 10));
        torreTit.setForeground(new Color(255, 80, 80));

        JPanel torrePanel = new JPanel(new BorderLayout(2, 2));
        torrePanel.setBackground(new Color(35, 15, 15));
        torrePanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(140, 40, 40)),
                BorderFactory.createEmptyBorder(5, 6, 5, 6)));
        torrePanel.add(torreTit,       BorderLayout.NORTH);
        torrePanel.add(pbTorre[ci],    BorderLayout.CENTER);
        torrePanel.add(lblTorreHP[ci], BorderLayout.SOUTH);

        // ── Card corsia ───────────────────────────────────────────────────────
        JPanel card = new JPanel(new BorderLayout(0, 3));
        card.setBackground(BG_CARD);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(c.colore.darker(), 2),
                BorderFactory.createEmptyBorder(0, 0, 0, 0)));
        card.add(header,     BorderLayout.NORTH);
        card.add(scroll,     BorderLayout.CENTER);
        card.add(torrePanel, BorderLayout.SOUTH);
        return card;
    }

    /**
     * Svuota e ricostruisce le righe nel pannello unità di una corsia.
     * Chiamato su EDT da startWave().
     */
    private void populateLane(int ci, Corsia c) {
        unitsPanel[ci].removeAll();
        rowMaps[ci].clear();

        // Prima i 7 minion (nell'ordine della lista, già casuale)
        for (Minion m : c.minions) {
            UnitRow row = new UnitRow(m);
            rowMaps[ci].put(m, row);
            unitsPanel[ci].add(row.panel);
            unitsPanel[ci].add(Box.createVerticalStrut(2));
        }

        // Poi il Campione (riga in evidenza)
        UnitRow champRow = new UnitRow(c.campione);
        rowMaps[ci].put(c.campione, champRow);
        unitsPanel[ci].add(Box.createVerticalStrut(4));
        unitsPanel[ci].add(champRow.panel);
        unitsPanel[ci].add(Box.createVerticalStrut(3));

        unitsPanel[ci].revalidate();
        unitsPanel[ci].repaint();
    }

    private JPanel buildLogPanel() {
        txtLog = new JTextArea(6, 60);
        txtLog.setEditable(false);
        txtLog.setBackground(new Color(10, 12, 20));
        txtLog.setForeground(new Color(130, 200, 130));
        txtLog.setFont(new Font("Monospaced", Font.PLAIN, 10));
        txtLog.setLineWrap(true);
        txtLog.setWrapStyleWord(true);

        JScrollPane sp = new JScrollPane(txtLog);
        sp.setBorder(BorderFactory.createLineBorder(new Color(40, 60, 40)));
        sp.setPreferredSize(new Dimension(0, 110));

        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(BG_DARK);
        p.setBorder(BorderFactory.createEmptyBorder(6, 0, 0, 0));
        p.add(sp);
        return p;
    }

    // ══════════════════════════════════════════════════════════════════════════
    // Logica di gioco
    // ══════════════════════════════════════════════════════════════════════════

    public void startSimulation() {
        for (Corsia c : corsie) startWave(c);
    }

    private void startWave(Corsia c) {
        if (tournamentOver || c.vinta) return;
        c.resetOndata();
        final int wave = c.currentWave;
        final int ci   = c.index - 1;

        SwingUtilities.invokeLater(() -> {
            lblStatus[ci].setText("Ondata " + wave + " — " + c.composizioneOndata());
            pbTorre[ci].setValue(c.torre.getVita());
            lblTorreHP[ci].setText("HP: " + c.torre.getVita() + " / " + Torre.VITA_MAX);
            populateLane(ci, c);
        });

        logMessage("[C" + c.index + "] ═══ ONDATA " + wave
                + " ═══  " + c.composizioneOndata());

        // Avvia un thread per ogni unità (7 minion + campione = 8 thread)
        for (Minion m : c.tutteLUnita()) {
            new ThreadLoL(m, c, this, wave).start();
        }
        c.avviaThreadTorre(this);
    }

    // ── Callback per i thread ─────────────────────────────────────────────────

    public void aggiornaMinion(Corsia c, Minion m) {
        SwingUtilities.invokeLater(() -> {
            UnitRow row = rowMaps[c.index - 1].get(m);
            if (row != null) row.refresh(m);
        });
    }

    public void aggiornaTorre(Corsia c) {
        SwingUtilities.invokeLater(() -> {
            pbTorre[c.index - 1].setValue(c.torre.getVita());
            lblTorreHP[c.index - 1].setText(
                    "HP: " + c.torre.getVita() + " / " + Torre.VITA_MAX);
        });
    }

    public synchronized void unitaMorta(Corsia c, Minion m) {
        c.bersagli.remove(m);
        boolean tuttiMorti = c.bersagli.stream().noneMatch(Minion::isVivo);
        if (tuttiMorti && !c.vinta) {
            logMessage("[C" + c.index + "] ⚠ Tutti eliminati! Nuova ondata tra 3s...");
            SwingUtilities.invokeLater(() ->
                    lblStatus[c.index - 1].setText("Prossima ondata in 3s..."));
            new Thread(() -> {
                try { Thread.sleep(3000); } catch (InterruptedException e) { return; }
                if (!tournamentOver && !c.vinta) startWave(c);
            }, "Restart-C" + c.index).start();
        }
    }

    public synchronized void torraDistrutta(Corsia c) {
        if (tournamentOver) return;
        tournamentOver = true;
        c.vinta = true;

        logMessage("🏆═══ VINCITORE: " + c.campione.getNome()
                + " [" + c.nomeCorsia + "] ═══🏆");

        SwingUtilities.invokeLater(() -> {
            pbTorre[c.index - 1].setValue(0);
            pbTorre[c.index - 1].setForeground(Color.GRAY);
            lblTorreHP[c.index - 1].setText("DISTRUTTA!");
            lblStatus[c.index - 1].setText("🏆 VITTORIA!");
            lblStatus[c.index - 1].setForeground(COL_GOLD);
            lblWinner.setText("🏆  CAMPIONE VINCITORE: "
                    + c.campione.getNome() + "  [" + c.nomeCorsia + "]  🏆");

            for (Corsia other : corsie) {
                if (other != c) {
                    lblStatus[other.index - 1].setText("❌ Sconfitta");
                    lblStatus[other.index - 1].setForeground(new Color(180, 80, 80));
                }
            }

            JOptionPane.showMessageDialog(FRM_Simulazione.this,
                    "<html><center>"
                    + "<b style='font-size:17px;color:gold'>🏆 TORNEO CONCLUSO!</b><br><br>"
                    + "Campione vincitore:<br>"
                    + "<b style='font-size:15px'>" + c.campione.getNome() + "</b><br>"
                    + "<i>" + c.nomeCorsia + "</i>"
                    + "</center></html>",
                    "Fine Torneo", JOptionPane.PLAIN_MESSAGE);
        });
    }

    public void logMessage(String msg) {
        String ts = new SimpleDateFormat("HH:mm:ss").format(new Date());
        SwingUtilities.invokeLater(() -> {
            txtLog.append("[" + ts + "] " + msg + "\n");
            txtLog.setCaretPosition(txtLog.getDocument().getLength());
        });
    }

    public boolean isTournamentOver() { return tournamentOver; }

    // ══════════════════════════════════════════════════════════════════════════
    // Helpers GUI
    // ══════════════════════════════════════════════════════════════════════════

    private JProgressBar makeBar(Color color, int max) {
        JProgressBar pb = new JProgressBar(0, max);
        pb.setValue(0);
        pb.setForeground(color);
        pb.setBackground(new Color(35, 38, 55));
        pb.setBorderPainted(false);
        pb.setPreferredSize(new Dimension(0, 12));
        return pb;
    }

    // ══════════════════════════════════════════════════════════════════════════
    // Classe interna: una riga GUI per una singola unità
    // ══════════════════════════════════════════════════════════════════════════

    private class UnitRow {
        final JPanel       panel;
        final JProgressBar pbAdv;
        final JProgressBar pbHP;
        final JLabel       lblHP;
        final boolean      isCampione;

        UnitRow(Minion m) {
            isCampione = (m.getTipo() == Minion.TipoMinion.CAMPIONE);
            Color col  = coloreTipo(m.getTipo());
            String icon = iconaTipo(m.getTipo());

            pbAdv = makeBar(col, 100);
            pbHP  = makeBar(new Color(70, 190, 70), m.getVitaMax());
            pbHP.setValue(m.getVitaMax());

            lblHP = new JLabel("HP:" + m.getVitaMax() + "/" + m.getVitaMax());
            lblHP.setFont(new Font("Arial", Font.PLAIN, 9));
            lblHP.setForeground(new Color(140, 210, 140));
            lblHP.setPreferredSize(new Dimension(76, 12));

            JLabel nameLbl = new JLabel(icon + " " + m.getNome());
            nameLbl.setFont(new Font("Arial", Font.BOLD, isCampione ? 11 : 10));
            nameLbl.setForeground(col);
            nameLbl.setPreferredSize(new Dimension(isCampione ? 100 : 68, 14));

            // Riga avanzata
            JPanel advRow = new JPanel(new BorderLayout(2, 0));
            advRow.setOpaque(false);
            JLabel advLbl = miniLabel("adv:");
            advRow.add(advLbl, BorderLayout.WEST);
            advRow.add(pbAdv,  BorderLayout.CENTER);

            // Riga HP
            JPanel hpRow = new JPanel(new BorderLayout(2, 0));
            hpRow.setOpaque(false);
            JLabel hpLbl = miniLabel("hp: ");
            hpRow.add(hpLbl, BorderLayout.WEST);
            hpRow.add(pbHP,  BorderLayout.CENTER);
            hpRow.add(lblHP, BorderLayout.EAST);

            JPanel bars = new JPanel(new GridLayout(2, 1, 0, 1));
            bars.setOpaque(false);
            bars.add(advRow);
            bars.add(hpRow);

            Color bgCard = isCampione ? new Color(30, 26, 12) : BG_CARD;
            panel = new JPanel(new BorderLayout(4, 0));
            panel.setBackground(bgCard);
            if (isCampione) {
                panel.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(COL_GOLD.darker()),
                        BorderFactory.createEmptyBorder(3, 4, 3, 4)));
            } else {
                panel.setBorder(BorderFactory.createEmptyBorder(2, 4, 2, 4));
            }
            panel.add(nameLbl, BorderLayout.WEST);
            panel.add(bars,    BorderLayout.CENTER);
        }

        void refresh(Minion m) {
            pbAdv.setValue(m.getPosizione());
            pbHP.setValue(m.getVita());
            lblHP.setText("HP:" + m.getVita() + "/" + m.getVitaMax());

            double pct = (double) m.getVita() / m.getVitaMax();
            if      (pct > 0.6) pbHP.setForeground(new Color(70, 190, 70));
            else if (pct > 0.3) pbHP.setForeground(new Color(230, 180, 50));
            else                pbHP.setForeground(new Color(210, 55, 55));

            if (!m.isVivo()) {
                pbAdv.setValue(0);
                pbAdv.setForeground(new Color(70, 70, 80));
                pbHP.setForeground(new Color(70, 70, 80));
                panel.setBackground(new Color(20, 20, 25));
                lblHP.setText("MORTO");
                lblHP.setForeground(new Color(120, 60, 60));
            }
        }
    }

    private JLabel miniLabel(String t) {
        JLabel l = new JLabel(t);
        l.setFont(new Font("Arial", Font.PLAIN, 8));
        l.setForeground(new Color(100, 100, 140));
        l.setPreferredSize(new Dimension(24, 12));
        return l;
    }
}
