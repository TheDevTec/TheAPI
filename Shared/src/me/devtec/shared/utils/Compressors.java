package me.devtec.shared.utils;

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
		for (int isf = 0; isf < 2; ++isf) {
			decompressor.setInput(in);
			while (!decompressor.finished())
				try {
					bos.write(Compressors.buf, 0, decompressor.inflate(Compressors.buf));
				} catch (Exception e) {
				}
			decompressor.reset();
			in = bos.toByteArray();
			bos.reset();
		}
		return in;
	}

	public static byte[] compress(byte[] in) {
		Deflater compressor = new Deflater(Deflater.BEST_COMPRESSION, true);
		ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
		for (int i = 0; i < 2; ++i) {
			compressor.setInput(in);
			compressor.finish();
			while (!compressor.finished())
				byteStream.write(Compressors.buf, 0, compressor.deflate(Compressors.buf));
			in = byteStream.toByteArray();
			compressor.reset();
			byteStream.reset();
		}
		return in;
	}

	public static class Compressor {
		private ByteArrayOutputStream end = new ByteArrayOutputStream();
		private GZIPOutputStream compressor;
		private ObjectOutputStream get;

		public Compressor() {
			try {
				this.compressor = new GZIPOutputStream(this.end);
				this.get = new ObjectOutputStream(this.compressor);
			} catch (Exception e) {
			}
		}

		public Compressor add(Object o) {
			try {
				this.get.writeObject(o);
			} catch (Exception e) {
			}
			return this;
		}

		public Compressor add(String o) {
			try {
				this.get.writeUTF(o);
			} catch (Exception e) {
			}
			return this;
		}

		public Compressor add(boolean o) {
			try {
				this.get.writeBoolean(o);
			} catch (Exception e) {
			}
			return this;
		}

		public Compressor add(float o) {
			try {
				this.get.writeFloat(o);
			} catch (Exception e) {
			}
			return this;
		}

		public Compressor add(int o) {
			try {
				this.get.writeInt(o);
			} catch (Exception e) {
			}
			return this;
		}

		public Compressor add(byte o) {
			try {
				this.get.writeByte(o);
			} catch (Exception e) {
			}
			return this;
		}

		public Compressor add(double o) {
			try {
				this.get.writeDouble(o);
			} catch (Exception e) {
			}
			return this;
		}

		public Compressor add(long o) {
			try {
				this.get.writeLong(o);
			} catch (Exception e) {
			}
			return this;
		}

		public Compressor add(short o) {
			try {
				this.get.writeShort(o);
			} catch (Exception e) {
			}
			return this;
		}

		public Compressor add(char o) {
			try {
				this.get.writeChar(o);
			} catch (Exception e) {
			}
			return this;
		}

		public Compressor flush() {
			try {
				this.get.flush();
				this.compressor.flush();
				this.compressor.finish();
				this.end.flush();
			} catch (Exception e) {
			}
			return this;
		}

		public void close() {
			try {
				this.get.close();
				this.compressor.close();
				this.end.close();
			} catch (Exception e) {
			}
		}

		public byte[] get() {
			this.flush();
			return this.end.toByteArray();
		}
	}

	public static class Decompressor {
		private ByteArrayInputStream end;
		private GZIPInputStream decompressor;
		private ObjectInputStream get;

		public Decompressor(byte[] toDecompress) {
			try {
				this.end = new ByteArrayInputStream(toDecompress);
				this.decompressor = new GZIPInputStream(this.end);
				this.get = new ObjectInputStream(this.decompressor);
			} catch (Exception e) {
			}
		}

		public Object readObject() {
			try {
				this.get.readObject();
			} catch (Exception e) {
			}
			return null;
		}

		public String readString() {
			try {
				this.get.readUTF();
			} catch (Exception e) {
			}
			return null;
		}

		public String readUTF() {
			return this.readString();
		}

		public boolean readBoolean() {
			try {
				return this.get.readBoolean();
			} catch (Exception e) {
			}
			return false;
		}

		public float readFloat() {
			try {
				return this.get.readFloat();
			} catch (Exception e) {
			}
			return 0;
		}

		public int readInt() {
			try {
				return this.get.readInt();
			} catch (Exception e) {
			}
			return 0;
		}

		public byte readByte() {
			try {
				return this.get.readByte();
			} catch (Exception e) {
			}
			return 0;
		}

		public double readDouble() {
			try {
				return this.get.readDouble();
			} catch (Exception e) {
			}
			return 0;
		}

		public long readLong() {
			try {
				return this.get.readLong();
			} catch (Exception e) {
			}
			return 0;
		}

		public short readShort() {
			try {
				return this.get.readShort();
			} catch (Exception e) {
			}
			return 0;
		}

		public char readChar() {
			try {
				return this.get.readChar();
			} catch (Exception e) {
			}
			return 0;
		}

		public void close() {
			try {
				this.get.close();
				this.decompressor.close();
				this.end.close();
			} catch (Exception e) {
			}
		}
	}
}
