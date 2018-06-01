package fr.gfi.tools.wcXmlTransformer.softType;

import java.io.File;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import fr.gfi.tools.wcXmlTransformer.xmlObjects.SoftType;

public class SoftTypeXmlWriter {
	Document doc;
	
	public SoftTypeXmlWriter() throws ParserConfigurationException {
		// Build XML document
		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

		doc = docBuilder.newDocument();
	}
	
	public void buildDocument(List<SoftType> types) {
		Element attributesElt;
		Element attrElt;
		Element constrElt;
		Element layoutElt;
		Element groupElt;
		Element memberElt;
		Element dataUtilityElt;
		
		/*
		 * Root Element
		 */
		Element typeDefElt = doc.createElement("typeDefinition");
		doc.appendChild(typeDefElt);		
		
		for(SoftType type : types) {
			
			/*
			 *  Type element
			 */
			Element typeElt = doc.createElement("type");
			typeDefElt.appendChild(typeElt);
			typeElt.setAttribute("name", type.name);
			typeElt.setAttribute("displayName", type.displayName);
			typeElt.setAttribute("parentType", type.parentType);
	
			/*
			 * Type attributes
			 */
			attributesElt = doc.createElement("typeAttributes");
			typeElt.appendChild(attributesElt);
			for (SoftType.Attribute attribute : type.attributeList) {
				attrElt = doc.createElement("attribute");
				attributesElt.appendChild(attrElt);
				attrElt.setAttribute("name", attribute.name);
				attrElt.setAttribute("displayName", attribute.displayName);
				attrElt.setAttribute("type", attribute.type);
				if (attribute.iba != null) {
					attrElt.setAttribute("iba", attribute.iba);
				}
				
				// Attribute constraints
				for (SoftType.Constraint constraint : attribute.constraintList) {
					constrElt = doc.createElement("constraint");
					attrElt.appendChild(constrElt);	
					constrElt.setAttribute("class", constraint.type);
					constrElt.setAttribute("rule", constraint.rule);
				}
			}
			
			/*
			 * Layouts
			 */
			for (SoftType.Layout layout : type.layoutList) {
				layoutElt = doc.createElement("layout");
				typeElt.appendChild(layoutElt);
				layoutElt.setAttribute("name", layout.name);
				
				/*
				 * Groups
				 */
				for(SoftType.Group group : layout.groupList) {
					groupElt = doc.createElement("group");
					layoutElt.appendChild(groupElt);
					if (group.name != null) {
						groupElt.setAttribute("name", group.name);
						groupElt.setAttribute("locale_en_us", group.localeUS);
						groupElt.setAttribute("locale_en_gb", group.localeGB);
						groupElt.setAttribute("locale_fr", group.localeFr);
					} else {
						groupElt.setAttribute("name", "**inherited**");					
					}
	
					for (SoftType.GroupMember member : group.memberList) {
						memberElt = doc.createElement("member");
						groupElt.appendChild(memberElt);
						if (member.attribute != null) {
							memberElt.setAttribute("name",member.attribute.name);
							memberElt.setAttribute("displayName",member.attribute.displayName);
	
						} else if (member.ref != null){
							memberElt.setAttribute("name",member.ref);						
						}
						
						// DataUtility
						if (member.dataUtility != null) {
							dataUtilityElt = doc.createElement("dataUtility");
							memberElt.appendChild(dataUtilityElt);
							dataUtilityElt.setTextContent(member.dataUtility);
						}
					}
					
				}
			}
			
		}
	}
	
	public void writeDocument(String filePath) throws TransformerException {
		// write the content into xml file
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		DOMSource source = new DOMSource(doc);
		StreamResult result = new StreamResult(new File(filePath));
		
		// Output to console for testing
		// StreamResult result = new StreamResult(System.out);
		transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		transformer.transform(source, result);

	}


}
