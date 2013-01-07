package com.android.augmentedManual.utility;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

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
	InputStream							CurrentXMLStream = null;
	InputStream							XMLDescriptionStream = null;
	Document 							CurrentDocFile = null;
	DocumentBuilderFactory 				DbFactory = null;
	DocumentBuilder 					DBuilder = null;
	
	Map<String, String> 				CurrentManualInfo = null;

	int 								StepCount;
	int									CurrentStepCount;
	
	String								CurrentTitle;
	Map<String, List<Geometry>>			CurrentStep = null;
	List<String>						CurrentTasks = null;
	List<String>						CurrentNeeds = null;
	List<String>						CurrentImages = null;

	List<String>						GeometriesName = null;
	
	
	// --------------------------------------------------------------v----------
	public ManualXMLParser() {
		
		try {
			// Init variables
			this.DbFactory = DocumentBuilderFactory.newInstance();
			this.DBuilder = this.DbFactory.newDocumentBuilder();
			
			this.CurrentTitle = "";
			this.CurrentManualInfo = new HashMap<String, String>();
			this.CurrentStep = new HashMap<String, List<Geometry>>();
			this.CurrentTasks = new ArrayList<String>();
			this.CurrentNeeds = new ArrayList<String>();
			this.CurrentImages = new ArrayList<String>();
			
			this.StepCount = 0;
			this.CurrentStepCount = 0;
		
		} catch (Exception e) {
			e.printStackTrace();
		}
	
	}
	
	// ------------------------------------------------------------------------
	public boolean setXMLManual(InputStream manualStream){

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
			
			// Check if the xml is follow the ManualXML format ?!
			// TODO, the function return false all the time ...
			if (this.XMLDescriptionStream != null) {
				if (!this.isValidManualXML(docFile)) {
					Log.v("DeBUG", "return false because not valid");
					return false;
				}
			}
			
			// Init manual informatio
			this.CurrentXMLStream = manualStream;
			this.CurrentDocFile = docFile;
			this.CurrentManualInfo = this.recoverManualInfoFromXML();
			this.GeometriesName = this.recoverGeometryNameFromXML();
			this.StepCount = 
					this.CurrentDocFile.getElementsByTagName("step").getLength();
			
			return true;
								
		} catch (Exception e) {
			e.printStackTrace();
			Log.v("DEBUG", "return false e : " + e);
		}		
		return false;
	}
	
	// ------------------------------------------------------------------------
	public boolean setXMLDescription(InputStream xmlDescription) {
		
		if (xmlDescription.toString().isEmpty()) {
			Log.v("DEBUG", "return false");
			return false;
		}
		
		this.XMLDescriptionStream = xmlDescription;
		return true;
	}
	
	// ------------------------------------------------------------------------
	public void nextStep() {
		this.CurrentStepCount ++;
		if (this.CurrentStepCount > this.StepCount) {
			this.CurrentStepCount = -1;
		}		
		// recoverStepFrom XML function will set recover all the information
		// from the XML and set all the information into the appropriate variables
		this.recoverCurrentStepFromXML(this.CurrentStepCount);

	}
	
	// ------------------------------------------------------------------------
	public void previousStep() {
		this.CurrentStepCount --;
		if (this.CurrentStepCount < 0) {
			this.CurrentStepCount = 0;
		}
		
		// recoverStepFrom XML function will set recover all the information
		// from the XML and set all the information into the appropriate variables
		this.recoverCurrentStepFromXML(this.CurrentStepCount);
	}
	
	// ------------------------------------------------------------------------
	public int getStepCount() {
		return this.StepCount;
	}
	
	public int getCurrentStepCount() {
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
	public Map<String, List<Geometry>> getCurrentStep() {
		return this.CurrentStep;
	}
	
	// ------------------------------------------------------------------------
	public List<String> getCurrentCosIDs() { 
		List<String> list = new ArrayList<String>();
		if (this.CurrentStep.isEmpty()) {
			return list;
		}
		
		for(Entry<String, List<Geometry>> entry : this.CurrentStep.entrySet()) {
		    String key = entry.getKey();
		    list.addAll(Arrays.asList(key.split(";")));
		}
		
		return list;
	}
	
	// ------------------------------------------------------------------------
	public List<String> getCurrentGeometries() { 
		List<String> geometries = new ArrayList<String>();
		
		for(Entry<String, List<Geometry>> entry : this.CurrentStep.entrySet()) {
		    String key = entry.getKey();
		    geometries.addAll(this.getCurrentGeometries(key));
		}

		return geometries;
	}
	
	// ------------------------------------------------------------------------
	public List<String> getCurrentGeometries(String cosId) {
		List<String> geometries = new ArrayList<String>();
		if (this.CurrentStep.isEmpty()) {
			return geometries;
		}
		
		for(Entry<String, List<Geometry>> entry : this.CurrentStep.entrySet()) {
		    String key = entry.getKey();
		    if (key.contains(cosId)) {
		    	List<Geometry> values = entry.getValue();
		    	for (int i = 0; i < values.size(); i++) {
		    		geometries.add(values.get(i).getGeometryName());
		    	}
		    }
		}
		return geometries;
	}
	
	// ------------------------------------------------------------------------
	public Float[] getRotation(String geometryName, String cosId) {
		Float[] rotation = new Float[3];
		Geometry geo = this.getGeometryFromCurrentStep(cosId, geometryName);
		if (geo != null) {
			rotation = geo.getRotation();
		}
		return rotation;
	}
	
	// ------------------------------------------------------------------------
	public Float[] getTranslation(String geometryName, String cosId) {
		Float[] translation = new Float[3];
		Geometry geo = this.getGeometryFromCurrentStep(cosId, geometryName);
		if (geo != null) {
			translation = geo.getTranslation();
		}
		return translation;
	}
	
	// ------------------------------------------------------------------------
	public Float[] getScale(String geometryName, String cosId) {
		Float[] scale = new Float[3];
		Geometry geo = this.getGeometryFromCurrentStep(cosId, geometryName);
		if (geo != null) {
			scale = geo.getScale();
		}
		return scale;
	}
	
	// ------------------------------------------------------------------------
	public String getCurrentTitle() {
		return this.CurrentTitle;
	}
	
	// ------------------------------------------------------------------------
	public List<String> getCurrentTasksDescription() {
		return this.CurrentTasks;
	}
	
	// ------------------------------------------------------------------------
	public List<String> getCurrentNeedsDescription() {
		return this.CurrentNeeds;
	}
	
	// ------------------------------------------------------------------------
	public List<String> getCurrentImagesName() {
		return this.CurrentImages;
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
	private void recoverCurrentStepFromXML(int value) {
		
		// Remove information about the previous step
		this.CurrentTitle = "";
		this.CurrentStep.clear();
		this.CurrentTasks.clear();
		this.CurrentNeeds.clear();
		this.CurrentImages.clear();
		
		// If we are at the biginning
		if (value == 0) {
			// TODO
			return;
		}
		
		// If Manual is over
		if (value == -1) {
			/// TODO
			return;
		}
		
		NodeList nList = this.CurrentDocFile.getElementsByTagName("step");
		Node stepNode = nList.item(value - 1);
		this.CurrentTitle = this.recoverAttributeValue(stepNode, "title");
		this.recoverStepFromCurrentStepNode(stepNode);
	}
	
	// ---
	// In this function, we will set the variable CurrentStep with all the info
	// from the XML associated to the step
	//
	// We recover each track, with geometries associates and parameters
	// ---
	// ------------------------------------------------------------------------
	private void recoverStepFromCurrentStepNode(Node stepNode) {
		NodeList childStepNode = stepNode.getChildNodes();
		Node trackNode;
		Node node;
		for (int i = 0; i < childStepNode.getLength(); i++) {
			// Recover Tracks
			if (childStepNode.item(i).getNodeName().equals("track") &&
					childStepNode.item(i).getNodeType() == Node.ELEMENT_NODE) {
				trackNode = childStepNode.item(i);
				this.CurrentStep.put(this.recoverAttributeValue(trackNode, "cosIds"),
									 this.recoverGeometries(trackNode));
			}
			// Recover Tasks
			else if (childStepNode.item(i).getNodeName().equals("tasks") &&
					 childStepNode.item(i).getNodeType() == Node.ELEMENT_NODE) {
				node = childStepNode.item(i);
				this.CurrentTasks.addAll(this.recoverValuesTypeFromTypeNode(node, "task"));
			}
			// Recover Needs
			else if (childStepNode.item(i).getNodeName().equals("needs") &&
					 childStepNode.item(i).getNodeType() == Node.ELEMENT_NODE) {
				node = childStepNode.item(i);
				this.CurrentNeeds.addAll(this.recoverValuesTypeFromTypeNode(node, "need"));
			}
			// Recover Images
			else if (childStepNode.item(i).getNodeName().equals("images") &&
					 	childStepNode.item(i).getNodeType() == Node.ELEMENT_NODE) {
				node = childStepNode.item(i);
				this.CurrentImages.addAll(this.recoverValuesTypeFromTypeNode(node, "image"));
			}
		}
	}
	
	// ---
	// In this function, we recover all the different values under a "value" !
	//
	// ---
	// ------------------------------------------------------------------------
	private List<String> recoverValuesTypeFromTypeNode(Node node, String value) {
		List<String> list = new ArrayList<String>();
		NodeList childNode = node.getChildNodes();
		for (int i = 0; i < childNode.getLength(); i++) {
			if (childNode.item(i).getNodeName().equals(value) &&
					childNode.item(i).getNodeType() == Node.ELEMENT_NODE) {
				list.add(childNode.item(i).getTextContent());
			}
		}
		return list;
	}
	
	// ---
	// In this function, we recover the geometries information !
	//
	// Recover geometries and parameters
	// ---
	// ------------------------------------------------------------------------
	private List<Geometry> recoverGeometries(Node trackNode) {
		List<Geometry> temp = new ArrayList<Geometry>();
		NodeList childTrackNode = trackNode.getChildNodes();
		Node geometryNode;
		for (int i = 0; i < childTrackNode.getLength(); i++) {
			if (childTrackNode.item(i).getNodeName().equals("geometry") &&
					childTrackNode.item(i).getNodeType() == Node.ELEMENT_NODE) {
				geometryNode = childTrackNode.item(i);
				temp.add(this.recoverGeometry(geometryNode));
			}
		}
		return temp;
	}
	
	// ---
	// In this function, we recover the vector !
	//
	// Recover vector
	// ---
	// ------------------------------------------------------------------------
	private Geometry recoverGeometry(Node geometryNode) {
		Geometry geometry = new Geometry();
		geometry.setGeometryName(this.recoverAttributeValue(geometryNode, "name"));
		
		NodeList childGeometryNode = geometryNode.getChildNodes();
		Node child;
		for (int i = 0; i < childGeometryNode.getLength(); i++) {
			child = childGeometryNode.item(i);
			if (child.getNodeName().equals("translation")) {
				geometry.setTranslation(this.recoverVector(child));
			}
			else if (child.getNodeName().equals("rotation")) {
				geometry.setRotation(this.recoverVector(child));
			}
			else if (child.getNodeName().equals("scale")) {
				geometry.setScale(this.recoverVector(child));
			}
		}
		return geometry;
	}
	
	// ---
	// In this function, we recover the vector !
	//
	// Recover vector
	// ---
	// ------------------------------------------------------------------------
	private Float[] recoverVector(Node node) {
		Float[] vector = new Float[3];
		NodeList childNode = node.getChildNodes();
		for (int i = 0; i < 3; i++) {
			vector[i] = Float.parseFloat(childNode.item(i).getTextContent());
		}
		return vector;
	}
	
	// ------------------------------------------------------------------------
	private String recoverAttributeValue(Node node, String attribute) {
		String value = "";
		Node attributeNode = node.getAttributes().getNamedItem(attribute);
		value = attributeNode.getTextContent();
		return value;
	}
	
	// ------------------------------------------------------------------------
	private List<String> recoverGeometryNameFromXML() {
		List<String> geometryList = new ArrayList<String>();
		NodeList nList = this.CurrentDocFile.getElementsByTagName("geometry");		
		
		for (int temp = 0; temp < nList.getLength(); temp ++) {
			String geometryName = 
					this.recoverAttributeValue(nList.item(temp), "name");
			if (geometryList.contains(geometryName)) {
				continue;
			}
			geometryList.add(geometryName);
		}
		return geometryList;
	}
	
	// ------------------------------------------------------------------------
	private Geometry getGeometryFromCurrentStep(String cosId, String name) {
		for(Entry<String, List<Geometry>> entry : this.CurrentStep.entrySet()) {
		    String key = entry.getKey();
		    if (key.contains(cosId)) {
		    	List<Geometry> values = entry.getValue();
		    	for (int i = 0; i < values.size(); i++) {
		    		if (values.get(i).getGeometryName().equals(name)) {
		    			return values.get(i);
		    		}
		    	}
		    }
		}
		
		return null;
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
