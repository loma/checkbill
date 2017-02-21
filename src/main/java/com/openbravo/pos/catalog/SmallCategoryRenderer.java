package com.openbravo.pos.catalog;

import java.awt.Component;

import javax.swing.DefaultListCellRenderer;
import javax.swing.ImageIcon;
import javax.swing.JList;

import com.openbravo.pos.ticket.CategoryInfo;

class SmallCategoryRenderer extends DefaultListCellRenderer {

	/**
	 * 
	 */
	private final JCatalog jCatalog;

	/**
	 * @param jCatalog
	 */
	SmallCategoryRenderer(JCatalog jCatalog) {
		this.jCatalog = jCatalog;
	}

	@Override
	public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
		super.getListCellRendererComponent(list, null, index, isSelected, cellHasFocus);
		CategoryInfo cat = (CategoryInfo) value;
		setText(cat.getName());
		setName("category_"+cat.getKey());
		setIcon(new ImageIcon(this.jCatalog.tnbcat.getThumbNail(cat.getImage())));
		return this;
	}
}