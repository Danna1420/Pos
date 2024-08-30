/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.openbravo.pos.planSepare;

import com.openbravo.data.loader.Session;
import com.openbravo.pos.forms.DriverWrapper;
import com.openbravo.pos.util.AltEncrypter;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.UUID;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;


import static com.openbravo.pos.customers.CustomersPayment.idCliente;

/**
 * @author User
 */
public class PlanSepareModelo {


    public Connection obtenerConexion() throws SQLException, IOException, InstantiationException, IllegalAccessException, ClassNotFoundException {
        Properties propiedades = new Properties();
        String fileConfigUser = System.getProperty("user.home") + "\\interpost.properties";
        propiedades.load(new FileReader(fileConfigUser));

        String driverlib = propiedades.getProperty("db.driverlib");
        String driver = propiedades.getProperty("db.driver");
        String url = propiedades.getProperty("db.URL");
        String user = propiedades.getProperty("db.user");
        String password = propiedades.getProperty("db.password");
        String nameBD = propiedades.getProperty("db.schema");
        String formatConection = "?zeroDateTimeBehavior=convertToNull";

        if (user != null && password != null && password.startsWith("crypt:")) {
            AltEncrypter cypher = new AltEncrypter("cypherkey" + user);
            password = cypher.decrypt(password.substring(6));
        }

        ClassLoader cloader = new URLClassLoader(new URL[]{new File(driverlib).toURI().toURL()});
        DriverManager.registerDriver(new DriverWrapper((Driver) Class.forName(driver, true, cloader).newInstance()));

        url = url + nameBD + formatConection;
        Session session = new Session(url, user, password);
        return session.getConnection();
    }

    public List<PlanSepareDTO> listarProducto(String idCliente) {
        List<PlanSepareDTO> listaProductoSelecionado = new ArrayList<>();

        try (Connection connection = obtenerConexion()) {
            boolean isValid = (connection != null) && connection.isValid(1000);
            if (isValid) {
                ResultSet resultSet = null;
                String SQL = "";
                PreparedStatement stmt = null;

                SQL = "SELECT * FROM plansepare WHERE idCliente = ? AND estadoPagado = ?";
                stmt = connection.prepareStatement(SQL);
                stmt.setString(1, idCliente);
                stmt.setString(2, "false");
                resultSet = stmt.executeQuery();

                while (resultSet.next()) {
                    PlanSepareDTO planSepareDTO = new PlanSepareDTO();
                    planSepareDTO.setId(resultSet.getInt(1));
                    planSepareDTO.setNombreCliente(resultSet.getString(2));

                    String productosString = resultSet.getString(3);
                    if (productosString != null && !productosString.isEmpty()) {
                        List<String> listaProductos = Arrays.asList(productosString.split(","));
                        planSepareDTO.setListaProducto(listaProductos);

                        List<String> nombresProductos = new ArrayList<>();
                        for (String productId : listaProductos) {
                            String nombreProducto = obtenerNombreProductoPorId(productId, connection);
                            nombresProductos.add(nombreProducto);
                        }
                        planSepareDTO.setListaProducto(nombresProductos);
                    }
                    String preciosString = resultSet.getString(4);
                    if (preciosString != null && !preciosString.isEmpty()) {
                        List<String> listaPrecios = Arrays.asList(preciosString.split(","));
                        planSepareDTO.setPrecioProducto(listaPrecios);
                    }

                    planSepareDTO.setIdCliente(resultSet.getString(5));
                    planSepareDTO.setPrecioAbonado(resultSet.getDouble(6));
                    planSepareDTO.setPrecioTotal(resultSet.getDouble(7));
                    planSepareDTO.setEstadoPagado(resultSet.getString(8));

                    String cantidadProducto = resultSet.getString(9);
                    if (cantidadProducto != null && !cantidadProducto.isEmpty()) {
                        List<String> listaCantidad = Arrays.asList(cantidadProducto.split(","));
                        planSepareDTO.setListaCantidadProducto(listaCantidad);
                    }

                    listaProductoSelecionado.add(planSepareDTO);
                }
            } else {
                throw new SQLException("Connection Error");
            }
        } catch (SQLException | IOException | InstantiationException | IllegalAccessException
                 | ClassNotFoundException e) {
            e.printStackTrace();
        }

        return listaProductoSelecionado;
    }

    private String obtenerNombreProductoPorId(String productId, Connection connection) throws SQLException {
        String nombreProducto = "";
        String SQL = "SELECT name FROM products WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(SQL)) {
            stmt.setString(1, productId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    nombreProducto = rs.getString("name");
                }
            }
        }
        return nombreProducto;
    }

    public void insertarActualizar(double pago, String idCliente, boolean insertarActualizar, int id) {
        try (Connection connection = obtenerConexion()) {
            boolean isValid = (connection != null) && connection.isValid(1000);
            if (isValid) {
                int pagoSinDecimales = (int) Math.floor(pago);
                String SQL = "UPDATE planSepare SET precioAbonado = precioAbonado + ?, estadoPagado = ? WHERE idCliente = ? AND id = ?";
                try (PreparedStatement stmt = connection.prepareStatement(SQL)) {
                    stmt.setDouble(1, pagoSinDecimales);
                    stmt.setString(2, String.valueOf(insertarActualizar));
                    stmt.setString(3, idCliente);
                    stmt.setInt(4, id);
                    int rowsAffected = stmt.executeUpdate();

                    if (rowsAffected > 0) {
                        System.out.println("Actualización exitosa.");
                    } else {
                        System.out.println("No se encontraron registros para actualizar.");
                    }
                }
            }

        } catch (SQLException | IOException | InstantiationException | IllegalAccessException
                 | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void insertarReciboYPago(double pago, String nombrePago) {
        String idCaja = obtenerValorMoney();
        int pagoSinDecimales = (int) Math.floor(pago);
        try (Connection connection = obtenerConexion()) {
            boolean isValid = (connection != null) && connection.isValid(1000);
            if (isValid) {
                String receiptId = UUID.randomUUID().toString();

                // Insertar el recibo en la tabla receipts
                String insertReceiptSQL = "INSERT INTO receipts (ID, MONEY, DATENEW, ATTRIBUTES, PERSON) VALUES (?, ?, ?, ?, ?)";
                try (PreparedStatement stmtReceipt = connection.prepareStatement(insertReceiptSQL)) {
                    stmtReceipt.setString(1, receiptId);
                    stmtReceipt.setString(2, idCaja);
                    stmtReceipt.setTimestamp(3, new Timestamp(System.currentTimeMillis()));
                    stmtReceipt.setNull(4, Types.BLOB);
                    stmtReceipt.setNull(5, Types.VARCHAR);

                    int rowsAffectedReceipt = stmtReceipt.executeUpdate();

                    if (rowsAffectedReceipt > 0) {
                        System.out.println("Recibo insertado correctamente con ID: " + receiptId);
                    } else {
                        System.out.println("Error al insertar el recibo.");
                        return;
                    }
                }


                //insertarTaxLine(receiptId, pagoSinDecimales);


                String insertPaymentSQL = "INSERT INTO payments (ID, RECEIPT, PAYMENT, TOTAL, TRANSID, RETURNMSG, TENDERED, CARDNAME, VOUCHER, NOTES) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
                try (PreparedStatement stmtPayment = connection.prepareStatement(insertPaymentSQL)) {
                    String name = nombrePago;
                    double total = pagoSinDecimales;
                    double paid = 0;
                    String returnMsg = null;
                    String voucher = null;
                    String cardName = null;
                    String notes = "";

                    stmtPayment.setString(1, UUID.randomUUID().toString());
                    stmtPayment.setString(2, receiptId);
                    stmtPayment.setString(3, name);
                    stmtPayment.setDouble(4, total);
                    stmtPayment.setString(5, "no ID");
                    stmtPayment.setString(6, returnMsg);
                    stmtPayment.setDouble(7, paid);
                    stmtPayment.setString(8, cardName);
                    stmtPayment.setString(9, voucher);
                    stmtPayment.setString(10, notes);

                    int rowsAffectedPayment = stmtPayment.executeUpdate();

                    System.out.println("Número total de filas afectadas por la inserción de pago: " + rowsAffectedPayment);
                }
            }
        } catch (SQLException | InstantiationException | IllegalAccessException | ClassNotFoundException |
                 IOException e) {
            e.printStackTrace();
        }
    }

    public void insertarTaxLine(String receiptId, double pago) {
        try (Connection connection = obtenerConexion()) {
            boolean isValid = (connection != null) && connection.isValid(1000);
            if (isValid) {
                String insertTaxLines = "INSERT INTO taxlines (ID, RECEIPT, TAXID, BASE, AMOUNT)  "
                        + "VALUES (?, ?, ?, ?, ?)";
                double porcentajeImpuesto = 0.0;
                double impuesto = (porcentajeImpuesto / 100) * pago;
                double subtotal = pago - impuesto;

                try (PreparedStatement stmtTaxline = connection.prepareStatement(insertTaxLines)) {
                    stmtTaxline.setString(1, UUID.randomUUID().toString());
                    stmtTaxline.setString(2, receiptId);
                    stmtTaxline.setString(3, "001");
                    stmtTaxline.setDouble(4, subtotal);
                    stmtTaxline.setDouble(5, impuesto);


                    int rowsAffectedReceipt = stmtTaxline.executeUpdate();

                    if (rowsAffectedReceipt > 0) {
                        System.out.println("Recibo insertado correctamente con ID: " + receiptId);
                    } else {
                        System.out.println("Error al insertar el recibo.");
                        return;
                    }
                }
            }
        } catch
        (SQLException | InstantiationException | IllegalAccessException | ClassNotFoundException | IOException e) {
            e.printStackTrace();
        }
    }


    public String obtenerValorMoney() {
        String money = null;
        try (Connection connection = obtenerConexion()) {

            boolean isValid = (connection != null) && connection.isValid(1000);
            if (isValid) {
                String selectSQL = "SELECT money FROM closedcash WHERE dateend IS NULL OR hostsequence = (SELECT MAX(hostsequence) FROM closedcash)";
                try (PreparedStatement stmt = connection.prepareStatement(selectSQL)) {
                    ResultSet rs = stmt.executeQuery();
                    if (rs.next()) {
                        money = rs.getString("money");
                    }
                }
            }

        } catch (SQLException | InstantiationException | IllegalAccessException | ClassNotFoundException |
                 IOException e) {
            e.printStackTrace();
        }

        return money;
    }

    public boolean verificarRegistroExistente(String idCliente) {
        try (Connection connection = obtenerConexion()) {
            boolean isValid = (connection != null) && connection.isValid(1000);
            if (isValid) {
                String selectSQL = "SELECT COUNT(*) AS count FROM planSepare WHERE idCliente = ? AND estadoPagado = 'false'";
                try (PreparedStatement stmt = connection.prepareStatement(selectSQL)) {
                    stmt.setString(1, idCliente);
                    ResultSet rs = stmt.executeQuery();
                    if (rs.next()) {
                        int count = rs.getInt("count");
                        return count == 0;
                    }
                }
            }
        } catch (SQLException | IOException | InstantiationException | IllegalAccessException |
                 ClassNotFoundException e) {
            e.printStackTrace();
        }
        return false;
    }


    public void sacarProducto() {
        List<ProductoCantidad> listaProducto = obtenerNombresProductosPlanSeparePorCliente(idCliente);
        try (Connection connection = obtenerConexion()) {
            boolean isValid = (connection != null) && connection.isValid(1000);
            if (isValid) {
                for (ProductoCantidad producto : listaProducto) {
                    int cantidad = producto.getCantidad(); // Obtener la cantidad del producto
                    String SQL = "UPDATE stockcurrent SET units = units - ? WHERE product = ?";
                    try (PreparedStatement stmt = connection.prepareStatement(SQL)) {
                        stmt.setInt(1, cantidad); // Establecer la cantidad como parámetro
                        stmt.setString(2, producto.getNombreProducto());
                        int rowsAffected = stmt.executeUpdate();

                        if (rowsAffected > 0) {
                            System.out.println("Actualización exitosa para el producto: " + producto.getNombreProducto());
                        } else {
                            System.out.println("No se encontraron registros para actualizar para el producto: " + producto.getNombreProducto());
                        }
                    }
                }
            }
        } catch (SQLException | IOException | InstantiationException | IllegalAccessException
                 | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }


    public List<ProductoCantidad> obtenerNombresProductosPlanSeparePorCliente(String idCliente) {
        List<ProductoCantidad> productosConCantidad = new ArrayList<>();

        try (Connection connection = obtenerConexion()) {
            boolean isValid = (connection != null) && connection.isValid(1000);
            if (isValid) {
                String SQL = "SELECT listaProducto, listaCantidad FROM planSepare WHERE idCliente = ? AND estadoPagado = ?";
                try (PreparedStatement stmt = connection.prepareStatement(SQL)) {
                    stmt.setString(1, idCliente);
                    stmt.setString(2, "false");
                    try (ResultSet rs = stmt.executeQuery()) {
                        while (rs.next()) {
                            String listaProducto = rs.getString("listaProducto");
                            String listaCantidadProducto = rs.getString("listaCantidad");
                            String[] productos = listaProducto.split(",");
                            String[] cantidades = listaCantidadProducto.split(",");

                            // Asegurarse de que haya la misma cantidad de productos y cantidades
                            if (productos.length == cantidades.length) {
                                for (int i = 0; i < productos.length; i++) {
                                    productosConCantidad.add(new ProductoCantidad(productos[i], Integer.parseInt(cantidades[i])));
                                }
                            }
                        }
                    }
                }
            }
        } catch (SQLException | IOException | InstantiationException | IllegalAccessException |
                 ClassNotFoundException e) {
            e.printStackTrace();
        }

        return productosConCantidad;
    }

    public List<PlanSepareDTO> informePlanSepare(String checkBoss) {
        List<PlanSepareDTO> listaInforme = new ArrayList<>();
        try (Connection connection = obtenerConexion()) {
            boolean isValid = (connection != null) && connection.isValid(1000);
            if (isValid) {
                String SQL;
                if (checkBoss.isEmpty()) {
                    SQL = "SELECT * FROM planSepare WHERE estadoPagado IN ('true', 'false')";
                } else {
                    SQL = "SELECT * FROM planSepare WHERE estadoPagado = ?";
                }

                try (PreparedStatement stmt = connection.prepareStatement(SQL)) {
                    if (!checkBoss.isEmpty()) {
                        stmt.setString(1, checkBoss);
                    }
                    try (ResultSet rs = stmt.executeQuery()) {
                        while (rs.next()) {
                            PlanSepareDTO planSepareDTO = new PlanSepareDTO();
                            planSepareDTO.setId(rs.getInt("id"));
                            planSepareDTO.setNombreCliente(rs.getString("nombreCliente"));
                            planSepareDTO.setIdCliente(rs.getString("idCliente"));
                            planSepareDTO.setEstadoPagado(rs.getString("estadoPagado"));
                            double precioAbonado = rs.getDouble("precioAbonado");
                            double precioTotal = rs.getDouble("precioTotal");
                            if (precioAbonado > precioTotal) {
                                planSepareDTO.setPrecioAbonado(precioTotal);
                            } else {
                                planSepareDTO.setPrecioAbonado(precioAbonado);
                            }
                            planSepareDTO.setPrecioTotal(precioTotal);
                            listaInforme.add(planSepareDTO);
                        }
                    }
                }
            }
        } catch (SQLException | IOException | InstantiationException | IllegalAccessException |
                 ClassNotFoundException e) {
            e.printStackTrace();
        }
        return listaInforme;
    }


    public void ejecutarInstrucionSql() throws SQLException, IOException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        try (Connection connection = obtenerConexion()) {
            boolean isValid = (connection != null) && connection.isValid(1000);
            if (isValid) {
                // Creamos un PreparedStatement para la inserción
                try (PreparedStatement pstmt = connection.prepareStatement("INSERT INTO resources(id, name, restype, content) VALUES(?, ?, ?, ?)")) {
                    // Insertamos cada archivo
                    insertFile(pstmt, "C:\\Users\\User\\Videos\\trabajo\\InterPost\\src\\main\\resources\\com\\openbravo\\images\\.01.png", 11);
                    insertFile(pstmt, "C:\\Users\\User\\Videos\\trabajo\\InterPost\\src\\main\\resources\\com\\openbravo\\images\\.02.png", 12);
                    insertFile(pstmt, "C:\\Users\\User\\Videos\\trabajo\\InterPost\\src\\main\\resources\\com\\openbravo\\images\\.05.png", 13);
                    // Inserta los demás archivos de la misma manera
                }
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void insertFile(PreparedStatement pstmt, String filePath, int id) throws SQLException, IOException {
        File file = new File(filePath);
        if (file.exists()) {
            byte[] fileContent = Files.readAllBytes(file.toPath());
            String fileName = file.getName();

            // Configura los parámetros del PreparedStatement
            pstmt.setInt(1, id);
            pstmt.setString(2, fileName);
            pstmt.setInt(3, 1); // restype siempre es 1
            pstmt.setBytes(4, fileContent);

            // Ejecuta la inserción
            pstmt.executeUpdate();
        } else {
            System.out.println("El archivo " + filePath + " no existe.");
        }
    }
}
