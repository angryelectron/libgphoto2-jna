CamDate
Copyright 2013 Andrew Bythell <abythell@ieee.org>
http://angryelectron.com

This application sets the system time on Linux systems using a USB-attached
camera and the libgphoto2-jna library.  It is useful for setting the time on a
Raspberry Pi which is not connected to a network and has no real time clock.

To build, run "ant jar".  To use, run "java -jar camdate.jar" in the ./dist
directory as "root" (or sudo).
