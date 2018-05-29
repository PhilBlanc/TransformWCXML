package fr.gfi.tools.xmlReader.workflow;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.ext.DefaultHandler2;

import fr.gfi.tools.xmlReader.xmlObjects.Workflow;

public class WflHandler extends DefaultHandler2 {
	private static String TAG_SEPARATOR = "/";
		
	Workflow wfl;
	Workflow.Variable var;
	Workflow.Activity activity;
	Workflow.Expression expression;
	Workflow.Actor actor;
	Workflow.Principal principal;
	Workflow.Role role;
	Workflow.RoleSetup roleSetup;
	Workflow.ResourcePool resourcePool;
	Workflow.Team team;
	Workflow.SynchRobot synchRobot;
	Workflow.Robot robot;
	
	//Enumeration of tags to read
	enum tagNames {csvWfProcessTemplateBegin, 
		csvWfVariable, csvname, csvtype, csvrequired, csvvisible, csvmutable, csvresetable,
		csvWfAssignedActivityTemplateBegin, csvWfAssignedActivityTemplateEnd, csvWfExpression, csvexpression, csvexprBody, csvsendNotification, csvinstructions,
		csvWfPrincipalAssignee, csvprincipal,csvWfRoleAssignee, csvrole, csvWfActorAssignee, csvactor, csvWfTeamAssignee, csvteamAssignee, 
		csvWfRoleSetup, csvassigneeName, csvviewPermission, csvaddPermission, csvremovePermission,
		csvWfResourcePool, csvresourcePoolType, csvresourcePoolName,
		csvWfInternalMethodTemplateBegin, csvWfInternalMethodTemplateEnd, 
		csvWfExprRobotTemplateBegin, csvWfExprRobotTemplateEnd,
		csvWfSynchRobotTemplateBegin, csvWfSynchRobotTemplateEnd, csvduration, csvprocessDuration, csvstartDelay, csvstartProcessDelay, csvnotifyBeforeDeadline, csvtimeToDeadline, csvnotifyAfterDeadline, csvtimePastDeadline, csvresponsibleRole,
		csvWfNodeTemplateLinkBegin, csvWfNodeTemplateLinkEnd,
		csvWfProxyTemplateBegin, csvWfProxyTemplateEnd};
	String courantTag = "";
	//Enumeration of contextual elements
	enum wflElements {workflow, activity, robot, synchRobot, internalMethod, link, process}
	wflElements courantElement = wflElements.workflow;

	@Override
	public void startDocument() throws SAXException {
		super.startDocument();
		
		// Create workflow object
		wfl = new Workflow();
	}
	
	@Override
	public void endDocument() throws SAXException {
		super.endDocument();
	}

	
	/* (non-Javadoc)
	 * Get tag content
	 * @see org.xml.sax.helpers.DefaultHandler#characters(char[], int, int)
	 */
	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {
		super.characters(ch, start, length);
		
		// Read WFL tag
		if (courantTag.equals(tagNames.csvWfProcessTemplateBegin + "/" + tagNames.csvname)) {
			wfl.name = new String(ch, start, length);
			System.out.println("** Reading Workflow:\n\t" + wfl.name);
		}
		
		// Read variables
		if ( courantElement.equals(wflElements.workflow) || courantElement.equals(wflElements.activity) || courantElement.equals(wflElements.robot) || courantElement.equals(wflElements.synchRobot) ) {
			if ( courantTag.endsWith(tagNames.csvWfVariable + "/" + tagNames.csvname) ) {
				var.name = new String(ch, start, length);
				System.out.print("\tvariable:\t" + var.name);
			} else if ( courantTag.endsWith(tagNames.csvWfVariable + "/" + tagNames.csvtype) ) {
				var.type = new String(ch, start, length);
				System.out.println(" : " + var.type);
			} else if ( courantTag.startsWith(tagNames.csvWfVariable + "/") ) {
				//TODO Finir la lecture des attributs des variables
				String attrName = courantTag.substring(courantTag.lastIndexOf("csv") + 3);
				var.attibutes.put(attrName, new String(ch, start, length));
				System.out.println("\t\tAttribute: " + attrName);										
			}
		}
		
		
		// Read Activity
		if (courantTag.equals(tagNames.csvWfAssignedActivityTemplateBegin + "/" + tagNames.csvname)) {
			activity.name = new String(ch, start, length);
			System.out.println("------------------------------------------------------------------------------------------");
			System.out.println("\nReading activity:\t" + activity.name);
		}
		
		// Read Notification flag
		if (courantTag.equals(tagNames.csvWfAssignedActivityTemplateBegin + "/" + tagNames.csvsendNotification)) {
			activity.notification = new String(ch, start, length);
			System.out.println("\nSend Notification:\t" + activity.notification);
		}
		
		// Read Instruction
		if (courantTag.endsWith(tagNames.csvWfAssignedActivityTemplateBegin + "/" + tagNames.csvinstructions)) {
			activity.instruction = new String(ch, start, length);
			activity.instruction = formatTextContent(activity.instruction);
			System.out.println("\n\tActivity: " + activity.instruction);
			System.out.println("---------------------------------------------");
		}
		
		// Read Expression
		if ( (courantElement.equals(wflElements.workflow) || courantElement.equals(wflElements.activity) || courantElement.equals(wflElements.robot) || courantElement.equals(wflElements.synchRobot))
				&& courantTag.endsWith(tagNames.csvWfExpression + "/" + tagNames.csvexpression) ) {
			expression.name = new String(ch, start, length);
			System.out.println("\n\texpression: " + expression.name);
			System.out.println("---------------------------------------------");
		}
		if ( (courantElement.equals(wflElements.workflow) || courantElement.equals(wflElements.activity) || courantElement.equals(wflElements.robot) || courantElement.equals(wflElements.synchRobot))
				&& courantTag.endsWith(tagNames.csvWfExpression + "/" + tagNames.csvexprBody) ) {
			expression.body = new String(ch, start, length);
			expression.body = formatTextContent(expression.body);
			System.out.println(expression.body);
			System.out.println("---------------------------------------------");
		}
		
		// Read Participants
		if ( courantElement.equals(wflElements.activity) && courantTag.equals(tagNames.csvWfActorAssignee + "/" + tagNames.csvactor) ) {
			actor.name = new String(ch, start, length);
			System.out.println("\n\tActor: " + actor.name);			
		}
		if ( courantElement.equals(wflElements.activity) && courantTag.equals(tagNames.csvWfPrincipalAssignee + "/" + tagNames.csvprincipal) ) {
			principal.name = new String(ch, start, length);
			System.out.println("\n\tPrincipal: " + principal.name);			
		}
		if ( courantElement.equals(wflElements.activity) && courantTag.equals(tagNames.csvWfRoleAssignee + "/" + tagNames.csvrole) ) {
			role.name = new String(ch, start, length);
			System.out.println("\n\tRole: " + role.name);			
		}
		if ( courantElement.equals(wflElements.activity) && courantTag.equals(tagNames.csvWfTeamAssignee + "/" + tagNames.csvteamAssignee) ) {
			team.name = new String(ch, start, length);
			System.out.println("\n\tTeam: " + team.name);			
		}
		
		// Read Role Setup
		if ( courantElement.equals(wflElements.activity) && courantTag.equals(tagNames.csvWfRoleSetup + "/" + tagNames.csvassigneeName) ) {
			roleSetup.assignee = new String(ch, start, length);
			System.out.println("\n\tRole Setup: " + roleSetup.assignee);			
		}
		if ( courantElement.equals(wflElements.activity) && courantTag.equals(tagNames.csvWfRoleSetup + "/" + tagNames.csvrole) ) {
			roleSetup.role = new String(ch, start, length);
			System.out.print("\t\t" + roleSetup.role);			
		}
		if ( courantElement.equals(wflElements.activity) && courantTag.equals(tagNames.csvWfRoleSetup + "/" + tagNames.csvviewPermission) ) {
			roleSetup.permission.view = new String(ch, start, length);
			System.out.print(" - view: " + roleSetup.permission.view);			
		}
		if ( courantElement.equals(wflElements.activity) && courantTag.equals(tagNames.csvWfRoleSetup + "/" + tagNames.csvaddPermission) ) {
			roleSetup.permission.add = new String(ch, start, length);
			System.out.print(", add: " + roleSetup.permission.add);			
		}
		if ( courantElement.equals(wflElements.activity) && courantTag.equals(tagNames.csvWfRoleSetup + "/" + tagNames.csvremovePermission) ) {
			roleSetup.permission.remove = new String(ch, start, length);
			System.out.print(", remove: " + roleSetup.permission.remove);			
		}
		
		// Read Resource Pool
		if ( courantElement.equals(wflElements.activity) && courantTag.equals(tagNames.csvWfResourcePool + "/" + tagNames.csvrole) ) {
			resourcePool.role = new String(ch, start, length);
			System.out.println("\n\tResource Pool: role: " + resourcePool.role);			
		}
		if ( courantElement.equals(wflElements.activity) && courantTag.equals(tagNames.csvWfResourcePool + "/" + tagNames.csvresourcePoolType) ) {
			resourcePool.resourceType = new String(ch, start, length);
			System.out.print(", type: " + resourcePool.resourceType);			
		}
		if ( courantElement.equals(wflElements.activity) && courantTag.equals(tagNames.csvWfResourcePool + "/" + tagNames.csvresourcePoolName) ) {
			resourcePool.resourceName = new String(ch, start, length);
			System.out.println(", name: " + resourcePool.resourceName);			
		}
		
		// Read Synchronization Robot
		if ( courantTag.equals(tagNames.csvWfSynchRobotTemplateBegin + "/" + tagNames.csvname) ) {
			synchRobot.name = new String(ch, start, length);
			System.out.println("------------------------------------------------------------------------------------------");
			System.out.println("\nReading Synchronization Robot:\t" + synchRobot.name);
		} else if ( courantTag.startsWith(tagNames.csvWfSynchRobotTemplateBegin + "/") ){
			String attrName = courantTag.substring(courantTag.lastIndexOf("csv") + 3);
			synchRobot.attibutes.put(attrName, new String(ch, start, length));
			System.out.println("\n\tAttribute: " + attrName);						
		}
		
		// Read Robot
		if ( courantTag.equals(tagNames.csvWfExprRobotTemplateBegin + "/" + tagNames.csvname) ) {
			robot.name = new String(ch, start, length);
			System.out.println("------------------------------------------------------------------------------------------");
			System.out.println("\nReading Robot:\t" + robot.name);
		} else if ( courantTag.startsWith(tagNames.csvWfExprRobotTemplateBegin + "/") ){
			String attrName = courantTag.substring(courantTag.lastIndexOf("csv") + 3);
			robot.attibutes.put(attrName, new String(ch, start, length));
			System.out.println("\n\tAttribute: " + attrName);						
		}

	}

	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		super.startElement(uri, localName, qName, attributes);
		
		if ( isInTagEnum(qName) ) {
			if (courantTag.isEmpty()) {
				courantTag = qName;
			} else {
				courantTag = courantTag + TAG_SEPARATOR + qName;
			}
			//System.out.println("Enter in tag:" + courantTag);
			
			// Init variables 
			if (courantTag.endsWith(tagNames.csvWfVariable.toString())) {
				var = wfl.new Variable();
				if (courantElement.equals(wflElements.workflow)) {
					wfl.variableList.add(var);
				} else if (courantElement.equals(wflElements.activity)) {
					activity.variableList.add(var);					
				} else if (courantElement.equals(wflElements.synchRobot)) {
					synchRobot.variableList.add(var);
				} else if (courantElement.equals(wflElements.robot)) {
					robot.variableList.add(var);
				}
			}
			
			// Init Activity
			if (courantTag.equals(tagNames.csvWfAssignedActivityTemplateBegin.toString())) {
				courantElement = wflElements.activity;
				activity = wfl.new Activity();
				wfl.activityList.add(activity);
			}
			
			// Init Expressions
			if ( courantTag.endsWith(tagNames.csvWfExpression.toString()) ) {
				expression = wfl.new Expression();
				if (courantElement.equals(wflElements.workflow)) {				
					wfl.expressionList.add(expression);
				} else if (courantElement.equals(wflElements.activity)) {
					activity.expressionList.add(expression);
				} else if (courantElement.equals(wflElements.synchRobot)) {
					synchRobot.expressionList.add(expression);
				} else if (courantElement.equals(wflElements.robot)) {
					robot.expressionList.add(expression);
				}
			}
			
			// Init Participants
			if ( courantElement.equals(wflElements.activity) && courantTag.endsWith(tagNames.csvWfActorAssignee.toString()) ) {
				actor = wfl.new Actor();
				activity.actorList.add(actor);
			}
			if ( courantElement.equals(wflElements.activity) && courantTag.endsWith(tagNames.csvWfPrincipalAssignee.toString()) ) {
				principal = wfl.new Principal();
				activity.principalList.add(principal);
			}
			if ( courantElement.equals(wflElements.activity) && courantTag.endsWith(tagNames.csvWfRoleAssignee.toString()) ) {
				role = wfl.new Role();
				activity.roleList.add(role);
			}
			if ( courantElement.equals(wflElements.activity) && courantTag.endsWith(tagNames.csvWfTeamAssignee.toString()) ) {
				team = wfl.new Team();
				activity.teamList.add(team);
			}
			
			// Init Role Setup
			if ( courantElement.equals(wflElements.activity) && courantTag.endsWith(tagNames.csvWfRoleSetup.toString()) ) {
				roleSetup = wfl.new RoleSetup();
				activity.roleSetupList.add(roleSetup);
			}	
			
			// Init Resource Pool
			if ( courantElement.equals(wflElements.activity) && courantTag.endsWith(tagNames.csvWfResourcePool.toString()) ) {
				resourcePool = wfl.new ResourcePool();
				activity.resourcePoolList.add(resourcePool);
			}		

			// Internal Method
			if (courantTag.equals(tagNames.csvWfInternalMethodTemplateBegin.toString())) {
				courantElement = wflElements.internalMethod;
			}
			
			// Robot
			if (courantTag.equals(tagNames.csvWfExprRobotTemplateBegin.toString())) {
				courantElement = wflElements.robot;
				robot = wfl.new Robot();
				wfl.robotList.add(robot);
			}
			
			// Synchronization Robot
			if (courantTag.equals(tagNames.csvWfSynchRobotTemplateBegin.toString())) {
				courantElement = wflElements.synchRobot;
				synchRobot = wfl.new SynchRobot();
				wfl.synchRobotList.add(synchRobot);
			}
			
			// Link
			if (courantTag.equals(tagNames.csvWfNodeTemplateLinkBegin.toString())) {
				courantElement = wflElements.link;
			}
			
			// Process
			if (courantTag.equals(tagNames.csvWfProxyTemplateBegin.toString())) {
				courantElement = wflElements.process;
			}
		}

	}
	
	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		super.endElement(uri, localName, qName);
		
		if ( isInTagEnum(qName) ) {
			//System.out.println("Exit from tag:" + courantTag);
			if ( courantTag.equals(qName) ) {
				courantTag = "";
			} else {
				int index = courantTag.lastIndexOf(TAG_SEPARATOR);
				if (index > 0) {
					courantTag = courantTag.substring(0,index);
				}
			}

			if (courantTag.equals(tagNames.csvWfAssignedActivityTemplateEnd)) {
				courantElement = wflElements.workflow;
			}

			if (courantTag.equals(tagNames.csvWfInternalMethodTemplateEnd)) {
				courantElement = wflElements.activity;
			}
			
			// Robot
			if (courantTag.equals(tagNames.csvWfExprRobotTemplateEnd)) {
				courantElement = wflElements.workflow;
			}
			
			// Synchronization Robot
			if (courantTag.equals(tagNames.csvWfSynchRobotTemplateEnd)) {
				courantElement = wflElements.workflow;
			}
			
			// Link
			if (courantTag.equals(tagNames.csvWfNodeTemplateLinkEnd)) {
				courantElement = wflElements.workflow;
			}
			
			// Process
			if (courantTag.equals(tagNames.csvWfProxyTemplateEnd)) {
				courantElement = wflElements.workflow;
			}
		}
		
	}
	

	/**
	 * Check if tag needs to be read
	 * @param tag
	 * @return
	 */
	private boolean isInTagEnum(String tag) {
		try {
			tagNames.valueOf(tag);
		} catch (IllegalArgumentException e) {
			return false;			
		}
		
		return true;
	}
	
	
	/**
	 * Format expressions with line return
	 * @param exp
	 * @return
	 */
	private String formatTextContent(String exp) {
		String result = "";
		result = exp.replace("@", "\n");
		return result;
	}
	
	/**
	 * Get Workflow object
	 * @return workflow
	 */
	public Workflow getWorkflow() {
		wfl.sort();
		return wfl;
	}
}
