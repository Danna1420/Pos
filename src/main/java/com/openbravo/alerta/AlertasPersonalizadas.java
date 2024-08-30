/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.openbravo.alerta;

/**
 *
 * @author User
 */
// Clase de Alerta

import javax.swing.*;
import java.awt.*;

public class AlertasPersonalizadas {

    private static boolean respuestaConfirmacion;

    public static void mostrarAlertaInformacion(String mensaje) {
        mostrarAlerta("Información", mensaje, JOptionPane.INFORMATION_MESSAGE);
    }

    public static boolean mostrarAlertaConfirmacion(String mensaje) {
        respuestaConfirmacion = false;
        mostrarAlertaConfirmacionDialog("Confirmación", mensaje);
        return respuestaConfirmacion;
    }

    public static void mostrarAlertaError(String mensaje) {
        mostrarAlerta("Error", mensaje, JOptionPane.ERROR_MESSAGE);
    }

    public static String mostrarAlertaEntrada(String mensaje) {
        String textoIngresado;
        do {
            textoIngresado = JOptionPane.showInputDialog(null, mensaje);

            if (textoIngresado == null) {
                mostrarAlertaInformacion("¡Hasta luego!");
                System.exit(0);
            }

            if (textoIngresado.isEmpty()) {
                mostrarAlertaInformacion("Debes ingresar un valor.");
            }

        } while (textoIngresado.isEmpty());

        return textoIngresado;
    }

    private static void mostrarAlerta(String titulo, String mensaje, int tipoMensaje) {
        UIManager.put("OptionPane.background", new Color(255, 255, 255));
        UIManager.put("Panel.background", new Color(255, 255, 255));

        JOptionPane.showMessageDialog(null, mensaje, titulo, tipoMensaje);
    }

    private static void mostrarAlertaConfirmacionDialog(String titulo, String mensaje) {
        UIManager.put("OptionPane.background", new Color(255, 255, 255));
        UIManager.put("Panel.background", new Color(255, 255, 255));

        int resultado = JOptionPane.showConfirmDialog(null, mensaje, titulo, JOptionPane.YES_NO_OPTION);

        respuestaConfirmacion = (resultado == JOptionPane.YES_OPTION);
    }

}