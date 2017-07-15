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
        while (i++ < 30) {
            try {
                return forSwingWindow.getAt(name);
            } catch (Exception e) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ex) {
                }
            }
        }
        System.out.println("Cant find element " + name + " after 30 seconds");
        return null;
    }

    SafeSwinger clickOn(String name) {
        int i = 0;
        while (i++ < 30) {
            try {
                forSwingWindow.clickOn(name);
                return this;
            } catch (Exception e) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ex) {
                }
            }
        }

        System.out.println("Cant click element " + name + " after 30 seconds");
        return null;
    }

    SafeSwinger type(String text) {
        int i = 0;
        while (i++ < 30) {
            try {
                forSwingWindow.type(text);
                return this;
            } catch (Exception e) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ex) {
                }
            }
        }

        System.out.println("Cant type element " + text + " after 30 seconds");
        return null;
    }

}
