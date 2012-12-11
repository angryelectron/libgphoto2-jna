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
import com.sun.jna.Pointer;
import com.sun.jna.PointerType;

/*
 * Simple Camera class which extends PointerType.  Original jnaerated class
 * was based on a structure that is only used internally.
 */
public class Camera extends PointerType {
    
    public Camera(Pointer address) {
        super(address);
    }
    
    public Camera() {
        super();
    }
}