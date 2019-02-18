# UniBuild

## Introduction

UniBuild is an open-source, cross-platform, customizable build, deployment and task executor tool. It is task-oriented, which means users can compile and customize their own set of tasks easily, execute them and monitor the progress of execution, both real-time and afterwards.

### What does UniBuild do?

* It provides a platform to execute a collection of pre-defined and user-defined tasks (goals), configured by the user, in a well-monitored, fully logged manner.
* It provides a CLI (command line interface), a web interface (for servers) and a client with a GUI to execute and monitor goals
* It provides a way to execute complex build and deployment tasks by one click
* It provides a templating mechanism so that you don't need to duplicate task configurations
* It provides security for your set of goals (projects)
* It provides detailed logging with error details and execution history
* It provides integration with source control servers (such as SVN or Git)

### What does UniBuild NOT do?

* It does not replace your build tool (such as Maven, Gradle, Make, MSBuild or Composer), it actually helps to join them into goals very easy to execute
* It is currently not focused on CI (continuous integration), actually integration with the most popular CI tools (Jenkins, Travis etc.) is planned.

### When should I be interested in using UniBuild?

* If you have complex, multi-platform build and deployment processes that require several user interactions on different platforms and tired of it, and want it to be fully automated,you should definitely give UniBuild a try. 
Sure there is a great build tool for every platform, but chaining them into one is sometimes challenging. 
* If you tried to do fully automated cross-platform builds, and failed (for instance, build a Linux server and Windows/OSX clients on a Linux build server)
* If you can build your individual software components but combining and deploying them takes too much time
* If your build tool does not support creating setup packages but you want to automate creating them
* If you are just a techy person, who wants to combine and automate scripts in an easy way

### Technical information

* UniBuild uses Java. Typically a suitable JDK is bundled in the setup packages but you can use your own JDK (at least JDK 8 is required)
* UniBuild uses the Spring Framework. If you want to develop plugins for UniBuild, you should create Spring components.
* UniBuild uses Hibernate/JPA2 as database abstraction layer. On a server machine, UniBuild supports MySQL, MS-SQL, Oracle and PosgreSQL. On a client machine, UniBuild uses an SQLite database by default, but any of the supported database servers can be used.
* UniBuild supports the following platforms: Windows (server and client), Linux (server), OSX (client)


## Build from source

### Linux (Debian / Ubuntu)

1. Download and Install Oracle JDK 8 (if you don't have it)
	```
	sudo apt-get install software-properties-common
	sudo apt-get install dirmngr
	sudo apt-key adv --keyserver keyserver.ubuntu.com --recv-keys C2518248EEA14886
	sudo apt-get update
	sudo apt-get install oracle-java8-installer
	```
	
2. Install Maven
	```
	sudo apt-get install maven
	```
	
3. Create a build folder, for example:
	```
	mkdir /opt/unibuild
	```

4. Install Git
	```
	sudo apt-get install git
	```
	
5. Clone from GitHub into your build folder, for example:
	```
	cd /opt/unibuild
	git clone https://github.com/unibuild/unibuild /opt/unibuild
	```

6. Install fakeroot
	```
	sudo apt-get install fakeroot
	```
	
7. To build a server, execute the build-server.sh script from your build folder, for example:
	```
	chmod 700 /opt/unibuild/build/scripts/linux/build-server.sh
	/opt/unibuild/build/scripts/linux/build-server.sh
	```
	
	You will find the resulting .deb package in the dist folder created under your build folder, for example:
	```
	/opt/unibuild/dist/unibuild-${version}.deb
	```
	
	
8. To build a CLI-only version, execute the build.sh script from your build folder, for example:
	```
	chmod 700 /opt/unibuild/build/scripts/linux/build.sh
	/opt/unibuild/build/scripts/linux/build.sh
	```
	
	You will find the resulting .deb package in the dist folder created under your build folder, for example:
	```
	/opt/unibuild/dist/unibuild-${version}.deb
	```