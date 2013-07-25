package at.ac.ait.dme.gtv.client.selection;

import com.google.gwt.dom.client.Element;

/**
 * Implementation of the CrossBrowserSelection base class for Internet Explorer 7+.
 * 
 * @author Rainer Simon
 */
public class CrossBrowserSelectionIEImpl extends CrossBrowserSelection {
	
	/**
	 * The selection text
	 */
	private String selection = null;
	
	/**
	 * The selection offset
	 */
	private int offset = 0;
	
	public CrossBrowserSelectionIEImpl(Element context) {
		TextRange s = TextRange.createTextRangeFromSelection();
		if ((s != null) && (!s.isEmpty())) {
			// TextRange from start of text to start of selection
			TextRange startOffsetRange = TextRange.createTextRange();
			startOffsetRange.moveToElementText(context);	
			startOffsetRange.setEndPoint(TextRange.END_TO_START, s);
			int startOffset = startOffsetRange.getText().length();
			
			// Range from start of text to end of selection
			TextRange endOffsetRange = TextRange.createTextRange();
			endOffsetRange.moveToElementText(context);
			endOffsetRange.setEndPoint(TextRange.END_TO_START, s);
			int endOffset = endOffsetRange.getText().length();
			
			if (startOffset < endOffset) {
				this.offset = startOffset;
			} else {
				this.offset = endOffset;
			}
			
			this.selection = s.getText();
		}
	}

	@Override
	public boolean isEmpty() {
		if (selection == null) return true;
		return (selection.length() == 0);
	}
	
	@Override
	public int getOffset() {
		return offset;
	}

	@Override
	public String getText() {
		return selection;
	}

}
