import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

class MatchesPanel extends JPanel {
    private TeamsPanel teamsPanel;
    private ArrayList<String> matches = new ArrayList<>();
    private DefaultTableModel model;
    private JTable table;
    private JComboBox<String> cbA, cbB;

    public MatchesPanel(TeamsPanel tp){
        this.teamsPanel = tp;
        setLayout(new BorderLayout(12,12));
        setBorder(BorderFactory.createEmptyBorder(12,12,12,12));
        setBackground(new Color(248,249,250));

        JLabel title = new JLabel("Gestion des matchs");
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        add(title, BorderLayout.NORTH);

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(Color.WHITE);
        form.setBorder(BorderFactory.createCompoundBorder(new LineBorder(new Color(39, 6, 7),1,true), BorderFactory.createEmptyBorder(12,12,12,12)));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8,10,8,10); gbc.fill = GridBagConstraints.HORIZONTAL;

        // Équipe A
        gbc.gridx=0; gbc.gridy=0; form.add(new JLabel("Équipe A:"), gbc);
        cbA = new JComboBox<>(); cbA.setPreferredSize(new Dimension(220,36)); gbc.gridx=1; form.add(cbA, gbc);
        // Score A
        gbc.gridx=2; form.add(new JLabel("Score:"), gbc);
        JTextField sA = new JTextField(3); sA.setHorizontalAlignment(JTextField.CENTER); sA.setPreferredSize(new Dimension(60,36)); gbc.gridx=3; form.add(sA, gbc);
        JLabel vs = new JLabel(" - ", SwingConstants.CENTER); vs.setFont(new Font("Segoe UI", Font.BOLD,14)); gbc.gridx=4; form.add(vs, gbc);
        // Score B
        JTextField sB = new JTextField(3); sB.setHorizontalAlignment(JTextField.CENTER); sB.setPreferredSize(new Dimension(60,36)); gbc.gridx=5; form.add(sB, gbc);
        // Équipe B
        gbc.gridx=6; form.add(new JLabel("Équipe B:"), gbc);
        cbB = new JComboBox<>(); cbB.setPreferredSize(new Dimension(220,36)); gbc.gridx=7; form.add(cbB, gbc);

        // Boutons d'action
        gbc.gridx=0; gbc.gridy=1; gbc.gridwidth=8;
        JPanel btns = new JPanel(new FlowLayout(FlowLayout.CENTER,14,6));

        JButton add = pill("➕ Ajouter", new Color(12, 141, 210));
        JButton mod = pill("✏ Modifier", new Color(10, 234, 45));
        JButton del = pill("🗑 Supprimer", new Color(233, 9, 21));
        btns.add(add); btns.add(mod); btns.add(del);
        form.add(btns, gbc);

        add(form, BorderLayout.CENTER);

        // Tableau des matchs
        model = new DefaultTableModel(new Object[]{"Équipe A","Score","Équipe B"},0){
            public boolean isCellEditable(int r,int c){return false;}
        };
        table = new JTable(model);
        table.setRowHeight(36);

        JScrollPane sp = new JScrollPane(table);
        sp.setPreferredSize(new Dimension(0,320));
        add(sp, BorderLayout.SOUTH);

        // Listeners pour s'assurer que l'équipe A et B sont différentes
        cbA.addItemListener(e-> { if(e.getStateChange()==ItemEvent.SELECTED) ensureDifferent(cbA, cbB); });
        cbB.addItemListener(e-> { if(e.getStateChange()==ItemEvent.SELECTED) ensureDifferent(cbB, cbA); });

        // Action Ajouter
        add.addActionListener(e-> {
            String a = (String)cbA.getSelectedItem(), b = (String)cbB.getSelectedItem();
            if(a==null||b==null){ JOptionPane.showMessageDialog(this,"Ajoutez d'abord des équipes"); return; }
            if(a.equals(b)){ JOptionPane.showMessageDialog(this,"Les équipes doivent être différentes"); return; }
            String sa = sA.getText().trim(), sb = sB.getText().trim();
            if(sa.isEmpty()||sb.isEmpty()){ JOptionPane.showMessageDialog(this,"Entrez les scores"); return; }
            try{ int ia=Integer.parseInt(sa), ib=Integer.parseInt(sb); if(ia<0||ib<0) throw new NumberFormatException(); } catch(Exception ex){ JOptionPane.showMessageDialog(this,"Scores entiers >=0"); return; }
            String m = a + " " + sa + "-" + sb + " " + b;
            matches.add(m); model.addRow(new Object[]{a, sa + " - " + sb, b});
            sA.setText(""); sB.setText("");
        });

        // Action Modifier
        mod.addActionListener(e-> {
            int r = table.getSelectedRow(); if(r==-1){ JOptionPane.showMessageDialog(this,"Sélectionnez un match"); return; }
            String a = (String)cbA.getSelectedItem(), b = (String)cbB.getSelectedItem();
            if(a.equals(b)){ JOptionPane.showMessageDialog(this,"Les équipes doivent être différentes"); return; }
            String sa = sA.getText().trim(), sb = sB.getText().trim();
            if(sa.isEmpty()||sb.isEmpty()){ JOptionPane.showMessageDialog(this,"Entrez les scores"); return; }
            try{ int ia=Integer.parseInt(sa), ib=Integer.parseInt(sb); if(ia<0||ib<0) throw new NumberFormatException(); } catch(Exception ex){ JOptionPane.showMessageDialog(this,"Scores entiers >=0"); return; }
            String m = a + " " + sa + "-" + sb + " " + b;
            matches.set(r, m); model.setValueAt(a,r,0); model.setValueAt(sa + " - " + sb,r,1); model.setValueAt(b,r,2);
        });

        // Action Supprimer
        del.addActionListener(e-> {
            int r = table.getSelectedRow(); if(r==-1){ JOptionPane.showMessageDialog(this,"Sélectionnez un match"); return; }
            if(JOptionPane.showConfirmDialog(this,"Supprimer ce match ?", "Confirmer", JOptionPane.YES_NO_OPTION)==JOptionPane.YES_OPTION){
                matches.remove(r); model.removeRow(r);
            }
        });

        // Mise à jour des champs lors de la sélection d'une ligne
        table.getSelectionModel().addListSelectionListener(e-> {
            if(!e.getValueIsAdjusting() && table.getSelectedRow()!=-1){
                int r = table.getSelectedRow();
                cbA.setSelectedItem(model.getValueAt(r,0).toString());
                cbB.setSelectedItem(model.getValueAt(r,2).toString());
                String[] sc = model.getValueAt(r,1).toString().split(" - ");
                // Les indices 3 et 5 correspondent aux JTextField sA et sB dans le GridBagLayout
                if(sc.length==2){ ((JTextField)form.getComponent(3)).setText(sc[0].trim()); ((JTextField)form.getComponent(5)).setText(sc[1].trim()); }
            }
        });

        updateTeams();
    }

    /** Crée un bouton de style 'pill' */
    private JButton pill(String text, Color bg){
        JButton b = new JButton(text);
        b.setBackground(bg);
        b.setForeground(Color.BLACK);
        b.setOpaque(true);
        b.setBorderPainted(false);
        b.setFocusPainted(false);
        b.setPreferredSize(new Dimension(140,36));
        return b;
    }

    /** S'assure que les deux ComboBox n'ont pas la même équipe sélectionnée */
    private void ensureDifferent(JComboBox<String> src, JComboBox<String> tgt){
        Object s = src.getSelectedItem();
        if(s==null) return;
        Object t = tgt.getSelectedItem();
        if(s.equals(t)){
            for(int i=0;i<tgt.getItemCount();i++){
                if(!tgt.getItemAt(i).equals(s)){ tgt.setSelectedIndex(i); return; }
            }
        }
    }

    /** Met à jour les ComboBox d'équipes avec la liste actuelle du TeamsPanel */
    public void updateTeams(){
        cbA.removeAllItems(); cbB.removeAllItems();
        for(String t : teamsPanel.getTeams()){ cbA.addItem(t); cbB.addItem(t); }
        // Sélectionne les deux premières équipes différentes par défaut
        if(cbA.getItemCount()>=2){ cbA.setSelectedIndex(0); cbB.setSelectedIndex(1); }
        else if(cbA.getItemCount()==1){ cbA.setSelectedIndex(0); cbB.setSelectedIndex(0); }
    }

    /** Méthode publique pour récupérer la liste des matchs */
    public ArrayList<String> getMatches(){ return matches; }
}