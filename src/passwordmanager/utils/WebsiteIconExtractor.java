/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package passwordmanager.utils;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;
import javax.imageio.ImageIO;

/**
 *
 * @author Colin Halseth
 */
public class WebsiteIconExtractor {

    /*
    You'll want to tackle this a few ways:

    Look for the favicon.ico at the root of the domain

    www.domain.com/favicon.ico
    Look for a <link> tag with the rel="shortcut icon" attribute

    <link rel="shortcut icon" href="/favicon.ico" />
    Look for a <link> tag with the rel="icon" attribute

    <link rel="icon" href="/favicon.png" />
    The latter two will usually yield a higher quality image.

    Just to cover all of the bases, there are device specific icon files that might yield higher quality images since these devices usually have larger icons on the device than a browser would need:

    <link rel="apple-touch-icon" href="images/touch.png" />

    <link rel="apple-touch-icon-precomposed" href="images/touch.png" />
    */
    private String getDomainName(String url) throws Exception{
        URI uri = new URI(url);
        String domain = uri.getHost();
        return (domain.startsWith("www.") ? domain.substring(4) : domain);
    }

    private BufferedImage ExtractFavicon(String urlStr) throws Exception{

        String domain = getDomainName(urlStr);
        String iconUrl = "https://www.google.com/s2/favicons?domain=" + domain;

        URL url = new URL(iconUrl);
        
        /*
        InputStream is = url.openStream();
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        
        //Read stream bytes
        int bytesRead = 0;
        byte[] chunk = new byte[4096];
        while((bytesRead = is.read(chunk)) > 0){
            os.write(chunk, 0, bytesRead);
        }
        
        //Create byte array
        byte[] icoBytes = os.toByteArray();
        
        byte width = icoBytes[6];
        byte height = icoBytes[7];
        int imgSize = ByteBuffer.wrap(Arrays.copyOfRange(icoBytes, 14, 18)).order(ByteOrder.LITTLE_ENDIAN).getInt();
        int offset = ByteBuffer.wrap(Arrays.copyOfRange(icoBytes, 18, 22)).order(ByteOrder.LITTLE_ENDIAN).getInt();
        
        byte[] pngData = Arrays.copyOfRange(icoBytes, offset, icoBytes.length);
        
        BufferedImage img = ImageIO.read(new ByteArrayInputStream(pngData));
        */
        BufferedImage img = ImageIO.read(url);
        
        return img;

    }

    public BufferedImage getIconFromSiteUrl(String url) {
        try {
            BufferedImage img;

            //Try to get favicon from the root domain
            img = ExtractFavicon(url);

            return img;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
