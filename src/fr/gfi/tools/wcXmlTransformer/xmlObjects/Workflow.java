package fr.gfi.tools.wcXmlTransformer.xmlObjects;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Workflow {
	public String name = "";
	
	// WFL Variable list
	public List<Variable> variableList;
	
	// WFL Expression list
	public List<Expression> expressionList;
	
	// Activity List
	public List<Activity> activityList;
	
	// Synchronization Robot List
	public ArrayList<SynchRobot> synchRobotList;
	
	// Robot List
	public ArrayList<Robot> robotList;
	
	public Workflow() {
		variableList = new ArrayList<Workflow.Variable>();
		expressionList = new ArrayList<Expression>();
		activityList = new ArrayList<Activity>();
		synchRobotList = new ArrayList<SynchRobot>();
		robotList = new ArrayList<Robot>();
	}

	public class Variable {
		public String name;
		public String type;
		public Map<String,String> attibutes = new HashMap<String,String>();
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
		public List<RoleSetup> roleSetupList;
		public List<ResourcePool> resourcePoolList;
		
		public Activity() {
			super();
			variableList = new ArrayList<Variable>();
			expressionList = new ArrayList<Expression>();
			actorList = new ArrayList<Actor>();
			principalList = new ArrayList<Principal>();
			roleList = new ArrayList<Role>();
			teamList = new ArrayList<Team>();
			roleSetupList = new ArrayList<RoleSetup>();
			resourcePoolList = new ArrayList<ResourcePool>();
		}
		
	}
	
	public class SynchRobot {
		public String name = "";
		public Map<String,String> attibutes = new HashMap<String,String>();
		public List<Variable> variableList;
		public List<Expression> expressionList;
		
		public SynchRobot() {
			super();
			variableList = new ArrayList<Variable>();
			expressionList = new ArrayList<Expression>();			
		}
	}
	
	public class Robot {
		public String name = "";
		public Map<String,String> attibutes = new HashMap<String,String>();
		public List<Variable> variableList;
		public List<Expression> expressionList;
		
		public Robot() {
			super();
			variableList = new ArrayList<Variable>();
			expressionList = new ArrayList<Expression>();			
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
			activity.roleSetupList.sort(new RoleSetupComparator());
			activity.resourcePoolList.sort(new ResourcePoolComparator());
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
	
	public class RoleSetup {
		public String assignee;
		public String role;
		public Permission permission;
		
		public RoleSetup() {
			super();
			permission = new Permission();
		}
	}
	
	public class Permission {
		public String view = "false";
		public String add = "false";
		public String remove = "false";
	}
	
	public class ResourcePool {
		public String role = "";
		public String resourceType = "";
		public String resourceName = "";
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
	
	private class RoleSetupComparator implements Comparator<RoleSetup> {
		@Override
		public int compare(RoleSetup roleSetup0, RoleSetup roleSetup1) {
			if (roleSetup0.assignee.equals(roleSetup1.assignee)) {
				return roleSetup0.role.compareTo(roleSetup1.role);
			} else {
				return roleSetup0.assignee.compareTo(roleSetup1.assignee);
			}
		}
	}
	
	private class ResourcePoolComparator implements Comparator<ResourcePool> {
		@Override
		public int compare(ResourcePool resourcePool0, ResourcePool resourcePool1) {
			if (resourcePool0.role.equals(resourcePool1.role)) {
				if (resourcePool0.resourceType.equals(resourcePool1.resourceType)) {
					return resourcePool0.resourceName.compareTo(resourcePool1.resourceName);
				} else {
					return resourcePool0.resourceType.compareTo(resourcePool1.resourceType);
				}
			} else {
				return resourcePool0.role.compareTo(resourcePool1.role);
			}
		}
	}
}
