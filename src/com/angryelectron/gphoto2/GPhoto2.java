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
import com.angryelectron.libgphoto2.CameraAbilities;
import com.angryelectron.libgphoto2.CameraFilePath;
import com.angryelectron.libgphoto2.GPPortInfo;
import com.angryelectron.libgphoto2.Gphoto2Library;
import com.angryelectron.libgphoto2.Gphoto2Library.CameraAbilitiesList;
import com.angryelectron.libgphoto2.Gphoto2Library.CameraCaptureType;
import com.angryelectron.libgphoto2.Gphoto2Library.CameraEventType;
import com.angryelectron.libgphoto2.Gphoto2Library.CameraFile;
import com.angryelectron.libgphoto2.Gphoto2Library.CameraFileType;
import com.angryelectron.libgphoto2.Gphoto2Library.CameraList;
import com.angryelectron.libgphoto2.Gphoto2Library.GPContext;
import com.angryelectron.libgphoto2.Gphoto2Library.GPContextErrorFunc;
import com.angryelectron.libgphoto2.Gphoto2Library.GPContextMessageFunc;
import com.angryelectron.libgphoto2.Gphoto2Library.GPPortInfoList;
import com.angryelectron.libgphoto2.Gphoto2Library.va_list;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.PointerByReference;
import java.io.File;
import java.io.IOException;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 * Simple API for controlling a camera using libgphoto2.
 */
public class GPhoto2 {

    Gphoto2Library gphoto2;
    GPContext context;
    Camera camera;
    private String error;
    private String message;
    private boolean waitForEvent = false;

    /**
     * This will be called when libgphoto2 has an error to share.
     */
    private final GPContextErrorFunc errorFunc = new GPContextErrorFunc() {
        @Override
        public void apply(GPContext context, Pointer format, va_list args, Pointer data) {
            //TODO: parse these errors better            
            error = format.getString(0);            
        }
    };

    /**
     * This will be called when libgphoto2 has a message to share.
     */
    private final GPContextMessageFunc messageFunc = new GPContextMessageFunc() {

        @Override
        public void apply(GPContext context, Pointer format, va_list args, Pointer data) {
            //TODO: parse these errors better
            message = format.getString(0);            
        }

    };

    /**
     * Constructor. Loads the native libgphoto2.so library and initializes it.
     */
    public GPhoto2() {
        gphoto2 = (Gphoto2Library) Native.loadLibrary("libgphoto2.so", Gphoto2Library.class);
        context = gphoto2.gp_context_new();
        gphoto2.gp_context_set_error_func(context, errorFunc, null);
        gphoto2.gp_context_set_message_func(context, messageFunc, null);
    }

    /**
     * Wait for camera to generate a capture event before returning. Default
     * behavior is not to wait (wait = false).
     * <p>
     * After a 'capture', some cameras will return immediately but before the
     * image has been generated. Enable waitForCaptureEvent() to ensure the
     * image is ready.</p>
     * <p>
     * Other cameras block internally until the image is ready, and therefore
     * don't generate capture events. These devices will timeout if
     * waitForCaptureEvent() is enabled.</p>
     *
     * @param wait When true, capture methods will block until the camera
     * returns a Capture or Timeout event.
     */
    public void waitForCaptureEvent(boolean wait) {
        this.waitForEvent = wait;
    }

    /**
     * Enumerate all cameras currently attached.  Will fail unless all attached
     * cameras are closed().
     *
     * @return A list of Camera objects.
     * @throws IOException
     */
    public List<Camera> listCameras() throws IOException {

        /*
         * This is an rather absurd amount of code for what it actually
         * accomplishes in the end.         
         */
        int rc;
        
        if (camera != null) {
           throw new IOException("Can't list cameras while devices are open."); 
        }

        /*
         * Create and load an Abilities List.
         */
        CameraAbilitiesList refAbilitiesList[] = new CameraAbilitiesList[1];
        rc = gphoto2.gp_abilities_list_new(refAbilitiesList);        
        if (rc != Gphoto2Library.GP_OK) {
            throw new IOException("gp_abilities_list_new failed with code " + rc);
        }
        
        CameraAbilitiesList cameraAbilitiesList = refAbilitiesList[0];
        rc = gphoto2.gp_abilities_list_load(cameraAbilitiesList, context);
        if (rc != Gphoto2Library.GP_OK) {
            throw new IOException("gp_abilities_list_load failed with code " + rc);
        }

        /*
         * Create and load a Ports List.
         */
        GPPortInfoList refPortList[] = new GPPortInfoList[1];
        rc = gphoto2.gp_port_info_list_new(refPortList);
        if (rc != Gphoto2Library.GP_OK) {
            throw new IOException("gp_port_info_list_new failed with code " + rc);
        }
        
        GPPortInfoList portInfoList = refPortList[0];
        rc = gphoto2.gp_port_info_list_load(portInfoList);
        if (rc != Gphoto2Library.GP_OK) {
            throw new IOException("gp_port_info_list_load failed with code " + rc);
        }

        /*
         * Create and load a Cameras List from the Ports and Abilities lists.
         */
        CameraList refCameraList[] = new CameraList[1];
        rc = gphoto2.gp_list_new(refCameraList);
        if (rc != Gphoto2Library.GP_OK) {
            throw new IOException("gp_list_new failed with code " + rc);
        }
        
        CameraList cameraList = refCameraList[0];
        rc = gphoto2.gp_abilities_list_detect(cameraAbilitiesList, portInfoList, cameraList, context);
        if (rc != Gphoto2Library.GP_OK) {
            throw new IOException("gp_abilities_list_detect failed with code " + rc);
        }

        /*
         * Convert the Cameras List into a list of Camera objects
         */
        List<Camera> cList = new ArrayList<>();
        int size = gphoto2.gp_list_count(cameraList);
        String[] model = new String[1];
        String[] port = new String[1];
        for (int i = 0; i < size; i++) {

            /*
             * Get Model
             */
            rc = gphoto2.gp_list_get_name(cameraList, i, model);
            if (rc != Gphoto2Library.GP_OK) {
                throw new IOException("gp_list_get_name failed with code " + rc);
            }

            /*
             * Get Port
             */
            rc = gphoto2.gp_list_get_value(cameraList, i, port);
            if (rc != Gphoto2Library.GP_OK) {
                throw new IOException("gp_list_get_value failed with code " + rc);
            }

            /*
             * Create new Camera Object
             */
            PointerByReference refCamera = new PointerByReference();
            gphoto2.gp_camera_new(refCamera);
            Camera c = new Camera(refCamera.getValue());

            /*
             * Get List of Abilities for this Model, then associate the 
             * Abilities with a Camera
             */
            CameraAbilities.ByValue cameraAbilities = new CameraAbilities.ByValue();
            int modelIndex = gphoto2.gp_abilities_list_lookup_model(cameraAbilitiesList, model[0]);
            gphoto2.gp_abilities_list_get_abilities(cameraAbilitiesList, modelIndex, cameraAbilities);
            gphoto2.gp_camera_set_abilities(c, cameraAbilities);

            /*
             * Do the same for the Port.  TODO: verify return codes.
             */
            GPPortInfo.ByValue portInfo = new GPPortInfo.ByValue();
            gphoto2.gp_port_info_list_lookup_path(portInfoList, port[0]);
            gphoto2.gp_port_info_list_get_info(portInfoList, i, portInfo);
            gphoto2.gp_camera_set_port_info(camera, portInfo);

            /*
             * Finally, add this Camera to the List
             */
            cList.add(c);
        }

        /*
         * Whew!  That was a lot of work just to enumerate some cameras!
         */
        return cList;

    }

    /**
     * Open camera connection (single camera). If multiple cameras are connected
     * only the first detected camera is used.
     *
     * @throws IOException If camera cannot be opened.
     */
    public void open() throws IOException {
        PointerByReference ref = new PointerByReference();
        gphoto2.gp_camera_new(ref);
        camera = new Camera(ref.getValue());
        int result = gphoto2.gp_camera_init(camera, context);
        if (result != Gphoto2Library.GP_OK) {
            gphoto2.gp_camera_unref(camera);
            camera = null;
            throw new IOException(error + "(" + result + ")");
        }
    }

    /**
     * Open camera connection (multiple cameras).
     *
     * @param camera Specify the camera to open by passing one of the Camera
     * objects obtained by {@link #listCameras()}.
     * @throws IOException
     */
    public void open(Camera camera) throws IOException {
        this.camera = camera;
        int rc = gphoto2.gp_camera_init(camera, context);
        if (rc != Gphoto2Library.GP_OK) {
            gphoto2.gp_camera_unref(camera);
            this.camera = null;
            throw new IOException("gp_camera_init failed with code " + rc);
        }
    }

    /**
     * Close camera connection.
     */
    public void close() {
        gphoto2.gp_camera_exit(camera, context);
        gphoto2.gp_context_unref(context);
        gphoto2.gp_camera_unref(camera);                
    }

    /**
     * Take a picture.
     *
     * @return Name of image file on the camera.
     * @throws IOException If the picture cannot be taken.
     */
    public String capture() throws IOException {
        CameraFilePath path = captureImage();
        return new String(path.name);
    }

    /**
     * Take a picture and save it to disk. Currently images can only be saved
     * into the current working directory.
     *
     * @param delete True if image should be removed from camera after saving.
     * @return a File object which points to the saved image.
     * @throws IOException If image cannot be captured or saved.
     */
    public File captureAndDownload(Boolean delete) throws IOException {
        CameraFilePath path = captureImage();
        return saveImage(path, delete);
    }

    /**
     * Don't take a picture, rather wait for camera to capture an image, then
     * download. Call in a loop to create a tethered-shooting mode.
     *
     * @param timeout Time to wait before aborting (in milliseconds).
     * @param delete If true, image is deleted from camera after downloading.
     * @return A File which points to the new image on disk.
     * @throws IOException on camera error
     * @throws InterruptedException when timeout is reached.
     */
    public File captureTethered(int timeout, boolean delete) throws IOException, InterruptedException {
        int rc;
        IntBuffer eventType = IntBuffer.allocate(1);
        CameraFilePath path = new CameraFilePath();
        PointerByReference ref = new PointerByReference(path.getPointer());

        /*
         * need to loop, othewise GP_EVENT_UNKNOWN is almost always returned
         */
        while (true) {
            rc = gphoto2.gp_camera_wait_for_event(camera, timeout, eventType, ref, context);
            if (rc != Gphoto2Library.GP_OK) {
                throw new IOException("Wait for Event failed with code " + rc);
            }
            if (eventType.get(0) == CameraEventType.GP_EVENT_FILE_ADDED) {
                return saveImage(path, delete);
            } else if (eventType.get(0) == CameraEventType.GP_EVENT_TIMEOUT) {
                throw new InterruptedException("Timeout occured waiting for GP_EVENT_FILE_ADDED.");
            }
        }
    }

    /**
     * Fire the shutter, then download all images on the camera. Useful when
     * using burstmode, which may capture multiple images for one captureImage()
     * call.
     *
     * @param delete If true, *all* images on the camera should be removed after
     * saving.
     * @return An ArrayList of File objects representing the saved images.
     * @throws IOException if images cannot be captured or saved.
     */
    public ArrayList<File> burstAndDownload(Boolean delete) throws IOException {
        return saveImages(captureImage(), delete);
    }

    /**
     * Change a single camera setting. If updating several parameters at once,
     * it may be more efficient to use {@link
     * com.angryelectron.gphoto2.GPhoto2Config#getParameter(java.lang.String)}.
     *
     * @param param The name of the parameter to set.
     * @param value The value of the parameter.
     * @throws IOException
     */
    public void setConfig(String param, String value) throws IOException {
        GPhoto2Config config = new GPhoto2Config(this);
        config.readConfig();
        config.setParameter(param, value);
        config.writeConfig();
    }

    /**
     * Read a single camera setting. If reading several parameters at once it
     * may be more efficient to use
     * {@link com.angryelectron.gphoto2.GPhoto2Config#getParameter(java.lang.String)}.
     *
     * @param param Parameter to be read.
     * @return Value of the parameter. Date values are returned as unix-time
     * strings.
     * @throws IOException if the parameter cannot be read.
     */
    public String getConfig(String param) throws IOException {
        GPhoto2Config config = new GPhoto2Config(this);
        config.readConfig();
        String value = config.getParameter(param);
        return value;
    }

    /**
     * Wait (block) until the specified event is received or a timeout occurs.
     *
     * @param timeout Timeout value, in milliseconds
     * @param event expected CameraEventType
     * @throws IOException if Timeout occurs or camera is unreachable.
     */
    private void waitForEvent(int timeout, int event) throws IOException {
        IntBuffer i = IntBuffer.allocate(1);
        PointerByReference data = new PointerByReference();
        int rc;

        /*
         * need to loop, othewise GP_EVENT_UNKNOWN is almost always returned
         */
        while (true) {
            rc = gphoto2.gp_camera_wait_for_event(camera, timeout, i, data, context);
            if (rc != Gphoto2Library.GP_OK) {
                throw new IOException("Wait for Event failed with code " + rc);
            }
            if (i.get(0) == event) {
                return;
            } else if (i.get(0) == CameraEventType.GP_EVENT_TIMEOUT) {
                throw new IOException("Timeout occured waiting for event " + event);
            }
        }
    }

    /**
     * Capture an Image.
     *
     * @return CameraFilePath which references the captured image.
     * @throws IOException If image cannot be captured.
     */
    private CameraFilePath captureImage() throws IOException {
        CameraFilePath cameraFilePath = new CameraFilePath();
        int result = gphoto2.gp_camera_capture(camera, CameraCaptureType.GP_CAPTURE_IMAGE, cameraFilePath, context);
        if (result != Gphoto2Library.GP_OK) {
            throw new IOException(error + "(" + result + ")");
        }
        if (this.waitForEvent) {
            waitForEvent(5000, CameraEventType.GP_EVENT_CAPTURE_COMPLETE);
        }
        return cameraFilePath;
    }

    /**
     * Save image to disk in current directory. TODO: allow path and filename to
     * be specified.
     *
     * @param path CameraFilePath object returned by captureImage()
     * @param delete True if the image should be deleted from the camera
     * @return a File which points to the new image.
     * @throws IOException If the image cannot be saved.
     */
    private File saveImage(CameraFilePath path, Boolean delete) throws IOException {
        String folder = new String(path.folder);
        String name = new String(path.name);
        int rc;

        /* initialize a CameraFile object */        
        CameraFile ref[] = new CameraFile[1];
        rc = gphoto2.gp_file_new(ref);
        if (rc != Gphoto2Library.GP_OK) {
            throw new IOException("gp_file_new failed with code " + rc);
        }
        CameraFile cameraFile = ref[0];

        /* point the CameraFile object at the CameraFilePath */
        rc = gphoto2.gp_camera_file_get(camera, folder, name, CameraFileType.GP_FILE_TYPE_NORMAL, cameraFile, context);
        if (rc != Gphoto2Library.GP_OK) {
            throw new IOException("gp_camera_file_get failed with code " + rc);
        }

        /* save CameraFile to disk */
        rc = gphoto2.gp_file_save(cameraFile, name);
        gphoto2.gp_file_free(cameraFile);
        if (rc != Gphoto2Library.GP_OK) {
            throw new IOException("gp_file_save failed with code " + rc);
        }

        if (delete) {
            rc = gphoto2.gp_camera_file_delete(camera, folder, name, context);
            if (rc != Gphoto2Library.GP_OK) {
                throw new IOException("gp_camera_file_delete failed with code " + rc);
            }
        }
        return new File(name.trim());
    }

    /**
     * Download all images in the given path into the current directory.
     * Warning: If delete option is enabled, all images on the camera will be
     * deleted, not just the ones from the most recent capture.
     *
     * @param path CameraFilePath to the image on the camera
     * @param delete True if all images should be removed from camera after
     * saving
     * @return ArrayList of Files for the downloaded images
     * @throws IOException on error
     */
    private ArrayList<File> saveImages(CameraFilePath path, Boolean delete) throws IOException {
        ArrayList<File> fileList = new ArrayList<>();
        String folder = new String(path.folder);

        /* get a list of files */
        PointerByReference ref = new PointerByReference();
        int rc = gphoto2.gp_list_new(ref);
        if (rc != Gphoto2Library.GP_OK) {
            throw new IOException("gp_list_new failed with code " + rc);
        }
        CameraList cameraList = new CameraList(ref.getValue());
        rc = gphoto2.gp_camera_folder_list_files(camera, folder, cameraList, context);
        if (rc != Gphoto2Library.GP_OK) {
            throw new IOException("gp_camera_folder_list_files failed with code " + rc);
        }

        /* iterate through list, downloading each item */
        int gp_list_count = gphoto2.gp_list_count(cameraList);
        for (int i = 0; i < gp_list_count; i++) {
            rc = gphoto2.gp_list_get_name(cameraList, i, ref);
            if (rc != Gphoto2Library.GP_OK) {
                throw new IOException("gp_list_get_name failed with code " + rc);
            }
            String name = ref.getValue().getString(0);
            path.name = name.getBytes();
            fileList.add(saveImage(path, delete));
        }
        return fileList;
    }

}
