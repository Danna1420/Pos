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

package com.openbravo.pos.config;

import com.alee.laf.WebLookAndFeel;
import com.openbravo.basic.BasicException;
import com.openbravo.pos.forms.AppConfig;
import com.openbravo.pos.forms.AppLocal;
import com.openbravo.pos.forms.AppProperties;
import com.openbravo.pos.forms.JRootFrame;
import java.awt.BorderLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import javax.imageio.ImageIO;

/**
 *
 * @author adrianromero
 */
public class JFrmConfig extends javax.swing.JFrame {
 
    private final JPanelConfiguration config;
    
    /** Creates new form JFrmConfig
     * @param props */
    public JFrmConfig(AppProperties props) {
      
        initComponents();
        
        try {
            this.setIconImage(ImageIO.read(JRootFrame.class.getResourceAsStream("/com/openbravo/images/favicon.png")));
        } catch (IOException e) {
        }   
        setTitle(AppLocal.APP_NAME + " - " + AppLocal.APP_VERSION + " - " + AppLocal.getIntString("Menu.Configuration"));
        
        addWindowListener(new MyFrameListener()); 
        
        config = new JPanelConfiguration(props);
        
        getContentPane().add(config, BorderLayout.CENTER);
       
        try {
            config.activate();
        } catch (BasicException e) { // never thrown ;-)
        }
    }
    
    private class MyFrameListener extends WindowAdapter{
        
        @Override
        public void windowClosing(WindowEvent evt) {
            if (config.deactivate()) {
                dispose();
            }
        }
        @Override
        public void windowClosed(WindowEvent evt) {
            System.exit(0);
        }
    }    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setBackground(new java.awt.Color(255, 255, 255));
        setMinimumSize(new java.awt.Dimension(950, 450));
        setPreferredSize(new java.awt.Dimension(950, 500));

        setSize(new java.awt.Dimension(808, 794));
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents
    
    /**
     * @param args the command line arguments
     */
    public static void main(final String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                
                AppConfig config = new AppConfig(args);
                config.load();    
                
                WebLookAndFeel.install ();                 
                
//                try {                    
//                    UIManager.setLookAndFeel(config.getProperty("swing.defaultlaf"));
//                } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
//                }
                
                new JFrmConfig(config).setVisible(true);
            }
        });
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
    
}
