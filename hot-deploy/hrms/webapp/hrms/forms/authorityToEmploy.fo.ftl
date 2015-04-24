<#escape x as x?xml>
    <#assign employeeId = parameters.partyId>
    <#assign employee = delegator.findOne("Person", {"partyId" : employeeId}, false)>
    <#if parameters.positionId?has_content>
        <#assign emplPositionTypeList = delegator.findByAnd("EmplPosition",{"emplPositionId":parameters.positionId})>
        <#if emplPositionTypeList?has_content>
            <#assign emplPositionTypeGv= emplPositionTypeList[0]>
            <#assign positionType = emplPositionTypeGv.emplPositionTypeId>
            <#assign emplPosGv = delegator.findByPrimaryKey("EmplPositionType",{"emplPositionTypeId":positionType})>
            <#assign description = emplPosGv.description>
            <#assign departmentId = emplPositionTypeGv.partyId>
            <#assign departmentPositionList = delegator.findByAnd("DepartmentPosition", {"departmentId":departmentId})>
            <#if departmentPositionList?has_content>
                <#assign departmentPosition = departmentPositionList[0]>
                <#assign departmentName = departmentPosition.departmentName>
            </#if>
        </#if>
    </#if>
    <#if employeeId?has_content>
        <#assign employmentList= delegator.findByAnd("Employment",{"partyIdTo":employeeId})>
        <#assign joiningDate = employmentList[0].fromDate?string("dd/MM/yyyy")>
    </#if>
    <#assign employeTerimationDetailList = delegator.findByAnd("MaxTerminationDetail",{"partyId":employeeId,"statusId":"ET_ADM_APPROVED"})>
    <#if employeTerimationDetailList?has_content>
        <#assign employeTerimationDetail= employeTerimationDetailList[0]>
        <#assign durationOfEmployement = Static["org.ofbiz.base.util.UtilDateTime"].formatInterval(employmentList[0].fromDate,employeTerimationDetailList[0].terminationDate,Static["java.util.Locale"].ENGLISH)>
    </#if>
<fo:block font-size="15pt" text-align="center" font-weight="bold" text-decoration="underline">AUTHORISATION FOR EMPLOYMENT</fo:block>
<fo:block font-size="12pt" text-align="right" >DATE: <fo:inline border-bottom-style="dotted" font-weight="bold">${.now?string("dd/MM/yyyy")}</fo:inline></fo:block>
<fo:block><fo:leader/></fo:block>
<fo:block  font-weight="bold">TO: HUMAN RESOURCE OFFICE,</fo:block>
<fo:block><fo:leader/></fo:block>
<fo:block><fo:leader/></fo:block>
<fo:block>You are authorised to employ Mr/Mrs/Ms <fo:inline border-bottom-style="dotted" font-weight="bold">${employee.firstName?if_exists}&#32;${employee.middleName?if_exists}&#32;${employee.lastName?if_exists}</fo:inline></fo:block>
<fo:block><fo:leader/></fo:block>
<fo:table margin-left="25pt" table-layout="fixed" space-after.optimum="2pt">
    <fo:table-column column-width="proportional-column-width(2)"/>
    <fo:table-column column-width="proportional-column-width(2)"/>
    <fo:table-body>
        <fo:table-row height="25pt">
            <fo:table-cell><fo:block>(i)&#32;&#32;&#32;&#32;Discipline title: </fo:block></fo:table-cell>
            <fo:table-cell><fo:block>
                <#if description?exists>
                    <fo:inline border-bottom-style="dotted" font-weight="bold">${description}</fo:inline>
                    <#else >
                        _____________________________
                </#if>
            </fo:block></fo:table-cell>
        </fo:table-row>
        <fo:table-row height="25pt">
            <fo:table-cell><fo:block>(ii)&#32;&#32;&#32;&#32;Department:     </fo:block></fo:table-cell>
            <fo:table-cell><fo:block>
                <#if departmentName?exists>
                    <fo:inline border-bottom-style="dotted" font-weight="bold">${departmentName}</fo:inline>
                <#else >
                    _____________________________
                </#if>
            </fo:block></fo:table-cell>
        </fo:table-row>
        <fo:table-row height="25pt">
            <fo:table-cell><fo:block>(iii)&#32;&#32;&#32;&#32;Salary Grade: </fo:block></fo:table-cell>
            <fo:table-cell><fo:block>
                <#if employee.grades?exists>
                    <fo:inline border-bottom-style="dotted" font-weight="bold">${employee.grades}</fo:inline>
                <#else >
                    _____________________________
                </#if>
            </fo:block></fo:table-cell>
        </fo:table-row>
        <fo:table-row height="25pt">
            <fo:table-cell><fo:block>(iv)&#32;&#32;&#32;&#32;Effective date: </fo:block></fo:table-cell>
            <fo:table-cell><fo:block>
                <#if joiningDate?exists>
                    <fo:inline border-bottom-style="dotted" font-weight="bold">${joiningDate}</fo:inline>
                <#else >
                    _____________________________
                </#if>
            </fo:block></fo:table-cell>
        </fo:table-row>
        <fo:table-row height="25pt">
            <fo:table-cell><fo:block>(v)&#32;&#32;&#32;&#32;Duration of Employment: </fo:block></fo:table-cell>
            <fo:table-cell><fo:block>
                <#if durationOfEmployement?exists>
                    <fo:inline border-bottom-style="dotted" font-weight="bold">${durationOfEmployement}</fo:inline>
                <#else >
                    _____________________________
                </#if>
            </fo:block></fo:table-cell>
        </fo:table-row>
        <fo:table-row height="25pt">
            <fo:table-cell><fo:block>(vi)&#32;&#32;&#32;&#32;Proposed Monthly Basic Salary : </fo:block></fo:table-cell>
            <fo:table-cell><fo:block>_____________________________</fo:block></fo:table-cell>
        </fo:table-row>
        <fo:table-row height="25pt">
            <fo:table-cell><fo:block>(vii)&#32;&#32;&#32;&#32;Others: </fo:block></fo:table-cell>
            <fo:table-cell><fo:block>_____________________________</fo:block></fo:table-cell>
        </fo:table-row>
    </fo:table-body>
</fo:table>
<fo:block><fo:leader/></fo:block>
<fo:block><fo:leader/></fo:block>
<fo:block><fo:leader/></fo:block>
<fo:block><fo:leader/></fo:block>
<fo:block>Kindly process the application which should include the following documents;</fo:block>
<fo:block><fo:leader/></fo:block>
<fo:block><fo:leader/></fo:block>
<fo:block margin-left="25pt">(a)&#32;&#32;&#32;&#32;Copy of NRC</fo:block>
<fo:block><fo:leader/></fo:block>
<fo:block margin-left="25pt">(b) &#32;&#32;&#32;&#32;Letter of offer</fo:block>
<fo:block><fo:leader/></fo:block>
<fo:block>For &amp; on behalf of</fo:block>
<fo:block font-weight="bold">ZAMBEZI PORTLAND CEMENT LTD.</fo:block>
<fo:block><fo:leader/></fo:block>
<fo:block><fo:leader/></fo:block>
<fo:block><fo:leader/></fo:block>
<fo:block><fo:leader/></fo:block>
<fo:block text-decoration="underline" font-weight="bold">CHIEF EXECUTIVE OFFICER</fo:block>


















<#--page 2-->

</#escape>