/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */


import javolution.util.FastMap
import org.ofbiz.accounting.payment.PaymentWorker
import org.ofbiz.base.util.UtilMisc
import org.ofbiz.base.util.UtilValidate
import org.ofbiz.entity.GenericValue
import org.ofbiz.entity.condition.EntityCondition
import org.ofbiz.entity.condition.EntityOperator
import org.ofbiz.entity.util.EntityFindOptions
import org.ofbiz.entity.util.EntityUtil
import org.ofbiz.order.order.OrderReadHelper
import org.ofbiz.party.contact.ContactHelper
import org.ofbiz.party.contact.ContactMechWorker
import org.ofbiz.product.catalog.CatalogWorker
import org.ofbiz.product.inventory.InventoryWorker

orderId = parameters.orderId;
context.orderId = orderId;

workEffortId = parameters.workEffortId;
assignPartyId = parameters.partyId;
assignRoleTypeId = parameters.roleTypeId;
fromDate = parameters.fromDate;
delegate = parameters.delegate;
if (delegate && fromDate) {
    fromDate = parameters.toFromDate;
}
context.workEffortId = workEffortId;
context.assignPartyId = assignPartyId;
context.assignRoleTypeId = assignRoleTypeId;
context.fromDate = fromDate;
context.delegate = delegate;
context.todayDate = new java.sql.Date(System.currentTimeMillis()).toString();


orderHeader = null;
orderItems = null;
orderAdjustments = null;

if (orderId) {
    orderHeader = delegator.findByPrimaryKey("OrderHeader", [orderId : orderId]);
}

if (orderHeader) {
    // note these are overridden in the OrderViewWebSecure.groovy script if run
    context.hasPermission = true;
    context.canViewInternalDetails = true;

    orderReadHelper = new OrderReadHelper(orderHeader);
    orderItems = orderReadHelper.getOrderItems();
    orderAdjustments = orderReadHelper.getAdjustments();
    orderHeaderAdjustments = orderReadHelper.getOrderHeaderAdjustments();

    orderSubTotal = BigDecimal.ZERO;




    //Override the Order Sub Total in case the Product Price already includes the Price.
    String productStoreId = orderHeader.getString("productStoreId");
    println "ORDERHEADER ***************"+orderHeader.formToIssue;

    if (UtilValidate.isNotEmpty(productStoreId)) {
        GenericValue productStore = delegator.findOne("ProductStore", true, "productStoreId", productStoreId);
        if ("Y".equals(productStore.getString("pricesIncludeTax"))) {
            orderSubTotal = OrderReadHelper.getSubItemTotalWithoutTax(orderItems, orderAdjustments);
        }
    }

    if (UtilValidate.isNotEmpty(orderHeader.formToIssue)) {
        if ("PRICEINCLUDETAX" == orderHeader.formToIssue) {
            orderSubTotal = OrderReadHelper.getSubItemTotalWithoutTax(orderItems, orderAdjustments);
        }
    }

    List orderItemBilling = delegator.findByAnd("OrderItemBilling", ["orderId": parameters.orderId]);
    if (UtilValidate.isNotEmpty(orderItemBilling)) {
        conditions = [];
        conditions.add(EntityCondition.makeCondition("invoiceId", EntityOperator.EQUALS, orderItemBilling.get(0).getString("invoiceId")));
        conditions.add(EntityCondition.makeCondition("invoiceItemTypeId", EntityOperator.EQUALS, "PINV_FPROD_ITEM"));
        conditions.add(EntityCondition.makeCondition("invoiceItemTypeId", EntityOperator.EQUALS, "INV_FPROD_ITEM"));
        List inviList = delegator.findList("InvoiceItem", EntityCondition.makeCondition(conditions, EntityOperator.AND), null, null, null, false);
        for (int i = 0; i < inviList.size(); i++) {
            totalAmount = inviList.get(i).getBigDecimal("amount").multiply(inviList.get(i).getBigDecimal("quantity"));
            orderSubTotal = orderSubTotal.add(totalAmount);
        }
    }

    if (orderSubTotal.compareTo(BigDecimal.ZERO) < 1)
         orderSubTotal = orderReadHelper.getOrderItemsSubTotal();

    context.orderSubTotal = orderSubTotal;
    orderTerms = orderHeader.getRelated("OrderTerm");
    //println " ORDER Sub Total "+ orderSubTotal;

    List<String> orderBy = UtilMisc.toList("sourcePercentage");
    oAdjCon =  EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, parameters.orderId);
    orderAdjustmentGrouped   = delegator.findList("OrderAdjustmentsGrouped", oAdjCon, null, orderBy ,null, false);

    context.orderHeader = orderHeader;
    context.orderReadHelper = orderReadHelper;
    context.orderItems = orderItems;
    context.orderAdjustments = orderAdjustments;
    context.orderHeaderAdjustments = orderHeaderAdjustments;
    context.currencyUomId = orderReadHelper.getCurrency();
    context.orderTerms = orderTerms;
    context.orderAdjustmentGrouped = orderAdjustmentGrouped;
    // get the order type
    orderType = orderHeader.orderTypeId;
    context.orderType = orderType;

    // get the display party
    displayParty = null;
    companyPartyId=null;
    if ("PURCHASE_ORDER".equals(orderType)) {
        displayParty = orderReadHelper.getSupplierAgent();
        context.companyPartyId=orderReadHelper.getPartyFromRole("BILL_TO_CUSTOMER").getString("partyId");
    } else {
        displayParty = orderReadHelper.getPlacingParty();
        context.companyPartyId=orderReadHelper.getPartyFromRole("BILL_FROM_VENDOR").getString("partyId")
    }
    if (displayParty) {
        partyId = displayParty.partyId;
        context.displayParty = displayParty;
        context.partyId = partyId;

        paymentMethodValueMaps = PaymentWorker.getPartyPaymentMethodValueMaps(delegator, displayParty.partyId, false);
        context.paymentMethodValueMaps = paymentMethodValueMaps;
    }

    //otherAdjAmount = OrderReadHelper.calcOrderAdjustments(orderHeaderAdjustments, orderSubTotal, true, false, false);
    //context.otherAdjAmount = otherAdjAmount;

    taxAmount = OrderReadHelper.getOrderTaxByTaxAuthGeoAndParty(orderAdjustments).taxGrandTotal;
    context.taxAmount = taxAmount;

    grandTotal = OrderReadHelper.getOrderGrandTotal(orderItems, orderAdjustments);
    context.grandTotal = grandTotal;
    canceledPromoOrderItem = [:];
    orderItemList = orderReadHelper.getOrderItems();
    orderItemList.each { orderItem -> 
        if("Y".equals(orderItem.get("isPromo")) && "ITEM_CANCELLED".equals(orderItem.get("statusId"))) {
            canceledPromoOrderItem = orderItem;
        }
        orderItemList.remove(canceledPromoOrderItem);
    }
    context.orderItemList = orderItemList;

    List<String> orderBySourcePercentage = UtilMisc.toList("sourcePercentage");

    ordAdjCon = [];
    ordAdjCon.add(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderItemList.get(0).getString("orderId")));
    ordAdjCon.add(EntityCondition.makeCondition("orderAdjustmentTypeId",EntityOperator.EQUALS,"DISCOUNT_ADJUSTMENT"));
    goupedOrderAdjustment = delegator.findList("OrderAdjustmentsGrouped", EntityCondition.makeCondition(ordAdjCon, EntityOperator.AND), null, orderBySourcePercentage, null, false);

    context.goupedOrderAdjustment = goupedOrderAdjustment;

    invItemVatConditions = [];
    invItemVatConditions.add(EntityCondition.makeCondition("orderId",EntityOperator.EQUALS,orderItemList.get(0).getString("orderId")));
    invItemVatConditions.add(EntityCondition.makeCondition("orderAdjustmentTypeId",EntityOperator.EQUALS,"SALES_TAX"));
    invoiceItemsVat = delegator.findList("OrderAdjustmentsGrouped", EntityCondition.makeCondition(invItemVatConditions, EntityOperator.AND), null, orderBySourcePercentage, null, false);

    context.invoiceItemsVat = invoiceItemsVat;

    shippingAddress = orderReadHelper.getShippingAddress();
    context.shippingAddress = shippingAddress;

    billingAddress = orderReadHelper.getBillingAddress();
    context.billingAddress = billingAddress;

    distributorId = orderReadHelper.getDistributorId();
    context.distributorId = distributorId;

    affiliateId = orderReadHelper.getAffiliateId();
    context.affiliateId = affiliateId;

    billingAccount = orderHeader.getRelatedOne("BillingAccount");
    context.billingAccount = billingAccount;
    context.billingAccountMaxAmount = orderReadHelper.getBillingAccountMaxAmount();

    // get a list of all shipments, and a list of ItemIssuances per order item
    allShipmentsMap = [:];
    primaryShipments = orderHeader.getRelated("PrimaryShipment");
    primaryShipments.each { primaryShipment ->
        allShipmentsMap[primaryShipment.shipmentId] = primaryShipment;
    }
    itemIssuancesPerItem = [:];
    itemIssuances = orderHeader.getRelated("ItemIssuance", null, ["shipmentId", "shipmentItemSeqId"]);
    itemIssuances.each { itemIssuance ->
        if (!allShipmentsMap.containsKey(itemIssuance.shipmentId)) {
            iiShipment = itemIssuance.getRelatedOne("Shipment");
            if (iiShipment) {
                allShipmentsMap[iiShipment.shipmentId] = iiShipment;
            }
        }

        perItemList = itemIssuancesPerItem[itemIssuance.orderItemSeqId];
        if (!perItemList) {
            perItemList = [];
            itemIssuancesPerItem[itemIssuance.orderItemSeqId] = perItemList;
        }
        perItemList.add(itemIssuance);
    }
    context.allShipments = allShipmentsMap.values();
    context.itemIssuancesPerItem = itemIssuancesPerItem;

    // get a list of all invoices
    allInvoices = new HashSet();
    orderBilling = delegator.findByAnd("OrderItemBilling", [orderId : orderId], ["invoiceId"]);
    orderBilling.each { billingGv ->
        allInvoices.add(billingGv.invoiceId);
    }
    context.invoices = allInvoices;

    ecl = EntityCondition.makeCondition([
                                    EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId),
                                    EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "PAYMENT_CANCELLED")],
                                EntityOperator.AND);
    orderPaymentPreferences = delegator.findList("OrderPaymentPreference", ecl, null, null, null, false);
    context.orderPaymentPreferences = orderPaymentPreferences;

    // ship groups
    shipGroups = delegator.findByAnd("OrderItemShipGroup", [orderId : orderId], ["shipGroupSeqId"]);
    context.shipGroups = shipGroups;
    orderItemShipGroup=[:];
    if(UtilValidate.isNotEmpty(shipGroups)){
        orderItemShipGroup =   shipGroups.get(0);
    }
     context.orderItemShipGroup=orderItemShipGroup;
    // get Shipment tracking info
    osisCond = EntityCondition.makeCondition([orderId : orderId], EntityOperator.AND);
    osisOrder = ["shipmentId", "shipmentRouteSegmentId", "shipmentPackageSeqId"];
    osisFields = ["shipGroupSeqId", "shipmentId", "shipmentRouteSegmentId", "carrierPartyId", "shipmentMethodTypeId"] as Set;
    osisFields.add("shipmentPackageSeqId");
    osisFields.add("trackingCode");
    osisFields.add("boxNumber");
    osisFindOptions = new EntityFindOptions();
    osisFindOptions.setDistinct(true);
    orderShipmentInfoSummaryList = delegator.findList("OrderShipmentInfoSummary", osisCond, osisFields, osisOrder, osisFindOptions, false);
    context.orderShipmentInfoSummaryList = orderShipmentInfoSummaryList;

    customerPoNumber = null;
    orderItemList.each { orderItem ->
        customerPoNumber = orderItem.correspondingPoId;
    }
    context.customerPoNumber = customerPoNumber;

    statusChange = delegator.findByAnd("StatusValidChange", [statusId : orderHeader.statusId]);
    context.statusChange = statusChange;

    currentStatus = orderHeader.getRelatedOne("StatusItem");
    context.currentStatus = currentStatus;

    orderHeaderStatuses = orderReadHelper.getOrderHeaderStatuses();
    context.orderHeaderStatuses = orderHeaderStatuses;

    adjustmentTypes = delegator.findList("OrderAdjustmentType", null, null, ["description"], null, false);
    context.orderAdjustmentTypes = adjustmentTypes;

    noInternalNotes = delegator.findByAnd("OrderHeaderNoteView", [orderId : orderId, internalNote : "N"], ["-noteDateTime"]);
    context.orderWithoutInternalNotes = noInternalNotes;

    notes = delegator.findByAnd("OrderHeaderNoteView", [orderId : orderId], ["-noteDateTime"]);
    context.orderNotes = notes;

    showNoteHeadingOnPDF = false;
    if (notes && EntityUtil.filterByCondition(notes, EntityCondition.makeCondition("internalNote", EntityOperator.EQUALS, "N")).size() > 0) {
        showNoteHeadingOnPDF = true;
    }
    context.showNoteHeadingOnPDF = showNoteHeadingOnPDF;

    cmvm = ContactMechWorker.getOrderContactMechValueMaps(delegator, orderId);
    context.orderContactMechValueMaps = cmvm;

    orderItemChangeReasons = delegator.findByAnd("Enumeration", [enumTypeId : "ODR_ITM_CH_REASON"], ["sequenceId"]);
    context.orderItemChangeReasons = orderItemChangeReasons;

    if ("PURCHASE_ORDER".equals(orderType)) {
        // for purchase orders, we need also the supplier's postal address
        supplier = orderReadHelper.getBillFromParty();
        if (supplier) {
            supplierContactMechValueMaps = ContactMechWorker.getPartyContactMechValueMaps(delegator, supplier.partyId, false, "POSTAL_ADDRESS");
            context.supplierId = supplier.partyId;
            context.supplierContactMechValueMaps = supplierContactMechValueMaps;
            supplierContactMechValueMaps.each { supplierContactMechValueMap ->
                contactMechPurposes = supplierContactMechValueMap.partyContactMechPurposes;
                contactMechPurposes.each { contactMechPurpose ->
                    if (contactMechPurpose.contactMechPurposeTypeId.equals("GENERAL_LOCATION")) {
                        context.supplierGeneralContactMechValueMap = supplierContactMechValueMap;
                    } else if (contactMechPurpose.contactMechPurposeTypeId.equals("SHIPPING_LOCATION")) {
                        context.supplierShippingContactMechValueMap = supplierContactMechValueMap;
                    } else if (contactMechPurpose.contactMechPurposeTypeId.equals("BILLING_LOCATION")) {
                        context.supplierBillingContactMechValueMap = supplierContactMechValueMap;
                    } else if (contactMechPurpose.contactMechPurposeTypeId.equals("PAYMENT_LOCATION")) {
                        context.supplierPaymentContactMechValueMap = supplierContactMechValueMap;
                    }
                }
            }
        }
    }

    // see if an approved order with all items completed exists
    context.setOrderCompleteOption = false;
    if ("ORDER_APPROVED".equals(orderHeader.statusId)) {
        expr = EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "ITEM_COMPLETED");
        notCreatedItems = orderReadHelper.getOrderItemsByCondition(expr);
        if (!notCreatedItems) {
            context.setOrderCompleteOption = true;
        }
    }

    // get inventory summary for each shopping cart product item
    inventorySummary = dispatcher.runSync("getProductInventorySummaryForItems", [orderItems : orderItems]);
    context.availableToPromiseMap = inventorySummary.availableToPromiseMap;
    context.quantityOnHandMap = inventorySummary.quantityOnHandMap;
    context.mktgPkgATPMap = inventorySummary.mktgPkgATPMap;
    context.mktgPkgQOHMap = inventorySummary.mktgPkgQOHMap;

    // get inventory summary with respect to facility
    productStore = orderHeader.getRelatedOne("ProductStore");
    if (productStore) {
        facility = productStore.getRelatedOne("Facility");
        inventorySummaryByFacility = dispatcher.runSync("getProductInventorySummaryForItems", [orderItems : orderItems, facilityId : facility.facilityId]);
        context.availableToPromiseByFacilityMap = inventorySummaryByFacility.availableToPromiseMap;
        context.quantityOnHandByFacilityMap = inventorySummaryByFacility.quantityOnHandMap;
        context.facility = facility;
    }

    // Get a list of facilities for purchase orders to receive against.
    // These facilities must be owned by the bill-to party of the purchase order.
    // For a given ship group, the allowed facilities are the ones associated
    // to the same contact mech of the ship group.
    if ("PURCHASE_ORDER".equals(orderType)) {
        facilitiesForShipGroup = [:];
        ownerPartyId = orderReadHelper.getBillToParty().partyId;
        Map ownedFacilities = FastMap.newInstance();
        shipGroups.each { shipGroup ->
            lookupMap = [ownerPartyId : ownerPartyId];
            if (shipGroup.contactMechId) {
                lookupMap.contactMechId = shipGroup.contactMechId;
            }
            facilities = delegator.findByAndCache("FacilityAndContactMech", lookupMap);
            facilitiesForShipGroup[shipGroup.shipGroupSeqId] = facilities;
            facilities.each { facility ->
                ownedFacilities[facility.facilityId] = facility;
            }
        }
        context.facilitiesForShipGroup = facilitiesForShipGroup;
        // Now get the list of all the facilities owned by the bill-to-party
        context.ownedFacilities = ownedFacilities.values();
    }

    // set the type of return based on type of order
    if ("SALES_ORDER".equals(orderType)) {
        GenericValue orderRxHeader = delegator.findOne("OrderRxHeader",true,"orderId",orderId);
        context.orderRxHeader=orderRxHeader;

        if(UtilValidate.isNotEmpty(orderRxHeader) && "INSURANCE".equals(orderRxHeader.patientType) && orderRxHeader.benefitPlanId!=null) {
            List<GenericValue> patientInsuranceList = delegator.findList("PatientInsurance",EntityCondition.makeCondition("benefitPlanId", orderRxHeader.benefitPlanId),null,null,null,false);

            if (UtilValidate.isNotEmpty(patientInsuranceList)) {
                GenericValue patientInsurance = EntityUtil.getFirst(patientInsuranceList);
                println '\n************** Patient Insurance **************\n' + patientInsurance;

                context.benefitPlanName = patientInsurance.benefitPlanName;
                context.benefitPlanId = patientInsurance.benefitPlanId;
                context.policyNo = patientInsurance.policyNo;
                context.healthPolicyName = patientInsurance.healthPolicyName;
                context.healthPolicyId = patientInsurance.healthPolicyId;
            }
        }
        context.returnHeaderTypeId = "CUSTOMER_RETURN";
        // also set the product store facility Id for sales orders
        productStore = orderHeader.getRelatedOne("ProductStore");
        if (productStore) {
            context.storeFacilityId = productStore.inventoryFacilityId;
            if (productStore.reqReturnInventoryReceive) {
                context.needsInventoryReceive = productStore.reqReturnInventoryReceive;
            } else {
                context.needsInventoryReceive = "Y";
            }
        }
    } else {
        context.returnHeaderTypeId = "VENDOR_RETURN";
    }

    // QUANTITY: get the returned quantity by order item map
    context.returnQuantityMap = orderReadHelper.getOrderItemReturnedQuantities();

    // INVENTORY: construct a Set of productIds in the order for use in querying for inventory, otherwise these queries can get expensive
    productIds = orderReadHelper.getOrderProductIds();

    // INVENTORY: get the production quantity for each product and store the results in a map of productId -> quantity
    productionMap = [:];
    productIds.each { productId ->
        if (productId) {  // avoid order items without productIds, such as bulk order items
            contextInput = [productId : productId, userLogin : userLogin];
            resultOutput = dispatcher.runSync("getProductManufacturingSummaryByFacility", contextInput);
            manufacturingInQuantitySummaryByFacility = resultOutput.summaryInByFacility;
            Double productionQuantity = 0;
            manufacturingInQuantitySummaryByFacility.values().each { manQuantity ->
                productionQuantity += manQuantity.estimatedQuantityTotal;
            }
            productionMap[productId] = productionQuantity;
        }
    }
    context.productionProductQuantityMap = productionMap;

    // INVENTORY: find the number of products in outstanding sales orders for the same product store
    requiredMap = InventoryWorker.getOutstandingProductQuantitiesForSalesOrders(productIds, delegator);
    context.requiredProductQuantityMap = requiredMap;

    // INVENTORY: find the quantity of each product in outstanding purchase orders
    onOrderMap = InventoryWorker.getOutstandingProductQuantitiesForPurchaseOrders(productIds, delegator);
    context.onOrderProductQuantityMap = onOrderMap;
}

paramString = "";
if (orderId) paramString += "orderId=" + orderId;
if (workEffortId) paramString += "&workEffortId=" + workEffortId;
if (assignPartyId) paramString += "&partyId=" + assignPartyId;
if (assignRoleTypeId) paramString += "&roleTypeId=" + assignRoleTypeId;
if (fromDate) paramString += "&fromDate=" + fromDate;
context.paramString = paramString;

workEffortStatus = null;
if (workEffortId && assignPartyId && assignRoleTypeId && fromDate) {
    fields = [workEffortId : workEffortId, partyId : assignPartyId, roleTypeId : assignRoleTypeId, fromDate : fromDate];
    wepa = delegator.findByPrimaryKey("WorkEffortPartyAssignment", fields);

    if ("CAL_ACCEPTED".equals(wepa?.statusId)) {
        workEffort = delegator.findByPrimaryKey("WorkEffort", [workEffortId : workEffortId]);
        workEffortStatus = workEffort.currentStatusId;
        if (workEffortStatus) {
            context.workEffortStatus = workEffortStatus;
            if (workEffortStatus.equals("WF_RUNNING") || workEffortStatus.equals("WF_SUSPENDED"))
                context.inProcess = true;
        }

        if (workEffort) {
            if ("true".equals(delegate) || "WF_RUNNING".equals(workEffortStatus)) {
                actFields = [packageId : workEffort.workflowPackageId, packageVersion : workEffort.workflowPackageVersion, processId : workEffort.workflowProcessId, processVersion : workEffort.workflowProcessVersion, activityId : workEffort.workflowActivityId];
                activity = delegator.findByPrimaryKey("WorkflowActivity", actFields);
                if (activity) {
                    transitions = activity.getRelated("FromWorkflowTransition", null, ["-transitionId"]);
                    context.wfTransitions = transitions;
                }
            }
        }
    }
}

if (orderHeader) {
    // list to find all the POSTAL_ADDRESS for the shipment party.
    orderParty = delegator.findByPrimaryKey("Party", [partyId : partyId]);
    shippingContactMechList = ContactHelper.getContactMech(orderParty, "SHIPPING_LOCATION", "POSTAL_ADDRESS", false);
    context.shippingContactMechList = shippingContactMechList;

    // list to find all the shipmentMethods from the view named "ProductStoreShipmentMethView".
    if (productStore) {
        context.productStoreShipmentMethList = delegator.findByAndCache('ProductStoreShipmentMethView', [productStoreId: productStore.productStoreId], ['sequenceNumber']);
    }

    // Get a map of returnable items
    returnableItems = [:];
    returnableItemServiceMap = dispatcher.runSync("getReturnableItems", [orderId : orderId]);
    if (returnableItemServiceMap.returnableItems) {
        returnableItems = returnableItemServiceMap.returnableItems;
    }
    context.returnableItems = returnableItems;

    // get the catalogIds for appending items
    if (context.request != null) {
        if ("SALES_ORDER".equals(orderType) && productStore) {
            catalogCol = CatalogWorker.getCatalogIdsAvailable(delegator, productStore.productStoreId, partyId);
        } else {
            catalogCol = CatalogWorker.getAllCatalogIds(request);
        }
        if (catalogCol) {
            currentCatalogId = catalogCol[0];
            currentCatalogName = CatalogWorker.getCatalogName(request, currentCatalogId);
            context.catalogCol = catalogCol;
            context.currentCatalogId = currentCatalogId;
            context.currentCatalogName = currentCatalogName;
        }
    }
}

if (orderHeader) {
   // list to find all the POSTAL_ADDRESS for the party.
   orderParty = delegator.findByPrimaryKey("Party", [partyId : partyId]);
   postalContactMechList = ContactHelper.getContactMechByType(orderParty,"POSTAL_ADDRESS", false);
   context.postalContactMechList = postalContactMechList;
}

if (orderItems) {
    orderItem = EntityUtil.getFirst(orderItems);
    context.orderItem = orderItem;
}

// getting online ship estimates corresponding to this Order from UPS when "Hold" button will be clicked, when user packs from weight package screen.
// This case comes when order's shipping amount is  more then or less than default percentage (defined in shipment.properties) of online UPS shipping amount.

    condn = EntityCondition.makeCondition([
                                      EntityCondition.makeCondition("primaryOrderId", EntityOperator.EQUALS, orderId), 
                                      EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "SHIPMENT_PICKED")],
                                  EntityOperator.AND);
    shipments = delegator.findList("Shipment", condn, null, null, null, false);
    if (shipments) {
        pickedShipmentId = EntityUtil.getFirst(shipments).shipmentId;
        shipmentRouteSegment = EntityUtil.getFirst(delegator.findList("ShipmentRouteSegment",EntityCondition.makeCondition([shipmentId : pickedShipmentId]), null, null, null, false));
        context.shipmentRouteSegmentId = shipmentRouteSegment.shipmentRouteSegmentId;
        context.pickedShipmentId = pickedShipmentId;
        if (pickedShipmentId && shipmentRouteSegment.trackingIdNumber) {
            if ("UPS" == shipmentRouteSegment.carrierPartyId && productStore) {
                resultMap = dispatcher.runSync('upsShipmentAlternateRatesEstimate', [productStoreId: productStore.productStoreId, shipmentId: pickedShipmentId]);
                shippingRates = resultMap.shippingRates;
                shippingRateList = [];
                shippingRates.each { shippingRate ->
                    shippingMethodAndRate = [:];
                    serviceCodes = shippingRate.keySet();
                    serviceCodes.each { serviceCode ->
                        carrierShipmentMethod = EntityUtil.getFirst(delegator.findByAnd("CarrierShipmentMethod", [partyId : "UPS", carrierServiceCode : serviceCode]));
                        shipmentMethodTypeId = carrierShipmentMethod.shipmentMethodTypeId;
                        rate = shippingRate.get(serviceCode);
                        shipmentMethodDescription = EntityUtil.getFirst(carrierShipmentMethod.getRelated("ShipmentMethodType")).description;
                        shippingMethodAndRate.shipmentMethodTypeId = carrierShipmentMethod.shipmentMethodTypeId;
                        shippingMethodAndRate.rate = rate;
                        shippingMethodAndRate.shipmentMethodDescription = shipmentMethodDescription;
                        shippingRateList.add(shippingMethodAndRate);
                    }
               }
                context.shippingRateList = shippingRateList;
            }
        }
    }

    List taxAdjustments = new ArrayList();
    taxAdjustments.addAll(delegator.findList("OrderAdjustmentGrouped",
        EntityCondition.makeCondition([EntityCondition.makeCondition(UtilMisc.toMap("orderId",orderId))]),null,null,null,true));
    context.taxAdjustments = taxAdjustments;

	//telephone
	phones = delegator.findByAnd("PartyContactMechPurpose", [partyId : partyId, contactMechPurposeTypeId : "PRIMARY_PHONE"]);
	selPhones = EntityUtil.filterByDate(phones, nowTimestamp, "fromDate", "thruDate", true);
	if (selPhones) {
		context.phone = delegator.findByPrimaryKey("TelecomNumber", [contactMechId : selPhones[0].contactMechId]);
	}  else{
		context.phone = [:];
	}
	
	//Email
	emails = delegator.findByAnd("PartyContactMechPurpose", [partyId : partyId, contactMechPurposeTypeId : "PRIMARY_EMAIL"]);
	selEmails = EntityUtil.filterByDate(emails, nowTimestamp, "fromDate", "thruDate", true);
	if (selEmails) {
		context.email = delegator.findByPrimaryKey("ContactMech", [contactMechId : selEmails[0].contactMechId]);
	} else {    //get email address from party contact mech
		contacts = delegator.findByAnd("PartyContactMech", [partyId : partyId]);
		selContacts = EntityUtil.filterByDate(contacts, nowTimestamp, "fromDate", "thruDate", true);
		if (selContacts) {
			i = selContacts.iterator();
			while (i.hasNext())    {
				email = i.next().getRelatedOne("ContactMech");
				if ("ELECTRONIC_ADDRESS".equals(email.contactMechTypeId))    {
					context.email = email;
					break;
				}
			}
		}
	}
	