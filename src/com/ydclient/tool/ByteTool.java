package com.ydclient.tool;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;

import android.graphics.Bitmap;

/**
 * 
 * @ClassName ByteTool
 * @Description byte处理工具
 * @author ouArea
 * @date 2012-11-15
 */
public class ByteTool {
	/**
	 * 
	 * @Title: shortToByteArray
	 * @Description: short 转 byte数组
	 * @param s
	 * @return
	 * @author: ouArea
	 * @return byte[]
	 * @throws
	 */
	public static byte[] shortToByteArray(short s) {
		byte[] targets = new byte[2];
		for (int i = 0; i < targets.length; i++) {
			int offset = (targets.length - 1 - i) * 8;
			targets[i] = (byte) ((s >>> offset) & 0xff);
		}
		return targets;
	}

	/**
	 * 
	 * @Title: byteToShort
	 * @Description: byte数组 转 short
	 * @param bytes
	 * @return
	 * @author: ouArea
	 * @return short
	 * @throws
	 */
	public static short byteToShort(byte[] bytes, int offset) {
		return (short) (bytes[offset] + 256 * bytes[offset + 1]);
	}

	/**
	 * byteToInt大小端
	 * 
	 * @Title: byteToIntDX
	 * @Description: TODO
	 * @param bytes
	 * @param offset
	 * @return
	 * @author: ouArea
	 * @return int
	 * @throws
	 */
	public static int byteToIntDX(byte[] bytes, int offset) {
		return (bytes[offset] & 0xff) + ((bytes[offset + 1] & 0xff) * 256);
	}

	/**
	 * byte转为十六进制
	 * 
	 * @Title: bytes2Hex
	 * @Description: TODO
	 * @param bts
	 * @return
	 * @author: ouArea
	 * @return String
	 * @throws
	 */
	public static String bytes2Hex(byte[] bts) {
		StringBuffer sb = new StringBuffer();
		String tmp = null;
		for (int i = 0; i < bts.length; i++) {
			tmp = (Integer.toHexString(bts[i] & 0xFF));
			if (tmp.length() == 1) {
				sb.append("0");
			}
			sb.append(tmp);
		}
		return sb.toString();
	}

	/**
	 * SHA-1加密
	 * 
	 * @Title: SHA_1
	 * @Description: TODO
	 * @param str
	 * @return
	 * @author: ouArea
	 * @return String
	 * @throws
	 */
	public static String SHA_1(String str) {
		byte[] bytes = str.getBytes();
		MessageDigest messageDigest;
		try {
			messageDigest = MessageDigest.getInstance("SHA-1");
			messageDigest.update(bytes);
			return bytes2Hex(messageDigest.digest());
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Bitmap转化为Bytes
	 * 
	 * @Title: Bitmap2Bytes
	 * @Description: TODO
	 * @param bm
	 * @return
	 * @author: ouArea
	 * @return byte[]
	 * @throws
	 */
	public static byte[] bitmap2BytesJPEG(Bitmap bm) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bm.compress(Bitmap.CompressFormat.JPEG, 100, baos);
		return baos.toByteArray();
	}

	// ----------------------------------------------------------------------
	/**
	 * 压缩
	 * 
	 * @param data
	 *            待压缩数据
	 * @return byte[] 压缩后的数据
	 */
	public static byte[] compress(byte[] data) {
		byte[] output = new byte[0];

		Deflater compresser = new Deflater();

		compresser.reset();
		compresser.setInput(data);
		compresser.finish();
		ByteArrayOutputStream bos = new ByteArrayOutputStream(data.length);
		try {
			byte[] buf = new byte[1024];
			while (!compresser.finished()) {
				int i = compresser.deflate(buf);
				bos.write(buf, 0, i);
			}
			output = bos.toByteArray();
		} catch (Exception e) {
			output = data;
			e.printStackTrace();
		} finally {
			try {
				bos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		compresser.end();
		return output;
	}

	/**
	 * 压缩
	 * 
	 * @param data
	 *            待压缩数据
	 * 
	 * @param os
	 *            输出流
	 */
	public static void compress(byte[] data, OutputStream os) {
		DeflaterOutputStream dos = new DeflaterOutputStream(os);

		try {
			dos.write(data, 0, data.length);

			dos.finish();

			dos.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 解压缩
	 * 
	 * @param data
	 *            待压缩的数据
	 * @return byte[] 解压缩后的数据
	 */

	public static byte[] decompress(byte[] data) {
		byte[] output = new byte[0];
		Inflater decompresser = new Inflater();
		decompresser.reset();
		decompresser.setInput(data);
		ByteArrayOutputStream o = new ByteArrayOutputStream(data.length);
		try {
			byte[] buf = new byte[1024];
			while (!decompresser.finished()) {
				int i = decompresser.inflate(buf);
				o.write(buf, 0, i);
			}
			output = o.toByteArray();
		} catch (Exception e) {
			output = data;
			e.printStackTrace();
		} finally {
			try {
				o.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		decompresser.end();
		return output;
	}

	/**
	 * 解压缩
	 * 
	 * @param is
	 *            输入流
	 * @return byte[] 解压缩后的数据
	 */

	public static byte[] decompress(InputStream is) {
		InflaterInputStream iis = new InflaterInputStream(is);
		ByteArrayOutputStream o = new ByteArrayOutputStream(1024);
		try {
			int i = 1024;
			byte[] buf = new byte[i];
			while ((i = iis.read(buf, 0, i)) > 0) {
				o.write(buf, 0, i);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return o.toByteArray();
	}
}
