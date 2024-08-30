//    uniCenta oPOS  - Touch Friendly Point Of Sale
//    Copyright © 2009-2020 uniCenta
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
package com.openbravo.pos.payment;

import com.openbravo.editor.JEditorCurrencyPositive;
import com.openbravo.format.Formats;
import com.openbravo.pos.customers.CustomerInfoExt;
import com.openbravo.pos.forms.AppLocal;
import com.openbravo.pos.util.RoundUtils;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import static com.openbravo.pos.payment.JPaymentSelect.nombrePagoCliente;
import static com.openbravo.pos.sales.JPanelTicket.quitarFormatoMoneda;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;

/**
 *
 * @author adrianromero
 */
public class JPaymentPlanSepare extends javax.swing.JPanel implements JPaymentInterface {

    private JPaymentNotifier notifier;
    private CustomerInfoExt customerext;
    private double m_dPaid;
    private double m_dTotal;
    public static String valorTxtDinero;
    private static final String NUMERO_CERO = "0";
    private static final String NUMERO_UNO = "1";
    private static final String NUMERO_DOS = "2";
    private static final String NUMERO_TRES = "3";
    private static final String NUMERO_CUATRO = "4";
    private static final String NUMERO_CINCO = "5";
    private static final String NUMERO_SEIS = "6";
    private static final String NUMERO_SIETE = "7";
    private static final String NUMERO_OCHO = "8";
    private static final String NUMERO_NUEVE = "9";
    private static final String NUMERO_CE = "";

    /**
     * Creates new form JPaymentDebt
     *
     * @param notifier
     */
    public JPaymentPlanSepare(JPaymentNotifier notifier) {

        // Método para devolver el tipo de pago según la opción seleccionada
        this.notifier = notifier;
        initComponents();
        m_jKeys.setVisible(false);
        m_jTendered.setVisible(false);
        m_jTendered.addEditorKeys(m_jKeys);
        txtDinero.setEditable(false);
        String[] opciones = {"Efectivo", "Tarjeta", "Banco", "Daviplata", "Nequi"};
        DefaultComboBoxModel<String> modelo = new DefaultComboBoxModel<>(opciones);
        comboBoxPago.setModel(modelo);
        comboBoxPago.setSelectedItem("Efectivo");
        nombrePagoCliente =obtenerTipoPago();
        comboBoxPago.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
               nombrePagoCliente =obtenerTipoPago();
            }
        });


    }

    public JPaymentPlanSepare() {
    }
    
    public String obtenerTipoPago() {
        String seleccion = (String) comboBoxPago.getSelectedItem();
        switch (seleccion) {
            case "Efectivo":
                return "cash";
            case "Tarjeta":
                return "ccard";
            case "Banco":
                return "bank";
            case "Daviplata":
                return "cheque";
            case "Nequi":
                return "slip";
            default:
                return ""; // Manejo de error si la opción seleccionada no coincide con ninguna de las esperadas
        }
    }

    private void agregarDigitoFormato(String c) {
        String quitarFormato = quitarFormatoMoneda(txtDinero.getText());
        if (esNumeroValido(quitarFormato)) {
            // Obtener el valor numérico subyacente
            int valorNumerico = Integer.parseInt(quitarFormato.replace(",", ""));
            if (quitarFormato.length() == 1 && quitarFormato.equals("0")) {
                quitarFormato = c;
            } else {
                valorNumerico = valorNumerico * 10 + Integer.parseInt(c); // Agregar el dígito ingresado
                quitarFormato = Formats.CURRENCY.formatValue(valorNumerico);
            }
            txtDinero.setText(quitarFormato);
            valorTxtDinero = quitarFormato;
        }
    }

    private void eliminarDigitoFormato() {
        String quitarFormato = quitarFormatoMoneda(txtDinero.getText());
        if (!quitarFormato.isEmpty()) {
            int valorNumerico = Integer.parseInt(quitarFormato.replace(",", ""));
            valorNumerico /= 10;
            quitarFormato = Formats.CURRENCY.formatValue(valorNumerico);
            txtDinero.setText(quitarFormato);
            valorTxtDinero = quitarFormato;
        }
    }

    private boolean esNumeroValido(String texto) {
        try {
            Integer.parseInt(texto.replace(",", ""));
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     *
     * @param customerext
     * @param dTotal
     * @param transID
     */
    @Override
    public void activate(CustomerInfoExt customerext, double dTotal, String transID) {

        this.customerext = customerext;
        m_dTotal = dTotal;

        m_jTendered.reset();

        if (customerext == null) {
            m_jName.setText(null);
            m_jNotes.setText(null);
            m_jKeys.setEnabled(false);
            m_jTendered.setEnabled(false);

        } else {
            m_jName.setText(customerext.getName());
            m_jNotes.setText(customerext.getNotes());

            if (RoundUtils.compare(RoundUtils.getValue(customerext.getAccdebt()), RoundUtils.getValue(customerext.getMaxdebt())) >= 0) {
                m_jKeys.setEnabled(false);
                m_jTendered.setEnabled(false);
            } else {
                m_jKeys.setEnabled(true);
                m_jTendered.setEnabled(true);
                m_jTendered.activate();
            }
        }

        printState();

    }

    /**
     *
     * @return
     */
    @Override
    public PaymentInfo executePayment() {
        return new PaymentInfoTicket(m_dPaid, "debt1");
    }

    /**
     *
     * @return
     */
    @Override
    public Component getComponent() {
        return this;
    }

    private void printState() {

        if (customerext == null) {

            txtDinero.setText(null);
            valorTxtDinero = "";
            jlblMessage.setText(AppLocal.getIntString("message.nocustomernodebt1"));
            notifier.setStatus(false, false);
        } else {
            Double value = m_jTendered.getDoubleValue();
            if (value == null || value == 0.0) {
                m_dPaid = m_dTotal;
            } else {
                m_dPaid = value;
            }

            txtDinero.setText(Formats.CURRENCY.formatValue(m_dPaid));
            valorTxtDinero = Formats.CURRENCY.formatValue(m_dPaid);

            jlblMessage.setText(null);
            int iCompare = RoundUtils.compare(m_dPaid, m_dTotal);
            // if iCompare > 0 then the payment is not valid
            notifier.setStatus(m_dPaid > 0.0 && iCompare <= 0, iCompare == 0);

        }
    }

    private class RecalculateState implements PropertyChangeListener {

        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            printState();
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel5 = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        m_jName = new javax.swing.JTextField();
        jLabel12 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        m_jNotes = new javax.swing.JTextArea();
        jlblMessage = new javax.swing.JTextArea();
        txtDinero = new javax.swing.JTextField();
        jPanel7 = new javax.swing.JPanel();
        btnCE = new javax.swing.JButton();
        btn0 = new javax.swing.JButton();
        btn7 = new javax.swing.JButton();
        btn1 = new javax.swing.JButton();
        btn4 = new javax.swing.JButton();
        btn8 = new javax.swing.JButton();
        btn5 = new javax.swing.JButton();
        btn2 = new javax.swing.JButton();
        btn3 = new javax.swing.JButton();
        btn6 = new javax.swing.JButton();
        btn9 = new javax.swing.JButton();
        btnCE1 = new javax.swing.JButton();
        comboBoxPago = new javax.swing.JComboBox<>();
        jPanel6 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        m_jKeys = new com.openbravo.editor.JEditorKeys();
        jPanel3 = new javax.swing.JPanel();
        m_jTendered = new com.openbravo.editor.JEditorCurrencyPositive();

        setLayout(new java.awt.BorderLayout());

        jPanel5.setLayout(new java.awt.BorderLayout());

        jPanel4.setBackground(new java.awt.Color(255, 255, 255));

        jLabel8.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel8.setText(AppLocal.getIntString("label.debt")); // NOI18N
        jLabel8.setPreferredSize(new java.awt.Dimension(100, 30));

        jLabel3.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel3.setText(AppLocal.getIntString("label.name")); // NOI18N
        jLabel3.setPreferredSize(new java.awt.Dimension(100, 30));

        m_jName.setEditable(false);
        m_jName.setBackground(new java.awt.Color(255, 255, 255));
        m_jName.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        m_jName.setPreferredSize(new java.awt.Dimension(170, 30));

        jLabel12.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel12.setText(AppLocal.getIntString("label.notes")); // NOI18N
        jLabel12.setPreferredSize(new java.awt.Dimension(100, 30));

        m_jNotes.setEditable(false);
        m_jNotes.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        m_jNotes.setBorder(null);
        m_jNotes.setEnabled(false);
        m_jNotes.setPreferredSize(new java.awt.Dimension(170, 80));
        jScrollPane1.setViewportView(m_jNotes);

        jlblMessage.setEditable(false);
        jlblMessage.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jlblMessage.setForeground(new java.awt.Color(204, 0, 102));
        jlblMessage.setLineWrap(true);
        jlblMessage.setWrapStyleWord(true);
        jlblMessage.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jlblMessage.setFocusable(false);
        jlblMessage.setPreferredSize(new java.awt.Dimension(275, 60));
        jlblMessage.setRequestFocusEnabled(false);

        txtDinero.setBackground(new java.awt.Color(102, 204, 255));
        txtDinero.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N

        jPanel7.setBackground(new java.awt.Color(51, 204, 255));
        jPanel7.setLayout(null);

        btnCE.setBackground(new java.awt.Color(255, 255, 255));
        btnCE.setFont(new java.awt.Font("Tahoma", 1, 30)); // NOI18N
        btnCE.setText("CE");
        btnCE.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCEActionPerformed(evt);
            }
        });
        jPanel7.add(btnCE);
        btnCE.setBounds(105, 220, 73, 40);

        btn0.setBackground(new java.awt.Color(255, 255, 255));
        btn0.setFont(new java.awt.Font("Tahoma", 1, 33)); // NOI18N
        btn0.setText("0");
        btn0.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn0ActionPerformed(evt);
            }
        });
        jPanel7.add(btn0);
        btn0.setBounds(20, 220, 73, 40);

        btn7.setBackground(new java.awt.Color(255, 255, 255));
        btn7.setFont(new java.awt.Font("Tahoma", 1, 33)); // NOI18N
        btn7.setText("7");
        btn7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn7ActionPerformed(evt);
            }
        });
        jPanel7.add(btn7);
        btn7.setBounds(20, 170, 73, 40);

        btn1.setBackground(new java.awt.Color(255, 255, 255));
        btn1.setFont(new java.awt.Font("Tahoma", 1, 33)); // NOI18N
        btn1.setText("1");
        btn1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn1ActionPerformed(evt);
            }
        });
        jPanel7.add(btn1);
        btn1.setBounds(20, 80, 73, 40);

        btn4.setBackground(new java.awt.Color(255, 255, 255));
        btn4.setFont(new java.awt.Font("Tahoma", 1, 33)); // NOI18N
        btn4.setText("4");
        btn4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn4ActionPerformed(evt);
            }
        });
        jPanel7.add(btn4);
        btn4.setBounds(20, 125, 73, 40);

        btn8.setBackground(new java.awt.Color(255, 255, 255));
        btn8.setFont(new java.awt.Font("Tahoma", 1, 33)); // NOI18N
        btn8.setText("8");
        btn8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn8ActionPerformed(evt);
            }
        });
        jPanel7.add(btn8);
        btn8.setBounds(105, 170, 73, 40);

        btn5.setBackground(new java.awt.Color(255, 255, 255));
        btn5.setFont(new java.awt.Font("Tahoma", 1, 33)); // NOI18N
        btn5.setText("5");
        btn5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn5ActionPerformed(evt);
            }
        });
        jPanel7.add(btn5);
        btn5.setBounds(105, 125, 73, 40);

        btn2.setBackground(new java.awt.Color(255, 255, 255));
        btn2.setFont(new java.awt.Font("Tahoma", 1, 33)); // NOI18N
        btn2.setText("2");
        btn2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn2ActionPerformed(evt);
            }
        });
        jPanel7.add(btn2);
        btn2.setBounds(105, 80, 73, 40);

        btn3.setBackground(new java.awt.Color(255, 255, 255));
        btn3.setFont(new java.awt.Font("Tahoma", 1, 33)); // NOI18N
        btn3.setText("3");
        btn3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn3ActionPerformed(evt);
            }
        });
        jPanel7.add(btn3);
        btn3.setBounds(190, 80, 73, 40);

        btn6.setBackground(new java.awt.Color(255, 255, 255));
        btn6.setFont(new java.awt.Font("Tahoma", 1, 33)); // NOI18N
        btn6.setText("6");
        btn6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn6ActionPerformed(evt);
            }
        });
        jPanel7.add(btn6);
        btn6.setBounds(190, 125, 73, 40);

        btn9.setBackground(new java.awt.Color(255, 255, 255));
        btn9.setFont(new java.awt.Font("Tahoma", 1, 33)); // NOI18N
        btn9.setText("9");
        btn9.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn9ActionPerformed(evt);
            }
        });
        jPanel7.add(btn9);
        btn9.setBounds(190, 170, 73, 40);

        btnCE1.setBackground(new java.awt.Color(255, 255, 255));
        btnCE1.setFont(new java.awt.Font("Tahoma", 1, 36)); // NOI18N
        btnCE1.setForeground(new java.awt.Color(204, 0, 0));
        btnCE1.setText("-");
        btnCE1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCE1ActionPerformed(evt);
            }
        });
        jPanel7.add(btnCE1);
        btnCE1.setBounds(190, 220, 73, 40);

        comboBoxPago.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        comboBoxPago.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        jPanel7.add(comboBoxPago);
        comboBoxPago.setBounds(20, 30, 240, 40);

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel4Layout.createSequentialGroup()
                            .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(txtDinero))
                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel4Layout.createSequentialGroup()
                            .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(m_jName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(jlblMessage, javax.swing.GroupLayout.PREFERRED_SIZE, 274, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, 280, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(280, 280, 280))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addGap(6, 6, 6)
                                .addComponent(txtDinero)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 11, Short.MAX_VALUE)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(m_jName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(32, 32, 32)
                        .addComponent(jlblMessage, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(101, 101, 101))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, 280, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
        );

        jPanel5.add(jPanel4, java.awt.BorderLayout.CENTER);

        jPanel6.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());
        jPanel5.add(jPanel6, java.awt.BorderLayout.PAGE_END);

        add(jPanel5, java.awt.BorderLayout.CENTER);

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));
        jPanel2.setLayout(new java.awt.BorderLayout());

        jPanel1.setLayout(null);

        m_jKeys.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                m_jKeysActionPerformed(evt);
            }
        });
        jPanel1.add(m_jKeys);
        m_jKeys.setBounds(0, 0, 300, 300);

        jPanel3.setBackground(new java.awt.Color(255, 255, 255));
        jPanel3.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 5, 5, 5));
        jPanel3.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N

        m_jTendered.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        m_jTendered.setPreferredSize(new java.awt.Dimension(200, 30));

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(40, 40, 40)
                .addComponent(m_jTendered, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(50, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(m_jTendered, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        jPanel1.add(jPanel3);
        jPanel3.setBounds(0, 300, 300, 40);

        jPanel2.add(jPanel1, java.awt.BorderLayout.NORTH);

        add(jPanel2, java.awt.BorderLayout.EAST);
    }// </editor-fold>//GEN-END:initComponents

    private void m_jKeysActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_m_jKeysActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_m_jKeysActionPerformed

    private void btnCEActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCEActionPerformed
        txtDinero.setText("0");
        valorTxtDinero = "0";

    }//GEN-LAST:event_btnCEActionPerformed

    private void btn0ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn0ActionPerformed
        agregarDigitoFormato(NUMERO_CERO);
    }//GEN-LAST:event_btn0ActionPerformed

    private void btn7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn7ActionPerformed
        agregarDigitoFormato(NUMERO_SIETE);
    }//GEN-LAST:event_btn7ActionPerformed

    private void btn1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn1ActionPerformed
        agregarDigitoFormato(NUMERO_UNO);
    }//GEN-LAST:event_btn1ActionPerformed

    private void btn4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn4ActionPerformed
        agregarDigitoFormato(NUMERO_CUATRO);
    }//GEN-LAST:event_btn4ActionPerformed

    private void btn8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn8ActionPerformed
        agregarDigitoFormato(NUMERO_OCHO);
    }//GEN-LAST:event_btn8ActionPerformed

    private void btn5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn5ActionPerformed
        agregarDigitoFormato(NUMERO_CINCO);
    }//GEN-LAST:event_btn5ActionPerformed

    private void btn2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn2ActionPerformed
        agregarDigitoFormato(NUMERO_DOS);
    }//GEN-LAST:event_btn2ActionPerformed

    private void btn3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn3ActionPerformed
        agregarDigitoFormato(NUMERO_TRES);
    }//GEN-LAST:event_btn3ActionPerformed

    private void btn6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn6ActionPerformed
        agregarDigitoFormato(NUMERO_SEIS);
    }//GEN-LAST:event_btn6ActionPerformed

    private void btn9ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn9ActionPerformed
        agregarDigitoFormato(NUMERO_NUEVE);
    }//GEN-LAST:event_btn9ActionPerformed

    private void btnCE1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCE1ActionPerformed
        eliminarDigitoFormato();
    }//GEN-LAST:event_btnCE1ActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btn0;
    private javax.swing.JButton btn1;
    private javax.swing.JButton btn2;
    private javax.swing.JButton btn3;
    private javax.swing.JButton btn4;
    private javax.swing.JButton btn5;
    private javax.swing.JButton btn6;
    private javax.swing.JButton btn7;
    private javax.swing.JButton btn8;
    private javax.swing.JButton btn9;
    private javax.swing.JButton btnCE;
    private javax.swing.JButton btnCE1;
    private javax.swing.JComboBox<String> comboBoxPago;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextArea jlblMessage;
    private com.openbravo.editor.JEditorKeys m_jKeys;
    private javax.swing.JTextField m_jName;
    private javax.swing.JTextArea m_jNotes;
    private com.openbravo.editor.JEditorCurrencyPositive m_jTendered;
    private javax.swing.JTextField txtDinero;
    // End of variables declaration//GEN-END:variables
}
