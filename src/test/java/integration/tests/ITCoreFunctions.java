package integration.tests;

import com.athaydes.automaton.Swinger;
import com.openbravo.pos.forms.StartPOS;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class ITCoreFunctions {

    static SafeSwinger swinger;

    @BeforeClass
    public static void launchApp() throws Exception {
        System.out.println("Launching Java App");
        String[] arguments = new String[]{"/Users/loma/checkbill.properties"};
        StartPOS.main(arguments);
        Thread.sleep(30000);

        final Swinger forSwingWindow = Swinger.getUserWith(StartPOS.root);

        // get a Swing-driver, or Swinger
        swinger = new SafeSwinger(forSwingWindow);

        // init db
        swinger.clickOn("text:Yes");

        System.out.println("App has been launched");
    }

    @AfterClass
    public static void cleanup() {
        System.out.println("Cleaning up");
    }

    @Test
    public void addNewCategory() throws InterruptedException {
        swinger.clickOn("user0");
        swinger.clickOn("toggle-menu");
        swinger.clickOn("text:ສາງສິນຄ້າ");
        swinger.clickOn("name:Menu.Categories");
        swinger.clickOn("category_name").type("new category");
        swinger.clickOn("save_button");
        swinger.clickOn("toggle-menu");
        swinger.clickOn("text:ການຂາຍ");
        swinger.clickOn("logout");

        swinger.clickOn("user0");
        swinger.clickOn("toggle-menu");
        swinger.clickOn("text:ສາງສິນຄ້າ");
        swinger.clickOn("text:ສິນຄ້າ");

        swinger.clickOn("sell_price_plus_tax").type("10000");
        swinger.clickOn("buy_price").type("8000");
        swinger.clickOn("product_code").type("1234");
        swinger.clickOn("product_name").type("test item 1234");
        swinger.clickOn("product_category").clickOn("text:new category");
        swinger.clickOn("product_tax").clickOn("text:Tax Exempt");
        swinger.clickOn("save_button");

        swinger.clickOn("toggle-menu");
        swinger.clickOn("text:ການຂາຍ");
        swinger.clickOn("logout");

        swinger.clickOn("user0");
        swinger.clickOn("toggle-menu");
        swinger.clickOn("text:ສາງສິນຄ້າ");
        swinger.clickOn("text:Menu.StockDiary=ບັນທຶກສາງສິນຄ້າ");
        SafeSwinger catalogSwinger = new SafeSwinger(Swinger.getUserWith(swinger.getAt("catcontainer")));
        catalogSwinger.clickOn("text:new category");
        swinger.clickOn("test item 1234");
        swinger.clickOn("units").type("100");
        swinger.clickOn("expired_date").type("May 27, 2018 2:19:00 PM");
        swinger.clickOn("save_button");

        swinger.clickOn("toggle-menu");
        swinger.clickOn("text:ການຂາຍ");
        swinger.clickOn("logout");

        swinger.clickOn("user0");
        catalogSwinger = new SafeSwinger(Swinger.getUserWith(swinger.getAt("product_catalog")));
        catalogSwinger.clickOn("text:new category");

        swinger.clickOn("test item 1234");
        swinger.clickOn("text:ຈ່າຍ");
        swinger.clickOn("cash_100000.0");

        Thread.sleep(200);
        swinger.type("\n");
        Thread.sleep(200);
        swinger.type("\n");

        swinger.clickOn("logout");
    }
}
