package com.openbravo.pos.forms;

import java.io.*;
import java.util.Locale;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.lang.SystemUtils;

public class AppConfig implements AppProperties {

	private static final Logger logger = Logger.getLogger("com.openbravo.pos.forms.AppConfig");
	private Properties m_propsconfig;
	private File configfile;

	public AppConfig(String[] args) {
		if (args.length == 0)
			init(getDefaultConfig());
		else 
			init(new File(args[0]));
	}

	public AppConfig(File configfile) {
		init(configfile);
	}

	private void init(File configfile) {
		this.configfile = configfile;
		m_propsconfig = new Properties();
		logger.log(Level.INFO, "Reading configuration file: {0}", configfile.getAbsolutePath());
	}

	private File getDefaultConfig() {
		return new File(new File(System.getProperty("user.home")), AppLocal.APP_ID + ".properties");
	}

	@Override
	public String getProperty(String sKey) {
		return m_propsconfig.getProperty(sKey);
	}

	@Override
	public String getHost() {
		return getProperty("machine.hostname");
	}

	@Override
	public File getConfigFile() {
		return configfile;
	}

	public void setProperty(String sKey, String sValue) {
		if (sValue == null)
			m_propsconfig.remove(sKey);
		else
			m_propsconfig.setProperty(sKey, sValue);
	}

	private String getLocalHostName() {
		try {
			return java.net.InetAddress.getLocalHost().getHostName();
		} catch (java.net.UnknownHostException eUH) {
			return "localhost";
		}
	}

	public boolean delete() {
		loadDefault();
		return configfile.delete();
	}

	public void load() {
		loadDefault();
		try {
			InputStream in = new FileInputStream(configfile);
			m_propsconfig.load(in);
			in.close();
		} catch (IOException e) {
			loadDefault();
		}
	}

	public Boolean isPriceWith00() {
		String prop = getProperty("pricewith00");
		if (prop == null)
			return false;

		return prop.equals("true");
	}

	public void save() throws IOException {
		OutputStream out = new FileOutputStream(configfile);
		m_propsconfig.store(out, AppLocal.APP_NAME + ". Configuration file.");
		out.close();
	}

	private void loadDefault() {

		m_propsconfig = new Properties();

		String dirname = System.getProperty("dirname.path");
		dirname = dirname == null ? "./" : dirname;

		m_propsconfig.setProperty("db.driverlib", new File(new File(dirname), "lib/derby.jar").getAbsolutePath());
		m_propsconfig.setProperty("db.driver", "org.apache.derby.jdbc.EmbeddedDriver");
		m_propsconfig.setProperty("db.URL", "jdbc:derby:" + new File(new File(System.getProperty("user.home")), AppLocal.APP_ID + "-database").getAbsolutePath() + ";create=true");
		m_propsconfig.setProperty("db.user", "");
		m_propsconfig.setProperty("db.password", "");

		m_propsconfig.setProperty("machine.hostname", getLocalHostName());

		Locale l = Locale.getDefault();
		m_propsconfig.setProperty("user.language", "en");
		m_propsconfig.setProperty("user.country", l.getCountry());
		m_propsconfig.setProperty("user.variant", l.getVariant());

		String defaultLookAndFeel = "com.sun.java.swing.plaf.gtk.GTKLookAndFeel";
		if (SystemUtils.IS_OS_WINDOWS) {
			defaultLookAndFeel = "com.sun.java.swing.plaf.windows.WindowsLookAndFeel";
		}
		m_propsconfig.setProperty("swing.defaultlaf", System.getProperty("swing.defaultlaf", defaultLookAndFeel));
		m_propsconfig.setProperty("format.currency", System.getProperty("format.currency", "#,###'kip'"));

		m_propsconfig.setProperty("machine.printer", "screen");
		m_propsconfig.setProperty("machine.printer.2", "Not defined");
		m_propsconfig.setProperty("machine.printer.3", "Not defined");
		m_propsconfig.setProperty("machine.printer.4", "Not defined");
		m_propsconfig.setProperty("machine.printer.5", "Not defined");
		m_propsconfig.setProperty("machine.printer.6", "Not defined");

		m_propsconfig.setProperty("machine.display", "screen");
		m_propsconfig.setProperty("machine.scale", "Not defined");
		m_propsconfig.setProperty("machine.screenmode", "window"); // fullscreen / window
		m_propsconfig.setProperty("machine.ticketsbag", "standard");
		m_propsconfig.setProperty("machine.scanner", "Not defined");

		m_propsconfig.setProperty("payment.gateway", "external");
		m_propsconfig.setProperty("payment.magcardreader", "Not defined");
		m_propsconfig.setProperty("payment.testmode", "false");
		m_propsconfig.setProperty("payment.commerceid", "");
		m_propsconfig.setProperty("payment.commercepassword", "password");

		m_propsconfig.setProperty("machine.printername", "(Default)");

		m_propsconfig.setProperty("paper.receipt.x", "10");
		m_propsconfig.setProperty("paper.receipt.y", "10");
		m_propsconfig.setProperty("paper.receipt.width", "190");
		m_propsconfig.setProperty("paper.receipt.height", "546");
		m_propsconfig.setProperty("paper.receipt.mediasizename", "A4");

		m_propsconfig.setProperty("paper.standard.x", "72");
		m_propsconfig.setProperty("paper.standard.y", "72");
		m_propsconfig.setProperty("paper.standard.width", "451");
		m_propsconfig.setProperty("paper.standard.height", "698");
		m_propsconfig.setProperty("paper.standard.mediasizename", "A4");

		m_propsconfig.setProperty("machine.uniqueinstance", "false");

		m_propsconfig.setProperty("screen.receipt.columns", "42");

		m_propsconfig.setProperty("ticket.width", "800");
	}
}
