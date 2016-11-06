/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package unit.tests;

import com.openbravo.format.DoubleUtils;
import junit.framework.TestCase;

/**
 *
 * @author loma
 */
public class TestSimple extends TestCase {
	
	public void testSimple() {
		assertEquals(1,1);
	}

	public void testFixDecimals(){
		assertEquals(5.4, DoubleUtils.fixDecimals(5.4));
	}
}
