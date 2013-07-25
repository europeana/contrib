package harvesterUI.client.panels.overviewGrid;

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.google.gwt.user.client.rpc.AsyncCallback;
import harvesterUI.client.HarvesterUI;
import harvesterUI.client.servlets.dataManagement.DataManagementServiceAsync;
import harvesterUI.client.util.ServerExceptionDialog;
import harvesterUI.client.util.UtilManager;
import harvesterUI.shared.dataTypes.DataContainer;

import java.util.List;

/**
 * Created to REPOX project.
 * User: Edmundo
 * Date: 28/03/12
 * Time: 13:01
 */
public class SearchComboBox extends ComboBox<ModelData>{

    public SearchComboBox(final MainGrid mainGrid) {
        ListStore<ModelData> searchComboStore = new ListStore<ModelData>();

        setEmptyText(HarvesterUI.CONSTANTS.goTo());
        setDisplayField("name");
        setWidth(200);
        setStore(searchComboStore);
        setTypeAhead(true);
        setTriggerAction(ComboBox.TriggerAction.ALL);
        setMinListWidth(300);
        setTemplate(getSearchTemplate());
//        modifyFiltering(searchCombo);
//        searchComboStore.addFilter(new StoreFilter<FilterAttribute>() {
//            public boolean select(Store<FilterAttribute> store, FilterAttribute parent,
//                                  FilterAttribute item, String property) {
//                for(ModelData model : store.getModels()) {
//                    String v = searchCombo.getRawValue();
//                    String name = ((FilterAttribute)model).getAttributeName();
//                    name = name.toLowerCase();
//                    return name.contains(v.toLowerCase());
//                }
//                return false;
//            }
//        });
        addSelectionChangedListener(new SelectionChangedListener<ModelData>() {
            @Override
            public void selectionChanged(SelectionChangedEvent<ModelData> se) {
                UtilManager.maskCentralPanel(HarvesterUI.CONSTANTS.loadingSearchResults());
                AsyncCallback<DataContainer> callback = new AsyncCallback<DataContainer>() {
                    public void onFailure(Throwable caught) {
                        new ServerExceptionDialog("Failed to get response from server",caught.getMessage()).show();
                    }
                    public void onSuccess(DataContainer result) {
                        mainGrid.getStore().removeAll();
                        mainGrid.getStore().add(result.getChildren(), true);

                        mainGrid.getMainDataGrid().expandAll();
//                        int totalSize = mainGrid.getStore().getModels().size();
//                        mainGrid.getPagingToolBar().setEnabled(false);
                        UtilManager.unmaskCentralPanel();
                    }
                };
                if(se.getSelectedItem() != null)
                    ((DataManagementServiceAsync) Registry.get(HarvesterUI.DATA_MANAGEMENT_SERVICE)).getSearchResult(se.getSelectedItem(), callback);
            }
        });
        setStore(searchComboStore);
    }

    public void populateSearchCombo(){
        AsyncCallback<List<ModelData>> callback = new AsyncCallback<List<ModelData>>() {
            public void onFailure(Throwable caught) {
                new ServerExceptionDialog("Failed to get response from server",caught.getMessage()).show();
            }
            public void onSuccess(List<ModelData> result) {
                store.removeAll();
                store.add(result);
            }
        };
        ((DataManagementServiceAsync) Registry.get(HarvesterUI.DATA_MANAGEMENT_SERVICE)).getSearchComboData(callback);
    }

    private native String getSearchTemplate() /*-{
        return  [
            '<tpl for=".">',
            '<div class="x-combo-list-item">',
//            '<tpl if="{[values.value]} == \'DP\'">',
//            '{[values.attributeName]}',
//            '</tpl>',
            '{[values.prefix]} - {[values.name]}',
            '</div>',
            '</tpl>'
        ].join("");
    }-*/;

}
