package eu.europeana.sip.licensing.gui;

import eu.europeana.sip.licensing.domain.Answers;
import eu.europeana.sip.licensing.domain.CreativeCommonsModel;
import eu.europeana.sip.licensing.model.Answer;
import eu.europeana.sip.licensing.model.NameValuePair;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

/**
 * Consists of three panels:
 * [COMMERCIAL] [DERIVATIVES] [JURISDICTION]
 *
 * @author Serkan Demirel <serkan@blackbuilt.nl>
 */
public class CreativeCommonsView extends JPanel {

    private static final int INSET = 15;
    private static final String BACK = "Back";
    private static final String CHOOSE = "Choose";
    private static final String COMMERCIAL = "commercial";
    private static final String DERIVATIVES = "derivatives";
    private static final String JURISDICTION = "jurisdiction";
    private final JButton backButton = new JButton(BACK);
    private final JButton chooseButton = new JButton(CHOOSE);
    private final Answers.LicenseStandard licenseStandard = new Answers.LicenseStandard();
    private final List<QuestionListener<Answers>> listeners = new ArrayList<QuestionListener<Answers>>();
    private final JComboBox jComboBox = new JComboBox();
    private final List<NamedButtonGroup> namedButtonGroups = new ArrayList<NamedButtonGroup>();
    private CreativeCommonsModel creativeCommonsModel;

    public CreativeCommonsView(CreativeCommonsModel creativeCommonsModel) {
        super(new BorderLayout());
        this.creativeCommonsModel = creativeCommonsModel;
        add(createSubview(COMMERCIAL), BorderLayout.LINE_START);
        add(createSubview(DERIVATIVES), BorderLayout.CENTER);
        add(createJurisdictionView(), BorderLayout.LINE_END);
        add(createNavigationView(), BorderLayout.PAGE_END);
        configureListeners();
        repaint();
    }

    private JPanel createNavigationView() {
        JPanel navigation = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(INSET, INSET, INSET, INSET);
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 0.1;
        gbc.weighty = 0.1;
        navigation.add(backButton, gbc);
        gbc.gridx = 1;
        navigation.add(chooseButton, gbc);
        return navigation;
    }

    private void configureListeners() {
        backButton.addActionListener(
                new ActionListener() {

                    @Override
                    public void actionPerformed(ActionEvent e) {

                    }
                }
        );
        chooseButton.addActionListener(
                new ActionListener() {

                    @Override
                    public void actionPerformed(ActionEvent e) {
                        licenseStandard.setJurisdiction(((NameValuePair) jComboBox.getSelectedItem()).getValue());
                        Answers answers = new Answers();
                        answers.setLicenseStandard(licenseStandard);
                        for (NamedButtonGroup namedButtonGroup : namedButtonGroups) {
                            Enumeration en = namedButtonGroup.getElements();
                            while (en.hasMoreElements()) {
                                AnswerRadioButton radioButton = (AnswerRadioButton) en.nextElement();
                                if (radioButton.getModel() == namedButtonGroup.getSelection()) {
                                    if (COMMERCIAL.equals(namedButtonGroup.getId())) {
                                        licenseStandard.setCommercial(radioButton.getAnswer().getValue());
                                    }
                                    if (DERIVATIVES.equals(namedButtonGroup.getId())) {
                                        licenseStandard.setDerivatives(radioButton.getAnswer().getValue());
                                    }
                                }
                            }
                        }
                        for (QuestionListener<Answers> questionListener : listeners) {
                            questionListener.chooseButtonPressed(answers);
                        }
                    }
                }
        );
    }

    private JPanel createJurisdictionView() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(INSET, INSET, INSET, INSET);
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 0.1;
        gbc.weighty = 0.1;
        Dimension preferredSize = new Dimension(300, 200);
        panel.setPreferredSize(preferredSize);
        CreativeCommonsModel.Field field = creativeCommonsModel.findFieldById(JURISDICTION);
        panel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), field.getLabel()));
        panel.add(new JLabel(field.getDescription()), gbc);
        for (CreativeCommonsModel.Field.Enum $enum : field.getEnum()) {
            jComboBox.addItem(new NameValuePair($enum.getId(), $enum.getLabel()));
        }
        gbc.gridy = 1;
        panel.add(jComboBox, gbc);
        return panel;
    }

    private JPanel createSubview(String fieldId) {
        JPanel panel = new JPanel(new GridBagLayout());
        Dimension preferredSize = new Dimension(300, 200);
        panel.setPreferredSize(preferredSize);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(INSET, INSET, INSET, INSET);
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 0.1;
        gbc.weighty = 0.1;
        CreativeCommonsModel.Field field = creativeCommonsModel.findFieldById(fieldId);
        panel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), field.getLabel()));
        panel.add(new JLabel(field.getDescription()), gbc);
        gbc.gridy++;
        NamedButtonGroup namedButtonGroup = new NamedButtonGroup(fieldId);
        namedButtonGroups.add(namedButtonGroup);
        for (CreativeCommonsModel.Field.Enum $enum : field.getEnum()) {
            Answer answer = new Answer($enum.getId(), $enum.getLabel());
            AnswerRadioButton radioButtonButton = new AnswerRadioButton(answer, (field.getEnum().indexOf($enum) == 0));
            namedButtonGroup.add(radioButtonButton);
            gbc.gridy++;
            panel.add(new JLabel($enum.getDescription()), gbc);
            gbc.gridy++;
            panel.add(radioButtonButton, gbc);
        }
        return panel;
    }

    private class NamedButtonGroup extends ButtonGroup {

        private String id;

        public NamedButtonGroup(String id) {
            this.id = id;
        }

        public String getId() {
            return id;
        }
    }

    public void addListener(QuestionListener<Answers> listener) {
        listeners.add(listener);
    }

    public boolean removeListener(QuestionListener listener) {
        return listeners.remove(listener);
    }

}
