package me.DevTec.TheAPI.Utils.Compression;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.GZIPOutputStream;

public class Compression {
	public static int COMPRESS = 2;

	private static byte[] buf = new byte[1024];

	public static byte[] compress(byte[] in) {
		Deflater compressor = new Deflater(Deflater.BEST_COMPRESSION, true);
		ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
		for (int i = 0; i < COMPRESS; ++i) {
			compressor.setInput(in);
			compressor.finish();
			while (!compressor.finished())
				byteStream.write(buf, 0, compressor.deflate(buf));
			in = byteStream.toByteArray();
			compressor.reset();
			byteStream.reset();
		}
		return in;
	}

	public static File zip(File file) {
		if (file == null)
			return null;
		try {
			FileInputStream inputStream = new FileInputStream(file);
			FileOutputStream outputStream = new FileOutputStream(file);
			DeflaterOutputStream compresser = new DeflaterOutputStream(outputStream);
			int contents;
			while ((contents = inputStream.read()) != -1)
				compresser.write(contents);
			inputStream.close();
			compresser.close();
		} catch (Exception e) {
		}
		return new File(file.getParent());
	}

	public static Compressor getCompressor() {
		return new Compressor();
	}

	public static class Compressor {
		private ByteArrayOutputStream end = new ByteArrayOutputStream();
		private GZIPOutputStream compressor;
		private ObjectOutputStream get;

		public Compressor() {
			try {
				compressor = new GZIPOutputStream(end);
				get = new ObjectOutputStream(compressor);
			} catch (Exception e) {
			}
		}

		public Compressor add(Object o) {
			try {
				get.writeObject(o);
			} catch (Exception e) {
			}
			return this;
		}

		public Compressor add(String o) {
			try {
				get.writeUTF(o);
			} catch (Exception e) {
			}
			return this;
		}

		public Compressor add(boolean o) {
			try {
				get.writeBoolean(o);
			} catch (Exception e) {
			}
			return this;
		}

		public Compressor add(float o) {
			try {
				get.writeFloat(o);
			} catch (Exception e) {
			}
			return this;
		}

		public Compressor add(int o) {
			try {
				get.writeInt(o);
			} catch (Exception e) {
			}
			return this;
		}

		public Compressor add(byte o) {
			try {
				get.writeByte(o);
			} catch (Exception e) {
			}
			return this;
		}

		public Compressor add(double o) {
			try {
				get.writeDouble(o);
			} catch (Exception e) {
			}
			return this;
		}

		public Compressor add(long o) {
			try {
				get.writeLong(o);
			} catch (Exception e) {
			}
			return this;
		}

		public Compressor add(short o) {
			try {
				get.writeShort(o);
			} catch (Exception e) {
			}
			return this;
		}

		public Compressor add(char o) {
			try {
				get.writeChar(o);
			} catch (Exception e) {
			}
			return this;
		}

		public Compressor flush() {
			try {
				get.flush();
				compressor.flush();
				compressor.finish();
				end.flush();
			} catch (Exception e) {
			}
			return this;
		}

		public void close() {
			try {
				get.close();
				compressor.close();
				end.close();
			} catch (Exception e) {
			}
		}

		public byte[] get() {
			flush();
			return end.toByteArray();
		}
	}

}
