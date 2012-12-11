package com.angryelectron.libgphoto2;
import com.sun.jna.Pointer;
import com.sun.jna.PointerType;

public class Camera extends PointerType {
    
    public Camera(Pointer address) {
        super(address);
    }
    
    public Camera() {
        super();
    }
}