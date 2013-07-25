package pt.utl.ist.repox.web.spring;

import org.springmodules.validation.bean.conf.loader.annotation.handler.NotBlank;

/**
 *
 * form-backing bean for aggregator
 *
 * @author Georg Petz
 */
public class AggregatorForm {

    private String aggregator_id;

    @NotBlank(message="Name missing")
    private String aggregator_name;
    
    @NotBlank(message="Namecode missing")
    private String aggregator_nameCode;
    
    private String aggregator_homePage;

    public String getAggregator_id() {
        return aggregator_id;
    }

    public void setAggregator_id(String aggregator_id) {
        this.aggregator_id = aggregator_id;
    }

    public String getAggregator_nameCode() {
        return aggregator_nameCode;
    }

    public String getAggregator_homePage() {
        return aggregator_homePage;
    }

    public void setAggregator_homePage(String aggregator_homePage) {
        this.aggregator_homePage = aggregator_homePage;
    }

    public void setAggregator_nameCode(String aggregator_nameCode) {
        this.aggregator_nameCode = aggregator_nameCode;
    }

    public String getAggregator_name() {
        return aggregator_name;
    }

    public void setAggregator_name(String aggregator_name) {
        this.aggregator_name = aggregator_name;
    }
}
