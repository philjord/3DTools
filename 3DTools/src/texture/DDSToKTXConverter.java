package texture;

import java.io.IOException;
import java.io.InputStream;
import java.nio.Buffer;
import java.nio.ByteBuffer;

import org.jogamp.java3d.NioImageBuffer;
import org.jogamp.java3d.compressedtexture.CompressedTextureLoader;
import org.jogamp.java3d.compressedtexture.dktxtools.dds.DDSDecompressor;

import compressedtexture.DDSImage;
import etcpack.ETCPack.FORMAT;
import etcpack.QuickETC;

public class DDSToKTXConverter {

	public static ByteBuffer convertDDSToKTX(InputStream inputStream, String filename) {
		try {
			DDSImage ddsImage = DDSImage.read(CompressedTextureLoader.toByteBuffer(inputStream));
			return convertDDSToKTX(ddsImage, filename);
		} catch (IOException e) {
			System.out.println("convertDDSToKTX had an IO problem with " + filename + " : " + e.getMessage());
			return null;
		}
	}
	public static ByteBuffer convertDDSToKTX(ByteBuffer inputBuffer, String filename) {
		try {
			DDSImage ddsImage = DDSImage.read(inputBuffer);
			return convertDDSToKTX(ddsImage, filename);
		} catch (IOException e) {
			System.out.println("convertDDSToKTX had an IO problem with " + filename + " : " + e.getMessage());
			return null;
		}
	}
	
	public static ByteBuffer convertDDSToKTX(DDSImage ddsImage, String filename) {
		if (ddsImage != null) {
			DDSDecompressor decomp = new DDSDecompressor(ddsImage, 0, filename);
			NioImageBuffer decompressedImage = decomp.convertImageNio();
			Buffer b = decompressedImage.getDataBuffer();
			if (b instanceof ByteBuffer) {
				//ok so now find the RGB or RGBA byte buffers
				ByteBuffer bb = (ByteBuffer)decompressedImage.getDataBuffer();
				byte[] img = null;
				byte[] imgalpha = null;
				if (decompressedImage.getImageType() == NioImageBuffer.ImageType.TYPE_3BYTE_RGB) {
					// just put the RGB data straight into the img byte array 
					img = new byte[bb.capacity()];
					bb.get(img, 0, bb.capacity());
				} else if (decompressedImage.getImageType() == NioImageBuffer.ImageType.TYPE_4BYTE_RGBA) {
					// copy RGB 3 sets out then 1 sets of alpha 
					img = new byte[(bb.capacity() / 4) * 3];
					imgalpha = new byte[(bb.capacity() / 4)];
					for (int i = 0; i < img.length / 3; i++) {
						img[i * 3 + 0] = bb.get();
						img[i * 3 + 1] = bb.get();
						img[i * 3 + 2] = bb.get();
						imgalpha[i] = bb.get();
					}
				} else if (decompressedImage.getImageType() == NioImageBuffer.ImageType.TYPE_BYTE_GRAY) {
					// copy RGB from the 1 byte of L8 data and use RGB (FORMAT.ETC2PACKAGE_R is odd 16 bit thing)
					img = new byte[bb.capacity() * 3];
					for (int i = 0; i < img.length / 3; i++) {
						byte byt = bb.get();
						img[i * 3 + 0] = byt;
						img[i * 3 + 1] = byt;
						img[i * 3 + 2] = byt;
					}
				} else {
					System.err.println("Bad Image Type " + decompressedImage.getImageType() + " in " + filename);
					return null;
				}

				//System.out.println("Debug of dds image " + filename);
				//ddsImage.debugPrint();
				int fmt = ddsImage.getPixelFormat();
				FORMAT format = FORMAT.ETC2PACKAGE_RGBA;

				if (fmt == DDSImage.D3DFMT_R8G8B8) {
					format = FORMAT.ETC2PACKAGE_RGB;
				} else if (fmt == DDSImage.D3DFMT_A8R8G8B8 || fmt == DDSImage.D3DFMT_X8R8G8B8 || fmt == DDSImage.D3DFMT_A8B8G8R8) {
					format = FORMAT.ETC2PACKAGE_RGBA;
				} else if (fmt == DDSImage.D3DFMT_DXT1) {
					if (!decomp.decompressedIsOpaque()) {
						format = FORMAT.ETC2PACKAGE_RGBA1;
					} else {
						format = FORMAT.ETC2PACKAGE_RGB;
					}
				} else if (fmt == DDSImage.D3DFMT_DXT2	|| fmt == DDSImage.D3DFMT_DXT3 || fmt == DDSImage.D3DFMT_DXT4
							|| fmt == DDSImage.D3DFMT_DXT5) {
					if (!decomp.decompressedIsOpaque()) {
						format = FORMAT.ETC2PACKAGE_RGBA;
					} else {
						format = FORMAT.ETC2PACKAGE_RGB;
					}
				} else if (fmt == DDSImage.D3DFMT_L8) {
					format = FORMAT.ETC2PACKAGE_RGB;
				}

				//TODO, perhaps normal maps (_n) should be forcibly set to RGB (not sRGBA)??
				if (filename.indexOf("_n.dds") > 0)
					format = FORMAT.ETC2PACKAGE_RGB;

				ByteBuffer ktxBB = null;
				QuickETC ep = new QuickETC();
				//ETCPack ep = new ETCPack();

				ktxBB = ep.compressImageToByteBuffer(img, imgalpha, ddsImage.getWidth(), ddsImage.getHeight(), format,
						true);

				return ktxBB;
			}
		}
		return null;

	}
}
