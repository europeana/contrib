package com.allen_sauer.gwt.dnd.client.drop;

import com.allen_sauer.gwt.dnd.client.DragContext;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * allows to drop a draggable widget anywhere on the specified AbsolutePanel,
 * no matter if parts of the widget lie outside of the panel. 
 * 
 * @author Christian Sadilek
 */
public class TolerantAbsolutePositionDropController extends AbsolutePositionDropController {
	final AbsolutePanel dropTarget;
	
	public TolerantAbsolutePositionDropController(AbsolutePanel dropTarget) {
		super(dropTarget);
		this.dropTarget = dropTarget;
	}

	public void drop(Widget widget, int left, int top) {
		dropTarget.add(widget, left, top);
	}
	
	@Override
	public void onMove(DragContext context) {
		for (Draggable draggable : draggableList) {
			draggable.desiredX = Math.max(
					Math.min(0, context.desiredDraggableX - 
							dropTargetOffsetX + draggable.relativeX),
					dropTarget.getOffsetWidth()-1-draggable.offsetWidth-1);
			draggable.desiredY = Math.max( 				
				Math.min(0, context.desiredDraggableY - 
					dropTargetOffsetY + draggable.relativeY),
					dropTarget.getOffsetHeight()-1-draggable.offsetHeight-1);

			dropTarget.add(draggable.positioner, draggable.desiredX, draggable.desiredY);
		}
	}
}
