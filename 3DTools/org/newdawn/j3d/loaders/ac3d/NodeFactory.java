package org.newdawn.j3d.loaders.ac3d;

import javax.media.j3d.Shape3D;

/**
 * A factory to produce scene tree nodes while loading an AC3D model
 *
 * @author Kevin Glass
 * @version $Revision: 1.1 $
 */
public interface NodeFactory {

    /**
     * Create a shape 3d object
     *
     * @return The created shaped 3d object
     */
    public Shape3D createShape3D();
        
    /**
     * "Finish off" a shape
     *
     * @param shape The shape to finish off
     */
    public void finishOffShape(Shape3D shape);
}

