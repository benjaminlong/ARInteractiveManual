package com.android.augmentedManual.unitTest;

import junit.framework.Assert;
import junit.framework.TestCase;

import com.android.augmentedManual.utility.Geometry;

/**
 * This class testsGeometry for proper behavior.
 * TODO
 *
 */
public class GeometryTest {
	
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
	 * Tests empty geometry.
	 */
	// ------------------------------------------------------------------------
	public void testGeometry()
	{
		Geometry empty = new Geometry();
		Assert.assertEquals("", empty.getGeometryName());
		Assert.assertEquals(new Float[3], empty.getRotation());
		Assert.assertEquals(new Float[3], empty.getTranslation());
		Assert.assertEquals(new Float[3], empty.getScale());
	}
	
	/**
	 * Tests setGeometryName.
	 */
	// ------------------------------------------------------------------------
	public void testSetGeometryName()
	{
		Geometry geo = new Geometry();
		geo.setGeometryName("TestName");
		Assert.assertEquals("TestName", geo.getGeometryName());
	}
	
	/**
	 * Tests setRotation.
	 */
	// ------------------------------------------------------------------------
	public void testSetRotation()
	{
		Geometry geo = new Geometry();
		Float[] rot = new Float[3];
		rot[0] = (float) 1.00;
		rot[1] = (float) 1.00;
		rot[2] = (float) 1.00;
		geo.setRotation(rot);
		Assert.assertEquals(rot, geo.getRotation());
	}
	
	/**
	 * Tests setTranslation.
	 */
	// ------------------------------------------------------------------------
	public void testSetTranslation()
	{
		Geometry geo = new Geometry();
		Float[] tr = new Float[3];
		tr[0] = (float) 1.00;
		tr[1] = (float) 1.00;
		tr[2] = (float) 1.00;
		geo.setTranslation(tr);
		Assert.assertEquals(tr, geo.getTranslation());
	}
	
	/**
	 * Tests setRotation.
	 */
	// ------------------------------------------------------------------------
	public void testSetScale()
	{
		Geometry geo = new Geometry();
		Float[] sca = new Float[3];
		sca[0] = (float) 1.00;
		sca[1] = (float) 1.00;
		sca[2] = (float) 1.00;
		geo.setScale(sca);
		Assert.assertEquals(sca, geo.getScale());
	}
}
