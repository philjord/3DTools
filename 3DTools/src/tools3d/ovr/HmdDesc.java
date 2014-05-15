package tools3d.ovr;

public class HmdDesc
{
	public int type;

	public String productName;

	public String manufacturer;

	public int caps;

	public int distortionCaps;

	public int resolutionW;

	public int resolutionH;

	public int windowsPosX;

	public int windowsPosY;

	public float defaultEyeFovU;

	public float defaultEyeFovD;

	public float defaultEyeFovL;

	public float defaultEyeFovR;

	public float maxEyeFovU;

	public float maxEyeFovD;

	public float maxEyeFovL;

	public float maxEyeFovR;

	public int eyeRenderOrder;

	public String displayDeviceName;

	public HmdDesc(int type, String productName, String manufacturer, int caps, int distortionCaps, int resolutionW, int resolutionH,
			int windowsPosX, int windowsPosY, 
			float defaultEyeFovU, float defaultEyeFovD, float defaultEyeFovL, float defaultEyeFovR,
			float maxEyeFovU, float maxEyeFovD, float maxEyeFovL, float maxEyeFovR, 
			int eyeRenderOrder, String displayDeviceName)
	{
		this.type = type;
		this.productName = productName;
		this.manufacturer = manufacturer;
		this.caps = caps;
		this.distortionCaps = distortionCaps;
		this.resolutionW = resolutionW;
		this.resolutionH = resolutionH;
		this.windowsPosX = windowsPosX;
		this.windowsPosY = windowsPosY;
		this.defaultEyeFovU = defaultEyeFovU;
		this.defaultEyeFovD = defaultEyeFovD;
		this.defaultEyeFovL = defaultEyeFovL;
		this.defaultEyeFovR = defaultEyeFovR;
		this.maxEyeFovU = maxEyeFovU;
		this.maxEyeFovD = maxEyeFovD;
		this.maxEyeFovL = maxEyeFovL;
		this.maxEyeFovR = maxEyeFovR;
		this.eyeRenderOrder = eyeRenderOrder;
		this.displayDeviceName = displayDeviceName;

	}
}
