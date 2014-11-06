/**
 * GPhoto2 Copyright 2012 Andrew Bythell, abythell@ieee.org
 *
 * This file is part of libgphoto2-jna.
 *
 * libgphoto2-jna is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 *
 * libgphoto2-jna is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * libphoto2-jna. If not, see <http://www.gnu.org/licenses/>.
 *
 */
package com.angryelectron.gphoto2;

import com.angryelectron.libgphoto2.Camera;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import org.junit.Test;

public class GPhoto2Test {
    
    /**
     * Test of capture method, of class GPhoto2.
     *
     * @throws java.io.IOException
     */
    @Test
    public void testCapture() throws IOException {
        System.out.println("testCapture");
        GPhoto2 camera = new GPhoto2();
        try {
            camera.open();
            camera.capture();
        } finally {
            camera.close();
        }
    }
    
    /**
     * Test of capture method, of class GPhoto2.
     *
     * @throws java.io.IOException
     */
    @Test
    public void testCaptureWithWait() throws IOException {
        System.out.println("testCaptureWithWait");
        GPhoto2 camera = new GPhoto2();
        try {
            camera.open();
            camera.waitForCaptureEvent(true);
            camera.capture();
        } finally {
            camera.close();
        }
    }

    /**
     * Test of captureAndDownload method, of class GPhoto2.
     * @throws java.io.IOException
     */
    @Test
    public void testCaptureAndDownload() throws IOException {
        System.out.println("testCaptureAndDownload");
        GPhoto2 camera = new GPhoto2();
        try {
            camera.open();
            File image = camera.captureAndDownload(true);            
            assertTrue(image.exists());
            Files.deleteIfExists(image.toPath());
        } finally {
            camera.close();
        }
    }
    
    /**
     * Test of captureAndDownload method, of class GPhoto2.
     * @throws java.io.IOException
     */
    @Test
    public void testCaptureAndDownloadWithWait() throws IOException {
        System.out.println("testCaptureAndDownloadWithWait");
        GPhoto2 camera = new GPhoto2();
        try {
            camera.open();
            camera.waitForCaptureEvent(true);
            File image = camera.captureAndDownload(true);            
            assertTrue(image.exists());
            Files.deleteIfExists(image.toPath());
        } finally {
            camera.close();
        }
    }
    
    /**
     * Test of burstAndDownload method, of class GPhoto2.
     *
     * @throws java.io.IOException
     */
    @Test
    public void testBurstAndDownload() throws IOException {
        System.out.println("testBurstAndDownload (not all models support burstnumber)");
        GPhoto2 camera = new GPhoto2();
        try {
            camera.open();
            camera.setConfig("burstnumber", String.valueOf(5));
            ArrayList<File> images = camera.burstAndDownload(true);
            for (File image : images) {
                assertTrue(image.exists());
                Files.deleteIfExists(image.toPath());
            }
            camera.setConfig("burstnumber", String.valueOf(1));
        } finally {
            camera.close();
        }
    }

    /**
     * Test of setConfig method, of class GPhoto2.
     * @throws java.io.IOException
     */
    @Test
    public void testConfig() throws IOException {
        System.out.println("testConfig");
        GPhoto2 camera = new GPhoto2();
        
        /**
         * This parameter was chosen as it is likely that all cameras
         * support it.
         */
        String parameter = "isoauto";
        try {
            camera.open();
            String v = camera.getConfig(parameter);
            camera.setConfig(parameter, "Off");
            assertTrue(camera.getConfig(parameter).equals("Off"));
            camera.setConfig(parameter, "On");
            assertTrue(camera.getConfig(parameter).equals("On"));
            camera.setConfig(parameter, v);
            assertTrue(camera.getConfig(parameter).equals(v));            
        } finally {
            camera.close();
        }
    }
    
    /**
     * This is critical to test as calling listCameras() while a camera
     * is open causes the JVM to crash.
     * @throws IOException 
     */
    @Test
    public void testCameraList() throws IOException {
        System.out.println("testCameraList");
        GPhoto2 camera = new GPhoto2();        
        List<Camera> cameras = camera.listCameras();
        assertFalse("No cameras found.", cameras.isEmpty());
    }   
    @Test(expected=IOException.class)
    public void testCameraListWhileOpen() throws IOException {
        System.out.println("testCameraListWhileOpen");
        GPhoto2 camera =new GPhoto2();
        try {
            camera.open();
        } catch (IOException ex) {
            fail("GPhoto2.open() threw exception.");
        }
        
        try {
            List<Camera> cameras = camera.listCameras();
        } finally {
            camera.close();
        }
    }
    
}
