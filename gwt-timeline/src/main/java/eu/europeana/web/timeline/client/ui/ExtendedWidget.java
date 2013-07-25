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

import com.google.gwt.user.client.ui.AbsolutePanel;
import eu.europeana.web.timeline.client.ui.effects.Tween;

/**
 * Widget with Flash-like behaviour.
 * <p/>
 * Supports the folowing properties
 * <ul>
 * <li> alpha channel
 * <li> visibility
 * <li> depth
 * <li> scale
 * </ul>
 *
 * @author Serkan Demirel <serkan@blackbuilt.nl>
 */
public class ExtendedWidget extends AbsolutePanel {

    private float scale;

    public float getAlpha() {
        return Float.parseFloat(getElement().getAttribute("opacity"));
    }

    public void setAlpha(int alpha) {
        getElement().setAttribute("opacity", alpha + "");
    }

    public boolean isVisible() {
        return "visible".equals(getElement().getAttribute("visibility"));
    }

    public void setVisible(boolean visible) {
        if (visible) {
            getElement().setAttribute("visibility", "visible");
        }
        else {
            getElement().setAttribute("visibility", "hidden");
        }
    }

    public int getDepth() {
        if (getElement().getAttribute("z-index").equals("")) {
            return 0;
        }
        else {
            return Integer.parseInt(getElement().getAttribute("z-index"));
        }
    }

    public void setDepth(int depth) {
        getElement().setAttribute("z-index", depth + "");
    }

    public void setBehind(ExtendedWidget extendedWidget) {
        setDepth(extendedWidget.getDepth() + 1);
    }

    public void setInFront(ExtendedWidget extendedWidget) {
        setDepth(extendedWidget.getDepth() - 1);
    }

    public float getScale() {
        return scale;
    }

    public void setScale(float scale) {
        this.scale = scale;
    }

    /**
     * @deprecated should be done by tween with property
     */
    @Deprecated
    public void fadeOut() {
        new Tween(getElement()).fadeOut();
    }

    /**
     * @deprecated should be done by tween with property
     */
    @Deprecated
    public void fadeIn() {
        new Tween(getElement()).fadeIn();
    }
}
