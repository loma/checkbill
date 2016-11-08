/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package integration.tests;

import com.athaydes.automaton.Swinger;
import java.awt.Component;

class SafeSwinger {

	private final Swinger forSwingWindow;

	public SafeSwinger(Swinger forSwingWindow) {
		this.forSwingWindow = forSwingWindow;
	}

	Component getAt(String name) {
		int i = 0;
		while (i++ < 100)
			try {
				return forSwingWindow.getAt(name);
			} catch (Exception e) {
				try {
					Thread.sleep(100);
				} catch (InterruptedException ex) {
				}
			}
		System.out.println("Cant find element after 10 seconds");
		return null;
	}

	void clickOn(String name) {
		int i = 0;
		while (i++ < 100)
			try {
				forSwingWindow.clickOn(name);
				return;
			} catch (Exception e) {
				try {
					Thread.sleep(100);
				} catch (InterruptedException ex) {
				}
			}

		System.out.println("Cant click element after 10 seconds");
	}
	
	
}
