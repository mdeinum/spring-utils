Spring Utils
============

Utility classes collected and created over the years. Some of these might be useful to others.


Modules
=========

Validation
----------
A Composite Validator to add multiple validators and use them. It also contains MappingValidators for which you can specify a class and fieldname(s) and decide if they
are required or not. Saves you writing javacode for all those required fields.

Multi Tenant
---------------
Utilizing the TargetSource and AOP infrastructure of Spring this enables for dynamic switching of basically anything. This has been used in production for switching SessionFactories and/or DataSources based on some request parameter.
As of Hibernate 4.1 multi-tenant support is build into hibernate, this multi-tenant support also has been integrated into this. Finally we added also support for Spring Integration, there is an interceptor which can be added to your channels to set/get the context from a message header.


Batch
-----
Contains a LoggingItemWriter which can be useful to debug problems or for testing purposes. It also contains a SynchronizedItemReader which can be useful if concurrency for reading is needed.

Batch - Excel Item Reader
--------------------------------
Spring Batch ItemReaders for reading MS Excel files. Currently has basic implementations for the JExcelAPI and Apache POI. It was build like the FlatFileItemReader from Spring Batch so it is fully configurable for your liking.

Web Services
------------
Currently only a FireAndForgetWebServiceMessageSender and connection. Current version of Spring-WS doesn't support (out-of-the-box) fire-and-forget webservice calls. This adapter will make a WebServiceMessageSender send only.