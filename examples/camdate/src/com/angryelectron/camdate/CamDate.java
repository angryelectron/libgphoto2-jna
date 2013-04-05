/**
 * Camdate - set system time using a Camera.
 * Copyright 2013 Andrew Bythell, abythell@ieee.org
 *
 * This is a handy tool for setting the system time when using a Raspberry Pi 
 * without a real-time clock or network connection.  Currently, this only works
 * on Linux systems when run as root.
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

package com.angryelectron.camdate;

import com.angryelectron.gphoto2.GPhoto2;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Set system date from camera date.
 */
public class CamDate {

    /**
     * Main.
     * @param args 
     */
    public static void main(String[] args) {
        
        /*
         * Connect to camera
         */
        GPhoto2 camera = new GPhoto2();
        try {
            camera.open();
        } catch (IOException ex) {
            Logger.getLogger(CamDate.class.getName()).log(Level.SEVERE, ex.getMessage());
            System.exit(-1);
        }
        
        /*
         * Get date from camera
         */       
        String dateTime = null;
        try {
            dateTime = camera.getConfig("datetime");       
        } catch (IOException ex) {
            Logger.getLogger(CamDate.class.getName()).log(Level.SEVERE, ex.getMessage());
            camera.close();
            System.exit(-1);
        }        
        Date cameraDate = new Date(Long.parseLong(dateTime));
        
        /*
         * Set system time
         */
        setLinuxSystemDate(cameraDate);
        
        /*
         * Cleanup
         */
        camera.close();
    }
    
    /**
     * Set Linux system time.
     * @param date 
     */
    private static void setLinuxSystemDate(Date date) {
        SimpleDateFormat df = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss Z");
        String[] args = new String[]{"date", "-s", df.format(date)};

        Runtime rt = Runtime.getRuntime();        
        Process proc;  
        int result = 1;
        try {
            proc = rt.exec(args);
            result = proc.waitFor();
        } catch (IOException | InterruptedException ex) {
            Logger.getLogger(CamDate.class.getName()).log(Level.SEVERE, ex.getMessage());
        }
        if (result != 0) {
            Logger.getLogger(CamDate.class.getName()).log(Level.SEVERE, "Failed to set system time from camera. (root?)");
        } else {
            Logger.getLogger(CamDate.class.getName()).log(Level.INFO, "Setting system time from camera.");
        }
    }
}
