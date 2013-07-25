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
import com.google.gwt.query.client.Function;

import java.util.Stack;

/**
 * Serial execution of effects. When SERIAL, adding an effect to the stack will execute it and remove
 * it and executes the next effect on the stack. When PARALLEL all effects will run simultaneously.
 *
 * @author Serkan Demirel <serkan@blackbuilt.nl>
 */
public class TweenManager {

    private Stack<Tween> stack = new Stack<Tween>();
    private static TweenManager tweenManager = new TweenManager();
    private static boolean busy = false;
    private Strategy executionStrategy = Strategy.PARALLEL;

    public static void add(Tween tween) {
        tweenManager.stack.add(tween);
        if (!busy) {
            tweenManager.execute();
        }
    }

    private void execute() {
        if (0 == stack.size()) {
            return;
        }
        busy = true;
        Tween tween = stack.pop();
        if (executionStrategy == Strategy.SERIAL) {
            tween.addListener(
                    new Function() {

                        @Override
                        public void f(Element e) {
                            busy = false;
                            execute();
                        }
                    }
            );
            tween.start();
        }
        else if (executionStrategy == Strategy.PARALLEL) {
            busy = false;
            tween.start();
        }
    }

    public enum Strategy {
        SERIAL,
        PARALLEL
    }
}
