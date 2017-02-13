//    uniCenta oPOS Touch Friendly Point of Sale
//    Copyright (c) 2009-2015 uniCenta & previous Openbravo POS works
//    http://www.unicenta.net/product
//
//    This file is part of uniCenta oPOS.
//
//    uniCenta oPOS is free software: you can redistribute it and/or modify
//    it under the terms of the GNU General Public License as published by
//    the Free Software Foundation, either version 3 of the License, or
//    (at your option) any later version.
//
//    uniCenta oPOS is distributed in the hope that it will be useful,
//    but WITHOUT ANY WARRANTY; without even the implied warranty of
//    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//    GNU General Public License for more details.
//
//    You should have received a copy of the GNU General Public License
//    along with Openbravo POS.  If not, see <http://www.gnu.org/licenses/>.
package com.openbravo.pos.forms;

import com.openbravo.basic.BasicException;
import com.openbravo.data.gui.JMessageDialog;
import com.openbravo.data.gui.MessageInf;
import com.openbravo.pos.customers.CustomerInfo;
import com.openbravo.pos.scripting.ScriptException;
import com.openbravo.pos.util.Hashcypher;
import com.openbravo.pos.util.StringUtils;
import java.awt.CardLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import org.jdesktop.swingx.JXTaskPane;
import org.jdesktop.swingx.JXTaskPaneContainer;

/**
 *
 * @author adrianromero
 */
public class JPrincipalApp extends javax.swing.JPanel implements AppUserView {

    private static final Logger logger = Logger.getLogger("com.openbravo.pos.forms.JPrincipalApp");

    private final JRootApp m_appview;
    private final AppUser m_appuser;

    private DataLogicSystem m_dlSystem;

    private JLabel m_principalnotificator;

    private JPanelView m_jLastView;
    private Action m_actionfirst;

    private Map<String, JPanelView> m_aPreparedViews; // Prepared views
    private Map<String, JPanelView> m_aCreatedViews;

    private Icon menu_open;
    private Icon menu_close;

    //HS Updates
    private CustomerInfo customerInfo;

    /**
     * Creates new form JPrincipalApp
     *
     * @param appview
     * @param appuser
     */
    public JPrincipalApp(JRootApp appview, AppUser appuser) {

        m_appview = appview;
        m_appuser = appuser;

        m_dlSystem = (DataLogicSystem) m_appview.getBean("com.openbravo.pos.forms.DataLogicSystem");

        m_appuser.fillPermissions(m_dlSystem);

        m_actionfirst = null;
        m_jLastView = null;

// JG 6 May 2013 use diamond inference
        m_aPreparedViews = new HashMap<>();
        m_aCreatedViews = new HashMap<>();

        initComponents();

        jPanel2.add(Box.createVerticalStrut(50), 0);

        applyComponentOrientation(appview.getComponentOrientation());

        m_principalnotificator = new JLabel();
        m_principalnotificator.applyComponentOrientation(getComponentOrientation());
        m_principalnotificator.setText(m_appuser.getName());
        m_principalnotificator.setIcon(m_appuser.getIcon());
        m_principalnotificator.setName("active-user");
        m_principalnotificator.setFont(new java.awt.Font("Saysettha OT", 0, 18)); // NOI18N

        if (jButton1.getComponentOrientation().isLeftToRight()) {
            menu_open = new javax.swing.ImageIcon(getClass().getResource("/com/openbravo/images/menu-right.png"));
            menu_close = new javax.swing.ImageIcon(getClass().getResource("/com/openbravo/images/menu-left.png"));
        } else {
            menu_open = new javax.swing.ImageIcon(getClass().getResource("/com/openbravo/images/menu-left.png"));
            menu_close = new javax.swing.ImageIcon(getClass().getResource("/com/openbravo/images/menu-right.png"));
        }
        assignMenuButtonIcon();

        m_jPanelTitle.setVisible(false);

        m_jPanelContainer.add(new JPanel(), "<NULL>");
        showView("<NULL>");

        try {
            m_jPanelLeft.setViewportView(getScriptMenu(m_dlSystem.getResourceAsText("Menu.Root")));
        } catch (ScriptException e) {
            logger.log(Level.SEVERE, "Cannot read Menu.Root resource. Trying default menu.", e);
            try {
                m_jPanelLeft.setViewportView(getScriptMenu(StringUtils.readResource("/com/openbravo/pos/templates/Menu.Root.txt")));
            } catch (IOException | ScriptException ex) {
                logger.log(Level.SEVERE, "Cannot read default menu", ex);
            }
        }
    }

    private Component getScriptMenu(String menutext) throws ScriptException {

        ScriptMenu menu = new ScriptMenu();
        ScriptGroup group;

        group = menu.addGroup("Menu.Main");
        group.addPanel("/com/openbravo/images/sale.png", "Menu.Ticket", "com.openbravo.pos.sales.JPanelTicketSales");
        group.addPanel("/com/openbravo/images/saleedit.png", "Menu.TicketEdit", "com.openbravo.pos.sales.JPanelTicketEdits");
        group.addPanel("/com/openbravo/images/customerpay.png", "Menu.CustomersPayment", "com.openbravo.pos.customers.CustomersPayment");
        group.addPanel("/com/openbravo/images/payments.png", "Menu.Payments", "com.openbravo.pos.panels.JPanelPayments");
        group.addPanel("/com/openbravo/images/calculator.png", "Menu.CloseTPV", "com.openbravo.pos.panels.JPanelCloseMoney");

        group = menu.addGroup("Menu.Backoffice");
        ScriptSubmenu submenu = group.addSubmenu("/com/openbravo/images/customer.png", "Menu.Customers", "com.openbravo.pos.forms.MenuCustomers");
        submenu.addTitle("Menu.Customers");
        submenu.addPanel("/com/openbravo/images/customer.png", "Menu.CustomersManagement", "com.openbravo.pos.customers.CustomersPanel");

        submenu.addTitle("Menu.Customers.Reports");
        submenu.addPanel("/com/openbravo/images/reports.png", "Menu.CustomersReport", "/com/openbravo/reports/customers.bs");
        submenu.addPanel("/com/openbravo/images/reports.png", "Menu.CustomersBReport", "/com/openbravo/reports/customersb.bs");
        submenu.addPanel("/com/openbravo/images/reports.png", "Menu.CustomersDebtors", "/com/openbravo/reports/customersdebtors.bs");
        submenu.addPanel("/com/openbravo/images/reports.png", "Menu.CustomersDiary", "/com/openbravo/reports/customersdiary.bs");
        submenu.addPanel("/com/openbravo/images/reports.png", "Menu.CustomersList", "/com/openbravo/reports/customers_list.bs");
        submenu.addPanel("/com/openbravo/images/reports.png", "Menu.SalesByCustomer", "/com/openbravo/reports/salebycustomer.bs");

        submenu = group.addSubmenu("/com/openbravo/images/products.png", "Menu.StockManagement", "com.openbravo.pos.forms.MenuStockManagement");
        submenu.addTitle("Menu.StockManagement.Edit");
        submenu.addPanel("/com/openbravo/images/products.png", "Menu.Products", "com.openbravo.pos.inventory.ProductsPanel");
        submenu.addPanel("/com/openbravo/images/category.png", "Menu.Categories", "com.openbravo.pos.inventory.CategoriesPanel");
        submenu.addPanel("/com/openbravo/images/location.png", "Menu.ProductsWarehouse", "com.openbravo.pos.inventory.ProductsWarehousePanel");
        submenu.addPanel("/com/openbravo/images/auxiliary.png", "Menu.Auxiliar", "com.openbravo.pos.inventory.AuxiliarPanel");
        submenu.addPanel("/com/openbravo/images/attributes.png", "Menu.Attributes", "com.openbravo.pos.inventory.AttributesPanel");
        submenu.addPanel("/com/openbravo/images/attributes.png", "Menu.AttributeValues", "com.openbravo.pos.inventory.AttributeValuesPanel");
        submenu.addPanel("/com/openbravo/images/attributes.png", "Menu.AttributeSets", "com.openbravo.pos.inventory.AttributeSetsPanel");
        submenu.addPanel("/com/openbravo/images/attributes.png", "Menu.AttributeUse", "com.openbravo.pos.inventory.AttributeUsePanel");
        submenu.addPanel("/com/openbravo/images/stockdiary.png", "Menu.StockDiary", "com.openbravo.pos.inventory.StockDiaryPanel");
        submenu.addPanel("/com/openbravo/images/stockmaint.png", "Menu.StockMovement", "com.openbravo.pos.inventory.StockManagement");

        submenu.addTitle("Menu.StockManagement.Reports");
        submenu.addPanel("/com/openbravo/images/reports.png", "Menu.BarcodeSheet", "/com/openbravo/reports/barcodesheet.bs");
        submenu.addPanel("/com/openbravo/images/reports.png", "Menu.Inventory", "/com/openbravo/reports/inventory.bs");
        submenu.addPanel("/com/openbravo/images/reports.png", "Menu.Inventory2", "/com/openbravo/reports/inventoryb.bs");
        submenu.addPanel("/com/openbravo/images/reports.png", "Menu.InventoryBroken", "/com/openbravo/reports/inventorybroken.bs");
        submenu.addPanel("/com/openbravo/images/reports.png", "Menu.InventoryDiff", "/com/openbravo/reports/inventorydiff.bs");
        submenu.addPanel("/com/openbravo/images/reports.png", "Menu.InventoryDiffDetail", "/com/openbravo/reports/inventorydiffdetail.bs");
        submenu.addPanel("/com/openbravo/images/reports.png", "Menu.InventoryListDetail", "/com/openbravo/reports/inventorylistdetail.bs");
        submenu.addPanel("/com/openbravo/images/reports.png", "Menu.ProductCatalog", "/com/openbravo/reports/productscatalog.bs");
        submenu.addPanel("/com/openbravo/images/reports.png", "Menu.Products", "/com/openbravo/reports/products.bs");
        submenu.addPanel("/com/openbravo/images/reports.png", "Menu.ProductLabels", "/com/openbravo/reports/productlabels.bs");
        submenu.addPanel("/com/openbravo/images/reports.png", "Menu.SaleCatalog", "/com/openbravo/reports/salecatalog.bs");
        submenu.addPanel("/com/openbravo/images/reports.png", "Menu.ShelfEdgeLabels", "/com/openbravo/reports/shelfedgelabels.bs");

        submenu = group.addSubmenu("/com/openbravo/images/sales.png", "Menu.SalesManagement", "com.openbravo.pos.forms.MenuSalesManagement");
        submenu.addTitle("Menu.SalesManagement.Reports");
        submenu.addPanel("/com/openbravo/images/reports.png", "Menu.Closing", "/com/openbravo/reports/closedpos.bs");
        submenu.addPanel("/com/openbravo/images/reports.png", "Menu.Closing1", "/com/openbravo/reports/closedpos_1.bs");
        submenu.addPanel("/com/openbravo/images/reports.png", "Menu.CashRegisterLog", "/com/openbravo/reports/cashregisterlog.bs");
        submenu.addPanel("/com/openbravo/images/reports.png", "Menu.ExtendedCashRegisterLog", "/com/openbravo/reports/extendedcashregisterlog.bs");
        submenu.addPanel("/com/openbravo/images/reports.png", "Menu.CashFlow", "/com/openbravo/reports/cashflow.bs");
        submenu.addPanel("/com/openbravo/images/reports.png", "Menu.PaymentReport", "/com/openbravo/reports/paymentreport.bs");
        submenu.addPanel("/com/openbravo/images/reports.png", "Menu.CategorySales", "/com/openbravo/reports/categorysales.bs");
        submenu.addPanel("/com/openbravo/images/reports.png", "Menu.ClosedProducts", "/com/openbravo/reports/closedproducts.bs");
        submenu.addPanel("/com/openbravo/images/reports.png", "Menu.ClosedProducts1", "/com/openbravo/reports/closedproducts_1.bs");
        submenu.addPanel("/com/openbravo/images/reports.png", "Menu.ExtendedByProducts", "/com/openbravo/reports/extproducts.bs");
        submenu.addPanel("/com/openbravo/images/reports.png", "Menu.SalesProfit", "/com/openbravo/reports/productsalesprofit.bs");

        submenu.addPanel("/com/openbravo/images/reports.png", "Menu.SaleTaxes", "/com/openbravo/reports/saletaxes.bs");

        submenu.addPanel("/com/openbravo/images/reports.png", "Menu.TaxCatSales", "/com/openbravo/reports/taxcatsales.bs");
        submenu.addPanel("/com/openbravo/images/reports.png", "Menu.ReportTaxes", "/com/openbravo/reports/taxes.bs");

        submenu.addTitle("Menu.SalesManagement.Charts");
        submenu.addPanel("/com/openbravo/images/chart.png", "Menu.ProductSales", "/com/openbravo/reports/productsales.bs");
        submenu.addPanel("/com/openbravo/images/chart.png", "Menu.ProductCategorySalesPieChart", "/com/openbravo/reports/piesalescat.bs");
        submenu.addPanel("/com/openbravo/images/chart.png", "Menu.SalesChart", "/com/openbravo/reports/chartsales.bs");
        submenu.addPanel("/com/openbravo/images/chart.png", "Menu.TimeSeriesProduct", "/com/openbravo/reports/timeseriesproduct.bs");
        submenu.addPanel("/com/openbravo/images/chart.png", "Menu.Top10Sales", "/com/openbravo/reports/top10sales.bs");
        submenu.addPanel("/com/openbravo/images/chart.png", "Menu.Top10Sales", "/com/openbravo/reports/top10salesMySQL.bs");

        submenu = group.addSubmenu("/com/openbravo/images/maintain.png", "Menu.Maintenance", "com.openbravo.pos.forms.MenuMaintenance");
        submenu.addTitle("Menu.Maintenance.POS");
        submenu.addPanel("/com/openbravo/images/user.png", "Menu.Users", "com.openbravo.pos.admin.PeoplePanel");
        submenu.addPanel("/com/openbravo/images/roles.png", "Menu.Roles", "com.openbravo.pos.admin.RolesPanel");
        submenu.addPanel("/com/openbravo/images/bookmark.png", "Menu.Taxes", "com.openbravo.pos.inventory.TaxPanel");
        submenu.addPanel("/com/openbravo/images/bookmark.png", "Menu.TaxCategories", "com.openbravo.pos.inventory.TaxCategoriesPanel");
        submenu.addPanel("/com/openbravo/images/bookmark.png", "Menu.TaxCustCategories", "com.openbravo.pos.inventory.TaxCustCategoriesPanel");
        submenu.addPanel("/com/openbravo/images/resources.png", "Menu.Resources", "com.openbravo.pos.admin.ResourcesPanel");
        submenu.addPanel("/com/openbravo/images/location.png", "Menu.Locations", "com.openbravo.pos.inventory.LocationsPanel");
        submenu.addPanel("/com/openbravo/images/floors.png", "Menu.Floors", "com.openbravo.pos.mant.JPanelFloors");
        submenu.addPanel("/com/openbravo/images/tables.png", "Menu.Tables", "com.openbravo.pos.mant.JPanelPlaces");

        submenu.addTitle("Menu.Maintenance.Reports");
        submenu.addPanel("/com/openbravo/images/reports.png", "Menu.UsersReport", "/com/openbravo/reports/people.bs");
        submenu.addPanel("/com/openbravo/images/reports.png", "Menu.UserSells", "/com/openbravo/reports/usersales.bs");

// EPM
//** Reporting only tested with MySQL.  Using with Derby (Default) DB will result in errors
        submenu = group.addSubmenu("/com/openbravo/images/users.png", "Menu.PresenceManagement", "com.openbravo.pos.forms.MenuEmployees");
        submenu.addTitle("Menu.PresenceManagement");
        submenu.addPanel("/com/openbravo/images/coffee.png", "Menu.Breaks", "com.openbravo.pos.epm.BreaksPanel");
        submenu.addPanel("/com/openbravo/images/leaves.png", "Menu.Leaves", "com.openbravo.pos.epm.LeavesPanel");
        submenu.addTitle("Menu.Employees.Reports");
// Not Derby        submenu.addPanel("/com/openbravo/images/reports.png", "Menu.DailyPresenceReport", "/com/openbravo/reports/dailypresencereport.bs");
// Not Derby        submenu.addPanel("/com/openbravo/images/reports.png", "Menu.DailyScheduleReport", "/com/openbravo/reports/dailyschedulereport.bs");
//        submenu.addPanel("/com/openbravo/images/reports.png", "Menu.PerformanceReport", "/com/openbravo/reports/performancereport.bs");

        group = menu.addGroup("Menu.Utilities");
        submenu = group.addSubmenu("/com/openbravo/images/utilities.png", "Menu.Tools", "com.openbravo.pos.imports.JPanelCSV");
        submenu.addTitle("Menu.Import");
        submenu.addPanel("/com/openbravo/images/import.png", "Menu.CSVImport", "com.openbravo.pos.imports.JPanelCSVImport");
        submenu.addPanel("/com/openbravo/images/database.png", "Menu.CSVReset", "com.openbravo.pos.imports.JPanelCSVCleardb");
        submenu.addTitle("Menu.Import.Reports");
        submenu.addPanel("/com/openbravo/images/reports.png", "Menu.UpdatedPrices", "/com/openbravo/reports/updatedprices.bs");
        submenu.addPanel("/com/openbravo/images/reports.png", "Menu.NewProducts", "/com/openbravo/reports/newproducts.bs");
        submenu.addPanel("/com/openbravo/images/reports.png", "Menu.MissingData", "/com/openbravo/reports/missingdata.bs");
        submenu.addPanel("/com/openbravo/images/reports.png", "Menu.InvalidData", "/com/openbravo/reports/invaliddata.bs");

        group = menu.addGroup("Menu.System");
        group.addChangePasswordAction();
        group.addPanel("/com/openbravo/images/configuration.png", "Menu.Configuration", "com.openbravo.pos.config.JPanelConfiguration");
        group.addPanel("/com/openbravo/images/printer.png", "Menu.Printer", "com.openbravo.pos.panels.JPanelPrinter");
        group.addPanel("/com/openbravo/images/timer.png", "Menu.CheckInCheckOut", "com.openbravo.pos.epm.JPanelEmployeePresence");

// Deprecated options. Only required with Openbravo ERP software
//       submenu.addTitle("Menu.Maintenance.ERP");
//       submenu.addExecution("/com/openbravo/images/openbravo.png", "Menu.ERPProducts", "com.openbravo.possync.ProductsSyncCreate");
//       submenu.addExecution("/com/openbravo/images/openbravo.png", "Menu.ERPOrders", "com.openbravo.possync.OrdersSyncCreate");
        group.addExitAction();

        /*
		ScriptEngine eng = ScriptFactory.getScriptEngine(ScriptFactory.BEANSHELL);
		eng.put("menu", menu);
		eng.eval(menutext);
         */
        return menu.getTaskPane();
    }

    private void assignMenuButtonIcon() {
        jButton1.setIcon(m_jPanelLeft.isVisible()
            ? menu_close
            : menu_open);
    }

    /**
     *
     */
    public class ScriptMenu {
//        private JTaskPane taskPane = new JTaskPane();

        private final JXTaskPaneContainer taskPane;

        private ScriptMenu() {
            taskPane = new JXTaskPaneContainer();
            taskPane.applyComponentOrientation(getComponentOrientation());
        }

        /**
         *
         * @param key
         * @return
         */
        public ScriptGroup addGroup(String key) {

            ScriptGroup group = new ScriptGroup(key);
            taskPane.add(group.getTaskGroup());
            return group;
        }

//        public JTaskPane getTaskPane() {
        /**
         *
         * @return
         */
        public JXTaskPaneContainer getTaskPane() {
            return taskPane;
        }
    }

    /**
     *
     */
    public class ScriptGroup {
//        private JTaskPaneGroup taskGroup;

        private final JXTaskPane taskGroup;

        private ScriptGroup(String key) {
//            taskGroup = new JTaskPaneGroup();
            taskGroup = new JXTaskPane();
            taskGroup.applyComponentOrientation(getComponentOrientation());
            taskGroup.setFocusable(false);
            taskGroup.setRequestFocusEnabled(false);
            taskGroup.setTitle(AppLocal.getIntString(key));
            taskGroup.setVisible(false); // Only groups with sons are visible.
        }

        /**
         *
         * @param icon
         * @param key
         * @param classname
         */
        public void addPanel(String icon, String key, String classname) {
            addAction(new MenuPanelAction(m_appview, icon, key, classname));
        }

        /**
         *
         * @param icon
         * @param key
         * @param classname
         */
        public void addExecution(String icon, String key, String classname) {
            addAction(new MenuExecAction(m_appview, icon, key, classname));
        }

        /**
         *
         * @param icon
         * @param key
         * @param classname
         * @return
         */
        public ScriptSubmenu addSubmenu(String icon, String key, String classname) {
            ScriptSubmenu submenu = new ScriptSubmenu(key);
            final JPanelMenu jPanelMenu = new JPanelMenu(submenu.getMenuDefinition());
            jPanelMenu.setName(key);
            m_aPreparedViews.put(classname, jPanelMenu);
            addAction(new MenuPanelAction(m_appview, icon, key, classname));
            return submenu;
        }

        /**
         *
         */
        public void addChangePasswordAction() {
            addAction(new ChangePasswordAction("/com/openbravo/images/password.png", "Menu.ChangePassword"));
        }

        /**
         *
         */
        public void addExitAction() {
            addAction(new ExitAction("/com/openbravo/images/logout.png", "Menu.Exit"));
        }

        private void addAction(Action act) {

            if (m_appuser.hasPermission((String) act.getValue(AppUserView.ACTION_TASKNAME))) {
                // add the action
                Component c = taskGroup.add(act);
                c.applyComponentOrientation(getComponentOrientation());
                c.setFocusable(false);
                //c.setRequestFocusEnabled(false);

                taskGroup.setVisible(true);

                if (m_actionfirst == null) {
                    m_actionfirst = act;
                }
            }
        }

//        public JTaskPaneGroup getTaskGroup() {
        /**
         *
         * @return
         */
        public JXTaskPane getTaskGroup() {
            return taskGroup;
        }
    }

    /**
     *
     */
    public class ScriptSubmenu {

        private final MenuDefinition menudef;

        private ScriptSubmenu(String key) {
            menudef = new MenuDefinition(key);
        }

        /**
         *
         * @param key
         */
        public void addTitle(String key) {
            menudef.addMenuTitle(key);
        }

        /**
         *
         * @param icon
         * @param key
         * @param classname
         */
        public void addPanel(String icon, String key, String classname) {
            menudef.addMenuItem(new MenuPanelAction(m_appview, icon, key, classname), key);
        }

        /**
         *
         * @param icon
         * @param key
         * @param classname
         */
        public void addExecution(String icon, String key, String classname) {
            menudef.addMenuItem(new MenuExecAction(m_appview, icon, key, classname), key);
        }

        /**
         *
         * @param icon
         * @param key
         * @param classname
         * @return
         */
        public ScriptSubmenu addSubmenu(String icon, String key, String classname) {
            ScriptSubmenu submenu = new ScriptSubmenu(key);
            m_aPreparedViews.put(classname, new JPanelMenu(submenu.getMenuDefinition()));
            menudef.addMenuItem(new MenuPanelAction(m_appview, icon, key, classname), key);
            return submenu;
        }

        /**
         *
         */
        public void addChangePasswordAction() {
            menudef.addMenuItem(new ChangePasswordAction("/com/openbravo/images/password.png", "Menu.ChangePassword"), "menu.change_password");
        }

        /**
         *
         */
        public void addExitAction() {
            menudef.addMenuItem(new ExitAction("/com/openbravo/images/logout.png", "Menu.Exit"), "menu.exit");
        }

        /**
         *
         * @return
         */
        public MenuDefinition getMenuDefinition() {
            return menudef;
        }
    }

    private void setMenuVisible(boolean value) {

        m_jPanelLeft.setVisible(value);
        assignMenuButtonIcon();
        revalidate();
    }

    /**
     *
     * @return
     */
    public JComponent getNotificator() {
        return m_principalnotificator;
    }

    /**
     *
     */
    public void activate() {

        setMenuVisible(getBounds().width > 800);

        // arranco la primera opcion
        if (m_actionfirst != null) {
            m_actionfirst.actionPerformed(null);
            m_actionfirst = null;
        }
    }

    /**
     *
     * @return
     */
    public boolean deactivate() {
        if (m_jLastView == null) {
            return true;
        } else if (m_jLastView.deactivate()) {
            m_jLastView = null;
            showView("<NULL>");
            return true;
        } else {
            return false;
        }

    }

    private class ExitAction extends AbstractAction {

        public ExitAction(String icon, String keytext) {
            putValue(Action.SMALL_ICON, new ImageIcon(JPrincipalApp.class.getResource(icon)));
            putValue(Action.NAME, AppLocal.getIntString(keytext));
            putValue(AppUserView.ACTION_TASKNAME, keytext);
        }

        @Override
        public void actionPerformed(ActionEvent evt) {
            m_appview.closeAppView();
        }
    }

    /**
     *
     */
    public void exitToLogin() {
        m_appview.closeAppView();
        //m_appview.showLogin();
    }

    private class ChangePasswordAction extends AbstractAction {

        public ChangePasswordAction(String icon, String keytext) {
            putValue(Action.SMALL_ICON, new ImageIcon(JPrincipalApp.class.getResource(icon)));
            putValue(Action.NAME, AppLocal.getIntString(keytext));
            putValue(AppUserView.ACTION_TASKNAME, keytext);

        }

        @Override
        public void actionPerformed(ActionEvent evt) {

            String sNewPassword = Hashcypher.changePassword(JPrincipalApp.this, m_appuser.getPassword());
            if (sNewPassword != null) {
                try {

                    m_dlSystem.execChangePassword(new Object[]{sNewPassword, m_appuser.getId()});
                    m_appuser.setPassword(sNewPassword);
                } catch (BasicException e) {
                    JMessageDialog.showMessage(JPrincipalApp.this, new MessageInf(MessageInf.SGN_WARNING, AppLocal.getIntString("message.cannotchangepassword")));
                }
            }
        }
    }

    private void showView(String sView) {
        CardLayout cl = (CardLayout) (m_jPanelContainer.getLayout());
        cl.show(m_jPanelContainer, sView);
    }

    /**
     *
     * @return
     */
    @Override
    public AppUser getUser() {
        return m_appuser;
    }

    /**
     *
     * @param sTaskClass
     */
    @Override
    public void showTask(String sTaskClass) {

        customerInfo = new CustomerInfo("");
        customerInfo.setName("");

        m_appview.waitCursorBegin();

        if (m_appuser.hasPermission(sTaskClass)) {

            JPanelView m_jMyView = (JPanelView) m_aCreatedViews.get(sTaskClass);

            if (m_jLastView == null || (m_jMyView != m_jLastView && m_jLastView.deactivate())) {

                // Construct the new view
                if (m_jMyView == null) {

                    // Is the view prepared
                    m_jMyView = m_aPreparedViews.get(sTaskClass);
                    if (m_jMyView == null) {
                        // The view is not prepared. Try to get as a Bean...
                        try {
                            m_jMyView = (JPanelView) m_appview.getBean(sTaskClass);
                        } catch (BeanFactoryException e) {
                            m_jMyView = new JPanelNull(m_appview, e);
                        }
                    }

                    m_jMyView.getComponent().applyComponentOrientation(getComponentOrientation());
                    m_jPanelContainer.add(m_jMyView.getComponent(), sTaskClass);
                    m_aCreatedViews.put(sTaskClass, m_jMyView);
                }

                try {
                    m_jMyView.activate();
                } catch (BasicException e) {
                    JMessageDialog.showMessage(this, new MessageInf(MessageInf.SGN_WARNING, AppLocal.getIntString("message.notactive"), e));
                }

                m_jLastView = m_jMyView;

                setMenuVisible(getBounds().width > 800);
// JG Added 10 Nov 12
                setMenuVisible(false);

                showView(sTaskClass);
                String sTitle = m_jMyView.getTitle();
                m_jPanelTitle.setVisible(sTitle != null);
                m_jTitle.setText(sTitle);
            }
        } else {

            JMessageDialog.showMessage(this, new MessageInf(MessageInf.SGN_WARNING, AppLocal.getIntString("message.notpermissions")));
        }
        m_appview.waitCursorEnd();
    }

    /**
     *
     * @param sTaskClass
     */
    @Override
    public void executeTask(String sTaskClass) {

        m_appview.waitCursorBegin();

        if (m_appuser.hasPermission(sTaskClass)) {
            try {
                ProcessAction myProcess = (ProcessAction) m_appview.getBean(sTaskClass);

                try {
                    MessageInf m = myProcess.execute();
                    if (m != null) {
                        JMessageDialog.showMessage(JPrincipalApp.this, m);
                    }
                } catch (BasicException eb) {
                    JMessageDialog.showMessage(JPrincipalApp.this, new MessageInf(eb));
                }
            } catch (BeanFactoryException e) {
                JMessageDialog.showMessage(JPrincipalApp.this, new MessageInf(MessageInf.SGN_WARNING, AppLocal.getIntString("Label.LoadError"), e));
            }
        } else {
            JMessageDialog.showMessage(JPrincipalApp.this, new MessageInf(MessageInf.SGN_WARNING, AppLocal.getIntString("message.notpermissions")));
        }
        m_appview.waitCursorEnd();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jButton1 = new javax.swing.JButton();
        m_jPanelLeft = new javax.swing.JScrollPane();
        m_jPanelRight = new javax.swing.JPanel();
        m_jPanelTitle = new javax.swing.JPanel();
        m_jTitle = new javax.swing.JLabel();
        m_jPanelContainer = new javax.swing.JPanel();

        setLayout(new java.awt.BorderLayout());

        jPanel1.setLayout(new java.awt.BorderLayout());

        jPanel2.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jPanel2.setLayout(new javax.swing.BoxLayout(jPanel2, javax.swing.BoxLayout.Y_AXIS));

        jButton1.setToolTipText("Open/Close Menu");
        jButton1.setFocusPainted(false);
        jButton1.setFocusable(false);
        jButton1.setIconTextGap(0);
        jButton1.setMargin(new java.awt.Insets(14, 2, 14, 2));
        jButton1.setMaximumSize(new java.awt.Dimension(32, 32));
        jButton1.setMinimumSize(new java.awt.Dimension(32, 32));
        jButton1.setName("toggle-menu"); // NOI18N
        jButton1.setPreferredSize(new java.awt.Dimension(32, 32));
        jButton1.setRequestFocusEnabled(false);
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        jPanel2.add(jButton1);

        jPanel1.add(jPanel2, java.awt.BorderLayout.LINE_END);

        m_jPanelLeft.setFont(new java.awt.Font("Saysettha OT", 0, 18)); // NOI18N
        m_jPanelLeft.setPreferredSize(new java.awt.Dimension(250, 4));
        jPanel1.add(m_jPanelLeft, java.awt.BorderLayout.CENTER);

        add(jPanel1, java.awt.BorderLayout.LINE_START);

        m_jPanelRight.setLayout(new java.awt.BorderLayout());

        m_jPanelTitle.setLayout(new java.awt.BorderLayout());

        m_jTitle.setFont(new java.awt.Font("Saysettha OT", 0, 18)); // NOI18N
        m_jTitle.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 1, 0, java.awt.Color.darkGray), javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10)));
        m_jPanelTitle.add(m_jTitle, java.awt.BorderLayout.NORTH);

        m_jPanelRight.add(m_jPanelTitle, java.awt.BorderLayout.NORTH);

        m_jPanelContainer.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        m_jPanelContainer.setName("m_jPanelContainer"); // NOI18N
        m_jPanelContainer.setLayout(new java.awt.CardLayout());
        m_jPanelRight.add(m_jPanelContainer, java.awt.BorderLayout.CENTER);

        add(m_jPanelRight, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed

        setMenuVisible(!m_jPanelLeft.isVisible());

}//GEN-LAST:event_jButton1ActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel m_jPanelContainer;
    private javax.swing.JScrollPane m_jPanelLeft;
    private javax.swing.JPanel m_jPanelRight;
    private javax.swing.JPanel m_jPanelTitle;
    private javax.swing.JLabel m_jTitle;
    // End of variables declaration//GEN-END:variables

}
