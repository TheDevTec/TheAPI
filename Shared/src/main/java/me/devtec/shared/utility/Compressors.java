package me.devtec.shared.utility;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.zip.Deflater;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import java.util.zip.Inflater;

public class Compressors {
	static byte[] buf = new byte[1024];

	public static byte[] decompress(byte[] in) {
		Inflater decompressor = new Inflater(true);
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		byte[] result = in;
		for (int isf = 0; isf < 2; ++isf) {
			decompressor.setInput(result);
			while (!decompressor.finished())
				try {
					bos.write(Compressors.buf, 0, decompressor.inflate(Compressors.buf));
				} catch (Exception e) {
				}
			decompressor.reset();
			result = bos.toByteArray();
			bos.reset();
		}
		return result;
	}

	public static byte[] compress(byte[] in) {
		Deflater compressor = new Deflater(Deflater.BEST_COMPRESSION, true);
		ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
		byte[] result = in;
		for (int i = 0; i < 2; ++i) {
			compressor.setInput(result);
			compressor.finish();
			while (!compressor.finished())
				byteStream.write(Compressors.buf, 0, compressor.deflate(Compressors.buf));
			result = byteStream.toByteArray();
			compressor.reset();
			byteStream.reset();
		}
		return result;
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
