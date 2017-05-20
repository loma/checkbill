package com.openbravo.pos.catalog;

import com.openbravo.beans.JFlowPanel;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionListener;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.SwingConstants;

public class JProductsSelector extends javax.swing.JPanel {

    private JFlowPanel flowpanel;

    public JProductsSelector() {
        initComponents();
        flowpanel = new JFlowPanel();
        add(flowpanel, BorderLayout.CENTER);
    }

    public void addProduct(Image img, String display, ActionListener al, String textTip) {
        JButton btn = new JButton();
        btn.applyComponentOrientation(getComponentOrientation());
        btn.setText(display);
        btn.setIcon(new ImageIcon(img));
        btn.setFocusPainted(false);
        if (textTip != null) {
            btn.setToolTipText(textTip);
        }
        btn.setFocusable(false);
        btn.setRequestFocusEnabled(false);
        btn.setHorizontalTextPosition(SwingConstants.CENTER);
        btn.setVerticalTextPosition(SwingConstants.BOTTOM);
        btn.setMargin(new Insets(2, 2, 2, 2));
        btn.setMaximumSize(new Dimension(80, 70));
        btn.setPreferredSize(new Dimension(80, 70));
        btn.setMinimumSize(new Dimension(80, 70));
        btn.addActionListener(al);
        flowpanel.add(btn);
    }

    private void initComponents() {

        setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        setLayout(new java.awt.BorderLayout());
    }
}
