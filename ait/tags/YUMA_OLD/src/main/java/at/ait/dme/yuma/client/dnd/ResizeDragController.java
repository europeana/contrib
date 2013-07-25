/**
 * adapted from Fred Sauer's dnd samples
 * @author Christian Sadilek
 *
 */
/*
 * Copyright 2008-2010 Austrian Institute of Technology
 *
 * Licensed under the EUPL, Version 1.1 or - as soon they
 * will be approved by the European Commission - subsequent
 * versions of the EUPL (the "Licence");
 * you may not use this work except in compliance with the
 * Licence.
 * You may obtain a copy of the Licence at:
 *
 * http://ec.europa.eu/idabc/eupl
 *
 * Unless required by applicable law or agreed to in
 * writing, software distributed under the Licence is
 * distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied.
 * See the Licence for the specific language governing
 * permissions and limitations under the Licence.
 */

package at.ait.dme.yuma.client.dnd;

import java.util.HashMap;

import com.allen_sauer.gwt.dnd.client.AbstractDragController;
import com.allen_sauer.gwt.dnd.client.drop.BoundaryDropController;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Widget;

public class ResizeDragController extends AbstractDragController {
	public static class DirectionConstant {
		public final int directionBits;
		public final String directionLetters;

		private DirectionConstant(int directionBits, String directionLetters) {
			this.directionBits = directionBits;
			this.directionLetters = directionLetters;
		}
	}
	
	public static final String CSS_CURSOR = "cursor";
	public static final String CSS_CURSOR_RESIZE = "-resize";
	public static final String CSS_CURSOR_DEFAULT = "default";

	public static final int DIRECTION_EAST = 0x0001;
	public static final int DIRECTION_NORTH = 0x0002;
	public static final int DIRECTION_SOUTH = 0x0004;
	public static final int DIRECTION_WEST = 0x0008;

	public static final DirectionConstant NORTH = new DirectionConstant(DIRECTION_NORTH, "n");
	public static final DirectionConstant SOUTH = new DirectionConstant(DIRECTION_SOUTH, "s");	
	public static final DirectionConstant NORTH_EAST = new DirectionConstant(DIRECTION_NORTH | DIRECTION_EAST, "ne");
	public static final DirectionConstant NORTH_WEST = new DirectionConstant(DIRECTION_NORTH | DIRECTION_WEST, "nw");
	public static final DirectionConstant SOUTH_EAST = new DirectionConstant(DIRECTION_SOUTH | DIRECTION_EAST, "se");
	public static final DirectionConstant SOUTH_WEST = new DirectionConstant(DIRECTION_SOUTH | DIRECTION_WEST, "sw");

	private static final int MIN_WIDGET_SIZE = 20;

	private HashMap<Widget, DirectionConstant> directionMap = new HashMap<Widget, DirectionConstant>();
	private Resizable resizableWidget = null;

	public ResizeDragController(AbsolutePanel boundaryPanel, Resizable widget) {
		super(boundaryPanel);
		resizableWidget=widget;
	}

	public void dragMove() {
		int direction = ((ResizeDragController) context.dragController).getDirection(context.draggable).directionBits;
		if ((direction & DIRECTION_NORTH) != 0) {
			int delta = context.draggable.getAbsoluteTop() - context.desiredDraggableY;
			if (delta != 0) {
				int contentHeight = resizableWidget.getHeight();
				int newHeight = Math.max(contentHeight + delta, MIN_WIDGET_SIZE);
				if (getBehaviorConstrainedToBoundaryPanel()) {
					if(getBoundaryPanel().getOffsetHeight()>0) {
						newHeight = Math.min(getBoundaryPanel().getOffsetHeight(), newHeight);
					}
				}
				if (newHeight != contentHeight) {
					int newTop = contentHeight - newHeight;
										
					if (getBehaviorConstrainedToBoundaryPanel()
							&& ((resizableWidget.getRelativeTop() + newTop - 1) < 0))
						newTop = -resizableWidget.getRelativeTop();

					if (newTop != 0)
						resizableWidget.moveBy(0, newTop);
				}

				if (getBehaviorConstrainedToBoundaryPanel()
						&& resizableWidget.getAbsoluteTop() <= getBoundaryPanel().getAbsoluteTop() + 1)
					return;
				
				resizableWidget.setSize(resizableWidget.getWidth(), newHeight);
			}
		} else if ((direction & DIRECTION_SOUTH) != 0) {
			int delta = context.desiredDraggableY - context.draggable.getAbsoluteTop();
			if (delta != 0) {
				int newHeight = resizableWidget.getHeight() + delta;
				if (getBehaviorConstrainedToBoundaryPanel()) {
					if(getBoundaryPanel().getOffsetHeight()>0) {
						newHeight = Math.min(getBoundaryPanel().getOffsetHeight() - 
								resizableWidget.getRelativeTop(), newHeight);
					}
				}
				resizableWidget.setSize(resizableWidget.getWidth(), Math.max(newHeight, MIN_WIDGET_SIZE));
			}
		}
		if ((direction & DIRECTION_WEST) != 0) {
			int delta = context.draggable.getAbsoluteLeft() - context.desiredDraggableX;
			if (delta != 0) {
				int contentWidth = resizableWidget.getWidth();
				int newWidth = Math.max(contentWidth + delta, MIN_WIDGET_SIZE);

				if (getBehaviorConstrainedToBoundaryPanel()) {
					if(getBoundaryPanel().getOffsetWidth()>0) {						
						newWidth = Math.min(getBoundaryPanel().getOffsetWidth(), newWidth);
					}
				}

				if (newWidth != contentWidth) {
					int newLeft = contentWidth - newWidth;
					if (getBehaviorConstrainedToBoundaryPanel()
							&& ((resizableWidget.getRelativeLeft() + newLeft - 1) < 0))
						newLeft = -resizableWidget.getRelativeLeft();

					if (newLeft != 0)
						resizableWidget.moveBy(newLeft, 0);
				}

				if (getBehaviorConstrainedToBoundaryPanel()
						&& resizableWidget.getAbsoluteLeft() <= getBoundaryPanel().getAbsoluteLeft() + 1)
					return;

				resizableWidget.setSize(newWidth, resizableWidget.getHeight());
			}
		} else if ((direction & DIRECTION_EAST) != 0) {
			int delta = context.desiredDraggableX - context.draggable.getAbsoluteLeft();
			if (delta != 0) {
				int newWidth = Math.max(resizableWidget.getWidth() + delta, MIN_WIDGET_SIZE);
				if (getBehaviorConstrainedToBoundaryPanel()) {
					if(getBoundaryPanel().getOffsetWidth()>0) {						
						newWidth = Math.min(getBoundaryPanel().getOffsetWidth()
								-resizableWidget.getAbsoluteLeft()+5, newWidth);
					}
				}
				resizableWidget.setSize(newWidth, resizableWidget.getHeight());
			}
		}
	}

	public void dragStart() {
		super.dragStart();		
	}

	public void makeDraggable(Widget widget, DirectionConstant direction) {
		super.makeDraggable(widget);
		directionMap.put(widget, direction);
	}

	protected BoundaryDropController newBoundaryDropController(AbsolutePanel boundaryPanel,
			boolean allowDroppingOnBoundaryPanel) {
		if (allowDroppingOnBoundaryPanel) {
			throw new IllegalArgumentException();
		}
		return new BoundaryDropController(boundaryPanel, false);
	}

	private DirectionConstant getDirection(Widget draggable) {
		return (DirectionConstant) directionMap.get(draggable);
	}
}
