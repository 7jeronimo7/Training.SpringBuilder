package org.foundations.app;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;

public class GenerateSpringFoundationsZipTest {
	
	@Test
	public void testBuildZip() {
		String result ="";
		GenerateSpringFoundationsZip classUnderTest =  new GenerateSpringFoundationsZip();
		//result = classUnderTest.buildZip("PUBLIC", "EVENT_LOG", "dummy", "org.sxh.bondapps","default", "1"); //no PK
		//result = classUnderTest.buildZip("PUBLIC", "DEALER_INVENTORY", "dummy", "org.sxh.bondapps","default", "1"); // no  pk
		result = classUnderTest.buildZip("PUBLIC", "ALL_INVENTORY", "dummy", "org.sxh.bondapps","default", "1");
		assertNotNull("object is not null", result);
		
	}

}
