package org.foundations.app.util;

import org.junit.Test;
import static org.junit.Assert.*;

public class StringUtilitiesTest {
	
	@Test
	public void testGetAttributeValue() {
		String testString = "<<columnLoop templateDirectory=\"searchParameters\" separator=\",\">>";
		String attributeStringDir="templateDirectory";
		String expectedValueDir = "searchParameters";
		
		String temp=StringUtilities.getAttributeValue(testString, attributeStringDir);
		System.out.println("value is:["+temp+"]");
		assertEquals("Values did not matche expected; actual:["+temp+"]",temp, expectedValueDir);
	}
	
	@Test
	public void testGetAttributeValue_no_separator() {
		String testString = "<<columnLoop templateDirectory=\"getNsetMethods\">>";
		String attributeStringDir="templateDirectory";
		String expectedValueDir = "getNsetMethods";
		
		String temp=StringUtilities.getAttributeValue(testString, attributeStringDir);
		System.out.println("value is:["+temp+"]");
		assertEquals("Values did not matche expected; actual:["+temp+"]",temp, expectedValueDir);
	}
	
	@Test
	public void testGetAttributeValue_separator() {
		String testString = "<<columnLoop templateDirectory=\"getNsetMethods\">>";
		String attributeStringDir="separator";
		String expectedValueDir = "";
		
		String temp=StringUtilities.getAttributeValue(testString, attributeStringDir);
		System.out.println("value is:["+temp+"]");
		assertEquals("Values did not matche expected; actual:["+temp+"]",temp, expectedValueDir);
	}
	
	@Test
	public void testRemoveTag() {
		String testString = "A really really big string with <<cool>> tags !!<<columnLoop templateDirectory=\"searchParameters\" separator=\",\">> This is <<awesome>>!!!";
		String tag="columnLoop";
		String openDelimit = "<<";
		String closeDelimit = ">>";
		
		String temp=StringUtilities.removeTag(testString, tag, openDelimit, closeDelimit);
		System.out.println("Cleansed String is:["+temp+"]");
		assertTrue("String not cleansed["+temp+"]",testString.length() > temp.length());
	}
	
	@Test
	public void testRemoveTag_startingPosition() {
		String testString = "<<columnLoop whatever>>A really really big string with <<cool>> tags !!<<columnLoop templateDirectory=\"searchParameters\" separator=\",\">> This is <<awesome>>!!!";
		String tag="columnLoop";
		String openDelimit = "<<";
		String closeDelimit = ">>";
		
		String temp=StringUtilities.removeTag(testString, tag, openDelimit, closeDelimit);
		System.out.println("Cleansed String is:["+temp+"]");
		assertTrue("String not cleansed["+temp+"]",testString.length() > temp.length());
	}
	
	@Test
	public void testGetTag() {
		String testString = "A really really big string with <<cool>> tags !!<<columnLoop templateDirectory=\"searchParameters\" separator=\",\">> This is <<awesome>>!!!";
		String tag="columnLoop";
		String openDelimit = "<<";
		String closeDelimit = ">>";
		
		String temp=StringUtilities.getTag(testString, tag, openDelimit, closeDelimit);
		System.out.println("Cleansed String is:["+temp+"]");
		assertTrue("String not cleansed["+temp+"]",testString.length() > temp.length());
	}

}
