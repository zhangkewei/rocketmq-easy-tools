package com.zkevin.rocketmq;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;
import org.apache.commons.codec.binary.Base64;

public class StringUrlTools
{
    public static String UrlEncoderCompress(String v, String encoder)
            throws Exception
    {
        if (v == null) {
            return null;
        }
        String urlencoder = URLEncoder.encode(v, encoder == null ? "UTF-8" : encoder);
        String ret = compressEncoder(urlencoder, encoder);
        return ret;
    }

    public static String UrlDecordUncompress(String v, String encoder)
            throws Exception
    {
        if (v == null) {
            return null;
        }
        String urlencoder = decoderUncompress(v, encoder);
        String ret = URLDecoder.decode(urlencoder, encoder == null ? "UTF-8" : encoder);
        return ret;
    }

    public static final byte[] compressStr(String str, String encoder)
            throws Exception
    {
        if (str == null)
            return null;
        byte[] compressed = compressByte(str.getBytes(encoder == null ? "UTF-8" : encoder));
        return compressed;
    }

    public static final byte[] compressByte(byte[] str)
            throws Exception
    {
        if (str == null) {
            return null; }
        ByteArrayOutputStream out = null;
        ZipOutputStream zout = null;
        byte[] compressed;
        try { out = new ByteArrayOutputStream();
            zout = new ZipOutputStream(out);
            zout.putNextEntry(new ZipEntry("0"));
            zout.write(str);
            zout.closeEntry();
            compressed = out.toByteArray();
        } finally {
            if (zout != null)
                try {
                    zout.close();
                } catch (Exception e) {
                }
            if (out != null)
                try {
                    out.close();
                } catch (Exception e) {
                }
        }
        return compressed;
    }

    public static final String decompress2str(byte[] compressed, String enc)
            throws Exception
    {
        if (compressed == null)
            return null;
        String decompressed = new String(decompress2bytes(compressed), enc == null ? "UTF-8" : enc);
        return decompressed;
    }

    public static final byte[] decompress2bytes(byte[] compressed)
            throws Exception
    {
        if (compressed == null)
            return null;
        ByteArrayOutputStream out = null;
        ByteArrayInputStream in = null;
        ZipInputStream zin = null;
        try {
            out = new ByteArrayOutputStream();
            in = new ByteArrayInputStream(compressed);
            zin = new ZipInputStream(in);
            zin.getNextEntry();
            byte[] buffer = new byte[1024];
            int offset = -1;
            while ((offset = zin.read(buffer)) != -1) {
                out.write(buffer, 0, offset);
            }
            return out.toByteArray();
        } finally {
            if (zin != null)
                try {
                    zin.close();
                }
                catch (Exception e) {
                }
            if (in != null)
                try {
                    in.close();
                }
                catch (Exception e) {
                }
            if (out != null)
                try {
                    out.close();
                }
                catch (Exception e)
                {
                }
        }
    }

    public static String encoderStr(String str, String encoder)
            throws Exception
    {
        byte[] fb = Base64.encodeBase64URLSafe(str.getBytes(encoder == null ? "UTF-8" : encoder));
        return new String(fb, encoder == null ? "UTF-8" : encoder);
    }

    public static String encoderByte(byte[] bytes, String encoder)
            throws Exception
    {
        byte[] e = Base64.encodeBase64URLSafe(bytes);
        return new String(e, encoder == null ? "UTF-8" : encoder);
    }

    public static String decoder2str(String str, String enc)
            throws Exception
    {
        byte[] fbb = Base64.decodeBase64(str.getBytes(enc == null ? "UTF-8" : enc));
        return new String(fbb, enc == null ? "UTF-8" : enc);
    }

    public static byte[] decoder2byte(String str, String enc)
            throws Exception
    {
        byte[] fbb = Base64.decodeBase64(str.getBytes(enc == null ? "UTF-8" : enc));
        return fbb;
    }

    public static String compressEncoder(String str, String enc)
            throws Exception
    {
        return encoderByte(compressStr(str, enc), enc);
    }

    public static String decoderUncompress(String str, String enc)
            throws Exception
    {
        return decompress2str(decoder2byte(str, enc), enc);
    }
}