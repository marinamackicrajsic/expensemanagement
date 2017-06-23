Application is built with maven.
I used tomcat9 to run the application. 

Database:
schema : expensemanagement

CREATE TABLE `expense` (
  `id` bigint(10) NOT NULL AUTO_INCREMENT,
  `amount` decimal(10,4) NOT NULL,
  `reason` varchar(45) NOT NULL,
  `date` date NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id_UNIQUE` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8;

I used plain jdbc to establish connection with the database, for simplicity. 
Query for expenses is limited to 5000 rows so it doesn't break. Otherwise I would probably use some ORM framework, and I would implement some paging logic(server or client side).

When calculating VAT I used one of the built-in rounding rules. This should otherwise be addressed in detail as certain countries have very exotic rounding rules when it comes to currency.

I implemented rest client for fetching currency rates. This would probably be a part of other module.

I used basic logger, for simplicity. Otherwise I would use log4j.

I think service described in the user story is very basic. At least some logging info should be kept for each entry - timestamp of entry and a user who entered it. Also I think that a user/person for whom the expense is entered should have also been part of the model.
I'm not happy with the overall exception handling, and the input check in the rest service. I would pay more attention to this, but I didn't want to spend more than couple of hours on the task.
I didn't get to write any tests, but I would use testng and mockito for unit tests.
I would also use some dependency injection framework - guice or spring.
Endpoint is expensemanagement/expenses. I was not able to start the front-end application - I had an issue with bootstrapping of alcExpenses, so I didn't change the apiroot from app to expensemanagement.

