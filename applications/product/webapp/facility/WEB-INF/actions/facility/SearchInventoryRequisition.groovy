import java.sql.Timestamp;
import java.text.SimpleDateFormat;

import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;


String invRequisitionId = request.getParameter("invRequisitionId");

String fromDate = request.getParameter("fromDate");
String thruDate = request.getParameter("thruDate");

String facilityIdFrom = request.getParameter("facilityIdFrom");
String facilityIdTo = request.getParameter("facilityId");
String status = request.getParameter("status");

SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

List<EntityCondition> allCondition = [] as ArrayList;
List andExprs = new ArrayList();
	//andExprs.add(EntityCondition.makeCondition("facilityIdTo", EntityOperator.EQUALS, facilityIdTo));
if(invRequisitionId)
	andExprs.add(EntityCondition.makeCondition("invRequisitionId", EntityOperator.EQUALS,invRequisitionId));
if(fromDate){
	Date date = formatter.parse(fromDate);
	Timestamp fDate = UtilDateTime.toTimestamp(date);
	andExprs.add(EntityCondition.makeCondition("requestDate", EntityOperator.GREATER_THAN_EQUAL_TO,fDate));
}
if(thruDate){
	Date date = formatter.parse(thruDate);
	Timestamp tDate = UtilDateTime.toTimestamp(date);
	andExprs.add(EntityCondition.makeCondition("requestDate", EntityOperator.LESS_THAN_EQUAL_TO,tDate));
}
if(status)
	andExprs.add(EntityCondition.makeCondition("status", EntityOperator.EQUALS, status));
if(facilityIdFrom)
	andExprs.add(EntityCondition.makeCondition("facilityIdFrom", EntityOperator.EQUALS, facilityIdFrom));
	
allCondition.add(EntityCondition.makeCondition(andExprs, EntityOperator.AND));

EntityCondition condition = EntityCondition.makeCondition(allCondition);

List<GenericValue> inventoryRequisitionList = delegator.findList("InventoryRequisition",condition,null,null,null,false);

context.inventoryRequisitionList = inventoryRequisitionList;

request.setAttribute("inventoryRequisitionList", inventoryRequisitionList);

return "success";
