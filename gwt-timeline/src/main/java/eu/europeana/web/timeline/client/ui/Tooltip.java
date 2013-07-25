/*
 * Copyright 2007 EDL FOUNDATION
 *
 * Licensed under the EUPL, Version 1.0 or? as soon they
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

package eu.europeana.web.timeline.client.ui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.query.client.Function;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import eu.europeana.web.timeline.client.events.ItemListener;
import eu.europeana.web.timeline.client.events.ItemPreviewEvent;
import eu.europeana.web.timeline.client.ui.effects.TweenManager;
import eu.europeana.web.timeline.client.ui.effects.Property;
import eu.europeana.web.timeline.client.ui.effects.Tween;

/**
 * Displays brief information about the focussed item.
 *
 * @author Serkan Demirel <serkan@blackbuilt.nl>
 */
public class Tooltip extends PopupPanel implements Visual {

    private int x;
    private int y;
    private int width;
    private int height;
    private Label caption = new Label();
    private ItemPreview parent;

    public Tooltip(ItemPreview parent, Image image) {
        super();
        this.x = parent.getAbsoluteLeft();
        this.y = parent.getAbsoluteTop();
        add(image);
        buildLayout();
        applyStyles();
        this.parent = parent;
    }

    public Tooltip(String text, int x, int y, int width, int height) {
        super();
        caption.setText(text);
        add(caption);
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        buildLayout();
        applyStyles();
    }

    public Tooltip(ItemPreview parent, HTML html, int x, int y, int width, int height) {
        super();
        add(html);
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        GWT.log("Creating an item preview right now");
        buildLayout();
        applyStyles();
        this.parent = parent;
        this.parent.addListener(
                new ItemListener<ItemPreview, ItemPreviewEvent>() {

                    @Override
                    public void activated(ItemPreview itemPreview) {
                        GWT.log("Activated! show TT");
                        activate();
                    }

                    @Override
                    public void deactivated(ItemPreview itemPreview) {
                        GWT.log("Deactivated, hide TT");
                        deactivate();
                    }

                    @Override
                    public void stateChanged(ItemPreviewEvent itemPreviewEvent) {

                    }
                }
        );
    }

    public void activate() {
        show();
        TweenManager.add(new Tween(Property.opacity(1.0), this.getElement()));
    }

    public void deactivate() {
        hide();
        TweenManager.add(new Tween(Property.opacity(0.0), this.getElement(), new Function() {        // todo: check

            @Override
            public void f(Element e) {
                hide();
            }
        }));
    }


    public void show() {
        super.show();
    }

    public void hide() {
        super.hide();
    }

    @Override
    public void buildLayout() {
        setPopupPosition(x, y);
    }

    @Override
    public void applyStyles() {
        caption.setStyleName("tooltip-text");
    }

    @Override
    public void setStyleName(String styleName) {
        super.setStyleName(styleName);
    }

    @Override
    public void addStyleName(String style) {
        super.addStyleName(style);
    }

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public int getHeight() {
        return height;
    }

    @Override
    public void configureListeners() {
        // todo: implement
    }
}
