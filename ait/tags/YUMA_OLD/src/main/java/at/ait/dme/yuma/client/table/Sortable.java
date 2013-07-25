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

package at.ait.dme.yuma.client.table;

/*
 * Sortable Interface defines the signatures and the 
 * constants for the sortable table
 */
public interface Sortable {
	// Constants defining the current direction of the 
	// sort on a column
	public static int SORT_ASC = 0;
	public static int SORT_DESC = 1;
	
	/*
	 * sort
	 * 
	 * Defines what happens when the column is sorted
	 * 
	 * @param columnIndex to be sorted (int)
	 */
	public void sort(int columnIndex);
}
