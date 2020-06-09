Feature: Application Login	

//Background:
//Given validate the browser
//When Browser is triggered
//Then check if browser is displayed


@MobileTest
Scenario: Home Page Default Login
Given User is on BM Landing Page
When User login into application with "jin" and "P123"
Then Home Page is populated
And Transation Log is displayed is "True"

@MobileTest
Scenario: Home Page Default Login
Given User is on BM Landing Page
When User login into application with "john" and "1234"
Then Home Page is populated
And Transation Log is displayed is "False"

@RegTest
Scenario: Home Page Default Login
Given User is on BM Landing Page
When signup with following details
| jenny | abcd | jenny@abcd.com | Australia | 327001|
Then Home Page is populated
And Transation Log is displayed is "False"

@MobileTest
Scenario Outline: Home Page Default Login
Given User is on BM Landing Page
When User login in to application with <Username> and <Password>
Then Home Page is populated
And Transation Log is displayed is "True"

Examples:
|Username |Password|
|User1 |Pass1|
|User2 |Pass2|