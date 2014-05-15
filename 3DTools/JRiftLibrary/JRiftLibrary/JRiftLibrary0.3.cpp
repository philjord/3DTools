#include "tools3d_ovr_OculusRift.h"
#include "OVR.h"	



#include <cstring>
#include <iostream>
 

// @void256 conio.h does not exist on OS X as far as I know
#ifndef __APPLE__
#include <conio.h>
#endif

using namespace OVR;
 

ovrHmd hmd;
ovrHmdDesc hmdDesc;	
bool Initialized = false;
ovrQuatf quaternion;
float yaw, pitch, roll;




JNIEXPORT jboolean JNICALL Java_tools3d_ovr_OculusRift03__1initSubsystem(JNIEnv *env, jobject jobj) 
{
	printf("Oculus Rift init.\n");
	// Initializes LibOVR.	
	if(ovr_Initialize())
	{
		hmd = ovrHmd_Create(0);
		if (hmd)
		{
			printf("Oculus Rift Device Interface created.\n");

			// Get more details about the HMD
			ovrHmd_GetDesc(hmd, &hmdDesc);

			printf(" Type: %i\n", hmdDesc.Type); 
			printf(" ProductName: %s\n", hmdDesc.ProductName); 
			printf(" Manufacturer: %s\n", hmdDesc.Manufacturer); 
			printf(" Caps: %i\n", hmdDesc.Caps); 
			printf(" DistortionCaps: %i\n", hmdDesc.DistortionCaps); 
			printf(" Resolution w: %i h:%i\n", hmdDesc.Resolution.w, hmdDesc.Resolution.h); 
			printf(" WindowPos w: %i h:%i\n", hmdDesc.WindowsPos.x, hmdDesc.WindowsPos.y); 
			printf(" DefaultEyeFov u:%f d:%f l:%f r:%f\n", hmdDesc.DefaultEyeFov[0].UpTan, hmdDesc.DefaultEyeFov[0].DownTan, hmdDesc.DefaultEyeFov[0].LeftTan, hmdDesc.DefaultEyeFov[0].RightTan); 
			printf(" MaxEyeFov u:%f d:%f l:%f r:%f\n",   hmdDesc.MaxEyeFov[0].UpTan, hmdDesc.MaxEyeFov[0].DownTan, hmdDesc.MaxEyeFov[0].LeftTan, hmdDesc.MaxEyeFov[0].RightTan); 
			printf(" EyeRenderOrder %i \n", hmdDesc.EyeRenderOrder ); 
			printf(" DisplayDeviceName %s \n", hmdDesc.DisplayDeviceName ); 

			//initialize the sensors with orientation, yaw correction, and position tracking capabilities
			//enabled if available, while actually requiring that only basic orientation tracking be present.
			if( ovrHmd_StartSensor(hmd, ovrHmdCap_Orientation | ovrHmdCap_YawCorrection |
				ovrHmdCap_Position | ovrHmdCap_LowPersistence,	ovrHmdCap_Orientation))
			{
				Initialized = true;
			}
			else
			{
				printf("ovrHmd_StartSensor returned false, required caps no supported,ovrHmdCap_Orientation?\n");
			}
		} 
		else
		{
			printf("ovrHmd_Create(0) return null.\n");
		}
	}
	else
	{
		printf("ovr_Initialize() Failed!.\n");
	}
	
	//flush the output to force writing
	fflush(stdout);
	return Initialized;
}

JNIEXPORT void JNICALL Java_tools3d_ovr_OculusRift03__1destroySubsystem(JNIEnv *env, jobject jobj) {
	printf("Shutdown Oculus Rift interface.\n");
	ovr_Shutdown();
}

JNIEXPORT void JNICALL Java_tools3d_ovr_OculusRift03__1pollSubsystem(JNIEnv *, jobject) {
	if (Initialized) 
	{
		// Query the HMD for the sensor state at a given time. "0.0" means "most recent time".
		//In a production application, however, you should use one
		//of the real-time computed values returned by ovrHmd_BeginFrame or ovrHmd_BeginFrameTiming.
		//TODO: improve time here
		ovrSensorState ss = ovrHmd_GetSensorState(hmd, 0.0);
		if (ss.StatusFlags & (ovrStatus_OrientationTracked | ovrStatus_PositionTracked)) // if either is present do ...
		{
			Posef pose = ss.Predicted.Pose;			
			pose.Orientation.GetEulerAngles<Axis_Y, Axis_X, Axis_Z>(&yaw, &pitch, &roll);
		}
	}
	else
	{
		printf("initSubsystem must be called first, no calls after destroySubsystem.\n");
	}

}

JNIEXPORT void JNICALL Java_tools3d_ovr_OculusRift03__1pollSubsystemDT(JNIEnv *, jobject, jfloat dt) {
	if (Initialized) 
	{ 

		//TODO: dt unused
		ovrSensorState ss = ovrHmd_GetSensorState(hmd, 0.0);
		if (ss.StatusFlags & (ovrStatus_OrientationTracked | ovrStatus_PositionTracked)) // if either is present do ...
		{
			Posef pose = ss.Predicted.Pose;			
			pose.Orientation.GetEulerAngles<Axis_Y, Axis_X, Axis_Z>(&yaw, &pitch, &roll);
		}
	}
	else
	{
		printf("initSubsystem must be called first, no calls after destroySubsystem.\n");
	}
}


JNIEXPORT jfloat JNICALL Java_tools3d_ovr_OculusRift03__1getYaw(JNIEnv *, jobject){
	if (Initialized) 
	{
		return yaw;
	}
	else
	{
		printf("initSubsystem must be called first, no calls after destroySubsystem.\n");
		return 0.0f;
	}
}

JNIEXPORT jfloat JNICALL Java_tools3d_ovr_OculusRift03__1getPitch(JNIEnv *, jobject){
	if (Initialized) 
	{
		return pitch;
	}
	else
	{
		printf("initSubsystem must be called first, no calls after destroySubsystem.\n");
		return 0.0f;
	}
	
}

JNIEXPORT jfloat JNICALL Java_tools3d_ovr_OculusRift03__1getRoll(JNIEnv *, jobject){
	if (Initialized) 
	{
		return roll;
	}
	else
	{
		printf("initSubsystem must be called first, no calls after destroySubsystem.\n");
		return 0.0f;
	}
}




JNIEXPORT void JNICALL Java_tools3d_ovr_OculusRift03__1reset(JNIEnv *, jobject) {
	if (Initialized) 
	{
		ovrHmd_ResetSensor(hmd);
	}
	else
	{
		printf("initSubsystem must be called first, no calls after destroySubsystem.\n");
	}
}

JNIEXPORT jobject JNICALL Java_tools3d_ovr_OculusRift03__1getHmdDesc(JNIEnv *env, jobject thisObj ) 
{
	if (Initialized) 
	{
		// Get a class reference for java.lang.Integer
		jclass cls = env->FindClass("tools3d/ovr/HmdDesc");
 
		// Get the Method ID of the constructor which takes an int
		jmethodID midInit = env->GetMethodID(cls, "<init>", "(ILjava/lang/String;Ljava/lang/String;IIIIIIFFFFFFFFILjava/lang/String;)V");
		if (NULL == midInit) return NULL;
	 
   
	  
		
		jobject newObj = env->NewObject( cls, midInit, 
			hmdDesc.Type
			, hmdDesc.ProductName
			, hmdDesc.Manufacturer
			, hmdDesc.Caps
			, hmdDesc.DistortionCaps
			, hmdDesc.Resolution.w, hmdDesc.Resolution.h
			, hmdDesc.WindowsPos.x, hmdDesc.WindowsPos.y
			, hmdDesc.DefaultEyeFov[0].UpTan
			, hmdDesc.DefaultEyeFov[0].DownTan
			, hmdDesc.DefaultEyeFov[0].LeftTan
			, hmdDesc.DefaultEyeFov[0].RightTan
			, hmdDesc.MaxEyeFov[0].UpTan
			, hmdDesc.MaxEyeFov[0].DownTan
			, hmdDesc.MaxEyeFov[0].LeftTan
			, hmdDesc.MaxEyeFov[0].RightTan
			, hmdDesc.EyeRenderOrder 
			, hmdDesc.DisplayDeviceName ); 
 
		return newObj;
	}
	else
	{
		printf("initSubsystem must be called first, no calls after destroySubsystem.\n");
		return NULL;
	}
}

 bool  VSyncEnabled = true;

  // ***** Oculus HMD Variables

    ovrHmd              Hmd;
    ovrHmdDesc          HmdDesc;
    ovrEyeRenderDesc    EyeRenderDesc[2];
    Matrix4f            Projection[2];      // Projection matrix for eye.
    Matrix4f            OrthoProjection[2]; // Projection for 2D.
    ovrTexture          EyeTexture[2];


JNIEXPORT jint JNICALL Java_tools3d_ovr_OculusRift03__1initRendering(JNIEnv *env, jobject thisObj ) 
{
	if (Initialized) 
	{


		// Configure Stereo settings.
		Sizei recommenedTex0Size = ovrHmd_GetFovTextureSize(hmd, ovrEye_Left,
		hmdDesc.DefaultEyeFov[0], 1.0f);
		Sizei recommenedTex1Size = ovrHmd_GetFovTextureSize(hmd, ovrEye_Right,
		hmdDesc.DefaultEyeFov[1], 1.0f);
		Sizei renderTargetSize;
		renderTargetSize.w = recommenedTex0Size.w + recommenedTex1Size.w;
		renderTargetSize.h = max( recommenedTex0Size.h, recommenedTex1Size.h );
		const int eyeRenderMultisample = 1;
		pRendertargetTexture = pRender->CreateTexture(Texture_RGBA | Texture_RenderTarget | eyeRenderMultisample,
		renderTargetSize.w, renderTargetSize.h, 0);
		// The actual RT size may be different due to HW limits.
		renderTargetSize.w = pRendertargetTexture->GetWidth();
		renderTargetSize.h = pRendertargetTexture->GetHeight();


		// Initialize eye rendering information for ovrHmd_Configure.
		// The viewport sizes are re-computed in case RenderTargetSize changed due to HW limitations.
		ovrEyeDesc eyes[2];
		eyes[0].Eye = ovrEye_Left;
		eyes[1].Eye = ovrEye_Right;
		eyes[0].Fov = hmdDesc.DefaultEyeFov[0];
		eyes[1].Fov = hmdDesc.DefaultEyeFov[1];
		eyes[0].TextureSize = renderTargetSize;
		eyes[1].TextureSize = renderTargetSize;
		eyes[0].RenderViewport.Pos = Vector2i(0,0);
		eyes[0].RenderViewport.Size = Sizei(renderTargetSize.w / 2, renderTargetSize.h);
		eyes[1].RenderViewport.Pos = Vector2i((renderTargetSize.w + 1) / 2, 0);
		eyes[1].RenderViewport.Size = eyes[0].RenderViewport.Size;

		// Configure OpenGL.
		ovrGLConfig cfg;
		cfg.OGL.Header.API = ovrRenderAPI_OpenGL;
		cfg.OGL.Header.RTSize = Sizei(hmdDesc.Resolution.w, hmdDesc.Resolution.h);
		cfg.OGL.Header.Multisample = backBufferMultisample;
		cfg.OGL.WglContext = WglContext;
		cfg.OGL.Window = Window;
		cfg.OGL.GdiDc = GdiDc;
		if (!ovrHmd_ConfigureRendering(hmd, &cfg.Config,(VSyncEnabled ? 0 : ovrHmdCap_NoVSync),	hmdDesc.DistortionCaps, eyes,  EyeRenderDesc))
			return(1);


		ovrGLTexture EyeTexture[2];
		Texture* rtt = (Texture*)pRendertargetTexture;
		EyeTexture[0].OGL.Header.API = ovrRenderAPI_OpenGL;
		EyeTexture[0].OGL.Header.TextureSize = RenderTargetSize;
		EyeTexture[0].OGL.Header.RenderViewport = eyes[0].RenderViewport;
		EyeTexture[0].OGL.TexId = textureId;
		// Right eye uses the same texture, but different rendering viewport.
		EyeTexture[1] = EyeTexture[0];
		EyeTexture[1].D3D11.Header.RenderViewport = eyes[1].RenderViewport;


	 }
	else
	{
		printf("initSubsystem must be called first, no calls after destroySubsystem.\n");
		return(1);
	}

	return(0);
}

JNIEXPORT jint JNICALL Java_tools3d_ovr_OculusRift03__1render(JNIEnv *env, jobject thisObj ) 
{
	if (Initialized) 
	{
		ovrFrameTiming hmdFrameTiming = ovrHmd_BeginFrame(hmd, 0);
		pRender->SetRenderTarget ( pRendertargetTexture );
		pRender->Clear();
		for (int eyeIndex = 0; eyeIndex < ovrEye_Count; eyeIndex++)
		{
			ovrEyeType eye = hmdDesc.EyeRenderOrder[eyeIndex];
			ovrPosef eyePose = ovrHmd_BeginEyeRender(hmd, eye);
			Quatf orientation = Quatf(eyePose.Orientation);
			Matrix4f proj = ovrMatrix4f_Projection(EyeRenderDesc[eye].Desc.Fov,
			0.01f, 10000.0f, true);
			// Test logic - assign quaternion result directly to view; TBD: Add translation.
			Matrix4f view = Matrix4f(orientation.Inverted()) * Matrix4f::Translation(-EyePos);
			pRender->SetViewport(EyeRenderDesc[eye].Desc.RenderViewport);
			pRender->SetProjection(proj);
			pRoomScene->Render(pRender, Matrix4f::Translation(EyeRenderDesc[eye].ViewAdjust) * view);
			ovrHmd_EndEyeRender(hmd, eye, eyePose, &EyeTexture[eye].Texture);
		}
		// Let OVR do distortion rendering, Present and flush/sync.
		ovrHmd_EndFrame(hmd);
	}