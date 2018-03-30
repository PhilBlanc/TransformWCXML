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
	
	//Enumeration of tags to read
	enum tagNames {csvWfProcessTemplateBegin, csvWfVariable, csvname, csvtype, 
		csvWfAssignedActivityTemplateBegin, csvWfAssignedActivityTemplateEnd, csvWfExpression, csvexpression, csvexprBody, csvWfActorAssignee, csvactor};
	String courantTag = "";
	//Enumeration of contextual elements
	enum wflElements {workflow, activity, robot, synchRobot, internalMethod, connector, link}
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
		if (courantTag.endsWith(tagNames.csvWfVariable + "/" + tagNames.csvname)) {
			if (var.name != null) throw new SAXException("Variable not initialized: " + new String(ch, start, length));
			var.name = new String(ch, start, length);
			System.out.print("\tvariable:\t" + var.name);
		}
		if (courantTag.endsWith(tagNames.csvWfVariable + "/" + tagNames.csvtype)) {
			var.type = new String(ch, start, length);
			System.out.println(" : " + var.type);
		}	
		
		
		// Read Activity
		if (courantTag.equals(tagNames.csvWfAssignedActivityTemplateBegin + "/" + tagNames.csvname)) {
			activity.name = new String(ch, start, length);
			System.out.println("\nReading activity:\t" + activity.name);
		}
		
		// Read Expression
		if (courantTag.endsWith(tagNames.csvWfExpression + "/" + tagNames.csvexpression)) {
			expression.name = new String(ch, start, length);
			System.out.println("\n\texpression: " + expression.name);
			System.out.println("---------------------------------------------");
		}
		if (courantTag.endsWith(tagNames.csvWfExpression + "/" + tagNames.csvexprBody)) {
			expression.body = new String(ch, start, length);
			expression.body = formatExpression(expression.body);
			System.out.println(expression.body);
			System.out.println("---------------------------------------------");
		}
		
		// Read Actor
		if ( courantTag.equals(tagNames.csvWfActorAssignee + "/" + tagNames.csvactor) ) {
			actor.name = new String(ch, start, length);
			System.out.println("\n\tactor: " + actor.name);			
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
			
			// Init WFL variables 
			if (courantElement.equals(wflElements.workflow) && courantTag.endsWith(tagNames.csvWfVariable.toString())) {
				var = wfl.new Variable();			
				wfl.variableList.add(var);
			}
			
			// Init activity variables 
			if (courantElement.equals(wflElements.activity) && courantTag.endsWith(tagNames.csvWfVariable.toString())) {
				var = wfl.new Variable();			
				activity.variableList.add(var);
			}
			
			// Init Activity
			if (courantTag.equals(tagNames.csvWfAssignedActivityTemplateBegin.toString())) {
				courantElement = wflElements.activity;
				activity = wfl.new Activity();
				wfl.activityList.add(activity);
			}
			
			// Init Expression
			if ( courantTag.endsWith(tagNames.csvWfExpression.toString()) ) {
				expression = wfl.new Expression();
				if (courantElement.equals(wflElements.activity)) {
					activity.expressionList.add(expression);
				} else {
					wfl.expressionList.add(expression);
				}
			}
			
			// Init Actor
			if ( courantElement.equals(wflElements.activity) && courantTag.endsWith(tagNames.csvWfActorAssignee.toString()) ) {
				actor = wfl.new Actor();
				activity.actorList.add(actor);
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
	private String formatExpression(String exp) {
		String result = "";
		result = exp.replace("@", "\n");
		return result;
	}
	
	/**
	 * Get Workflow object
	 * @return workflow
	 */
	public Workflow getWorkflow() {
		return wfl;
	}
}
