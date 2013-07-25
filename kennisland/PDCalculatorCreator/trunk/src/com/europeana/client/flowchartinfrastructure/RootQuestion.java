package com.europeana.client.flowchartinfrastructure;

import java.util.ArrayList;


import com.google.gwt.user.client.Window;

/**
 * Always points to the rootQuestion of a flowchart, use whenever lost in the tree or for plain reset functions
 * @author mzeinstra
 *
 */
public class RootQuestion {
    private Question Root = null;
    private final ArrayList<String> writtenQuestions = new ArrayList<String>();

    private static RootQuestion instance = null;

    public static RootQuestion getInstance() {
        if (instance == null) {
            instance = new RootQuestion();
        }
        return instance;
    }

    private RootQuestion() {
        // Exists only to defeat instantiation.
    }

    public Question getRoot() {
        return Root;
    }

    public void replaceQuestion(Question q) {
        Root.ReplaceAnswer(q);

    }

    public void setRoot(Question r) {
        Root = r;
    }

    public String toXML() {
        writtenQuestions.clear();
        return Root.toXML();
    }

    public boolean written(String q) {
        if (writtenQuestions.contains(q)) {
            return true;
        } else {
            writtenQuestions.add(q);
            return false;
        }
    }
}
