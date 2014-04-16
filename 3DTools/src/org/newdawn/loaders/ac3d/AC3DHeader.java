package org.newdawn.loaders.ac3d;

/**
 * Data holder for the ac3d header information
 *
 * @author  $Author: jeremy $
 * @version  £Revision$
 */
public class AC3DHeader {

    /** The filetype */
    private String filetype;
    /** The version id of the file */
    private int version;

    /**
     * Creates new AC3DHeader
     *
     * @param filetype The filetype (should always be "AC3D")
     * @param version The version id of the file
     */
    public AC3DHeader(String filetype, int version) {
        this.filetype = new String(filetype);
        this.version = version;
        //System.out.println("Created an AC3DHeader, Type: " + filetype + " Version: " + version);
    }
    
    /**
     * Returns the filetype
     *
     * @return The filetype
     */
    public String getFiletype(){
        return new String(filetype);
    }
    
    /**
     * Returns the version number
     *
     * @return The version number
     */
    public int getVersion(){
        return version;
    }
}
