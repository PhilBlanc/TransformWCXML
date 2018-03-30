package fr.gfi.tools.utils;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class XmlUtils {
	/**
	 * Get descendant node by name
	 * @param parentNode
	 * @param nodeName	node name of the child
	 * @return child node if found, null otherwise
	 */
	public static NodeList getChildByName(Node parentNode, String nodeName) {
		NodeList childNodes = null;
		
		if (parentNode instanceof Element) {
			NodeList templateChildNodeList = ((Element)parentNode).getElementsByTagName(nodeName);
			if (templateChildNodeList.getLength() > 0) {
				childNodes = templateChildNodeList;
			}
		}
		
		return childNodes;
	}

	/**
	 * Get descendant node content by name
	 * @param parentNode
	 * @param nodeName	node name of the child
	 * @return child node content if found, null otherwise
	 */
	public static List<String> getChildContentByName(Node parentNode, String nodeName) {
		List<String> nodeContents = null;
		
		NodeList childNodes =  getChildByName(parentNode, nodeName);
		if (childNodes != null && childNodes.getLength() > 0) {
			nodeContents = new ArrayList<String>();
			for (int i = 0; i < childNodes.getLength(); i++) {
				nodeContents.add(childNodes.item(i).getTextContent());
			}
				
		} 
		
		return nodeContents;
	}
	
}
