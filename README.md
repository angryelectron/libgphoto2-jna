libgphoto2-java
===
Java native bindings for libgphoto2.  For details, please see 
http://angryelectron.com/projects/libgphoto2-jna.  This is a simple, basic
library that by no means implements all of libgphoto2 features. 

Build
---
Get the source or pre-built binaries at https://github.com/angryelectron/libgphoto2-jna.

To build this library you will need the Java 7 SDK and Apache Ant.  Run 'ant' to 
build ./dist/libgphoto2-jna.jar and ./dist/javadoc.  To run tests, attach a camera and run
'ant test'.

Alternatively, you can open and build the library using the Netbeans IDE.

Use
---
Build and add libgphoto2-jna.jar to your project.  Also ensure that libgphoto2 
is installed and that libgphoto2.so is on the library path (you may need to 
create a symbolic link from your specific version).   See the test classes
and/or javadocs for details.

Examples
---
Simplified example showing how to take a picture:

    GPhoto2 camera = new GPhoto2();
    camera.open();    
    camera.capture();  // image remains on camera
    File image = camera.captureAndDownload(); // image saved to disk
    camera.close();
   
To get/set simple configuration parameters:

    GPhoto2 camera = new GPhoto2();
    camera.open();
    camera.setConfig("isoauto", "On");
    String value = camera.getConfig("isoauto");

To get/set more complex configurations:

    GPhoto2 camera = new GPhoto2();
    camera.open();
    GPhoto2Config config = new GPhoto2Config(camera);
    config.readConfig();
    config.setParameter("burstnumber", "5");
    config.setParameter("capturetarget", "Memory card");
    config.writeConfig();
    camera.close();

Any parameter than can be set or read using the gphoto2 command line can be configured
in this way.

Help / Support / Bugs
---
Please see the [GitHub Issue Tracker](https://github.com/angryelectron/libgphoto2-jna/issues)

About
---
* libgphoto2-java
* Copyright 2013-2014, Andrew Bythell <abythell@ieee.org>
* <http://angryelectron.com/projects/libgphoto2-jna>

This library is free software: you can redistribute it and/or modify it under
the terms of the GNU General Public License as published by the Free Software
Foundation, either version 3 of the License, or (at your option) any later
version.

This library is distributed in the hope that it will be useful, but WITHOUT ANY
WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
PARTICULAR PURPOSE. See the GNU General Public License for more details.

You should have received a copy of the GNU General Public License along with
the library. If not, see <http://www.gnu.org/licenses/>.
