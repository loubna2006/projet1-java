import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

class TeamsPanel extends JPanel {
    private ArrayList<String> teams = new ArrayList<>();
    private DefaultTableModel model;
    private JTable table;

    public TeamsPanel(){
        setLayout(new BorderLayout(12,12));
        setBorder(BorderFactory.createEmptyBorder(12,12,12,12));
        setBackground(new Color(248,249,250));

        JLabel title = new JLabel("Gestion des équipes");
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setBorder(BorderFactory.createEmptyBorder(6,6,12,6));
        add(title, BorderLayout.NORTH);

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 6));
        top.setBackground(Color.white);
        top.setBorder(BorderFactory.createLineBorder(new Color(220,220,224),1,true));
        JTextField txt = new JTextField(22);
        txt.setPreferredSize(new Dimension(300,36));

        // Boutons
        JButton add = pillButton("➕ Ajouter", new Color(12, 141, 210));
        JButton mod = pillButton("✏ Modifier", new Color(23, 185, 14));
        JButton del = pillButton("🗑 Supprimer", new Color(233, 9, 21, 238));
        JButton search = pillButton("🔍 Rechercher", new Color(238, 189, 132));

        top.add(new JLabel("Nom de l'équipe:"));
        top.add(txt);
        top.add(add);
        top.add(mod);
        top.add(del);
        top.add(search);

        add(top, BorderLayout.CENTER);

        // Configuration de la JTable
        model = new DefaultTableModel(new Object[]{"ID","Nom"},0){
            public boolean isCellEditable(int r,int c){return false;}
        };
        table = new JTable(model);
        table.setRowHeight(34);
        table.getTableHeader().setBackground(new Color(65,105,225));
        table.getTableHeader().setForeground(Color.black);
        JScrollPane sp = new JScrollPane(table);
        sp.setPreferredSize(new Dimension(0,300));
        add(sp, BorderLayout.SOUTH);

        // Listeners pour les actions
        add.addActionListener(e -> {
            String name = txt.getText().trim();
            if(name.isEmpty()){ JOptionPane.showMessageDialog(this,"Entrez un nom"); return; }
            if(teams.contains(name)){ JOptionPane.showMessageDialog(this,"Déjà existant"); return; }
            teams.add(name); model.addRow(new Object[]{teams.size(), name}); txt.setText("");
        });
        mod.addActionListener(e -> {
            int r = table.getSelectedRow(); if(r==-1){ JOptionPane.showMessageDialog(this,"Sélectionnez"); return; }
            String n = txt.getText().trim(); if(n.isEmpty()){ JOptionPane.showMessageDialog(this,"Entrez un nom"); return; }
            if(teams.contains(n) && !teams.get(r).equals(n)){ JOptionPane.showMessageDialog(this,"Nom existe"); return; }
            teams.set(r,n); model.setValueAt(n,r,1); txt.setText("");
        });
        del.addActionListener(e -> {
            int r = table.getSelectedRow(); if(r==-1){ JOptionPane.showMessageDialog(this,"Sélectionnez"); return; }
            if(JOptionPane.showConfirmDialog(this,"Supprimer ?","Confirmer",JOptionPane.YES_NO_OPTION)==JOptionPane.YES_OPTION){
                teams.remove(r); model.removeRow(r);
                for(int i=0;i<model.getRowCount();i++) model.setValueAt(i+1,i,0); // Réindexation
            }
        });
        search.addActionListener(e -> {
            String q = txt.getText().trim().toLowerCase(); if(q.isEmpty()){ JOptionPane.showMessageDialog(this,"Entrez le terme"); return; }
            for(int i=0;i<model.getRowCount();i++){
                if(model.getValueAt(i,1).toString().toLowerCase().contains(q)){ table.setRowSelectionInterval(i,i); table.scrollRectToVisible(table.getCellRect(i,0,true)); return; }
            }
            JOptionPane.showMessageDialog(this,"Aucun résultat");
        });

        table.getSelectionModel().addListSelectionListener(e -> {
            if(!e.getValueIsAdjusting() && table.getSelectedRow()!=-1){
                txt.setText(model.getValueAt(table.getSelectedRow(),1).toString());
            }
        });
    }

    /** Crée un bouton de style 'pill' */
    private JButton pillButton(String text, Color bg){
        JButton b = new JButton(text);
        b.setBackground(bg);
        b.setForeground(Color.BLACK);
        b.setOpaque(true);
        b.setBorderPainted(false);
        b.setFocusPainted(false);
        b.setPreferredSize(new Dimension(120,36));
        return b;
    }

    /** Méthode publique pour récupérer la liste des équipes */
    public ArrayList<String> getTeams(){ return teams; }
}