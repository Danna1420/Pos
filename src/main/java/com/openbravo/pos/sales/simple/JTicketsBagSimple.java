//    uniCenta oPOS  - Touch Friendly Point Of Sale
//    Copyright © 2009-2020 uniCenta & previous Openbravo POS works
//    https://unicenta.com
//
//    This file is part of uniCenta oPOS
//
//    uniCenta oPOS is free software: you can redistribute it and/or modify
//    it under the terms of the GNU General Public License as published by
//    the Free Software Foundation, either version 3 of the License, or
//    (at your option) any later version.
//
//   uniCenta oPOS is distributed in the hope that it will be useful,
//    but WITHOUT ANY WARRANTY; without even the implied warranty of
//    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//    GNU General Public License for more details.
//
//    You should have received a copy of the GNU General Public License
//    along with uniCenta oPOS.  If not, see <http://www.gnu.org/licenses/>.

package com.openbravo.pos.sales.simple;

import com.openbravo.beans.JNumberPop;

import javax.swing.*;

import com.openbravo.pos.forms.AppLocal;
import com.openbravo.pos.forms.AppView;
import com.openbravo.pos.sales.*;
import com.openbravo.pos.ticket.TicketInfo;

/**
 *
 * @author JG uniCenta
 */
public class JTicketsBagSimple extends JTicketsBag {
    
    /** Creates new form JTicketsBagSimple
     * @param app
     * @param panelticket */
    public JTicketsBagSimple(AppView app, TicketsEditor panelticket) {
        
        super(app, panelticket);
        
        initComponents();
    }
    
    /**
     *
     */
    @Override
    public void activate() {
        
        m_panelticket.setActiveTicket(new TicketInfo(), null);
        
        // Authorization
        m_jDelTicket.setEnabled(m_App.getAppUserView().getUser().hasPermission("com.openbravo.pos.sales.JPanelTicketEdits"));

    }

    /**
     *
     * @return
     */
    @Override
    public boolean deactivate() {
        m_panelticket.setActiveTicket(null, null);      
        return true;
    }
    
    /**
     *
     */
    @Override
    public void deleteTicket() {           
        m_panelticket.setActiveTicket(new TicketInfo(), null);
    }
    
    /**
     *
     * @return
     */
    @Override
    protected JComponent getBagComponent() {
        return this;
    }

    /**
     *
     * @return
     */
    @Override
    protected JComponent getNullComponent() {
        return new JPanel();
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        m_jDelTicket = new javax.swing.JButton();

        setBackground(new java.awt.Color(255, 255, 255));
        setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        m_jDelTicket.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/openbravo/images/sale_delete.png"))); // NOI18N
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("pos_messages"); // NOI18N
        m_jDelTicket.setToolTipText(bundle.getString("tooltip.delete")); // NOI18N
        m_jDelTicket.setFocusPainted(false);
        m_jDelTicket.setFocusable(false);
        m_jDelTicket.setMargin(new java.awt.Insets(0, 4, 0, 4));
        m_jDelTicket.setMaximumSize(new java.awt.Dimension(50, 40));
        m_jDelTicket.setMinimumSize(new java.awt.Dimension(50, 40));
        m_jDelTicket.setPreferredSize(new java.awt.Dimension(80, 45));
        m_jDelTicket.setRequestFocusEnabled(false);
        m_jDelTicket.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                m_jDelTicketActionPerformed(evt);
            }
        });
        add(m_jDelTicket);
    }// </editor-fold>//GEN-END:initComponents

    private void m_jDelTicketActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_m_jDelTicketActionPerformed
        boolean pinOK = false;

        if (m_App.getProperties().getProperty("override.check").equals("true")) {
            Integer secret = Integer.parseInt(m_App.getProperties().getProperty("override.pin"));
            Integer iValue = JNumberPop.showEditNumber(this, AppLocal.getIntString("title.override.enterpin"));

            if (iValue == null ? secret == null : iValue.equals(secret)) {
                pinOK = true;
                int res = JOptionPane.showConfirmDialog(this
                        , AppLocal.getIntString("message.wannadelete")
                        , AppLocal.getIntString("title.editor")
                        , JOptionPane.YES_NO_OPTION
                        , JOptionPane.QUESTION_MESSAGE);

                if (res == JOptionPane.YES_OPTION) {
                    deleteTicket();
                }
            } else {
                pinOK = false;
                JOptionPane.showMessageDialog(this, AppLocal.getIntString("message.override.badpin"));
            }
        } else {
            int res = JOptionPane.showConfirmDialog(this
                    , AppLocal.getIntString("message.wannadelete")
                    , AppLocal.getIntString("title.editor")
                    , JOptionPane.YES_NO_OPTION
                    , JOptionPane.QUESTION_MESSAGE);

            if (res == JOptionPane.YES_OPTION) {
                deleteTicket();
            }
        }
        
    }//GEN-LAST:event_m_jDelTicketActionPerformed
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton m_jDelTicket;
    // End of variables declaration//GEN-END:variables
    
}
