package controller;

import java.io.File;

/**
 * Created by hadoop on 16-12-24.
 */
public interface ServerCall {
    void receiveEncryptedFile(byte[] dataBytes);
    void receiveNormalFile(File imageFile);
}
