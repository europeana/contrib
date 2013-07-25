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
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import eu.europeana.web.timeline.client.constants.Dimensions;

/**
 * Error message.
 *
 * @author Serkan Demirel <serkan@blackbuilt.nl>
 */
public class ErrorMessage extends PopupPanel implements Visual {

    private final Dimensions dimensions = (Dimensions) GWT.create(Dimensions.class);
    private final VerticalPanel verticalPanel = new VerticalPanel();
    private final Button closeButton = new Button(dimensions.buttonClose());
    private final HTML content = new HTML();

    {
        buildLayout();
        applyStyles();
        configureListeners();
    }

    public ErrorMessage(String htmlText) {
        content.setHTML(htmlText);
    }

    @Override
    public void buildLayout() {
        verticalPanel.add(content);
        verticalPanel.add(closeButton);
        add(verticalPanel);
        center();
    }

    @Override
    public void applyStyles() {
        addStyleName("ui-widget");
        addStyleName("ui-error");
        closeButton.setStyleName("button");
    }

    @Override
    public int getWidth() {
        return 0;  // todo: implement
    }

    @Override
    public int getHeight() {
        return 0;  // todo: implement
    }

    @Override
    public void configureListeners() {
        closeButton.addClickHandler(
                new ClickHandler() {

                    @Override
                    public void onClick(ClickEvent clickEvent) {
                        hide();
                    }
                }
        );
    }
}
    