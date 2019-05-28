
# Anypoint Template: Salesforce to Netsuite Account to Customer Broadcast

<!-- Header (start) -->
Broadcasts new customer accountsor updates to existing accounts in Salesforce to NetSuite in real time. This template can be used to ensure that the customer account information in NetSuite remains updated to the changes in Salesforce.

The detection criteria and fields to integrate are configurable. Additional systems can be added to be notified of the changes. Real time synchronization is achieved either via rapid polling of Salesforce or Outbound Notifications to reduce the number of API calls.

This template uses batch processing and watermarking capabilities within the Anypoint Platform to efficiently process many records.

![b2b4af6b-288a-4288-b34a-5c099f567a16-image.png](https://exchange2-file-upload-service-kprod.s3.us-east-1.amazonaws.com:443/b2b4af6b-288a-4288-b34a-5c099f567a16-image.png)
<!-- Header (end) -->

# License Agreement
This template is subject to the conditions of the <a href="https://s3.amazonaws.com/templates-examples/AnypointTemplateLicense.pdf">MuleSoft License Agreement</a>. Review the terms of the license before downloading and using this template. You can use this template for free with the Mule Enterprise Edition, CloudHub, or as a trial in Anypoint Studio.
# Use Case
<!-- Use Case (start) -->
This template performs an online sync of accounts from a Salesforce instance to customers in a NetSuite instance. Each time an account is added or an existing account changed, the integration polls for changes in the Salesforce source instance and creates or updates the customer in the NetSuite target instance.

Requirements have been set not only to be used as examples, but also to establish a starting point to adapt your integration to your requirements.

This template leverage the Mule batch module.
The batch job is divided in Input, Process, and On Complete stages.
A scheduler in the flow triggers the application, queries Salesforce for updates or creates that match a filter, and executes the batch job. Data is then adapted to create or update the customer in NetSuite and call the upsert operation in the NetSuite system. Finally, during the On Complete stage the template logs output statistics data to the console.
<!-- Use Case (end) -->

# Considerations
<!-- Default Considerations (start) -->

<!-- Default Considerations (end) -->

<!-- Considerations (start) -->
To make this template run, there are certain preconditions that must be considered. All of them deal with the preparations in both source and destination systems, that must be made for the template to run smoothly. Failing to do so can lead to unexpected behavior of the template.
<!-- Considerations (end) -->



## Salesforce Considerations

To get this template to work:

- Where can I check that the field configuration for my Salesforce instance is the right one? See: <a href="https://help.salesforce.com/HTViewHelpDoc?id=checking_field_accessibility_for_a_particular_field.htm&language=en_US">Salesforce: Checking Field Accessibility for a Particular Field</a>.
- Can I modify the Field Access Settings? How? See: <a href="https://help.salesforce.com/HTViewHelpDoc?id=modifying_field_access_settings.htm&language=en_US">Salesforce: Modifying Field Access Settings</a>.

### As a Data Source

If the user who configured the template for the source system does not have at least *read only* permissions for the fields that are fetched, then an *InvalidFieldFault* API fault displays.

```
java.lang.RuntimeException: [InvalidFieldFault [ApiQueryFault
[ApiFault  exceptionCode='INVALID_FIELD'
exceptionMessage='Account.Phone, Account.Rating, Account.RecordTypeId,
Account.ShippingCity
^
ERROR at Row:1:Column:486
No such column 'RecordTypeId' on entity 'Account'. If you are
attempting to use a custom field, be sure to append the '__c'
after the custom field name. Reference your WSDL or the describe
call for the appropriate names.'
]
row='1'
column='486'
]
]
```

## NetSuite Considerations


### As a Data Destination

Customer must be assigned to a subsidiary. In this template, this is done statically and you must configure the property file with subsidiary *internalId* that is already in the system. You can find this number by entering `subsidiaries`
in the NetSuite search field and selecting 'Page - Subsidiaries'. When you click **View** next to a subsidiary, you see the ID in the URL line. Use this ID to populate the *nets.subsidiaryId* property in the property file.

# Run it!
Simple steps to get this template running.
<!-- Run it (start) -->
See below.
<!-- Run it (end) -->

## Running On Premises
In this section we detail the way you have to run you Anypoint Temple on you computer.

After starting your app, there is no need to do anything else. The application polls accounts in Salesforce for newly created or updated objects, and synchronizes them.
<!-- Running on premise (start) -->

<!-- Running on premise (end) -->

### Where to Download Anypoint Studio and the Mule Runtime
If you are new to Mule, download this software:

+ [Download Anypoint Studio](https://www.mulesoft.com/platform/studio)
+ [Download Mule runtime](https://www.mulesoft.com/lp/dl/mule-esb-enterprise)

**Note:** Anypoint Studio requires JDK 8.
<!-- Where to download (start) -->

<!-- Where to download (end) -->

### Importing a Template into Studio
In Studio, click the Exchange X icon in the upper left of the taskbar, log in with your Anypoint Platform credentials, search for the template, and click Open.
<!-- Importing into Studio (start) -->

<!-- Importing into Studio (end) -->

### Running on Studio
After you import your template into Anypoint Studio, follow these steps to run it:

1. Locate the properties file `mule.dev.properties`, in src/main/resources.
2. Complete all the properties required as per the examples in the "Properties to Configure" section.
3. Right click the template project folder.
4. Hover your mouse over `Run as`.
5. Click `Mule Application (configure)`.
6. Inside the dialog, select Environment and set the variable `mule.env` to the value `dev`.
7. Click `Run`.
<!-- Running on Studio (start) -->

<!-- Running on Studio (end) -->

### Running on Mule Standalone
Update the properties in one of the property files, for example in mule.prod.properties, and run your app with a corresponding environment variable. In this example, use `mule.env=prod`.

## Running on CloudHub
While creating your application on CloudHub (or you can do it later as a next step), you need to go to Deployment > Advanced to set all environment variables detailed in **Properties to Configure** as well as setting the **mule.env** file.

After starting your app, there is no need to do anything else. The application polls accounts in Salesforce for newly created or updated objects, and synchronizes to NetSuite as long as it has an Email.

<!-- Running on Cloudhub (start) -->

<!-- Running on Cloudhub (end) -->

### Deploying a Template in CloudHub
In Studio, right click your project name in Package Explorer and select Anypoint Platform > Deploy on CloudHub.
<!-- Deploying on Cloudhub (start) -->

<!-- Deploying on Cloudhub (end) -->

## Properties to Configure
To use this template, configure properties such as credentials, configurations, etc.) in the properties file or in CloudHub from Runtime Manager > Manage Application > Properties. The sections that follow list example values.
### Application Configuration
<!-- Application Configuration (start) -->
**Application Configuration**

+ scheduler.frequency `20000`
+ scheduler.start.delay `1000`
+ watermark.default.expression `2017-12-31T00:00:00.00Z`

**Salesforce Connector Configuration**

+ sfdc.username `bob.dylan@orga`
+ sfdc.password `DylanPassword123`
+ sfdc.securityToken `avsfwCUl7apQs56Xq2AKi3X`

**Netsuite Connector Configuration**

+ nets.email `example@organization.com`
+ nets.password `Passowrd123`
+ nets.account `NetsuiteAccount`
+ nets.roleId `3`
+ nets.applicationId `77EBCBD6-AF9F-11E5-BF7F-FEFF819CDC9F`
+ nets.customer.subsidiary.internalId `1`
+ nets.connectionTimeout `30000`
+ nets.readTimeout `30000`

**Note**: The property `nets.customer.subsidiary.internalId` set **subsidiary** for every new customer in the NetSuite instance.
<!-- Application Configuration (end) -->

# API Calls
<!-- API Calls (start) -->
Salesforce imposes limits on the number of API Calls that can be made. However, in this template, only one call per scheduler cycle is done to retrieve all the information required.
<!-- API Calls (end) -->

# Customize It!
This brief guide provides a high level understanding of how this template is built and how you can change it according to your needs. As Mule applications are based on XML files, this page describes the XML files used with this template. More files are available such as test classes and Mule application files, but to keep it simple, we focus on these XML files:

* config.xml
* businessLogic.xml
* endpoints.xml
* errorHandling.xml<!-- Customize it (start) -->

<!-- Customize it (end) -->

## config.xml
<!-- Default Config XML (start) -->
This file provides the configuration for connectors and configuration properties. Only change this file to make core changes to the connector processing logic. Otherwise, all parameters that can be modified should instead be in a properties file, which is the recommended place to make changes.
<!-- Default Config XML (end) -->

<!-- Config XML (start) -->

<!-- Config XML (end) -->

## businessLogic.xml
<!-- Default Business Logic XML (start) -->
The business logic XML file creates or updates objects in the destination system for a represented use case. You can customize and extend the logic of this template in this XML file to more meet your needs.
<!-- Default Business Logic XML (end) -->

<!-- Business Logic XML (start) -->

<!-- Business Logic XML (end) -->

## endpoints.xml
<!-- Default Endpoints XML (start) -->
This file contains the endpoints for triggering the template and for retrieving the objects that meet the defined criteria in a query. You can execute a batch job process with the query results.
<!-- Default Endpoints XML (end) -->

<!-- Endpoints XML (start) -->

<!-- Endpoints XML (end) -->

## errorHandling.xml
<!-- Default Error Handling XML (start) -->
This file handles how your integration reacts depending on the different exceptions. This file provides error handling that is referenced by the main flow in the business logic.
<!-- Default Error Handling XML (end) -->

<!-- Error Handling XML (start) -->

<!-- Error Handling XML (end) -->

<!-- Extras (start) -->

<!-- Extras (end) -->
