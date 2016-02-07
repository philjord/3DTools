package com.jogamp.opengl.test.junit.graph.demos;

import com.jogamp.nativewindow.ScalableSurface;
import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLProfile;

import com.jogamp.graph.curve.Region;
import com.jogamp.newt.event.WindowAdapter;
import com.jogamp.newt.event.WindowEvent;
import com.jogamp.newt.opengl.GLWindow;
 
import com.jogamp.opengl.util.Animator;

public class GPUUISceneNewtDemo {
    static final boolean DEBUG = false;
    static final boolean TRACE = false;

    static int SceneMSAASamples = 0;
    static boolean GraphVBAAMode = false;
    static boolean GraphMSAAMode = false;
    static float GraphAutoMode = GPUUISceneGLListener0A.DefaultNoAADPIThreshold;

    static float[] reqSurfacePixelScale = new float[] { ScalableSurface.AUTOMAX_PIXELSCALE, ScalableSurface.AUTOMAX_PIXELSCALE };

    public static void main(final String[] args) {
        int width = 800, height = 400;
        int x = 10, y = 10;

        boolean forceES2 = false;
        boolean forceES3 = false;
        boolean forceGL3 = false;
        boolean forceGLDef = false;

      
        System.err.println("forceES2   "+forceES2);
        System.err.println("forceES3   "+forceES3);
        System.err.println("forceGL3   "+forceGL3);
        System.err.println("forceGLDef "+forceGLDef);
        System.err.println("Desired win size "+width+"x"+height);
        System.err.println("Desired win pos  "+x+"/"+y);
        System.err.println("Scene MSAA Samples "+SceneMSAASamples);
        System.err.println("Graph MSAA Mode "+GraphMSAAMode);
        System.err.println("Graph VBAA Mode "+GraphVBAAMode);
        System.err.println("Graph Auto Mode "+GraphAutoMode+" no-AA dpi threshold");

        final GLProfile glp;
        if(forceGLDef) {
            glp = GLProfile.getDefault();
        } else if(forceGL3) {
            glp = GLProfile.get(GLProfile.GL3);
        } else if(forceES3) {
            glp = GLProfile.get(GLProfile.GLES3);
        } else if(forceES2) {
            glp = GLProfile.get(GLProfile.GLES2);
        } else {
            glp = GLProfile.getGL2ES2();
        }
        System.err.println("GLProfile: "+glp);
        final GLCapabilities caps = new GLCapabilities(glp);
        caps.setAlphaBits(4);
        if( SceneMSAASamples > 0 ) {
            caps.setSampleBuffers(true);
            caps.setNumSamples(SceneMSAASamples);
        }
        System.out.println("Requested: " + caps);

        final int rmode;
        if( GraphVBAAMode ) {
            rmode = Region.VBAA_RENDERING_BIT;
        } else if( GraphMSAAMode ) {
            rmode = Region.MSAA_RENDERING_BIT;
        } else {
            rmode = 0;
        }

        final GLWindow window = GLWindow.create(caps);
        window.setPosition(x, y);
        window.setSize(width, height);
        window.setTitle("GraphUI Newt Demo: graph["+Region.getRenderModeString(rmode)+"], msaa "+SceneMSAASamples);
        window.setSurfaceScale(reqSurfacePixelScale);
        final float[] valReqSurfacePixelScale = window.getRequestedSurfaceScale(new float[2]);

        final GPUUISceneGLListener0A sceneGLListener = 0 < GraphAutoMode ? new GPUUISceneGLListener0A(GraphAutoMode, DEBUG, TRACE) :
                                                                     new GPUUISceneGLListener0A(rmode, DEBUG, TRACE);

        window.addGLEventListener(sceneGLListener);
        sceneGLListener.attachInputListenerTo(window);

        final Animator animator = new Animator();
        animator.setUpdateFPSFrames(60, System.err);
        animator.add(window);

        window.addWindowListener(new WindowAdapter() {
            public void windowDestroyed(final WindowEvent e) {
                animator.stop();
            }
        });

        window.setVisible(true);
        animator.start();
    }
}
