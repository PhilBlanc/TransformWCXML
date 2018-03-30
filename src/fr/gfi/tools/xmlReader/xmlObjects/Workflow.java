package fr.gfi.tools.xmlReader.xmlObjects;

import java.util.ArrayList;
import java.util.List;

public class Workflow {
	public String name = "";
	
	// WFL Variable list
	public List<Variable> variableList;
	
	// WFL Expression list
	public List<Expression> expressionList;
	
	// Activity List
	public List<Activity> activityList;
	
	public Workflow() {
		variableList = new ArrayList<Workflow.Variable>();
		expressionList = new ArrayList<Expression>();
		activityList = new ArrayList<Activity>();
	}

	public class Variable {
		public String name;
		public String type;
	}
	
	public class Activity {
		public String name;
		public List<Variable> variableList;
		public List<Expression> expressionList;
		public List<Actor> actorList;
		
		public Activity() {
			super();
			variableList = new ArrayList<Variable>();
			expressionList = new ArrayList<Expression>();
			actorList = new ArrayList<Actor>();
		}
		
	}
	
	public class Expression {
		public String name;
		public String body;
	}
	
	public class Actor {
		public String name;
	}
}
