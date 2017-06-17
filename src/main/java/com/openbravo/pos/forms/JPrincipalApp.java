package com.openbravo.pos.forms;

import com.openbravo.basic.BasicException;
import com.openbravo.data.gui.JMessageDialog;
import com.openbravo.data.gui.MessageInf;
import com.openbravo.pos.customers.CustomerInfo;
import com.openbravo.pos.scripting.ScriptException;
import java.awt.CardLayout;
import java.awt.Component;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import leftmenu.ScriptGroup;
import leftmenu.ScriptMenu;
import leftmenu.ScriptSubmenu;

public class JPrincipalApp extends javax.swing.JPanel implements AppUserView {

    private static final Logger logger = Logger.getLogger("com.openbravo.pos.forms.JPrincipalApp");

    private final JRootApp m_appview;
    private final AppUser m_appuser;

    private DataLogicSystem data_logic_system;

    public DataLogicSystem getDataLogicSystem() {
        return data_logic_system;
    }

    private JLabel m_principalnotificator;

    private JPanelView m_jLastView;
    private Action m_actionfirst;

    private Map<String, JPanelView> m_aPreparedViews; // Prepared views
    private Map<String, JPanelView> m_aCreatedViews;

    private Icon menu_open;
    private Icon menu_close;

    private CustomerInfo customerInfo;

    public JPrincipalApp(JRootApp appview, AppUser appuser) {

        m_appview = appview;
        m_appuser = appuser;

        data_logic_system = (DataLogicSystem) m_appview.getBean("com.openbravo.pos.forms.DataLogicSystem");

        m_appuser.fillPermissions(data_logic_system);

        m_actionfirst = null;
        m_jLastView = null;

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
            m_jPanelLeft.setViewportView(getScriptMenu(data_logic_system.getResourceAsText("Menu.Root")));
        } catch (ScriptException e) {
            logger.log(Level.SEVERE, "Cannot read default menu", e);
        }
    }

    private Component getScriptMenu(String menutext) throws ScriptException {

        ScriptMenu menu = new ScriptMenu(this);
        ScriptGroup group;

        group = menu.addGroup("Menu.Main");
        group.addPanel("/com/openbravo/images/sale.png", "Menu.Ticket", "com.openbravo.pos.sales.JPanelTicketSales");
        group.addPanel("/com/openbravo/images/saleedit.png", "Menu.TicketEdit", "com.openbravo.pos.sales.JPanelTicketEdits");
        //group.addPanel("/com/openbravo/images/customerpay.png", "Menu.CustomersPayment", "com.openbravo.pos.customers.CustomersPayment");
        group.addPanel("/com/openbravo/images/payments.png", "Menu.Payments", "com.openbravo.pos.panels.JPanelPayments");
        group.addPanel("/com/openbravo/images/calculator.png", "Menu.CloseTPV", "com.openbravo.pos.panels.JPanelCloseMoney");

        group = menu.addGroup("Menu.Backoffice");
        ScriptSubmenu submenu = null;
        /*
        submenu = group.addSubmenu("/com/openbravo/images/customer.png", "Menu.Customers", "com.openbravo.pos.forms.MenuCustomers");
        submenu.addTitle("Menu.Customers");
        submenu.addPanel("/com/openbravo/images/customer.png", "Menu.CustomersManagement", "com.openbravo.pos.customers.CustomersPanel");

        submenu.addTitle("Menu.Customers.Reports");
        submenu.addPanel("/com/openbravo/images/reports.png", "Menu.CustomersReport", "/com/openbravo/reports/customers.bs");
        submenu.addPanel("/com/openbravo/images/reports.png", "Menu.CustomersBReport", "/com/openbravo/reports/customersb.bs");
        submenu.addPanel("/com/openbravo/images/reports.png", "Menu.CustomersDebtors", "/com/openbravo/reports/customersdebtors.bs");
        submenu.addPanel("/com/openbravo/images/reports.png", "Menu.CustomersDiary", "/com/openbravo/reports/customersdiary.bs");
        submenu.addPanel("/com/openbravo/images/reports.png", "Menu.CustomersList", "/com/openbravo/reports/customers_list.bs");
        submenu.addPanel("/com/openbravo/images/reports.png", "Menu.SalesByCustomer", "/com/openbravo/reports/salebycustomer.bs");
         */

        submenu = group.addSubmenu("/com/openbravo/images/products.png", "Menu.StockManagement", "com.openbravo.pos.forms.MenuStockManagement");
        submenu.addTitle("Menu.StockManagement.Edit");
        submenu.addPanel("/com/openbravo/images/products.png", "Menu.Products", "com.openbravo.pos.inventory.ProductsPanel");
        submenu.addPanel("/com/openbravo/images/category.png", "Menu.Categories", "com.openbravo.pos.inventory.CategoriesPanel");
        submenu.addPanel("/com/openbravo/images/location.png", "Menu.ProductsWarehouse", "com.openbravo.pos.inventory.ProductsWarehousePanel");
        /*
        submenu.addPanel("/com/openbravo/images/auxiliary.png", "Menu.Auxiliar", "com.openbravo.pos.inventory.AuxiliarPanel");
        submenu.addPanel("/com/openbravo/images/attributes.png", "Menu.Attributes", "com.openbravo.pos.inventory.AttributesPanel");
        submenu.addPanel("/com/openbravo/images/attributes.png", "Menu.AttributeValues", "com.openbravo.pos.inventory.AttributeValuesPanel");
        submenu.addPanel("/com/openbravo/images/attributes.png", "Menu.AttributeSets", "com.openbravo.pos.inventory.AttributeSetsPanel");
        submenu.addPanel("/com/openbravo/images/attributes.png", "Menu.AttributeUse", "com.openbravo.pos.inventory.AttributeUsePanel");
         */
        submenu.addPanel("/com/openbravo/images/stockdiary.png", "Menu.StockDiary", "com.openbravo.pos.inventory.StockDiaryPanel");
        submenu.addPanel("/com/openbravo/images/stockmaint.png", "Menu.StockMovement", "com.openbravo.pos.inventory.StockManagement");

        submenu.addTitle("Menu.StockManagement.Reports");
        //submenu.addPanel("/com/openbravo/images/reports.png", "Menu.BarcodeSheet", "/com/openbravo/reports/barcodesheet.bs");
        submenu.addPanel("/com/openbravo/images/reports.png", "Menu.Inventory", "/com/openbravo/reports/inventory.bs");
        submenu.addPanel("/com/openbravo/images/reports.png", "Menu.Inventory2", "/com/openbravo/reports/inventoryb.bs");
        //submenu.addPanel("/com/openbravo/images/reports.png", "Menu.InventoryBroken", "/com/openbravo/reports/inventorybroken.bs");
        submenu.addPanel("/com/openbravo/images/reports.png", "Menu.InventoryDiff", "/com/openbravo/reports/inventorydiff.bs");
        submenu.addPanel("/com/openbravo/images/reports.png", "Menu.InventoryDiffDetail", "/com/openbravo/reports/inventorydiffdetail.bs");
        submenu.addPanel("/com/openbravo/images/reports.png", "Menu.InventoryListDetail", "/com/openbravo/reports/inventorylistdetail.bs");
        //submenu.addPanel("/com/openbravo/images/reports.png", "Menu.ProductCatalog", "/com/openbravo/reports/productscatalog.bs");
        submenu.addPanel("/com/openbravo/images/reports.png", "Menu.Products", "/com/openbravo/reports/products.bs");
        //submenu.addPanel("/com/openbravo/images/reports.png", "Menu.ProductLabels", "/com/openbravo/reports/productlabels.bs");
        //submenu.addPanel("/com/openbravo/images/reports.png", "Menu.SaleCatalog", "/com/openbravo/reports/salecatalog.bs");
        //submenu.addPanel("/com/openbravo/images/reports.png", "Menu.ShelfEdgeLabels", "/com/openbravo/reports/shelfedgelabels.bs");

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
        submenu.addPanel("/com/openbravo/images/reports.png", "Menu.DailyPresenceReport", "/com/openbravo/reports/dailypresencereport.bs");
        submenu.addPanel("/com/openbravo/images/reports.png", "Menu.DailyScheduleReport", "/com/openbravo/reports/dailyschedulereport.bs");
        submenu.addPanel("/com/openbravo/images/reports.png", "Menu.PerformanceReport", "/com/openbravo/reports/performancereport.bs");

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

    public void setFirstAction(Action act) {
        if (m_actionfirst == null) {
            m_actionfirst = act;
        }
    }

    public void exitToLogin() {
        m_appview.closeAppView();
        //m_appview.showLogin();
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

    public JRootApp getAppView() {
        return m_appview;
    }

    public void addPreparedView(String classname, JPanelMenu jPanelMenu) {
        m_aPreparedViews.put(classname, jPanelMenu);
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
