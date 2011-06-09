Spring Utils
============

Utility classes collected and created over the years. Some of these might be useful to others like me. 

Utilities
=========

URLBuilder - 
---------
Simple Utility for parsing urls and retrieving and adding parameters as a map.

Validation
----------
A Composite Validator to add multiple validators and use them. It also contains MappingValidators for which you can specify a class and fieldname(s) and decide if they
are required or not. Saves you writing javacode for all those required fields.

Target Swapping
---------------
Utilizing the TargetSource and AOP infrastructure of Spring this enables for dynamic switching of basically anything. This has been used in production for switching SessionFactories and/or DataSources based on some request parameter.
