package com.europeana.client.flowchartinfrastructure;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * Extension of Question to reflect results.
 * @author mzeinstra
 *
 */
public class Result extends Question {

    private String result;

    public Result(String questionNumber, String text) {
        questionNr = questionNumber;
        result = text;
    }

    @Override
    public Answer getEmptyAnswer() {
        // No further empty questions
        return null;
    }

    @Override
    public Question getNextNode() {
        // No new nodes
        return null;
    }

    @Override
    public String getQuestion() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public VerticalPanel showEditPanel() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public VerticalPanel showPanel() {
        final VerticalPanel resultPanel = new VerticalPanel();
        final HorizontalPanel panel = new HorizontalPanel();
        Label questionLbl = new Label(result);
        questionLbl.setStyleName("QuestionLbl");
        panel.add(questionLbl);
        if (!information.isEmpty()) {
            panel.add(addInformation());
        }
        resultPanel.add(panel);
        return resultPanel;
    }

    @Override
    public String toXML() {
        if (!written()) {
            // internal semaphore protecting against recursive redundancy.
            final StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("\t<step nr=\"" + escapeString(questionNr)
                    + "\">\n");
            stringBuilder.append("\t\t<type>result</type>\n");
            stringBuilder.append("\t\t<result>\n");
            stringBuilder.append("\t\t\t<text>" + escapeString(result)
                    + "</text>\n");
            stringBuilder.append("\t\t\t<information>"
                    + escapeString(information) + "</information>\n");
            stringBuilder.append("\t\t</result>\n");
            stringBuilder.append("\t</step>\n");
            return stringBuilder.toString();
        } else {
            return "";
        }
    }
    
    /**
     * Adds 'I' icon to panel, with information panel
     * 
     * @param information
     * @return
     */
    protected Image addInformation() {
        final Image openButton = new Image("info.png");
        final Button closeButton = new Button("Close");
        final Button editButton = new Button("Edit");

        // Create the popup dialog box
        final DialogBox dialogBox = new DialogBox();
        dialogBox.hide();
        dialogBox.setText("Additional Information: " + this.getQuestion());
        
        dialogBox.setAnimationEnabled(true);

        final HTML text = new HTML(this.getInformation().replace("\n", "<br />"));

        final VerticalPanel infobox = new VerticalPanel();
        final VerticalPanel infobox_content = new VerticalPanel();
        infobox_content.addStyleName("infobox_content");
        infobox_content.add(text);
        
        final VerticalPanel infobox_footer = new VerticalPanel();
        infobox_footer.add(closeButton);
        infobox_footer.add(editButton);
        infobox_footer.addStyleName("infobox_footer");
        
        infobox.add(infobox_content);
        infobox.add(infobox_footer);
        
        dialogBox.setWidget(infobox);

        // Add a handler to close the DialogBox
        closeButton.addClickHandler(new ClickHandler() {

            public void onClick(final ClickEvent event) {
                dialogBox.hide();
                RootPanel.get("overlay").getElement().getStyle().setProperty("display", "none");

            }
        });

        editButton.addClickHandler(new ClickHandler() {

            public void onClick(final ClickEvent event) {
                dialogBox.hide();
                RootPanel.get("overlay").getElement().getStyle().setProperty("display", "none");
                showEditInformation();
            }

        });
        
        // Add a handler to open the DialogBox
        openButton.addClickHandler(new ClickHandler() {
            public void onClick(final ClickEvent event) {
                dialogBox.show();
                RootPanel.get("overlay").getElement().getStyle().setProperty("display", "block");
            }
        });
        return openButton;
    }

    private void showEditInformation() {
        final Button closeButton = new Button("Close");
        final TextArea informationBox = new TextArea();
        informationBox.setVisibleLines(4);
        
        
        informationBox.setValue(getInformation());
        informationBox.addChangeHandler(new ChangeHandler() {

            public void onChange(ChangeEvent event) {
                setInformation(informationBox.getText());

            }

        });
        
        // Create the popup dialog box
        final DialogBox dialogBox = new DialogBox();
        dialogBox.hide();
        dialogBox.setText("Additional Information: " + this.getQuestion());
        
        dialogBox.setAnimationEnabled(true);

        final VerticalPanel infobox = new VerticalPanel();
        final VerticalPanel infobox_content = new VerticalPanel();
        infobox_content.addStyleName("infobox_content");
        infobox_content.add(informationBox);
        
        final VerticalPanel infobox_footer = new VerticalPanel();
        infobox_footer.add(closeButton);
        infobox_footer.addStyleName("infobox_footer");
        
        infobox.add(infobox_content);
        infobox.add(infobox_footer);
        
        dialogBox.setWidget(infobox);

        // Add a handler to close the DialogBox
        closeButton.addClickHandler(new ClickHandler() {

            public void onClick(final ClickEvent event) {
                dialogBox.hide();
                RootPanel.get("overlay").getElement().getStyle().setProperty("display", "none");

            }
        });
        
    }
}
