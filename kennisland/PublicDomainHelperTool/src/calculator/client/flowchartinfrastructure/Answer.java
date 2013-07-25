package calculator.client.flowchartinfrastructure;

import calculator.client.History;
import calculator.client.Panel;
import calculator.client.datainfrastructure.Codification;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * 
 * @author mzeinstra
 * 
 */
public class Answer {
    /**
     * Adds 'I' icon to panel, with information panel
     * 
     * @param info
     * @return
     */
    protected static Image addInformation(final String info, String answer) {
        final Image openButton = new Image("info_deactivated.png");
        openButton.addMouseOverHandler(new MouseOverHandler() {
            @Override
            public void onMouseOver(MouseOverEvent event) {
                openButton.setUrl("info_activated.png");
            }
        });

        openButton.addMouseOutHandler(new MouseOutHandler() {
            @Override
            public void onMouseOut(MouseOutEvent event) {
                openButton.setUrl("info_deactivated.png");
            }
        });
        final Button closeButton = new Button("Close");

        // Create the popup dialog box
        final DialogBox dialogBox = new DialogBox();
        dialogBox.hide();
        dialogBox.setText("Additional Information:" + answer);
        dialogBox.setAnimationEnabled(true);

        final HTML text = new HTML(info.replace("\n", "<br />"));

        final VerticalPanel infobox = new VerticalPanel();
        final VerticalPanel infobox_content = new VerticalPanel();
        infobox_content.addStyleName("infobox_content");
        infobox_content.add(text);

        final VerticalPanel infobox_footer = new VerticalPanel();
        infobox_footer.add(closeButton);
        infobox_footer.addStyleName("infobox_footer");

        infobox.add(infobox_content);
        infobox.add(infobox_footer);

        dialogBox.setWidget(infobox);

        // Add a handler to close the DialogBox
        closeButton.addClickHandler(new ClickHandler() {

            public void onClick(ClickEvent event) {
                dialogBox.hide();
                RootPanel.get("overlay").getElement().getStyle().setProperty(
                        "display", "none");

            }
        });

        // Add a handler to open the DialogBox
        openButton.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                openButton.setUrl("info_deactivated.png");
                dialogBox.show();
                RootPanel.get("overlay").getElement().getStyle().setProperty(
                        "display", "block");
            }
        });
        return openButton;
    }

    private String answer;
    private Question redirect;

    private String Information;

    public Answer(String text, Question q, String info) {
        answer = text;
        redirect = q;
        Information = info;
    }

    public VerticalPanel editAnswer() {
        final VerticalPanel panel = new VerticalPanel();
        final TextBox box = new TextBox();
        box.setText(getAnswer());
        box.addChangeHandler(new ChangeHandler() {

            public void onChange(ChangeEvent event) {
                setAnswer(box.getText());
            }

        });
        panel.add(new Label("Answer:"));
        panel.add(box);

        final TextArea info = new TextArea();
        info.setText(getInformation());
        info.addChangeHandler(new ChangeHandler() {

            public void onChange(ChangeEvent event) {
                setInformation(info.getText());
            }

        });
        panel.add(new Label("Information:"));
        panel.add(info);

        return panel;
    }

    public String getAnswer() {
        return answer;
    }

    private String getInformation() {
        return Information;
    }

    public Question getRedirect() {
        return redirect;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public void setInformation(String i) {
        Information = i;
    }

    public void setRedirect(Question redirect) {
        this.redirect = redirect;
    }

    public HorizontalPanel showButton(final Question parent) {
        final HorizontalPanel panel = new HorizontalPanel();
        final Button btn = new Button(getAnswer());
        btn.addClickHandler(new ClickHandler() {

            public void onClick(ClickEvent event) {
                // Add current Question to history
                History.getInstance().pushQuestion(parent);

                if (Codification.getInstance().getCurrentClass()
                        .equals("class")) {
                    Codification.getInstance().setCurrentClass(getAnswer());
                }
                // change Panel.
                Panel.changeContent(getRedirect().showPanel());
            }

        });
        panel.add(btn);
        if (!getInformation().isEmpty()) {
            panel.add(addInformation(getInformation(), getAnswer()));
        }

        return panel;
    }

    public String toXML() {
        final StringBuilder result = new StringBuilder();
        result.append("\t\t<answer>\n");
        result.append("\t\t\t<value>" + getAnswer() + "</value>\n");
        result.append("\t\t\t<gotoNr>" + getRedirect().getQuestionNr()
                + "</gotoNr>\n");
        result.append("\t\t\t<information>" + getInformation()
                + "</information>\n");
        result.append("\t\t</answer>\n");

        return result.toString();
    }

}
