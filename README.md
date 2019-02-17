# UniBuild

## Introduction

UniBuild is a cross-platform, customizable build, deployment and task executor tool. It is task-oriented, which means users can compile and customize their own set of tasks easily, execute them and monitor the progress of execution, both real-time and afterwards.

### What does UniBuild do?

* It provides a platform to execute a collection of pre-defined and user-defined tasks (goals), configured by the user, in a well-monitored, fully logged manner.
* It provides a CLI (command line interface), a web interface (for servers) and a client with a GUI to execute and monitor goals
* It provides a way to execute complex build and deployment tasks by one click
* It provides a templating mechanism so that you don't need to duplicate task configurations
* It provides security for your set of goals (projects)

### What does UniBuild NOT do?

* It does not replace your build tool (such as Maven, Gradle, Make, MSBuild or Composer), it actually helps to join them into goals very easy to execute
* It is currently not focused on CI (continuous integration), actually integration with the most popular CI tools (Jenkins, Travis etc.) is in progress.

### When should I be interested in using UniBuild?

* If you have complex, multi-platform build and deployment processes that require several user interactions on different platforms and tired of it, and want it to be fully automated,you should definitely give UniBuild a try. 
Sure there is a great build tool for every platform, but chaining them into one is sometimes challenging. 
* If you tried to do fully automated cross-platform builds, and failed (for instance, build a Linux server and Windows/OSX clients on a Linux build server)
* If you can build your individual software components but combining and deploying them takes too much time
* If your build tool does not support creating setup packages but you want to automate creating them
* If you are just a techy person, who wants to combine and automate scripts in an easy way




