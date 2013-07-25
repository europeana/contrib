package pt.utl.ist.repox.web.action.mapMetadata;

import org.junit.Assert;
import org.junit.Test;

public class TagGroupTest {
	private String[] stringRepresentation = new String[]{
			"group(''::/lixo::/lixo::'asd')",
			"group('asdasd'::/lixo::/lixo::'asd')",
			"group('asdasd'::/lixo::/lixo::''::/lixo2::'asd')",
			"group('initialPrefix'::/oneTag::/oneTag::'prefix'::/anotherTag/subNode::'')",
			"group('initialPrefix'::/oneTag::/oneTag::'prefix'::/anotherTag/subNode::''::/yetAnotherTag::'')",
			"group('initialPrefix'::::/oneTag::'prefix'::/anotherTag/subNode::''::/yetAnotherTag::'')",
			"group(''::::/record/datafield[@tag='215']/subfield[@code='d']::''::/record/datafield[@tag='712']/subfield[@code='3']::'')",
			"group('('::/record/datafield[@tag='712']::/record/datafield[@tag='712']/subfield[@code='4']::','::/record/datafield[@tag='712']/subfield[@code='a']::')')"
	};
	
	@Test
	public void testTagGroupParsing() {
		for (String currentStringRepresentation : stringRepresentation) {
			TagGroup parsedGroup = new TagGroup(currentStringRepresentation);
			String reparsedStringRepresentation = parsedGroup.getXpath();
			Assert.assertEquals(currentStringRepresentation, reparsedStringRepresentation);
		}
	}
}
