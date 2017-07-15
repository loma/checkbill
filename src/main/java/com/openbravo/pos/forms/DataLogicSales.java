package com.openbravo.pos.forms;

import com.openbravo.basic.BasicException;
import com.openbravo.data.loader.*;
import com.openbravo.data.model.Field;
import com.openbravo.data.model.Row;
import com.openbravo.format.Formats;
import com.openbravo.pos.customers.CustomerInfoExt;
import com.openbravo.pos.customers.CustomerTransaction;
import com.openbravo.pos.inventory.*;
import com.openbravo.pos.mant.FloorsInfo;
import com.openbravo.pos.payment.PaymentInfo;
import com.openbravo.pos.payment.PaymentInfoTicket;
import com.openbravo.pos.promotion.PromoInfo;
import com.openbravo.pos.promotion.PromoTypeInfo;
import com.openbravo.pos.ticket.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

public class DataLogicSales extends BeanFactoryDataSingle {

    protected Session s;
    protected Datas[] auxiliarDatas;
    protected Datas[] stockdiaryDatas;
    protected Datas[] paymenttabledatas;
    protected Datas[] stockdatas;
    protected Row productsRow;

    private String pName;
    private Double getTotal;
    private Double getTendered;
    private String getRetMsg;

    public static final String DEBT = "debt";
    public static final String DEBT_PAID = "debtpaid";
    protected static final String PREPAY = "prepay";
    private static final Logger logger = Logger.getLogger("com.openbravo.pos.forms.DataLogicSales");

    private String getCardName;

    public DataLogicSales() {
        stockdiaryDatas = new Datas[]{
            Datas.STRING,
            Datas.TIMESTAMP,
            Datas.INT,
            Datas.STRING,
            Datas.STRING,
            Datas.STRING,
            Datas.DOUBLE,
            Datas.DOUBLE,
            Datas.STRING
        };
        paymenttabledatas = new Datas[]{Datas.STRING, Datas.STRING, Datas.TIMESTAMP, Datas.STRING, Datas.STRING, Datas.DOUBLE, Datas.STRING};
        stockdatas = new Datas[]{Datas.STRING, Datas.STRING, Datas.STRING, Datas.DOUBLE, Datas.DOUBLE, Datas.DOUBLE};
        auxiliarDatas = new Datas[]{Datas.STRING, Datas.STRING, Datas.STRING, Datas.STRING, Datas.STRING, Datas.STRING};

        productsRow = new Row(
            new Field("ID", Datas.STRING, Formats.STRING),
            new Field(AppLocal.getIntString("label.prodref"), Datas.STRING, Formats.STRING, true, true, true),
            new Field(AppLocal.getIntString("label.prodbarcode"), Datas.STRING, Formats.STRING, false, true, true),
            new Field(AppLocal.getIntString("label.prodbarcodetype"), Datas.STRING, Formats.STRING, false, true, true),
            new Field(AppLocal.getIntString("label.prodname"), Datas.STRING, Formats.STRING, true, true, true),
            new Field(AppLocal.getIntString("label.prodpricebuy"), Datas.DOUBLE, Formats.CURRENCY, false, true, true),
            new Field(AppLocal.getIntString("label.prodpricesell"), Datas.DOUBLE, Formats.CURRENCY, false, true, true),
            new Field(AppLocal.getIntString("label.prodcategory"), Datas.STRING, Formats.STRING, false, false, true),
            new Field(AppLocal.getIntString("label.taxcategory"), Datas.STRING, Formats.STRING, false, false, true),
            new Field(AppLocal.getIntString("label.attributeset"), Datas.STRING, Formats.STRING, false, false, true),
            new Field("STOCKCOST", Datas.DOUBLE, Formats.CURRENCY),
            new Field("STOCKVOLUME", Datas.DOUBLE, Formats.DOUBLE),
            new Field("IMAGE", Datas.IMAGE, Formats.NULL),
            new Field("ISCOM", Datas.BOOLEAN, Formats.BOOLEAN),
            new Field("ISSCALE", Datas.BOOLEAN, Formats.BOOLEAN),
            new Field("ISKITCHEN", Datas.BOOLEAN, Formats.BOOLEAN),
            new Field("PRINTKB", Datas.BOOLEAN, Formats.BOOLEAN),
            new Field("SENDSTATUS", Datas.BOOLEAN, Formats.BOOLEAN),
            new Field("ISSERVICE", Datas.BOOLEAN, Formats.BOOLEAN),
            new Field("PROPERTIES", Datas.BYTES, Formats.NULL),
            new Field(AppLocal.getIntString("label.display"), Datas.STRING, Formats.STRING, false, true, true),
            new Field("ISVPRICE", Datas.BOOLEAN, Formats.BOOLEAN),
            new Field("ISVERPATRIB", Datas.BOOLEAN, Formats.BOOLEAN),
            new Field("TEXTTIP", Datas.STRING, Formats.STRING),
            new Field("WARRANTY", Datas.BOOLEAN, Formats.BOOLEAN),
            new Field(AppLocal.getIntString("label.stockunits"), Datas.DOUBLE, Formats.DOUBLE),
            new Field("ISCATALOG", Datas.BOOLEAN, Formats.BOOLEAN),
            new Field("CATORDER", Datas.INT, Formats.INT),
            new Field("BUNDLE_SELL_PRICE", Datas.DOUBLE, Formats.DOUBLE),
            new Field("BUNDLE_UNITS", Datas.DOUBLE, Formats.DOUBLE),
            new Field("BOX_SELL_PRICE", Datas.DOUBLE, Formats.DOUBLE),
            new Field("BOX_UNITS", Datas.DOUBLE, Formats.DOUBLE)
        );
    }

    public void init(Session s) {
        this.s = s;
    }

    public final Row getProductsRow() {
        return productsRow;
    }

    public final ProductInfoExt getProductInfo(String id) throws BasicException {
        return (ProductInfoExt) new PreparedSentence(s, "SELECT " + "ID, " + "REFERENCE, " + "CODE, " + "CODETYPE, "
            + "NAME, " + "PRICEBUY, " + "PRICESELL, " + "CATEGORY, " + "TAXCAT, " + "ATTRIBUTESET_ID, "
            + "STOCKCOST, " + "STOCKVOLUME, " + "IMAGE, " + "ISCOM, " + "ISSCALE, " + "ISKITCHEN, " + "PRINTKB, "
            + "SENDSTATUS, " + "ISSERVICE, " + "ATTRIBUTES, " + "DISPLAY, " + "ISVPRICE, " + "ISVERPATRIB, "
            + "TEXTTIP, " + "WARRANTY, " + "STOCKCURRENT.UNITS, BUNDLE_SELL_PRICE, BUNDLE_UNITS, BOX_SELL_PRICE, BOX_UNITS  "
            + "FROM STOCKCURRENT LEFT JOIN PRODUCTS ON (STOCKCURRENT.PRODUCT = PRODUCTS.ID) " + "WHERE ID = ? ",
            SerializerWriteString.INSTANCE, ProductInfoExt.getSerializerRead()).find(id);
    }

    public final ProductInfoExt getProductInfoByCode(String sCode) throws BasicException {
        return (ProductInfoExt) new PreparedSentence(s, "SELECT " + "ID, " + "REFERENCE, " + "CODE, " + "CODETYPE, "
            + "NAME, " + "PRICEBUY, " + "PRICESELL, " + "CATEGORY, " + "TAXCAT, " + "ATTRIBUTESET_ID, "
            + "STOCKCOST, " + "STOCKVOLUME, " + "IMAGE, " + "ISCOM, " + "ISSCALE, " + "ISKITCHEN, " + "PRINTKB, "
            + "SENDSTATUS, " + "ISSERVICE, " + "ATTRIBUTES, " + "DISPLAY, " + "ISVPRICE, " + "ISVERPATRIB, "
            + "TEXTTIP, " + "WARRANTY, " + "STOCKCURRENT.UNITS,  BUNDLE_SELL_PRICE, BUNDLE_UNITS, BOX_SELL_PRICE, BOX_UNITS  "
            + "FROM STOCKCURRENT RIGHT JOIN PRODUCTS ON (STOCKCURRENT.PRODUCT = PRODUCTS.ID) " + "WHERE CODE = ?",
            SerializerWriteString.INSTANCE, ProductInfoExt.getSerializerRead()).find(sCode);
    }

    public final ProductInfoExt getProductInfoByShortCode(String sCode) throws BasicException {
        return (ProductInfoExt) new PreparedSentence(s, "SELECT " + "ID, " + "REFERENCE, " + "CODE, " + "CODETYPE, "
            + "NAME, " + "PRICEBUY, " + "PRICESELL, " + "CATEGORY, " + "TAXCAT, " + "ATTRIBUTESET_ID, "
            + "STOCKCOST, " + "STOCKVOLUME, " + "IMAGE, " + "ISCOM, " + "ISSCALE, " + "ISKITCHEN, " + "PRINTKB, "
            + "SENDSTATUS, " + "ISSERVICE, " + "ATTRIBUTES, " + "DISPLAY, " + "ISVPRICE, " + "ISVERPATRIB, "
            + "TEXTTIP, " + "WARRANTY, " + "STOCKCURRENT.UNITS,  BUNDLE_SELL_PRICE, BUNDLE_UNITS, BOX_SELL_PRICE, BOX_UNITS  "
            + "FROM STOCKCURRENT RIGHT JOIN PRODUCTS ON (STOCKCURRENT.PRODUCT = PRODUCTS.ID) "
            + "WHERE SUBSTR( CODE, 3, 6 ) = ?", SerializerWriteString.INSTANCE, ProductInfoExt.getSerializerRead())
            .find(sCode.substring(2, 8));
    }

    public final ProductInfoExt getProductInfoByReference(String sReference) throws BasicException {
        return (ProductInfoExt) new PreparedSentence(s, "SELECT " + "ID, " + "REFERENCE, " + "CODE, " + "CODETYPE, "
            + "NAME, " + "PRICEBUY, " + "PRICESELL, " + "CATEGORY, " + "TAXCAT, " + "ATTRIBUTESET_ID, "
            + "STOCKCOST, " + "STOCKVOLUME, " + "IMAGE, " + "ISCOM, " + "ISSCALE, " + "ISKITCHEN, " + "PRINTKB, "
            + "SENDSTATUS, " + "ISSERVICE, " + "ATTRIBUTES, " + "DISPLAY, " + "ISVPRICE, " + "ISVERPATRIB, "
            + "TEXTTIP, " + "WARRANTY, " + "STOCKCURRENT.UNITS,  BUNDLE_SELL_PRICE, BUNDLE_UNITS, BOX_SELL_PRICE, BOX_UNITS   "
            + "FROM STOCKCURRENT RIGHT JOIN PRODUCTS ON (STOCKCURRENT.PRODUCT = PRODUCTS.ID) "
            + "WHERE REFERENCE = ?", SerializerWriteString.INSTANCE, ProductInfoExt.getSerializerRead())
            .find(sReference);
    }

    public final List<CategoryInfo> getRootCategories() throws BasicException {
        return new PreparedSentence(s, "SELECT " + "ID, " + "NAME, " + "IMAGE, " + "TEXTTIP, " + "CATSHOWNAME "
            + "FROM CATEGORIES " + "WHERE PARENTID IS NULL AND CATSHOWNAME = " + s.DB.TRUE() + " "
            + "ORDER BY NAME", null, CategoryInfo.getSerializerRead()).list();
    }

    public final List<CategoryInfo> getSubcategories(String category) throws BasicException {
        return new PreparedSentence(s, "SELECT " + "ID, " + "NAME, " + "IMAGE, " + "TEXTTIP, " + "CATSHOWNAME "
            + "FROM CATEGORIES WHERE PARENTID = ? ORDER BY NAME", SerializerWriteString.INSTANCE,
            CategoryInfo.getSerializerRead()).list(category);
    }

    public List<ProductInfoExt> getProductCatalog(String category) throws BasicException {
        return new PreparedSentence(s, "SELECT " + "P.ID, " + "P.REFERENCE, " + "P.CODE, " + "P.CODETYPE, "
            + "P.NAME, " + "P.PRICEBUY, " + "P.PRICESELL, " + "P.CATEGORY, " + "P.TAXCAT, " + "P.ATTRIBUTESET_ID, "
            + "P.STOCKCOST, " + "P.STOCKVOLUME, " + "P.IMAGE, " + "P.ISCOM, " + "P.ISSCALE, " + "P.ISKITCHEN, "
            + "P.PRINTKB, " + "P.SENDSTATUS, " + "P.ISSERVICE, " + "P.ATTRIBUTES, " + "P.DISPLAY, "
            + "P.ISVPRICE, " + "P.ISVERPATRIB, " + "P.TEXTTIP, " + "P.WARRANTY, " + "P.STOCKUNITS, "
            + "P.BUNDLE_SELL_PRICE, P.BUNDLE_UNITS, P.BOX_SELL_PRICE, P.BOX_UNITS "
            + "FROM PRODUCTS P, PRODUCTS_CAT O " + "WHERE P.ID = O.PRODUCT AND P.CATEGORY = ? "
            + "ORDER BY O.CATORDER, P.NAME ", SerializerWriteString.INSTANCE, ProductInfoExt.getSerializerRead())
            .list(category);
    }

    public List<ProductInfoExt> getProductComments(String id) throws BasicException {
        return new PreparedSentence(s, "SELECT " + "P.ID, " + "P.REFERENCE, " + "P.CODE, " + "P.CODETYPE, "
            + "P.NAME, " + "P.PRICEBUY, " + "P.PRICESELL, " + "P.CATEGORY, " + "P.TAXCAT, " + "P.ATTRIBUTESET_ID, "
            + "P.STOCKCOST, " + "P.STOCKVOLUME, " + "P.IMAGE, " + "P.ISCOM, " + "P.ISSCALE, " + "P.ISKITCHEN, "
            + "P.PRINTKB, " + "P.SENDSTATUS, " + "P.ISSERVICE, " + "P.ATTRIBUTES, " + "P.DISPLAY, "
            + "P.ISVPRICE, " + "P.ISVERPATRIB, " + "P.TEXTTIP, " + "P.WARRANTY, " + "P.STOCKUNITS "
            + "FROM PRODUCTS P, " + "PRODUCTS_CAT O, PRODUCTS_COM M "
            + "WHERE P.ID = O.PRODUCT AND P.ID = M.PRODUCT2 AND M.PRODUCT = ? " + "AND P.ISCOM = " + s.DB.TRUE()
            + " " + "ORDER BY O.CATORDER, P.NAME", SerializerWriteString.INSTANCE,
            ProductInfoExt.getSerializerRead()).list(id);
    }

    public List<PromoInfo> getCurrentPromos() throws BasicException {

        return new PreparedSentence(
            s,
            "SELECT "
            + "ID, "
            + "NAME, "
            + "STARTHOUR, "
            + "ENDHOUR, "
            + "ARTICLE, "
            + "ARTICLECATEGORY, "
            + "TYPE, "
            + "AMOUNT, "
            + "QTYMIN, "
            + "QTYMAX, "
            + "QTYSTEP, "
            + "AMOUNTSTEP, "
            + "BONUSARTICLE, "
            + "BONUSARTICLEDESC "
            + "FROM PROMO_HEADER "
            + "WHERE DATE(concat(substring(startdate, 1,4), "
            + "'-',substring(startdate, 5,2), '-',substring(startdate, 7,2))) <= current_date "
            + "AND DATE(concat(substring(enddate, 1,4),'-',substring(enddate, 5,2),'-',substring(enddate, 7,2))) >= current_date "
            + "AND time(concat(starthour,':00:00')) <= current_time AND time(concat(endhour,':00:00')) >= current_time "
            + "ORDER BY TYPE DESC", null, PromoInfo.getSerializerRead()).list();
    }

    public PromoInfo[] getPromos() throws BasicException {
        List<PromoInfo> _promos = getCurrentPromos();
        PromoInfo[] _tabpromo = new PromoInfo[_promos.size()];
        return _promos.toArray(_tabpromo);
    }

    public final SentenceList getPromoTypeList() {
        return new StaticSentence(s, "SELECT ID, " + "DESCRIPTION " + "FROM PROMO_TYPE " + "ORDER BY ID", null,
            new SerializerReadClass(PromoTypeInfo.class));
    }

    public final SentenceList getCatName(String id) {
        return new StaticSentence(s, "SELECT " + "ID " + "FROM CATEGORIES WHERE ID = ?", null,
            new SerializerReadClass(PromoTypeInfo.class));
    }

    public final CategoryInfo getCategoryInfo(String id) throws BasicException {
        return (CategoryInfo) new PreparedSentence(s, "SELECT " + "ID, " + "NAME, " + "IMAGE, " + "TEXTTIP, "
            + "CATSHOWNAME " + "FROM CATEGORIES " + "WHERE ID = ? " + "ORDER BY NAME",
            SerializerWriteString.INSTANCE, CategoryInfo.getSerializerRead()).find(id);
    }

    public final SentenceList getProductList() {
        return new StaticSentence(s, new QBFBuilder("SELECT " + "ID, " + "REFERENCE, " + "CODE, " + "CODETYPE, "
            + "NAME, " + "PRICEBUY, " + "PRICESELL, " + "CATEGORY, " + "TAXCAT, " + "ATTRIBUTESET_ID, "
            + "STOCKCOST, " + "STOCKVOLUME, " + "IMAGE, " + "ISCOM, " + "ISSCALE, " + "ISKITCHEN, " + "PRINTKB, "
            + "SENDSTATUS, " + "ISSERVICE, " + "ATTRIBUTES, " + "DISPLAY, " + "ISVPRICE, " + "ISVERPATRIB, "
            + "TEXTTIP, " + "WARRANTY, " + "STOCKCURRENT.UNITS "
            + "FROM STOCKCURRENT RIGHT OUTER JOIN PRODUCTS ON (STOCKCURRENT.PRODUCT = PRODUCTS.ID) "
            + "WHERE ?(QBF_FILTER) " + "ORDER BY REFERENCE, NAME", new String[]{"NAME", "PRICEBUY", "PRICESELL",
                "CATEGORY", "CODE", "UNITS"}), new SerializerWriteBasic(new Datas[]{Datas.OBJECT, Datas.STRING,
            Datas.OBJECT, Datas.DOUBLE, Datas.OBJECT, Datas.DOUBLE, Datas.OBJECT, Datas.STRING, Datas.OBJECT,
            Datas.STRING, Datas.OBJECT, Datas.DOUBLE,}), ProductInfoExt.getSerializerRead());
    }

    public SentenceList getProductListNormal() {
        return new StaticSentence(s, new QBFBuilder("SELECT " + "ID, " + "REFERENCE, " + "CODE, " + "CODETYPE, "
            + "NAME, " + "PRICEBUY, " + "PRICESELL, " + "CATEGORY, " + "TAXCAT, " + "ATTRIBUTESET_ID, "
            + "STOCKCOST, " + "STOCKVOLUME, " + "IMAGE, " + "ISCOM, " + "ISSCALE, " + "ISKITCHEN, " + "PRINTKB, "
            + "SENDSTATUS, " + "ISSERVICE, " + "ATTRIBUTES, " + "DISPLAY, " + "ISVPRICE, " + "ISVERPATRIB, "
            + "TEXTTIP, " + "WARRANTY, " + "STOCKUNITS " + "FROM PRODUCTS " + "WHERE ISCOM = " + s.DB.FALSE()
            + " AND ?(QBF_FILTER) ORDER BY REFERENCE", new String[]{"NAME", "PRICEBUY", "PRICESELL", "CATEGORY",
                "CODE"}), new SerializerWriteBasic(new Datas[]{Datas.OBJECT, Datas.STRING, Datas.OBJECT,
            Datas.DOUBLE, Datas.OBJECT, Datas.DOUBLE, Datas.OBJECT, Datas.STRING, Datas.OBJECT, Datas.STRING}),
            ProductInfoExt.getSerializerRead());
    }

    public SentenceList getProductListAuxiliar() {
        return new StaticSentence(s, new QBFBuilder("SELECT " + "ID, " + "REFERENCE, " + "CODE, " + "CODETYPE, "
            + "NAME, " + "PRICEBUY, " + "PRICESELL, " + "CATEGORY, " + "TAXCAT, " + "ATTRIBUTESET_ID, "
            + "STOCKCOST, " + "STOCKVOLUME, " + "IMAGE, " + "ISCOM, " + "ISSCALE, " + "ISKITCHEN, " + "PRINTKB, "
            + "SENDSTATUS, " + "ISSERVICE, " + "ATTRIBUTES, " + "DISPLAY, " + "ISVPRICE, " + "ISVERPATRIB, "
            + "TEXTTIP, " + "WARRANTY, " + "STOCKUNITS " + "FROM PRODUCTS " + "WHERE ISCOM = " + s.DB.TRUE()
            + " AND ?(QBF_FILTER) " + "ORDER BY REFERENCE", new String[]{"NAME", "PRICEBUY", "PRICESELL",
                "CATEGORY", "CODE"}), new SerializerWriteBasic(new Datas[]{Datas.OBJECT, Datas.STRING, Datas.OBJECT,
            Datas.DOUBLE, Datas.OBJECT, Datas.DOUBLE, Datas.OBJECT, Datas.STRING, Datas.OBJECT, Datas.STRING}),
            ProductInfoExt.getSerializerRead());
    }

    public SentenceList getTicketsList() {
        return new StaticSentence(s, new QBFBuilder("SELECT " + "T.TICKETID, " + "T.TICKETTYPE, " + "R.DATENEW, "
            + "P.NAME, " + "C.NAME, " + "SUM(PM.TOTAL) " + "FROM RECEIPTS "
            + "R JOIN TICKETS T ON R.ID = T.ID LEFT OUTER JOIN PAYMENTS PM "
            + "ON R.ID = PM.RECEIPT LEFT OUTER JOIN CUSTOMERS C "
            + "ON C.ID = T.CUSTOMER LEFT OUTER JOIN PEOPLE P ON T.PERSON = P.ID " + "WHERE ?(QBF_FILTER) "
            + "GROUP BY " + "T.ID, " + "T.TICKETID, " + "T.TICKETTYPE, " + "R.DATENEW, " + "P.NAME, " + "C.NAME "
            + "ORDER BY R.DATENEW DESC, T.TICKETID", new String[]{"T.TICKETID", "T.TICKETTYPE", "PM.TOTAL",
                "R.DATENEW", "R.DATENEW", "P.NAME", "C.NAME"}), new SerializerWriteBasic(new Datas[]{Datas.OBJECT,
            Datas.INT, Datas.OBJECT, Datas.INT, Datas.OBJECT, Datas.DOUBLE, Datas.OBJECT, Datas.TIMESTAMP,
            Datas.OBJECT, Datas.TIMESTAMP, Datas.OBJECT, Datas.STRING, Datas.OBJECT, Datas.STRING}),
            new SerializerReadClass(FindTicketsInfo.class));
    }

    public final SentenceList getUserList() {
        return new StaticSentence(s, "SELECT " + "ID, " + "NAME " + "FROM PEOPLE " + "ORDER BY NAME", null,
            new SerializerRead() {
            @Override
            public Object readValues(DataRead dr) throws BasicException {
                return new TaxCategoryInfo(dr.getString(1), dr.getString(2));
            }
        });
    }

    public final SentenceList getTaxList() {
        return new StaticSentence(s, "SELECT " + "ID, " + "NAME, " + "CATEGORY, " + "CUSTCATEGORY, " + "PARENTID, "
            + "RATE, " + "RATECASCADE, " + "RATEORDER " + "FROM TAXES " + "ORDER BY NAME", null,
            new SerializerRead() {
            @Override
            public Object readValues(DataRead dr) throws BasicException {
                return new TaxInfo(dr.getString(1), dr.getString(2), dr.getString(3), dr.getString(4),
                    dr.getString(5), dr.getDouble(6), dr.getBoolean(7), dr.getInt(8));
            }
        });
    }

    public final SentenceList getCategoriesList() {
        return new StaticSentence(s, "SELECT " + "ID, " + "NAME, " + "IMAGE, " + "TEXTTIP, " + "CATSHOWNAME "
            + "FROM CATEGORIES " + "ORDER BY NAME", null, CategoryInfo.getSerializerRead());
    }

    public final SentenceList getTaxCustCategoriesList() {
        return new StaticSentence(s, "SELECT " + "ID, " + "NAME " + "FROM TAXCUSTCATEGORIES " + "ORDER BY NAME", null,
            new SerializerRead() {
            @Override
            public Object readValues(DataRead dr) throws BasicException {
                return new TaxCustCategoryInfo(dr.getString(1), dr.getString(2));
            }
        });
    }

    @SuppressWarnings("unchecked")
    public final List<CustomerTransaction> getCustomersTransactionList() throws BasicException {
        return new PreparedSentence(
            s,
            "SELECT TICKETS.TICKETID, PRODUCTS.NAME AS PNAME, "
            + "SUM(TICKETLINES.UNITS) AS UNITS, "
            + "SUM(TICKETLINES.UNITS * TICKETLINES.PRICE) AS AMOUNT, "
            + "SUM(TICKETLINES.UNITS * TICKETLINES.PRICE * (1.0 + TAXES.RATE)) AS TOTAL, "
            + "RECEIPTS.DATENEW, CUSTOMERS.NAME AS CNAME "
            + "FROM RECEIPTS, CUSTOMERS, TICKETS, TICKETLINES "
            + "LEFT OUTER JOIN PRODUCTS ON TICKETLINES.PRODUCT = PRODUCTS.ID "
            + "LEFT OUTER JOIN TAXES ON TICKETLINES.TAXID = TAXES.ID  "
            + "WHERE CUSTOMERS.ID = TICKETS.CUSTOMER AND TICKETLINES.PRODUCT = PRODUCTS.ID AND RECEIPTS.ID = TICKETS.ID AND TICKETS.ID = TICKETLINES.TICKET "
            + "GROUP BY CUSTOMERS.NAME, RECEIPTS.DATENEW, TICKETS.TICKETID, PRODUCTS.NAME, TICKETS.TICKETTYPE "
            + "ORDER BY RECEIPTS.DATENEW DESC, PRODUCTS.NAME", null,
            CustomerTransaction.getSerializerRead()).list();
    }

    public final SentenceList getTaxCategoriesList() {
        return new StaticSentence(s, "SELECT " + "ID, " + "NAME " + "FROM TAXCATEGORIES " + "ORDER BY NAME", null,
            new SerializerRead() {
            @Override
            public Object readValues(DataRead dr) throws BasicException {
                return new TaxCategoryInfo(dr.getString(1), dr.getString(2));
            }
        });
    }

    public final SentenceList getAttributeSetList() {
        return new StaticSentence(s, "SELECT " + "ID, " + "NAME " + "FROM ATTRIBUTESET " + "ORDER BY NAME", null,
            new SerializerRead() {
            @Override
            public Object readValues(DataRead dr) throws BasicException {
                return new AttributeSetInfo(dr.getString(1), dr.getString(2));
            }
        });
    }

    public final SentenceList getLocationsList() {
        return new StaticSentence(s, "SELECT " + "ID, " + "NAME, " + "ADDRESS FROM LOCATIONS " + "ORDER BY NAME", null,
            new SerializerReadClass(LocationInfo.class));
    }

    public final SentenceList getFloorsList() {
        return new StaticSentence(s, "SELECT ID, NAME FROM FLOORS ORDER BY NAME", null, new SerializerReadClass(
            FloorsInfo.class));
    }

    public CustomerInfoExt findCustomerExt(String card) throws BasicException {
        return (CustomerInfoExt) new PreparedSentence(s, "SELECT " + "ID, " + "TAXID, " + "SEARCHKEY, " + "NAME, "
            + "CARD, " + "TAXCATEGORY, " + "NOTES, " + "MAXDEBT, " + "VISIBLE, " + "CURDATE, " + "CURDEBT, "
            + "FIRSTNAME, " + "LASTNAME, " + "EMAIL, " + "PHONE, " + "PHONE2, " + "FAX, " + "ADDRESS, "
            + "ADDRESS2, " + "POSTAL, " + "CITY, " + "REGION, " + "COUNTRY, " + "IMAGE " + "FROM CUSTOMERS "
            + "WHERE CARD = ? AND VISIBLE = " + s.DB.TRUE() + " " + "ORDER BY NAME",
            SerializerWriteString.INSTANCE, new CustomerExtRead()).find(card);
    }

    public CustomerInfoExt loadCustomerExt(String id) throws BasicException {
        return (CustomerInfoExt) new PreparedSentence(s, "SELECT " + "ID, " + "TAXID, " + "SEARCHKEY, " + "NAME, "
            + "CARD, " + "TAXCATEGORY, " + "NOTES, " + "MAXDEBT, " + "VISIBLE, " + "CURDATE, " + "CURDEBT, "
            + "FIRSTNAME, " + "LASTNAME, " + "EMAIL, " + "PHONE, " + "PHONE2, " + "FAX, " + "ADDRESS, "
            + "ADDRESS2, " + "POSTAL, " + "CITY, " + "REGION, " + "COUNTRY, " + "IMAGE "
            + "FROM CUSTOMERS WHERE ID = ?", SerializerWriteString.INSTANCE, new CustomerExtRead()).find(id);
    }

    public final boolean isCashActive(String id) throws BasicException {
        return new PreparedSentence(s, "SELECT MONEY FROM CLOSEDCASH WHERE DATEEND IS NULL AND MONEY = ?",
            SerializerWriteString.INSTANCE, SerializerReadString.INSTANCE).find(id) != null;
    }

    public final TicketInfo loadTicket(final int tickettype, final int ticketid) throws BasicException {
        TicketInfo ticket = (TicketInfo) new PreparedSentence(s, "SELECT " + "T.ID, " + "T.TICKETTYPE, "
            + "T.TICKETID, " + "R.DATENEW, " + "R.MONEY, " + "R.ATTRIBUTES, " + "P.ID, " + "P.NAME, "
            + "T.CUSTOMER " + "FROM RECEIPTS R " + "JOIN TICKETS T ON R.ID = T.ID "
            + "LEFT OUTER JOIN PEOPLE P ON T.PERSON = P.ID " + "WHERE T.TICKETTYPE = ? AND T.TICKETID = ? "
            + "ORDER BY R.DATENEW DESC", SerializerWriteParams.INSTANCE, new SerializerReadClass(TicketInfo.class))
            .find(new DataParams() {
                @Override
                public void writeValues() throws BasicException {
                    setInt(1, tickettype);
                    setInt(2, ticketid);
                }
            });
        if (ticket != null) {
            String customerid = ticket.getCustomerId();
            ticket.setCustomer(customerid == null ? null : loadCustomerExt(customerid));
            ticket.setLines(new PreparedSentence(
                s,
                "SELECT L.TICKET, L.LINE, L.PRODUCT, L.ATTRIBUTESETINSTANCE_ID, L.UNITS, L.PRICE, T.ID, T.NAME, T.CATEGORY, T.CUSTCATEGORY, T.PARENTID, T.RATE, T.RATECASCADE, T.RATEORDER, L.ATTRIBUTES "
                + "FROM TICKETLINES L, TAXES T WHERE L.TAXID = T.ID AND L.TICKET = ? ORDER BY L.LINE",
                SerializerWriteString.INSTANCE, new SerializerReadClass(TicketLineInfo.class)).list(ticket.getId()));
            ticket.setPayments(new PreparedSentence(
                s,
                "SELECT PAYMENT, TOTAL, TRANSID, TENDERED, CARDNAME FROM PAYMENTS WHERE RECEIPT = ?",
                SerializerWriteString.INSTANCE, new SerializerReadClass(PaymentInfoTicket.class)).list(ticket
                .getId()));
        }
        return ticket;
    }

    public final void saveTicket(final TicketInfo ticket, final String location) throws BasicException {
        Transaction t;
        t = new Transaction(s) {
            @Override
            public Object transact() throws BasicException {
                if (ticket.getTicketId() == 0) {
                    switch (ticket.getTicketType()) {
                        case TicketInfo.RECEIPT_NORMAL:
                            ticket.setTicketId(getNextTicketIndex());
                            break;
                        case TicketInfo.RECEIPT_REFUND:
                            ticket.setTicketId(getNextTicketRefundIndex());
                            break;
                        case TicketInfo.RECEIPT_PAYMENT:
                            ticket.setTicketId(getNextTicketPaymentIndex());
                            break;
                        case TicketInfo.RECEIPT_NOSALE:
                            ticket.setTicketId(getNextTicketPaymentIndex());
                            break;
                        default:
                            throw new BasicException();
                    }
                }
                new PreparedSentence(s,
                    "INSERT INTO RECEIPTS (ID, MONEY, DATENEW, ATTRIBUTES, PERSON) VALUES (?, ?, ?, ?, ?)",
                    SerializerWriteParams.INSTANCE).exec(new DataParams() {
                    @Override
                    public void writeValues() throws BasicException {
                        setString(1, ticket.getId());
                        setString(2, ticket.getActiveCash());
                        setTimestamp(3, ticket.getDate());
                        try {
                            ByteArrayOutputStream o = new ByteArrayOutputStream();
                            ticket.getProperties().storeToXML(o, AppLocal.APP_NAME, "UTF-8");
                            setBytes(4, o.toByteArray());
                        } catch (IOException e) {
                            setBytes(4, null);
                        }
                        setString(5, ticket.getProperty("person"));
                    }
                });

                new PreparedSentence(s,
                    "INSERT INTO TICKETS (ID, TICKETTYPE, TICKETID, PERSON, CUSTOMER) VALUES (?, ?, ?, ?, ?)",
                    SerializerWriteParams.INSTANCE).exec(new DataParams() {
                    @Override
                    public void writeValues() throws BasicException {
                        setString(1, ticket.getId());
                        setInt(2, ticket.getTicketType());
                        setInt(3, ticket.getTicketId());
                        setString(4, ticket.getUser().getId());
                        setString(5, ticket.getCustomerId());
                    }
                });

                SentenceExec ticketlineinsert = new PreparedSentence(
                    s,
                    "INSERT INTO TICKETLINES (TICKET, LINE, PRODUCT, ATTRIBUTESETINSTANCE_ID, UNITS, PRICE, TAXID, ATTRIBUTES) VALUES (?, ?, ?, ?, ?, ?, ?, ?)",
                    SerializerWriteBuilder.INSTANCE);

                //convert all bundle ticket line to be single items
                for (TicketLineInfo l : ticket.getLines()) {
                    if (l.getProductID().contains("_BOX")) {
                        l.setProductID(l.getProductID().replace("_BOX", ""));
                        ProductInfoExt info = getProductInfo(l.getProductID());
                        l.setPrice(info.getBoxPrice() / (info.getBoxUnits() * info.getBundleUnits()));
                        l.setMultiply(l.getMultiply() * info.getBoxUnits() * info.getBundleUnits());
                    }
                    if (l.getProductID().contains("_BUNDLE")) {
                        l.setProductID(l.getProductID().replace("_BUNDLE", ""));
                        ProductInfoExt info = getProductInfo(l.getProductID());
                        l.setPrice(info.getBundlePrice() / info.getBundleUnits());
                        l.setMultiply(l.getMultiply() * info.getBundleUnits());
                    }
                }
                

                for (TicketLineInfo l : ticket.getLines()) {

                    ticketlineinsert.exec(l);

                    if (l.getProductID() != null && l.isProductService() != true) {
                        getStockDiaryInsert().exec(
                            new Object[]{
                                UUID.randomUUID().toString(),
                                ticket.getDate(),
                                l.getMultiply() < 0.0 ? MovementReason.IN_REFUND.getKey() : MovementReason.OUT_SALE.getKey(), location, l.getProductID(),
                                l.getProductAttSetInstId(), -l.getMultiply(), l.getPrice(),
                                ticket.getUser().getName()});
                    }

                }
                final Payments payments = new Payments();
                SentenceExec paymentinsert = new PreparedSentence(
                    s,
                    "INSERT INTO PAYMENTS (ID, RECEIPT, PAYMENT, TOTAL, TRANSID, RETURNMSG, TENDERED, CARDNAME) VALUES (?, ?, ?, ?, ?, ?, ?, ?)",
                    SerializerWriteParams.INSTANCE);
                for (final PaymentInfo p : ticket.getPayments()) {
                    payments.addPayment(p.getName(), p.getTotal(), p.getPaid(), ticket.getReturnMessage());
                }

                while (payments.getSize() >= 1) {
                    paymentinsert.exec(new DataParams() {
                        @Override
                        public void writeValues() throws BasicException {
                            pName = payments.getFirstElement();
                            getTotal = payments.getPaidAmount(pName);
                            getTendered = payments.getTendered(pName);
                            getRetMsg = payments.getRtnMessage(pName);
                            payments.removeFirst(pName);
                            setString(1, UUID.randomUUID().toString());
                            setString(2, ticket.getId());
                            setString(3, pName);
                            setDouble(4, getTotal);
                            setString(5, ticket.getTransactionID());
                            setBytes(6, (byte[]) Formats.BYTEA.parseValue(getRetMsg));
                            setDouble(7, getTendered);
                            setString(8, getCardName);
                            payments.removeFirst(pName);
                        }
                    });

                    if ("debt".equals(pName) || "debtpaid".equals(pName)) {
                        ticket.getCustomer().updateCurDebt(getTotal, ticket.getDate());
                        getDebtUpdate().exec(new DataParams() {
                            @Override
                            public void writeValues() throws BasicException {
                                setDouble(1, ticket.getCustomer().getCurdebt());
                                setTimestamp(2, ticket.getCustomer().getCurdate());
                                setString(3, ticket.getCustomer().getId());
                            }
                        });
                    }
                }

                SentenceExec taxlinesinsert = new PreparedSentence(s,
                    "INSERT INTO TAXLINES (ID, RECEIPT, TAXID, BASE, AMOUNT)  VALUES (?, ?, ?, ?, ?)",
                    SerializerWriteParams.INSTANCE);

                if (ticket.getTaxes() != null) {
                    for (final TicketTaxInfo tickettax : ticket.getTaxes()) {
                        taxlinesinsert.exec(new DataParams() {
                            @Override
                            public void writeValues() throws BasicException {
                                setString(1, UUID.randomUUID().toString());
                                setString(2, ticket.getId());
                                setString(3, tickettax.getTaxInfo().getId());
                                setDouble(4, tickettax.getSubTotal());
                                setDouble(5, tickettax.getTax());
                            }
                        });
                    }
                }

                return null;
            }
        };
        t.execute();
    }

    public final void deleteTicket(final TicketInfo ticket, final String location) throws BasicException {
        Transaction t = new Transaction(s) {
            @Override
            public Object transact() throws BasicException {
                Date d = new Date();
                for (int i = 0; i < ticket.getLinesCount(); i++) {
                    if (ticket.getLine(i).getProductID() != null) {
                        getStockDiaryInsert().exec(
                            new Object[]{
                                UUID.randomUUID().toString(),
                                d,
                                ticket.getLine(i).getMultiply() >= 0.0 ? MovementReason.IN_REFUND.getKey() : MovementReason.OUT_SALE.getKey(), location,
                                ticket.getLine(i).getProductID(), ticket.getLine(i).getProductAttSetInstId(),
                                ticket.getLine(i).getMultiply(), ticket.getLine(i).getPrice(),
                                ticket.getUser().getName()});
                    }
                }

                for (PaymentInfo p : ticket.getPayments()) {
                    if ("debt".equals(p.getName()) || "debtpaid".equals(p.getName())) {
                        ticket.getCustomer().updateCurDebt(-p.getTotal(), ticket.getDate());
                        getDebtUpdate().exec(new DataParams() {
                            @Override
                            public void writeValues() throws BasicException {
                                setDouble(1, ticket.getCustomer().getCurdebt());
                                setTimestamp(2, ticket.getCustomer().getCurdate());
                                setString(3, ticket.getCustomer().getId());
                            }
                        });
                    }
                }

                // and delete the receipt
                new StaticSentence(s, "DELETE FROM TAXLINES WHERE RECEIPT = ?", SerializerWriteString.INSTANCE).exec(ticket.getId());
                new StaticSentence(s, "DELETE FROM PAYMENTS WHERE RECEIPT = ?", SerializerWriteString.INSTANCE).exec(ticket.getId());
                new StaticSentence(s, "DELETE FROM TICKETLINES WHERE TICKET = ?", SerializerWriteString.INSTANCE).exec(ticket.getId());
                new StaticSentence(s, "DELETE FROM TICKETS WHERE ID = ?", SerializerWriteString.INSTANCE).exec(ticket.getId());
                new StaticSentence(s, "DELETE FROM RECEIPTS WHERE ID = ?", SerializerWriteString.INSTANCE).exec(ticket.getId());
                return null;
            }
        };
        t.execute();
    }

    public final Integer getNextPickupIndex() throws BasicException {
        return (Integer) s.DB.getSequenceSentence(s, "PICKUP_NUMBER").find();
    }

    public final Integer getNextTicketIndex() throws BasicException {
        return (Integer) s.DB.getSequenceSentence(s, "TICKETSNUM").find();
    }

    public final Integer getNextTicketRefundIndex() throws BasicException {
        return (Integer) s.DB.getSequenceSentence(s, "TICKETSNUM_REFUND").find();
    }

    public final Integer getNextTicketPaymentIndex() throws BasicException {
        return (Integer) s.DB.getSequenceSentence(s, "TICKETSNUM_PAYMENT").find();
    }

    public final SentenceList getProductCatQBF() {
        return new StaticSentence(s, new QBFBuilder("SELECT " + "P.ID, " + "P.REFERENCE, " + "P.CODE, "
            + "P.CODETYPE, " + "P.NAME, " + "P.PRICEBUY, " + "P.PRICESELL, " + "P.CATEGORY, " + "P.TAXCAT, "
            + "P.ATTRIBUTESET_ID, " + "P.STOCKCOST, " + "P.STOCKVOLUME, " + "P.IMAGE, " + "P.ISCOM, "
            + "P.ISSCALE, " + "P.ISKITCHEN, " + "P.PRINTKB, " + "P.SENDSTATUS, " + "P.ISSERVICE, "
            + "P.ATTRIBUTES, " + "P.DISPLAY, " + "P.ISVPRICE, " + "P.ISVERPATRIB, " + "P.TEXTTIP, "
            + "P.WARRANTY, " + "P.STOCKUNITS, " + "CASE WHEN " + "C.PRODUCT IS NULL " + "THEN " + s.DB.FALSE()
            + " ELSE " + s.DB.TRUE() + " END, " + "C.CATORDER, P.BUNDLE_SELL_PRICE, P.BUNDLE_UNITS, P.BOX_SELL_PRICE, P.BOX_UNITS " 
            + "FROM PRODUCTS P LEFT OUTER JOIN PRODUCTS_CAT C "
            + "ON P.ID = C.PRODUCT " + "WHERE ?(QBF_FILTER) " + "ORDER BY P.REFERENCE", new String[]{"P.NAME",
                "P.PRICEBUY", "P.PRICESELL", "P.CATEGORY", "P.CODE"}), 
            new SerializerWriteBasic(new Datas[]{
            Datas.OBJECT, Datas.STRING, Datas.OBJECT, Datas.DOUBLE, Datas.OBJECT, Datas.DOUBLE, Datas.OBJECT,
            Datas.STRING, Datas.OBJECT, Datas.STRING}), productsRow.getSerializerRead());
    }

    public final SentenceExec getProductCatInsert() {
        return new SentenceExecTransaction(s) {
            @Override
            public int execInTransaction(Object params) throws BasicException {
                Object[] values = (Object[]) params;

                boolean hasBundle = values.length >= 30;
                
                int i = new PreparedSentence(s, "INSERT INTO PRODUCTS ( " + "ID, " + "REFERENCE, " + "CODE, "
                    + "CODETYPE, " + "NAME, " + "PRICEBUY, " + "PRICESELL, " + "CATEGORY, " + "TAXCAT, "
                    + "ATTRIBUTESET_ID, " + "STOCKCOST, " + "STOCKVOLUME, " + "IMAGE, " + "ISCOM, " + "ISSCALE, "
                    + "ISKITCHEN, " + "PRINTKB, " + "SENDSTATUS, " + "ISSERVICE, " + "ATTRIBUTES, " + "DISPLAY, "
                    + "ISVPRICE, " + "ISVERPATRIB, " + "TEXTTIP, " + "WARRANTY, " + "STOCKUNITS "+ 
                    (hasBundle ? ", BUNDLE_SELL_PRICE, BUNDLE_UNITS, BOX_SELL_PRICE, BOX_UNITS" : "")
                    +") "
                    + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?" +
                    (hasBundle ? ", ?, ?, ?, ?" : "")
                    +")",
                    new SerializerWriteBasicExt(productsRow.getDatas(), 
                        (hasBundle ? 
                            new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 28, 29, 30, 31} :
                            new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25}) 
                        )).exec(params);
                new PreparedSentence(s, "INSERT INTO STOCKCURRENT (LOCATION, PRODUCT, UNITS) VALUES ('0', ?, 0.0)",
                    new SerializerWriteBasicExt(productsRow.getDatas(),
                        new int[]{0})).exec(params);

                if (i > 0 && ((Boolean) values[26])) {
                    return new PreparedSentence(s, "INSERT INTO PRODUCTS_CAT (PRODUCT, CATORDER) VALUES (?, ?)",
                        new SerializerWriteBasicExt(productsRow.getDatas(), new int[]{0, 27})).exec(params);
                } else {
                    return i;
                }
            }
        };
    }

    public final SentenceExec getProductCatUpdate() {
        return new SentenceExecTransaction(s) {
            @Override
            public int execInTransaction(Object params) throws BasicException {
                Object[] values = (Object[]) params;

                boolean hasBundle = values.length >= 30;

                int i = new PreparedSentence(s, "UPDATE PRODUCTS SET " + "ID = ?, " + "REFERENCE = ?, " + "CODE = ?, "
                    + "CODETYPE = ?, " + "NAME = ?, " + "PRICEBUY = ?, " + "PRICESELL = ?, " + "CATEGORY = ?, "
                    + "TAXCAT = ?, " + "ATTRIBUTESET_ID = ?, " + "STOCKCOST = ?, " + "STOCKVOLUME = ?, "
                    + "IMAGE = ?, " + "ISCOM = ?, " + "ISSCALE = ?, " + "ISKITCHEN = ?, " + "PRINTKB = ?, "
                    + "SENDSTATUS = ?, " + "ISSERVICE = ?, " + "ATTRIBUTES = ?, " + "DISPLAY = ?, "
                    + "ISVPRICE = ?, " + "ISVERPATRIB = ?, " + "TEXTTIP = ?, " + "WARRANTY = ?, "
                    + "STOCKUNITS = ? " 
                    + (hasBundle ? ", BUNDLE_SELL_PRICE = ?, BUNDLE_UNITS = ? , BOX_SELL_PRICE = ?, BOX_UNITS = ?" : "")
                    + " WHERE ID = ?",
                    new SerializerWriteBasicExt(productsRow.getDatas(),
                        (hasBundle ? 
                            new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 28, 29, 30, 31, 0} :
                            new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 0}
                        )
                    )).exec(params);

                if (i > 0) {
                    if (((Boolean) values[26])) {
                        if (new PreparedSentence(s, "UPDATE PRODUCTS_CAT SET CATORDER = ? WHERE PRODUCT = ?",
                            new SerializerWriteBasicExt(productsRow.getDatas(),
                                new int[]{27, 0})).exec(params) == 0) {
                            new PreparedSentence(s, "INSERT INTO PRODUCTS_CAT (PRODUCT, CATORDER) VALUES (?, ?)",
                                new SerializerWriteBasicExt(productsRow.getDatas(), new int[]{0, 27})).exec(params);
                        }
                    } else {
                        new PreparedSentence(s, "DELETE FROM PRODUCTS_CAT WHERE PRODUCT = ?",
                            new SerializerWriteBasicExt(productsRow.getDatas(),
                                new int[]{0})).exec(params);
                    }
                }
                return i;
            }
        };
    }

    public final SentenceExec getProductCatDelete() {
        return new SentenceExecTransaction(s) {
            @Override
            public int execInTransaction(Object params) throws BasicException {
                new PreparedSentence(s, "DELETE FROM PRODUCTS_CAT WHERE PRODUCT = ?", new SerializerWriteBasicExt(productsRow.getDatas(), new int[]{0})).exec(params);
                new PreparedSentence(s, "DELETE FROM STOCKCURRENT WHERE PRODUCT = ?", new SerializerWriteBasicExt(productsRow.getDatas(), new int[]{0})).exec(params);
                new PreparedSentence(s, "DELETE FROM STOCKDIARY WHERE PRODUCT = ?", new SerializerWriteBasicExt(productsRow.getDatas(), new int[]{0})).exec(params);
                new PreparedSentence(s, "DELETE FROM STOCKLEVEL WHERE PRODUCT = ?", new SerializerWriteBasicExt(productsRow.getDatas(), new int[]{0})).exec(params);
                new PreparedSentence(s, "DELETE FROM TICKETLINES WHERE PRODUCT = ?", new SerializerWriteBasicExt(productsRow.getDatas(), new int[]{0})).exec(params);
                return new PreparedSentence(s, "DELETE FROM PRODUCTS WHERE ID = ?", new SerializerWriteBasicExt(productsRow.getDatas(), new int[]{0})).exec(params);

            }
        };
    }

    public final SentenceExec getDebtUpdate() {
        return new PreparedSentence(s, "UPDATE CUSTOMERS SET CURDEBT = ?, CURDATE = ? WHERE ID = ?",
            SerializerWriteParams.INSTANCE);
    }

    public final SentenceExec getStockDiaryInsert() {
        return new SentenceExecTransaction(s) {
            @Override
            public int execInTransaction(Object params) throws BasicException {
                Object[] objectParams = (Object[]) params;
                int updateresult = objectParams[5] == null
                    ? new PreparedSentence(
                        s,
                        "UPDATE STOCKCURRENT SET UNITS = (UNITS + ?) WHERE LOCATION = ? AND PRODUCT = ? AND ATTRIBUTESETINSTANCE_ID IS NULL",
                        new SerializerWriteBasicExt(stockdiaryDatas, new int[]{6, 3, 4})).exec(params)
                    : new PreparedSentence(
                        s,
                        "UPDATE STOCKCURRENT SET UNITS = (UNITS + ?) WHERE LOCATION = ? AND PRODUCT = ? AND ATTRIBUTESETINSTANCE_ID = ?",
                        new SerializerWriteBasicExt(stockdiaryDatas, new int[]{6, 3, 4, 5})).exec(params);

                if (updateresult == 0) {
                    new PreparedSentence(
                        s,
                        "INSERT INTO STOCKCURRENT (LOCATION, PRODUCT, ATTRIBUTESETINSTANCE_ID, UNITS) VALUES (?, ?, ?, ?)",
                        new SerializerWriteBasicExt(stockdiaryDatas, new int[]{3, 4, 5, 6})).exec(params);
                }

                if (objectParams.length > 14 && objectParams[14] != null) {
                    Datas[] temp = new Datas[]{
                        Datas.STRING, Datas.TIMESTAMP, Datas.INT, Datas.STRING, Datas.STRING, Datas.STRING, Datas.DOUBLE, Datas.DOUBLE, Datas.STRING,
                        Datas.TIMESTAMP, Datas.TIMESTAMP, Datas.TIMESTAMP, Datas.TIMESTAMP, Datas.TIMESTAMP, Datas.TIMESTAMP
                    };
                    return new PreparedSentence(
                        s,
                        "INSERT INTO STOCKDIARY (ID, DATENEW, REASON, LOCATION, PRODUCT, ATTRIBUTESETINSTANCE_ID, UNITS, PRICE, AppUser, EXPIRED_DATE) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                        new SerializerWriteBasicExt(temp, new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 14}))
                        .exec(params);
                } else {
                    return new PreparedSentence(
                        s,
                        "INSERT INTO STOCKDIARY (ID, DATENEW, REASON, LOCATION, PRODUCT, ATTRIBUTESETINSTANCE_ID, UNITS, PRICE, AppUser) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)",
                        new SerializerWriteBasicExt(stockdiaryDatas, new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8}))
                        .exec(params);
                }
            }
        };
    }

    public final SentenceExec getStockDiaryDelete() {
        return new SentenceExecTransaction(s) {
            @Override
            public int execInTransaction(Object params) throws BasicException {
                int updateresult = ((Object[]) params)[5] == null
                    ? new PreparedSentence(
                        s,
                        "UPDATE STOCKCURRENT SET UNITS = (UNITS - ?) WHERE LOCATION = ? AND PRODUCT = ? AND ATTRIBUTESETINSTANCE_ID IS NULL",
                        new SerializerWriteBasicExt(stockdiaryDatas, new int[]{6, 3, 4})).exec(params)
                    : new PreparedSentence(
                        s,
                        "UPDATE STOCKCURRENT SET UNITS = (UNITS - ?) WHERE LOCATION = ? AND PRODUCT = ? AND ATTRIBUTESETINSTANCE_ID = ?",
                        new SerializerWriteBasicExt(stockdiaryDatas, new int[]{6, 3, 4, 5})).exec(params);

                if (updateresult == 0) {
                    new PreparedSentence(
                        s,
                        "INSERT INTO STOCKCURRENT (LOCATION, PRODUCT, ATTRIBUTESETINSTANCE_ID, UNITS) VALUES (?, ?, ?, -(?))",
                        new SerializerWriteBasicExt(stockdiaryDatas, new int[]{3, 4, 5, 6})).exec(params);
                }
                return new PreparedSentence(s, "DELETE FROM STOCKDIARY WHERE ID = ?",
                    new SerializerWriteBasicExt(stockdiaryDatas, new int[]{0})).exec(params);
            }
        };
    }

    public final SentenceExec getPaymentMovementInsert() {
        return new SentenceExecTransaction(s) {
            @Override
            public int execInTransaction(Object params) throws BasicException {
                new PreparedSentence(s, "INSERT INTO RECEIPTS (ID, MONEY, DATENEW) VALUES (?, ?, ?)",
                    new SerializerWriteBasicExt(paymenttabledatas, new int[]{0, 1, 2})).exec(params);
                return new PreparedSentence(
                    s, "INSERT INTO PAYMENTS (ID, RECEIPT, PAYMENT, TOTAL, NOTES) VALUES (?, ?, ?, ?, ?)",
                    new SerializerWriteBasicExt(paymenttabledatas, new int[]{3, 0, 4, 5, 6})).exec(params);
            }
        };
    }

    public final SentenceExec getPaymentMovementDelete() {
        return new SentenceExecTransaction(s) {
            @Override
            public int execInTransaction(Object params) throws BasicException {
                new PreparedSentence(s, "DELETE FROM PAYMENTS WHERE ID = ?",
                    new SerializerWriteBasicExt(paymenttabledatas, new int[]{3})).exec(params);
                return new PreparedSentence(s, "DELETE FROM RECEIPTS WHERE ID = ?",
                    new SerializerWriteBasicExt(paymenttabledatas, new int[]{0})).exec(params);
            }
        };
    }

    public final double findProductStock(String warehouse, String id, String attsetinstid) throws BasicException {
        PreparedSentence p = attsetinstid == null
            ? new PreparedSentence(
                s, "SELECT UNITS FROM STOCKCURRENT WHERE LOCATION = ? AND PRODUCT = ? AND ATTRIBUTESETINSTANCE_ID IS NULL",
                new SerializerWriteBasic(Datas.STRING, Datas.STRING), SerializerReadDouble.INSTANCE)
            : new PreparedSentence(
                s, "SELECT UNITS FROM STOCKCURRENT WHERE LOCATION = ? AND PRODUCT = ? AND ATTRIBUTESETINSTANCE_ID = ?",
                new SerializerWriteBasic(Datas.STRING, Datas.STRING, Datas.STRING), SerializerReadDouble.INSTANCE);

        Double d = (Double) p.find(warehouse, id, attsetinstid);
        return d == null ? 0.0 : d;
    }

    public final SentenceExec getCatalogCategoryAdd() {
        return new StaticSentence(s, "INSERT INTO PRODUCTS_CAT(PRODUCT, CATORDER) SELECT ID, " + s.DB.INTEGER_NULL()
            + " FROM PRODUCTS WHERE CATEGORY = ?", SerializerWriteString.INSTANCE);
    }

    public final SentenceExec getCatalogCategoryDel() {
        return new StaticSentence(s, "DELETE FROM PRODUCTS_CAT WHERE PRODUCT = ANY (SELECT ID FROM PRODUCTS WHERE CATEGORY = ?)",
            SerializerWriteString.INSTANCE);
    }

    public final TableDefinition getTableCategories() {
        return new TableDefinition(s, "CATEGORIES",
            new String[]{"ID", "NAME", "PARENTID", "IMAGE", "TEXTTIP", "CATSHOWNAME"},
            new String[]{"ID", AppLocal.getIntString("Label.Name"), "", AppLocal.getIntString("label.image")},
            new Datas[]{Datas.STRING, Datas.STRING, Datas.STRING, Datas.IMAGE, Datas.STRING, Datas.BOOLEAN},
            new Formats[]{Formats.STRING, Formats.STRING, Formats.STRING, Formats.NULL, Formats.STRING, Formats.BOOLEAN},
            new int[]{0});
    }

    public final TableDefinition getTableTaxes() {
        return new TableDefinition(s, "TAXES",
            new String[]{"ID", "NAME", "CATEGORY", "CUSTCATEGORY", "PARENTID", "RATE", "RATECASCADE", "RATEORDER"},
            new String[]{"ID", AppLocal.getIntString("Label.Name"), AppLocal.getIntString("label.taxcategory"), AppLocal.getIntString("label.custtaxcategory"), AppLocal.getIntString("label.taxparent"), AppLocal.getIntString("label.dutyrate"), AppLocal.getIntString("label.cascade"), AppLocal.getIntString("label.order")},
            new Datas[]{Datas.STRING, Datas.STRING, Datas.STRING, Datas.STRING, Datas.STRING, Datas.DOUBLE, Datas.BOOLEAN, Datas.INT},
            new Formats[]{Formats.STRING, Formats.STRING, Formats.STRING, Formats.STRING, Formats.STRING, Formats.PERCENT, Formats.BOOLEAN, Formats.INT},
            new int[]{0});
    }

    public final TableDefinition getTableTaxCustCategories() {
        return new TableDefinition(s, "TAXCUSTCATEGORIES",
            new String[]{"ID", "NAME"},
            new String[]{"ID", AppLocal.getIntString("Label.Name")},
            new Datas[]{Datas.STRING, Datas.STRING},
            new Formats[]{Formats.STRING, Formats.STRING},
            new int[]{0});
    }

    public final TableDefinition getTableTaxCategories() {
        return new TableDefinition(s, "TAXCATEGORIES",
            new String[]{"ID", "NAME"},
            new String[]{"ID", AppLocal.getIntString("Label.Name")},
            new Datas[]{Datas.STRING, Datas.STRING},
            new Formats[]{Formats.STRING, Formats.STRING},
            new int[]{0});
    }

    public final TableDefinition getTableLocations() {
        return new TableDefinition(s, "LOCATIONS",
            new String[]{"ID", "NAME", "ADDRESS"},
            new String[]{"ID", AppLocal.getIntString("label.locationname"), AppLocal.getIntString("label.locationaddress")},
            new Datas[]{Datas.STRING, Datas.STRING, Datas.STRING},
            new Formats[]{Formats.STRING, Formats.STRING, Formats.STRING},
            new int[]{0});
    }

    protected static class CustomerExtRead implements SerializerRead {

        @Override
        public Object readValues(DataRead dr) throws BasicException {
            CustomerInfoExt c = new CustomerInfoExt(dr.getString(1));
            c.setTaxid(dr.getString(2));
            c.setSearchkey(dr.getString(3));
            c.setName(dr.getString(4));
            c.setCard(dr.getString(5));
            c.setTaxCustomerID(dr.getString(6));
            c.setNotes(dr.getString(7));
            c.setMaxdebt(dr.getDouble(8));
            c.setVisible(dr.getBoolean(9));
            c.setCurdate(dr.getTimestamp(10));
            c.setCurdebt(dr.getDouble(11));
            c.setFirstname(dr.getString(12));
            c.setLastname(dr.getString(13));
            c.setEmail(dr.getString(14));
            c.setPhone(dr.getString(15));
            c.setPhone2(dr.getString(16));
            c.setFax(dr.getString(17));
            c.setAddress(dr.getString(18));
            c.setAddress2(dr.getString(19));
            c.setPostal(dr.getString(20));
            c.setCity(dr.getString(21));
            c.setRegion(dr.getString(22));
            c.setCountry(dr.getString(23));
            c.setImage(dr.getString(24));
            return c;
        }
    }
}
