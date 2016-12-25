package controller;

/**
 * Created by hadoop on 16-12-24.
 */
public interface ServerCall {
    void receiveEncryptedFile(byte[] dataBytes);
}
