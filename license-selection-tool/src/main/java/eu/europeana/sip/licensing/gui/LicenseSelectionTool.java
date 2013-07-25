package eu.europeana.sip.licensing.gui;

import eu.europeana.sip.api.ExtensionEntrypoint;
import eu.europeana.sip.api.ExtensionInformation;
import eu.europeana.sip.api.ExtensionSource;
import eu.europeana.sip.licensing.io.QuestionModelLoader;
import eu.europeana.sip.licensing.model.CustomClassLoader;
import eu.europeana.sip.licensing.model.QuestionModel;
import eu.europeana.sip.model.FileSet;
import eu.europeana.sip.model.SipModel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * The entrypoint of the extension.
 *
 * @author Serkan Demirel <serkan@blackbuilt.nl>
 * @author Maarten Zeinstra <mz@kl.nl>
 */
public class LicenseSelectionTool extends JFrame implements ExtensionEntrypoint {

    private static final int PANEL_WIDTH = 600;
    private static final int PANEL_HEIGHT = 400;
    private JMenu rootMenu;
    private ExtensionSource extensionSource;
    private ExtensionInformation extensionInformation;
    private Frame owner;
    private String TITLE = "Licensing";
    private SipModel sipModel;
    private FileSet fileSet;

    /**
     * Creates the JFrame, sets the interaction and start the questions
     */
    public LicenseSelectionTool() {
        super();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    private void integrate() {
        JMenuItem licenseMenuItem = new JMenuItem(TITLE);
        licenseMenuItem.addActionListener(
                new ActionListener() {

                    @Override
                    public void actionPerformed(ActionEvent e) {
                        showDialog();
                    }
                }
        );
        if (null == rootMenu) {
            throw new NullPointerException("rootMenu can't be null");
        }
        rootMenu.add(licenseMenuItem);
    }

    private void showDialog() {
        JDialog dialog = new JDialog(owner, TITLE);
        try {
            QuestionModel model = QuestionModelLoader.load("questionnaire.xml", CustomClassLoader.getInstance());
            dialog.add(new QuestionView(model, sipModel));
        }
        catch (Exception e) {
            JOptionPane.showMessageDialog(null, String.format("Error loading %s : %s", "questionnaire.xml", e.getClass().getSimpleName()), e.getMessage(), JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
        dialog.pack();
        dialog.setSize(new Dimension(PANEL_WIDTH, PANEL_HEIGHT));
        dialog.setLocationRelativeTo(owner);
        dialog.setVisible(true);
    }

    @Override
    public void setExtensionInformation(ExtensionInformation extensionInformation) {
        this.extensionInformation = extensionInformation;
    }

    @Override
    public ExtensionInformation getExtensionInformation() {
        return extensionInformation;
    }

    @Override
    public void setOwner(Frame frame) {
        this.owner = frame;
    }

    @Override
    public void setSipModel(SipModel sipModel) {
        this.sipModel = sipModel;
    }

    public void setFileSet(FileSet fileSet) {
        this.fileSet = fileSet;
    }

    @Override
    public void setMenu(JMenu jMenu) {
        rootMenu = jMenu;
        integrate();
    }

    @Override
    public void setClassLoader(ClassLoader classLoader) {
        new CustomClassLoader(classLoader);
    }

    @Override
    public void setExtensionSource(ExtensionSource extensionSource) {
        this.extensionSource = extensionSource;
    }

    @Override
    public String toString() {
        return "LicenseSelectionTool{" +
                "rootMenu=" + rootMenu +
                ", extensionSource=" + extensionSource +
                '}';
    }
}

