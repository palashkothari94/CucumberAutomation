Feature: Application Login	


@SanityTest
Scenario: Home Page Default Login
Given User is on BM Landing Page
When User login into application with "jin" and "P123"
Then Home Page is populated
And Transation Log is displayed is "True"
