package com.openbravo.pos.catalog;

import javax.swing.AbstractListModel;

class CategoriesListModel extends AbstractListModel {

	private final java.util.List m_aCategories;

	public CategoriesListModel(java.util.List aCategories) {
		m_aCategories = aCategories;
	}

	@Override
	public int getSize() {
		return m_aCategories.size();
	}

	@Override
	public Object getElementAt(int i) {
		return m_aCategories.get(i);
	}
}