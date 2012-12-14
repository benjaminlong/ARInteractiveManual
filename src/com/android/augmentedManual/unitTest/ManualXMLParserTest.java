package com.android.augmentedManual.unitTest;

//Required import for JUnit.
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

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
	 			"C:\\Work\\ARInteractiveManual\\assets\\data_tests\\TrackingData_ML3D.xml");
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
			assertEquals(true, info.containsKey("manualname"));
			assertEquals(true, info.containsKey("manualicon"));
			assertEquals(true, info.containsKey("factoryname"));
			assertEquals(true, info.containsKey("factoryicon"));
			assertEquals(true, info.containsKey("description"));
			assertEquals("Manual Test", info.get("manualname"));
			assertEquals(true, info.containsKey("trackingdata"));
			assertEquals("TrackingData_Test_ML3D.xml", info.get("trackingdata"));
	
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

	// ------------------------------------------------------------------------
	public void testNextStep()
	{
		ManualXMLParser parser = new ManualXMLParser();
		
		try {
		InputStream xml = new FileInputStream(
				"C:\\Work\\ARInteractiveManual\\assets\\data_tests\\ManualTest.xml");
		parser.setXMLManual(xml);
		assertNotNull(parser.getCurrentFile());
		
		assertEquals(new ArrayList<String>(), parser.getCurrentCosIDs());
		assertEquals(true, parser.getCurrentGeometries().isEmpty());
		assertEquals("", parser.getCurrentStepInfo());
		assertEquals(0, parser.getStepCount());
		
		
		// STEP 1
		parser.nextStep();
		// current codIds ?
		assertEquals(2, parser.getCurrentCosIDs().size());
		assertEquals(true, parser.getCurrentCosIDs().contains("1"));
		assertEquals(true, parser.getCurrentCosIDs().contains("2"));
		
		// current geometries ?
		assertEquals(2, parser.getCurrentGeometries().size());
		assertEquals(true, parser.getCurrentGeometries().contains("geometry1.fbx"));
		assertEquals(true, parser.getCurrentGeometries().contains("geometry2.fbx"));

		// parameters rotation ?
		Float[] rot = {(float) 90.0, (float) 0.0, (float) 0.0};
		assertEquals(rot[0], parser.getRotation("geometry1.fbx", String.valueOf(1))[0]);
		assertEquals(rot[1], parser.getRotation("geometry1.fbx", String.valueOf(1))[1]);
		assertEquals(rot[2], parser.getRotation("geometry1.fbx", String.valueOf(1))[2]);
		assertEquals(rot[0], parser.getRotation("geometry2.fbx", String.valueOf(1))[0]);
		assertEquals(rot[1], parser.getRotation("geometry2.fbx", String.valueOf(1))[1]);
		assertEquals(rot[2], parser.getRotation("geometry2.fbx", String.valueOf(1))[2]);
		
		// parameters translation ?
		Float[] tr1 = {(float) 1.1, (float) 1.2, (float) 1.3};
		Float[] tr2 = {(float) 2.1, (float) 2.2, (float) 2.3};
		assertEquals(tr1[0], parser.getTranslation("geometry1.fbx", String.valueOf(2))[0]);
		assertEquals(tr1[1], parser.getTranslation("geometry1.fbx", String.valueOf(2))[1]);
		assertEquals(tr1[2], parser.getTranslation("geometry1.fbx", String.valueOf(2))[2]);
		assertEquals(tr2[0], parser.getTranslation("geometry2.fbx", String.valueOf(2))[0]);
		assertEquals(tr2[1], parser.getTranslation("geometry2.fbx", String.valueOf(2))[1]);
		assertEquals(tr2[2], parser.getTranslation("geometry2.fbx", String.valueOf(2))[2]);
		
		// parameters scale ?
		Float[] sc1 = {(float) 10.1, (float) 10.2, (float) 10.3};
		Float[] sc2 = {(float) 20.1, (float) 20.2, (float) 20.3};
		assertEquals(sc1[0], parser.getScale("geometry1.fbx", String.valueOf(1))[0]);
		assertEquals(sc1[1], parser.getScale("geometry1.fbx", String.valueOf(1))[1]);
		assertEquals(sc1[2], parser.getScale("geometry1.fbx", String.valueOf(1))[2]);
		assertEquals(sc2[0], parser.getScale("geometry2.fbx", String.valueOf(1))[0]);
		assertEquals(sc2[1], parser.getScale("geometry2.fbx", String.valueOf(1))[1]);
		assertEquals(sc2[2], parser.getScale("geometry2.fbx", String.valueOf(1))[2]);

		
		// STEP 2
		parser.nextStep();
		// current codIds ?
		assertEquals(2, parser.getCurrentCosIDs().size());
		assertEquals(true, parser.getCurrentCosIDs().contains("1"));
		assertEquals(true, parser.getCurrentCosIDs().contains("2"));
		
		// current geometries ?
		assertEquals(3, parser.getCurrentGeometries().size());
		assertEquals(true, parser.getCurrentGeometries().contains("geometry1.fbx"));
		assertEquals(true, parser.getCurrentGeometries().contains("geometry2.fbx"));
		
		// current geometries cos 1 ?
		assertEquals(1, parser.getCurrentGeometries("1").size());
		assertEquals(true, parser.getCurrentGeometries("1").contains("geometry1.fbx"));
		
		// current geometries cos 2 ?
		assertEquals(2, parser.getCurrentGeometries("2").size());
		assertEquals(true, parser.getCurrentGeometries("2").contains("geometry1.fbx"));
		assertEquals(true, parser.getCurrentGeometries("2").contains("geometry2.fbx"));

		// parameters cos 1 rotation ?
		Float[] rot_cos1_geo1 = {(float) 90.0, (float) 0.0, (float) 0.0};
		assertEquals(rot_cos1_geo1[0], 
					 parser.getRotation("geometry1.fbx", String.valueOf(1))[0]);
		assertEquals(rot_cos1_geo1[1], 
					 parser.getRotation("geometry1.fbx", String.valueOf(1))[1]);
		assertEquals(rot_cos1_geo1[2], 
					 parser.getRotation("geometry1.fbx", String.valueOf(1))[2]);

		// parameters cos 1 rotation ?
		Float[] rot_cos2_geo1 = {(float) 90.0, (float) 90.0, (float) 0.0};
		Float[] rot_cos2_geo2 = {(float) 90.0, (float) 90.0, (float) 90.0};
		assertEquals(rot_cos2_geo1[0], 
					 parser.getRotation("geometry1.fbx", String.valueOf(2))[0]);
		assertEquals(rot_cos2_geo1[1], 
					 parser.getRotation("geometry1.fbx", String.valueOf(2))[1]);
		assertEquals(rot_cos2_geo1[2], 
					 parser.getRotation("geometry1.fbx", String.valueOf(2))[2]);
		assertEquals(rot_cos2_geo2[0], 
				 	 parser.getRotation("geometry2.fbx", String.valueOf(2))[0]);
		assertEquals(rot_cos2_geo2[1], 
					 parser.getRotation("geometry2.fbx", String.valueOf(2))[1]);
		assertEquals(rot_cos2_geo2[2], 
					 parser.getRotation("geometry2.fbx", String.valueOf(2))[2]);
		
		
		// STEP OVER !
		parser.nextStep();
		assertEquals(true, parser.getCurrentStep().isEmpty());
		assertEquals(true, parser.getCurrentStep().isEmpty());
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
			assertEquals(2 , list.size());
			assertEquals(true , list.contains("geometry1.fbx"));
			assertEquals(true , list.contains("geometry2.fbx"));
			
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}
}
