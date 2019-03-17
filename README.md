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

## Installation

### Linux (Debian / Ubuntu)

1. Download and install Oracle JDK 8 (if you don't have it)
	```
	sudo apt-get install software-properties-common
	sudo apt-get install dirmngr
	sudo apt-key adv --keyserver keyserver.ubuntu.com --recv-keys C2518248EEA14886
	sudo apt-get update
	sudo apt-get install oracle-java8-installer
	```
	
2. Remove previous versions if necessary
	```
	dpkg -r unibuild
	```
	
3. Download or build the desired .deb package

4. Install the .deb package obtained, for example:
	```
	dpkg -i unibuild-0.2.1.deb
	```
	
5. Configure UniBuild
	
    If you installed a server package for the first time, you can configure UniBuild using a web-based setup. It is available on the default port 8999, for example: http://localhost:8999. See the details here:  [Configuration using the web setup](https://github.com/unibuild/unibuild/wiki/Configuration-using-the-web-setup).
    
    You can always configure UniBuild (in fact, if you installed a CLI-only package, you will need to) using the configuration file. See the details here:  [Manual configuration](https://github.com/unibuild/unibuild/wiki/Manual-configuration).
	

## Build from source

### Linux (Debian / Ubuntu)

1. Download and install Oracle JDK 8 (if you don't have it)
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
	git clone https://github.com/unibuild/unibuild /opt/unibuild
	```

6. Install fakeroot
	```
	sudo apt-get install fakeroot
	```
	
7. To build a server, execute the build-server.sh script from your build folder, for example:
	```
	chmod 700 /opt/unibuild/build/scripts/linux/build-server.sh
	cd /opt/unibuild
	/opt/unibuild/build/scripts/linux/build-server.sh
	```
	
	You will find the resulting .deb package in the dist folder created under your build folder, for example:
	```
	/opt/unibuild/dist/unibuild-${version}.deb
	```
	
	
8. To build a CLI-only version, execute the build.sh script from your build folder, for example:
	```
	chmod 700 /opt/unibuild/build/scripts/linux/build.sh
	cd /opt/unibuild
	/opt/unibuild/build/scripts/linux/build.sh
	```
	
	You will find the resulting .deb package in the dist folder created under your build folder, for example:
	```
	/opt/unibuild/dist/unibuild-${version}.deb
	```
	
	
### Windows

1. Download and install Oracle JDK 8 (if you don't have it)
2. Install Maven: https://maven.apache.org/guides/getting-started/windows-prerequisites.html
3. Create a build folder, for example:
	```
	mkdir c:\dev\unibuild
	```
4. Install a Git client of your choice (Git for Windows, Tortoise Git etc.)
5. Clone the repository https://github.com/unibuild/unibuild from GitHub into your build folder (c:\dev\unibuild in the example)
6. To build a server, execute the build-server-x64.bat or build-server-x86.bat script, for example:
	```
	c:\dev\unibuild\build\scripts\win\build-server-x64.bat
	```
		
	You will find the resulting .exe filee in the dist folder created under your build folder
	

