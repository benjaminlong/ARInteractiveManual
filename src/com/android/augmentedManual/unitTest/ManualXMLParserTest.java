package com.android.augmentedManual.unitTest;

//Required import for JUnit.
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

import android.content.res.AssetManager;

import com.android.augmentedManual.utility.ManualXMLParser;

/**
 * This class tests ManualXMLParser for proper behavior.  It covers
 * TODO
 *
 */
//------------------------------------------------------------------------
public class ManualXMLParserTest extends TestCase{
	
	
	/**
	 * Sets up the test environment
	 */
	// ------------------------------------------------------------------------
	public void setUp()
	{
	}

	/**
	 * Tears down the test environment
	 */
	// ------------------------------------------------------------------------
	public void tearDown()
	{
	}
	
	/**
	 * Tests to load a file from the asset.
	 */
	// ------------------------------------------------------------------------
	public void testSetXMLManual()
	{
		ManualXMLParser parser = new ManualXMLParser();
		assertEquals(null, parser.getCurrentFile());
		assertEquals(null, parser.getCurrentDocument());
		
		// Valid Manual XML
		try {
			InputStream xmlDescription = new FileInputStream(
					"C:\\Work\\ARInteractiveManual\\assets\\XML\\ManualXMLDescription.xsd");
			InputStream xml = new FileInputStream(
					"C:\\Work\\ARInteractiveManual\\assets\\data_tests\\ManualTest.xml");
			
//			parser.setXMLDescription(xmlDescription);
			boolean result = parser.setXMLManual(xml);
			assertEquals(true, result);
			assertEquals(false, parser.getCurrentFile().toString().isEmpty());
			assertEquals(false, parser.getCurrentDocument().toString().isEmpty());
			
			// Not valid Manual XML, no xml set ! 
			ManualXMLParser parser2 = new ManualXMLParser();
			InputStream xml2 = new FileInputStream(
	 			"C:\\Work\\ARInteractiveManual\\assets\\TrackingData_ML3D.xml");
			parser2.setXMLDescription(xmlDescription);
			boolean result2 = parser.setXMLManual(xml2);
			assertEquals(false, result2);
			assertEquals(null, parser2.getCurrentFile());
			assertEquals(null, parser2.getCurrentDocument());
			
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}
	
	// ------------------------------------------------------------------------
	public void testGetManualInfo()
	{
		ManualXMLParser parser = new ManualXMLParser();
		
		try {
			InputStream xml = new FileInputStream(
					"C:\\Work\\ARInteractiveManual\\assets\\data_tests\\ManualTest.xml");
			parser.setXMLManual(xml);
			assertNotNull(parser.getCurrentFile());
			
			Map<String, String> info = parser.getManualInfo();
			assertEquals(true, info.containsKey("name"));
			assertEquals("ManualTest.xml", info.get("name"));
			assertEquals(true, info.containsKey("trackingdata"));
			assertEquals("TrackingData_Test_ML3D.xml", info.get("trackingdata"));
	
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

	// ------------------------------------------------------------------------
	public void testGetCurrentGeometry()
	{
		ManualXMLParser parser = new ManualXMLParser();
		
		try {
		InputStream xml = new FileInputStream(
				"C:\\Work\\ARInteractiveManual\\assets\\data_tests\\ManualTest.xml");
		parser.setXMLManual(xml);
		assertNotNull(parser.getCurrentFile());
		
		assertEquals("", parser.getCurrentCosName());
		assertEquals("", parser.getCurrentGeometry());
		assertEquals("", parser.getCurrentStepInfo());
		assertEquals(0, parser.getStepCount());
		
		parser.nextStep();
		assertEquals("object1", parser.getCurrentCosName());
		assertEquals("object1.md2", parser.getCurrentGeometry());
		assertEquals("Detection of the first object", parser.getCurrentStepInfo());
		assertEquals(1, parser.getStepCount());
		
		parser.nextStep();
		assertEquals("object2", parser.getCurrentCosName());
		assertEquals("object1.md2", parser.getCurrentGeometry());
		assertEquals("No information", parser.getCurrentStepInfo());
		assertEquals(2, parser.getStepCount());
		
		parser.nextStep();
		assertEquals("", parser.getCurrentCosName());
		assertEquals("", parser.getCurrentGeometry());
		assertEquals("", parser.getCurrentStepInfo());
		assertEquals(-1, parser.getStepCount());
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}
	
	// ------------------------------------------------------------------------
	public void testGetGeometryList()
	{
		ManualXMLParser parser = new ManualXMLParser();
		try {
			InputStream xml = new FileInputStream(
					"C:\\Work\\ARInteractiveManual\\assets\\data_tests\\ManualTest.xml");
			parser.setXMLManual(xml);
			assertNotNull(parser.getCurrentFile());
			
			List<String> list = parser.getGeometryList();
			assertEquals(1 , list.size());
			assertEquals(true , list.contains("object1.md2"));
			
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}
}
