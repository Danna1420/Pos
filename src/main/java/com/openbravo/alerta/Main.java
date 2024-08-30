/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.openbravo.alerta;

import com.openbravo.data.loader.LocalRes;
import javax.swing.JOptionPane;

/**
 *
 * @author User
 */
// Clase Main
public class Main {
    public static void main(String[] args) {
        // Ejemplo de alerta de información
        AlertasPersonalizadas.mostrarAlertaInformacion("Esto es un mensaje de información.");
        boolean confirmacion = AlertasPersonalizadas.mostrarAlertaConfirmacion(LocalRes.getIntString("message.wannadelete"));

        // Ejemplo de alerta de confirmación
       
        if (confirmacion) {
            System.out.println("Usuario hizo clic en Sí");
        } else {
            System.out.println("Usuario hizo clic en No");
        }

        // Ejemplo de alerta de error
        AlertasPersonalizadas.mostrarAlertaError("Esto es un mensaje de error.");
    }
}
