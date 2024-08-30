/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.openbravo.pos.licencia;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;
import javax.swing.JFrame;
import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
/**
 *
 * @author User
 */
public class Main {



    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            
           // EnviarCorreo dialogo = new EnviarCorreo();
        //dialogo.setVisible(true);
            /* SendMail frame = new SendMail();

            // Deshabilita la capacidad de redimensionar la ventana
            frame.setResizable(false);

            // Evita que la ventana se cierre directamente al hacer clic en la "X"
            frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

            // Agrega un WindowListener para controlar el evento de cierre
            frame.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    // Puedes colocar aquí tu lógica antes de cerrar la ventana
                    // Por ejemplo, mostrar un cuadro de diálogo de confirmación

                    int opcion = JOptionPane.showConfirmDialog(frame, "¿Estás seguro de que deseas salir?", "Confirmar salida", JOptionPane.YES_NO_OPTION);
                    if (opcion == JOptionPane.YES_OPTION) {
                        // Cierra la ventana si el usuario confirma
                        frame.dispose();
                    }
                }
            });

            // Configura el contenido de la ventana (puedes agregar tus componentes aquí)
            frame.setSize(350, 250);
            frame.setLocationRelativeTo(null); // Centra la ventana en la pantalla
            frame.setVisible(true);
            
            /*VerificadorLicencia verificadorLicencia = new VerificadorLicencia();
            KeyGenerator keyGen = KeyGenerator.getInstance("AES");
            keyGen.init(128); // por ejemplo
            SecretKey secretKey = verificadorLicencia.cargarClaveSecreta("RBwPsitxu232Fvgnc1aHsQ==");

            // Codificar la clave secreta a Base64
            String encodedKey = Base64.getEncoder().encodeToString(secretKey.getEncoded());
             verificadorLicencia.incriparArchivo("true", secretKey);
              verificadorLicencia.incriparArchivo("licencia", secretKey);

            // Imprimir la clave generada
          
            // verificadorLicencia.guardarInformacionEnBlockDeNotas(RUTA_ARCHIVO, 1000, secretKey);
            /* String claveSecretaString = "CY8EFXa89kDWioKw2sH/Qg==";

            // Decodificar la clave secreta desde Base64
            byte[] claveSecretaBytes = Base64.getDecoder().decode(claveSecretaString);

            // Crear la instancia de SecretKey usando la clave decodificada
           // SecretKey claveSecreta = new SecretKeySpec(claveSecretaBytes, "AES");

        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        SecretKey claveSecreta = keyGenerator.generateKey();

        // Imprimir la clave generada
        System.out.println("Clave generada: " + Base64.getEncoder().encodeToString(claveSecreta.getEncoded()));

        String textoPlano = "60fdf521f4";

        String textoEncriptado = verificadorLicencia.encriptar(textoPlano, claveSecreta);
        System.out.println("Texto encriptado: " + textoEncriptado);

        String textoDesencriptado = verificadorLicencia.desencriptar(textoEncriptado, claveSecreta);
        System.out.println("Texto desencriptado: " + textoDesencriptado);
        verificadorLicencia.obtenerValidacionConfiguracionTxt();
        
        verificadorLicencia.editarUltimasDosLineas("Ejemplo de valor123");*/
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
