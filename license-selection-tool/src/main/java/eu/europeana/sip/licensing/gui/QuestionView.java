package eu.europeana.sip.licensing.gui;

import eu.europeana.definitions.annotations.Europeana;
import eu.europeana.definitions.annotations.EuropeanaField;
import eu.europeana.definitions.annotations.Solr;
import eu.europeana.sip.groovy.FieldMapping;
import eu.europeana.sip.licensing.domain.Answers;
import eu.europeana.sip.licensing.domain.License;
import eu.europeana.sip.licensing.model.Answer;
import eu.europeana.sip.licensing.model.Navigable;
import eu.europeana.sip.licensing.model.Question;
import eu.europeana.sip.licensing.model.QuestionModel;
import eu.europeana.sip.licensing.model.Result;
import eu.europeana.sip.licensing.model.View;
import eu.europeana.sip.licensing.network.LicenseService;
import eu.europeana.sip.licensing.network.LicenseServiceImpl;
import eu.europeana.sip.model.SipModel;
import org.apache.log4j.Logger;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.border.TitledBorder;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Enumeration;

/**
 * The UI for the question selection. The view needs to display the following items
 * for each question:
 * <p/>
 * <ul>
 * <li> Title
 * <li> Description
 * <li> Options (if available)
 * <li> Back button
 * <li> Choose button
 * </ul>
 *
 * @author Serkan Demirel <serkan@blackbuilt.nl>
 */
public class QuestionView extends JPanel {

    private final static Logger LOG = Logger.getLogger(Question.class);

    private static final String DEFAULT_TITLE = "Please wait ...";
    private static final TitledBorder BORDER = BorderFactory.createTitledBorder(DEFAULT_TITLE);
    private static final String BACK = "Back";
    private static final String CHOOSE = "Choose";
    private static final int INSET = 15;

    private final JButton backButton = new JButton(BACK);
    private final JButton chooseButton = new JButton(CHOOSE);
    private final JPanel navigation = new JPanel(new GridBagLayout());
    private final JPanel content = new JPanel(new GridLayout(0, 1));
    private final JLabel description = new JLabel();
    private QuestionModel questionModel;
    private ButtonGroup options;
    private Navigable currentView;
    private SipModel sipModel;

    public QuestionView(QuestionModel questionModel, SipModel sipModel) {
        super(new BorderLayout());
        this.questionModel = questionModel;
        this.sipModel = sipModel;
        buildLayout();
        configureListeners();
        renderFollowUp(questionModel.getFirst());
    }

    private void buildLayout() {
        content.setBorder(BORDER);
        navigation.setPreferredSize(new Dimension(1024, 80));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(INSET, INSET, INSET, INSET);
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 0.5;
        gbc.weighty = 1.0;
        add(content, BorderLayout.CENTER);
        add(navigation, BorderLayout.SOUTH);
        navigation.add(backButton, gbc);
        gbc.gridx = 1;
        navigation.add(chooseButton, gbc);
    }

    private void renderFollowUp(Navigable followUp) {
        if (null == followUp) {
            throw new NullPointerException("Navigable object can't be null");
        }
        currentView = followUp;
        if (followUp instanceof Question) {
            renderQuestion((Question) followUp);
        }
        else if (followUp instanceof Result) {
            renderResult((Result) followUp);
        }
        else if (followUp instanceof View) {
            for (Method method : getClass().getDeclaredMethods()) {
                if (followUp.toString().equals(method.getName())) {
                    try {
                        method.invoke(this);
                    }
                    catch (IllegalAccessException e) {
                        JOptionPane.showMessageDialog(this, e.getMessage(), "Error rendering view", JOptionPane.ERROR_MESSAGE);
                        LOG.error("Error rendering view", e);
                    }
                    catch (InvocationTargetException e) {
                        JOptionPane.showMessageDialog(this, e.getMessage(), "Error rendering view", JOptionPane.ERROR_MESSAGE);
                        LOG.error("Error rendering view", e);
                    }
                }
            }
        }
        else {
            throw new IllegalArgumentException(String.format("Class [%s] can't be rendered.", followUp));
        }
    }

    private void renderQuestion(Question question) {
        content.removeAll();
        BORDER.setTitle(question.getTitle());
        backButton.setEnabled(null != currentView.previous());
        chooseButton.setEnabled(questionModel.hasNext());
        content.add(description);
        description.setText(question.getDescription());
        if (null == question.getAnswers()) {
            throw new NullPointerException("This questions doesn't seem to have any answers assiociated with it.");
        }
        options = new ButtonGroup();
        for (Answer answer : question.getAnswers()) {
            AnswerRadioButton answerRadioButton = new AnswerRadioButton(answer);
            answerRadioButton.setSelected((0 == question.getAnswers().indexOf(answer)));
            options.add(answerRadioButton);
            content.add(answerRadioButton);
        }
        content.repaint();
    }

    private void renderResult(Result result) {
        BORDER.setTitle(result.getTitle());
        content.removeAll();
        content.add(description);
        description.setText(result.getValue());
        chooseButton.setEnabled(false);
        backButton.setEnabled(null != currentView.previous());
        LOG.info(String.format("Dispatch {%s}%n", result.getValue()));
        LOG.info(String.format("Element count %s%n", sipModel.getElementCount()));
        class R { // todo: get rid of this class
            @Europeana
            @Solr
            String[] rights;
        }
        EuropeanaField field = null;
        try {
            field = new EuropeanaField(R.class.getDeclaredField("rights"));
        }
        catch (NoSuchFieldException e) {
            e.printStackTrace();  // todo: handle catch
        }
        FieldMapping fieldMapping = new FieldMapping(field);
        fieldMapping.addCodeLine(String.format("europeana.rights \"%s\";", result.getValue()));
        inject(fieldMapping);
        repaint();
    }

    private void inject(FieldMapping fieldMapping) {
        sipModel.addFieldMapping(fieldMapping);
    }


    /**
     * Invoked by reflection.
     */
    private void renderCreativeCommonsView() {
        // todo: show preloader
        content.removeAll();
        final LicenseService licenseService = new LicenseServiceImpl();
        try {
            CreativeCommonsView creativeCommonsView = new CreativeCommonsView(licenseService.retrieveStandardLicenseSet());
            creativeCommonsView.addListener(
                    new QuestionListener<Answers>() {

                        @Override
                        public void selected(JRadioButton value) {
                            // todo: implement
                        }

                        @Override
                        public void backButtonPressed() {
                            // todo: implement
                        }

                        @Override
                        public void chooseButtonPressed() {
                            // todo: implement
                        }

                        @Override
                        public void chooseButtonPressed(Answers answers) {
                            try {
                                License license = licenseService.requestLicense(answers);
                                System.out.printf("%s:%s%n", license.getLicenseName(), license.getLicenseUri());
                            }
                            catch (IOException e) {
                                e.printStackTrace();  // todo: handle catch
                            }
                        }
                    }
            );
            content.add(creativeCommonsView);
            content.revalidate();
            content.repaint();
        }
        catch (Exception e) {
            LOG.error("Error rendering Creative Commons view", e);
            JOptionPane.showMessageDialog(
                    this, String.format("%s : %s", e.getClass().getSimpleName(), e.getMessage()),
                    "Error rendering Creative Commons view",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Navigate through questions based on button events.
     */
    private void configureListeners() {
        backButton.addActionListener(
                new ActionListener() {

                    @Override
                    public void actionPerformed(ActionEvent e) {
                        if (null != currentView.previous()) {
                            renderFollowUp(currentView.previous());
                        }
                    }
                }
        );
        chooseButton.addActionListener(
                new ActionListener() {

                    @Override
                    public void actionPerformed(ActionEvent e) {
                        Enumeration<AbstractButton> radioButtons = options.getElements();
                        while (radioButtons.hasMoreElements()) {
                            AnswerRadioButton answerRadioButton = (AnswerRadioButton) radioButtons.nextElement();
                            if (answerRadioButton.isSelected()) {
                                renderFollowUp(answerRadioButton.getAnswer().getFollowUp());
                            }
                        }
                    }
                }
        );
    }
}
