package com.openbravo.pos.inventory;

import com.openbravo.basic.BasicException;
import com.openbravo.data.gui.ComboBoxValModel;
import com.openbravo.data.loader.SentenceList;
import com.openbravo.data.user.DirtyManager;
import com.openbravo.data.user.EditorRecord;
import com.openbravo.format.Formats;
import com.openbravo.pos.forms.AppLocal;
import com.openbravo.pos.forms.DataLogicSales;
import com.openbravo.pos.sales.TaxesLogic;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.Date;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JPanel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public final class ProductsEditor extends JPanel implements EditorRecord {

    private final SentenceList m_sentcat;
    private ComboBoxValModel m_CategoryModel;

    private final SentenceList taxcatsent;
    private ComboBoxValModel taxcatmodel;

    private final SentenceList attsent;
    private ComboBoxValModel attmodel;

    private final SentenceList taxsent;
    private TaxesLogic taxeslogic;

    private Object m_id;
    private Object pricesell;
    private boolean priceselllock = false;

    private boolean reportlock = false;

    private DataLogicSales dataLogicSales = null;

    public ProductsEditor(DataLogicSales dlSales, DirtyManager dirty) {
        initComponents();

        taxsent = dlSales.getTaxList();
        dataLogicSales = dlSales;

        m_sentcat = dlSales.getCategoriesList();
        m_CategoryModel = new ComboBoxValModel();

        taxcatsent = dlSales.getTaxCategoriesList();
        taxcatmodel = new ComboBoxValModel();

        attsent = dlSales.getAttributeSetList();
        attmodel = new ComboBoxValModel();

        m_jRef.getDocument().addDocumentListener(dirty);
        m_jCode.getDocument().addDocumentListener(dirty);
        m_jCodetype.addActionListener(dirty);
        m_jName.getDocument().addDocumentListener(dirty);
        m_jPriceBuy.getDocument().addDocumentListener(dirty);
        m_jPriceSell.getDocument().addDocumentListener(dirty);
        m_jCategory.addActionListener(dirty);
        m_jTax.addActionListener(dirty);
        m_jAtt.addActionListener(dirty);
        m_jstockcost.getDocument().addDocumentListener(dirty);
        m_jstockvolume.getDocument().addDocumentListener(dirty);
        m_jImage.addPropertyChangeListener("image", dirty);
        m_jComment.addActionListener(dirty);
        m_jScale.addActionListener(dirty);
        m_jKitchen.addActionListener(dirty);
        m_jPrintKB.addActionListener(dirty);
        m_jSendStatus.addActionListener(dirty);
        m_jService.addActionListener(dirty);
        txtAttributes.getDocument().addDocumentListener(dirty);
        m_jDisplay.getDocument().addDocumentListener(dirty);
        m_jVprice.addActionListener(dirty);
        m_jVerpatrib.addActionListener(dirty);
        m_jTextTip.getDocument().addDocumentListener(dirty);
        m_jCheckWarrantyReceipt.addActionListener(dirty);
        m_jStockUnits.getDocument().putProperty(dlSales, 26);

        m_jInCatalog.addActionListener(dirty);
        m_jCatalogOrder.getDocument().addDocumentListener(dirty);

        FieldsManager fm = new FieldsManager();
        m_jPriceBuy.getDocument().addDocumentListener(fm);
        m_jPriceSell.getDocument().addDocumentListener(new PriceSellManager());
        m_jTax.addActionListener(fm);
        m_jPriceSellTax.getDocument().addDocumentListener(new PriceTaxManager());
        m_jmargin.getDocument().addDocumentListener(new MarginManager());
        m_jGrossProfit.getDocument().addDocumentListener(new MarginManager());

        m_jBundleUnit.getDocument().addDocumentListener(dirty);
        m_jPriceSellBundle.getDocument().addDocumentListener(dirty);

        box_sell_price.getDocument().addDocumentListener(dirty);
        box_units.getDocument().addDocumentListener(dirty);
        
        writeValueEOF();
    }

    /**
     *
     * @throws BasicException
     */
    public void activate() throws BasicException {

        // Load the taxes logic
        taxeslogic = new TaxesLogic(taxsent.list());

        m_CategoryModel = new ComboBoxValModel(m_sentcat.list());
        m_jCategory.setModel(m_CategoryModel);

        taxcatmodel = new ComboBoxValModel(taxcatsent.list());
        m_jTax.setModel(taxcatmodel);

        attmodel = new ComboBoxValModel(attsent.list());
        attmodel.add(0, null);
        m_jAtt.setModel(attmodel);
    }

    /**
     *
     */
    @Override
    public void refresh() {
    }

    /**
     *
     */
    @Override
    public void writeValueEOF() {

        reportlock = true;

        m_jTitle.setText(AppLocal.getIntString("label.recordeof"));
        m_id = null;
        m_jRef.setText(null);
        m_jCode.setText(null);
        m_jCodetype.setSelectedIndex(0);
        m_jName.setText(null);
        m_jPriceBuy.setText(null);
        setPriceSell(null);
        m_CategoryModel.setSelectedKey(null);
        taxcatmodel.setSelectedKey(null);
        attmodel.setSelectedKey(null);
        m_jstockcost.setText("0.0");
        m_jstockvolume.setText("0.0");
        m_jImage.setImage(null);
        m_jComment.setSelected(false);
        m_jScale.setSelected(false);
        m_jKitchen.setSelected(false);
        m_jPrintKB.setSelected(false);
        m_jSendStatus.setSelected(false);
        m_jService.setSelected(false);
        txtAttributes.setText(null);
        m_jDisplay.setText(null);
        m_jVprice.setSelected(false);
        m_jVerpatrib.setSelected(false);
        m_jTextTip.setText(null);
        m_jCheckWarrantyReceipt.setSelected(false);
        m_jStockUnits.setVisible(false);

        m_jInCatalog.setSelected(false);
        m_jCatalogOrder.setText(null);

        reportlock = false;

        m_jRef.setEnabled(false);
        m_jCode.setEnabled(false);
        m_jCodetype.setEnabled(false);
        m_jName.setEnabled(false);
        m_jPriceBuy.setEnabled(false);
        m_jPriceSell.setEnabled(false);
        m_jCategory.setEnabled(false);
        m_jTax.setEnabled(false);
        m_jAtt.setEnabled(false);
        m_jstockcost.setEnabled(false);
        m_jstockvolume.setEnabled(false);
        m_jImage.setEnabled(false);
        m_jComment.setEnabled(false);
        m_jScale.setEnabled(false);
        m_jKitchen.setEnabled(false);
        m_jPrintKB.setVisible(false);
        m_jSendStatus.setVisible(false);
        m_jService.setEnabled(false);
        txtAttributes.setEnabled(false);
        m_jDisplay.setEnabled(false);
        m_jVprice.setEnabled(false);
        m_jVerpatrib.setEnabled(false);
        m_jTextTip.setEnabled(false);
        m_jCheckWarrantyReceipt.setEnabled(false);
        m_jStockUnits.setVisible(false);

        m_jInCatalog.setEnabled(false);
        m_jCatalogOrder.setEnabled(false);

        m_jPriceSellTax.setEnabled(false);
        m_jmargin.setEnabled(false);

        calculateMargin();
        calculatePriceSellTax();
        calculateGP();
    }

    /**
     *
     */
    @Override
    public void writeValueInsert() {

        reportlock = true;

        m_jTitle.setText(AppLocal.getIntString("label.recordnew"));

        m_id = UUID.randomUUID().toString();
        m_jRef.setText(null);
        m_jCode.setText(null);
        m_jCodetype.setSelectedIndex(0);
        m_jName.setText(null);
        m_jPriceBuy.setText(null);
        setPriceSell(null);
        m_CategoryModel.setSelectedKey(null);
        taxcatmodel.setSelectedKey(null);
        attmodel.setSelectedKey(null);
        m_jstockcost.setText("0.0");
        m_jstockvolume.setText("0.0");
        m_jImage.setImage(null);
        m_jComment.setSelected(false);
        m_jScale.setSelected(false);
        m_jKitchen.setSelected(false);
        m_jPrintKB.setSelected(false);
        m_jSendStatus.setSelected(false);
        m_jService.setSelected(false);
        txtAttributes.setText(null);
        m_jDisplay.setText(null);
        m_jVprice.setSelected(false);
        m_jVerpatrib.setSelected(false);
        m_jTextTip.setText(null);
        m_jCheckWarrantyReceipt.setSelected(false);
        m_jStockUnits.setVisible(false);

        m_jInCatalog.setSelected(true);
        m_jCatalogOrder.setText(null);

        reportlock = false;

        m_jRef.setEnabled(true);
        m_jCode.setEnabled(true);
        m_jCodetype.setEnabled(true);
        m_jName.setEnabled(true);
        m_jPriceBuy.setEnabled(true);
        m_jPriceSell.setEnabled(true);
        m_jCategory.setEnabled(true);
        m_jTax.setEnabled(true);
        m_jAtt.setEnabled(true);
        m_jstockcost.setEnabled(true);
        m_jstockvolume.setEnabled(true);
        m_jImage.setEnabled(true);
        m_jComment.setEnabled(true);
        m_jScale.setEnabled(true);
        m_jKitchen.setEnabled(true);
        m_jPrintKB.setVisible(false);
        m_jSendStatus.setVisible(false);
        m_jService.setEnabled(true);
        txtAttributes.setEnabled(true);
        m_jDisplay.setEnabled(true);
        m_jVprice.setEnabled(true);
        m_jVerpatrib.setEnabled(true);
        m_jTextTip.setEnabled(true);
        m_jCheckWarrantyReceipt.setEnabled(true);
        m_jStockUnits.setVisible(false);

        m_jPriceSellTax.setEnabled(true);
        m_jmargin.setEnabled(true);

        m_jInCatalog.setEnabled(true);
        m_jCatalogOrder.setEnabled(false);

        calculateMargin();
        calculatePriceSellTax();
        calculateGP();
    }

    /**
     *
     * @return myprod
     * @throws BasicException
     */
    @Override
    public Object createValue() throws BasicException {

        Object[] myprod = new Object[32];
        myprod[0] = m_id;
        myprod[1] = m_jRef.getText();
        myprod[2] = m_jCode.getText();
        myprod[3] = m_jCodetype.getSelectedItem();
        myprod[4] = m_jName.getText();
        myprod[5] = Formats.CURRENCY.parseValue(m_jPriceBuy.getText());
        myprod[6] = pricesell;
        myprod[7] = m_CategoryModel.getSelectedKey();
        myprod[8] = taxcatmodel.getSelectedKey();
        myprod[9] = attmodel.getSelectedKey();
        myprod[10] = Formats.CURRENCY.parseValue(m_jstockcost.getText());
        myprod[11] = Formats.DOUBLE.parseValue(m_jstockvolume.getText());
        myprod[12] = m_jImage.getImage();
        myprod[13] = m_jComment.isSelected();
        myprod[14] = m_jScale.isSelected();
        myprod[15] = m_jKitchen.isSelected();
        myprod[16] = m_jPrintKB.isSelected();
        myprod[17] = m_jSendStatus.isSelected();
        myprod[18] = m_jService.isSelected();
        myprod[19] = Formats.BYTEA.parseValue(txtAttributes.getText());
        myprod[20] = m_jDisplay.getText();
        myprod[21] = m_jVprice.isSelected();
        myprod[22] = m_jVerpatrib.isSelected();
        myprod[23] = m_jTextTip.getText();
        myprod[24] = m_jCheckWarrantyReceipt.isSelected();
        myprod[25] = Formats.DOUBLE.parseValue(m_jStockUnits.getText());

        myprod[26] = m_jInCatalog.isSelected();
        myprod[27] = Formats.INT.parseValue(m_jCatalogOrder.getText());

        myprod[28] = Formats.DOUBLE.parseValue(m_jPriceSellBundle.getText());
        myprod[29] = Formats.DOUBLE.parseValue(m_jBundleUnit.getText());

        myprod[30] = Formats.DOUBLE.parseValue(box_sell_price.getText());
        myprod[31] = Formats.DOUBLE.parseValue(box_units.getText());

        return myprod;

    }

    /**
     *
     * @param value
     */
    @Override
    public void writeValueEdit(Object value) {

        reportlock = true;
        Object[] myprod = (Object[]) value;
        m_jTitle.setText(Formats.STRING.formatValue(myprod[1]) + " - " + Formats.STRING.formatValue(myprod[4]));
        m_id = myprod[0];
        m_jRef.setText(Formats.STRING.formatValue(myprod[1]));
        m_jCode.setText(Formats.STRING.formatValue(myprod[2]));
        m_jCodetype.setSelectedItem(myprod[3]);
        m_jName.setText(Formats.STRING.formatValue(myprod[4]));
        m_jPriceBuy.setText(Formats.CURRENCY.formatValue(myprod[5]));
        setPriceSell(myprod[6]);
        m_CategoryModel.setSelectedKey(myprod[7]);
        taxcatmodel.setSelectedKey(myprod[8]);
        attmodel.setSelectedKey(myprod[9]);
        m_jstockcost.setText(Formats.CURRENCY.formatValue(myprod[10]));
        m_jstockvolume.setText(Formats.DOUBLE.formatValue(myprod[11]));
        m_jImage.setImage((BufferedImage) myprod[12]);
        m_jComment.setSelected(((Boolean) myprod[13]));
        m_jScale.setSelected(((Boolean) myprod[14]));
        m_jKitchen.setSelected(((Boolean) myprod[15]));
        m_jPrintKB.setSelected(((Boolean) myprod[16]));
        m_jSendStatus.setSelected(((Boolean) myprod[17]));
        m_jService.setSelected(((Boolean) myprod[18]));
        txtAttributes.setText(Formats.BYTEA.formatValue(myprod[19]));
        m_jDisplay.setText(Formats.STRING.formatValue(myprod[20]));
        m_jVprice.setSelected(((Boolean) myprod[21]));
        m_jVerpatrib.setSelected(((Boolean) myprod[22]));
        m_jTextTip.setText(Formats.STRING.formatValue(myprod[23]));
        m_jCheckWarrantyReceipt.setSelected(((Boolean) myprod[24]));
        m_jStockUnits.setText(Formats.DOUBLE.formatValue(0.0));

        m_jInCatalog.setSelected(((Boolean) myprod[26]));
        m_jCatalogOrder.setText(Formats.INT.formatValue(myprod[27]));

        m_jPriceSellBundle.setText(Formats.DOUBLE.formatValue(myprod[28]));
        m_jBundleUnit.setText(Formats.DOUBLE.formatValue(myprod[29]));

        box_sell_price.setText(Formats.DOUBLE.formatValue(myprod[30]));
        box_units.setText(Formats.DOUBLE.formatValue(myprod[31]));

        txtAttributes.setCaretPosition(0);

        reportlock = false;

        m_jRef.setEnabled(true);
        m_jCode.setEnabled(true);
        m_jCodetype.setEnabled(true);
        m_jName.setEnabled(true);
        m_jPriceBuy.setEnabled(true);
        m_jPriceSell.setEnabled(true);
        m_jCategory.setEnabled(true);
        m_jTax.setEnabled(true);
        m_jAtt.setEnabled(true);
        m_jstockcost.setEnabled(true);
        m_jstockvolume.setEnabled(true);
        m_jImage.setEnabled(true);
        m_jComment.setEnabled(true);
        m_jScale.setEnabled(true);
        m_jKitchen.setEnabled(true);
        m_jPrintKB.setVisible(false);
        m_jSendStatus.setVisible(false);
        m_jService.setEnabled(true);
        txtAttributes.setEnabled(true);
        m_jDisplay.setEnabled(true);
        m_jSendStatus.setEnabled(true);
        m_jVerpatrib.setEnabled(true);
        m_jTextTip.setEnabled(true);
        m_jCheckWarrantyReceipt.setEnabled(true);
        m_jStockUnits.setVisible(false);

        m_jInCatalog.setEnabled(true);
        m_jCatalogOrder.setEnabled(m_jInCatalog.isSelected());

        m_jPriceSellTax.setEnabled(true);
        m_jmargin.setEnabled(true);

        setButtonHTML();
        calculateMargin();
        calculatePriceSellTax();
        calculateGP();

        try {
            int unit = (int) dataLogicSales.findProductStock("0", (String) m_id, null);
            product_unit.setText(Integer.toString(unit));
        } catch (BasicException ex) {
            Logger.getLogger(ProductsEditor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     *
     * @param value
     */
    @Override
    public void writeValueDelete(Object value) {

        reportlock = true;
        Object[] myprod = (Object[]) value;
        m_jTitle.setText(Formats.STRING.formatValue(myprod[1]) + " - " + Formats.STRING.formatValue(myprod[4]) + " " + AppLocal.getIntString("label.recorddeleted"));
        m_id = myprod[0];
        m_jRef.setText(Formats.STRING.formatValue(myprod[1]));
        m_jCode.setText(Formats.STRING.formatValue(myprod[2]));
        m_jCodetype.setSelectedItem(myprod[3]);
        m_jName.setText(Formats.STRING.formatValue(myprod[4]));
        m_jPriceBuy.setText(Formats.CURRENCY.formatValue(myprod[5]));
        setPriceSell(myprod[6]);
        m_CategoryModel.setSelectedKey(myprod[7]);
        taxcatmodel.setSelectedKey(myprod[8]);
        attmodel.setSelectedKey(myprod[9]);
        m_jstockcost.setText(Formats.CURRENCY.formatValue(myprod[10]));
        m_jstockvolume.setText(Formats.DOUBLE.formatValue(myprod[11]));
        m_jImage.setImage((BufferedImage) myprod[12]);
        m_jComment.setSelected(((Boolean) myprod[13]));
        m_jScale.setSelected(((Boolean) myprod[14]));
        m_jKitchen.setSelected(((Boolean) myprod[15]));
        m_jPrintKB.setSelected(((Boolean) myprod[16]));
        m_jSendStatus.setSelected(((Boolean) myprod[17]));
        m_jService.setSelected(((Boolean) myprod[18]));
        txtAttributes.setText(Formats.BYTEA.formatValue(myprod[19]));
        m_jDisplay.setText(Formats.STRING.formatValue(myprod[20]));
        m_jVprice.setSelected(((Boolean) myprod[21]));
        m_jVerpatrib.setSelected(((Boolean) myprod[22]));
        m_jTextTip.setText(Formats.STRING.formatValue(myprod[23]));
        m_jCheckWarrantyReceipt.setSelected(((Boolean) myprod[24]));
        m_jStockUnits.setText(Formats.DOUBLE.formatValue(myprod[25]));

        m_jInCatalog.setSelected(((Boolean) myprod[26]));
        m_jCatalogOrder.setText(Formats.INT.formatValue(myprod[27]));

        txtAttributes.setCaretPosition(0);

        reportlock = false;

        m_jRef.setEnabled(false);
        m_jCode.setEnabled(false);
        m_jCodetype.setEnabled(false);
        m_jName.setEnabled(false);
        m_jPriceBuy.setEnabled(false);
        m_jPriceSell.setEnabled(false);
        m_jCategory.setEnabled(false);
        m_jTax.setEnabled(false);
        m_jAtt.setEnabled(false);
        m_jstockcost.setEnabled(false);
        m_jstockvolume.setEnabled(false);
        m_jImage.setEnabled(false);
        m_jComment.setEnabled(false);
        m_jScale.setEnabled(false);
        m_jKitchen.setEnabled(false);
        m_jPrintKB.setVisible(false);
        m_jSendStatus.setVisible(false);
        m_jService.setEnabled(true);
        txtAttributes.setEnabled(false);
        m_jDisplay.setEnabled(false);
        m_jVprice.setEnabled(false);
        m_jVerpatrib.setEnabled(false);
        m_jTextTip.setEnabled(false);
        m_jCheckWarrantyReceipt.setEnabled(false);
        m_jStockUnits.setVisible(false);

        m_jInCatalog.setEnabled(false);
        m_jCatalogOrder.setEnabled(false);

        m_jPriceSellTax.setEnabled(false);
        m_jmargin.setEnabled(false);

        calculateMargin();
        calculatePriceSellTax();
        calculateGP();
    }

    /**
     *
     * @return this
     */
    @Override
    public Component getComponent() {
        return this;
    }

    private void setCode() {
        Long lDateTime = new Date().getTime(); // USED FOR RANDOM CODE DETAILS
        if (!reportlock) {
            reportlock = true;
            if (m_jRef == null) {
                m_jCode.setText(Long.toString(lDateTime));
            } else if (m_jCode.getText() == null || "".equals(m_jCode.getText())) {
                m_jCode.setText(m_jRef.getText());
            }
            reportlock = false;
        }
    }

// ADDED JG 19 NOV 12 - AUTOFILL BUTTON
// AMENDED JDL 11 MAY 12 - STOP AUTOFILL IF FIELD ALREADY EXSISTS
    private void setDisplay() {

        String str = (m_jName.getText());
        int length = str.length();

        if (!reportlock) {
            reportlock = true;

            if (length == 0) {
                m_jDisplay.setText(m_jName.getText());
            } else if (m_jDisplay.getText() == null || "".equals(m_jDisplay.getText())) {
                m_jDisplay.setText("<html>" + m_jName.getText());
            }
            reportlock = false;
        }
    }
// ADDED JG 20 Jul 13 - AUTOFILL HTML BUTTON

    private void setButtonHTML() {

        String str = (m_jDisplay.getText());
        int length = str.length();

        if (!reportlock) {
            reportlock = true;

            if (length == 0) {
                jButtonHTML.setText("Click Me");
            } else {
                jButtonHTML.setText(m_jDisplay.getText());
            }
            reportlock = false;
        }
    }

    private void calculateMargin() {

        if (!reportlock) {
            reportlock = true;

            Double dPriceBuy = readCurrency(m_jPriceBuy.getText());
            Double dPriceSell = (Double) pricesell;

            if (dPriceBuy == null || dPriceSell == null) {
                m_jmargin.setText(null);
            } else {
                m_jmargin.setText(Formats.PERCENT.formatValue(dPriceSell / dPriceBuy - 1.0));
            }
            reportlock = false;
        }
    }

    private void calculatePriceSellTax() {

        if (!reportlock) {
            reportlock = true;

            Double dPriceSell = (Double) pricesell;

            if (dPriceSell == null) {
                m_jPriceSellTax.setText(null);
            } else {
                double dTaxRate = taxeslogic.getTaxRate((TaxCategoryInfo) taxcatmodel.getSelectedItem());
                m_jPriceSellTax.setText(Formats.CURRENCY.formatValue(dPriceSell * (1.0 + dTaxRate)));
            }
            reportlock = false;
        }
    }

    private void calculateGP() {

        if (!reportlock) {
            reportlock = true;

            Double dPriceBuy = readCurrency(m_jPriceBuy.getText());
            Double dPriceSell = (Double) pricesell;

            if (dPriceBuy == null || dPriceSell == null) {
                m_jGrossProfit.setText(null);
            } else {
                m_jGrossProfit.setText(Formats.PERCENT.formatValue((dPriceSell - dPriceBuy) / dPriceSell));
            }
            reportlock = false;
        }
    }

    private void calculatePriceSellfromMargin() {

        if (!reportlock) {
            reportlock = true;

            Double dPriceBuy = readCurrency(m_jPriceBuy.getText());
            Double dMargin = readPercent(m_jmargin.getText());

            if (dMargin == null || dPriceBuy == null) {
                setPriceSell(null);
            } else {
                setPriceSell(dPriceBuy * (1.0 + dMargin));
            }

            reportlock = false;
        }

    }

    private void calculatePriceSellfromPST() {

        if (!reportlock) {
            reportlock = true;

            Double dPriceSellTax = readCurrency(m_jPriceSellTax.getText());

            if (dPriceSellTax == null) {
                setPriceSell(null);
            } else {
                double dTaxRate = taxeslogic.getTaxRate((TaxCategoryInfo) taxcatmodel.getSelectedItem());
                setPriceSell(dPriceSellTax / (1.0 + dTaxRate));
            }

            reportlock = false;
        }
    }

    private void setPriceSell(Object value) {

        if (!priceselllock) {
            priceselllock = true;
            pricesell = value;
            m_jPriceSell.setText(Formats.CURRENCY.formatValue(pricesell));
            priceselllock = false;
        }
    }

    private class PriceSellManager implements DocumentListener {

        @Override
        public void changedUpdate(DocumentEvent e) {
            if (!priceselllock) {
                priceselllock = true;
                pricesell = readCurrency(m_jPriceSell.getText());
                priceselllock = false;
            }
            calculateMargin();
            calculatePriceSellTax();
            calculateGP();
        }

        @Override
        public void insertUpdate(DocumentEvent e) {
            if (!priceselllock) {
                priceselllock = true;
                pricesell = readCurrency(m_jPriceSell.getText());
                priceselllock = false;
            }
            calculateMargin();
            calculatePriceSellTax();
            calculateGP();
        }

        @Override
        public void removeUpdate(DocumentEvent e) {
            if (!priceselllock) {
                priceselllock = true;
                pricesell = readCurrency(m_jPriceSell.getText());
                priceselllock = false;
            }
            calculateMargin();
            calculatePriceSellTax();
            calculateGP();
        }
    }

    private class FieldsManager implements DocumentListener, ActionListener {

        @Override
        public void changedUpdate(DocumentEvent e) {
            calculateMargin();
            calculatePriceSellTax();
            calculateGP();

        }

        @Override
        public void insertUpdate(DocumentEvent e) {
            calculateMargin();
            calculatePriceSellTax();
            calculateGP();
        }

        @Override
        public void removeUpdate(DocumentEvent e) {
            calculateMargin();
            calculatePriceSellTax();
            calculateGP();
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            calculateMargin();
            calculatePriceSellTax();
            calculateGP();
        }
    }

    private class PriceTaxManager implements DocumentListener {

        @Override
        public void changedUpdate(DocumentEvent e) {
            calculatePriceSellfromPST();
            calculateMargin();
            calculateGP();
        }

        @Override
        public void insertUpdate(DocumentEvent e) {
            calculatePriceSellfromPST();
            calculateMargin();
            calculateGP();
        }

        @Override
        public void removeUpdate(DocumentEvent e) {
            calculatePriceSellfromPST();
            calculateMargin();
            calculateGP();
        }
    }

    private class MarginManager implements DocumentListener {

        @Override
        public void changedUpdate(DocumentEvent e) {
            calculatePriceSellfromMargin();
            calculatePriceSellTax();
            calculateGP();
        }

        @Override
        public void insertUpdate(DocumentEvent e) {
            calculatePriceSellfromMargin();
            calculatePriceSellTax();
            calculateGP();
        }

        @Override
        public void removeUpdate(DocumentEvent e) {
            calculatePriceSellfromMargin();
            calculatePriceSellTax();
            calculateGP();
        }
    }

    private static Double readCurrency(String sValue) {
        try {
            return (Double) Formats.CURRENCY.parseValue(sValue);
        } catch (BasicException e) {
            return null;
        }
    }

    private static Double readPercent(String sValue) {
        try {
            return (Double) Formats.PERCENT.parseValue(sValue);
        } catch (BasicException e) {
            return null;
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel24 = new javax.swing.JLabel();
        jLabel27 = new javax.swing.JLabel();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        m_jRef = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        m_jCode = new javax.swing.JTextField();
        m_jCodetype = new javax.swing.JComboBox();
        jLabel2 = new javax.swing.JLabel();
        m_jName = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        m_jCategory = new javax.swing.JComboBox();
        jLabel13 = new javax.swing.JLabel();
        m_jAtt = new javax.swing.JComboBox();
        jLabel7 = new javax.swing.JLabel();
        m_jTax = new javax.swing.JComboBox();
        jLabel16 = new javax.swing.JLabel();
        m_jPriceSellTax = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        m_jPriceSell = new javax.swing.JTextField();
        jLabel19 = new javax.swing.JLabel();
        m_jmargin = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        m_jPriceBuy = new javax.swing.JTextField();
        m_jVerpatrib = new javax.swing.JCheckBox();
        m_jTextTip = new javax.swing.JTextField();
        product_unit = new javax.swing.JLabel();
        m_jCheckWarrantyReceipt = new javax.swing.JCheckBox();
        m_jGrossProfit = new javax.swing.JTextField();
        jLabel22 = new javax.swing.JLabel();
        m_jTitle = new javax.swing.JLabel();
        jLabel33 = new javax.swing.JLabel();
        jLabel34 = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();
        m_jPriceSellBundle = new javax.swing.JTextField();
        jLabel35 = new javax.swing.JLabel();
        m_jBundleUnit = new javax.swing.JTextField();
        jLabel36 = new javax.swing.JLabel();
        box_sell_price = new javax.swing.JTextField();
        jLabel37 = new javax.swing.JLabel();
        box_units = new javax.swing.JTextField();
        jLabel38 = new javax.swing.JLabel();
        jLabel39 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jLabel9 = new javax.swing.JLabel();
        m_jstockcost = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        m_jstockvolume = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        m_jInCatalog = new javax.swing.JCheckBox();
        jLabel18 = new javax.swing.JLabel();
        m_jCatalogOrder = new javax.swing.JTextField();
        jLabel15 = new javax.swing.JLabel();
        m_jService = new javax.swing.JCheckBox();
        jLabel11 = new javax.swing.JLabel();
        m_jComment = new javax.swing.JCheckBox();
        jLabel12 = new javax.swing.JLabel();
        m_jScale = new javax.swing.JCheckBox();
        m_jKitchen = new javax.swing.JCheckBox();
        jLabel14 = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        m_jSendStatus = new javax.swing.JCheckBox();
        jLabel23 = new javax.swing.JLabel();
        m_jStockUnits = new javax.swing.JTextField();
        m_jVprice = new javax.swing.JCheckBox();
        m_jPrintKB = new javax.swing.JCheckBox();
        m_jImage = new com.openbravo.data.gui.JImageEditor();
        jPanel4 = new javax.swing.JPanel();
        jLabel28 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        m_jDisplay = new javax.swing.JTextPane();
        jButtonHTML = new javax.swing.JButton();
        jLabel17 = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        jPanel5 = new javax.swing.JPanel();
        jLabel32 = new javax.swing.JLabel();
        jLabel25 = new javax.swing.JLabel();
        jLabel29 = new javax.swing.JLabel();
        jLabel26 = new javax.swing.JLabel();
        jLabel31 = new javax.swing.JLabel();
        jLabel30 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        txtAttributes = new javax.swing.JTextArea();

        jLabel24.setText("jLabel24");

        jLabel27.setText("jLabel27");

        setLayout(null);

        jTabbedPane1.setFont(new java.awt.Font("Saysettha OT", 0, 16)); // NOI18N

        jPanel1.setLayout(null);

        jLabel1.setFont(new java.awt.Font("Saysettha OT", 0, 16)); // NOI18N
        jLabel1.setText(AppLocal.getIntString("label.prodref")); // NOI18N
        jPanel1.add(jLabel1);
        jLabel1.setBounds(10, 10, 160, 25);

        m_jRef.setFont(new java.awt.Font("Arial", 0, 16)); // NOI18N
        m_jRef.setName("product_code"); // NOI18N
        m_jRef.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                m_jRefFocusLost(evt);
            }
        });
        jPanel1.add(m_jRef);
        m_jRef.setBounds(190, 10, 180, 25);

        jLabel6.setFont(new java.awt.Font("Saysettha OT", 0, 16)); // NOI18N
        jLabel6.setText(AppLocal.getIntString("label.prodbarcode")); // NOI18N
        jPanel1.add(jLabel6);
        jLabel6.setBounds(10, 40, 170, 25);

        m_jCode.setFont(new java.awt.Font("Arial", 0, 16)); // NOI18N
        jPanel1.add(m_jCode);
        m_jCode.setBounds(190, 40, 180, 25);

        m_jCodetype.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        m_jCodetype.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "None", "EAN-8", "EAN-13", "UPC-A", "UPC-E", "CODE128" }));
        m_jCodetype.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                m_jCodetypeActionPerformed(evt);
            }
        });
        jPanel1.add(m_jCodetype);
        m_jCodetype.setBounds(370, 40, 90, 25);

        jLabel2.setFont(new java.awt.Font("Saysettha OT", 0, 16)); // NOI18N
        jLabel2.setText(AppLocal.getIntString("label.prodname")); // NOI18N
        jPanel1.add(jLabel2);
        jLabel2.setBounds(10, 70, 170, 25);

        m_jName.setFont(new java.awt.Font("Saysettha OT", 0, 16)); // NOI18N
        m_jName.setName("product_name"); // NOI18N
        m_jName.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                m_jNameFocusLost(evt);
            }
        });
        jPanel1.add(m_jName);
        m_jName.setBounds(190, 70, 270, 25);

        jLabel5.setFont(new java.awt.Font("Saysettha OT", 0, 16)); // NOI18N
        jLabel5.setText(AppLocal.getIntString("label.prodcategory")); // NOI18N
        jPanel1.add(jLabel5);
        jLabel5.setBounds(10, 100, 170, 25);

        m_jCategory.setFont(new java.awt.Font("Saysettha OT", 0, 16)); // NOI18N
        m_jCategory.setName("product_category"); // NOI18N
        jPanel1.add(m_jCategory);
        m_jCategory.setBounds(190, 100, 270, 25);

        jLabel13.setFont(new java.awt.Font("Saysettha OT", 0, 16)); // NOI18N
        jLabel13.setText(AppLocal.getIntString("label.attributes")); // NOI18N
        jPanel1.add(jLabel13);
        jLabel13.setBounds(10, 130, 170, 25);

        m_jAtt.setFont(new java.awt.Font("Saysettha OT", 0, 16)); // NOI18N
        jPanel1.add(m_jAtt);
        m_jAtt.setBounds(190, 130, 270, 25);

        jLabel7.setFont(new java.awt.Font("Saysettha OT", 0, 16)); // NOI18N
        jLabel7.setText(AppLocal.getIntString("label.taxcategory")); // NOI18N
        jPanel1.add(jLabel7);
        jLabel7.setBounds(10, 160, 170, 25);

        m_jTax.setFont(new java.awt.Font("Saysettha OT", 0, 16)); // NOI18N
        m_jTax.setName("product_tax"); // NOI18N
        m_jTax.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                m_jTaxActionPerformed(evt);
            }
        });
        jPanel1.add(m_jTax);
        m_jTax.setBounds(190, 160, 270, 25);

        jLabel16.setFont(new java.awt.Font("Saysettha OT", 0, 16)); // NOI18N
        jLabel16.setText(AppLocal.getIntString("label.prodpriceselltax")); // NOI18N
        jPanel1.add(jLabel16);
        jLabel16.setBounds(10, 190, 170, 25);

        m_jPriceSellTax.setFont(new java.awt.Font("Arial", 0, 16)); // NOI18N
        m_jPriceSellTax.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        m_jPriceSellTax.setName("sell_price_plus_tax"); // NOI18N
        m_jPriceSellTax.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                m_jPriceSellTaxActionPerformed(evt);
            }
        });
        jPanel1.add(m_jPriceSellTax);
        m_jPriceSellTax.setBounds(190, 190, 140, 25);

        jLabel4.setFont(new java.awt.Font("Saysettha OT", 0, 16)); // NOI18N
        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel4.setText(AppLocal.getIntString("label.prodpricesell")); // NOI18N
        jPanel1.add(jLabel4);
        jLabel4.setBounds(350, 190, 100, 25);

        m_jPriceSell.setFont(new java.awt.Font("Arial", 0, 16)); // NOI18N
        m_jPriceSell.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jPanel1.add(m_jPriceSell);
        m_jPriceSell.setBounds(450, 190, 130, 25);

        jLabel19.setFont(new java.awt.Font("Saysettha OT", 0, 16)); // NOI18N
        jLabel19.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("pos_messages"); // NOI18N
        jLabel19.setText(bundle.getString("label.margin")); // NOI18N
        jLabel19.setPreferredSize(new java.awt.Dimension(48, 15));
        jPanel1.add(jLabel19);
        jLabel19.setBounds(350, 280, 100, 30);

        m_jmargin.setFont(new java.awt.Font("Arial", 0, 16)); // NOI18N
        m_jmargin.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        m_jmargin.setCursor(new java.awt.Cursor(java.awt.Cursor.TEXT_CURSOR));
        m_jmargin.setEnabled(false);
        jPanel1.add(m_jmargin);
        m_jmargin.setBounds(450, 280, 130, 25);

        jLabel3.setFont(new java.awt.Font("Saysettha OT", 0, 16)); // NOI18N
        jLabel3.setText(AppLocal.getIntString("label.prodpricebuy")); // NOI18N
        jPanel1.add(jLabel3);
        jLabel3.setBounds(10, 280, 170, 25);

        m_jPriceBuy.setFont(new java.awt.Font("Arial", 0, 16)); // NOI18N
        m_jPriceBuy.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        m_jPriceBuy.setName("buy_price"); // NOI18N
        jPanel1.add(m_jPriceBuy);
        m_jPriceBuy.setBounds(190, 280, 140, 25);

        m_jVerpatrib.setFont(new java.awt.Font("Saysettha OT", 0, 16)); // NOI18N
        m_jVerpatrib.setText(bundle.getString("label.mandatory")); // NOI18N
        m_jVerpatrib.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                none(evt);
            }
        });
        jPanel1.add(m_jVerpatrib);
        m_jVerpatrib.setBounds(460, 130, 120, 23);

        m_jTextTip.setFont(new java.awt.Font("Saysettha OT", 0, 16)); // NOI18N
        jPanel1.add(m_jTextTip);
        m_jTextTip.setBounds(190, 310, 390, 25);

        product_unit.setFont(new java.awt.Font("Saysettha OT", 0, 16)); // NOI18N
        product_unit.setText("0");
        jPanel1.add(product_unit);
        product_unit.setBounds(600, 40, 110, 25);

        m_jCheckWarrantyReceipt.setFont(new java.awt.Font("Saysettha OT", 0, 16)); // NOI18N
        m_jCheckWarrantyReceipt.setText(bundle.getString("label.productreceipt")); // NOI18N
        m_jCheckWarrantyReceipt.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                m_jCheckWarrantyReceiptActionPerformed(evt);
            }
        });
        jPanel1.add(m_jCheckWarrantyReceipt);
        m_jCheckWarrantyReceipt.setBounds(190, 340, 310, 23);

        m_jGrossProfit.setFont(new java.awt.Font("Arial", 0, 16)); // NOI18N
        m_jGrossProfit.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        m_jGrossProfit.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        m_jGrossProfit.setEnabled(false);
        jPanel1.add(m_jGrossProfit);
        m_jGrossProfit.setBounds(670, 280, 110, 25);

        jLabel22.setFont(new java.awt.Font("Saysettha OT", 0, 16)); // NOI18N
        jLabel22.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel22.setText(bundle.getString("label.grossprofit")); // NOI18N
        jPanel1.add(jLabel22);
        jLabel22.setBounds(580, 280, 90, 30);

        m_jTitle.setFont(new java.awt.Font("Saysettha OT", 1, 16)); // NOI18N
        m_jTitle.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jPanel1.add(m_jTitle);
        m_jTitle.setBounds(380, 10, 390, 20);

        jLabel33.setFont(new java.awt.Font("Saysettha OT", 0, 16)); // NOI18N
        jLabel33.setText(bundle.getString("label.texttip")); // NOI18N
        jPanel1.add(jLabel33);
        jLabel33.setBounds(10, 310, 170, 25);

        jLabel34.setFont(new java.awt.Font("Saysettha OT", 1, 16)); // NOI18N
        jLabel34.setText(bundle.getString("label.stock_unit")); // NOI18N
        jPanel1.add(jLabel34);
        jLabel34.setBounds(470, 40, 130, 25);

        jLabel21.setFont(new java.awt.Font("Saysettha OT", 0, 16)); // NOI18N
        jLabel21.setText(AppLocal.getIntString("label.prodpricesell_bundle")); // NOI18N
        jPanel1.add(jLabel21);
        jLabel21.setBounds(10, 220, 170, 25);

        m_jPriceSellBundle.setFont(new java.awt.Font("Arial", 0, 16)); // NOI18N
        m_jPriceSellBundle.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        m_jPriceSellBundle.setName("buy_price"); // NOI18N
        m_jPriceSellBundle.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                m_jPriceSellBundleActionPerformed(evt);
            }
        });
        jPanel1.add(m_jPriceSellBundle);
        m_jPriceSellBundle.setBounds(190, 220, 140, 25);

        jLabel35.setFont(new java.awt.Font("Saysettha OT", 0, 16)); // NOI18N
        jLabel35.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel35.setText(AppLocal.getIntString("label.prodBundle_quantity_unit_box")); // NOI18N
        jPanel1.add(jLabel35);
        jLabel35.setBounds(590, 250, 40, 25);

        m_jBundleUnit.setFont(new java.awt.Font("Arial", 0, 16)); // NOI18N
        m_jBundleUnit.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jPanel1.add(m_jBundleUnit);
        m_jBundleUnit.setBounds(450, 220, 130, 25);

        jLabel36.setFont(new java.awt.Font("Saysettha OT", 0, 16)); // NOI18N
        jLabel36.setText(AppLocal.getIntString("label.prodpricesell_box")); // NOI18N
        jLabel36.setToolTipText("");
        jPanel1.add(jLabel36);
        jLabel36.setBounds(10, 250, 170, 25);

        box_sell_price.setFont(new java.awt.Font("Arial", 0, 16)); // NOI18N
        box_sell_price.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        box_sell_price.setName("buy_price"); // NOI18N
        box_sell_price.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                box_sell_priceActionPerformed(evt);
            }
        });
        jPanel1.add(box_sell_price);
        box_sell_price.setBounds(190, 250, 140, 25);

        jLabel37.setFont(new java.awt.Font("Saysettha OT", 0, 16)); // NOI18N
        jLabel37.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel37.setText(AppLocal.getIntString("label.prod_box_quantity")); // NOI18N
        jPanel1.add(jLabel37);
        jLabel37.setBounds(360, 250, 90, 25);

        box_units.setFont(new java.awt.Font("Arial", 0, 16)); // NOI18N
        box_units.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jPanel1.add(box_units);
        box_units.setBounds(450, 250, 130, 25);

        jLabel38.setFont(new java.awt.Font("Saysettha OT", 0, 16)); // NOI18N
        jLabel38.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel38.setText(AppLocal.getIntString("label.prodBundle_quantity")); // NOI18N
        jPanel1.add(jLabel38);
        jLabel38.setBounds(350, 220, 100, 25);

        jLabel39.setFont(new java.awt.Font("Saysettha OT", 0, 16)); // NOI18N
        jLabel39.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel39.setText(AppLocal.getIntString("label.prodBundle_quantity_unit")); // NOI18N
        jPanel1.add(jLabel39);
        jLabel39.setBounds(580, 220, 40, 25);

        jTabbedPane1.addTab(AppLocal.getIntString("label.prodgeneral"), jPanel1); // NOI18N

        jPanel2.setLayout(null);

        jLabel9.setFont(new java.awt.Font("Saysettha OT", 0, 16)); // NOI18N
        jLabel9.setText(AppLocal.getIntString("label.prodstockcost")); // NOI18N
        jPanel2.add(jLabel9);
        jLabel9.setBounds(320, 120, 170, 25);

        m_jstockcost.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        m_jstockcost.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jPanel2.add(m_jstockcost);
        m_jstockcost.setBounds(500, 120, 160, 25);

        jLabel10.setFont(new java.awt.Font("Saysettha OT", 0, 16)); // NOI18N
        jLabel10.setText(AppLocal.getIntString("label.prodstockvol")); // NOI18N
        jPanel2.add(jLabel10);
        jLabel10.setBounds(320, 160, 170, 25);

        m_jstockvolume.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        m_jstockvolume.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jPanel2.add(m_jstockvolume);
        m_jstockvolume.setBounds(500, 160, 160, 25);

        jLabel8.setFont(new java.awt.Font("Saysettha OT", 0, 16)); // NOI18N
        jLabel8.setText(AppLocal.getIntString("label.prodincatalog")); // NOI18N
        jPanel2.add(jLabel8);
        jLabel8.setBounds(10, 60, 200, 25);

        m_jInCatalog.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        m_jInCatalog.setSelected(true);
        m_jInCatalog.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                m_jInCatalogActionPerformed(evt);
            }
        });
        jPanel2.add(m_jInCatalog);
        m_jInCatalog.setBounds(210, 60, 30, 25);

        jLabel18.setFont(new java.awt.Font("Saysettha OT", 0, 16)); // NOI18N
        jLabel18.setText(AppLocal.getIntString("label.prodorder")); // NOI18N
        jPanel2.add(jLabel18);
        jLabel18.setBounds(320, 60, 170, 25);

        m_jCatalogOrder.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        m_jCatalogOrder.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jPanel2.add(m_jCatalogOrder);
        m_jCatalogOrder.setBounds(500, 60, 160, 25);

        jLabel15.setFont(new java.awt.Font("Saysettha OT", 0, 16)); // NOI18N
        jLabel15.setText("Service Item");
        jPanel2.add(jLabel15);
        jLabel15.setBounds(10, 90, 200, 25);

        m_jService.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        m_jService.setToolTipText("A Service Item will not be deducted from the Inventory");
        jPanel2.add(m_jService);
        m_jService.setBounds(210, 90, 30, 25);
        m_jService.getAccessibleContext().setAccessibleDescription("null");

        jLabel11.setFont(new java.awt.Font("Saysettha OT", 0, 16)); // NOI18N
        jLabel11.setText(AppLocal.getIntString("label.prodaux")); // NOI18N
        jPanel2.add(jLabel11);
        jLabel11.setBounds(10, 120, 200, 25);

        m_jComment.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jPanel2.add(m_jComment);
        m_jComment.setBounds(210, 120, 30, 25);

        jLabel12.setFont(new java.awt.Font("Saysettha OT", 0, 16)); // NOI18N
        jLabel12.setText(AppLocal.getIntString("label.prodscale")); // NOI18N
        jPanel2.add(jLabel12);
        jLabel12.setBounds(10, 150, 190, 25);

        m_jScale.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jPanel2.add(m_jScale);
        m_jScale.setBounds(210, 150, 30, 25);

        m_jKitchen.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jPanel2.add(m_jKitchen);
        m_jKitchen.setBounds(210, 180, 30, 25);

        jLabel14.setFont(new java.awt.Font("Saysettha OT", 0, 16)); // NOI18N
        jLabel14.setText("Print to Remote Printer");
        jPanel2.add(jLabel14);
        jLabel14.setBounds(10, 180, 200, 25);

        jLabel20.setFont(new java.awt.Font("Saysettha OT", 0, 16)); // NOI18N
        jLabel20.setText(bundle.getString("label.variableprice")); // NOI18N
        jPanel2.add(jLabel20);
        jLabel20.setBounds(10, 210, 200, 25);

        m_jSendStatus.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jPanel2.add(m_jSendStatus);
        m_jSendStatus.setBounds(210, 270, 30, 25);

        jLabel23.setFont(new java.awt.Font("Saysettha OT", 0, 16)); // NOI18N
        jLabel23.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel23.setText(bundle.getString("label.prodminmax")); // NOI18N
        jLabel23.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        jPanel2.add(jLabel23);
        jLabel23.setBounds(320, 220, 340, 60);

        m_jStockUnits.setEditable(false);
        m_jStockUnits.setFont(new java.awt.Font("Arial", 0, 16)); // NOI18N
        m_jStockUnits.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        m_jStockUnits.setText("0");
        m_jStockUnits.setBorder(null);
        jPanel2.add(m_jStockUnits);
        m_jStockUnits.setBounds(320, 280, 80, 25);

        m_jVprice.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jPanel2.add(m_jVprice);
        m_jVprice.setBounds(210, 210, 30, 25);

        m_jPrintKB.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jPanel2.add(m_jPrintKB);
        m_jPrintKB.setBounds(210, 240, 30, 25);

        jTabbedPane1.addTab(AppLocal.getIntString("label.prodstock"), jPanel2); // NOI18N
        jTabbedPane1.addTab("Image", m_jImage);

        jPanel4.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jPanel4.setLayout(null);

        jLabel28.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel28.setText(bundle.getString("label.prodbuttonhtml")); // NOI18N
        jPanel4.add(jLabel28);
        jLabel28.setBounds(10, 10, 270, 20);

        m_jDisplay.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jScrollPane2.setViewportView(m_jDisplay);

        jPanel4.add(jScrollPane2);
        jScrollPane2.setBounds(10, 40, 480, 40);

        jButtonHTML.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jButtonHTML.setText(bundle.getString("button.htmltest")); // NOI18N
        jButtonHTML.setMargin(new java.awt.Insets(1, 1, 1, 1));
        jButtonHTML.setMaximumSize(new java.awt.Dimension(96, 72));
        jButtonHTML.setMinimumSize(new java.awt.Dimension(96, 72));
        jButtonHTML.setPreferredSize(new java.awt.Dimension(96, 72));
        jButtonHTML.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jButtonHTMLMouseClicked(evt);
            }
        });
        jButtonHTML.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonHTMLActionPerformed(evt);
            }
        });
        jPanel4.add(jButtonHTML);
        jButtonHTML.setBounds(205, 90, 110, 70);

        jLabel17.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel17.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel17.setText(bundle.getString("label.producthtmlguide")); // NOI18N
        jLabel17.setToolTipText("");
        jLabel17.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        jPanel4.add(jLabel17);
        jLabel17.setBounds(10, 200, 330, 100);
        jPanel4.add(jSeparator1);
        jSeparator1.setBounds(150, 300, 0, 12);

        jPanel5.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        jLabel32.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel32.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel32.setText(bundle.getString("label.fontexample")); // NOI18N
        jLabel32.setToolTipText(bundle.getString("tooltip.fontexample")); // NOI18N
        jLabel32.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseDragged(java.awt.event.MouseEvent evt) {
                jLabel32MouseDragged(evt);
            }
        });

        jLabel25.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel25.setText(bundle.getString("label.fontcolour")); // NOI18N
        jLabel25.setToolTipText(bundle.getString("tooltip.fontcolour")); // NOI18N
        jLabel25.setPreferredSize(new java.awt.Dimension(160, 30));

        jLabel29.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel29.setText(bundle.getString("label.fontsizelarge")); // NOI18N
        jLabel29.setToolTipText(bundle.getString("tooltip.fontsizelarge")); // NOI18N
        jLabel29.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        jLabel29.setPreferredSize(new java.awt.Dimension(160, 30));

        jLabel26.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel26.setText(bundle.getString("label.fontsize")); // NOI18N
        jLabel26.setToolTipText(bundle.getString("tooltip.fontsizesmall")); // NOI18N
        jLabel26.setPreferredSize(new java.awt.Dimension(160, 30));

        jLabel31.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel31.setText(bundle.getString("label.fontitalic")); // NOI18N
        jLabel31.setToolTipText(bundle.getString("tooltip.fontitalic")); // NOI18N
        jLabel31.setPreferredSize(new java.awt.Dimension(160, 30));

        jLabel30.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel30.setText(bundle.getString("label.fontweight")); // NOI18N
        jLabel30.setToolTipText(bundle.getString("tooltip.fontbold")); // NOI18N
        jLabel30.setPreferredSize(new java.awt.Dimension(160, 30));

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGap(7, 7, 7)
                        .addComponent(jLabel32, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addComponent(jLabel25, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addComponent(jLabel29, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addComponent(jLabel26, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel31, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel30, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(6, 6, 6))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addComponent(jLabel32, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(5, 5, 5)
                .addComponent(jLabel25, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(5, 5, 5)
                .addComponent(jLabel29, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(5, 5, 5)
                .addComponent(jLabel26, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(5, 5, 5)
                .addComponent(jLabel31, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(5, 5, 5)
                .addComponent(jLabel30, javax.swing.GroupLayout.DEFAULT_SIZE, 17, Short.MAX_VALUE)
                .addContainerGap())
        );

        jPanel4.add(jPanel5);
        jPanel5.setBounds(360, 110, 180, 220);

        jTabbedPane1.addTab("Button", jPanel4);

        jPanel3.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 5, 5, 5));
        jPanel3.setLayout(new java.awt.BorderLayout());

        txtAttributes.setFont(new java.awt.Font("Monospaced", 0, 14)); // NOI18N
        jScrollPane1.setViewportView(txtAttributes);

        jPanel3.add(jScrollPane1, java.awt.BorderLayout.CENTER);

        jTabbedPane1.addTab(AppLocal.getIntString("label.properties"), jPanel3); // NOI18N

        add(jTabbedPane1);
        jTabbedPane1.setBounds(10, 0, 800, 420);
    }// </editor-fold>//GEN-END:initComponents

    private void m_jInCatalogActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_m_jInCatalogActionPerformed

        if (m_jInCatalog.isSelected()) {
            m_jCatalogOrder.setEnabled(true);
        } else {
            m_jCatalogOrder.setEnabled(false);
            m_jCatalogOrder.setText(null);
        }

    }//GEN-LAST:event_m_jInCatalogActionPerformed

    private void m_jTaxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_m_jTaxActionPerformed

    }//GEN-LAST:event_m_jTaxActionPerformed

    private void m_jPriceSellTaxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_m_jPriceSellTaxActionPerformed

    }//GEN-LAST:event_m_jPriceSellTaxActionPerformed

    private void m_jRefFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_m_jRefFocusLost
// ADDED JG 19 NOV 12 - AUTOFILL CODE FIELD AS CANNOT BE NOT NULL
        setCode();
    }//GEN-LAST:event_m_jRefFocusLost

    private void m_jNameFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_m_jNameFocusLost
// ADDED JG 19 NOV 12 - AUTOFILL
        setDisplay();
    }//GEN-LAST:event_m_jNameFocusLost

    private void none(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_none

    }//GEN-LAST:event_none

    private void m_jCheckWarrantyReceiptActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_m_jCheckWarrantyReceiptActionPerformed

    }//GEN-LAST:event_m_jCheckWarrantyReceiptActionPerformed

    private void jButtonHTMLMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButtonHTMLMouseClicked
        setButtonHTML();
    }//GEN-LAST:event_jButtonHTMLMouseClicked

    private void jButtonHTMLActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonHTMLActionPerformed

    }//GEN-LAST:event_jButtonHTMLActionPerformed

    private void jLabel32MouseDragged(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel32MouseDragged
        // TODO for later
    }//GEN-LAST:event_jLabel32MouseDragged

    private void m_jCodetypeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_m_jCodetypeActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_m_jCodetypeActionPerformed

    private void m_jPriceSellBundleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_m_jPriceSellBundleActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_m_jPriceSellBundleActionPerformed

    private void box_sell_priceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_box_sell_priceActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_box_sell_priceActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField box_sell_price;
    private javax.swing.JTextField box_units;
    private javax.swing.JButton jButtonHTML;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel32;
    private javax.swing.JLabel jLabel33;
    private javax.swing.JLabel jLabel34;
    private javax.swing.JLabel jLabel35;
    private javax.swing.JLabel jLabel36;
    private javax.swing.JLabel jLabel37;
    private javax.swing.JLabel jLabel38;
    private javax.swing.JLabel jLabel39;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JComboBox m_jAtt;
    private javax.swing.JTextField m_jBundleUnit;
    private javax.swing.JTextField m_jCatalogOrder;
    private javax.swing.JComboBox m_jCategory;
    private javax.swing.JCheckBox m_jCheckWarrantyReceipt;
    private javax.swing.JTextField m_jCode;
    private javax.swing.JComboBox m_jCodetype;
    private javax.swing.JCheckBox m_jComment;
    private javax.swing.JTextPane m_jDisplay;
    private javax.swing.JTextField m_jGrossProfit;
    private com.openbravo.data.gui.JImageEditor m_jImage;
    private javax.swing.JCheckBox m_jInCatalog;
    private javax.swing.JCheckBox m_jKitchen;
    private javax.swing.JTextField m_jName;
    private javax.swing.JTextField m_jPriceBuy;
    private javax.swing.JTextField m_jPriceSell;
    private javax.swing.JTextField m_jPriceSellBundle;
    private javax.swing.JTextField m_jPriceSellTax;
    private javax.swing.JCheckBox m_jPrintKB;
    private javax.swing.JTextField m_jRef;
    private javax.swing.JCheckBox m_jScale;
    private javax.swing.JCheckBox m_jSendStatus;
    private javax.swing.JCheckBox m_jService;
    private javax.swing.JTextField m_jStockUnits;
    private javax.swing.JComboBox m_jTax;
    private javax.swing.JTextField m_jTextTip;
    private javax.swing.JLabel m_jTitle;
    private javax.swing.JCheckBox m_jVerpatrib;
    private javax.swing.JCheckBox m_jVprice;
    private javax.swing.JTextField m_jmargin;
    private javax.swing.JTextField m_jstockcost;
    private javax.swing.JTextField m_jstockvolume;
    private javax.swing.JLabel product_unit;
    private javax.swing.JTextArea txtAttributes;
    // End of variables declaration//GEN-END:variables

    public void setDefaultTaxAndCategory() {
        m_jTax.setSelectedIndex(0);
        m_jCategory.setSelectedIndex(1);
    }

}
