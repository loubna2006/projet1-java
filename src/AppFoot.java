import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

public class AppFoot extends JFrame {
    private TeamsPanel teamsPanel;
    private MatchesPanel matchesPanel;

    public AppFoot() {
        setTitle("⚽ Tournoi de Football");
        setSize(920, 680);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        // Tente d'appliquer le Look and Feel du système
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } catch (Exception ignored){}

        teamsPanel = new TeamsPanel();
        // Le MatchesPanel a besoin de TeamsPanel pour obtenir la liste des équipes
        matchesPanel = new MatchesPanel(teamsPanel);

        // Configuration de l'en-tête (NORTH)
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(26,115,232));
        header.setPreferredSize(new Dimension(0,100));
        JLabel lbl = new JLabel("⚽  TOURNOI DE FOOTBALL", SwingConstants.CENTER);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lbl.setForeground(Color.WHITE);
        header.add(lbl, BorderLayout.CENTER);
        add(header, BorderLayout.NORTH);

        // Configuration du panneau central (CENTER) avec les boutons de carte
        JPanel center = new JPanel(new GridBagLayout());
        center.setBackground(new Color(248,249,250));
        center.setBorder(BorderFactory.createEmptyBorder(24,24,24,24));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(14,14,14,14);
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1; gbc.weighty = 1;

        JButton bTeams = cardButton("👥", "Gérer les équipes");
        JButton bMatches = cardButton("⚽", "Gérer les matchs");
        JButton bShow = cardButton("📋", "Afficher les matchs");
        JButton bTable = cardButton("🏆", "Classement");

        gbc.gridx = 0; gbc.gridy = 0; center.add(bTeams, gbc);
        gbc.gridx = 1; center.add(bMatches, gbc);
        gbc.gridx = 0; gbc.gridy = 1; center.add(bShow, gbc);
        gbc.gridx = 1; center.add(bTable, gbc);

        add(center, BorderLayout.CENTER);

        // Configuration des Listeners pour les boutons
        bTeams.addActionListener(e -> showDialog("Gestion des équipes", teamsPanel, 820, 540));
        bMatches.addActionListener(e -> { matchesPanel.updateTeams(); showDialog("Gestion des matchs", matchesPanel, 920, 600); });
        bShow.addActionListener(e -> showMatchesDialog());
        bTable.addActionListener(e -> showClassementDialog());
    }

    /** Crée un bouton de style "carte" pour l'accueil */
    private JButton cardButton(String icon, String title) {
        JButton btn = new JButton();
        btn.setLayout(new BorderLayout());
        btn.setBackground(Color.WHITE);
        btn.setBorder(BorderFactory.createCompoundBorder(new LineBorder(new Color(220,220,224),1,true), BorderFactory.createEmptyBorder(18,18,18,18)));
        JLabel li = new JLabel(icon, SwingConstants.CENTER);
        li.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 42));
        JLabel lt = new JLabel(title, SwingConstants.CENTER);
        lt.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lt.setForeground(new Color(32,33,36));
        btn.add(li, BorderLayout.NORTH);
        btn.add(lt, BorderLayout.CENTER);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.addMouseListener(new MouseAdapter(){
            public void mouseEntered(MouseEvent e){ btn.setBackground(new Color(245,247,250)); btn.setBorder(new LineBorder(new Color(26,115,232),2,true)); }
            public void mouseExited(MouseEvent e){ btn.setBackground(Color.WHITE); btn.setBorder(BorderFactory.createCompoundBorder(new LineBorder(new Color(220,220,224),1,true), BorderFactory.createEmptyBorder(18,18,18,18))); }
        });
        return btn;
    }

    /** Affiche un JDialog standard avec un JPanel donné */
    private void showDialog(String title, JPanel panel, int w, int h) {
        JDialog d = new JDialog(this, title, true);
        d.setSize(w, h);
        d.setLocationRelativeTo(this);
        d.add(panel);
        d.setVisible(true);
    }

    /** Affiche le dialogue de tous les matchs */
    private void showMatchesDialog() {
        JDialog d = new JDialog(this, "Tous les matchs", true);
        d.setSize(760,420);
        d.setLocationRelativeTo(this);

        DefaultTableModel model = new DefaultTableModel(new Object[]{"Équipe A","Score","Équipe B"},0);
        JTable table = styledTable(model);
        // Ajout des matchs à la table
        for(String m : matchesPanel.getMatches()){
            int fs = m.indexOf(" ");
            int ls = m.lastIndexOf(" ");
            if(fs>0 && ls>fs){
                String a = m.substring(0,fs);
                String score = m.substring(fs+1, ls);
                String b = m.substring(ls+1);
                model.addRow(new Object[]{a, score.replace('-', ' ').replaceAll("\\s+", " - ").trim(), b});
            } else model.addRow(new Object[]{m,"",""});
        }

        d.add(new JScrollPane(table));
        d.setVisible(true);
    }

    /** Affiche le dialogue du classement */
    private void showClassementDialog() {
        JDialog d = new JDialog(this, "Classement", true);
        d.setSize(800,460);
        d.setLocationRelativeTo(this);

        ArrayList<TeamStat> stats = new ArrayList<>();
        // Création des objets TeamStat pour chaque équipe
        for(String t : teamsPanel.getTeams()) stats.add(new TeamStat(t));

        // Calcul des statistiques
        for(String match : matchesPanel.getMatches()){
            int fs = match.indexOf(" ");
            int ls = match.lastIndexOf(" ");
            if(fs<=0 || ls<=fs) continue; // Match non formaté
            String a = match.substring(0,fs);
            String scorePart = match.substring(fs+1,ls);
            String b = match.substring(ls+1);
            String[] sp = scorePart.split("-");
            if(sp.length!=2) continue; // Score non valide
            int sa = 0, sb = 0;
            try {
                sa = Integer.parseInt(sp[0]);
                sb = Integer.parseInt(sp[1]);
            } catch (NumberFormatException e) { continue; } // Score non numérique

            TeamStat A = stats.stream().filter(s->s.name.equals(a)).findFirst().orElse(null);
            TeamStat B = stats.stream().filter(s->s.name.equals(b)).findFirst().orElse(null);
            if(A==null || B==null) continue;

            A.matches++; B.matches++;
            A.goalsFor += sa; A.goalsAgainst += sb;
            B.goalsFor += sb; B.goalsAgainst += sa;
            if(sa>sb) A.pts+=3; else if(sb>sa) B.pts+=3; else {A.pts++;B.pts++;}
        }

        // Tri du classement : 1. Points, 2. Différence de buts, 3. Buts marqués (goalsFor)
        stats.sort((x,y)-> {
            if(y.pts!=x.pts) return y.pts-x.pts;
            if(y.getGoalDiff()!=x.getGoalDiff()) return y.getGoalDiff()-x.getGoalDiff();
            return y.goalsFor - x.goalsFor;
        });

        // Création et remplissage du modèle de table
        DefaultTableModel model = new DefaultTableModel(new Object[]{"Pos","Équipe","Pts","J","BM","BE","Diff"},0);
        int pos=1;
        for(TeamStat s: stats){
            model.addRow(new Object[]{pos++, s.name, s.pts, s.matches, s.goalsFor, s.goalsAgainst, (s.getGoalDiff()>0?"+":"")+s.getGoalDiff()});
        }
        JTable table = styledTable(model);
        d.add(new JScrollPane(table));
        d.setVisible(true);
    }

    /** Applique un style uniforme aux JTable */
    private JTable styledTable(DefaultTableModel model){
        JTable table = new JTable(model);
        table.setRowHeight(36);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        table.getTableHeader().setBackground(new Color(26,115,232));
        table.getTableHeader().setForeground(Color.black);
        table.setGridColor(new Color(220,220,224));
        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(JLabel.CENTER);
        for(int i=0;i<table.getColumnCount();i++) table.getColumnModel().getColumn(i).setCellRenderer(center);
        return table;
    }

    public static void main(String[] args){
        SwingUtilities.invokeLater(() -> new AppFoot().setVisible(true));
    }
}