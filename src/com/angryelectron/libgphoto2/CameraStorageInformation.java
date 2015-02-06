/**
 * Copyright 2012 Andrew Bythell, abythell@ieee.org
 *
 * This file is part of libgphoto2-jna.
 *
 * libgphoto2-jna is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * libgphoto2-jna is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * libphoto2-jna. If not, see <http://www.gnu.org/licenses/>.
 */
package com.angryelectron.libgphoto2;
import java.util.Arrays;
import java.util.List;

import com.sun.jna.NativeLong;
import com.sun.jna.Structure;
/**
 * <i>native declaration : /usr/include/gphoto2/gphoto2-filesys.h:974</i><br>
 * This file was autogenerated by <a href="http://jnaerator.googlecode.com/">JNAerator</a>,<br>
 * a tool written by <a href="http://ochafik.com/">Olivier Chafik</a> that <a href="http://code.google.com/p/jnaerator/wiki/CreditsAndLicense">uses a few opensource projects.</a>.<br>
 * For help, please visit <a href="http://nativelibs4java.googlecode.com/">NativeLibs4Java</a> , <a href="http://rococoa.dev.java.net/">Rococoa</a>, or <a href="http://jna.dev.java.net/">JNA</a>.
 */
public class CameraStorageInformation extends Structure {
	/**
	 * @see CameraStorageInfoFields<br>
	 * < \brief Bitmask of struct members that are specified.<br>
	 * C type : CameraStorageInfoFields
	 */
	public int fields;
	/**
	 * < \brief Basedirectory of the storage. Will be "/" if just 1 storage on the camera.<br>
	 * C type : char[256]
	 */
	public byte[] basedir = new byte[256];
	/**
	 * < \brief Label of the storage. Similar to DOS label.<br>
	 * C type : char[256]
	 */
	public byte[] label = new byte[256];
	/**
	 * < \brief Description of the storage.<br>
	 * C type : char[256]
	 */
	public byte[] description = new byte[256];
	/**
	 * @see CameraStorageType<br>
	 * < \brief Hardware type of the storage.<br>
	 * C type : CameraStorageType
	 */
	public int type;
	/**
	 * @see CameraStorageFilesystemType<br>
	 * < \brief Hierarchy type of the filesystem.<br>
	 * C type : CameraStorageFilesystemType
	 */
	public int fstype;
	/**
	 * @see CameraStorageAccessType<br>
	 * < \brief Access permissions.<br>
	 * C type : CameraStorageAccessType
	 */
	public int access;
	/// < \brief Total capacity in kbytes.
	public NativeLong capacitykbytes;
	/// < \brief Free space in kbytes.
	public NativeLong freekbytes;
	/// < \brief Free space in images (guessed by camera).
	public NativeLong freeimages;
	public CameraStorageInformation() {
		super();
	}
	@SuppressWarnings("rawtypes")
  @Override
  protected List getFieldOrder() {
	  return Arrays.asList(new String[]{"fields", "basedir", "label", "description", "type", "fstype", "access", "capacitykbytes", "freekbytes", "freeimages"});
  };

	public static class ByReference extends CameraStorageInformation implements Structure.ByReference {
		
	};
	public static class ByValue extends CameraStorageInformation implements Structure.ByValue {
		
	};
}
