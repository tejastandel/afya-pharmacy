<#--
Licensed to the Apache Software Foundation (ASF) under one
or more contributor license agreements.  See the NOTICE file
distributed with this work for additional information
regarding copyright ownership.  The ASF licenses this file
to you under the Apache License, Version 2.0 (the
"License"); you may not use this file except in compliance
with the License.  You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing,
software distributed under the License is distributed on an
"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
KIND, either express or implied.  See the License for the
specific language governing permissions and limitations
under the License.
-->
<#escape x as x?xml>

    <#if security.hasEntityPermission("ORDERMGR", "_VIEW", session)>

    <fo:block font-size="15pt" text-align="center" font-weight="bold">Sales</fo:block>
    <#-- blank line -->
    <fo:table table-layout="fixed" space-after.optimum="2pt">
        <fo:table-column/>
        <fo:table-column/>
        <fo:table-body>
            <fo:table-row height="12.5px" space-start=".15in">
                <fo:table-cell>
                    <fo:block/>
                </fo:table-cell>
                <fo:table-cell>
                    <fo:block/>
                </fo:table-cell>
            </fo:table-row>
        </fo:table-body>
    </fo:table>
    <fo:table table-layout="fixed" space-after.optimum="2pt">
        <fo:table-column column-width="proportional-column-width(5)"/>
        <fo:table-column column-width="proportional-column-width(70)"/>
        <fo:table-column column-width="proportional-column-width(40)"/>
        <fo:table-body>
            <fo:table-row>
                <fo:table-cell>
                    <fo:block/>
                </fo:table-cell>
                <fo:table-cell>
                    <#if fromDate?has_content>
                        <fo:block number-columns-spanned="2">
                            From Date : ${fromDate?if_exists?string("dd/MM/yyyy")}
                        </fo:block>
                    <#else>
                        <fo:block number-columns-spanned="2">
                            From Date :
                        </fo:block>
                    </#if>
                </fo:table-cell>
                <fo:table-cell>
                    <#if thruDate?has_content>
                        <fo:block number-columns-spanned="2">
                            Thru Date : ${thruDate?if_exists?string("dd/MM/yyyy")}
                        </fo:block>
                    <#else>
                        <fo:block number-columns-spanned="2">
                            Thru Date :
                        </fo:block>
                    </#if>
                </fo:table-cell>
            </fo:table-row>
        </fo:table-body>
    </fo:table>
    <fo:block>
        <fo:leader></fo:leader>
    </fo:block>

        <#if orderItemList?has_content>
        <fo:block space-after.optimum="10pt" font-size="8pt">
            <fo:table table-layout="fixed" border-style="solid" border-color="black">
				<fo:table-column column-width="proportional-column-width(2)"/>
                <fo:table-column column-width="proportional-column-width(1)"/>
                <fo:table-column column-width="proportional-column-width(3)"/>
                <fo:table-column column-width="proportional-column-width(2)"/>
                <fo:table-column column-width="proportional-column-width(2)"/>
                <fo:table-column column-width="proportional-column-width(2)"/>
                <fo:table-column column-width="proportional-column-width(2)"/>
                <fo:table-column column-width="proportional-column-width(2)"/>
                <fo:table-column column-width="proportional-column-width(2)"/>
                <fo:table-column column-width="proportional-column-width(2)"/>
                <fo:table-column column-width="proportional-column-width(3)"/>
                <fo:table-column column-width="proportional-column-width(3)"/>
                <fo:table-column column-width="proportional-column-width(3)"/>
                <fo:table-column column-width="proportional-column-width(2)"/>
                <fo:table-header>
                    <fo:table-row font-weight="bold" text-align="center">
                    	<fo:table-cell background-color="#D4D0C8" height="20pt" display-align="center"
                                       border-top-style="solid" border-bottom-style="solid">
                            <fo:block>Order Date</fo:block>
                        </fo:table-cell>
                        <fo:table-cell background-color="#D4D0C8" height="20pt" display-align="center"
                                       border-top-style="solid" border-bottom-style="solid">
                            <fo:block>Order Id</fo:block>
                        </fo:table-cell>
                        <fo:table-cell background-color="#D4D0C8" height="20pt" display-align="center"
                                       border-top-style="solid" border-bottom-style="solid">
                            <fo:block>Customer</fo:block>
                        </fo:table-cell>
                        <fo:table-cell background-color="#D4D0C8" height="20pt" display-align="center"
                                       border-top-style="solid" border-bottom-style="solid">
                            <fo:block>Status</fo:block>
                        </fo:table-cell>
                        <fo:table-cell background-color="#D4D0C8" height="20pt" display-align="center"
                                       border-top-style="solid" border-bottom-style="solid">
                            <fo:block>Invoice Date</fo:block>
                        </fo:table-cell>
                        <fo:table-cell background-color="#D4D0C8" height="20pt" display-align="center"
                                       border-top-style="solid" border-bottom-style="solid">
                            <fo:block>Invoice Number</fo:block>
                        </fo:table-cell>
                        <fo:table-cell background-color="#D4D0C8" height="20pt" display-align="center"
                                       border-top-style="solid" border-bottom-style="solid">
                            <fo:block>Product</fo:block>
                        </fo:table-cell>
                        <fo:table-cell background-color="#D4D0C8" height="20pt" display-align="center"
                                       border-top-style="solid" border-bottom-style="solid">
                            <fo:block>Unit</fo:block>
                        </fo:table-cell>
                        <fo:table-cell background-color="#D4D0C8" height="20pt" display-align="center"
                                       border-top-style="solid" border-bottom-style="solid">
                            <fo:block>Invoice Quantity</fo:block>
                        </fo:table-cell>
                        <fo:table-cell background-color="#D4D0C8" height="20pt" display-align="center"
                                       border-top-style="solid" border-bottom-style="solid">
                            <fo:block>Order Quantity</fo:block>
                        </fo:table-cell>
                        <fo:table-cell background-color="#D4D0C8" height="20pt" display-align="center"
                                       border-top-style="solid" border-bottom-style="solid">
                            <fo:block>Unit Price</fo:block>
                        </fo:table-cell>
                        <fo:table-cell background-color="#D4D0C8" height="20pt" display-align="center"
                                       border-top-style="solid" border-bottom-style="solid">
                            <fo:block>Amount VAT Exclusive</fo:block>
                        </fo:table-cell>
                        <fo:table-cell background-color="#D4D0C8" height="20pt" display-align="center"
                                       border-top-style="solid" border-bottom-style="solid">
                            <fo:block>VAT Amount</fo:block>
                        </fo:table-cell>
                        <fo:table-cell background-color="#D4D0C8" height="20pt" display-align="center"
                                       border-top-style="solid" border-bottom-style="solid">
                            <fo:block>Amount VAT Inclusive</fo:block>
                        </fo:table-cell>
                    </fo:table-row>
                </fo:table-header>
                <#assign  totalAmountWithoutVAT = 0.00/>
                <#assign  totalVAT = 0.00/>
                <#assign  totalAmount = 0.00/>
                <fo:table-body>
                    <#assign rowColor = "white">
                    <#list orderItemList as orderItem>
                        <fo:table-row text-align="center" height="15pt">
                           <fo:table-cell padding="2pt" background-color="${rowColor}">
                                <#if orderItem.ORDER_DATE?has_content>
                                    <fo:block
                                            margin-top="3px">${orderItem.ORDER_DATE?if_exists?string("dd/MM/yyyy")}</fo:block>
                                <#else>
                                    <fo:block></fo:block>
                                </#if>
                            </fo:table-cell>
                            <fo:table-cell padding="2pt" background-color="${rowColor}">
                                <fo:block margin-top="3px">${orderItem.ORDER_ID?if_exists}</fo:block>
                            </fo:table-cell>
                            <fo:table-cell padding="2pt" background-color="${rowColor}">
                                <fo:block margin-top="3px">${orderItem.GROUP_NAME?if_exists}</fo:block>
                            </fo:table-cell>
                            <#assign statusItem = delegator.findOne("StatusItem",{"statusId",orderItem.STATUS_ID},false)>
                            <fo:table-cell padding="2pt" background-color="${rowColor}">
                                <fo:block margin-top="3px"> ${statusItem.description?if_exists}</fo:block>
                            </fo:table-cell>
                            <fo:table-cell padding="2pt" background-color="${rowColor}">
                                <fo:block margin-top="3px">
                                <#if orderItem.INVOICE_DATE?has_content>
                                	${orderItem.INVOICE_DATE?string("dd/MM/yyyy")}
                                </#if>
                                </fo:block>
                            </fo:table-cell>
                            <fo:table-cell padding="2pt" background-color="${rowColor}">
                                <fo:block margin-top="3px">${orderItem.INVOICE_ID?if_exists}</fo:block>
                            </fo:table-cell>
                            <fo:table-cell padding="2pt" background-color="${rowColor}">
                                <fo:block margin-top="3px">${orderItem.INTERNAL_NAME?if_exists}</fo:block>
                            </fo:table-cell>
                            <fo:table-cell padding="2pt" background-color="${rowColor}">
                                <fo:block margin-top="3px">${orderItem.DESCRIPTION?if_exists}</fo:block>
                            </fo:table-cell>
                            <fo:table-cell padding="2pt" background-color="${rowColor}">
                                <fo:block margin-top="3px">${orderItem.InvoicedQuantity?if_exists}</fo:block>
                            </fo:table-cell>
                            <fo:table-cell padding="2pt" background-color="${rowColor}">
                                <fo:block margin-top="3px">${orderItem.ORDER_QUANTITY?if_exists}</fo:block>
                            </fo:table-cell>
                            <fo:table-cell padding="2pt" background-color="${rowColor}">
                                <fo:block
                                        margin-top="3px"><@ofbizCurrency amount=orderItem.UNIT_PRICE?default(0)?if_exists /></fo:block>
                            </fo:table-cell>
                            <fo:table-cell padding="2pt" background-color="${rowColor}">
                            	<#if orderItem.PRICES_INCLUDE_TAX == "N">
                            		<#assign amountVatEx = orderItem.UNIT_PRICE * orderItem.InvoicedQuantity>
                            		 <fo:block margin-top="3px"><@ofbizCurrency amount=amountVatEx?default(0)?if_exists  /></fo:block>
                            		<#else>
                            		<#assign amountVatEx = (orderItem.UNIT_PRICE * orderItem.InvoicedQuantity) - orderItem.VAT>
                                	<fo:block margin-top="3px"><@ofbizCurrency amount=amountVatEx?default(0)?if_exists  /></fo:block>
                                </#if>
                            </fo:table-cell>
                            <fo:table-cell padding="2pt" background-color="${rowColor}">
                                <fo:block
                                        margin-top="3px"><@ofbizCurrency amount=orderItem.VAT?default(0)?if_exists  /></fo:block>
                            </fo:table-cell>
                            <fo:table-cell padding="2pt" background-color="${rowColor}">
                            	<#if orderItem.PRICES_INCLUDE_TAX == "N">
                            		<#assign amountVatIn = (orderItem.UNIT_PRICE * orderItem.InvoicedQuantity) + orderItem.VAT>
                            		 <fo:block margin-top="3px"><@ofbizCurrency amount=amountVatIn?default(0)?if_exists  /></fo:block>
                            		<#else>
                            		<#assign amountVatIn = orderItem.UNIT_PRICE * orderItem.InvoicedQuantity>
                                	 <fo:block margin-top="3px"><@ofbizCurrency amount=amountVatIn?default(0)?if_exists  /></fo:block>
                                </#if>
                            </fo:table-cell>
                        </fo:table-row>

                    <#-- toggle the row color -->
                        <#if rowColor == "white">
                            <#assign rowColor = "#CCCCCC">
                        <#else>
                            <#assign rowColor = "white">
                        </#if>
                    </#list>
                    <#-- <fo:table-row text-align="center">
                        <fo:table-cell background-color="#D4D0C8" height="20pt" display-align="center"
                                       border-top-style="solid" border-bottom-style="solid">
                            <fo:block>Total</fo:block>
                        </fo:table-cell>
                        <fo:table-cell background-color="#D4D0C8" height="20pt" display-align="center"
                                       border-top-style="solid" border-bottom-style="solid">
                            <fo:block></fo:block>
                        </fo:table-cell>
                        <fo:table-cell background-color="#D4D0C8" height="20pt" display-align="center"
                                       border-top-style="solid" border-bottom-style="solid">
                            <fo:block></fo:block>
                        </fo:table-cell>
                        <fo:table-cell background-color="#D4D0C8" height="20pt" display-align="center"
                                       border-top-style="solid" border-bottom-style="solid">
                            <fo:block></fo:block>
                        </fo:table-cell>
                        <fo:table-cell background-color="#D4D0C8" height="20pt" display-align="center"
                                       border-top-style="solid" border-bottom-style="solid">
                            <fo:block></fo:block>
                        </fo:table-cell>
                        <fo:table-cell background-color="#D4D0C8" height="20pt" display-align="center"
                                       border-top-style="solid" border-bottom-style="solid">
                            <fo:block></fo:block>
                        </fo:table-cell>
                        <fo:table-cell background-color="#D4D0C8" height="20pt" display-align="center"
                                       border-top-style="solid" border-bottom-style="solid">
                            <fo:block></fo:block>
                        </fo:table-cell>
                        <fo:table-cell background-color="#D4D0C8" height="20pt" display-align="center"
                                       border-top-style="solid" border-bottom-style="solid">
                            <fo:block></fo:block>
                        </fo:table-cell>
                        <fo:table-cell background-color="#D4D0C8" height="20pt" display-align="center"
                                       border-top-style="solid" border-bottom-style="solid">
                            <fo:block></fo:block>
                        </fo:table-cell>
                        <fo:table-cell background-color="#D4D0C8" height="20pt" display-align="center"
                                       border-top-style="solid" border-bottom-style="solid">
                            <fo:block></fo:block>
                        </fo:table-cell>
                        <fo:table-cell background-color="#D4D0C8" height="20pt" display-align="center"
                                       border-top-style="solid" border-bottom-style="solid">
                            <fo:block><@ofbizCurrency amount=totalAmountWithoutVAT?string("#.00") /> </fo:block>
                        </fo:table-cell>
                        <fo:table-cell background-color="#D4D0C8" height="20pt" display-align="center"
                                       border-top-style="solid" border-bottom-style="solid">
                            <fo:block><@ofbizCurrency amount=totalVAT?string("#.00") /> </fo:block>
                        </fo:table-cell>
                        <fo:table-cell background-color="#D4D0C8" height="20pt" display-align="center"
                                       border-top-style="solid" border-bottom-style="solid">
                            <fo:block><@ofbizCurrency amount=totalAmount?string("#.00") /> </fo:block>
                        </fo:table-cell>
                        <fo:table-cell background-color="#D4D0C8" height="20pt" display-align="center"
                                       border-top-style="solid" border-bottom-style="solid">
                            <fo:block></fo:block>
                        </fo:table-cell>
                    </fo:table-row> -->
                    
                </fo:table-body>
            </fo:table>
        </fo:block>
        <#else>
        <fo:block>No records found.</fo:block>
        </#if>

    <#else>
    <fo:block font-size="14pt">
    ${uiLabelMap.OrderViewPermissionError}
    </fo:block>
    </#if>

</#escape>
