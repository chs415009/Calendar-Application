# Calendar-Application
Description: An application that help users to manage their to-do list with a calendar in different layout.

## Download:
1. Click below and download the zip file.<br>
https://github.com/chs415009/Calendar-Application.git <br>

2. Add the gson file to the libraries of the project.<br>
   The gson file is already in the package. Just make sure the path is correct.<br><br>
   <img src="./bin/Libraries.png" width="500">

## Instructions:
### 1. __Sign in & Sign up__ <br>
<img src="./bin/login.png" width="400"><img src="./bin/register.png" width="400">

* If you are a new user, go to the register page first. We design two kinds of users: normal and VIP.
* Register the VIP user will have some advanced features. 

<br><br>

### 2. __Introduction to Layout__ <br>
<img src="./bin/inbox.png" width="400"><img src="./bin/today.png" width="400">
<img src="./bin/weekly.png" width="400"><img src="./bin/monthly.png" width="400">

* These are the different layout for the application:<br>
   *  __Inbox__: All the tasks of this account.<br>
   *  __Today__: Showing tasks for today.<br>
   *  __Weekly__: Showing tasks for this week.<br>
   *  __Monthly__: Showing tasks for this month.<br>
* The application has implemented real calendar, so we can see different weeks or months through the "previos" and "next" buttons.

<br><br>

### 3. __Features of Normal Users__ <br>
<img src="./bin/add.png" width="300"><img src="./bin/edit.png" width="300"><img src="./bin/filter.png" width="300">

* These two images shows the "add", "edit", "delete" and "filter" feature.
   * Normal user can only create one task at a time. No empty input is allowed.
   * Edit button is only allowed after selecting one the task. And once you select the task, the detail will show at the bottom of the page.
   * As to the "delete" button, once clicked, the selected task will be deleted from this account.
   * Filter can only be accessed in the inbox page, it helps the user to go though tasks catogorized by tags.

<br><br>

### 4. __Features of VIP Users__ <br>
<img src="./bin/VIP add.png" width="400"><img src="./bin/VIP delete.png" width="400">

* VIP users have all the features that normal users have and two advanced feature.
   * When adding tasks, VIP user can add recurring tasks through input the frequency and quantity. <br>
   For example, "frequency: weekly + quantity: 3" means repeat this task 3 times every week since the selected date.
   * When deleting tasks, the user can decide to delete only this task, or all the other tasks that have the same name and tag.
 
<br><br>

### 5. __Logout & Save__ <br>
* When the user clicks the **Logout** button at the bottom-left corner of the page, the system automatically navigates back to the login page.  
* All changes made by users during the session are saved when the page is closed.  
* Data is stored in a **JSON** file, which the system reads when the project is launched again.  
