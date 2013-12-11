package eu.europeana.ranking.bm25f.enums;

import org.apache.commons.lang.StringUtils;
import org.apache.solr.common.util.NamedList;

@Deprecated
public class SolrFields {

	private final static String BOOSTS = "boosts";
	private final static String BPARAMS = "bParams";
	private static SolrFields fields;
	private static String defaultField;
	private static String[] fieldNames;
	private static Float[] boosts;
	private static Float[] bParams;

	private SolrFields() {

	}

	public static SolrFields getInstance() {

		if (fields == null) {
			fields = new SolrFields();
		}
		return fields;

	}

	private static Float[] createFloatArray(String bParamsString) {
		String[] arr = StringUtils.substringsBetween(
				bParamsString.replace('}', ','), "=", ",");
		Float[] arrFloat = new Float[arr.length];
		int i = 0;
		for (String str : arr) {
			arrFloat[i] = Float.parseFloat(str);
			i++;
		}
		return arrFloat;
	}

	private static String[] createFields(String bParamsString) {

		return StringUtils.substringsBetween(bParamsString.replace('{', ','),
				",", "=");
	}

	public String getDefaultField() {
		return defaultField;
	}

	public String[] getFieldNames() {
		return fieldNames;
	}

	public Float[] getBoosts() {
		return boosts;
	}

	public Float[] getBParams() {
		return bParams;
	}

	public void createSimilarityFields(NamedList pars) {
		String bParamsString = (String) pars.get(BPARAMS);
		fieldNames = createFields(bParamsString);
		bParams = createFloatArray(bParamsString);
		boosts = createFloatArray((String) pars.get(BOOSTS));
	}

	public void setDefaultField(String defaultField) {
		SolrFields.defaultField = defaultField;
	}

}
