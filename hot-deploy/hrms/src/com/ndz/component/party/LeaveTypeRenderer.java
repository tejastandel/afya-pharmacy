package com.ndz.component.party;

/**
 * @author samir
 */
import org.ofbiz.entity.GenericValue;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

public class LeaveTypeRenderer implements ListitemRenderer {

	public void render(Listitem li, Object data) throws Exception {
		// TODO Auto-generated method stub

		if (data instanceof GenericValue) {
			GenericValue val = (GenericValue) data;
			li.setLabel(val.getString("description"));
			li.setValue(val.getString("enumId"));
		}
	}

}
