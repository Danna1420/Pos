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
//    along with uniCenta oPOS.  If not, see <http://www.gnu.org/licenses/>
package com.openbravo.pos.forms;

import com.dalsemi.onewire.OneWireAccessProvider;
import com.dalsemi.onewire.OneWireException;
import com.dalsemi.onewire.adapter.DSPortAdapter;
import com.dalsemi.onewire.application.monitor.DeviceMonitor;
import com.dalsemi.onewire.application.monitor.DeviceMonitorEvent;
import com.dalsemi.onewire.application.monitor.DeviceMonitorEventListener;
import com.dalsemi.onewire.application.monitor.DeviceMonitorException;
import com.dalsemi.onewire.container.OneWireContainer;
import com.dalsemi.onewire.utils.Address;
import com.openbravo.alerta.AlertasPersonalizadas;
import com.openbravo.basic.BasicException;
import com.openbravo.beans.JPasswordDialog;
import com.openbravo.data.gui.JMessageDialog;
import com.openbravo.data.gui.MessageInf;
import com.openbravo.data.loader.BatchSentence;
import com.openbravo.data.loader.BatchSentenceResource;
import com.openbravo.data.loader.Session;
import com.openbravo.format.Formats;
import com.openbravo.pos.licencia.VerificadorLicencia;
import com.openbravo.pos.printer.DeviceTicket;
import com.openbravo.pos.printer.TicketParser;
import com.openbravo.pos.printer.TicketPrinterException;
import com.openbravo.pos.scale.DeviceScale;
import com.openbravo.pos.scanpal2.DeviceScanner;
import com.openbravo.pos.scanpal2.DeviceScannerFactory;
import com.openbravo.pos.util.uOWWatch;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.joda.time.Instant;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.sql.*;
import java.text.DateFormat;
import java.util.Date;
import java.util.*;
import java.util.regex.Matcher;

import static com.openbravo.pos.forms.JPrincipalApp.previousSize;

/**
 * @author adrianromero
 */
// public class JRootApp extends JPanel implements AppView {
@Slf4j
public class JRootApp extends JPanel implements AppView, DeviceMonitorEventListener {

    private AppProperties m_props;
    private Session session;
    private DataLogicSystem m_dlSystem;

    private Properties m_propsdb = null;
    private String m_sActiveCashIndex;
    private int m_iActiveCashSequence;
    private Date m_dActiveCashDateStart;
    private Date m_dActiveCashDateEnd;

    private Double m_dActiveCashNotes;
    private Double m_dActiveCashCoins;
    private Double m_dActiveCashCards;

    private String m_sClosedCashIndex;
    private int m_iClosedCashSequence;
    private Date m_dClosedCashDateStart;
    private Date m_dClosedCashDateEnd;

//    private Double m_dClosedCashNotes;
//    private Double m_dClosedCashCoins;
//    private Double m_dClosedCashCards;
    private String m_sInventoryLocation;

    private StringBuilder inputtext;

    private DeviceScale m_Scale;
    private DeviceScanner m_Scanner;
    private DeviceTicket m_TP;
    private TicketParser m_TTP;

    private final Map<String, BeanFactory> m_aBeanFactories;

    private JPrincipalApp m_principalapp = null;

    private static HashMap<String, String> m_oldclasses;

    private String m_clock;
    private String m_date;
    private Connection con;
    private ResultSet rs;
    private Statement stmt;
    private String SQL;
    private String sJLVersion;
    private DatabaseMetaData md;

    private final int m_rate = 0;

    static {
        initOldClasses();
    }

    private String sLaunch;
    private String sMachine;
    private HashMap<String, AppUser> userMap = new HashMap<>();

    private class PrintTimeAction implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent evt) {
            m_clock = getLineTimer();
            m_date = getLineDate();

            DateTime m_datetime = getDateTime();

            jLabel2.setText("  " + m_date + " " + m_clock);
            /*
* JG Note: Arbritary 8 hour cycle for MySQL server ping on chosen port:nnnn
* MySQL default setting is 28800 seconds (8hrs)
* Better than a host ping as need to know if MySQL is alive & kicking
* Be careful though as MySQL could run out of Connections if it's left on default
            

            webProgressBar.setValue(0);
            
            if (getDateTime().getHourOfDay() == 7 
                || getDateTime().getHourOfDay() == 15
                || getDateTime().getHourOfDay() == 23) {

                if (getDateTime().getMinuteOfHour() == 59 && 
                    (getDateTime().getSecondOfMinute() == 59)) {
                    try {
                        if (pingServer()) {
                            webProgressBar.setValue(0);
                        } else {
                            webProgressBar.setString("Server is down!");
                            webProgressBar.setValue(100);
                        }
                    } catch (UnknownHostException ex) {
                        log.error(ex.getMessage());                    }
                }
            }
             */
        }
    }

    private DateTime getDateTime() {
        DateTime dt = DateTime.now();
        return dt;
    }

    private String getLineTimer() {
        return Formats.HOURMIN.formatValue(new Date());
    }

    private String getLineDate() {
        DateFormat df = DateFormat.getDateInstance(DateFormat.DEFAULT, getDefaultLocale());
        return df.format(new Date());
    }

    public JRootApp() {

        m_aBeanFactories = new HashMap<>();
             VerificadorLicencia verificar = new VerificadorLicencia();
        

        initComponents();
        if (verificar.validarInicio()) {
        serverMonitor.setVisible(false);
        webMemoryBar1.setShowMaximumMemory(true);
        setPreferredSize(new Dimension(50, 50));
        }else{
            System.exit(0);
        }
       
    }

    private DSPortAdapter m_oneWireAdapter;
    private DeviceMonitor m_oneWireMonitor;

    private void initIButtonMonitor() {

        assert m_oneWireMonitor == null;
        try {
            m_oneWireAdapter = OneWireAccessProvider.getDefaultAdapter();
            m_oneWireAdapter.setSearchAllDevices();
            m_oneWireAdapter.targetFamily(0x01);
            m_oneWireAdapter.setSpeed(DSPortAdapter.SPEED_REGULAR);
            m_oneWireMonitor = new DeviceMonitor(m_oneWireAdapter);
// Normal state
            m_oneWireMonitor.setMaxStateCount(5);
// Use for testing
//            m_oneWireMonitor.setMaxStateCount(100);                        
            m_oneWireMonitor.addDeviceMonitorEventListener(this);
            new Thread(m_oneWireMonitor).start();
        } catch (OneWireException e) {
            JMessageDialog.showMessage(this,
                    new MessageInf(MessageInf.SGN_WARNING,
                            AppLocal.getIntString("message.ibuttonnotfound"), e));
        }
    }

    private void shutdownIButtonMonitor() {
        if (m_oneWireMonitor != null) {
            m_oneWireMonitor.killMonitor();
            try {
                m_oneWireAdapter.freePort();
            } catch (OneWireException e) {
//                System.out.println(e);
            }
        }
    }

    public void releaseResources() {
        shutdownIButtonMonitor();
    }

    final static int UNIQUE_KEY_FAMILY = 0x01;

    private boolean isDeviceRelevant(OneWireContainer container) {
        String iButtonId = container.getAddressAsString();
        try {
            if (container.getAdapter().getAdapterAddress().equals(iButtonId)) {
                return false;
            }
        } catch (OneWireException e) {
        }

        int familyNumber = Address.toByteArray(iButtonId)[0];
        return (familyNumber == UNIQUE_KEY_FAMILY);
    }

    /**
     * Called when an iButton is inserted.
     *
     * @param devt
     */
    @Override
    public void deviceArrival(DeviceMonitorEvent devt) {
        assert m_dlSystem != null;

        for (int i = 0; i < devt.getDeviceCount(); i++) {
            OneWireContainer container = devt.getContainerAt(i);
            if (!isDeviceRelevant(container)) {
                continue;
            }

            String iButtonId = devt.getAddressAsStringAt(i);

            AppUser user = null;
            try {
                user = m_dlSystem.findPeopleByCard(iButtonId);
            } catch (BasicException e) {
                if (user == null) {
                    AlertasPersonalizadas.mostrarAlertaInformacion(AppLocal.getIntString("message.ibuttonnotassign"));
                }
            }

            if (user == null) {
                AlertasPersonalizadas.mostrarAlertaInformacion(AppLocal.getIntString("message.ibuttonnotassign"));

            } else {
                setVisible(false);
                openAppView(user);
                setVisible(true);
            }
        }
    }

    /**
     * Called when an iButton is removed.
     *
     * @param devt
     */
    @Override
    public void deviceDeparture(DeviceMonitorEvent devt) {

        for (int i = 0; i < devt.getDeviceCount(); i++) {
            OneWireContainer container = devt.getContainerAt(i);
            if (!isDeviceRelevant(container)) {
                continue;
            }

            String iButtonId = devt.getAddressAsStringAt(i);

            if (m_principalapp != null) {
                AppUser currentUser = m_principalapp.getUser();
                if (currentUser != null && currentUser.getCard().equals(iButtonId)) {
                    closeAppView();
                }
            }
        }
    }

    @Override
    public void networkException(DeviceMonitorException dexc) {
//        System.out.println("ERROR: " + dexc.toString());
    }

    /**
     * @param props
     * @return
     */
    public boolean initApp(AppProperties props) {

        m_props = props;
        m_jPanelDown.setVisible(!(Boolean.valueOf(m_props.getProperty("till.hideinfo"))));
        applyComponentOrientation(ComponentOrientation.getOrientation(Locale.getDefault()));

        try {
            session = AppViewConnection.createSession(m_props);

        } catch (BasicException e) {
            JMessageDialog.showMessage(this, new MessageInf(MessageInf.SGN_DANGER, e.getMessage(), e));
            return false;
        }

        m_dlSystem = (DataLogicSystem) getBean("com.openbravo.pos.forms.DataLogicSystem");
        /**
         * CONDITIONS; if dbversion is null then new createDatabase if dbversion
         * is not null then check the version if appversion equals dbversion
         * then check for updates IF APP_VERSION = existing db version
         *
         */
        String sDBVersion = readDataBaseVersion();
        if (!AppLocal.APP_VERSION.equals(sDBVersion)) {
            String sScript = sDBVersion == null
                    ? m_dlSystem.getInitScript() + "-create.sql"
                    : m_dlSystem.getInitScript() + "-upgrade_master.sql";

            if (JRootApp.class.getResource(sScript) == null) {
                JMessageDialog.showMessage(this, new MessageInf(MessageInf.SGN_DANGER, sDBVersion == null
                        ? AppLocal.getIntString("message.databasenotsupported", session.DB.getName() + " " + sDBVersion)
                        : AppLocal.getIntString("message.noupdatescript")));
                session.close();
                return false;
            } else {
                if (JOptionPane.showConfirmDialog(this,
                        AppLocal.getIntString(sDBVersion == null
                                ? "message.createdatabase"
                                : "message.eolupdate", session.DB.getName() + " " + sDBVersion),
                        AppLocal.getIntString("message.title"),
                        JOptionPane.OK_OPTION,
                        JOptionPane.WARNING_MESSAGE) == JOptionPane.OK_OPTION) {

                    try {
                        BatchSentence bsentence = new BatchSentenceResource(session, sScript);
                        bsentence.putParameter("APP_ID", Matcher.quoteReplacement(AppLocal.APP_ID));
                        bsentence.putParameter("APP_NAME", Matcher.quoteReplacement(AppLocal.APP_NAME));
                        bsentence.putParameter("APP_VERSION", Matcher.quoteReplacement(AppLocal.APP_VERSION));

                        java.util.List l = bsentence.list();

                        if (l.size() > 0) {
                            JMessageDialog.showMessage(this, new MessageInf(MessageInf.SGN_WARNING,
                                    AppLocal.getIntString("database.scriptwarning"),
                                    l.toArray(new Throwable[l.size()])));
                        }
                    } catch (BasicException e) {
                        JMessageDialog.showMessage(this,
                                new MessageInf(MessageInf.SGN_DANGER,
                                        AppLocal.getIntString("database.scripterror"), e));
                        session.close();
                        return false;
                    }
                } else {
                    session.close();
                    return false;
                }
            }
        }

        /*
     * JG 10 Dec 2018
     * Test rig in prep' to track user install/launch success/fails
     * We only need core info' to identify for POC
     * For testing; code and declares are deliberately verbose
     *
     * FUTURE :
     * App' error logging
     * Also auto-Notify users of available app' updates.
     * To be replaced with our REST API
         */
        try {
            sMachine = InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException ex) {
            log.error(ex.getMessage());
        }
// create the filename
        String sUserPath = System.getProperty("user.home");
        String filePath = sUserPath + "/" + sMachine + ".lau";

        Instant machineTimestamp = Instant.now();
        String sContent = sUserPath + ","
                + machineTimestamp + ","
                + AppLocal.APP_ID + ","
                + AppLocal.APP_NAME + ","
                + AppLocal.APP_VERSION + "\n";

        try {
            Files.write(Paths.get(filePath), sContent.getBytes(),
                    StandardOpenOption.APPEND, StandardOpenOption.CREATE);
        } catch (IOException ex) {
            log.error(ex.getMessage());
        }

        try {
            filePath = sUserPath + "/open.db";
            Files.write(Paths.get(filePath), sContent.getBytes(),
                    StandardOpenOption.CREATE);
        } catch (IOException ex) {
            log.error(ex.getMessage());
        }

        m_propsdb = m_dlSystem.getResourceAsProperties(m_props.getHost() + "/properties");

        try {
            String sActiveCashIndex = m_propsdb.getProperty("activecash");
            Object[] valcash = sActiveCashIndex == null
                    ? null
                    : m_dlSystem.findActiveCash(sActiveCashIndex);
            if (valcash == null || !m_props.getHost().equals(valcash[0])) {
                setActiveCash(UUID.randomUUID().toString(),
                        m_dlSystem.getSequenceCash(m_props.getHost()) + 1, new Date(), null);
                m_dlSystem.execInsertCash(
                        new Object[]{getActiveCashIndex(), m_props.getHost(),
                            getActiveCashSequence(),
                            getActiveCashDateStart(),
                            getActiveCashDateEnd()});
            } else {
                setActiveCash(sActiveCashIndex,
                        (Integer) valcash[1],
                        (Date) valcash[2],
                        (Date) valcash[3]);
            }
        } catch (BasicException e) {
            MessageInf msg = new MessageInf(MessageInf.SGN_NOTICE,
                    AppLocal.getIntString("message.cannotclosecash"), e);
            msg.show(this);
            session.close();
            return false;
        }

        m_sInventoryLocation = m_propsdb.getProperty("location");
        if (m_sInventoryLocation == null) {
            m_sInventoryLocation = "0";
            m_propsdb.setProperty("location", m_sInventoryLocation);
            m_dlSystem.setResourceAsProperties(m_props.getHost() + "/properties",
                    m_propsdb);
        }

        m_TP = new DeviceTicket(this, m_props);

        m_TTP = new TicketParser(getDeviceTicket(), m_dlSystem);
        printerStart();

        m_Scale = new DeviceScale(this, m_props);

        m_Scanner = DeviceScannerFactory.createInstance(m_props);

        new javax.swing.Timer(250, new PrintTimeAction()).start();

        String sWareHouse;

        try {
            sWareHouse = m_dlSystem.findLocationName(m_sInventoryLocation);
        } catch (BasicException e) {
            sWareHouse = null;
        }

        String url;
        try {
            url = session.getURL();
        } catch (SQLException e) {
            url = "";
        }
        m_jHost.setText("<html>" + m_props.getHost() + " - " + sWareHouse + "<br>" + url);

        showLogin();

        String ibutton = m_props.getProperty("machine.iButton");
        if (ibutton.equals("true")) {
            initIButtonMonitor();
            uOWWatch.iButtonOn();
        }
        return true;
    }

    private String readDataBaseVersion() {
        try {
            return m_dlSystem.findVersion();
        } catch (BasicException ed) {
            return null;
        }
    }

    public void tryToClose() {

        if (closeAppView()) {
            m_TP.getDeviceDisplay().clearVisor();
            shutdownIButtonMonitor();

// delete the open.db tracking file
            String sUserPath = System.getProperty("user.home");
//                String filePath = sUserPath + "\\open.db";
            String filePath = sUserPath + "/open.db";
            try {
                Files.deleteIfExists(Paths.get(filePath));
            } catch (IOException ex) {
                log.error(ex.getMessage());
            }

            session.close();
            SwingUtilities.getWindowAncestor(this).dispose();
        }
    }

    @Override
    public DeviceTicket getDeviceTicket() {
        return m_TP;
    }

    @Override
    public DeviceScale getDeviceScale() {
        return m_Scale;
    }

    @Override
    public DeviceScanner getDeviceScanner() {
        return m_Scanner;
    }

    @Override
    public Session getSession() {
        return session;
    }

    @Override
    public String getInventoryLocation() {
        return m_sInventoryLocation;
    }

    @Override
    public String getActiveCashIndex() {
        return m_sActiveCashIndex;
    }

    @Override
    public int getActiveCashSequence() {
        return m_iActiveCashSequence;
    }

    @Override
    public Date getActiveCashDateStart() {
        return m_dActiveCashDateStart;
    }

    @Override
    public Date getActiveCashDateEnd() {
        return m_dActiveCashDateEnd;
    }

    @Override
    public void setActiveCash(String sIndex, int iSeq, Date dStart, Date dEnd) {
        m_sActiveCashIndex = sIndex;
        m_iActiveCashSequence = iSeq;
        m_dActiveCashDateStart = dStart;
        m_dActiveCashDateEnd = dEnd;

        m_propsdb.setProperty("activecash", m_sActiveCashIndex);
        m_dlSystem.setResourceAsProperties(m_props.getHost() + "/properties", m_propsdb);
    }

    @Override
    public String getClosedCashIndex() {
        return m_sClosedCashIndex;
    }

    @Override
    public int getClosedCashSequence() {
        return m_iClosedCashSequence;
    }

    @Override
    public Date getClosedCashDateStart() {
        return m_dClosedCashDateStart;
    }

    @Override
    public Date getClosedCashDateEnd() {
        return m_dClosedCashDateEnd;
    }

    @Override
    public void setClosedCash(String sIndex, int iSeq, Date dStart, Date dEnd) {
        m_sClosedCashIndex = sIndex;
        m_iClosedCashSequence = iSeq;
        m_dClosedCashDateStart = dStart;
        m_dClosedCashDateEnd = dEnd;

        m_dlSystem.setResourceAsProperties(m_props.getHost() + "/properties", m_propsdb);
    }

    @Override
    public AppProperties getProperties() {
        return m_props;
    }

    @Override
    public Object getBean(String beanfactory) throws BeanFactoryException {

        beanfactory = mapNewClass(beanfactory);
        BeanFactory bf = m_aBeanFactories.get(beanfactory);

        if (bf == null) {

            if (beanfactory.startsWith("/")) {
                bf = new BeanFactoryScript(beanfactory);
            } else {
                try {
                    Class bfclass = Class.forName(beanfactory);

                    if (BeanFactory.class.isAssignableFrom(bfclass)) {
                        bf = (BeanFactory) bfclass.newInstance();
                    } else {
                        Constructor constMyView = bfclass.getConstructor(new Class[]{AppView.class});
                        Object bean = constMyView.newInstance(new Object[]{this});
                        bf = new BeanFactoryObj(bean);
                    }

                } catch (ClassNotFoundException | InstantiationException
                        | IllegalAccessException | NoSuchMethodException
                        | SecurityException | IllegalArgumentException | InvocationTargetException e) {
                    throw new BeanFactoryException(e);
                }
            }

            m_aBeanFactories.put(beanfactory, bf);

            if (bf instanceof BeanFactoryApp) {
                ((BeanFactoryApp) bf).init(this);
            }
        }
        return bf.getBean();
    }

    private static String mapNewClass(String classname) {
        String newclass = m_oldclasses.get(classname);
        return newclass == null
                ? classname
                : newclass;
    }

    private static void initOldClasses() {

        m_oldclasses = new HashMap<>();

        m_oldclasses.put("com.openbravo.pos.reports.JReportCustomers", "/com/openbravo/reports/customers.bs");
        m_oldclasses.put("com.openbravo.pos.reports.JReportCustomersB", "/com/openbravo/reports/customersb.bs");
        m_oldclasses.put("com.openbravo.pos.reports.JReportClosedPos", "/com/openbravo/reports/closedpos.bs");
        m_oldclasses.put("com.openbravo.pos.reports.JReportClosedProducts", "/com/openbravo/reports/closedproducts.bs");
        m_oldclasses.put("com.openbravo.pos.reports.JChartSales", "/com/openbravo/reports/chartsales.bs");
        m_oldclasses.put("com.openbravo.pos.reports.JReportInventory", "/com/openbravo/reports/inventory.bs");
        m_oldclasses.put("com.openbravo.pos.reports.JReportInventory2", "/com/openbravo/reports/inventoryb.bs");
        m_oldclasses.put("com.openbravo.pos.reports.JReportInventoryBroken", "/com/openbravo/reports/inventorybroken.bs");
        m_oldclasses.put("com.openbravo.pos.reports.JReportInventoryDiff", "/com/openbravo/reports/inventorydiff.bs");
        m_oldclasses.put("com.openbravo.pos.reports.JReportPeople", "/com/openbravo/reports/people.bs");
        m_oldclasses.put("com.openbravo.pos.reports.JReportTaxes", "/com/openbravo/reports/taxes.bs");
        m_oldclasses.put("com.openbravo.pos.reports.JReportUserSales", "/com/openbravo/reports/usersales.bs");
        m_oldclasses.put("com.openbravo.pos.reports.JReportProducts", "/com/openbravo/reports/products.bs");
        m_oldclasses.put("com.openbravo.pos.reports.JReportCatalog", "/com/openbravo/reports/productscatalog.bs");

        m_oldclasses.put("com.openbravo.pos.panels.JPanelTax", "com.openbravo.pos.inventory.TaxPanel");

    }

    @Override
    public void waitCursorBegin() {
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
    }

    @Override
    public void waitCursorEnd() {
        setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    }

    @Override
    public AppUserView getAppUserView() {
        return m_principalapp;
    }

    private void printerStart() {

        String sresource = m_dlSystem.getResourceAsXML("Printer.Start");
        if (sresource == null) {
            m_TP.getDeviceDisplay().writeVisor(AppLocal.APP_NAME, AppLocal.APP_VERSION);
        } else {
            try {
                m_TTP.printTicket(sresource);
            } catch (TicketPrinterException eTP) {
                m_TP.getDeviceDisplay().writeVisor(AppLocal.APP_NAME, AppLocal.APP_VERSION);
            }
        }

    }

    private Set<String> nombresAgregados = new HashSet<>();

    private void listPeople() {
        try {
            java.util.List people = m_dlSystem.listPeopleVisible();

            for (Object people1 : people) {
                AppUser user = (AppUser) people1;

                // Agregar nombre al JComboBox comboRol si no está ya agregado
                String nombre = cambiarNombreBoton(user.getUserInfo().getName());
                if (!nombresAgregados.contains(nombre)) {
                    comboRol.addItem(nombre);

                    // Agregar el mapeo entre el nombre y la instancia de AppUser al HashMap
                    userMap.put(nombre, user);

                    // Agregar el nombre al conjunto para evitar duplicados
                    nombresAgregados.add(nombre);
                }
            }

        } catch (BasicException ee) {
            // Manejar la excepción según sea necesario
        }
    }

    private String cambiarNombreBoton(String nombre) {
        switch (nombre) {
            case "Administrator":
                return "Harrison";
            case "Employee":
                return "Empleado";
            case "Guest":
                return "Invitado";
            case "Manager":
                return "Gerente";
            default:
                return nombre;
        }
    }

    class AppUserAction extends AbstractAction {

        private final AppUser m_actionuser;

        public AppUserAction(AppUser user) {
            m_actionuser = user;
            putValue(Action.SMALL_ICON, m_actionuser.getIcon());
            putValue(Action.NAME, m_actionuser.getName());
        }

        public AppUser getUser() {
            return m_actionuser;
        }

        @Override
        public void actionPerformed(ActionEvent evt) {

            if (m_actionuser.authenticate()) {
                openAppView(m_actionuser);
            } else {
                String sPassword = JPasswordDialog.showEditPassword(JRootApp.this,
                        AppLocal.getIntString("label.Password"),
                        m_actionuser.getName(),
                        m_actionuser.getIcon());
                if (sPassword != null) {

                    if (m_actionuser.authenticate(sPassword)) {
                        openAppView(m_actionuser);
                    } else {
                        MessageInf msg = new MessageInf(MessageInf.SGN_WARNING,
                                AppLocal.getIntString("message.BadPassword"));
                        msg.show(JRootApp.this);
                    }
                }
            }
        }
    }

    private void showView(String view) {

        CardLayout cl = (CardLayout) (m_jPanelContainer.getLayout());
        cl.show(m_jPanelContainer, view);

    }

 private void openAppView(AppUser user) {

    if (closeAppView()) {
        m_principalapp = new JPrincipalApp(this, user);

        jPanel3.add(m_principalapp.getNotificator());
        jPanel3.revalidate();

        m_jPanelContainer.add(m_principalapp, "_" + m_principalapp.getUser().getId());
        showView("_" + m_principalapp.getUser().getId());

        m_principalapp.activate();
        JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);
        Dimension previousSizes = frame.getSize();
        frame.setSize(930,505);
        if (frame != null) {
            frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        }
        previousSize = new Dimension(previousSizes);
    }
}

    public void exitToLogin() {
        closeAppView();
        showLogin();
    }


public boolean closeAppView() {
    if (m_principalapp == null) {
        return true;
    } else if (!m_principalapp.deactivate()) {
        return false;
    } else {
        jPanel3.remove(m_principalapp.getNotificator());
        jPanel3.revalidate();
        jPanel3.repaint();

        m_jPanelContainer.remove(m_principalapp);
        m_principalapp = null;

        JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);

        Dimension previousSizes = previousSize;
        if (previousSizes != null && frame != null) {
            frame.setSize(previousSizes);
              frame.setLocationRelativeTo(null);
        }

        if (frame != null) {
            frame.setExtendedState(JFrame.NORMAL);
        }

        showLogin();

        return true;
    }
}

    private void showLogin() {

        listPeople();
        showView("login");

        printerStart();

        inputtext = new StringBuilder();

    }

    private void processKey(char c) {

        if ((c == '\n') || (c == '?')) {
            AppUser user = null;
            try {
                user = m_dlSystem.findPeopleByCard(inputtext.toString());
            } catch (BasicException e) {
            }

            if (user == null) {
                MessageInf msg = new MessageInf(MessageInf.SGN_WARNING,
                        AppLocal.getIntString("message.nocard"));
                msg.show(this);
            } else {
                openAppView(user);
            }

            inputtext = new StringBuilder();
        } else {
            inputtext.append(c);
        }
    }

    private int getProgressBar() {
        int rate = serverMonitor.getValue();
        return rate;
    }

    private boolean pingServer() throws UnknownHostException {
        /*
     * This method is for the future. Connects and will include both servers + backup server
     * Tested locally on JG machine and unicenta-server
         */
        serverMonitor.setString("Checking...");

        InetAddress addr = InetAddress.getByName(AppLocal.getIntString("db.ip"));
        int port = 3306;

        SocketAddress sockaddr = new InetSocketAddress(addr, port);
        Socket sock = new Socket();
        try {
            sock.connect(sockaddr, 2000);
            serverMonitor.setString("Server is alive!");
            serverMonitor.setValue(0);
            return true;
        } catch (IOException ex) {
            log.error(ex.getMessage());
            return false;
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the FormEditor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        m_jPanelContainer = new javax.swing.JPanel();
        m_jPanelLogin = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        filler2 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 10), new java.awt.Dimension(32767, 0));
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        comboRol = new javax.swing.JComboBox<>();
        btnIniciar = new javax.swing.JButton();
        jLabel6 = new javax.swing.JLabel();
        jPanel5 = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        m_jPanelDown = new javax.swing.JPanel();
        panelTask = new javax.swing.JPanel();
        m_jHost = new javax.swing.JLabel();
        webMemoryBar1 = new com.alee.extended.statusbar.WebMemoryBar();
        serverMonitor = new com.alee.laf.progressbar.WebProgressBar();
        jPanel3 = new javax.swing.JPanel();

        setEnabled(false);
        setPreferredSize(new java.awt.Dimension(903, 500));
        setLayout(new java.awt.BorderLayout());

        m_jPanelContainer.setBackground(new java.awt.Color(255, 255, 255));
        m_jPanelContainer.setLayout(new java.awt.CardLayout());

        m_jPanelLogin.setLayout(new java.awt.BorderLayout());

        jPanel4.setBackground(new java.awt.Color(255, 255, 255));
        jPanel4.setLayout(null);
        jPanel4.add(filler2);
        filler2.setBounds(0, 0, 724, 0);

        jPanel1.setBackground(new java.awt.Color(0, 102, 255));
        jPanel1.setLayout(null);

        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/interPos.jpg"))); // NOI18N
        jPanel1.add(jLabel1);
        jLabel1.setBounds(120, 100, 200, 200);

        jLabel3.setBackground(new java.awt.Color(255, 255, 255));
        jLabel3.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(255, 255, 255));
        jLabel3.setText(" ¡Bienvenido a InterPos - Tu Punto de Venta Amigable!");
        jPanel1.add(jLabel3);
        jLabel3.setBounds(50, 40, 390, 40);

        jLabel5.setBackground(new java.awt.Color(255, 255, 255));
        jLabel5.setFont(new java.awt.Font("Tahoma", 1, 10)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(255, 255, 255));
        jLabel5.setText("¡InterPos hace que la gestión de tu punto de venta");
        jPanel1.add(jLabel5);
        jLabel5.setBounds(100, 310, 270, 40);

        jLabel2.setFont(new java.awt.Font("Arial", 1, 16)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(102, 102, 102));
        jLabel2.setPreferredSize(new java.awt.Dimension(180, 34));
        jPanel1.add(jLabel2);
        jLabel2.setBounds(0, 600, 180, 39);

        jLabel4.setFont(new java.awt.Font("Tahoma", 1, 10)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(255, 255, 255));
        jLabel4.setText(" ventajas de nuestro software hoy mismo.");
        jPanel1.add(jLabel4);
        jLabel4.setBounds(110, 360, 240, 30);

        jLabel8.setFont(new java.awt.Font("Tahoma", 1, 10)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(255, 255, 255));
        jLabel8.setText("sea fácil y divertida! Empieza a disfrutar de las");
        jPanel1.add(jLabel8);
        jLabel8.setBounds(110, 350, 233, 13);

        jLabel11.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel11.setForeground(new java.awt.Color(255, 255, 255));
        jLabel11.setText("Soporte Técnico:    3203568200");
        jPanel1.add(jLabel11);
        jLabel11.setBounds(110, 420, 270, 17);

        jLabel12.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel12.setForeground(new java.awt.Color(255, 255, 255));
        jLabel12.setText("Ventas:                    3227631065");
        jPanel1.add(jLabel12);
        jLabel12.setBounds(110, 440, 230, 17);

        jLabel13.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel13.setForeground(new java.awt.Color(255, 255, 255));
        jLabel13.setText("Contacto:");
        jPanel1.add(jLabel13);
        jLabel13.setBounds(110, 400, 140, 17);

        jLabel14.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/openbravo/images/llamada.png"))); // NOI18N
        jPanel1.add(jLabel14);
        jLabel14.setBounds(70, 430, 40, 30);

        jPanel4.add(jPanel1);
        jPanel1.setBounds(0, 0, 460, 480);

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));
        jPanel2.setLayout(null);

        jPanel2.add(comboRol);
        comboRol.setBounds(120, 190, 290, 30);

        btnIniciar.setBackground(new java.awt.Color(255, 255, 255));
        btnIniciar.setForeground(new java.awt.Color(0, 0, 204));
        btnIniciar.setText("Iniciar");
        btnIniciar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnIniciarActionPerformed(evt);
            }
        });
        jPanel2.add(btnIniciar);
        btnIniciar.setBounds(120, 250, 290, 30);

        jLabel6.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/rol.png"))); // NOI18N
        jPanel2.add(jLabel6);
        jLabel6.setBounds(70, 180, 32, 50);

        jPanel5.setBackground(new java.awt.Color(245, 245, 245));

        jLabel7.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(0, 51, 255));
        jLabel7.setText("Iniciar Sesion");

        org.jdesktop.layout.GroupLayout jPanel5Layout = new org.jdesktop.layout.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel5Layout.createSequentialGroup()
                .addContainerGap(195, Short.MAX_VALUE)
                .add(jLabel7, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 90, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(135, 135, 135))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel5Layout.createSequentialGroup()
                .add(19, 19, 19)
                .add(jLabel7, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 30, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(171, Short.MAX_VALUE))
        );

        jPanel2.add(jPanel5);
        jPanel5.setBounds(20, 130, 420, 220);

        jPanel4.add(jPanel2);
        jPanel2.setBounds(460, -20, 480, 680);

        m_jPanelLogin.add(jPanel4, java.awt.BorderLayout.CENTER);

        m_jPanelContainer.add(m_jPanelLogin, "login");

        add(m_jPanelContainer, java.awt.BorderLayout.CENTER);

        m_jPanelDown.setBorder(javax.swing.BorderFactory.createMatteBorder(1, 0, 0, 0, javax.swing.UIManager.getDefaults().getColor("Button.darkShadow")));
        m_jPanelDown.setLayout(new java.awt.BorderLayout());

        m_jHost.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        m_jHost.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/openbravo/images/display.png"))); // NOI18N
        m_jHost.setText("*Hostname");
        panelTask.add(m_jHost);

        webMemoryBar1.setBackground(new java.awt.Color(153, 153, 153));
        webMemoryBar1.setText("");
        webMemoryBar1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        webMemoryBar1.setPreferredSize(new java.awt.Dimension(150, 30));
        webMemoryBar1.setUsedBorderColor(new java.awt.Color(0, 204, 204));
        webMemoryBar1.setUsedFillColor(new java.awt.Color(0, 204, 255));
        panelTask.add(webMemoryBar1);

        serverMonitor.setToolTipText("");
        serverMonitor.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        serverMonitor.setMaximumSize(new java.awt.Dimension(50, 18));
        serverMonitor.setPreferredSize(new java.awt.Dimension(150, 30));
        serverMonitor.setProgressBottomColor(new java.awt.Color(76, 197, 237));
        serverMonitor.setRound(2);
        serverMonitor.setString("Keep Alive");
        serverMonitor.setStringPainted(true);
        panelTask.add(serverMonitor);

        m_jPanelDown.add(panelTask, java.awt.BorderLayout.LINE_START);
        m_jPanelDown.add(jPanel3, java.awt.BorderLayout.LINE_END);

        add(m_jPanelDown, java.awt.BorderLayout.SOUTH);
    }// </editor-fold>//GEN-END:initComponents

    private void btnIniciarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnIniciarActionPerformed
        String nombreSeleccionado = (String) comboRol.getSelectedItem();
        AppUser userSeleccionado = userMap.get(nombreSeleccionado);

        // Crear y ejecutar la acción AppUserAction
        AppUserAction action = new AppUserAction(userSeleccionado);
        action.actionPerformed(evt);
    }//GEN-LAST:event_btnIniciarActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnIniciar;
    private javax.swing.JComboBox<String> comboRol;
    private javax.swing.Box.Filler filler2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JLabel m_jHost;
    private javax.swing.JPanel m_jPanelContainer;
    private javax.swing.JPanel m_jPanelDown;
    private javax.swing.JPanel m_jPanelLogin;
    private javax.swing.JPanel panelTask;
    private com.alee.laf.progressbar.WebProgressBar serverMonitor;
    private com.alee.extended.statusbar.WebMemoryBar webMemoryBar1;
    // End of variables declaration//GEN-END:variables
}
