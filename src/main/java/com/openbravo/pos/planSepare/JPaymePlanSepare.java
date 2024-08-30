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
package com.openbravo.pos.planSepare;

import com.openbravo.alerta.AlertasPersonalizadas;
import com.openbravo.basic.BasicException;
import com.openbravo.data.gui.MessageInf;
import com.openbravo.data.loader.LocalRes;
import com.openbravo.data.user.DirtyManager;
import com.openbravo.pos.payment.*;
import com.openbravo.pos.customers.CustomerInfoExt;
import com.openbravo.pos.customers.DataLogicCustomers;
import com.openbravo.pos.forms.AppLocal;
import com.openbravo.pos.forms.AppView;
import com.openbravo.pos.forms.BeanFactoryApp;
import com.openbravo.pos.forms.BeanFactoryException;
import com.openbravo.pos.forms.DataLogicSales;
import com.openbravo.pos.forms.DataLogicSystem;
import com.openbravo.pos.forms.JPanelView;
import com.openbravo.pos.printer.TicketParser;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.text.NumberFormat;
import java.util.Locale;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;

import static com.openbravo.pos.customers.CustomersPayment.idCliente;
import static com.openbravo.pos.planSepare.PlanSepares.SALDO;
import static com.openbravo.pos.planSepare.PlanSepares.VALOR_A_PAGAR;
import static com.openbravo.pos.planSepare.PlanSepares.idRegistro;
import static com.openbravo.pos.sales.JPanelTicket.abrirCajonDinero;
import static com.openbravo.pos.sales.JPanelTicket.quitarFormatoMoneda;

/**
 * @author JG uniCenta
 */
public class JPaymePlanSepare extends JDialog implements JPanelView, BeanFactoryApp {

    private AppView app;
    private DataLogicCustomers dlcustomers;
    private DataLogicSales dlsales;
    private DataLogicSystem dlsystem;
    private TicketParser ttp;

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
    private static final String NUMERO_CE = "CE";

    private CustomerInfoExt customerext;
    private DirtyManager dirty;

    private PlanSepareModelo planSepareModelo;
    private JPaymentSelect paymentdialog;

    public JPaymePlanSepare() {
        initComponents();
        txtValorRecibido.setText(formatoMonedas(Double.valueOf(VALOR_A_PAGAR)));
        txtValorCuenta.setText(formatoMonedas(Double.valueOf(SALDO)));
        planSepareModelo = new PlanSepareModelo();
        txtValorRecibido.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                if (Character.isDigit(c)) {
                    digitarNumero(String.valueOf(c));
                }
            }

            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
                    digitarNumero("borrarDigito");
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {

            }
        });

        txtValorCuenta.setEditable(false);
        txtValorRecibido.setEditable(false);
        setSize(575, 360);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        setResizable(false);
        setModal(true);
    }

    private void digitarNumero(String numero) {
        if (numero.trim().equals(NUMERO_CE)) {
            txtValorRecibido.setText("");
            return;
        }
        String textoActual;
        if (txtValorRecibido.getText().length() == 3
                && txtValorRecibido.getText().charAt(txtValorRecibido.getText().length() - 1) == '0'
                && txtValorRecibido.getText().charAt(txtValorRecibido.getText().length() - 2) == '.'
                && !numero.equals("borrarDigito")) {
            char primerDigito = txtValorRecibido.getText().charAt(0);
            txtValorRecibido.setText(String.valueOf(primerDigito).trim());
            textoActual = txtValorRecibido.getText();
        } else {
            textoActual = quitarFormatoMoneda(txtValorRecibido.getText());
        }
        if (!numero.equals("borrarDigito")) {
            String nuevoTexto = textoActual + numero;

            if (nuevoTexto.length() == Integer.parseInt(NUMERO_UNO) && nuevoTexto.equals(NUMERO_CERO)) {
                AlertasPersonalizadas.mostrarAlertaInformacion("El número cero no puede ser el primer dígito");
                return;
            }

            String formatoNumero = formatoMonedas(Double.valueOf(nuevoTexto));
            txtValorRecibido.setText(formatoNumero);
            return;
        }

        String cadena = txtValorRecibido.getText();
        String nuevoTexto = cadena.substring(0, cadena.length() - 1);
        String formatoNumero = formatoMonedas(Double.valueOf(quitarFormatoMoneda(nuevoTexto)));
        txtValorRecibido.setText(formatoNumero);

    }

    public static String formatoMonedas(double amount) {
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("es", "CO"));
        currencyFormat.setMinimumFractionDigits(0);

        String cantidadString = String.valueOf(amount);
        if (cantidadString.length() > 3) {
            return currencyFormat.format(amount);
        } else {
            return cantidadString;
        }
    }

    public void validarCampos(String nombrePago) {
        if (txtValorRecibido.getText().equals("")) {
            AlertasPersonalizadas.mostrarAlertaInformacion("Valor no valido");
            return;
        }
        String valorPagado = quitarFormatoMoneda(txtValorRecibido.getText());
        String valorCuenta = quitarFormatoMoneda(txtValorCuenta.getText());

        if (valorPagado.equals(valorCuenta)
                || Integer.parseInt(valorPagado) >= Integer.parseInt(valorCuenta)) {
            planSepareModelo.sacarProducto();
            planSepareModelo.insertarActualizar(Double.valueOf(valorPagado),
                    idCliente, true, idRegistro);
            AlertasPersonalizadas.mostrarAlertaInformacion("Pago Exitoso");
        } else {
            planSepareModelo.insertarActualizar(Double.valueOf(valorPagado),
                    idCliente, false, idRegistro);
            AlertasPersonalizadas.mostrarAlertaInformacion("Abono exitoso");
        }

        planSepareModelo.insertarReciboYPago(Double.valueOf(valorPagado), nombrePago);
        abrirCajonDinero();
        dispose();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jButton10 = new javax.swing.JButton();
        jButton11 = new javax.swing.JButton();
        jButton12 = new javax.swing.JButton();
        jPanel4 = new javax.swing.JPanel();
        txtValorCuenta = new javax.swing.JTextField();
        jPanel1 = new javax.swing.JPanel();
        txtValorRecibido = new javax.swing.JTextField();
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
        btnDaviPlata = new javax.swing.JButton();
        btnEfectivo = new javax.swing.JButton();
        btnTarjeta = new javax.swing.JButton();
        btnBanco = new javax.swing.JButton();
        btnNequi = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();

        jButton10.setText("jButton1");

        jButton11.setText("jButton1");

        jButton12.setText("jButton1");

        jPanel4.setBackground(new java.awt.Color(255, 255, 255));
        jPanel4.setLayout(null);

        txtValorCuenta.setBackground(new java.awt.Color(51, 204, 255));
        txtValorCuenta.setFont(new java.awt.Font("Tahoma", 1, 33)); // NOI18N
        jPanel4.add(txtValorCuenta);
        txtValorCuenta.setBounds(10, 40, 250, 30);

        jPanel1.setBackground(new java.awt.Color(51, 204, 255));
        jPanel1.setLayout(null);

        txtValorRecibido.setFont(new java.awt.Font("Tahoma", 1, 33)); // NOI18N
        txtValorRecibido.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtValorRecibidoActionPerformed(evt);
            }
        });
        jPanel1.add(txtValorRecibido);
        txtValorRecibido.setBounds(20, 30, 240, 40);

        btnCE.setBackground(new java.awt.Color(255, 255, 255));
        btnCE.setFont(new java.awt.Font("Tahoma", 1, 33)); // NOI18N
        btnCE.setText("CE");
        btnCE.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCEActionPerformed(evt);
            }
        });
        jPanel1.add(btnCE);
        btnCE.setBounds(150, 220, 116, 40);

        btn0.setBackground(new java.awt.Color(255, 255, 255));
        btn0.setFont(new java.awt.Font("Tahoma", 1, 33)); // NOI18N
        btn0.setText("0");
        btn0.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn0ActionPerformed(evt);
            }
        });
        jPanel1.add(btn0);
        btn0.setBounds(20, 220, 116, 40);

        btn7.setBackground(new java.awt.Color(255, 255, 255));
        btn7.setFont(new java.awt.Font("Tahoma", 1, 33)); // NOI18N
        btn7.setText("7");
        btn7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn7ActionPerformed(evt);
            }
        });
        jPanel1.add(btn7);
        btn7.setBounds(20, 170, 73, 40);

        btn1.setBackground(new java.awt.Color(255, 255, 255));
        btn1.setFont(new java.awt.Font("Tahoma", 1, 33)); // NOI18N
        btn1.setText("1");
        btn1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn1ActionPerformed(evt);
            }
        });
        jPanel1.add(btn1);
        btn1.setBounds(20, 80, 73, 40);

        btn4.setBackground(new java.awt.Color(255, 255, 255));
        btn4.setFont(new java.awt.Font("Tahoma", 1, 33)); // NOI18N
        btn4.setText("4");
        btn4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn4ActionPerformed(evt);
            }
        });
        jPanel1.add(btn4);
        btn4.setBounds(20, 125, 73, 40);

        btn8.setBackground(new java.awt.Color(255, 255, 255));
        btn8.setFont(new java.awt.Font("Tahoma", 1, 33)); // NOI18N
        btn8.setText("8");
        btn8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn8ActionPerformed(evt);
            }
        });
        jPanel1.add(btn8);
        btn8.setBounds(105, 170, 73, 40);

        btn5.setBackground(new java.awt.Color(255, 255, 255));
        btn5.setFont(new java.awt.Font("Tahoma", 1, 33)); // NOI18N
        btn5.setText("5");
        btn5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn5ActionPerformed(evt);
            }
        });
        jPanel1.add(btn5);
        btn5.setBounds(105, 125, 73, 40);

        btn2.setBackground(new java.awt.Color(255, 255, 255));
        btn2.setFont(new java.awt.Font("Tahoma", 1, 33)); // NOI18N
        btn2.setText("2");
        btn2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn2ActionPerformed(evt);
            }
        });
        jPanel1.add(btn2);
        btn2.setBounds(105, 80, 73, 40);

        btn3.setBackground(new java.awt.Color(255, 255, 255));
        btn3.setFont(new java.awt.Font("Tahoma", 1, 33)); // NOI18N
        btn3.setText("3");
        btn3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn3ActionPerformed(evt);
            }
        });
        jPanel1.add(btn3);
        btn3.setBounds(190, 80, 73, 40);

        btn6.setBackground(new java.awt.Color(255, 255, 255));
        btn6.setFont(new java.awt.Font("Tahoma", 1, 33)); // NOI18N
        btn6.setText("6");
        btn6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn6ActionPerformed(evt);
            }
        });
        jPanel1.add(btn6);
        btn6.setBounds(190, 125, 73, 40);

        btn9.setBackground(new java.awt.Color(255, 255, 255));
        btn9.setFont(new java.awt.Font("Tahoma", 1, 33)); // NOI18N
        btn9.setText("9");
        btn9.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn9ActionPerformed(evt);
            }
        });
        jPanel1.add(btn9);
        btn9.setBounds(190, 170, 73, 40);

        jPanel4.add(jPanel1);
        jPanel1.setBounds(280, 30, 280, 280);

        btnDaviPlata.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        btnDaviPlata.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/openbravo/images/cheque.png"))); // NOI18N
        btnDaviPlata.setText("Daviplata");
        btnDaviPlata.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDaviPlataActionPerformed(evt);
            }
        });
        jPanel4.add(btnDaviPlata);
        btnDaviPlata.setBounds(140, 90, 120, 70);

        btnEfectivo.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        btnEfectivo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/openbravo/images/cash.png"))); // NOI18N
        btnEfectivo.setText("Efectivo");
        btnEfectivo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEfectivoActionPerformed(evt);
            }
        });
        jPanel4.add(btnEfectivo);
        btnEfectivo.setBounds(10, 90, 120, 70);

        btnTarjeta.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        btnTarjeta.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/openbravo/images/ccard.png"))); // NOI18N
        btnTarjeta.setText("Tarjeta");
        btnTarjeta.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnTarjetaActionPerformed(evt);
            }
        });
        jPanel4.add(btnTarjeta);
        btnTarjeta.setBounds(10, 250, 120, 70);

        btnBanco.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        btnBanco.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/openbravo/images/bank.png"))); // NOI18N
        btnBanco.setText("Banco");
        btnBanco.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBancoActionPerformed(evt);
            }
        });
        jPanel4.add(btnBanco);
        btnBanco.setBounds(140, 170, 120, 70);

        btnNequi.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        btnNequi.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/openbravo/images/nequi.png"))); // NOI18N
        btnNequi.setText("Nequi");
        btnNequi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNequiActionPerformed(evt);
            }
        });
        jPanel4.add(btnNequi);
        btnNequi.setBounds(10, 170, 120, 70);

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        jLabel1.setText("faltante");
        jPanel4.add(jLabel1);
        jLabel1.setBounds(10, 4, 190, 30);

        getContentPane().add(jPanel4, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

    private void btnDaviPlataActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDaviPlataActionPerformed
        validarCampos("cheque");
    }//GEN-LAST:event_btnDaviPlataActionPerformed

    private void btnEfectivoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEfectivoActionPerformed
        validarCampos("cash");
    }//GEN-LAST:event_btnEfectivoActionPerformed

    private void btnTarjetaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTarjetaActionPerformed
        validarCampos("ccard");
    }//GEN-LAST:event_btnTarjetaActionPerformed

    private void btnBancoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBancoActionPerformed
        validarCampos("bank");
    }//GEN-LAST:event_btnBancoActionPerformed

    private void btnNequiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNequiActionPerformed
        validarCampos("slip");
    }//GEN-LAST:event_btnNequiActionPerformed

    private void btn1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn1ActionPerformed
        digitarNumero(NUMERO_UNO);
    }//GEN-LAST:event_btn1ActionPerformed

    private void btn2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn2ActionPerformed
        digitarNumero(NUMERO_DOS);
    }//GEN-LAST:event_btn2ActionPerformed

    private void btn3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn3ActionPerformed
        digitarNumero(NUMERO_TRES);
    }//GEN-LAST:event_btn3ActionPerformed

    private void btn4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn4ActionPerformed
        digitarNumero(NUMERO_CUATRO);
    }//GEN-LAST:event_btn4ActionPerformed

    private void btn5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn5ActionPerformed
        digitarNumero(NUMERO_CINCO);
    }//GEN-LAST:event_btn5ActionPerformed

    private void btn6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn6ActionPerformed
        digitarNumero(NUMERO_SEIS);
    }//GEN-LAST:event_btn6ActionPerformed

    private void btn7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn7ActionPerformed
        digitarNumero(NUMERO_SIETE);
    }//GEN-LAST:event_btn7ActionPerformed

    private void btn8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn8ActionPerformed
        digitarNumero(NUMERO_OCHO);
    }//GEN-LAST:event_btn8ActionPerformed

    private void btn9ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn9ActionPerformed
        digitarNumero(NUMERO_NUEVE);
    }//GEN-LAST:event_btn9ActionPerformed

    private void btn0ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn0ActionPerformed
        digitarNumero(NUMERO_CERO);
    }//GEN-LAST:event_btn0ActionPerformed

    private void btnCEActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCEActionPerformed
        digitarNumero(NUMERO_CE);
    }//GEN-LAST:event_btnCEActionPerformed

    private void txtValorRecibidoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtValorRecibidoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtValorRecibidoActionPerformed


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
    private javax.swing.JButton btnBanco;
    private javax.swing.JButton btnCE;
    private javax.swing.JButton btnDaviPlata;
    private javax.swing.JButton btnEfectivo;
    private javax.swing.JButton btnNequi;
    private javax.swing.JButton btnTarjeta;
    private javax.swing.JButton jButton10;
    private javax.swing.JButton jButton11;
    private javax.swing.JButton jButton12;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JTextField txtValorCuenta;
    private javax.swing.JTextField txtValorRecibido;
    // End of variables declaration//GEN-END:variables

    @Override
    public void activate() throws BasicException {
        paymentdialog = JPaymentSelectCustomer.getDialog(this);
        paymentdialog.init(app);
    }

    /**
     * @return
     */
    @Override
    public boolean deactivate() {
        if (dirty.isDirty()) {
            boolean confirmacion = AlertasPersonalizadas.mostrarAlertaConfirmacion(LocalRes.getIntString("message.wannasave"));

            if (confirmacion) {
                save();
                return true;
            } else {
                return false;
            }
        } else {
            return true;
        }
    }

    private void save() {

    }

    @Override
    public void init(AppView app) throws BeanFactoryException {

        this.app = app;
        dlcustomers = (DataLogicCustomers) app.getBean("com.openbravo.pos.customers.DataLogicCustomers");
        dlsales = (DataLogicSales) app.getBean("com.openbravo.pos.forms.DataLogicSales");
        dlsystem = (DataLogicSystem) app.getBean("com.openbravo.pos.forms.DataLogicSystem");
        ttp = new TicketParser(app.getDeviceTicket(), dlsystem);
    }

    /**
     * @return
     */
    @Override
    public Object getBean() {
        return this;
    }

    @Override
    public JComponent getComponent() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getTitle() {
        return "Plan Separe";
    }

}
