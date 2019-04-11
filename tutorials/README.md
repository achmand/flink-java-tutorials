# Getting started with Apache Flink using Python 

In the following tutorials we will be looking on how to use Apache Flink with python. 
Learn more about Flink at http://flink.apache.org/

## Ecosystem Components
In the following section we will look at different components which make up the Apache Flink ecosystem. The image below and the following explanation on different components was taken from this [source](https://data-flair.training/blogs/flink-tutorial/), big thanks to the [Dataflair Team](https://data-flair.training/blogs/author/dfteam2/). 

![Flink Ecosystem](https://raw.githubusercontent.com/achmand/ics5114_assignment/master/tutorials/images/flink_ecosystem.png)

TODO -> Explain the ecosystem

## Building Apache Flink
Parts of this section was taken from Apache Flink GitHub [page](https://github.com/apache/flink) from the "Building Apache Flink from Source" section. As from this date the current stable version of Apache Flink is 1.8.0, so the following tutorials will be based on this version. 

### Prerequisites for building Apache Flink:
* Unix-like environment (we use Linux, Mac OS X, Cygwin)
* git
* Maven (we recommend version 3.2.5)
* Java 8 (Java 9 and 10 are not yet supported)

#### 1. Installing a Unix-like environment 
We are currently using Ubuntu 18.04.1 LTS. I suggest installing [Ubuntu](https://tutorials.ubuntu.com/tutorial/tutorial-install-ubuntu-desktop#0) or [Debian](https://www.debian.org/releases/stretch/installmanual) if you want to use a Linux distro. If you are using Windows you can set up a [Virtual Machine](https://www.virtualbox.org/) and install one of the suggested Linux distros. Any one using Mac OS X is good to go, since it is a Unix-like environment.

#### 2. Installing Git  
##### Debian-based Linux distros (inc Ubuntu)
```
sudo apt-get update
sudo apt-get upgrade
sudo apt-get install git
```
##### Mac OS X
* Step 1: Install [Homebrew](https://brew.sh/) package manager. 
```
ruby -e "$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/master/install)"
brew doctor
```
* Step 2: Install [Git](https://git-scm.com/). 
```
brew install git
```
#### 3. Installing Java 8 (Java 9 and 10 are not yet supported)
##### Debian-based Linux distros (inc Ubuntu)
```
sudo add-apt-repository ppa:webupd8team/java # when prompted press ENTER
sudo apt update
sudo apt install oracle-java8-installer
```
##### Mac OS X
To install Java 8 on Mac OS X, follow this [tutorial](https://docs.oracle.com/javase/8/docs/technotes/guides/install/mac_jdk.html).

#### 3. Installing Maven (recommend version 3.2.5)
The following steps were taken from this [source](https://www.javahelps.com/2017/10/install-apache-maven-on-linux.html), big thanks to [Gobinath Loganathan](https://www.blogger.com/profile/13489835818968107322). 

* Step 1: Download the recommended version 'apache-maven-3.2.5-bin.tar.gz' from this [source](https://archive.apache.org/dist/maven/maven-3/3.2.5/binaries/).
* Step 2: Move to the /opt directory.
```
cd /opt
```
* Step 3: Extract the maven archive into the opt directory.
```
sudo tar -xvzf ~/Downloads/apache-maven-3.2.5-bin.tar.gz
```
* Step 4: Edit the /etc/environment file.
```
# open nano and edit environment variables
sudo nano /etc/environment 

 # add the following environment variable
M2_HOME="/opt/apache-maven-3.6.0"

# append the bin directory to the PATH variable
/opt/apache-maven-3.6.0/bin

# so the result should be something similar to the below
PATH="/usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:/sbin:/bin:/usr/games:/usr/local/games:/opt/apache-maven-3.2.5/bin"
M2_HOME="/opt/apache-maven-3.2.5"

# once you added the environment variable and appended the PATH 
# press "ctrl + o" to save changes and "ctrl + x" to close nano. 
```
* Step 5: Update the mvn command.
```
sudo update-alternatives --install "/usr/bin/mvn" "mvn" "/opt/apache-maven-3.2.5/bin/mvn" 0
sudo update-alternatives --set mvn /opt/apache-maven-3.2.5/bin/mvn
```
* Step 6: Check version to confirm installation.
```
mvn --version
```

### Building from Source
Once all the prerequisites are installed execute the following commands to build from source.  
```
git clone https://github.com/apache/flink.git
cd flink
mvn clean package -DskipTests # this will take up to 10 minutes
```

Flink is now installed in `build-target`

*NOTE: Maven 3.3.x can build Flink, but will not properly shade away certain dependencies. Maven 3.0.3 creates the libraries properly.
To build unit tests with Java 8, use Java 8u51 or above to prevent failures in unit tests that use the PowerMock runner.*

## Learning Apache Flink with Python


## Further Reading and Other Resources 
The following suggestions were taken from the following sources;
TODO -> Write this section 
https://www.quora.com/How-do-I-learn-Apache-Flink
