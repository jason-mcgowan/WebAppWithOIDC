Web Application With OIDC Login and Persistent Data
===========================================

### Background
This project was done as part of the Fall 2021 CS601 Principles of Software Development course at the University of San Francisco.

### Project Goals
Implement the following:
- Web server using only Java base packages
  - Session tracking for distinct clients
  - HTTP request filtering based on session login, respond with redirect to login prompt
  - HTTP response filtering to manage cookies for each session
- Web application providing
  - Event service to allow users to create events, view events, search events, purchase tickets, and transfer tickets to other users
  - OIDC (OAuth 2.0) login with Slack
  - Creates new users and tracks existing user information
  - Allow users to modify their information
  
  - Interacts with backing database
- Database for persisting information

### Learning Journey Results
- Extensive use of JDBC (Java database connectivity) to include prepared statements, transactions, rollback, and more
- Extended my experience with concurrency and asynchronous operations to include database calls
- Gained expertise implementing an OIDC OAuth 2.0 flow from scratch using base packages
