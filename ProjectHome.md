## Introduction ##

A recurring problem in software engineering is the correct definition and enforcement of an architecture that can guide the development and maintenance processes of software systems. This is due in part to a lack of correct definition and maintenance of architectural documentation.

In this project, an approach based on a bayesian network classifier is proposed to aid in the generation of an architecture view for web applications developed according to the Model View Controller (MVC) architectural pattern. This view is comprised of the system components, their inter-project relations and their classification according to the MVC pattern. The generated view can then be used as part of the system documentation to help enforce the original architectural intent when changes are applied to the system.

Finally, an implementation of this approach is presented for Java based-systems, using training data from popular web development frameworks.

## How to use it ##

The GenerateArchitectureFromWar tutorial includes detailed instructions of how to create a software architecture from an existing WAR file. You may also create an architecture from an Eclipse project, following a similar approach.

## Installation ##

  1. Download and install [Graphviz](http://www.graphviz.org/Download..php).
  1. Download the latest [Mvc Analyzer distribution](http://code.google.com/p/mvc-analyzer/downloads/list).
  1. Download and install the latest [Eclipse Classic IDE](http://www.eclipse.org/downloads/packages/eclipse-classic-37/indigor).
  1. Extract the _Mvc Analyzer distribution_ to the **plugins** directory of your Eclipse distribution.

## Related projects ##

This tool is the base of a related project, [Web2MexADL](http://code.google.com/p/web2mexadl/), which in addition to the SVG document, also generates an architecture view based on the [MexADL](http://code.google.com/p/mexadl/) approach.

## Reference ##

Castrejón, J., Lozano, R., Vargas-Solar, G.: “Generation of an Architecture View for Web Applications using a Bayesian Network Classifier”. In:  22nd International Conference on Electrical Communications and Computers (CONIELECOMP 2012), February 2012.

_Note: A publicly available version of this paper can be found [right here](http://mvc-analyzer.googlecode.com/files/MvcAnalyzer_accepted_version.pdf)._

#### Notes ####

  * The current implementation has been tested on Mac OS X 10.6 - 10.7 and on Ubuntu 11.10
  * To make sure that Eclipse has access to the correct PATH in Mac OS X, perform the following steps: (http://stackoverflow.com/questions/135688/setting-environment-variables-in-os-x)
    * Open a terminal and type: sudo vi /etc/launchd.conf
    * Add the following line: setenv PATH /usr/bin:/bin:/usr/sbin:/sbin:/usr/local/bin:/usr/X11/bin
    * Save the file and reboot your mac