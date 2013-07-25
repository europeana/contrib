package pt.utl.ist.repox.web.spring;

import org.springmodules.validation.bean.conf.loader.annotation.handler.NotBlank;

/**
 *
 * @author Georg Petz
 */
public class DataProviderForm {

    private String dataProvider_id;

    private String dataProvider_country;

    @NotBlank(message = "Name missing")
    private String dataProvider_name;

    private String dataProvider_description;

    private String dataProvider_dataSetType;

    @NotBlank(message = "Namecode missing")
    private String dataProvider_nameCode;
    
    private String dataProvider_homePage;

    public String getDataProvider_id() {
        return dataProvider_id;
    }

    public void setDataProvider_id(String dataProvider_id) {
        this.dataProvider_id = dataProvider_id;
    }

    public String getDataProvider_dataSetType() {
        return dataProvider_dataSetType;
    }

    public void setDataProvider_dataSetType(String dataProvider_dataSetType) {
        this.dataProvider_dataSetType = dataProvider_dataSetType;
    }

    public String getDataProvider_description() {
        return dataProvider_description;
    }

    public void setDataProvider_description(String dataProvider_description) {
        this.dataProvider_description = dataProvider_description;
    }

    public String getDataProvider_homePage() {
        return dataProvider_homePage;
    }

    public void setDataProvider_homePage(String dataProvider_homePage) {
        this.dataProvider_homePage = dataProvider_homePage;
    }

    public String getDataProvider_name() {
        return dataProvider_name;
    }

    public void setDataProvider_name(String dataProvider_name) {
        this.dataProvider_name = dataProvider_name;
    }

    public String getDataProvider_nameCode() {
        return dataProvider_nameCode;
    }

    public void setDataProvider_nameCode(String dataProvider_nameCode) {
        this.dataProvider_nameCode = dataProvider_nameCode;
    }

    public String getDataProvider_country() {
        return dataProvider_country;
    }

    public void setDataProvider_country(String dataProvider_country) {
        this.dataProvider_country = dataProvider_country;
    }
}
