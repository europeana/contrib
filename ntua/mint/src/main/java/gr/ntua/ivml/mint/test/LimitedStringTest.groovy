package gr.ntua.ivml.mint.test;

import gr.ntua.ivml.mint.util.LimitedStringBuilder
import gr.ntua.ivml.mint.util.StringUtils
import groovy.util.GroovyTestCase

class LimitedStringTest extends GroovyTestCase {
	public void notestCreate() {
		def ls = new LimitedStringBuilder( 30, " ..." );
		ls.append( "this works\n");
		ls.append( "this works\n");
		ls.append( "this works\n");
		ls.append( "this works\n");
		assert( ls.getContent().length() == 30 )
		assert( ls.getContent().endsWith( "..."))
		// and a short one
		ls = new LimitedStringBuilder( 5, " ..." )
		ls.append( "Rather stupid test")
		assert( ls.getContent().length() == 5 )
		assert( ls.getContent().startsWith( "R " ))
	}
	
	public void testStringUtils() {
		println StringUtils.prettyPrint( [48,47,43,10,49,50,51,52,53,64,54,56,67,78,45,34,23,12,33,34,90,100,110,130,240] as byte[], 100)
	}
}
