/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.openbravo.pos.planSepare;

/**
 *
 * @author User
 */
import com.openbravo.alerta.AlertasPersonalizadas;
import com.openbravo.format.Formats;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import static com.openbravo.pos.sales.JPanelTicket.formatoMoneda;

public class InformePlanSepare extends JDialog {
    private JRadioButton pendienteRadioButton;
    private JRadioButton pagadoRadioButton;
    private JRadioButton todoRadioButton;
    private JButton buscarButton;
    private JTable informeTable;

    public InformePlanSepare() {
        setTitle("Informe Plan Separe");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        setResizable(false);
        setModal(true);

        ButtonGroup radioButtonGroup = new ButtonGroup();

        JPanel filterPanel = new JPanel();
        filterPanel.setLayout(new GridLayout(1, 3));

        pendienteRadioButton = new JRadioButton("Pendiente");
        pagadoRadioButton = new JRadioButton("Pagado");
        todoRadioButton = new JRadioButton("Todo");

        radioButtonGroup.add(pendienteRadioButton);
        radioButtonGroup.add(pagadoRadioButton);
        radioButtonGroup.add(todoRadioButton);

        filterPanel.add(pendienteRadioButton);
        filterPanel.add(pagadoRadioButton);
        filterPanel.add(todoRadioButton);

        informeTable = new JTable(new DefaultTableModel(new Object[]{"Nombre Cliente", "Precio Abonado", "Precio Total", "Estado"}, 0));

        buscarButton = new JButton("Buscar");
        buscarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                buscar();
            }
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(buscarButton);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(filterPanel, BorderLayout.NORTH);
        mainPanel.add(new JScrollPane(informeTable), BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        setContentPane(mainPanel);
    }

    private void buscar() {
        DefaultTableModel model = (DefaultTableModel) informeTable.getModel();
        model.setRowCount(0); // Clear table

        String estado = "";
        if (pendienteRadioButton.isSelected()) {
            estado = "false";
        } else if (pagadoRadioButton.isSelected()) {
            estado = "true";
        }

        PlanSepareModelo planSepareModelo = new PlanSepareModelo();
        List<PlanSepareDTO> listaInformacionPlanSepare = planSepareModelo.informePlanSepare(estado);
        if(listaInformacionPlanSepare.isEmpty()){
            AlertasPersonalizadas.mostrarAlertaInformacion("no hay datos");
            return;
        }

        for (PlanSepareDTO planSepareDTO : listaInformacionPlanSepare) {
            double precioAbonado = planSepareDTO.getPrecioAbonado();
            double precioTotal = planSepareDTO.getPrecioTotal();

            String estadoRow = planSepareDTO.getEstadoPagado().equalsIgnoreCase("true") ? "Terminado" : "Pendiente";

            DefaultTableCellRenderer renderer = new DefaultTableCellRenderer() {
                @Override
                public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                    Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

                    if ("Pendiente".equals(value)) {
                        c.setBackground(Color.RED);
                        c.setForeground(Color.WHITE);
                    } else {
                        c.setBackground(Color.BLUE);
                        c.setForeground(Color.WHITE);
                    }

                    return c;
                }
            };

            informeTable.getColumnModel().getColumn(3).setCellRenderer(renderer);

            model.addRow(new Object[]{planSepareDTO.getNombreCliente(), Formats.CURRENCY.formatValue(precioAbonado),
                    Formats.CURRENCY.formatValue(precioTotal), estadoRow});
        }
    }


}
