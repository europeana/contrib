package at.ac.ait.dme.gtv.client;

import at.ac.ait.dme.gtv.client.listeners.TextMouseListener;
import at.ac.ait.dme.gtv.client.listeners.TextSelectionListener;
import at.ac.ait.dme.gtv.client.model.FeedbackEntry;
import at.ac.ait.dme.gtv.client.model.GeoparsedText;
import at.ac.ait.dme.gtv.client.model.Location;
import at.ac.ait.dme.gtv.client.server.GeoparserService;
import at.ac.ait.dme.gtv.client.server.GeoparserServiceAsync;
import com.google.gwt.cell.client.ActionCell;
import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.http.client.URL;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SplitLayoutPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.SingleSelectionModel;

import java.util.LinkedList;
import java.util.List;

/**
 * Main application
 *
 * @author Rainer Simon
 * @author Manuel Bernhardt
 */
public class Application implements EntryPoint {

    private static final String DEFAULT_DOCUMENT = "http://geoparser.digmap.eu/test-document.txt";

    /**
     * The text editing panel
     */
    private GTVTextPanel textPanel;

    /**
     * The OpenLayers map panel
     */
    private GTVMapPanel mapPanel;

    /**
     * The command pannel
     */
    private VerticalPanel commandPanel;

    /**
     * Table to display feedback
     */
    private final CellTable<FeedbackEntry> feedbackCellTable = new CellTable<FeedbackEntry>();

    /**
     * Popup panel for debug purposes
     */
    private final PopupPanel dummyPopup = new PopupPanel();

    /**
     * List of Locations for the currently displayed text
     */
    private List<Location> locations = new LinkedList<Location>();

    /**
     * List of FeedbackEntry-s produced by the user
     */
    private final List<FeedbackEntry> feedbackEntries = new LinkedList<FeedbackEntry>();

    /**
     * The link to the feedback download
     */
    private Anchor xmlLink;

    /**
     * Position at which the feedback popup should be shown
     */
    private PopupPosition feedbackPopupPosition;


    private static final String DOWNLOAD_SERVLET_PATH = "/at.ac.ait.dme.gtv.Application/downloadFeedback";

    /**
     * This is the entry point method.
     */
    public void onModuleLoad() {

        // Two column base layout
        SplitLayoutPanel page = new SplitLayoutPanel();
        SplitLayoutPanel sidebar = new SplitLayoutPanel();

        buildTextPanel();
        buildCommandPanel(sidebar);
        buildMapPanel(sidebar);

        textPanel.setTextHoverListener(new PositionAwareTextMouseListener());
        
        page.addEast(sidebar, 420);
        page.add(textPanel);
        RootLayoutPanel.get().add(page);

        // Load text from the URL, if any was specified
        String url = Window.Location.getParameter("url");
        if (url != null) {
            loadText(url);
        } else {
            loadText(DEFAULT_DOCUMENT);
        }
    }

    private void buildTextPanel() {
        // Main area: text field
        textPanel = new GTVTextPanel();
        textPanel.setTextSelectionListener(new PositionAwareTextSelectionListener());
    }

    private void buildMapPanel(SplitLayoutPanel sidebar) {
        // Sidebar: map
        mapPanel = new GTVMapPanel();

        mapPanel.setMapCenter(16.367f, 48.207f);
        mapPanel.setMapZoom(10);
        sidebar.add(mapPanel);
    }

    private void buildCommandPanel(SplitLayoutPanel sidebar) {
        commandPanel = new VerticalPanel();
        ScrollPanel sp = new ScrollPanel();
        sp.add(commandPanel);
        sidebar.addNorth(sp, 200);

        // cell table
        final ListDataProvider dataProvider = new ListDataProvider();
        dataProvider.setList(feedbackEntries);
        dataProvider.addDataDisplay(feedbackCellTable);

        feedbackCellTable.setTitle("Feedback");

        final SingleSelectionModel<FeedbackEntry> selectionModel = new SingleSelectionModel<FeedbackEntry>();
        feedbackCellTable.setSelectionModel(selectionModel);

        addColumn(new TextCell(), "Location", new GetValue<String>() {
            public String getValue(FeedbackEntry feedbackEntry) {
                return feedbackEntry.getText();
            }
        });
        addColumn(new TextCell(), "Type", new GetValue<String>() {
            public String getValue(FeedbackEntry feedbackEntry) {
                return feedbackEntry.getType().label();
            }
        });
        addColumn(new ActionCell<FeedbackEntry>(
                "Remove", new ActionCell.Delegate<FeedbackEntry>() {
                    public void execute(FeedbackEntry feedbackEntry) {
                        feedbackEntries.remove(feedbackEntry);
                        updateCellTableData();
                        updateDownloadLink();
                    }
                }), "Action", new GetValue<FeedbackEntry>() {
            public FeedbackEntry getValue(FeedbackEntry contact) {
                return contact;
            }
        });

        commandPanel.add(feedbackCellTable);

        // download link to feedback export as XML
        addDownloadLink();

    }

    /**
     * Just for debug purposes for now.
     *
     * @param text text to show in an alert box
     */
    private void alert(String text) {
        final DialogBox popup = new DialogBox(false);
        popup.setText("DBG");
        VerticalPanel vpanel = new VerticalPanel();
        vpanel.add(new HTML(text));

        Button ok = new Button("OK", new ClickHandler() {
            @Override
            public void onClick(ClickEvent arg0) {
                popup.hide();
            }
        });
        vpanel.add(ok);

        popup.add(vpanel);
        popup.center();
        popup.show();
    }

    private void showPopup(String id, int clientX, int clientY) {
        dummyPopup.setWidget(new HTML("MouseOver: " + id));
        dummyPopup.setPopupPosition(clientX, clientY);
        dummyPopup.show();
    }

    private void hidePopup() {
        dummyPopup.hide();
    }


    private void loadText(String url) {
        // TODO: put an "AJAX wheel" in the panel during loading
        GeoparserServiceAsync service = (GeoparserServiceAsync) GWT.create(GeoparserService.class);
        service.getGeoparsedText(url, new AsyncCallback<GeoparsedText>() {

            @Override
            public void onSuccess(GeoparsedText result) {
                if (result != null) {
                    textPanel.setHTML(result.getHtml());
                    locations = result.getLocations();
                }
            }

            @Override
            public void onFailure(Throwable t) {
                alert("failure: " + t.getClass().getName() + " " + t.getLocalizedMessage());
                t.printStackTrace();
            }
        });
    }

    private final class PositionAwareTextMouseListener extends TextMouseListener {

        /*
        @Override
        public void onMouseOver(String id, int clientX, int clientY) {
            showPopup(id, clientX, clientY);
        }

        @Override
        public void onMouseOut(String id, int clientX, int clientY) {
            hidePopup();
        }
        */

        @Override
        public void onClick(String id, int clientX, int clientY) {
            for (Location l : locations) {
                if (l.getId().equals(id)) {
                    mapPanel.setMapCenter(l.getLongitude().floatValue(), l.getLatitude().floatValue());
                    if (!l.isCountry()) {
                        mapPanel.setMapZoom(10);
                    }
                }
            }
            feedbackPopupPosition = new PopupPosition(clientX, clientY);
        }
    }

    private final class PositionAwareTextSelectionListener extends TextSelectionListener {
        @Override
        public void onSelect(final String selectedText, final int offset) {

            // TODO display the dialog at the position of the selected text
            final DialogBox db = new DialogBox(false);
            db.setTitle("Feedback");
            db.setPopupPosition(feedbackPopupPosition.getX(), feedbackPopupPosition.getY());
            VerticalPanel choices = new VerticalPanel();
            choices.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
            db.add(choices);

            Button invalidLocationButton = new Button("This is not a place", new ClickHandler() {

                @Override
                public void onClick(ClickEvent clickEvent) {
                    feedbackEntries.add(new FeedbackEntry(FeedbackEntry.FeedbackType.NOT_A_PLACE, selectedText, offset));
                    updateCellTableData();
                    updateDownloadLink();
                    db.hide();
                }
            });
            choices.add(invalidLocationButton);

            Button unrecognizedPlaceButton = new Button("This is a place but it was not recognized", new ClickHandler() {

                @Override
                public void onClick(ClickEvent clickEvent) {
                    feedbackEntries.add(new FeedbackEntry(FeedbackEntry.FeedbackType.UNRECOGNIZED_PLACE, selectedText, offset));
                    updateCellTableData();
                    updateDownloadLink();
                    db.hide();
                }
            });
            choices.add(unrecognizedPlaceButton);

            Button cancelButton = new Button("Cancel", new ClickHandler() {
                @Override
                public void onClick(ClickEvent clickEvent) {
                    db.hide();
                }
            });
            choices.add(cancelButton);

            db.show();
        }

    }

    private void updateDownloadLink() {
        commandPanel.remove(xmlLink);
        addDownloadLink();
    }

    private void addDownloadLink() {
        StringBuffer sb = new StringBuffer();
        sb.append(DOWNLOAD_SERVLET_PATH + "?feedback=");
        try {
            for (FeedbackEntry e : feedbackEntries) {
                sb.append(e.getType().name());
                sb.append("@");
                sb.append(e.getText());
                sb.append("@");
                sb.append(e.getOffset());
                sb.append(";");
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
        xmlLink = new Anchor("Download feedback as XML", URL.encode(sb.toString()), "blank");
        commandPanel.add(xmlLink);
    }

    private void updateCellTableData() {
        feedbackCellTable.setRowData(0, feedbackEntries);
        feedbackCellTable.setRowCount(feedbackEntries.size());
    }

    private <C> void addColumn(Cell<C> cell, String headerText, final GetValue<C> getter) {
        Column<FeedbackEntry, C> column = new Column<FeedbackEntry, C>(cell) {
            @Override
            public C getValue(FeedbackEntry object) {
                return getter.getValue(object);
            }
        };
        feedbackCellTable.addColumn(column, headerText);
    }

    private static interface GetValue<C> {
        C getValue(FeedbackEntry contact);
    }

    private static class PopupPosition {
        private final int x; private final int y;

        private PopupPosition(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }
    }
}
