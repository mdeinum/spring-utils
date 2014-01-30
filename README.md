Spring Utils
============

Utility classes collected and created over the years. Some of these might be useful to others.


Modules
=========

Validation
----------
A Composite `Validator` to add multiple validators and use them. It also contains `MappingValidator`s for which you can specify a class and fieldname(s) and decide if they
are required or not. Saves you writing javacode for all those required fields.

Multi Tenant
---------------
Utilizing the `TargetSource` and AOP infrastructure of Spring this enables for dynamic switching of basically anything. This has been used in production for switching `SessionFactory`'s and/or `DataSource`s based on some request parameter.
As of [Hibernate](http://www.hibernate.org) 4.1 multi-tenant support is build into hibernate, this multi-tenant support also has been integrated into this. Finally we added also support for [Spring Integration](http://projects.spring.io/spring-integration/), there is a `ChannelInterceptor` which can be added to your channels to set/get the context from a message header.

Batch
-----
Contains a `LoggingItemWriter` which can be useful to debug problems or for testing purposes. It also contains a `SynchronizedItemReader` which can be useful if concurrency for reading is needed.

Batch - Excel Item Reader
--------------------------------
Spring Batch `ItemReader`s for reading MS Excel files. Currently has basic implementations for the [JExcelAPI](http://jexcelapi.sourceforge.net/) and [Apache POI](http://poi.apache.org/spreadsheet/index.html). It was build like the `FlatFileItemReader` from [Spring Batch](http://projects.spring.io/spring-batch/) so it is fully configurable for your liking.

Web
---
`FilterInitDestroyBeanPostProcessor` ties `javax.servlet.Filter`s in the application context to the lifecycle of the container. It also enabled the filters to be configured inside the application context, which is especially nice with legacy filters (i.e. filters which rely on retrieving init-params inside the init method for configuration).

Some servlet listeners for debugging things that happen to the `ServletContext` and `HttpSession`, can be registered manually or in a servlet 3.0 environment automatically.

Web Services
------------
Currently only a `FireAndForgetWebServiceMessageSender` and connection. Current version of [Spring Web Services](http://projects.spring.io/spring-ws) doesn't support (out-of-the-box) fire-and-forget webservice calls. This adapter will make a `WebServiceMessageSender` send only.
