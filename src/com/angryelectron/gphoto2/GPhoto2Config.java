/**
 * GPhoto2Config
 * @author ZIPTREK\abythell
 * (C) 2012 Ziptrek Ecotours
 */ 

package com.angryelectron.gphoto2;

import com.angryelectron.libgphoto2.Camera;
import com.angryelectron.libgphoto2.Gphoto2Library;
import com.angryelectron.libgphoto2.Gphoto2Library.CameraWidget;
import com.angryelectron.libgphoto2.Gphoto2Library.CameraWidgetType;
import com.angryelectron.libgphoto2.Gphoto2Library.GPContext;
import com.sun.jna.Memory;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.PointerByReference;
import java.io.IOException;
import java.nio.IntBuffer;
import java.util.Date;

/**
 * Read and write camera settings. Once all the settings have been retrieved from
 * the camera using readConfig(), get or set as many parameters as necessary, then
 * write any updated parameters back to the camera with writeConfig()
 */
public class GPhoto2Config {
          
    private Gphoto2Library gphoto2;
    private GPContext context;
    private Camera camera;    
    private CameraWidget cameraWidget;
    
    /**
     * Constructor.
     * @param g An open GPhoto2 object.
     */
    public GPhoto2Config(GPhoto2 g) {        
        this.gphoto2 = g.gphoto2;
        this.context = g.context;
        this.camera = g.camera;        
    }
    
    /**
     * Verify return codes.  If the return code is not 
     * GP_OK, an IO Exception is thrown.  Used to verify every libgphoto2 call.
     * @param msg A message to include with the exception
     * @param rc The return code to validate.
     * @throws IOException 
     */
    private void validateResult(String msg, int rc) throws IOException {        
        if (rc != Gphoto2Library.GP_OK) {
            throw new IOException(msg + "failed with code " + rc);
        }        
    }
    
    /**
     * Retrieve the CameraWidget for the given parameter.
     * @param param Name of the parameter
     * @return CameraWidget object.
     * @throws IOException If parameter is invalid or cannot be retrieved.
     */
    private CameraWidget getParameterWidget(String param) throws IOException {        
        Pointer name = new Memory(param.length() + 1);
        name.setString(0, param);
        PointerByReference refChild = new PointerByReference();
        int rc = gphoto2.gp_widget_get_child_by_name(cameraWidget, name, refChild);
        validateResult("gp_widget_get_child_by_name", rc);
        return new Gphoto2Library.CameraWidget(refChild.getValue());              
    }
        
    /**
     * Retrieve a parameter's value.  The value type depends on the parameter 
     * type.  This method retrieves the value using the correct type, then
     * converts it to a string.
     * @param paramWidget A CameraWidget representing the parameter to be read.
     * @return A String representing the parameter's value.
     * @throws IOException If the value cannot be read.
     */
    private String getParameterValue(CameraWidget paramWidget) throws IOException {        
        
        PointerByReference pValue = new PointerByReference();
        int rc = gphoto2.gp_widget_get_value(paramWidget, pValue);
        validateResult("gp_widget_get_value", rc);
        
        IntBuffer type = IntBuffer.allocate(4);
        rc = gphoto2.gp_widget_get_type(paramWidget, type);
        validateResult("gp_widget_get_type", rc);        
        switch (type.get()) {
            case CameraWidgetType.GP_WIDGET_MENU:
            case CameraWidgetType.GP_WIDGET_TEXT:
            case CameraWidgetType.GP_WIDGET_RADIO:                                
                return pValue.getValue().getString(0);                
            case CameraWidgetType.GP_WIDGET_RANGE:
                Float f = pValue.getValue().getFloat(0);
                return f.toString();
            case CameraWidgetType.GP_WIDGET_DATE:
                long l = pValue.getValue().getLong(0) * 1000;
                Date d = new Date(l);
                return d.toString();
            case CameraWidgetType.GP_WIDGET_TOGGLE:                             
                Integer i = pValue.getValue().getInt(0);
                return i.toString();
            default:
                throw new UnsupportedOperationException("Unsupported CameraWidgetType");
        }        
    }
     
    /**
     * Set the value of a parameter.  This method will convert the value into the
     * appropriate widget type.
     * @param paramWidget A CameraWidget representing the parameter to be set.
     * @param value The new value for the parameter.
     * @throws IOException If the parameter cannot be set.
     */
    private void setParameterValue(CameraWidget paramWidget, String value) throws IOException {
        Pointer pValue = null;
        IntBuffer type = IntBuffer.allocate(4);
        int rc = gphoto2.gp_widget_get_type(paramWidget, type);
        validateResult("gp_widget_get_type", rc);
        
        switch (type.get()) {
            case CameraWidgetType.GP_WIDGET_MENU:
            case CameraWidgetType.GP_WIDGET_TEXT:
            case CameraWidgetType.GP_WIDGET_RADIO:                
                //char *
                pValue = new Memory(value.length() + 1);
                pValue.setString(0, value);
                break;
            case CameraWidgetType.GP_WIDGET_RANGE:
                //floats are 32-bits or 4 bytes
                float fValue = Float.parseFloat(value);
                pValue = new Memory(4);
                pValue.setFloat(0, fValue);
                break;
            case CameraWidgetType.GP_WIDGET_DATE:
            case CameraWidgetType.GP_WIDGET_TOGGLE:             
                //ints are 32-bits or 4 bytes
                int iValue = Integer.parseInt(value);
                pValue = new Memory(4);
                pValue.setInt(0, iValue);
                break;
            default:
                throw new UnsupportedOperationException("Unsupported CameraWidgetType");
        }
        rc = gphoto2.gp_widget_set_value(paramWidget, pValue);
        validateResult("gp_widget_set_value", rc);        
    }
    
    /**
     * Read the camera's current configuration.  This must be called before
     * getting or setting any parameters.
     * @throws IOException If the configuration cannot be read.
     */
    public void readConfig() throws IOException {
        PointerByReference refWidget = new PointerByReference();                        
        int rc = gphoto2.gp_camera_get_config(camera, refWidget, context);
        validateResult("gp_camera_get_config", rc);
        cameraWidget = new Gphoto2Library.CameraWidget(refWidget.getValue());                        
    }
    
    /**
     * Write the current settings to the camera.  This must be called after any 
     * setParameter() calls to save the new settings to the camera.  It can be
     * called just once after setting several parameters.
     * @throws IOException If the settings cannot be written.
     */
    public void writeConfig() throws IOException {
        int rc = gphoto2.gp_camera_set_config(camera, cameraWidget, context);
        validateResult("gp_camera_set_config", rc);
    }
    
    /**
     * Set a new value for a parameter.  A list of parameters can be viewed by
     * running 'gphoto2 --list-config'.  To view a list of valid options for a
     * parameter, run 'gphoto2 --get-config <parameter>'.  Note that "Choices" are
     * numbered, with the value appearing last (ie. when setting "evstep", 
     * use "1/3" for "Choice: 0 1/3").  Strings are case sensitive.
     * @param param The parameter to set.  If getting parameters via --list-config,
     * do not specify the entire path (ie. use 'iso', not '/main/imgsettings/iso').
     * @param value The value to set.
     * @throws IOException If the parameter cannot be set.
     */
    public void setParameter(String param, String value) throws IOException {        
        CameraWidget paramWidget = getParameterWidget(param);
        setParameterValue(paramWidget, value);                
    }
    
    /**
     * Get the value of a parameter. A list of parameters can be viewed by
     * running 'gphoto2 --list-config'. 
     * @param param The parameter to retrieve.
     * @return The value of the parameter, as a string.
     * @throws IOException If the parameter cannot be read.
     */
    public String getParameter(String param) throws IOException {
        CameraWidget paramWidget = getParameterWidget(param);
        return getParameterValue(paramWidget);
    }
}
