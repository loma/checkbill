package com.openbravo.pos.catalog;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import com.openbravo.pos.ticket.CategoryInfo;

class SelectedCategory implements ActionListener {

    private final JCatalog jCatalog;
    private final CategoryInfo category;

    public SelectedCategory(JCatalog jCatalog, CategoryInfo category) {
        this.jCatalog = jCatalog;
        this.category = category;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        this.jCatalog.showSubcategoryPanel(category);
    }
}