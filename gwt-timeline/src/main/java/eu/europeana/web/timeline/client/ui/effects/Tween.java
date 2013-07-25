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

package eu.europeana.web.timeline.client.ui.effects;

import com.google.gwt.dom.client.Element;
import com.google.gwt.query.client.Effects;
import com.google.gwt.query.client.Function;
import com.google.gwt.query.client.Properties;
import eu.europeana.web.timeline.client.events.EventModel;

import java.util.ArrayList;
import java.util.List;

import static com.google.gwt.query.client.GQuery.$;

/**
 * Wrapper class for GWT-Query Effect.
 *
 * @author Serkan Demirel <serkan@blackbuilt.nl>
 */
public class Tween implements EventModel<Function> {

    private Properties properties;
    private Element element;
    private Function callBack;
    private List<Function> listeners = new ArrayList<Function>();

    @Override
    public void addListener(Function function) {
        listeners.add(function);
    }

    @Override
    public boolean removeListener(Function function) {
        return listeners.remove(function);
    }

    public Tween(Properties properties, Element element) {
        this.properties = properties;
        this.element = element;
        this.callBack = new Function() {

            @Override
            public void f(Element e) {
                notifyListeners(e);
            }
        };
    }

    /**
     * Constructs a new tween object.
     *
     * @param object   The object to apply the tween on.
     * @param property The property of the object to apply the tween on.
     */
    public Tween(Element object, Properties property) {

    }

    // todo: change names

    public Tween(Properties properties, Element element, Function function) {
        this.properties = properties;
        this.element = element;
        listeners.add(function);
        this.callBack = new Function() {

            @Override
            public void f(Element e) {
                notifyListeners(e);
            }
        };
    }

    public Tween(Element element) {
        this.element = element;
    }

    public void start() {
        $(element).as(Effects.Effects).animate(properties,
                Effects.Speed.FAST,
                Effects.Easing.SWING,
                callBack
        );
    }

    private void notifyListeners(Element e) {
        for (Function function : listeners) {
            function.f(e);
        }
    }

    public void fadeOut() {
        $(element).as(Effects.Effects).fadeOut(Effects.Speed.DEFAULT, callBack);
    }

    public void fadeIn() {
        $(element).as(Effects.Effects).fadeIn(Effects.Speed.DEFAULT, callBack);
    }
}
