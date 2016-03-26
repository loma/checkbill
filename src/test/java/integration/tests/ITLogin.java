package integration.tests;

import com.athaydes.automaton.Swinger;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.openbravo.pos.forms.StartPOS;
import java.awt.Component;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JLabel;
import static org.junit.Assert.assertEquals;

public class ITLogin {

	static Swinger swinger;


	@BeforeClass
	public static void launchApp() throws Exception {
		System.out.println( "Launching Java App" );

	      	String[] arguments = new String[] {"/Users/loma/unicentaopos.properties"};
      		StartPOS.main(arguments);

		// get a Swing-driver, or Swinger
		swinger = Swinger.forSwingWindow();

		System.out.println( "App has been launched" );

		// let the window open and show before running tests
		explicitWait( 10000 );
	}

	@AfterClass
	public static void cleanup() {
		System.out.println( "Cleaning up" );
	}

	private static void explicitWait(int time) {
		try {
			Thread.sleep( time );
		} catch (InterruptedException ex) {
			Logger.getLogger(ITLogin.class.getName()).log(Level.SEVERE, null, ex);
		}
	}
	@Test
	public void windowTitleTest() {
		Component c = swinger.getAt( "m_jLbTitle" );
		JLabel l = (JLabel)c;
		assertEquals("uniCenta oPOS - Touch Friendly Point Of Sale", l.getText());
	}

	@Test
	public void loginTest() {
		swinger.clickOn( "user0" );
		explicitWait(1000);
		Component c = swinger.getAt( "active-user" );
		JLabel l = (JLabel)c;
		assertEquals("ຜູ້ຈັດການ", l.getText());
		swinger.clickOn( "logout" );
		explicitWait(1000);
		swinger.clickOn( "close" );
	}
}