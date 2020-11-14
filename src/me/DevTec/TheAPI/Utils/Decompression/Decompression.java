package me.DevTec.TheAPI.Utils.Decompression;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.util.Enumeration;
import java.util.zip.DataFormatException;
import java.util.zip.GZIPInputStream;
import java.util.zip.Inflater;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class Decompression {
	public static int DECOMPRESS = 2;

	public static byte[] decompress(byte[] in) {
		Inflater decompressor = new Inflater(true);
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		for (int isf = 0; isf < DECOMPRESS; ++isf) {
			decompressor.setInput(in);
			byte[] buf = new byte[1024];
			while (!decompressor.finished())
				try {
					bos.write(buf, 0, decompressor.inflate(buf));
				} catch (DataFormatException e) {
				}
			decompressor.reset();
			in = bos.toByteArray();
			bos.reset();
		}
		return in;
	}

	public static InputStream unZip(File file) {
		try {
			ZipFile zip = new ZipFile(file);
			ZipEntry entry = null;
			for (Enumeration<?> e = zip.entries(); e.hasMoreElements();)
				entry = (ZipEntry) e.nextElement();
			InputStream in = zip.getInputStream(entry);
			zip.close();
			return in;
		} catch (Exception e) {
		}
		return null;

	}

	public static StringBuffer getText(InputStream in) {
		StringBuffer out = new StringBuffer();
		BufferedReader reader = new BufferedReader(new InputStreamReader(in));
		String line;
		try {
			while ((line = reader.readLine()) != null)
				out.append(line);
		} catch (Exception e) {
		}
		return out;
	}

	public static class Decompressor {
		private ByteArrayInputStream end;
		private GZIPInputStream decompressor;
		private ObjectInputStream get;

		public Decompressor(byte[] toDecompress) {
			try {
				end = new ByteArrayInputStream(toDecompress);
				decompressor = new GZIPInputStream(end);
				get = new ObjectInputStream(decompressor);
			} catch (Exception e) {
			}
		}

		public Object readObject() {
			try {
				get.readObject();
			} catch (Exception e) {
			}
			return null;
		}

		public String readString() {
			try {
				get.readUTF();
			} catch (Exception e) {
			}
			return null;
		}

		public String readUTF() {
			return readString();
		}

		public boolean readBoolean() {
			try {
				return get.readBoolean();
			} catch (Exception e) {
			}
			return false;
		}

		public float readFloat() {
			try {
				return get.readFloat();
			} catch (Exception e) {
			}
			return 0;
		}

		public int readInt() {
			try {
				return get.readInt();
			} catch (Exception e) {
			}
			return 0;
		}

		public byte readByte() {
			try {
				return get.readByte();
			} catch (Exception e) {
			}
			return 0;
		}

		public double readDouble() {
			try {
				return get.readDouble();
			} catch (Exception e) {
			}
			return 0;
		}

		public long readLong() {
			try {
				return get.readLong();
			} catch (Exception e) {
			}
			return 0;
		}

		public short readShort() {
			try {
				return get.readShort();
			} catch (Exception e) {
			}
			return 0;
		}

		public char readChar() {
			try {
				return get.readChar();
			} catch (Exception e) {
			}
			return 0;
		}

		public void close() {
			try {
				get.close();
				decompressor.close();
				end.close();
			} catch (Exception e) {
			}
		}
	}

}
