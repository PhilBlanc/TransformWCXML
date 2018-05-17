package fr.gfi.tools.xmlReader.xmlObjects;

import java.util.ArrayList;
import java.util.Comparator;
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
		public String instruction = "";
		public String notification = "";
		public List<Variable> variableList;
		public List<Expression> expressionList;
		public List<Actor> actorList;
		public List<Principal> principalList;
		public List<Role> roleList;
		public List<Team> teamList;
		
		public Activity() {
			super();
			variableList = new ArrayList<Variable>();
			expressionList = new ArrayList<Expression>();
			actorList = new ArrayList<Actor>();
			principalList = new ArrayList<Principal>();
			roleList = new ArrayList<Role>();
			teamList = new ArrayList<Team>();
		}
		
	}
	
	
	/**
	 * Sort Workflow components
	 */
	public void sort() {
		variableList.sort(new VariableComparator());
		activityList.sort(new ActivityComparator());
		
		for (Activity activity : activityList) {
			activity.variableList.sort(new VariableComparator());
		}
	}
	
	
	public class Expression {
		public String name;
		public String body;
	}
	
	public class Actor {
		public String name;
	}
	
	public class Principal {
		public String name;
	}
	
	public class Role {
		public String name;
	}
	
	public class Team {
		public String name;
	}
	
	
	/*
	 * Comparators
	 */
	private class ActivityComparator implements Comparator<Activity> {
		@Override
		public int compare(Activity act0, Activity act1) {
			return act0.name.compareTo(act1.name);
		}
	}
	
	private class VariableComparator implements Comparator<Variable> {
		@Override
		public int compare(Variable var0, Variable var1) {
			return var0.name.compareTo(var1.name);
		}
	}
}
