package fr.gfi.tools.xmlReader.workflow;

import java.io.File;

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

import fr.gfi.tools.xmlReader.xmlObjects.Workflow;

public class WflXmlWriter {
	Document doc;
	
	public WflXmlWriter() throws ParserConfigurationException {
		// Build XML document
		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

		doc = docBuilder.newDocument();
	}
	
	public void buildDocument(Workflow wfl) {
		Element variablesElt;
		Element varElt;
		
		Element expressionsElt;
		Element exprElt;
		
		Element activitiesElt;
		Element activityElt;
		
		Element actorsElt;
		Element actorElt;
		
		// Workflow element
		Element wflElt = doc.createElement("workflow");
		doc.appendChild(wflElt);
		wflElt.setAttribute("name", wfl.name);

		// Workflow variables
		variablesElt = doc.createElement("variables");
		wflElt.appendChild(variablesElt);
		for (Workflow.Variable wflVar : wfl.variableList) {
			varElt = doc.createElement("variable");
			variablesElt.appendChild(varElt);
			varElt.setAttribute("name", wflVar.name);
			varElt.setAttribute("type", wflVar.type);
		}
		
		// Workflow expressions
		expressionsElt = doc.createElement("expressions");
		wflElt.appendChild(expressionsElt);
		for (Workflow.Expression wflExpr : wfl.expressionList) {
			exprElt = doc.createElement("expression");
			expressionsElt.appendChild(exprElt);
			exprElt.setAttribute("name", wflExpr.name);
			//exprElt.setTextContent(wflExpr.body);
			exprElt.appendChild(doc.createCDATASection(wflExpr.body));
		}
		
		// Workflow activities 
		activitiesElt = doc.createElement("activities");
		wflElt.appendChild(activitiesElt);
		for (Workflow.Activity wflActivity : wfl.activityList) {
			activityElt = doc.createElement("activity");
			activitiesElt.appendChild(activityElt);
			activityElt.setAttribute("name", wflActivity.name);
			
			// Activity variables
			variablesElt = doc.createElement("variables");
			activityElt.appendChild(variablesElt);
			for (Workflow.Variable activityVar : wflActivity.variableList) {
				varElt = doc.createElement("variable");
				variablesElt.appendChild(varElt);
				varElt.setAttribute("name", activityVar.name);
				varElt.setAttribute("type", activityVar.type);

			}
			
			// Activity expressions
			expressionsElt = doc.createElement("expressions");
			activityElt.appendChild(expressionsElt);
			for (Workflow.Expression activityExpr : wflActivity.expressionList) {
				exprElt = doc.createElement("expression");
				expressionsElt.appendChild(exprElt);
				exprElt.setAttribute("name", activityExpr.name);
				//exprElt.setTextContent(activityExpr.body);
				exprElt.appendChild(doc.createCDATASection(activityExpr.body));
			}
			
			// Activity actors
			actorsElt = doc.createElement("actors");
			activityElt.appendChild(actorsElt);
			for(Workflow.Actor actor : wflActivity.actorList) {
				actorElt = doc.createElement("actor");
				actorsElt.appendChild(actorElt);
				actorElt.setAttribute("name", actor.name);
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
