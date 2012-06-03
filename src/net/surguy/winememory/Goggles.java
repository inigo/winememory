package net.surguy.winememory;

import java.io.*;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Random;

/**
 * http://notanothercodeblog.blogspot.co.uk/2011/02/google-goggles-api.html
 * http://prodroid.com.ua/?p=385
 * <p/>
 * https://github.com/deetch/goggles-experiment/blob/master/parse_dict.py
 * https://developers.google.com/protocol-buffers/docs/javatutorial
 * http://code.google.com/p/protobuf/
 *
 * @author Poloz Igor
 */
public class Goggles {

    // The POST body required to validate the CSSID.
    private static final byte[] cssidPostBody = new byte[]{0x22,
            0x00, 0x62, 0x3C, 0x0A, 0x13, 0x22, 0x02, 0x65, 0x6E, (byte) 0xBA,
            (byte) 0xD3, (byte) 0xF0, 0x3B, 0x0A, 0x08, 0x01, 0x10, 0x01, 0x28, 0x01,
            0x30, 0x00, 0x38, 0x01, 0x12, 0x1D, 0x0A, 0x09, 0x69, 0x50, 0x68, 0x6F, 0x6E,
            0x65, 0x20, 0x4F, 0x53, 0x12, 0x03, 0x34, 0x2E, 0x31, 0x1A, 0x00, 0x22, 0x09,
            0x69, 0x50, 0x68, 0x6F, 0x6E, 0x65, 0x33, 0x47, 0x53, 0x1A, 0x02, 0x08, 0x02,
            0x22, 0x02, 0x08, 0x01};

    // Bytes trailing the image byte array.
    private static final byte[] trailingBytes = new byte[]{
            0x18, 0x4B, 0x20, 0x01, 0x30, 0x00, (byte) 0x92, (byte) 0xEC, (byte) 0xF4, 0x3B,
            0x09, 0x18, 0x00, 0x38, (byte) 0xC6, (byte) 0x97, (byte) 0xDC, (byte) 0xDF, (byte) 0xF7, 0x25,
            0x22, 0x00};

    private final String cssId;

    public Goggles() throws IOException {
        this.cssId = generateCssId();
    }

    private String generateCssId() throws IOException {
        for (int i = 0; i < 3; i++) {
            BigInteger bi = new BigInteger(64, new Random());
            String possibleId = bi.toString(16).toUpperCase();
            if (isValidCssId(possibleId)) {
                return possibleId;
            }
        }
        throw new IllegalStateException("Not able to generate valid CSSID after three attempts");
    }

    private boolean isValidCssId(String possibleId) throws IOException {
        sendRequest(possibleId, cssidPostBody);
        // @todo Parse response and check is cssid is valid
        return true;
    }

    private String sendPhoto(File file) throws IOException {
        int fileLength = (int) file.length();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int newLine = 10;
        out.write(newLine);
        out.write(toVarint32(fileLength + 32));
        out.write(newLine);
        out.write(toVarint32(fileLength + 14));
        out.write(newLine);
        out.write(toVarint32(fileLength + 10));
        out.write(newLine);
        out.write(toVarint32(fileLength));
        out.write(getFileBytes(file));
        out.write(trailingBytes);
        out.close();
        return sendRequest(cssId, out.toByteArray());
    }

    private String sendRequest(String cssId, byte[] content) throws IOException {
        URL url = new URL("http://www.google.com/goggles/container_proto?cssid=" + cssId);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setDoOutput(true);
        conn.setUseCaches(false);
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/x-protobuffer");
        conn.setRequestProperty("Pragma", "no-cache");
        OutputStream out = conn.getOutputStream();
        out.write(content);
        out.close();

        BufferedReader buffRead = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        StringBuilder response = new StringBuilder();
        String line;
        while ((line = buffRead.readLine()) != null) {
            response.append(line).append("\n");
        }
        return response.toString();
    }

    // Encodes an int32 into varint32.
    public static byte[] toVarint32(int value) {
        int index = 0;
        int tmp = value;
        while ((0x7F & tmp) != 0) {
            tmp = tmp >> 7;
            index++;
        }
        byte[] res = new byte[index];
        index = 0;
        while ((0x7F & value) != 0) {
            int i = (0x7F & value);
            if ((0x7F & (value >> 7)) != 0) {
                i += 128;
            }
            res[index] = ((byte) i);
            value = value >> 7;
            index++;
        }
        return res;
    }

    private static byte[] getFileBytes(File file) throws IOException {
        byte[] bytes = new byte[(int) file.length()];
        DataInputStream dis = new DataInputStream(new FileInputStream(file));
        int read = 0;
        int numRead;
        while (read < bytes.length && (numRead = dis.read(bytes, read, bytes.length - read)) >= 0) {
            read = read + numRead;
        }
        return bytes;
    }

    public static void main(String[] args) throws IOException {
        String imageFile = "tuborg.jpg";
        Goggles goggles = new Goggles();
        String response = goggles.sendPhoto(new File(imageFile));
        System.out.println("response = " + response);
    }

}
