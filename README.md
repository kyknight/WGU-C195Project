# WGU-C195Project
WGU Software 2 Project (C195)

COMPETENCIES
4025.01.05 Database and File Server Applications 

The graduate produces database and file server applications using advanced Java programming language constructs to meet business requirements.

4025.01.06 Lambda

The graduate incorporates lambda expressions in application development to meet business requirements more efficiently.

4025.01.07 Collections (Streams and Filters)

The graduate incorporates streams and filters in application development to manipulate data more efficiently.

4025.01.08 Localization API & Date/Time API

The graduate applies the localization API and date/time API in application development to support end-users in various geographical regions.

4025.01.09 Advanced Exception Control

The graduate incorporates advanced exception control mechanisms in application development for improving user experience and application stability.

INTRODUCTION
Throughout your career in software design and development, you will be asked to create applications with various features and criteria based on a variety of business requirements. For this assessment, you will create your own Java application with requirements that mirror those you will encounter in a real-world job assignment.

The skills you will showcase in this assessment are also directly relevant to technical interview questions for future employment. This application should become a portfolio piece for you to show to future employers.

Several attachments and links have been included to help you complete this task. Refer to the “MySQL Virtual Access Instructions” attachment for help accessing the database for your application. Note that this database is for functional purposes only and does not include any pre-existing data. The attached “Database ERD” shows the entity relationship diagram (ERD) for this database, which you can reference as you create your application.

The preferred integrated development environment (IDE) for this assignment is NetBeans. Use the web link “NetBeans Installation Instructions” to install this application. If you choose to use another IDE, you must export your project into NetBeans format for submission.

When you have completed this task, you must submit a zip file with all the necessary code files to compile, support, and run your application.

Note: The zip file submission must also keep the project file and folder structure intact for the NetBeans IDE

SCENARIO
You are working for a software company that has been contracted to develop a scheduling desktop user interface application. The contract is with a global consulting organization that conducts business in multiple languages and has main offices in Phoenix, Arizona; New York, New York; and London, England. The consulting organization has provided a MySQL database that your application must pull data from. The database is used for other systems and therefore its structure cannot be modified.

The organization outlined specific business requirements that must be included as part of the application. From these requirements, a system analyst at your company created solution statements for you to implement in developing the application. These statements are listed in the requirements section.

REQUIREMENTS
Your submission must be your original work. No more than a combined total of 30% of the submission and no more than a 10% match to any one individual source can be directly quoted or closely paraphrased from sources, even if cited correctly. An originality report is provided when you submit your task that can be used as a guide.

You must use the rubric to direct the creation of your submission because it provides detailed criteria that will be used to evaluate your work. Each requirement below may be evaluated by more than one rubric aspect. The rubric aspect titles may contain hyperlinks to relevant portions of the course.

You are not allowed to use frameworks or external libraries. The database does not contain data, so it needs to be populated. You must use “test” as the username and password to log-in.

A.   Create a log-in form that can determine the user’s location and translate log-in and error control messages (e.g., “The username and password did not match.”) into two languages.
B.   Provide the ability to add, update, and delete customer records in the database, including name, address, and phone number.
C.   Provide the ability to add, update, and delete appointments, capturing the type of appointment and a link to the specific customer record in the database.
D.   Provide the ability to view the calendar by month and by week.
E.    Provide the ability to automatically adjust appointment times based on user time zones and daylight saving time.
F.   Write exception controls to prevent each of the following. You may use the same mechanism of exception control more than once, but you must incorporate at least  two different mechanisms of exception control.
•   scheduling an appointment outside business hours
•   scheduling overlapping appointments
•   entering nonexistent or invalid customer data
•   entering an incorrect username and password
G.  Write two or more lambda expressions to make your program more efficient, justifying the use of each lambda expression with an in-line comment.
H.   Write code to provide an alert if there is an appointment within 15 minutes of the user’s log-in.
I.   Provide the ability to generate each  of the following reports:
•   number of appointment types by month
•   the schedule for each consultant
•   one additional report of your choice
J.   Provide the ability to track user activity by recording timestamps for user log-ins in a .txt file. Each new record should be appended to the log file, if the file already exists.

K. Demonstrate professional communication in the content and presentation of your submission.
