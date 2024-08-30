/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.openbravo.pos.licencia;

import com.openbravo.alerta.AlertasPersonalizadas;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

/**
 *
 * @author User
 */
public class VerificadorLicencia {

    private static final String RUTA_ARCHIVO = System.getProperty("user.dir") + "/target/Configs/configuracion.txt";

    public VerificadorLicencia() {
    }

    public String direccionIpUsuario() {

        try {
            InetAddress localhost = InetAddress.getLocalHost();

            NetworkInterface networkInterface = NetworkInterface.getByInetAddress(localhost);

            if (networkInterface != null) {
                byte[] macAddress = networkInterface.getHardwareAddress();

                if (macAddress != null) {
                    StringBuilder macAddressStr = new StringBuilder();
                    for (int i = 0; i < macAddress.length; i++) {
                        macAddressStr.append(String.format("%02X%s", macAddress[i], (i < macAddress.length - 1) ? "-" : ""));
                    }
                    return macAddressStr.toString();
                }
            } else {
                System.out.println("No se pudo obtener la interfaz de red.");
            }
        } catch (UnknownHostException | SocketException e) {
            e.printStackTrace();
        }
        return "";
    }


    public String encriptar(String textoPlano, SecretKey claveSecreta) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");

            // Generar IV
            SecureRandom random = new SecureRandom();
            byte[] iv = new byte[cipher.getBlockSize()];
            random.nextBytes(iv);

            // Inicializar Cipher con IV
            cipher.init(Cipher.ENCRYPT_MODE, claveSecreta, new IvParameterSpec(iv));

            byte[] textoEncriptado = cipher.doFinal(textoPlano.getBytes());

            // Concatenar IV al inicio del texto encriptado
            byte[] resultado = new byte[iv.length + textoEncriptado.length];
            System.arraycopy(iv, 0, resultado, 0, iv.length);
            System.arraycopy(textoEncriptado, 0, resultado, iv.length, textoEncriptado.length);

            return Base64.getEncoder().encodeToString(resultado);
        } catch (InvalidAlgorithmParameterException ex) {
            Logger.getLogger(VerificadorLicencia.class.getName()).log(Level.SEVERE, null, ex);
            return "";
        }
    }

    public String desencriptar(String textoEncriptado, SecretKey claveSecreta) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException {
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        byte[] datosEncriptados = Base64.getDecoder().decode(textoEncriptado);
        byte[] iv = new byte[cipher.getBlockSize()];
        System.arraycopy(datosEncriptados, 0, iv, 0, iv.length);
        cipher.init(Cipher.DECRYPT_MODE, claveSecreta, new IvParameterSpec(iv));
        byte[] textoDesencriptado = cipher.doFinal(datosEncriptados, iv.length, datosEncriptados.length - iv.length);

        return new String(textoDesencriptado);
    }

    public List<String> obtenerValidacionConfiguracionTxt() {
        File archivo = new File(RUTA_ARCHIVO);
        if (archivo.exists()) {
            try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
                List<String> lineas = new ArrayList<>();
                String lineaActual;
                while ((lineaActual = br.readLine()) != null) {
                    lineas.add(lineaActual);
                }
                return lineas;
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("No se encontró el archivo: " + RUTA_ARCHIVO);
        }
        return Collections.emptyList();
    }

    public static void editarUltimasDosLineas(String valor1, String valor2) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(RUTA_ARCHIVO, false))) {
            // Escribir las nuevas líneas en el archivo
            writer.write(valor1);
            writer.newLine(); // Añadir una nueva línea
            writer.write(valor2);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String generarClaveAleatoria(int longitud) {
        Random random = new Random();
        StringBuilder clave = new StringBuilder();

        for (int i = 0; i < longitud; i++) {
            clave.append(random.nextInt(10));
        }

        return clave.toString();
    }

    public boolean validarInicio() {
        try {
            String clave1;
            String clave2;
            String ipMacUsuario;
            List<String> lineas = obtenerValidacionConfiguracionTxt();
            ipMacUsuario = obtenerIdentificadorUnicoPC();
            KeyGenerator keyGen = KeyGenerator.getInstance("AES");
            keyGen.init(128); 
            SecretKey secretKey = cargarClaveSecreta("RBwPsitxu232Fvgnc1aHsQ==");

            for (String linea : lineas) {
                System.out.println(linea);
            }

            if (!lineas.isEmpty() || lineas.size() != 2) {
                clave1 = lineas.get(0);
                clave2 = lineas.get(1);
                clave1 = desencriptar(clave1, secretKey);
                clave2 = desencriptar(clave2, secretKey);

                if (clave1.equals("true")) {
                    EnviarCorreo dialogo = new EnviarCorreo();

                    dialogo.setVisible(true);

                } else {
                    if (clave2.equals(ipMacUsuario)) {
                        return true;
                    } else {
                        AlertasPersonalizadas
                                .mostrarAlertaError("hace falta una configuracion importante comunicate con el administrados");
                        return false;
                    }
                }

            } else {
                AlertasPersonalizadas
                        .mostrarAlertaError("hace falta una configuracion importante comunicate con el administrados");
                return false;
            }

        } catch (Exception e) {
            
            AlertasPersonalizadas
                    .mostrarAlertaError("error al iniciar la aplicacion");
            return false;
        }
        return true;
    }

    public SecretKey cargarClaveSecreta(String clave) {
        byte[] decodedKey = Base64.getDecoder().decode(clave);
        SecretKey originalKey = new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES");
        return originalKey;
    }

    public String obtenerIdentificadorUnicoPC() {
        try {
            InetAddress localhost = InetAddress.getLocalHost();
            String nombreHost = localhost.getHostName();
            String nombreSO = System.getProperty("os.name");
            String versionSO = System.getProperty("os.version");
            String arquitecturaSO = System.getProperty("os.arch");

            String identificador = nombreHost + "_" + nombreSO + "_" + versionSO + "_" + arquitecturaSO;

            return identificador;
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return "";
    }

}
