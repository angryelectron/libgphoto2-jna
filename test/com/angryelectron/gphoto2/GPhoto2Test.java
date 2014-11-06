/**
 * GPhoto2 
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
 *
 */


package com.angryelectron.gphoto2;

import com.angryelectron.libgphoto2.Camera;
import java.io.IOException;
import java.util.List;
import static org.junit.Assert.assertFalse;
import org.junit.Test;

public class GPhoto2Test {        
        
    /**
     * Test of listCameras method, of class GPhoto2.
     * @throws java.io.IOException
     */
    @Test
    public void testListCameras() throws IOException {
        GPhoto2 camera = new GPhoto2();
        try {
            camera.open();
            List<Camera> cameraList = camera.listCameras();
            assertFalse("No cameras could be listed.", cameraList.isEmpty());
        } finally {
            camera.close();
        }
    }  
    
    /**
     * Test of capture method, of class GPhoto2.
     * @throws java.io.IOException
     */
    @Test
    public void testCapture() throws IOException {                        
            GPhoto2 camera = new GPhoto2();
            try {
                camera.open();
                camera.capture();
                camera.capture();
                camera.capture();
            } finally {
                camera.close();
            }            
    }

    /**
     * Test of captureAndDownload method, of class GPhoto2.     
     */
    @Test
    public void testCaptureAndDownload() throws IOException {                

    }

    /**
     * Test of captureTethered method, of class GPhoto2.
     */
    @Test
    public void testCaptureTethered() {
    }

    /**
     * Test of burstAndDownload method, of class GPhoto2.
     * @throws java.io.IOException
     */
    @Test
    public void testBurstAndDownload() throws IOException {
        GPhoto2 camera = new GPhoto2();
        try {
            camera.open();            
            camera.setConfig("burstnumber", String.valueOf(5));
            camera.burstAndDownload(true);
        } finally {
            camera.close();
        }
    }

    /**
     * Test of setConfig method, of class GPhoto2.
     */
    @Test
    public void testSetConfig() {
    }

    /**
     * Test of getConfig method, of class GPhoto2.
     */
    @Test
    public void testGetConfig() {
    }
    
}
