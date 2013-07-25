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

package at.ait.dme.yuma.client.image.shape;

import at.ait.dme.yuma.client.colorpicker.ColorPicker;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * panel used to control a shape (change its color, form and stroke width)
 * 
 * @author Christian Sadilek
 */
public class ShapeControlPanel extends PopupPanel {
	private static final String SHAPE_RADIO_GROUP_NAME = "shape";
	private static final String STROKEWIDTH_RADIO_GROUP_NAME = "stroke-width";
		
	private VerticalPanel contentPanel = new VerticalPanel();
	private FlexTable contentTable = new FlexTable();
	
	private ColorPicker colorPicker = null;	
	private HTML colorPreview = new HTML();
		
	private AbsolutePanel shapeParentPanel = null;
	private ShapePanel shapePanel = null;
	private Shape shape = null;
	
	// the internationalized shape constants
	private ShapeConstants shapeConstants = (ShapeConstants) GWT.create(ShapeConstants.class);	
			
	public ShapeControlPanel(AbsolutePanel shapeParentPanel, ShapePanel shapePanel,
			Shape shape) {
		
		this.shapeParentPanel = shapeParentPanel;
		this.shapePanel = shapePanel;
		this.shape = shape;

		createShapeRadioButtons();
		createStrokeWidthRadioButtons();
		createColorPicker();
			
		contentPanel.add(contentTable);
		contentPanel.add(colorPicker);	
		// w/o this workaround IE crashes when applying an alpha filter
		contentPanel.add(new Label(" "));
		this.add(contentPanel);
		
		this.setStyleName("imageFragment-controlpanel");		
	}
	
	/**
	 * returns the shape panel
	 * @return shape panel
	 */
	public ShapePanel getShapePanel() {
		return shapePanel;
	}
	
	/**
	 * creates the radio buttons to choose the shape
	 */
	private void createShapeRadioButtons() {
		Label shapeLabel = new Label(shapeConstants.shape());
		shapeLabel.setStyleName("imageFragment-controlpanel-label");

		RadioButton rectangle = new RadioButton(SHAPE_RADIO_GROUP_NAME, shapeConstants.rectangle());
		rectangle.setStyleName("imageFragment-controlpanel-radio-button");
		rectangle.setValue(shape instanceof Rectangle);		
		rectangle.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				shape=new Rectangle(shape);
				redrawShape();			
			}
		});		
		
		RadioButton ellipse = new RadioButton(SHAPE_RADIO_GROUP_NAME, shapeConstants.ellipse());
		ellipse.setStyleName("imageFragment-controlpanel-radio-button");
		ellipse.setValue(shape instanceof Ellipse);		
		ellipse.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				shape=new Ellipse(shape);
				redrawShape();
			}
		});

		RadioButton cross = new RadioButton(SHAPE_RADIO_GROUP_NAME, shapeConstants.cross());
		cross.setStyleName("imageFragment-controlpanel-radio-button");
		cross.setValue(shape instanceof Cross);		
		cross.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				shape=new Cross(shape);
				redrawShape();
			}
		});
		
		RadioButton polygon = new RadioButton(SHAPE_RADIO_GROUP_NAME, shapeConstants.polygon());
		polygon.setStyleName("imageFragment-controlpanel-radio-button");
		polygon.setValue(shape instanceof Polygon);
		polygon.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				shape=new Polygon(shape);
				redrawShape();
				shapePanel.startDrawPolygon((Polygon)shape);

			}
		});

		RadioButton freehand = new RadioButton(SHAPE_RADIO_GROUP_NAME, shapeConstants.freehand());
		freehand.setStyleName("imageFragment-controlpanel-radio-button");
		freehand.setValue(shape instanceof Polyline);
		freehand.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				shape=new Polyline(shape);
				redrawShape();
				shapePanel.startDrawPolyline((Polyline)shape);
			}
		});
		
		contentTable.setWidget(0, 0, shapeLabel);
		contentTable.setWidget(0, 1, rectangle);
		contentTable.setWidget(0, 2, ellipse);
		contentTable.setWidget(0, 3, cross);
		contentTable.setWidget(0, 4, polygon);	
		contentTable.setWidget(0, 5, freehand);			
	}
	
	/**
	 * creates the color picker to change the shape's color
	 */
	private void createColorPicker() {
		Label colorLabel = new Label(shapeConstants.color());
		colorLabel.setStyleName("imageFragment-controlpanel-label");

		colorPicker = new ColorPicker();
		try {
			colorPicker.setRGB(shape.getColor().getR(), shape.getColor().getG(), 
					shape.getColor().getB());
		} catch (Exception e) {}
		colorPicker.addChangeHandler(new ChangeHandler() {
			public void onChange(ChangeEvent event) {
				shape.setColor(new Color(colorPicker.getHexColor()));
				redrawShape();
				DOM.setStyleAttribute(colorPreview.getElement(), "backgroundColor", "#" +
						colorPicker.getHexColor());
			}			
		});
	
		colorPreview.setStyleName("imageFragment-controlpanel-color-preview");
		DOM.setStyleAttribute(colorPreview.getElement(), "backgroundColor", "#" + 
				colorPicker.getHexColor());
		
		colorPreview.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				if(contentPanel.remove(colorPicker)==false)
					contentPanel.add(colorPicker);
			}			
		});
				
		contentTable.setWidget(2,0,colorLabel);
		contentTable.setWidget(2,1,colorPreview);		
	}
	
	/**
	 * creates radio buttons to change the stroke width of the shape
	 */
	private void createStrokeWidthRadioButtons() {
		Label strokeLabel = new Label(shapeConstants.strokeWidth());
		strokeLabel.setStyleName("imageFragment-controlpanel-label");

		RadioButton thin = new RadioButton(STROKEWIDTH_RADIO_GROUP_NAME,shapeConstants.thin());
		thin.setStyleName("imageFragment-controlpanel-radio-button");
		thin.setValue(shape.getStrokeWidth()==1);
		thin.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				shape.setStrokeWidth(1);
				redrawShape();			
			}
		});
		
		RadioButton medium = new RadioButton(STROKEWIDTH_RADIO_GROUP_NAME, shapeConstants.medium());
		medium.setStyleName("imageFragment-controlpanel-radio-button");
		medium.setValue(shape.getStrokeWidth()==2);
		medium.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				shape.setStrokeWidth(2);
				redrawShape();
			}
		});

		RadioButton thick = new RadioButton(STROKEWIDTH_RADIO_GROUP_NAME, shapeConstants.thick());
		thick.setStyleName("imageFragment-controlpanel-radio-button");
		thick.setValue(shape.getStrokeWidth()==3);
		thick.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				shape.setStrokeWidth(3);
				redrawShape();
			}
		});
		
		contentTable.setWidget(1,0,strokeLabel);
		contentTable.setWidget(1,1,thin);	
		contentTable.setWidget(1,2,medium);	
		contentTable.setWidget(1,3,thick);	
	}
	
	/**
	 * redraw the shape after changes have been made
	 */
	public void redrawShape() {
		int currentLeft = shapeParentPanel.getWidgetLeft(shapePanel)-1;
		int currentTop = shapeParentPanel.getWidgetTop(shapePanel)-1;
		
		shape.setLeft(currentLeft);
		shape.setTop(currentTop);
		shapePanel.setShape(shape);
	}	
}
