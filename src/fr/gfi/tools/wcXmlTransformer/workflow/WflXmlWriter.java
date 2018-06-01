package fr.gfi.tools.wcXmlTransformer.workflow;

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

import fr.gfi.tools.wcXmlTransformer.xmlObjects.Workflow;

public class WflXmlWriter {
	Document doc;
	
	public WflXmlWriter() throws ParserConfigurationException {
		// Build XML document
		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

		doc = docBuilder.newDocument();
	}
	
	public void buildDocument(Workflow wfl) {
		Element activitiesElt;
		Element activityElt;
		
		Element synchRobotsElt;
		Element synchRobotElt;
		
		Element robotsElt;
		Element robotElt;
		
		Element instructionElt;
		Element participantsElt;
		Element actorElt;
		Element principalElt;
		Element roleElt;
		Element teamElt;
		Element roleSetupElt;
		
		Element courantElt;
		
		// Workflow element
		Element wflElt = doc.createElement("workflow");
		doc.appendChild(wflElt);
		wflElt.setAttribute("name", wfl.name);

		// Workflow variables
		writeVariables(wfl.variableList, wflElt);
		
		// Workflow expressions
		writeExpressions(wfl.expressionList, wflElt);
		
		// Workflow activities 
		activitiesElt = doc.createElement("activities");
		wflElt.appendChild(activitiesElt);
		for (Workflow.Activity wflActivity : wfl.activityList) {
			activityElt = doc.createElement("activity");
			activitiesElt.appendChild(activityElt);
			activityElt.setAttribute("name", wflActivity.name);
			
			// Activity notification
			activityElt.setAttribute("notification", wflActivity.notification);
			
			// Activity instruction
			instructionElt = doc.createElement("instruction");
			activityElt.appendChild(instructionElt);
			instructionElt.appendChild(doc.createCDATASection(wflActivity.instruction));
			
			// Activity variables
			writeVariables(wflActivity.variableList, activityElt);
			
			// Activity expressions
			writeExpressions(wflActivity.expressionList, activityElt);
			
			// Activity participants
			participantsElt = doc.createElement("participants");
			
			if (!wflActivity.actorList.isEmpty()) {
				// Actors
				activityElt.appendChild(participantsElt);
				for(Workflow.Actor actor : wflActivity.actorList) {
					actorElt = doc.createElement("actor");
					participantsElt.appendChild(actorElt);
					actorElt.setAttribute("name", actor.name);
				}
			}
			
			if (!wflActivity.principalList.isEmpty()) {
				// Principals
				activityElt.appendChild(participantsElt);
				for(Workflow.Principal principal : wflActivity.principalList) {
					principalElt = doc.createElement("principal");
					participantsElt.appendChild(principalElt);
					principalElt.setAttribute("name", principal.name);
				}
			}
			
			if (!wflActivity.roleList.isEmpty()) {
				// Roles
				activityElt.appendChild(participantsElt);
				for(Workflow.Role role : wflActivity.roleList) {
					roleElt = doc.createElement("role");
					participantsElt.appendChild(roleElt);
					roleElt.setAttribute("name", role.name);
				}
			}
			
			if (!wflActivity.teamList.isEmpty()) {
				// Teams
				activityElt.appendChild(participantsElt);
				for(Workflow.Team team : wflActivity.teamList) {
					teamElt = doc.createElement("team");
					participantsElt.appendChild(teamElt);
					teamElt.setAttribute("name", team.name);
				}
			}
			
			// Role Setup
			if (!wflActivity.roleSetupList.isEmpty()) {
				for(Workflow.RoleSetup roleSetup : wflActivity.roleSetupList) {
					roleSetupElt = doc.createElement("roleSetup");
					activityElt.appendChild(roleSetupElt);
					roleSetupElt.setAttribute("assignee", roleSetup.assignee);
					roleSetupElt.setAttribute("role", roleSetup.role);

					courantElt = doc.createElement("permissions");
					roleSetupElt.appendChild(courantElt);
					courantElt.setAttribute("view", roleSetup.permission.view);
					courantElt.setAttribute("add", roleSetup.permission.add);
					courantElt.setAttribute("remove", roleSetup.permission.remove);
				}
			}			
			
			// Resource Pool
			if (!wflActivity.resourcePoolList.isEmpty()) {
				for(Workflow.ResourcePool resource : wflActivity.resourcePoolList) {
					courantElt = doc.createElement("resourcePool");
					activityElt.appendChild(courantElt);
					courantElt.setAttribute("role", resource.role);
					courantElt.setAttribute("resourceType", resource.resourceType);
					courantElt.setAttribute("resourceName", resource.resourceName);
				}
			}
		} // End Activity
		
		// Synchronization Robot
		synchRobotsElt = doc.createElement("synchRobots");
		wflElt.appendChild(synchRobotsElt);
		for(Workflow.SynchRobot robot : wfl.synchRobotList) {
			synchRobotElt = doc.createElement("synchRobot");
			synchRobotsElt.appendChild(synchRobotElt);
			synchRobotElt.setAttribute("name", robot.name);
			
			// Write robot attributes
			for(String attrName : robot.attibutes.keySet()) {
				Element attrElt = doc.createElement(attrName);
				synchRobotElt.appendChild(attrElt);
				attrElt.setTextContent(robot.attibutes.get(attrName));
			}
			
			// Activity variables
			writeVariables(robot.variableList, synchRobotsElt);
			
			// Activity expressions
			writeExpressions(robot.expressionList, synchRobotsElt);
		}
		
		// Robot
		robotsElt = doc.createElement("robots");
		wflElt.appendChild(robotsElt);
		for(Workflow.Robot robot : wfl.robotList) {
			robotElt = doc.createElement("synchRobot");
			robotsElt.appendChild(robotElt);
			robotElt.setAttribute("name", robot.name);
			
			// Write robot attributes
			for(String attrName : robot.attibutes.keySet()) {
				Element attrElt = doc.createElement(attrName);
				robotElt.appendChild(attrElt);
				attrElt.setTextContent(robot.attibutes.get(attrName));
			}
			
			// Activity variables
			writeVariables(robot.variableList, robotsElt);
			
			// Activity expressions
			writeExpressions(robot.expressionList, robotsElt);
		}
	}

	/**
	 * Write expression tags
	 * @param expList
	 * @param parentElt
	 */
	private void writeExpressions(List<Workflow.Expression> expList, Element parentElt) {
		Element expressionsElt;
		Element exprElt;
		expressionsElt = doc.createElement("expressions");
		parentElt.appendChild(expressionsElt);
		for (Workflow.Expression wflExpr : expList) {
			exprElt = doc.createElement("expression");
			expressionsElt.appendChild(exprElt);
			exprElt.setAttribute("name", wflExpr.name);
			exprElt.appendChild(doc.createCDATASection(wflExpr.body));
		}
	}

	/**
	 * Write variable tags
	 * @param varList
	 * @param parentElt
	 */
	private void writeVariables(List<Workflow.Variable> varList, Element parentElt) {
		Element variablesElt;
		Element varElt;
		variablesElt = doc.createElement("variables");
		parentElt.appendChild(variablesElt);
		for (Workflow.Variable wflVar : varList) {
			varElt = doc.createElement("variable");
			variablesElt.appendChild(varElt);
			varElt.setAttribute("name", wflVar.name);
			varElt.setAttribute("type", wflVar.type);
			
			// Add variable properties
			Element propertyElt = doc.createElement("properties");
			varElt.appendChild(propertyElt);
			for(String attr : wflVar.attibutes.keySet()) {
				propertyElt.setAttribute(attr, wflVar.attibutes.get(attr));
			}

		}
	}
	
	/**
	 * Write XML document to file
	 * @param filePath
	 * @throws TransformerException
	 */
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
