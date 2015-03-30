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
 
import org.ofbiz.base.util.*;
 
 
String myCompanyId;
partyId = UtilProperties.getPropertyValue("general.properties", "ORGANIZATION_PARTY");
if (userLogin) {
    companies = delegator.findByAnd("PartyRelationship",
            [partyIdTo: userLogin.partyId,
             roleTypeIdTo: "CONTACT",
             roleTypeIdFrom: "ACCOUNT"]);
    if (companies) {
        company = companies[0];
        myCompanyId = company.partyIdFrom;
    } else {
        myCompanyId = userLogin.partyId;
    }
    partyGroupList = delegator.findByAnd("PartyGroup",[partyId: partyId]);
    context.myCompanyId = myCompanyId;
    if(partyGroupList){
    	context.vatTinNumber = partyGroupList[0].vatTinNumber;
    }
}