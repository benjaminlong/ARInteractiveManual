package com.android.augmentedManual.unitTest;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import com.android.augmentedManual.unitTest.ManualXMLParserTest;

/**
 * This file aggregates all of the Unit Tests for this component.
 */
public class UnitTests extends TestCase{

	/**
	 * Creates a test suite containing all unit tests
	 * for this component.
	 *
	 * @return A test suite containing all unit tests.
	 */
	public static Test suite() 
	{
		TestSuite suite = new TestSuite();
		
		// Add tests here.
		// suite.addTestSuite(TestClass.class);
		suite.addTestSuite(ManualXMLParserTest.class);
		
		return suite;
	}
}
