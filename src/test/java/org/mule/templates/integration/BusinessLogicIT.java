/**
 * Mule Anypoint Template
 * Copyright (c) MuleSoft, Inc.
 * All rights reserved.  http://www.mulesoft.com
 */

package org.mule.templates.integration;

import static org.junit.Assert.assertEquals;
import static org.mule.templates.builders.SfdcObjectBuilder.anAccount;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mule.MessageExchangePattern;
import org.mule.api.MuleEvent;
import org.mule.api.MuleException;
import org.mule.context.notification.NotificationException;
import org.mule.processor.chain.SubflowInterceptingChainLifecycleWrapper;
import org.mule.templates.builders.SfdcObjectBuilder;
import org.mule.templates.test.utils.ListenerProbe;

import com.mulesoft.module.batch.BatchTestHelper;
import com.sforce.soap.partner.SaveResult;

/**
 * The objective of this class is to validate the correct behavior of the
 * Anypoint Template that make calls to external systems.
 * 
 */
public class BusinessLogicIT extends AbstractTemplateTestCase {

	private BatchTestHelper helper;
	private Map<String, Object> createdAccountInSalesforce = new HashMap<>();
	private String customerExternalId;
	
	private SubflowInterceptingChainLifecycleWrapper createAccountInSalesforceFlow;
	private SubflowInterceptingChainLifecycleWrapper queryAccountFromSalesforceFlow;
	private SubflowInterceptingChainLifecycleWrapper deleteAccountFromSalesforceFlow;
	private SubflowInterceptingChainLifecycleWrapper queryCustomerFromNetsuiteFlow;
	private SubflowInterceptingChainLifecycleWrapper deleteCustomerFromNetsuiteFlow;
	
	@Before
	public void setUp() throws Exception {
		stopFlowSchedulers(POLL_FLOW_NAME);
		registerListeners();
		
		System.setProperty("watermark.default.expression","#[groovy: new Date(System.currentTimeMillis() - 10000).format(\"yyyy-MM-dd'T'HH:mm:ss.SSS'Z'\", TimeZone.getTimeZone('UTC'))]");
		System.setProperty("polling.frequency","15000");
		System.setProperty("polling.startDelay","1000");
		helper = new BatchTestHelper(muleContext);

		createAccountInSalesforceFlow = getSubFlow("createAccountInSalesforceFlow");
		createAccountInSalesforceFlow.initialise();		
		
		queryAccountFromSalesforceFlow = getSubFlow("queryAccountFromSalesforceFlow");
		queryAccountFromSalesforceFlow.initialise();
		
		deleteAccountFromSalesforceFlow = getSubFlow("deleteAccountFromSalesforceFlow");
		deleteAccountFromSalesforceFlow.initialise();
		
		queryCustomerFromNetsuiteFlow = getSubFlow("queryCustomerFromNetsuiteFlow");
		queryCustomerFromNetsuiteFlow.initialise();
		
		deleteCustomerFromNetsuiteFlow = getSubFlow("deleteCustomerFromNetsuiteFlow");
		deleteCustomerFromNetsuiteFlow.initialise();
		
		createTestAccount();
	}

	@After
	public void tearDown() throws Exception {
		stopFlowSchedulers(POLL_FLOW_NAME);
		deleteTestsData();
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testMainFlow() throws Exception {
		// Run poll and wait for it to run
		runSchedulersOnce(POLL_FLOW_NAME);
		waitForPollToRun();

		// Wait for the batch job executed by the poll flow to finish
		helper.awaitJobTermination(TIMEOUT_SEC * 1000, 500);
		helper.assertJobWasSuccessful();

		
		// Assert object was sync to target system 
		MuleEvent event = queryCustomerFromNetsuiteFlow.process(getTestEvent(createdAccountInSalesforce, MessageExchangePattern.REQUEST_RESPONSE));
		Map<String,Object> resultPayload = (Map<String, Object>)event.getMessage().getPayload();
		
		assertEquals("The account name should have been sync", createdAccountInSalesforce.get("Name"), resultPayload.get("companyName"));
		customerExternalId = resultPayload.get("externalId").toString();
		assertEquals("The account id should have been sync", createdAccountInSalesforce.get("Id"), customerExternalId);
		assertEquals("The account phone should have been sync", createdAccountInSalesforce.get("Phone"), resultPayload.get("phone").toString().replaceAll("\\D", ""));
		assertEquals("The account fax should have been sync", createdAccountInSalesforce.get("Fax"), resultPayload.get("fax"));
	}

	private void registerListeners() throws NotificationException {
		muleContext.registerListener(pipelineListener);
	}

	private void waitForPollToRun() {
		pollProber.check(new ListenerProbe(pipelineListener));
	}

	@SuppressWarnings("unchecked")
	private void createTestAccount() throws MuleException, Exception {

		SfdcObjectBuilder updateAccount = anAccount().with("Name", buildUniqueName(TEMPLATE_NAME))
													 .with("Phone", "5554448")
													 .with("Fax", "12345678");
		
		createdAccountInSalesforce = updateAccount.build();
		List<Map<String,Object>> listOfAccounts = new ArrayList<>();
		listOfAccounts.add(createdAccountInSalesforce);

		final MuleEvent event = createAccountInSalesforceFlow.process(getTestEvent(listOfAccounts, MessageExchangePattern.REQUEST_RESPONSE));
		final List<SaveResult> results = (List<SaveResult>) event.getMessage().getPayload();
		for (SaveResult result : results) {
			createdAccountInSalesforce.put("Id", result.getId());
		}
	}

	private void deleteTestsData() throws MuleException, Exception {
		// Delete the created Account in Salesforce		
		final List<Object> idList = new ArrayList<Object>();
		idList.add(createdAccountInSalesforce.get("Id"));		
		deleteAccountFromSalesforceFlow.process(getTestEvent(idList, MessageExchangePattern.REQUEST_RESPONSE));

		// Delete the created Customer in Netsuite
		deleteCustomerFromNetsuiteFlow.process(getTestEvent(createdAccountInSalesforce, MessageExchangePattern.REQUEST_RESPONSE));
	}

}
