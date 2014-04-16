package org.newdawn.j3d.loaders.ac3d.tree;

/**
 * A non unique key that is used to identify geometry node types
 *
 * @author $Author: jeremy $
 * @version $Revision: 1.1 $
 */
public class GeometryNodeKey {
    
    /** Indicates the sidedness of geometry in this node*/
    private boolean isTwoSided;
    /** Indicades if the geometry is a line or polygon*/
    private boolean isLine;
    /** Index of the material this geometry should have applied*/
    private int materialIndex;
    /** The name of the textures that all surfaces share in*/
    private String textureName;

    /**
     * Creates new GeometryNodeKey
     *
     * @param isTwoSided Indicates the sidedness of geometry in this node type
     * @param isLine Indicades if the geometry in this type is a line or polygon
     * @param materialIndex Index of the material this geometry type should have applied
     * @param textureName the name of the textures that all surfaces share in
     *     this node type
     */
    public GeometryNodeKey(boolean isTwoSided, boolean isLine, int materialIndex, String textureName) {
        this.isTwoSided = isTwoSided;
        this.isLine = isLine;
        this.materialIndex = materialIndex;
        this.textureName = textureName;
    }
    
    /**
     * Copares the provided object with this node type and returns true if it is
     * equal
     *
     * @param object The object to compare to
     * @return True if this node mathes the provided object
     */    
    public boolean equals(Object object) {
        GeometryNodeKey compareKey;
        if(! (object instanceof GeometryNodeKey)){
            return false;
        } else {
            compareKey = (GeometryNodeKey) object;
        }
        if(isTwoSided != compareKey.isTwoSided){
            return false;
        }
        if(isLine!=compareKey.isLine){
            return false;
        }
        if(materialIndex!=compareKey.materialIndex){
            return false;
        }
        if(textureName == null) {
            if(compareKey.textureName!=null){
                return false;
            }
        } else {
            if(compareKey.textureName == null){
                return false;
            }
            if(!(textureName.equals(compareKey.textureName))) {
                return false;
            }
        }
        return true;
    }
    
    /**
     * Returns the hash code of this node type
     *
     * @return The hash code
     */    
    public int hashCode() {
        return toString().hashCode();
    }
    
    /**
     * Returns this key as a string, usefull for debugging
     * @return String describing the type
     */    
    public String toString(){
        return new String("isTwoSided: " + isTwoSided + " isLine: " + isLine + " material: "  + materialIndex + " textureName: " + textureName);
    }
    
}
