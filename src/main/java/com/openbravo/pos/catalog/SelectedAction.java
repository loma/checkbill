package com.openbravo.pos.catalog;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import com.openbravo.pos.ticket.ProductInfoExt;

class SelectedAction implements ActionListener {

	/**
	 * 
	 */
	private final JCatalog jCatalog;
	private final ProductInfoExt prod;

	public SelectedAction(JCatalog jCatalog, ProductInfoExt prod) {
		this.jCatalog = jCatalog;
		this.prod = prod;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		this.jCatalog.fireSelectedProduct(prod);
	}
}