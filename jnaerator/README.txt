How to generate JNA bindings for libgphoto2.

This was tested on Ubuntu 12.04 with libgphoto2 2.4.13.  Modify
config.jnaerator to set include paths if they are different on your system.

Use this to generate JNA bindings for libgphoto2:

	java -jar jnaerator.jar
	cat jna.patch | patch -p1

This will produce source bindings in ./com/angryelectron/libgphoto2, replace theCamera class with a simple PointerType, and remove dependencies on com.ochafik.lang.jnaerator.runtime.

Copy the resulting classes to your project and add /usr/shared/java/jna.jar to the project's classpath.

