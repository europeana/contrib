package eu.europeana.solr;

import junit.framework.Assert;

import org.junit.Test;

public class ZipFileUtilsTest {

	/**
	 * Simple unit test to check that the functionalityof ZipFile works ok
	 */
	@Test
	public void test(){
		Assert.assertEquals(600, ZipFileUtils.readDocuments("./src/test/resources/testdata.zip").size());
	}
}
