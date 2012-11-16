package com.android.augmentedManual.utility;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;

import android.util.Log;

import java.io.InputStream;

//-----------------------------------------------------------------------------
public class ManualXMLParser {

	// XML file
	InputStream				CurrentXMLStream = null;
	InputStream				XMLDescriptionStream = null;
	Document 				CurrentDocFile = null;
	DocumentBuilderFactory 	DbFactory = null;
	DocumentBuilder 		DBuilder = null;
	
	int						CurrentStepCount;
	Map<String, String> 	CurrentManualInfo = null;
	Map<String, String>		CurrentStep = null;
	List<String>			GeometriesName = null;
	
	// ------------------------------------------------------------------------
	public ManualXMLParser() {
		
		try {
			this.DbFactory = DocumentBuilderFactory.newInstance();
			this.DBuilder = this.DbFactory.newDocumentBuilder();
			
			// Init variables
			this.CurrentManualInfo = new HashMap<String, String>();
			this.CurrentStep = new HashMap<String, String>();
			this.CurrentStepCount = 0;
		
		} catch (Exception e) {
			e.printStackTrace();
		}
	
	}
	
	// ------------------------------------------------------------------------
	public boolean setXMLManual(InputStream manualStream){

//		Log.v("DEBUG", "setXMLManual Start");
		try {
			// Check if the xml file exists
			if (manualStream.toString().isEmpty()) {
				Log.v("DEBUG", "return false because empty");
				return false;
			}
			
			InputStream input = manualStream;
//			Log.v("DEBUG", "setXMLManual DOcFile " + input.toString());
			Document docFile = this.DBuilder.parse(input);
//			Log.v("DEBUG", "setXMLManual DOcFile parsed");
			docFile.getDocumentElement().normalize();
//			Log.v("DEBUG", "setXMLManual DOcFile normalized");
			
			// Check if the xml if a Manual XML
			if (this.XMLDescriptionStream != null) {
				if (!this.isValidManualXML(docFile)) {
					Log.v("DeBUG", "return false because not valid");
					return false;
				}
			}
			
//			Log.v("DEBUG", "Init manual information");
			// Init manual information
			this.CurrentXMLStream = manualStream;
			this.CurrentDocFile = docFile;
			this.CurrentManualInfo = this.recoverManualInfoFromXML();
			this.GeometriesName = this.recoverGeometryNameFromXML();
			
//			Log.v("DEBUG", "return true");
			return true;
								
		} catch (Exception e) {
			e.printStackTrace();
			Log.v("DEBUG", "return false e : " + e);
		}		
		return false;
	}
	
	// ------------------------------------------------------------------------
	public boolean setXMLDescription(InputStream xmlDescription) {
		
//		Log.v("DEBGU", "setXMLDescription Start");
		if (xmlDescription.toString().isEmpty()) {
			Log.v("DEBUG", "return false");
			return false;
		}
		
//		Log.v("DEBGU", "return true");
		this.XMLDescriptionStream = xmlDescription;
		return true;
	}
	
	// ------------------------------------------------------------------------
	public void nextStep() {
		this.CurrentStepCount ++;
//		Log.v("DEBUG", "Count : " + this.CurrentStepCount);
		this.CurrentStep = this.recoverStepFromXML(this.CurrentStepCount);
		
		if (this.CurrentStepCount > 0 && this.CurrentStep.isEmpty()) {
			this.CurrentStepCount = -1;
		}
	}
	
	// ------------------------------------------------------------------------
	public void previousStep() {
		this.CurrentStepCount --;
		if (this.CurrentStepCount < 0) {
			this.CurrentStepCount = 0;
		}
		this.CurrentStep = this.recoverStepFromXML(this.CurrentStepCount);
	}
	
	// ------------------------------------------------------------------------
	public int getStepCount() {
		return this.CurrentStepCount;
	}
	
	// ------------------------------------------------------------------------
	public InputStream getCurrentFile() {
		return this.CurrentXMLStream;
	}
	
	// ------------------------------------------------------------------------
	public Document getCurrentDocument() {
		return this.CurrentDocFile;
	}
	
	// ------------------------------------------------------------------------
	public Map<String, String> getManualInfo() {
		return this.CurrentManualInfo;
	}
	
	// ------------------------------------------------------------------------
	public Map<String, String> getCurrentStep() {
		return this.CurrentStep;
	}
	
	// ------------------------------------------------------------------------
	public String getCurrentGeometry() { 
		return this.recoverTagValueFromCurrentStep("geometry");
	}
	
	// ------------------------------------------------------------------------
	public String getCurrentCosName() { 
		return this.recoverTagValueFromCurrentStep("cosName");
	}
	
	// ------------------------------------------------------------------------
	public String getCurrentCosID() { 
		return this.recoverTagValueFromCurrentStep("cosID");
	}
	
	// ------------------------------------------------------------------------
	public String getCurrentStepInfo() {
		return this.recoverTagValueFromCurrentStep("info");
	}
	
	// ------------------------------------------------------------------------
	public List<String> getGeometryList() {
		return this.GeometriesName;
	}
	
	// ------------------------------------------------------------------------
	private Map<String, String> recoverManualInfoFromXML() {
		Map<String, String> tempMap = new HashMap<String, String>();
		NodeList nList = this.CurrentDocFile.getElementsByTagName("manualinfo");
		if (nList.getLength() != 1) {
			return tempMap;
		}
		
		NodeList nChildList = nList.item(0).getChildNodes();
		for (int temp = 0; temp < nChildList.getLength(); temp++) {
			   Node nNode = nChildList.item(temp);
			   
			   if (nNode.getNodeType() == Node.ELEMENT_NODE) {
				   tempMap.put(nNode.getNodeName(), 
						   					  nNode.getTextContent());
			   }
		}
		return tempMap;
	}
	
	// ------------------------------------------------------------------------
	private Map<String, String> recoverStepFromXML(int value) {
		Map<String, String> tempMap = new HashMap<String, String>();
		
//		Log.v("DEBUG", "recoverStepFromXML nO = " + this.CurrentStepCount);
		NodeList nList = this.CurrentDocFile.getElementsByTagName("step");		
		if (value < 1 || value > nList.getLength()) {
			return tempMap;
		}
		
		NodeList nChildNode = nList.item(value - 1).getChildNodes();
		for (int temp = 0; temp < nChildNode.getLength(); temp ++) {
			Node nNode = nChildNode.item(temp);
			
			if (nNode.getNodeType() == Node.ELEMENT_NODE) {
				tempMap.put(nNode.getNodeName(), nNode.getTextContent());
			}
		}
		
		return tempMap;
	}
	
	// ------------------------------------------------------------------------
	private String recoverTagValueFromCurrentStep(String tag) {
		String value = "";
		if (this.CurrentStep.containsKey(tag)){
			value = this.CurrentStep.get(tag);
		}
		return value;
	}
	
	// ------------------------------------------------------------------------
	private List<String> recoverGeometryNameFromXML() {
		List<String> geometryList = new ArrayList<String>();
		NodeList nList = this.CurrentDocFile.getElementsByTagName("geometry");		
		
		for (int temp = 0; temp < nList.getLength(); temp ++) {
			if (geometryList.contains(nList.item(temp).getTextContent())) {
				continue;
			}
			geometryList.add(nList.item(temp).getTextContent());
		}
		return geometryList;
	}
	
	// ------------------------------------------------------------------------
	private boolean isValidManualXML(Document document) {
		
		try {
			// create a SchemaFactory capable of understanding WXS schemas
			SchemaFactory factory = SchemaFactory.newInstance(
					XMLConstants.W3C_XML_SCHEMA_NS_URI);

		    // load a XSD schema, represented by a Schema instance
		    Source schemaFile = new StreamSource(this.XMLDescriptionStream);
		    Schema schema = factory.newSchema(schemaFile);
	
		    // create a Validator instance, which can be used to validate an 
		    // instance document
		    Validator validator = schema.newValidator();
			validator.validate(new DOMSource(document));
			
			return true;
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return false;
	}
}
