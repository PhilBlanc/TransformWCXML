package fr.gfi.tools.xmlReader.xmlObjects;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class SoftType {
	public String name = "";
	public String parentType = "";
	public String displayName = "";

	// Attribute List
	public List<Attribute> attributeList = new ArrayList<Attribute>();

	// Layout list
	public List<Layout> layoutList = new ArrayList<Layout>();
	
	/**
	 * Sort type components
	 */
	public void sort() {
		attributeList.sort(new AttributeComparator());
		
		for (Attribute attribute : attributeList) {
			attribute.constraintList.sort(new ConstraintComparator());
		}
	}
	
	/*
	 * Subclasses
	 */
	public class Attribute {
		public String name;
		public String displayName;
		public String type;
		public String iba;
		public List<Constraint> constraintList = new ArrayList<Constraint>();
	}
	
	public class Constraint {
		public String type;
		public String rule;		
	}

	public class Layout {
		public String name;
		public List<Group> groupList = new ArrayList<Group>();
	}
	
	public class Group {
		public String name;
		public String localeUS;
		public String localeGB;
		public String localeFr;
		public List<GroupMember> memberList = new ArrayList<GroupMember>();		
	}
	
	public class GroupMember {
		public Attribute attribute;
		public String dataUtility;
		public String ref;
	}
	
	/*
	 * Comparators
	 */
	private class AttributeComparator implements Comparator<Attribute> {
		@Override
		public int compare(Attribute attr0, Attribute attr1) {
			return attr0.name.compareTo(attr1.name);
		}
	}
	
	private class ConstraintComparator implements Comparator<Constraint> {
		@Override
		public int compare(Constraint constr0, Constraint constr1) {
			return constr0.type.compareTo(constr1.type);
		}
	}
}
