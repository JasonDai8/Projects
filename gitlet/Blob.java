package gitlet;

import java.io.File;
import java.io.Serializable;

public class Blob implements Serializable {

    private byte[] file;
    private String fileName;

    // Creates a standard blob object
    public Blob(byte[] file) {
        this.file = file;
    }

    // Creates a blob object with a reference file name
    public Blob(byte[] file, String fileName) {
        this.file = file;
        this.fileName = fileName;
    }

    /** Reads a blob from file with a given name */
    public static Blob readBlob(String name) {
        return Utils.readObject(new File(Repository.GITLET_DIR + "/blobs/" + name), Blob.class);
    }

    /** Saves a blob to file */
    public static void saveBlob(Blob blob, String name) {
        Utils.writeObject(new File(Repository.GITLET_DIR + "/blobs/" + name), blob);
    }

    /** Returns the byte array associated with the file that the blob references */
    public byte[] getFile() {
        return this.file;
    }

    /** Returns the name of the file associated with the blob */
    public String getFileName() {
        return this.fileName;
    }

}
