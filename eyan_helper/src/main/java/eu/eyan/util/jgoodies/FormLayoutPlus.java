package eu.eyan.util.jgoodies;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.LayoutManager2;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout.LayoutInfo;
import com.jgoodies.forms.layout.RowSpec;

public class FormLayoutPlus implements LayoutManager2 {
	private final com.jgoodies.forms.layout.FormLayout formLayout;
	private int rows = 0;
	private final Container container;

	public FormLayoutPlus(Container container, String colSpec) {
		this.container = container;
		formLayout = new com.jgoodies.forms.layout.FormLayout(colSpec);
		container.setLayout(this);
	}

	public FormLayoutPlus(Container container, String colSpec, String rowSpec, Component component) {
		this(container, colSpec);
		appendRow(rowSpec, component);
	}

	public FormLayoutPlus(Container container, String colSpec, String rowSpec, FormLayoutPlus formLayout) {
		this(container, colSpec);
		appendRow(rowSpec, formLayout);
	}

	public int appendRow(String... rowSpecs) {
		for (String rowSpec : rowSpecs) {
			appendRow(RowSpec.decode(rowSpec));
			rows++;
		}
		return rows;
	}

	public Container getComponent() {
		return container;
	}
	
	public FormLayoutPlus appendRow(String rowSpec, Component component) {
		appendRow(RowSpec.decode(rowSpec));
		rows++;
		if(component!=null){
			container.add(component, CC.xy(1, rows));
		}
		return this;
	}

	public FormLayoutPlus appendRow(String rowSpec, FormLayoutPlus formLayout) {
		return appendRow(rowSpec, formLayout.getComponent());
	}
	
	/// DELEGATES ///
	public int hashCode() {
		return formLayout.hashCode();
	}

	public boolean equals(Object obj) {
		return formLayout.equals(obj);
	}

	public String toString() {
		return formLayout.toString();
	}

	public int getColumnCount() {
		return formLayout.getColumnCount();
	}

	public ColumnSpec getColumnSpec(int columnIndex) {
		return formLayout.getColumnSpec(columnIndex);
	}

	public void setColumnSpec(int columnIndex, ColumnSpec columnSpec) {
		formLayout.setColumnSpec(columnIndex, columnSpec);
	}

	public void appendColumn(ColumnSpec columnSpec) {
		formLayout.appendColumn(columnSpec);
	}

	public void insertColumn(int columnIndex, ColumnSpec columnSpec) {
		formLayout.insertColumn(columnIndex, columnSpec);
	}

	public void removeColumn(int columnIndex) {
		formLayout.removeColumn(columnIndex);
	}

	public int getRowCount() {
		return formLayout.getRowCount();
	}

	public RowSpec getRowSpec(int rowIndex) {
		return formLayout.getRowSpec(rowIndex);
	}

	public void setRowSpec(int rowIndex, RowSpec rowSpec) {
		formLayout.setRowSpec(rowIndex, rowSpec);
	}

	public void appendRow(RowSpec rowSpec) {
		formLayout.appendRow(rowSpec);
	}

	public void insertRow(int rowIndex, RowSpec rowSpec) {
		formLayout.insertRow(rowIndex, rowSpec);
	}

	public void removeRow(int rowIndex) {
		formLayout.removeRow(rowIndex);
	}

	public CellConstraints getConstraints(Component component) {
		return formLayout.getConstraints(component);
	}

	public void setConstraints(Component component, CellConstraints constraints) {
		formLayout.setConstraints(component, constraints);
	}

	public int[][] getColumnGroups() {
		return formLayout.getColumnGroups();
	}

	public void setColumnGroups(int[][] colGroupIndices) {
		formLayout.setColumnGroups(colGroupIndices);
	}

	public void addGroupedColumn(int columnIndex) {
		formLayout.addGroupedColumn(columnIndex);
	}

	public int[][] getRowGroups() {
		return formLayout.getRowGroups();
	}

	public void setRowGroups(int[][] rowGroupIndices) {
		formLayout.setRowGroups(rowGroupIndices);
	}

	public void addGroupedRow(int rowIndex) {
		formLayout.addGroupedRow(rowIndex);
	}

	public boolean getHonorsVisibility() {
		return formLayout.getHonorsVisibility();
	}

	public void setHonorsVisibility(boolean b) {
		formLayout.setHonorsVisibility(b);
	}

	public void setHonorsVisibility(Component component, Boolean b) {
		formLayout.setHonorsVisibility(component, b);
	}

	public void addLayoutComponent(String name, Component component) {
		formLayout.addLayoutComponent(name, component);
	}

	public void addLayoutComponent(Component comp, Object constraints) {
		formLayout.addLayoutComponent(comp, constraints);
	}

	public void removeLayoutComponent(Component comp) {
		formLayout.removeLayoutComponent(comp);
	}

	public Dimension minimumLayoutSize(Container parent) {
		return formLayout.minimumLayoutSize(parent);
	}

	public Dimension preferredLayoutSize(Container parent) {
		return formLayout.preferredLayoutSize(parent);
	}

	public Dimension maximumLayoutSize(Container target) {
		return formLayout.maximumLayoutSize(target);
	}

	public float getLayoutAlignmentX(Container parent) {
		return formLayout.getLayoutAlignmentX(parent);
	}

	public float getLayoutAlignmentY(Container parent) {
		return formLayout.getLayoutAlignmentY(parent);
	}

	public void invalidateLayout(Container target) {
		formLayout.invalidateLayout(target);
	}

	public void layoutContainer(Container parent) {
		formLayout.layoutContainer(parent);
	}

	public LayoutInfo getLayoutInfo(Container parent) {
		return formLayout.getLayoutInfo(parent);
	}
	////  END DELEGATES  ////
}