/*
 * SortableTable Widget for GWT library of Google, Inc.
 * 
 * Copyright (c) 2006 Parvinder Thapar
 * http://psthapar.googlepages.com/
 * 
 * This library is free software; you can redistribute 
 * it and/or modify it under the terms of the GNU Lesser 
 * General Public License as published by the Free Software 
 * Foundation; either version 2.1 of the License, or 
 * (at your option) any later version. This library is 
 * distributed in the hope that it will be useful, but 
 * WITHOUT ANY WARRANTY; without even the implied warranty of 
 * MERCHANTABILITY  or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNULesser General Public License for more details. 
 * You should have received a copy of the GNU Lesser General 
 * PublicLicense along with this library; if not, write to the 
 * Free Software Foundation, Inc.,  
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA  
 */

package at.researchstudio.dme.imageannotation.client.table;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.FlexTable;

/*
 * SortableTable Widget for GWT library of Google, Inc.
 * 
 * Copyright (c) 2006 Parvinder Thapar
 * http://psthapar.googlepages.com/
 * 
 * This library is free software; you can redistribute 
 * it and/or modify it under the terms of the GNU Lesser 
 * General Public License as published by the Free Software 
 * Foundation; either version 2.1 of the License, or 
 * (at your option) any later version. This library is 
 * distributed in the hope that it will be useful, but 
 * WITHOUT ANY WARRANTY; without even the implied warranty of 
 * MERCHANTABILITY  or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNULesser General Public License for more details. 
 * You should have received a copy of the GNU Lesser General 
 * PublicLicense along with this library; if not, write to the 
 * Free Software Foundation, Inc.,  
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA  
 */
public class SortableTable extends FlexTable implements Sortable, ClickHandler {

	// Holds the current column being sorted
	private int sortColIndex = -1;

	// Holds the current direction of sort: Asc/ Desc
	private int sortDirection = -1;

	// The default image to show acending order arrow
	private String sortAscImage = "images/ascending.gif";

	// The default image to show descending order arrow
	private String sortDescImage = "images/descending.gif";

	// The default image to show the blank image
	// This is needed to paint the columns other than
	// the one which is being sorted.
	// Should be same length and width as the asc/ desc
	// images.
	private String blankImage = "images/blank.gif";

	// Holds the data rows of the table
	// This is a list of RowData Object
	private List<RowData> tableRows = new ArrayList<RowData>();

	// Holds the data for the column headers
	private List<String> tableHeaders = new ArrayList<String>();

	/*
	 * Default Constructor
	 * 
	 * Calls the super class constructor and adds a TableListener object
	 */
	public SortableTable() {
		super();
		this.addClickHandler(this);
	}

	/*
	 * addColumnHeader
	 * 
	 * Adds the Column Header to the table Uses the rowIndex 0 to add the header
	 * names. Renders the name and the asc/desc/blank gif to the column
	 * 
	 * @param columnName (String) @param columnIndex (int)
	 */
	public void addColumnHeader(String name, int index) {
		tableHeaders.add(index, name);
		this.renderTableHeader(name, index);
	}

	/*
	 * setValue
	 * 
	 * Sets the values in specifed row/column Expects a Comparable Object for
	 * sorting
	 * 
	 * @param rowIndex (int) @param columnIndex (int) @param Value (Comparable)
	 */
	public void setValue(int rowIndex, int colIndex, String value) {
		// The rowIndex should begin with 1 as rowIndex 0 is for the Header
		// Any row with index == 0 will not be displayed.
		if (rowIndex == 0) {
			return;
		}

		if ((rowIndex - 1) >= this.tableRows.size()
				|| null == tableRows.get(rowIndex - 1)) {
			tableRows.add(rowIndex - 1, new RowData());
		}

		RowData rowData = (RowData) this.tableRows.get(rowIndex - 1);
		rowData.addColumnValue(colIndex, value);
		this.setHTML(rowIndex, colIndex, "" + value.toString() + "");
	}

	/*
	 * sort
	 * 
	 * Implementation of Sortable Interface, this method decribes how to sort
	 * the specified column. It checks the current sort direction and flips it
	 * 
	 * @param columnIndex (int)
	 */
	public void sort(int columnIndex) {
		Collections.sort(this.tableRows);
		if (this.sortColIndex != columnIndex) {
			// New Column Header clicked
			// Reset the sortDirection to ASC
			this.sortDirection = SORT_ASC;
		} else {
			// Same Column Header clicked
			// Reverse the sortDirection
			this.sortDirection = (this.sortDirection == SORT_ASC) ? SORT_DESC
					: SORT_ASC;
		}
		this.sortColIndex = columnIndex;
	}

	/**
	 * sort and redraw
	 * 
	 * @param columnIndex
	 * @param redraw
	 */
	public void sort(int columnIndex, boolean redraw) {
		this.setSortColIndex(columnIndex);
		sort(columnIndex);
		if (redraw)
			drawTable();
	}

	/**
	 * handle the on click event to start sorting
	 */
	@Override
	public void onClick(ClickEvent event) {
		Cell cell = this.getCellForEvent(event);
		if (cell == null) return;
		
		this.setSortColIndex(cell.getCellIndex());
		this.sort(cell.getCellIndex());
		this.drawTable();
	}

	/*
	 * getSortAscImage
	 * 
	 * Getter for Sort Ascending Image
	 * 
	 * @return String
	 */
	public String getSortAscImage() {
		return sortAscImage;
	}

	/*
	 * setSortAscImage
	 * 
	 * Setter for Sort Ascending Image
	 * 
	 * @param relative path + image name (String) e.g. images/asc.gif
	 */
	public void setSortAscImage(String sortAscImage) {
		this.sortAscImage = sortAscImage;
	}

	/*
	 * getSortDescImage
	 * 
	 * Getter for Sort Descending Image
	 * 
	 * @return String
	 */
	public String getSortDescImage() {
		return sortDescImage;
	}

	/*
	 * setSortDescImgage
	 * 
	 * Setter for Sort Descending Image
	 * 
	 * @param relative path + image name (String) e.g. images/desc.gif
	 */
	public void setSortDescImgage(String sortDescImgage) {
		this.sortDescImage = sortDescImgage;
	}

	/*
	 * getBlankImage
	 * 
	 * Getter for blank Image
	 * 
	 * @return String
	 */
	public String getBlankImage() {
		return blankImage;
	}

	/*
	 * setBlankImage
	 * 
	 * Setter for the blank Image
	 * 
	 * @param relative path + image name (String) e.g. images/blank.gif
	 */
	public void setBlankImage(String blankImage) {
		this.blankImage = blankImage;
	}

	/*
	 * drawTable
	 * 
	 * Renders the header as well as the body of the table
	 */
	protected void drawTable() {
		this.displayTableHeader();
		this.displayTableBody();
	}

	/*
	 * displayTableHeader
	 * 
	 * Renders only the table header
	 */
	private void displayTableHeader() {
		int colIndex = 0;		
		for (String header : this.tableHeaders) {
			this.renderTableHeader(header, colIndex++);
		}
	}

	/*
	 * displayTableBody
	 * 
	 * Renders the body or the remaining rows of the table except the header. It
	 * checks the sort direction and displays the rows accordingly
	 */
	private void displayTableBody() {
		if (this.sortDirection == SORT_ASC || this.sortDirection == -1) {
			// Ascending order and Default Display
			for (int rowIndex = 0; rowIndex < tableRows.size(); rowIndex++) {
				RowData columns = (RowData) tableRows.get(rowIndex);
				for (int colIndex = 0; colIndex < columns.getColumnValues()
						.size(); colIndex++) {
					Object value = columns.getColumnValue(colIndex);
					if (null != value) {
						this.setHTML(rowIndex + 1, colIndex, value.toString());
					}
				}
			}
		} else {
			// Descending Order Display
			for (int rowIndex = tableRows.size() - 1, rowNum = 1; rowIndex >= 0; rowIndex--, rowNum++) {
				RowData columns = (RowData) tableRows.get(rowIndex);
				for (int colIndex = 0; colIndex < columns.getColumnValues()
						.size(); colIndex++) {
					Object value = columns.getColumnValue(colIndex);
					if (null != value) {
						this.setHTML(rowNum, colIndex, value.toString());
					}
				}
			}
		}
	}

	/*
	 * setSortColIndex
	 * 
	 * Sets the current column index being sorted
	 * 
	 * @param column index being sorted (int)
	 */
	private void setSortColIndex(int sortIndex) {
		for (int rowIndex = 0; rowIndex < tableRows.size(); rowIndex++) {
			RowData row = (RowData) tableRows.get(rowIndex);
			row.setSortColIndex(sortIndex);
		}
	}

	/*
	 * renderTableHeader Renders a particular column in the Table Header
	 * 
	 * @param Column Name (String) @param Column Index (int)
	 */
	private void renderTableHeader(String name, int index) {
		StringBuffer headerText = new StringBuffer();
		headerText.append(name);
		headerText.append("&nbsp;<img border='0' src=");
		if (this.sortColIndex == index) {
			if (this.sortDirection == SORT_ASC) {
				headerText.append("'" + this.sortAscImage
						+ "' alt='Ascending' ");
			} else {
				headerText.append("'" + this.sortDescImage
						+ "' alt='Descending' ");
			}
		} else {
			headerText.append("'" + this.blankImage + "'");
		}
		headerText.append("/>");

		this.setHTML(0, index, headerText.toString());
	}
}
