package com.openbravo.pos.forms;

import com.openbravo.basic.BasicException;
import com.openbravo.format.Formats;
import com.openbravo.pos.ticket.ProductInfoExt;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.LookAndFeel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import org.pushingpixels.substance.api.SubstanceLookAndFeel;
import org.pushingpixels.substance.api.SubstanceSkin;
import com.openbravo.pos.ticket.TicketInfo;
import java.awt.Font;

public class StartPOS {

	private static final Logger logger = Logger.getLogger("com.openbravo.pos.forms.StartPOS");

	public StartPOS() {
	}

	public static void setUIFont(javax.swing.plaf.FontUIResource f) {
		java.util.Enumeration keys = UIManager.getDefaults().keys();
		while (keys.hasMoreElements()) {
			Object key = keys.nextElement();
			Object value = UIManager.get(key);
			if (value != null && value instanceof javax.swing.plaf.FontUIResource) {
				UIManager.put(key, f);
			}
		}
	}

	public static JRootFrame root;
	
	public static String TempProductCode = null;

	public static void main(final String args[]) throws InterruptedException {

        
		java.awt.EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				AppConfig config = new AppConfig(args);
				config.load();

				setFont();
				setLocale(config);
				setFormatPatterns(config);
				setLookAndFeel(config);
				setHostName(config);
				initScreenMode(config);

			}

			private void setHostName(AppConfig config) {
				TicketInfo.setHostname(config.getProperty("machine.hostname"));
			}

			private void setLocale(AppConfig config) {
				String slang = config.getProperty("user.language");
				String scountry = config.getProperty("user.country");
				String svariant = config.getProperty("user.variant");
				if (slang != null && !slang.equals("") && scountry != null && svariant != null) {
					Locale.setDefault(new Locale(slang, scountry, svariant));
				}
			}

			private void setFormatPatterns(AppConfig config) {
				Formats.setIntegerPattern(config.getProperty("format.integer"));
				Formats.setDoublePattern(config.getProperty("format.double"));
				Formats.setCurrencyPattern(config.getProperty("format.currency"));
				Formats.setPercentPattern(config.getProperty("format.percent"));
				Formats.setDatePattern(config.getProperty("format.date"));
				Formats.setTimePattern(config.getProperty("format.time"));
				Formats.setDateTimePattern(config.getProperty("format.datetime"));
			}

			private void initScreenMode(AppConfig config) {
				if ("fullscreen".equals(config.getProperty("machine.screenmode"))) {
					JRootKiosk rootkiosk = new JRootKiosk();
					rootkiosk.initFrame(config);
				} else {
					JRootFrame rootframe = new JRootFrame();
					rootframe.initFrame(config);
					root = rootframe;
				}
			}

			private void setLookAndFeel(AppConfig config) {
				try {
					setLookAndFeel(config.getProperty("swing.defaultlaf"));
				} catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
					logger.log(Level.WARNING, "Cannot set Look and Feel", e);
					try {
						setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
					} catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e2) {
						Logger.getLogger(StartPOS.class.getName()).log(Level.SEVERE, null, e2);
					}
				}
			}

			private void setLookAndFeel(final String lookAndFeelClassName) throws UnsupportedLookAndFeelException, ClassNotFoundException, IllegalAccessException, InstantiationException {
				Object lafMetal = Class.forName(lookAndFeelClassName).newInstance();
				if (lafMetal instanceof LookAndFeel) {
					UIManager.setLookAndFeel((LookAndFeel) lafMetal);
				} else if (lafMetal instanceof SubstanceSkin) {
					SubstanceLookAndFeel.setSkin((SubstanceSkin) lafMetal);
				}
			}
		});
	}

	private static void setFont() {
		setUIFont(new javax.swing.plaf.FontUIResource("Saysettha OT", Font.PLAIN, 18));
	}
}
