package at.ac.ait.dme.gtv.client.selection;

import com.google.gwt.dom.client.Element;

/**
 * Implementation of the CrossBrowserSelection base class for Mozilla et al.
 * 
 * @author Rainer Simon
 */
public class CrossBrowserSelectionMozillaImpl extends CrossBrowserSelection {
	
	/**
	 * The selection text
	 */
	private String selection = null;
	
	/**
	 * The selection offset
	 */
	private int offset = 0;
	
	public CrossBrowserSelectionMozillaImpl(Element context) {
		Selection s = Selection.getSelection();
		if ((s != null) && (!s.isEmpty())) {
			// Range from start of text to start of selection
			Range startOffsetRange = Range.createRange();
			startOffsetRange.setStart(context, 0);
			startOffsetRange.setEnd(s.getAnchorNode(), s.getAnchorOffset());
			int startOffset = startOffsetRange.getText().length();
			
			// Range from start of text to end of selection
			Range endOffsetRange = Range.createRange();
			endOffsetRange.setStart(context, 0);
			endOffsetRange.setEnd(s.getFocusNode(), s.getFocusOffset());
			int endOffset = endOffsetRange.getText().length();
			
			if (startOffset < endOffset) {
				this.offset = startOffset;
			} else {
				this.offset = endOffset;
			}
			
			this.selection = s.getRangeAt(0).getText();
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
