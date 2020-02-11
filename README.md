This repository contains the source code of the *VOLANO™* chat server, a formerly proprietary software package that is now available as free and open source software under the [GNU General Public License v3.0](LICENSE).

The *VOLANO* chat server is a cross-platform console application written in Java with clients that run in the Web browser as Java applets. I wrote the initial release in 1996 and licensed the software to customers in 50 countries over the following years. Customers used the *VOLANO* chat server to provide chat rooms for visitors to their Web sites.

### Goals

My goals for this project are to update the software as follows:

- Switch the build automation from Apache Ant and GNU Make to [Gradle](https://docs.gradle.org/current/userguide/multi_project_builds.html).

- Refactor the *VOLANO* chat server for the latest [Java Development Kit](https://jdk.java.net/) (JDK).

- Develop a new client for the [modern Web](https://developer.mozilla.org/en-US/docs/Web/API/Websockets_API) to replace the existing Java applets.

- Provide the *VOLANO* chat server for Linux as a self-contained app in the [Snap Store](https://snapcraft.io/store).

- Create a Web-based administrator interface for managing the *VOLANO* chat server.

This is a relatively large project, and I have a lot to learn. The initial commit contains 20,100 lines of Java source code. The time-frame for completing all of these goals, therefore, is likely on the order of a few years rather than a few weeks. Please note that I may modify or abandon some of the goals, depending on my availability and support, and based on changes to the software ecosystem during that time.

### Status

I expect to make comprehensive changes as I bring the project up to date with current Java code practices, build automation tools, and software packaging techniques. During that time, entire sections of the code may be refactored, rewritten, moved, or deleted. I plan to accept issue reports and pull requests once the source code is stable enough to allow for collaboration.

I will post build instructions and information about running and debugging the software once I convert the build automation to Gradle.

Thank you for your interest!

— John Neffenger
