package org.newdawn.j3d.loaders.ac3d.tree;

import java.util.ArrayList;
import javax.media.j3d.Geometry;

/**
 * Node representing some geomtry in the tree, including the multi key to it
 *
 * @author $Author: jeremy $
 * @version $Revision: 1.1 $
 */
public class GeometryNode {
    
    /** The geometrys */
    private ArrayList<Geometry> geometrys = new ArrayList<Geometry>();
    /** Indicates the sidedness of geometry in this node*/
    private boolean isTwoSided;
    /** Indicades if the geometry is a line or polygon*/
    private boolean isLine;
    /** Index of the material this geometry should have applied*/
    private int materialIndex;
    /** The name of the textures that all surfaces share in*/
    private String textureName;

    /**
     * Creates new GeometryNode
     *
     * @param isTwoSided Indicates the sidedness of geometry in this node
     * @param isLine Indicades if the geometry is a line or polygon
     * @param materialIndex Index of the material this geometry should have applied
     * @param textureName the name of the textures that all surfaces share in
     *      this node
     */
    public GeometryNode(boolean isTwoSided, boolean isLine, int materialIndex, String textureName) {
        this.isTwoSided = isTwoSided;
        this.isLine = isLine;
        this.materialIndex = materialIndex;
        this.textureName = textureName;
    }
    
    /**
     * Adds a geometry to this node
     *
     * @param geometry The geometry to add
     */
    public void addGeometry(Geometry geometry){
        geometrys.add(geometry);
    }
    
    /**
     * Gets the geometrys in this node
     *
     * @return The geometrys
     */
    public ArrayList<Geometry> getGeometrys(){
        return geometrys;
    }

    /** 
     * Returns indication of sidedness
     *
     * @return True if the geometry is two sided
     */    
    public boolean isTwoSided() {
        return isTwoSided;
    }
    
    /** 
     * Indication of if this geometry is a line
     *
     * @return True if this geomtry is a line
     */    
    public boolean isLine() {
        return isLine;
    }
    
    /** 
     * Gets the index of the material to apply to this geometry
     *
     * @return The index of the material to apply
     */    
    public int getMaterialIndex() {
        return materialIndex;
    }
    
    /**
     * Gets the texture name to apply to this geomtry
     *
     * @return The texture name
     */    
    public String getTextureName() {
        return textureName;
    }
}
