package functions;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import org.apache.log4j.Level;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;


public class OcrApiUtil {

    private static URL url;
    private static String CRLF = "\r\n";
    private static String boundary = Long.toHexString(System.currentTimeMillis());

    static {
        try {
            url = new URL("https://api.ocr.space/parse/image");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    public static boolean checkText(String imgPath, String expected) {
        boolean match = false;
        File img = null;
        try {
            img = new File((imgPath));
            img.toURI();
        } catch (Exception e) {
            Environment.loger.log(Level.ERROR, "Image path is not Valid");
        }

        try {
            HttpURLConnection connection = (HttpURLConnection) (url).openConnection();

            connection.setDoOutput(true);
            connection.setRequestProperty("apikey", "04fab21c6088957");
            connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);

            sendImage(connection, img);
            match = checkResponse(connection, expected);
            connection.disconnect();

        } catch (IOException e) {
            Environment.loger.log(Level.ERROR, "Error connecting to OCR Api");
        }
        return match;
    }

    private static boolean checkResponse(HttpURLConnection connection, String expected) {

        String line;
        String[] parsedText = new String[1];

        while (true) {
            try {
                InputStream response = connection.getInputStream();
                BufferedReader br = new BufferedReader(new InputStreamReader(response));
                if (!((line = br.readLine()) != null)) break;
                JsonParser parser = new JsonParser();
                JsonElement resp = parser.parse(line);

                parsedText = resp.getAsJsonObject().getAsJsonArray("ParsedResults").get(0).getAsJsonObject().getAsJsonPrimitive("ParsedText").getAsString().split(CRLF);
            } catch (IOException e) {
                Environment.loger.log(Level.ERROR, "Error in response from OCR Api");
            }

            for (String text : parsedText) {
                if (text.equalsIgnoreCase(expected)) {
                    Environment.loger.log(Level.INFO, text);
                    return true;
                }
            }
        }
        return false;
    }

    private static void sendImage(HttpURLConnection connection, File img) {
        OutputStream output = null;
        try {
            output = connection.getOutputStream();
            PrintWriter writer = new PrintWriter(new OutputStreamWriter(output), true);

            writer.append("--" + boundary).append(CRLF);

            writer.append("Content-Disposition: form-data; name=\"file\"; filename=\"" + img.getPath() + "\"").append(CRLF);
            writer.append("Content-Type: text/plain; ").append(CRLF);
            writer.append(CRLF).flush();
            Files.copy(img.toPath(), output);
            writer.append(CRLF).flush();

            writer.append("--" + boundary + "--").append(CRLF).flush();

        } catch (IOException e) {
            Environment.loger.log(Level.ERROR, "Error uploading image to OCR Api");
        }
    }

    public static void main(String[] args) throws URISyntaxException {
        //  boolean match = checkText("file:///C:/Users/bsinghbisen/Downloads/toastmessage1300.png", "No work instructions exist for this work");
    }
}
