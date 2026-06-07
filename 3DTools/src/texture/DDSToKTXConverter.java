package texture;

import java.io.IOException;
import java.io.InputStream;
import java.nio.Buffer;
import java.nio.ByteBuffer;

import org.jogamp.java3d.NioImageBuffer;
import org.jogamp.java3d.NioImageBuffer.ImageType;
import org.jogamp.java3d.compressedtexture.CompressedTextureLoader;

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
			DDSDecompressorExt decomp = new DDSDecompressorExt(ddsImage, 0, filename);
			NioImageBuffer decompressedImage = decomp.convertImageNio();
			// can be null if the dds image format is a later freaky one
			if (decompressedImage != null) {
				Buffer b = decompressedImage.getDataBuffer();
				if (b instanceof ByteBuffer) {

					ImageType decompressedImageType = decompressedImage.getImageType();
					FORMAT etcFormat = null;
					ByteBuffer bb = (ByteBuffer)decompressedImage.getDataBuffer();
					byte[] img = null;
					byte[] imgalpha = null;

					if (decompressedImageType == NioImageBuffer.ImageType.TYPE_3BYTE_RGB) {
						// just put the RGB data straight into the img byte array 
						img = new byte[bb.capacity()];
						bb.get(img, 0, bb.capacity());

						etcFormat = FORMAT.ETC2PACKAGE_RGB;
					} else if (decompressedImageType == NioImageBuffer.ImageType.TYPE_4BYTE_RGBA) {
						// copy RGB 3 sets out then 1 sets of alpha 
						img = new byte[(bb.capacity() / 4) * 3];
						imgalpha = new byte[(bb.capacity() / 4)];
						for (int i = 0; i < img.length / 3; i++) {
							img[i * 3 + 0] = bb.get();
							img[i * 3 + 1] = bb.get();
							img[i * 3 + 2] = bb.get();
							imgalpha[i] = bb.get();
						}

						etcFormat = FORMAT.ETC2PACKAGE_RGBA;
						// special type change here
						if (ddsImage.getPixelFormat() == DDSImage.D3DFMT_DXT1 && !decomp.decompressedIsOpaque()) {
							etcFormat = FORMAT.ETC2PACKAGE_RGBA1;
						}
					} else if (decompressedImageType == NioImageBuffer.ImageType.TYPE_BYTE_GRAY) {
						//FIXME let signed data come through too, ETCPACK has this comment //Depends on the global variable formatSigned.

						// copy RGB from the 1 byte of L8 data and use RGB (FORMAT.ETC2PACKAGE_R is an odd 16 bit thing)
						img = new byte[(bb.capacity() / 1) * 3];
						for (int i = 0; i < img.length / 3; i++) {
							byte byt = bb.get();
							img[i * 3 + 0] = byt;
							img[i * 3 + 1] = byt;
							img[i * 3 + 2] = byt;
						}

						etcFormat = FORMAT.ETC2PACKAGE_RGB;

						//FIXME: when FORMAT.ETC2PACKAGE_R for QuickETC is fast enough change to use that as below
						//ETC2PACKAGE_R expects 16 bits in the alpha array
						/*imgalpha = new byte[bb.capacity() * 2];
						for (int i = 0; i < imgalpha.length/2; i++) {
							byte byt = bb.get();
							imgalpha[i * 2 + 0] = byt;
							imgalpha[i * 2 + 1] = 0;// we don't get 16bits of return data, pity							
						}
						
						etcFormat = FORMAT.ETC2PACKAGE_R;*/
						// textures\dlc_oldmars\weapons\lasercutter\dd_lasercutter_oldmars\lasercutter_battery_ao.dds
					} else if (decompressedImageType == NioImageBuffer.ImageType.TYPE_BYTE_RG) {
						//currently only BC5Us and S creates this guy
						//FIXME let signed data come through too, ETCPACK has this comment //Depends on the global variable formatSigned.

						img = new byte[(bb.capacity() / 2) * 3];
						for (int i = 0; i < img.length / 3; i++) {
							img[i * 3 + 0] = bb.get();
							img[i * 3 + 1] = bb.get();
							img[i * 3 + 2] = 0;
						}

						etcFormat = FORMAT.ETC2PACKAGE_RGB;

						//FIXME: when FORMAT.ETC2PACKAGE_R for QuickETC is fast enough change to use that as below

						/*img = new byte[bb.capacity() * 4];
						for (int i = 0; i < img.length / 4; i++) {
							byte byt1 = bb.get();
							byte byt2 = bb.get();
							img[i * 4 + 0] = byt1;
							img[i * 4 + 1] = 0;// we don't get 16bits of return data, pity
							img[i * 4 + 2] = byt2;
							img[i * 4 + 3] = 0;
						}
						
						etcFormat = FORMAT.ETC2PACKAGE_RG; */
					} else {
						System.err.println("Bad Image Type " + decompressedImage.getImageType() + " in " + filename);
						return null;
					}

					//System.out.println("Debug of dds image " + filename);
					//ddsImage.debugPrint();

					//public static enum FORMAT{ETC1_RGB,ETC2PACKAGE_RGBA_OLD,//no ETC1 or old format
					// these guys are the basics
					//ETC2PACKAGE_RGB,ETC2PACKAGE_RGBA,ETC2PACKAGE_RGBA1,ETC2PACKAGE_R,ETC2PACKAGE_RG,
					//actually modifies the base number in each 4x4, no idea why it's gooder?
					//ETC2PACKAGE_R_SIGNED,ETC2PACKAGE_RG_SIGNED,
					//sRGB is just a signal to the openGL, treated identically to non sRGB
					//ETC2PACKAGE_sRGB,ETC2PACKAGE_sRGBA,ETC2PACKAGE_sRGBA1};

					ByteBuffer ktxBB = null;
					QuickETC ep = new QuickETC();

					ktxBB = ep.compressImageToByteBuffer(img, imgalpha, ddsImage.getWidth(), ddsImage.getHeight(),
							etcFormat, ddsImage.getNumMipMaps() > 1);

					return ktxBB;
				}
			}
		}
		return null;

	}
}
