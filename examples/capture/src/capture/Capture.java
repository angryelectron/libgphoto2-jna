package capture;

import com.angryelectron.gphoto2.GPhoto2;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Capture {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        /*
         * Connect to camera
         */
        GPhoto2 camera = new GPhoto2();
        try {
            camera.open();
        } catch (IOException ex) {
            Logger.getLogger(Capture.class.getName()).log(Level.SEVERE, ex.getMessage());
            System.exit(-1);
        }
        
        /*
         * Capture image and download.  Nikon cameras need to buffer burst shots
         * via the memory card, not internal memory like Canons.
         */               
        try {
            camera.setConfig("burstnumber", "5");
            camera.setConfig("capturetarget", "Memory card");
            camera.burstAndDownload(true);
        } catch (IOException ex) {
            Logger.getLogger(Capture.class.getName()).log(Level.SEVERE, ex.getMessage());
            camera.close();
            System.exit(-1);
        }                                     
        
        /*
         * Cleanup
         */
        camera.close();
    }
}
