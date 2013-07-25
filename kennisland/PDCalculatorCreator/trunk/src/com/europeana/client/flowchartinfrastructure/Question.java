package com.europeana.client.flowchartinfrastructure;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * Holds the abstract base class for alle calculator questions
 * @author mzeinstra
 *
 */
public abstract class Question {
    
    List<Answer> answers = null;
    String questionNr = "";
    String information = "";
    String className = "";
    String Parameter = "";
    
   
    /**
     * Gets the current year, for calculation purposes.
     * 
     * @return
     */
    public static String currentYear() {
        final Date today = new Date();
        final String year = DateTimeFormat.getFormat("yyyy").format(today);
        return year;
    }

    public static String escapeString(final String string) {
        final StringBuffer sb = new StringBuffer(string.length());
        // true if last char was blank
        boolean lastWasBlankChar = false;
        final int len = string.length();
        char c = ' ';

        for (int i = 0; i < len; i++) {
            c = string.charAt(i);
            if (c == ' ') {
                // blank gets extra work,
                // this solves the problem you get if you replace all
                // blanks with &nbsp;, if you do that you loss
                // word breaking
                if (lastWasBlankChar) {
                    lastWasBlankChar = false;
                    sb.append("&nbsp;");
                } else {
                    lastWasBlankChar = true;
                    sb.append(' ');
                }
            } else {
                lastWasBlankChar = false;
                //
                // HTML Special Chars
                if (c == '"') {
                    sb.append("&quot;");
                } else if (c == '&') {
                    sb.append("&amp;");
                } else if (c == '<') {
                    sb.append("&lt;");
                } else if (c == '>') {
                    sb.append("&gt;");
                } else if (c == '\n') {
                    // Handle Newline
                    sb.append("&lt;br/&gt;");
                } else {
                    final int ci = 0xffff & c;
                    if (ci < 160) {
                        // nothing special only 7 Bit
                        sb.append(c);
                    } else {
                        // Not 7 Bit use the unicode system
                        sb.append("&#");
                        final String res = Integer.toString(ci);
                        sb.append(res);
                        sb.append(';');
                    }
                }
            }
        }
        return sb.toString();
    }

    /**
     * Performs an evaluation (javascript side)
     * 
     * @param arg
     *            argument that contains code to be executed (Example:
     *            1984<2010-50)
     * @return boolean that is the result of an evaluation
     */
    public static native boolean evaluate(String arg) /*-{
                                                      return eval(arg);
     }-*/;



    public void addNode(final String answerText, final Question q,
            final String info) {


        boolean found = false;
        for (int i = 0; i < answers.size(); i++) {
            if (answers.get(i).getAnswer().equals(answerText)) {
                answers.get(i).setRedirect(q);
                answers.get(i).setInformation(info);
                found = true;
                break;
            }
        }
        if (!found) {
            answers.add(new Answer(answerText, q, info));
        }

    }

    public void clearAnswers() {
        if (answers != null) {
            answers.clear();
        }
    }

    public List<Answer> getAnswers() {
        if (answers != null) {
            return new ArrayList<Answer>(answers);
        }
        return null;
    }

    public String getClassName() {
        return className;
    }

    public Answer getEmptyAnswer() {
        for (int i = 0; i < answers.size(); i++) {
            if (answers.get(i).getRedirect() == null) {
                return answers.get(i);
            }
        }
        return null;
    }

    public String getInformation() {
        return information;
    }

    public Question getNextNode() {

        for (int i = 0; i < answers.size(); i++) {
            if (answers.get(i).getRedirect() == null) {
                return this;
            } else {
                final Question returned = answers.get(i).getRedirect()
                        .getNextNode();
                if (returned != null) {
                    return returned;
                }
            }
        }
        return null;
    }

    public String getParameter() {
        return Parameter;
    }

    public abstract String getQuestion();

    public String getQuestionNr() {
        return questionNr;
    }

    void ReplaceAnswer(final Question q) {
        if (answers == null) {
            return;
        }
        for (final Answer a : answers) {
            if (a.getRedirect().getQuestionNr().equals(q.getQuestionNr())) {
                a.setRedirect(q);
            } else {
                a.getRedirect().ReplaceAnswer(q);
            }
        }
    }

    public void setClassName(final String name) {
        className = name;
    }

    public void setInformation(final String text) {
        information = text;
    }

    public void setParameter(final String p) {
        Parameter = p;
    }

    public abstract VerticalPanel showPanel();

    // Abstract methods that need implementing
    public abstract String toXML();

    protected boolean written() {
        return RootQuestion.getInstance().written(questionNr);
    }
}
