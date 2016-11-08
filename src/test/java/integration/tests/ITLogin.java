package integration.tests;

import com.athaydes.automaton.Swinger;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.openbravo.pos.forms.StartPOS;
import java.awt.Component;
import javax.swing.JLabel;
import static org.junit.Assert.assertEquals;

public class ITLogin {

	static SafeSwinger swinger;


	@BeforeClass
	public static void launchApp() throws Exception {
		System.out.println( "Launching Java App" );
		String[] arguments = new String[] {"~/checkbill.properties"};
		StartPOS.main(arguments);
		Thread.sleep(5000);

		final Swinger forSwingWindow = Swinger.getUserWith(StartPOS.root);

		// get a Swing-driver, or Swinger
		swinger = new SafeSwinger(forSwingWindow);

		// init db
		swinger.clickOn("text:Yes");

		System.out.println( "App has been launched" );
	}

	@AfterClass
	public static void cleanup() {
		System.out.println( "Cleaning up" );
	}

	@Test
	public void windowTitleTest() {
		Component c = swinger.getAt( "m_jLbTitle" );
		JLabel l = (JLabel)c;
		assertEquals("ເຊັກບິນ - ລະບົບການຂາຍແບບສະບາຍ", l.getText());
	}

	@Test
	public void loginTest() {
		swinger.clickOn( "user0" );
		Component c = swinger.getAt( "active-user" );
		JLabel l = (JLabel)c;
		assertEquals("Administrator", l.getText());
		swinger.clickOn( "logout" );
		swinger.clickOn( "close" );
	}
}