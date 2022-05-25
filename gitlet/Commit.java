package gitlet;

// TODO: any imports you need here

import java.io.File;
import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;

/** Represents a gitlet commit object.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 *  @author TODO
 */
public class Commit implements Serializable {

    /** Reference to most newest unique name convention */
    private static int count = 0;
    /** Unique name of the commit */
    private String name;
    /** Date of the commit */
    private Date date;
    /** The message of this Commit. */
    private String message;
    /** Parent commit sha1 Object String representation */
    private String parent;
    /** Parent 2 commit sha1 Object String representation */
    private String parent2;
    /** HashMap of pointers to blob objects currently tracked by this commit */
    private HashMap<String, String> blobs; // fileName : blobHash


    /**
     * Create an initial commit object
     * @param message Initial name of the commit
     */
    public Commit(String message) {
        this.date = new Date(0);
        //System.out.println("[INTERNAL] " + date);
        this.message = message;
        this.parent = null;
        this.name = "Commit_" + count;
        count += 1;
        blobs = new HashMap<>();
    }


    /**
     * Creates a commit object with specified parameters
     * @param message Message of the commit
     * @param parent Sha1 String of the parent Commit object
     */
    public Commit(String message, String parent) {
        this.date = new Date();
        //System.out.println("[INTERNAL] " + date);
        this.message = message;
        this.parent = parent;
        this.name = "Commit_" + count;
        count += 1;
        blobs = readCommit(parent).getBlobs();
    }

    /** Reads a commit with a given hash name from file*/
    public static Commit readCommit(String name) {
        return Utils.readObject(new File(Repository.GITLET_DIR + "/commits/" + name), Commit.class);
    }

    /** Saves a commit to file given the commit object and its name (often sha1 hash) */
    public static void saveCommit(Commit commit, String name) {
        Utils.writeObject(new File(Repository.GITLET_DIR + "/commits/" + name), commit);
    }

    /** Gets the name of the Commit's parent */
    public String getParent() {
        return this.parent;
    }

    /** Gets the message of the Commit */
    public String getMessage() {
        return this.message;
    }

    /** Gets the blobs associated with the Commit */
    public HashMap<String, String> getBlobs() {
        return this.blobs;
    }

    /** Sets the blobs associated with the Commit */
    public void setBlobs(HashMap<String, String> blobs) {
        this.blobs = blobs;
    }

    /** Gets the date the Commit object was created */
    public Date getDate() {
        return this.date;
    }

    /** Sets the parent of the second merge */
    public void setMergeParent(String parent) {
        this.parent2 = parent;
    }

    /** Gets the second merge parent */
    public String getMergeParent() {
        return this.parent2;
    }
}
