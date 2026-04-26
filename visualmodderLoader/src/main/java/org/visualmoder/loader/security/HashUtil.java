package org.visualmoder.loader.security;

import java.io.File;
import java.io.FileInputStream;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.util.HexFormat;

public class HashUtil {

    public static String sha256(File file) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");

        try (DigestInputStream dis = new DigestInputStream(new FileInputStream(file), digest)) {
            byte[] buffer = new byte[8192]; 
            while (dis.read(buffer) != -1) {
                // reading stream updates digest
            }
        }

        return HexFormat.of().formatHex(digest.digest());
    }

    public static void verify(File file, String expectedHash) throws Exception {
        String actual = sha256(file);
        //System.out.println(" expected"+expectedHash);
        //System.out.println(" actual  "+actual);
        if (!actual.equalsIgnoreCase(expectedHash)) {
            file.delete();
            throw new SecurityException("SHA-256 mismatch! File deleted.");
        }
    }
}