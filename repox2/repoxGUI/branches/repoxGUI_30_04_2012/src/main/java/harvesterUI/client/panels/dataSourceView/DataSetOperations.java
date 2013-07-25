package harvesterUI.client.panels.dataSourceView;

import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.mvc.Dispatcher;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.toolbar.FillToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.SeparatorToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.user.client.History;
import harvesterUI.client.HarvesterUI;
import harvesterUI.client.core.AppEvents;
import harvesterUI.client.panels.harvesting.dialogs.ExportNowDialog;
import harvesterUI.client.panels.harvesting.dialogs.ScheduleExportDialog;
import harvesterUI.client.panels.harvesting.dialogs.ScheduleTaskDialog;
import harvesterUI.shared.dataTypes.DataSourceUI;

import java.util.ArrayList;
import java.util.List;

/**
 * Created to REPOX.
 * User: Edmundo
 * Date: 24-11-2011
 * Time: 15:53
 */
public class DataSetOperations extends ToolBar{

    private DataSourceUI associatedDataSourceUI;
    private Button ingestNow,edit,move,remove,ingestSample,scheduleIngest,empty, scheduleExport, exportNow;
    private boolean drawWidget;

    public DataSetOperations() {
        checkRole();
        /*
         * Listeners
         */
        final SelectionListener<ButtonEvent> emptyDataSetListener = new SelectionListener<ButtonEvent> () {
            public void componentSelected(ButtonEvent be) {
                Dispatcher.forwardEvent(AppEvents.EmptyDataSet,getFinalDSList());
            }
        };

        final SelectionListener<ButtonEvent> removeDSListener = new SelectionListener<ButtonEvent> () {
            public void componentSelected(ButtonEvent ce) {
                Dispatcher.forwardEvent(AppEvents.RemoveDataSet,getFinalDSList());
            }
        };

        /*
         * Buttons
         */

        empty = new Button();
        empty.setText(HarvesterUI.CONSTANTS.emptyDataSet());
        empty.setIcon(HarvesterUI.ICONS.broom_icon());
        empty.addSelectionListener(new SelectionListener<ButtonEvent>() {
            @Override
            public void componentSelected(ButtonEvent me) {
                HarvesterUI.UTIL_MANAGER.createConfirmMessageBox(HarvesterUI.CONSTANTS.confirm(), HarvesterUI.CONSTANTS.emptyDataSetMessage(), emptyDataSetListener);
            }
        });

        ingestNow = new Button();
        ingestNow.setText(HarvesterUI.CONSTANTS.ingestNow());
        ingestNow.setIcon(HarvesterUI.ICONS.ingest_now_icon());
        ingestNow.addSelectionListener(new SelectionListener<ButtonEvent>() {
            @Override
            public void componentSelected(ButtonEvent me) {
                Dispatcher.forwardEvent(AppEvents.IngestDataSet,getFinalDSList());
            }
        });

        ingestSample = new Button();
        ingestSample.setText(HarvesterUI.CONSTANTS.ingestSample());
        ingestSample.setIcon(HarvesterUI.ICONS.ingest_sample_icon());
        ingestSample.addSelectionListener(new SelectionListener<ButtonEvent>() {
            @Override
            public void componentSelected(ButtonEvent me) {
                Dispatcher.forwardEvent(AppEvents.IngestDataSetSample,getFinalDSList());
            }
        });

        scheduleIngest = new Button();
        scheduleIngest.setText(HarvesterUI.CONSTANTS.scheduleIngest());
        scheduleIngest.setIcon(HarvesterUI.ICONS.calendar());
        scheduleIngest.addSelectionListener(new SelectionListener<ButtonEvent>() {
            @Override
            public void componentSelected(ButtonEvent ce) {
                new ScheduleTaskDialog(associatedDataSourceUI.getDataSourceSet());
            }
        });

        scheduleExport = new Button();
        scheduleExport.setText(HarvesterUI.CONSTANTS.scheduleExport());
        scheduleExport.setIcon(HarvesterUI.ICONS.schedule_export_icon());
        scheduleExport.addSelectionListener(new SelectionListener<ButtonEvent>() {
            @Override
            public void componentSelected(ButtonEvent ce) {
                ScheduleExportDialog scheduleExportDialog = new ScheduleExportDialog(associatedDataSourceUI.getDataSourceSet());
                scheduleExportDialog.setModal(true);
                scheduleExportDialog.resetValues();
                scheduleExportDialog.show();
                scheduleExportDialog.center();
            }
        });

        exportNow = new Button();
        exportNow.setText(HarvesterUI.CONSTANTS.exportNow());
        exportNow.setIcon(HarvesterUI.ICONS.export_now_icon());
        exportNow.addSelectionListener(new SelectionListener<ButtonEvent>() {
            @Override
            public void componentSelected(ButtonEvent ce) {
                ExportNowDialog exportNowDialog = new ExportNowDialog(associatedDataSourceUI);
                exportNowDialog.setModal(true);
                exportNowDialog.show();
                exportNowDialog.center();
            }
        });

        add(ingestNow);
        add(ingestSample);
        add(scheduleIngest);
        add(empty);

        add(new SeparatorToolItem());

        add(exportNow);
        add(scheduleExport);

        // Management part
        if(drawWidget){
            add(new SeparatorToolItem());
            move = new Button();
            move.setText(HarvesterUI.CONSTANTS.moveDataSet());
            move.setIcon(HarvesterUI.ICONS.arrow_move());
            move.addSelectionListener(new SelectionListener<ButtonEvent>() {
                @Override
                public void componentSelected(ButtonEvent me) {
                    Dispatcher.forwardEvent(AppEvents.ViewMoveDataSetDialog, getFinalDSList());
                }
            });

            edit = new Button();
            edit.setText(HarvesterUI.CONSTANTS.editDataSet());
            edit.setIcon(HarvesterUI.ICONS.table());
            edit.addSelectionListener(new SelectionListener<ButtonEvent>() {
                public void componentSelected(ButtonEvent ce) {
                    Dispatcher.forwardEvent(AppEvents.ViewDataSourceForm, associatedDataSourceUI);
                }
            });

            remove = new Button();
            remove.setText(HarvesterUI.CONSTANTS.removeDataSet());
            remove.setIcon(HarvesterUI.ICONS.delete());
            remove.addSelectionListener(new SelectionListener<ButtonEvent>() {
                public void componentSelected(ButtonEvent ce) {
                    HarvesterUI.UTIL_MANAGER.createConfirmMessageBox(HarvesterUI.CONSTANTS.confirm(), HarvesterUI.CONSTANTS.removeDataSetMessage(), removeDSListener);
                }
            });
            add(edit);
            add(move);
            add(remove);
        }

        add(new FillToolItem());

        Button refreshButton = new Button(HarvesterUI.CONSTANTS.refresh(),HarvesterUI.ICONS.refresh_icon(),
                new SelectionListener<ButtonEvent>() {
                    @Override
                    public void componentSelected(ButtonEvent be) {
                        History.fireCurrentHistoryState();
                    }
                }
        );
        add(refreshButton);
    }

    private List<DataSourceUI> getFinalDSList(){
        List<DataSourceUI> finalDSList = new ArrayList<DataSourceUI>();
        finalDSList.add(associatedDataSourceUI);
        return finalDSList;
    }

    public void setAssociatedDataSourceUI(DataSourceUI associatedDataSourceUI) {
        this.associatedDataSourceUI = associatedDataSourceUI;
    }

    public void checkRole(){
        switch (HarvesterUI.UTIL_MANAGER.getLoggedUserRole()){
            case ADMIN : drawWidget = true;
                break;
            case NORMAL: drawWidget = true;
                break;
            default: drawWidget = false;
                break;
        }
    }
}
