package fr.gfi.tools.xmlReader.workflow;

import java.io.File;
import java.io.FilenameFilter;

class WflFilenameFilter implements FilenameFilter { 

	public boolean accept(File dir, String name) {
		boolean isAccepted = true;
		
		if (!name.endsWith(".xml")) isAccepted = false;
		if (name.endsWith("_ok.xml")) isAccepted = false;
		if (name.endsWith("_tmp.xml")) isAccepted = false;
		
		return isAccepted;
	}

}
