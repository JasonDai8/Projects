package gitlet;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

import static gitlet.Utils.*;

// TODO: any imports you need here

/** Represents a gitlet repository.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 *  @author Logan Dickey, Jason Dai
 */
public class Repository implements Serializable {

    /** An internal class representing a Branch object */
    private static class Branch implements Serializable {
        public String head;
        public String name;

        public Branch(String name, String head){
            this.head = head;
            this.name = name;
        }
    }

    /** The name of the Repository */
    private String name;
    /** Map of branch pointers (treemap provides sorting ability) */
    TreeMap<String, Branch> branches;
    /** Set of all blobs */
    HashSet<String> blobs;
    /** Set of all commits */
    HashSet<String> commits;
    /** Reference to the current branch */
    private String workingBranch = "master";
    /** The current working directory. */
    public static final File CWD = new File(System.getProperty("user.dir"));
    /** The .gitlet directory. */
    public static final File GITLET_DIR = Utils.join(CWD, ".gitlet");
    /** Tracked files to be removed */
    TreeSet<String> stagedRemoval;

    /**
     * Creates a Repository object with the specified parameter.
     * @param name Name of repository
     */
    public Repository(String name) {
        this.name = name;
        this.branches = new TreeMap<>();
        this.blobs = new HashSet<>();
        this.stagedRemoval = new TreeSet<>();
        this.commits = new HashSet<>();

        // Creates an initial commit message, assigns a pointer to the sha1 and saves the commit with that pointer
        Commit init = new Commit("initial commit");
        String pointer = Utils.sha1(Utils.serialize(init));
        Commit.saveCommit(init, pointer);

        Branch master = new Branch("master", pointer);
        branches.put("master", master);

        //System.out.println("[INTERNAL] MASTER HEAD POINTER: " + pointer);
    }


    /** Initializes a repository. If a working directory already exists, return false. Otherwise true. */
    public static boolean init(String name) {
        // Check the working directory
        if (!GITLET_DIR.isDirectory()) {
            // Gitlet directory has not been initialized

            // Create the Gitlet directory
            GITLET_DIR.mkdir();

            // Create the blobs directory
            File blobsDir = new File(GITLET_DIR + "/blobs");
            blobsDir.mkdir();

            // Create the staging area directory
            File stagingDir = new File(GITLET_DIR + "/staging");
            stagingDir.mkdir();

            // Create the commits directory
            File commitsDir = new File(GITLET_DIR + "/commits");
            commitsDir.mkdir();

            Repository repo = new Repository(name);
            repo.saveRepo();
            return true;
        }
        return false;
    }

    public void saveRepo() {
        //System.out.println("[INTERNAL] saving to " + this.name);
        File outFile = new File(GITLET_DIR + "/" + this.name);
        try {
            ObjectOutputStream out =
                    new ObjectOutputStream(new FileOutputStream(outFile));
            out.writeObject(this);
            out.close();
        } catch (IOException excp) {
            //System.out.println(excp);
            //System.out.println("[INTERNAL] Invalid repository");
        }
    }

    /** Fetches a repository from the file system given a name. */
    public static Repository fromFile(String repoName) {
        Repository repository;
        File inFile = new File(GITLET_DIR + "/" + repoName);
        try {
            ObjectInputStream inp =
                    new ObjectInputStream(new FileInputStream(inFile));
            repository = (Repository) inp.readObject();
            inp.close();
        } catch (IOException | ClassNotFoundException excp) {
            //System.out.println("[INTERNAL] Invalid repository name");
            repository = null;
        }
        return repository;
    }

    /** Adds a file to the staging area */
    public void add(String filename) {
        File file = new File(filename);
        if (file.exists()) {
            // The file exists
            // Create a blob object with the existing file
            Blob blob = new Blob(Utils.readContents(file), file.getName());
            String blobHash = Utils.sha1(Utils.serialize(blob));

            // Check if the file already has been saved before
            if (blobs.contains(blobHash)) {
                //System.out.println("Blob already exists in the repository");
                // TODO: if we want to commit a file that has already been committed (IN A PREVIOUS COMMIT)
                // if the file is NOT in the current commit, then we can fetch the blob PREVIOUSLY associated
                // with the old commit object
                // ie. Blob.getBlob of the hash IFF the current commit DOES NOT have the blob being tracked in
                // THAT current state

                // Check if the CURRENT commit has a reference to the file
                HashMap<String, String> blobs = Commit.readCommit(branches.get(workingBranch).head).getBlobs();
                if (blobs.containsValue(blobHash)) {
                    // The current commit already has a reference to the file (duplicate)
                    // Remove it from the staging area (if it exists) and unstage it for removal
                    if (stagedRemoval.contains(filename)) {
                        stagedRemoval.remove(filename);
                    }

                    File staged = new File(GITLET_DIR + "/staging/" + filename);
                    if (staged.exists()) {
                        staged.delete();
                    }
                }
            } else {
                //System.out.println("blob doesn't exist, staging file");
                File copy = new File(GITLET_DIR + "/staging/" + file);
                Utils.writeContents(copy, Utils.readContents(file));
            }
            this.saveRepo();
        } else {
            // The file doesn't exist, can't stage it.
            System.out.println("File does not exist.");
            System.exit(0);
        }
    }

    /** Removes a file from the staging area */
    public void remove(String fileName) {
        File stagedFile = new File(GITLET_DIR + "/staging/" + fileName);
        String head = branches.get(workingBranch).head;
        Commit commit = Commit.readCommit(head);

        if (stagedFile.exists() || commit.getBlobs().containsKey(fileName)) {

            // The file either exists in the staging area or is being tracked
            if (stagedFile.exists()) {
                stagedFile.delete();
            }

            // Check if the file is currently being tracked
            if (commit.getBlobs().containsKey(fileName)) {
                File workingFile = new File(fileName);
                workingFile.delete();

                // Stage the file for removal and save the repository
                stagedRemoval.add(fileName);
                this.saveRepo();
            }

        } else {
            System.out.println("No reason to remove the file.");
        }
    }

    /** Commits the current files in the staging area */
    public void commit(String message) {
        /*
        Step 1: create a new commit with the parent referencing the existing head
        Step 2: Copy the tracked files as they exist in the parent Commit (keep references)
        Step 3: Create new blobs for staged files (and assign sha1 pointers to each blob)
        Step 3B: Remove the file from the staging area
        Step 4: Either a) update the pointers for updated tracked files or b) add a new blob/pointer set
        Step 5: Remove the tracked blobs for the current commit for files staged to be removed
        Step 5B: Remove the files from the removal list
        Step 6: Update the existing HEAD pointer for the current working branch
        Step 7: Save the repository and Commit objects
         */

        // Gets the current head commit sha1 hash and creates a new commit
        // NOTE: the non-init Commit constructor will automatically copy blobs from the parent
        String workingBranchHead = branches.get(workingBranch).head;
        Commit commit = new Commit(message, workingBranchHead);

        // Get the current list of files
        File folder = new File(GITLET_DIR + "/staging");
        File[] listOfFiles = folder.listFiles();

        // Assert that there are files to be committed
        if (listOfFiles.length == 0 && stagedRemoval.size() == 0) {
            System.out.println("No changes added to the commit.");
            System.exit(0);
        }

        // For each file, create a blob 
        for (File stagedFile: listOfFiles) {
            //System.out.println("committing file " + stagedFile + ".");
            // Create a blob for the staged file
            Blob blob = new Blob(Utils.readContents(stagedFile), stagedFile.getName());

            // Hash the blob and save it to memory
            String blobHash = Utils.sha1(Utils.serialize(blob));
            Blob.saveBlob(blob, blobHash);
            //System.out.println("saved to blob " + blobHash + " with blob object " + blob);

            // Read the blobs of the current commit, put the updated blob object, and save it to memory
            HashMap<String, String> blobs = commit.getBlobs();
            blobs.put(stagedFile.getName(), blobHash);
            this.blobs.add(blobHash);
            commit.setBlobs(blobs);

            // Delete the staged file
            stagedFile.delete();
        }


        for (String fileName: stagedRemoval) {
            // Read the blobs of the current commit, put the updated blob object, and save it to memory
            HashMap<String, String> blobs = commit.getBlobs();
            blobs.remove(fileName);
            commit.setBlobs(blobs);
        }
        // Should prevent ConcurrentModification
        stagedRemoval.clear();

        // Update the existing head pointer
        String commitPointer = Utils.sha1(Utils.serialize(commit));
        branches.get(workingBranch).head = commitPointer;
        //System.out.println("[INTERNAL] created new commit with pointer: " + commitPointer);

        // Save the repo and the commit
        commits.add(commitPointer);
        this.saveRepo();
        Commit.saveCommit(commit, commitPointer);
    }

    /*
    Dumps the current repository commit history with associated tracked blob objects
     */
    public void dump() {
        // Get the most recent commit object
        //System.out.println("Watching: " + workingBranch + " for head commit: " + branches.get(workingBranch).head);
        Commit commit = Commit.readCommit(branches.get(workingBranch).head);

        while (commit.getParent() != null) {
            //System.out.println(commit.getMessage());
            // Print out the attached blobs
            for (Map.Entry<String, String> item : commit.getBlobs().entrySet()) {
                //System.out.println("blob hash: " + item.getValue() + " fileName: " + item.getKey());
            }
            commit = Commit.readCommit(commit.getParent());
            //System.out.println("---------------------");
        }
    }

    /** Logs commit information starting at the head commit until the initial */
    public void log() {
        // Start at the head commit and log commit information until the initial commit object.
        Commit commit;
        String head = branches.get(workingBranch).head;
        for (commit = Commit.readCommit(head); commit.getParent() != null; commit = Commit.readCommit(commit.getParent())) {
            logCommit(commit);
        }
        logCommit(commit);
    }

    /** Logs commit information for every commit created */
    public void globalLog() {
        List<String> commitRef = Utils.plainFilenamesIn(GITLET_DIR + "/commits");
        for (String commitString: commitRef) {
            Commit commit = Commit.readCommit(commitString);
            logCommit(commit);
        }
    }

    /** Logs the commit id for all commits with the commit message 'name' */
    public void find(String name) {
        List<String> commitRef = Utils.plainFilenamesIn(GITLET_DIR + "/commits");
        boolean found = false;
        for (String commitString: commitRef) {
            Commit commit = Commit.readCommit(commitString);
            if (commit.getMessage().equals(name)) {
                System.out.println(Utils.sha1(Utils.serialize(commit)));
                found = true;
            }
        }
        if (!found) System.out.println("Found no commit with that message.");
    }

    private void logCommit(Commit commit) {
        System.out.println("===");
        System.out.println("commit " + Utils.sha1(Utils.serialize(commit)));
        System.out.println(String.format("Date: " + (new SimpleDateFormat("EEE MMM d kk:mm:ss yyyy Z").format(commit.getDate())), "yyyy"));
        System.out.println(commit.getMessage());
        System.out.println();
    }

    public void status() {
        System.out.println("=== Branches ===");
        for (Map.Entry<String, Branch> branchEntry: branches.entrySet()) {
            if (branchEntry.getKey().equals(workingBranch)) {
                System.out.print("*");
            }
            System.out.println(branchEntry.getValue().name);
        }
        System.out.println();
        System.out.println("=== Staged Files ===");
        List<String> stagedFiles = Utils.plainFilenamesIn(GITLET_DIR + "/staging");
        Collections.sort(stagedFiles);
        for (String file: stagedFiles) {
            System.out.println(file);
        }
        System.out.println();
        System.out.println("=== Removed Files ===");
        for (String file: stagedRemoval) {
            System.out.println(file);
        }
        System.out.println();
        System.out.println("=== Modifications Not Staged For Commit ===");
        System.out.println();
        System.out.println("=== Untracked Files ===");
        System.out.println();
    }

    /** Checks out a file */
    public void checkoutFile(String filename) {
        // Get the current commit referenced by the head
        String head = branches.get(workingBranch).head;
        Commit commit = Commit.readCommit(head);

        if (commit.getBlobs().containsKey(filename)) {
            // The file exists in the previous commit
            byte[] fileContents = Blob.readBlob(commit.getBlobs().get(filename)).getFile();
            File file = new File(filename);
            Utils.writeContents(file, fileContents);
        } else {
            // The commit doesn't track the requested file
            System.out.println("File does not exist in that commit.");
        }
    }

    /** Checks out a file per a commit id */
    // TODO: can be optimized with checkoutFile. Create function that passes in a blob object instead.
    public void checkout(String filename, String commitID) {
//        List<String> commitRef = Utils.plainFilenamesIn(GITLET_DIR + "/commits");
        commitID = getFullCommit(commitID);
        if (commits.contains(commitID)) {
            // The given commit is valid
            Commit commit = Commit.readCommit(commitID);

            if (commit.getBlobs().containsKey(filename)) {
                // The file exists in the previous commit
                byte[] fileContents = Blob.readBlob(commit.getBlobs().get(filename)).getFile();
                File file = new File(filename);
                Utils.writeContents(file, fileContents);
            } else {
                // The commit doesn't track the requested file
                System.out.println("File does not exist in that commit.");
            }
        } else {
            System.out.println("No commit with that id exists.");
        }
    }

    /** Checks out a branch */
    public void checkoutBranch(String branch) {
        if (branches.containsKey(branch)) {
            // Branch exists
            if (!workingBranch.equals(branch)) {
                // Working branch is not the current branch
                // Check if there is an untracked file that would be overridden by branch checkout
                // 1. loop through all tracked files
                // 2. for each of those files in the CWD, check if they have different blob representations
                // 3. if the blob is not in the staging area, abort
//                String head = branches.get(workingBranch).head;
//                Commit commit = Commit.readCommit(head);
//                for (Map.Entry<String, String> blobEntry : commit.getBlobs().entrySet()) {
//                    File cwdFile = new File(blobEntry.getKey());
//                    if (cwdFile.exists()) {
//                        // Check if the blob file exists in the CWD
//                        // Create a blob and compare the hashes
//                        Blob cwdBlob = new Blob(Utils.readContents(cwdFile), cwdFile.getName());
//                        if (!Utils.sha1(Utils.serialize(cwdBlob)).equals(blobEntry.getValue())) {
//                            System.out.println("There is an untracked file in the way; delete it, or add and commit it first.");
//                            System.exit(0);
//                        }
//                    }
//                }

                // Reset Commit
                Commit commit = Commit.readCommit(branches.get(branch).head);
                Commit currentCommit = Commit.readCommit(branches.get(workingBranch).head);
                Set<String> resetFiles = commit.getBlobs().keySet();
                Set<String> files = currentCommit.getBlobs().keySet();

                // If a working file is untracked in the current branch and would be overwritten, abort
                for (String file : resetFiles) {
                    if (!files.contains(file)) {
                        // The current commit doesn't contains the reset file, check if it exists in the CWD
                        // AKA: the file will be modified, and is untracked in the current commit
                        File file1 = new File(file);
                        // Check that the file exists and will be overwritten by merge
                        if (file1.exists()) {
                            // Check that the file contents are different
                            if (!Utils.sha1(Utils.serialize(Blob.readBlob(commit.getBlobs().get(file)).getFile())).equals(Utils.sha1(Utils.serialize(Utils.readContents(file1))))) {
                                System.out.println("There is an untracked file in the way; delete it, or add and commit it first.");
                                System.exit(0);
                            }
                        }
                    }
                }


                // All files should be valid, proceed to revert files
                for (Map.Entry<String, String> blobEntry : commit.getBlobs().entrySet()) {
                    File cwdFile = new File(blobEntry.getKey());
                    Utils.writeContents(cwdFile, Blob.readBlob(blobEntry.getValue()).getFile());
                }

                // For each file in the current commit not tracked by the checked out branch is removed
                for (String file : files) {
                    if (!resetFiles.contains(file)) {
                        File file1 = new File(file);
                        file1.delete();
                    }
                }

                // Clear the staging area
                List<String> filesN = Utils.plainFilenamesIn(GITLET_DIR + "/staging");
                for (String file : filesN) {
                    File file1 = new File(GITLET_DIR + "/staging/" + file);
                    file1.delete();
                }

                // Update the working branch
                workingBranch = branch;
                this.saveRepo();
            } else {
                System.out.println("No need to checkout the current branch.");
            }
        } else {
            System.out.println("No such branch exists.");
        }
    }

    private String getFullCommit(String commit) {
        int length = commit.length();
        for (String commitString : commits) {
            if (commitString.substring(0, length).equals(commit)) {
                // The commit *should* be close enough
                return commitString;
            }
        }
        return commit;
    }

    public void createBranch(String branchName) {
        if (!branches.containsKey(branchName)) {
            // Branch doesn't already exist
            Branch branch = new Branch(branchName, branches.get(workingBranch).head);
            branches.put(branchName, branch);
            this.saveRepo();
        } else {
            System.out.println("A branch with that name already exists.");
        }
    }

    public void removeBranch(String branchName) {
        if (branches.containsKey(branchName)) {
            // Branch exists
            if (!workingBranch.equals(branchName)) {
                // Working branch is not the requested branch to be removed
                // Delete the branch
                branches.remove(branchName);
                this.saveRepo();
            } else {
                System.out.println("Cannot remove the current branch.");
            }
        } else {
            System.out.println("A branch with that name does not exist.");
        }
    }

    /** Checks out all files tracked by a commit */
    public void reset(String commit) {
        commit = getFullCommit(commit);
        List<String> commitRef = Utils.plainFilenamesIn(GITLET_DIR + "/commits");
        if (commitRef.contains(commit)) {
            // Valid commit to reset to
            Commit currentCommit = Commit.readCommit(branches.get(workingBranch).head);
            Commit commitObj = Commit.readCommit(commit);
            Set<String> files = currentCommit.getBlobs().keySet();
            Set<String> resetFiles = commitObj.getBlobs().keySet();

            // If the reset commit has a file that is not tracked in the current commit, exit()
            for (String file : resetFiles) {
                if (!files.contains(file)) {
                    // The current commit doesn't contains the reset file, check if it exists in the CWD
                    // AKA: the file will be modified, and is untracked in the current commit
                    File file1 = new File(file);
                    // Check that the file exists and will be overwritten by merge
                    if (file1.exists()) {
                        // Check that the file contents are different
                        if (!Utils.sha1(Utils.serialize(Blob.readBlob(commitObj.getBlobs().get(file)).getFile())).equals(Utils.sha1(Utils.serialize(Utils.readContents(file1))))) {
                            System.out.println("There is an untracked file in the way; delete it, or add and commit it first.");
                            System.exit(0);
                        }
                    }
                }
            }

            // Clear the staging area
            List<String> filesN = Utils.plainFilenamesIn(GITLET_DIR + "/staging");
            for (String file : filesN) {
                File file1 = new File(GITLET_DIR + "/staging/" + file);
                file1.delete();
            }

            // No tracked files would be overwritten, for each file tracked by the commit, checkout file
            for (String filename : resetFiles) {
                // Loop through each tracked file
                this.checkout(filename, commit);
            }

            // Remove tracked files not in the previous commit
            for (String file : files) {
                if (!resetFiles.contains(file)) {
                    // If the currently tracked file doesn't exist in the reset file, remove it
                    File file1 = new File(file);
                    file1.delete();
                }
            }

            // Update the head pointer
            branches.get(workingBranch).head = commit;
            this.saveRepo();
        } else {
            System.out.println("No commit with that id exists.");
        }
    }

    /** Merges branch BRANCH into current working branch */
    public void merge(String branch) {

        // Ensure no uncommitted changed
        if (stagedRemoval.size() > 0 || new File(GITLET_DIR + "/staging").listFiles().length > 0) {
            System.out.println("You have uncommitted changes.");
            System.exit(0);
        }

        // Ensure that the branch exists
        if (!branches.containsKey(branch)) {
            System.out.println("A branch with that name does not exist.");
            System.exit(0);
        }

        // Ensure the branch is not itself
        if (branch.equals(workingBranch)) {
            System.out.println("Cannot merge a branch with itself.");
            System.exit(0);
        }

        boolean mergeConflict = false;

        // Get references to the current branch and the one to merge files from
        Branch otherBranch = branches.get(branch);
        Branch currentBranch = branches.get(workingBranch);

        // Step 1: find the split point
        // Iterate through the current branch's commits (starting from head) and find a common split point
        String head = currentBranch.head;
        String splitSHA = "";
        while (head != null) {
            String otherHead = otherBranch.head;
            while (otherHead != null) {
                if (otherHead.equals(head)) {
                    splitSHA = head;
                    break;
                }
                otherHead = Commit.readCommit(otherHead).getParent();
            }
            head = Commit.readCommit(head).getParent();
        }

        if (splitSHA.equals(otherBranch.head)) {
            System.out.println("Given branch is an ancestor of the current branch.");
            System.exit(0);
        } else if (splitSHA.equals(currentBranch.head)) {
            System.out.println("Current branch fast-forwarded.");
            this.checkoutBranch(branch);
            System.exit(0);
        }

        if (!splitSHA.equals("")) {
            // Found a valid split point

            Commit split = Commit.readCommit(splitSHA);
            Commit current = Commit.readCommit(currentBranch.head);
            Commit other = Commit.readCommit(otherBranch.head);

            // Ensure no untracked files in the current branch would be overwritten by a merge
            checkOverwriteUntracked(current, other);
            checkOverwriteUntracked(current, split);

            /* 1. Any files modified in otherBranch since split, but not in current branch
                  since the split should be checked out from the front of the given branch and staged
             */

            // Loop through each file in the other branch
            for (String file : other.getBlobs().keySet()) {

                // Check if the split point contains the file in other branch
                if (split.getBlobs().containsKey(file)) {
                    // File exists, check if the contents are the same
                    if (other.getBlobs().get(file).equals(split.getBlobs().get(file))) {
                        // other branch file is same as the split... do nothing
                    } else {
                        // Contents are different than the split point, check if the current file is same as split point
                        if (current.getBlobs().get(file).equals(split.getBlobs().get(file))) {
                            // Current is same as split, but other branch differ. Set current to be other branch contents.
                            HashMap<String, String> blobs = current.getBlobs();
                            blobs.put(file, other.getBlobs().get(file));
                            this.add(file);
                        }
                    }
                } else {
                    // Split point does not contain the file, check if the current branch has the file
                    if (current.getBlobs().containsKey(file)) {
                        // Check if they're different
                        if (!current.getBlobs().get(file).equals(other.getBlobs().get(file))) {
                            // Files are different, MERGE CONFLICT
                            File newFile = new File(file);
                            Utils.writeContents(newFile, "<<<<<<< HEAD\n", Blob.readBlob(current.getBlobs().get(file)).getFile(), "=======", Blob.readBlob(other.getBlobs().get(file)).getFile(), ">>>>>>>");
                            this.add(file);
                            mergeConflict = true;
                        } // if the files are the same, do nothing
                    } else {
                        // File doesnt exist in current branch, check it out and stage it
                        this.checkout(file, otherBranch.head);
                        this.add(file);
                    }
                }
            }

            // Loop through each file in the current branch
            for (String file : current.getBlobs().keySet()) {
                // Check if the current branch file differs from the split point
                if (split.getBlobs().containsKey(file) && !current.getBlobs().get(file).equals(split.getBlobs().get(file))) {
                    // Check if the other branch file is the same as the split point
                    if (other.getBlobs().get(file).equals(split.getBlobs().get(file))) {
                        // Set the file in the CWD to be equal to the other branch
                        HashMap<String, String> blobs = other.getBlobs();
                        blobs.put(file, current.getBlobs().get(file));
                        this.add(file);
                    }
                } else if (!split.getBlobs().containsKey(file)) {
                    // Check if the split point does not contain the file in the current branch

                    // Check if the other branch has the file
                    if (other.getBlobs().containsKey(file)) {
                        // Check if they're the same
                        if (!current.getBlobs().get(file).equals(other.getBlobs().get(file))) {
                            // Files are different, MERGE CONFLICT
                            File newFile = new File(file);
                            Utils.writeContents(newFile, "<<<<<<< HEAD\n", Blob.readBlob(current.getBlobs().get(file)).getFile(), "=======", Blob.readBlob(other.getBlobs().get(file)).getFile(), ">>>>>>>");
                            this.add(file);
                            mergeConflict = true;
                        }
                    } else {
                        // File doesnt exist in current branch, check it out and stage it
                        this.checkout(file, currentBranch.head);
                        this.add(file);
                    }
                }
            }

            // Loop through each file at the split (check for removals)
            for (String file : split.getBlobs().keySet()) {

                if (current.getBlobs().containsKey(file) && !other.getBlobs().containsKey(file)) {
                    // Current branch HAS the file, other branch DOESN'T
                    // Check if the current branch's file is the same as the split point (if so, remove it)
                    if (current.getBlobs().get(file).equals(split.getBlobs().get(file))) {
                        // Blobs are identical, stage the file for removal
                        this.remove(file);
                    } else {
                        // The file differs from the split point (but is removed in other branch). MERGE CONFLICT
                        File newFile = new File(file);
                        Utils.writeContents(newFile, "<<<<<<< HEAD\n", Blob.readBlob(current.getBlobs().get(file)).getFile(), "=======", Blob.readBlob(other.getBlobs().get(file)).getFile(), ">>>>>>>");
                        this.add(file);
                        mergeConflict = true;
                    }
                } else if (!current.getBlobs().containsKey(file) && other.getBlobs().containsKey(file)) {
                    // Current branch DOESN'T have file, other branch DOES
                    // Check if the current branch's file is the same as the split point (if so, remove it)
                    if (!other.getBlobs().get(file).equals(split.getBlobs().get(file))) {
                        // The file differs from the split point (but is removed in current). MERGE CONFLICT
                        File newFile = new File(file);
                        Utils.writeContents(newFile, "<<<<<<< HEAD\n", Blob.readBlob(current.getBlobs().get(file)).getFile(), "=======", Blob.readBlob(other.getBlobs().get(file)).getFile(), ">>>>>>>");
                        this.add(file);
                        mergeConflict = true;
                    } // else: do nothing. current branch already removed the file
                }
            }

            if (mergeConflict) {
                System.out.println("Encountered a merge conflict.");
            }

            Commit.saveCommit(current, currentBranch.head);
            Commit.saveCommit(other, otherBranch.head);
            this.saveRepo();
            this.commit("Merged " + otherBranch.name + " into " + currentBranch.name);
            this.saveRepo();
        } else {
            System.out.println("No valid split point found.");
            System.exit(0);
        }
    }

    // Checks if CURRENT would have an untracked file overwritten
    private void checkOverwriteUntracked(Commit current, Commit other) {
        for (String file : other.getBlobs().keySet()) {
            // For each file in other branch, check if its tracked in current branch
            if (!current.getBlobs().containsKey(file) && (new File(file)).exists() && !Arrays.equals(Blob.readBlob(other.getBlobs().get(file)).getFile(), readContents(new File(file)))) {
                // Current file is untracked and differs, exit
                System.out.println("There is an untracked file in the way; delete it, or add and commit it first.");
                System.exit(0);
            }
        }
    }
}































