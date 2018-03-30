package fr.gfi.tools.xmlReader.SoftType;

import java.util.ArrayList;
import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.ext.DefaultHandler2;

import fr.gfi.tools.xmlReader.xmlObjects.SoftType;

public class SoftTypeHandler extends DefaultHandler2 {
	private static String TAG_SEPARATOR = "/";
	private static String DISPLAY_NAME = "displayName";
	private static String DATA_UTILITY = "dataUtilityId";
	private static String LAYOUT_SEPARATOR = "SEPARATOR=com.ptc.core.lwc.server.LWCSeparator";
		
	List<SoftType> typeList;
	SoftType type;
	SoftType.Attribute attribute;
	SoftType.Constraint constraint;
	SoftType.Layout layout;
	SoftType.Group group;
	SoftType.GroupMember groupMember;
	String attributeContext;

	
	//Enumeration of tags to read
	enum tagNames {csvBeginTypeDefView, csvPropertyValue, csvname, csvtypeParent,
		csvBeginAttributeDefView, csvEndAttributeDefView, csvBeginConstraintDefView, csvruleClassname, csvruleData, csvdatatype, csvIBA, csvvalue,
		csvBeginLayoutDefView, csvEndLayoutDefView, csvBeginGroupDefView, csvEndGroupDefView, 
		csvBeginGroupMemberView, csvEndGroupMemberView, csvtypeContext, csvlocale_en_us, csvlocale_en_gb, csvlocale_fr};
	String courantTag = "";
	//Enumeration of contextual elements
	enum typeElements {type, typeDisplayName, attribute, attributeDisplayName, layout, 
		group, groupDisplayName, groupMember, dataUtility}
	typeElements courantElement = typeElements.type;

	@Override
	public void startDocument() throws SAXException {
		super.startDocument();
		
		// Create type objects
		typeList = new ArrayList<SoftType>();
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
		
		String tagContent = new String(ch, start, length);
		
		/* 
		 * Read SoftType 
		 */
		if (courantTag.equals(tagNames.csvBeginTypeDefView + "/" + tagNames.csvname)) {
			type.name = tagContent;
			System.out.println("** Reading Type:\n\t" + type.name);
		}
		if (courantTag.equals(tagNames.csvBeginTypeDefView + "/" + tagNames.csvtypeParent)) {
			type.parentType = tagContent;
			System.out.println("\tParent type: " + type.parentType);
		}
		// Search type display name
		if (courantElement.equals(typeElements.type) 
				&& courantTag.equals(tagNames.csvPropertyValue + "/" + tagNames.csvname)
				&& tagContent.equals(DISPLAY_NAME)) {
			courantElement = typeElements.typeDisplayName;
		}
		if (courantElement.equals(typeElements.typeDisplayName) && courantTag.equals(tagNames.csvPropertyValue + "/" + tagNames.csvvalue)) {
			type.displayName = tagContent;
			System.out.println("\tDisplay name: " + type.displayName);
		}
		
		/*
		 * Read Attributes
		 */
		if (courantTag.equals(tagNames.csvBeginAttributeDefView + "/" + tagNames.csvname)) {
			attribute.name = tagContent;
			System.out.print("\tAttribute: " + attribute.name);
		}
		// Search attribute display name
		if (courantElement.equals(typeElements.attribute) 
				&& courantTag.equals(tagNames.csvPropertyValue + "/" + tagNames.csvname)
				&& tagContent.equals(DISPLAY_NAME)) {
			courantElement = typeElements.attributeDisplayName;
		}
		if (courantTag.equals(tagNames.csvBeginAttributeDefView + "/" + tagNames.csvdatatype)) {
			attribute.type = tagContent;
			System.out.print(" : " + attribute.type);
		}
		if (courantTag.equals(tagNames.csvBeginAttributeDefView + "/" + tagNames.csvIBA)) {
			attribute.iba = tagContent;
			if (attribute.iba != null) System.out.print(" (" + attribute.iba + ")");
		}
		if (courantElement.equals(typeElements.attributeDisplayName) &&  courantTag.equals(tagNames.csvPropertyValue + "/" + tagNames.csvvalue)) {
			attribute.displayName = tagContent;
			System.out.println(" - " + attribute.displayName);
		}
		// Read constraints
		if (courantElement.equals(typeElements.attribute) &&  courantTag.equals(tagNames.csvBeginConstraintDefView + "/" + tagNames.csvruleClassname)) {
			constraint = type.new Constraint();
			attribute.constraintList.add(constraint);
			constraint.type = tagContent;
			System.out.println("\t\tConstraint: " + tagContent);
		}		
		if (courantElement.equals(typeElements.attribute) &&  courantTag.equals(tagNames.csvBeginConstraintDefView + "/" + tagNames.csvruleData)) {
			//TODO To be managed: global enumeration (csvBeginEnumDefView)
			constraint.rule = tagContent;
			System.out.println("\t\t\t" + tagContent);
		}	
		
		/* 
		 * Read Layouts
		 */
		if (courantTag.equals(tagNames.csvBeginLayoutDefView + "/" + tagNames.csvname)) {
			layout.name = tagContent;
			System.out.println("Layout:\t" + layout.name);
		}
		// Search group display name
		if (courantElement.equals(typeElements.group) 
				&& courantTag.equals(tagNames.csvPropertyValue + "/" + tagNames.csvname)
				&& tagContent.equals(DISPLAY_NAME)) {
			courantElement = typeElements.groupDisplayName;
		}
		if ( courantElement.equals(typeElements.groupDisplayName) ) {
			if ( courantTag.equals(tagNames.csvPropertyValue + "/" + tagNames.csvvalue) ) {
				group.name = tagContent;
				System.out.println("\t Group:\t" + group.name);
			}
			if ( courantTag.equals(tagNames.csvPropertyValue + "/" + tagNames.csvlocale_en_us) ) {
				group.localeUS = tagContent;
			}
			if ( courantTag.equals(tagNames.csvPropertyValue + "/" + tagNames.csvlocale_en_gb) ) {
				group.localeGB = tagContent;
			}
			if ( courantTag.equals(tagNames.csvPropertyValue + "/" + tagNames.csvlocale_fr) ) {
				group.localeFr = tagContent;
			}
		}
		// Member
		if (courantElement.equals(typeElements.groupMember) &&  courantTag.equals(tagNames.csvBeginGroupMemberView + "/" + tagNames.csvtypeContext)) {
			attributeContext = tagContent;
		}
		if (courantElement.equals(typeElements.groupMember) &&  courantTag.equals(tagNames.csvBeginGroupMemberView + "/" + tagNames.csvname)) {
			SoftType.Attribute attr = findAttribute(tagContent) ;
			if (attr != null) {
				groupMember.attribute = attr;
				System.out.println("\t\tAttribute: " + attr.displayName + " (" + attr.name + ")");
				group.memberList.add(groupMember);
			} else {
				// Ignore Layout separators
				if (!tagContent.startsWith(LAYOUT_SEPARATOR)) {					
					groupMember.ref = attributeContext + ":" + tagContent;
					System.out.println("\t\tAttribute: " + groupMember.ref);
					group.memberList.add(groupMember);
				}
			}
		}
		// Search data utility in group member
		if (courantElement.equals(typeElements.groupMember) 
				&& courantTag.equals(tagNames.csvPropertyValue + "/" + tagNames.csvname)
				&& tagContent.equals(DATA_UTILITY)) {
			courantElement = typeElements.dataUtility;
		}
		if (courantElement.equals(typeElements.dataUtility) &&  courantTag.equals(tagNames.csvPropertyValue + "/" + tagNames.csvvalue)) {
			groupMember.dataUtility = tagContent;
			System.out.println("\t\t\tDataUtility:\t" + groupMember.dataUtility);
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


			//Read begin tags
			if (qName.equals(tagNames.csvBeginTypeDefView.toString())) {
				courantElement = typeElements.type;
				type = new SoftType();
				typeList.add(type);
			}

			if (qName.equals(tagNames.csvBeginAttributeDefView.toString())) {
				courantElement = typeElements.attribute;
				attribute = type.new Attribute();
				type.attributeList.add(attribute);
			}
			if (qName.equals(tagNames.csvBeginLayoutDefView.toString())) {
				courantElement = typeElements.layout;
				layout = type.new Layout();
				type.layoutList.add(layout);
			}
			if (qName.equals(tagNames.csvBeginGroupDefView.toString())) {
				courantElement = typeElements.group;
				group = type.new Group();
				layout.groupList.add(group);
			}
			if (qName.equals(tagNames.csvBeginGroupMemberView.toString())) {
				courantElement = typeElements.groupMember;
				groupMember = type.new GroupMember();
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

			//Read end tags
			if (qName.equals(tagNames.csvEndAttributeDefView.toString())) {
				courantElement = typeElements.type;
			}
			if (qName.equals(tagNames.csvEndLayoutDefView.toString())) {
				courantElement = typeElements.type;
			}
			if (qName.equals(tagNames.csvEndGroupDefView.toString())) {
				courantElement = typeElements.layout;
			}
			if (courantElement.equals(typeElements.attributeDisplayName) && qName.equals(tagNames.csvPropertyValue.toString())) {
				courantElement = typeElements.attribute;
			}
			if (courantElement.equals(typeElements.typeDisplayName) && qName.equals(tagNames.csvPropertyValue.toString())) {
				courantElement = typeElements.type;
			}
			if (courantElement.equals(typeElements.groupDisplayName) && qName.equals(tagNames.csvPropertyValue.toString())) {
				courantElement = typeElements.group;
			}
			if (courantElement.equals(typeElements.groupMember) && qName.equals(tagNames.csvEndGroupMemberView.toString())) {
				courantElement = typeElements.group;
			}
			if (courantElement.equals(typeElements.dataUtility) && qName.equals(tagNames.csvPropertyValue.toString())) {
				courantElement = typeElements.groupMember;
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
	 * Get read list of SoftType object
	 * @return type
	 */
	public List<SoftType> getTypes() {
		// Sort type objects
		for(SoftType type : typeList) {
			type.sort();
		}
		return typeList;
	}
	
	/**
	 * Search for attribute by name in type attribute list
	 * @param name	Attribute name
	 * @return Attribute object found else null
	 */
	private SoftType.Attribute findAttribute(String name) {
		SoftType.Attribute result = null;
		
		for (SoftType.Attribute attribute : type.attributeList) {
			if (attribute.name.equals(name)) {
				result = attribute;
				break;
			}
		}
		
		return result;
	}
}
