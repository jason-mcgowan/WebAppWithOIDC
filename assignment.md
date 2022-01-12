Project 4 - Ticket Purchase Web Application
===========================================

#### Final Code and Deployment Due - December 12, 2021 - Your solution must be complete by 12/12/21. Instructions for scheduling your final interactive grading session during finals week will be posted on Slack.

#### Checkpoint Due - December 7, 2021 - All students will be required to demonstrate their progress on or before the last day of class - December 7, 2021. Students who fail to demonstrate reasonable progress before this checkpoint will not receive credit for the Project Checkpoint criterion.

For this project you will implement a ticket purchase web application (i.e., your own EventBrite!). You will design and implement a two-tier web application with a Java (Jetty/Servlets) front end and an SQL backend. 

### Required Features

You *must* complete all of the following required features for a total of 55 points.


| Points   | Feature         | Description |
| :-------: |:-------------:| :-----|
| 5 | Login with ... and logout | Authenticate users to use your site through Slack or another third party (e.g., Google, github). Provide a signout option and maintain user state appropriately.|
| 5 | User account | Allow a user to view and update personal user account information (e.g., name). | 
| 5 | View user transactions | Display *details* for all events for which the user has purchased tickets. |
| 5 | View events | Display a list of all events. |
| 5 | View event | Display details for a specific event. |
| 5 | Create event | Allow the user to create a new event by entering all appropriate detail. |
| 5 | Purchase tickets | Allow the user to purchase tickets for an event. |
| 5 | Transfer tickets | Allow the user to transfer tickets to another user. |
| 5 | SQL DB | Your solution must use your [assigned SQL database](https://docs.google.com/spreadsheets/d/1zEoGR9wBEJD7EKQQcjm8OMBD-APjPo-Ya_fRKD70OVU/edit?usp=sharing). |
| 5 | Table - Users | Use a relational database to store *user account* data. |
| 5 | Table - Events | Use a relational database to store *event* data. |

You may use additional database tables.

### Additional Features

Select up to 20 points of additional features. *No* extra credit will be awarded for implementing additional features.

| Points   | Feature |  Description |
| :-------: |:-------------:|  :-----|
| 5 | Show *n* events per page | Provide pagination to allow a user to see some specific number of events per page and scroll to the next page. |
| 5 | Discount/VIP Tickets | Provide the ability to specify some tickets as discounted (e.g., students) or more expensive (e.g., VIP). |
| 5 | Modify/delete event | Allow a user to modify or delete an event *that s/he has created*.|
| 10 | Images | Integrate images into your site, allow a user to upload images when creating an event and display when the event is viewed. |
| 10 | Web API Integration |  Provide a feature that integrates another web API. For example, provide an option for a user to post events on Slack through their account (not just an anonymous bot!) or tweet out upcoming events. |
| 10 | Templates |  Use [Thymeleaf](https://www.thymeleaf.org/) or another template engine to generate your HTML. |
| 10 | Search | Allow a user to search events for particular phrases or other features. |
| 10 | Hosted | Run on Amazon Web Services or another hosting site. |
| 5 | Branding |  Brand your site with a logo, color scheme, etc. |

You may propose additional features via Slack or by coming to office hour. The instructor will tell you how many points you may earn by completing the features you propose.


### Other Criteria

In addition, your project will be evaluated based on the following criteria for a total of 25 points.

| Points   | Criteria |
| :-------: |:-------------:| 
| 5 | Project Checkpoint |  
| 15 | Code Quality |  
| 5 | Code Style |  

### Requirements

1. You will use Servlets/Jetty as your web framework.
2. You will use your [assigned SQL database](https://docs.google.com/spreadsheets/d/1zEoGR9wBEJD7EKQQcjm8OMBD-APjPo-Ya_fRKD70OVU/edit?usp=sharing) to store all data including user information, event information, and ticket transaction/purchase information. You are required to design the database tables. 
3. By the deadline you must have all code committed to your Github repository. All features must be demonstrated during your interactive grading session.
4. This is an individual assignment. Teams will not be permitted for this assignment.

### Submission Requirements

You are required to schedule an interactive grading appointment during finals week. Details will be provided via Slack. Failure to meet with the instructor for interactive grading will result in a grade of 0.

Use the following link to create your private github repository for this assignment: [Project 4](https://classroom.github.com/a/BLqK3RvW)

For full credit, make sure to follow all [Style Guidelines](https://github.com/CS601-F21/notes/blob/main/admin/style.md). Points will be deducted for each violation.


### Academic Dishonesty

Any work you submit is expected to be your own original work. If you use any web resources in developing your code you are strongly advised to cite those resources. The only exception to this rule is code that is posted on the class website. The URL of the resource you used in a comment in your code is fine. If I google even a single line of uncited code and find it on the internet you may get a 0 on the assignment or an F in the class. You may also get a 0 on the assignment or an F in the class if your solution is at all similar to that of any other student.
