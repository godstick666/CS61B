package gitlet;

import java.io.File;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.*;

import static gitlet.Utils.*;
import static gitlet.Utils.readContentsAsString;

// TODO: any imports you need here

/** Represents a gitlet repository.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 *  @author Xinpeng WU
 */
public class Repository implements Serializable {
    /**
     * TODO: add instance variables here.
     *
     * List all instance variables of the Repository class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided two examples for you.
     */

    /** The current working dir. */
    public static final File CWD = new File(System.getProperty("user.dir"));
    /** The .gitlet dir. */
    public static final File GITLET_DIR = join(CWD, ".gitlet");
    /** The staging area in .gitlet */
    public static final File STAGING_AREA = join(GITLET_DIR, "stagingArea");
    public static final File ADDEDFILES = join(GITLET_DIR, ".addedfiles");
    public static final File REMOVEDFILES = join(GITLET_DIR, ".removedfiles");
    /** The commits object dir */
    public static final File COMMITS = join(GITLET_DIR, "commits");
    /** The blobs dir */
    public static final File BLOBS = join(GITLET_DIR, "blobs");
    /** The currently active pointer file */
    public static final File HEAD = join(GITLET_DIR, "HEAD");
    /** The branch pointers dir*/
    public static final File BRANCHES = join(GITLET_DIR, "branches");

    /** Creates a new Gitlet version-control system in the current directory.
     *  This system will automatically start with one commit: a commit that contains no files
     *  and has the commit message initial commit (just like that, with no punctuation).
     *  It will have a single branch: master, which initially points to this initial commit,
     *  and master will be the current branch. The timestamp for this initial commit will be
     *  00:00:00 UTC, Thursday, 1 January 1970 in whatever format you choose for dates
     *  (this is called “The (Unix) Epoch”, represented internally by the time 0.) Since the
     *  initial commit in all repositories created by Gitlet will have exactly the same content,
     *  it follows that all repositories will automatically share this commit (they will all have
     *  the same UID) and all commits in all repositories will trace back to it.
     *  Failure cases :
     *  If there is already a Gitlet version-control system in the current directory, it should abort.
     *  It should NOT overwrite the existing system with a new one. Should print the error message
     *  "A Gitlet version-control system already exists in the current directory." */
    public static void init (){
        if (GITLET_DIR.exists()){
            System.out.println("A Gitlet version-control system already exists in the current directory.");
            return;
        }// failure cases
        GITLET_DIR.mkdir();
        STAGING_AREA.mkdir();
        COMMITS.mkdir();
        BRANCHES.mkdir();
        BLOBS.mkdir();
        writeContents(HEAD, "master");
        Commit initial = new Commit ("initial commit");
        storeNewCommit(initial);
    }
    /** return the map mapping added files with their hashID */
    private static Map<String, String> getAddedFiles(){
        Map<String, String> addedFiles = ADDEDFILES.exists() ?
                readObject(ADDEDFILES, TreeMap.class) : new TreeMap<>();
        return addedFiles;
    }
    /** return the set that contains removed files' name */
    private static SortedSet<String> getRemovedFiles(){
        SortedSet<String> removedFiles = REMOVEDFILES.exists() ?
                readObject(REMOVEDFILES, TreeSet.class) : new TreeSet<>();
        return removedFiles;
    }
    /** Adds a copy of the file as it currently exists to the staging area
     * (see the description of the commit command). For this reason, adding
     * a file is also called staging the file for addition. Staging an
     * already-staged file overwrites the previous entry in the staging area
     * with the new contents. The staging area should be somewhere in .gitlet.
     * If the current working version of the file is identical to the version
     * in the current commit, do not stage it to be added, and remove it from
     * the staging area if it is already there (as can happen when a file is
     * changed, added, and then changed back to it’s original version). The
     * file will no longer be staged for removal (see gitlet rm), if it was
     * at the time of the command. 
     * ATTENTION: Significant modification, add addedFiles object to record Relative
     * Paths file name with its SHA1 ID. */
    public static void add (String source){
        File sourceFile = join(CWD, source);
        if (!sourceFile.isFile() || !sourceFile.exists()) {
            System.out.println("File does not exist.");
            return;
        }// failure cases
        byte[] sourceFileContents = readContents(sourceFile);
        String sourceFileID = sha1(sourceFileContents);
        String committedFileID = getHeadCommit().getBlobs().get(source);
        Map<String, String> addedFiles = getAddedFiles();
        String stagedFileID = addedFiles.get(source);
        File stagedFile = join(STAGING_AREA, stagedFileID);
        if (sourceFileID.equals(committedFileID)){
            // using SHA1 to determine whether source is identical to current commit file
            if (stagedFile.exists()){
                addedFiles.remove(source);
                restrictedDelete(stagedFile);
                writeObject(ADDEDFILES, (Serializable) addedFiles);
            }
        }else if (!sourceFileID.equals(stagedFileID)){
            if (stagedFile.exists()){ restrictedDelete(stagedFile);}
            addedFiles.put(source, sourceFileID);
            File destFile = join(ADDEDFILES, sourceFileID);
            writeContents(destFile, sourceFileContents); //copy the file to specific file path
            writeObject(ADDEDFILES, (Serializable) addedFiles);
        }
    }
    /** Saves a snapshot of tracked files in the current commit and staging area,
     * so they can be restored at a later time, creating a new commit. The commit
     * is said to be tracking the saved files. By default, each commit’s snapshot
     * of files will be exactly the same as its parent commit’s snapshot of files;
     * it will keep versions of files exactly as they are, and not update them. A
     * commit will only update the contents of files it is tracking that have been
     * staged for addition at the time of commit, in which case the commit will now
     * include the version of the file that was staged instead of the version it
     * got from its parent. A commit will save and start tracking any files that
     * were staged for addition but weren’t tracked by its parent. Finally, files
     * tracked in the current commit may be untracked in the new commit as a result
     * being staged for removal by the rm command (below).
     * The bottom line: By default a commit has the same file contents as its parent.
     * Files staged for addition and removal are the updates to the commit. Of course,
     * the date (and likely the mesage) will also different from the parent.*/
    public static void commit(String message){
        Map<String, String> addedFiles = getAddedFiles();
        SortedSet<String> removedFiles = getRemovedFiles();
        if (addedFiles.isEmpty() && removedFiles.isEmpty()){
            System.out.println("No changes added to the commit.");
            System.exit(0);
        }
        Commit headCommit = getHeadCommit();
        // read from my computer the HEAD commit object and the staging area
        Commit newCommit = new Commit (message, getHeadCommitID());
        newCommit.setBlobs(new TreeMap<>(headCommit.getBlobs()));
        // clone the HEAD commit's blobs

        for (Map.Entry<String, String> entry : addedFiles.entrySet()){
            String key = entry.getKey(); String value = entry.getValue();
            newCommit.getBlobs().put(key, value);
            File sourceFile = join(STAGING_AREA, value);
            File destFile = join(BLOBS, value);
            writeContents(destFile, readContents(sourceFile));
            restrictedDelete(sourceFile);
        }
        for (String removedFile : removedFiles){
            newCommit.getBlobs().remove(removedFile);
        }
        storeNewCommit(newCommit);
        restrictedDelete(ADDEDFILES);
        restrictedDelete(REMOVEDFILES);
        // using the staging area to modify the files tracked by new commit
        // write back any new object made or any modified object read earlier
    }
    /** Unstage the file if it is currently staged for addition. If the file is
     * tracked in the current commit, stage it for removal and remove the file
     * from the working directory if the user has not already done so (do not
     * remove it unless it is tracked in the current commit).*/
    public static void remove(String target){
        Map<String, String> addedFiles = getAddedFiles();
        SortedSet<String> removedFiles = getRemovedFiles();
        File sourceFile = join(CWD, target);
        String addedFileID = addedFiles.get(target);
        if (addedFileID != null) {
            addedFiles.remove(target);
            writeObject(ADDEDFILES, (Serializable) addedFiles);
            File addedFile = join(STAGING_AREA, addedFileID);
            restrictedDelete(addedFile);
        }else if (getHeadCommit().getBlobs().containsKey(target)){
            removedFiles.add(target);
            writeObject(REMOVEDFILES, (Serializable) removedFiles);
            if (sourceFile.exists()) { restrictedDelete(sourceFile); }
        }else {
            System.out.println("No reason to remove the file.");
            System.exit(0);
        }
    }
    /** Starting at the current head commit, display information about each commit
     * backwards along the commit tree until the initial commit, following the first
     * parent commit links, ignoring any second parents found in merge commits.
     * (In regular Git, this is what you get with git log --first-parent). This set
     * of commit nodes is called the commit’s history. For every node in this history,
     * the information it should display is the commit id, the time the commit was made,
     * and the commit message. Here is an example of the exact format it should follow:
     * */
    public static void log(){
        SimpleDateFormat sdf = printLogStyleSet();
        String commitID = getHeadCommitID(); Commit commit;
        //TODO: it is impossible to be null for commitID and commit object.
        while (true){
            commit = getCommit(commitID);
            printLogInfo(commitID, commit, sdf);
            if (commit.getParents().isEmpty()){ break; } // current commit is initial commit
            commitID = commit.getParents().get(0);
        }
    }

    private static SimpleDateFormat printLogStyleSet(){
        SimpleDateFormat sdf = new SimpleDateFormat("E MMM d HH:mm:ss y, z", Locale.US);
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        return sdf;
    }

    private static void printLogInfo(String commitID, Commit commit, SimpleDateFormat sdf){
        System.out.println("===");
        System.out.println("commit " + commitID);
        if(commit.getParents().size() >= 2){
            System.out.println("Merge: ");
            for (String str : commit.getParents()){
                System.out.print(str.substring(0, 8) + "  ");
            }
        }
        System.out.println("Date:");
        System.out.print(sdf.format(commit.getTimestamp()));
        System.out.println(commit.getMessage());
        System.out.println();
    }

    /** Like log, except displays information about all commits ever made.
     * The order of the commits does not matter. Hint: there is a useful method in 
     * gitlet.Utils that will help you iterate over files within a directory. */
    public static void globalLog(){
        SimpleDateFormat sdf = printLogStyleSet();
        for (String uidPrefix : plainFilenamesIn(COMMITS)){
            for (String uidSuffix : plainFilenamesIn(join(COMMITS, uidPrefix))){
                String UID = uidPrefix + uidSuffix;
                printLogInfo(UID, getCommit(UID), sdf);
            }
        }
    }
    /** Prints out the ids of all commits that have the given commit message, one per
     *  line. If there are multiple such commits, it prints the ids out on separate
     *  lines. The commit message is a single operand; to indicate a multiword message,
     *  put the operand in quotation marks, as for the commit command below.
     *  Failure cases: If no such commit exists, prints the error message Found no commit
     *  with that message.*/
    public static void find(String commitMsg){
        boolean suchCommitsExit = false;
        for (String commitUidPrefix : plainFilenamesIn(COMMITS)){
            for (String commitUidSuffix : plainFilenamesIn(join(COMMITS, commitUidPrefix))){
                String commitID = commitUidPrefix + commitUidSuffix;
                Commit commit = getCommit(commitID);
                if (commit.getMessage().equals(commitMsg)){
                    System.out.println(commitID);
                    suchCommitsExit = true;
                }
            }
        }
        if (suchCommitsExit){
            System.out.println("Found no commit with that message.");
        }
    }
    /** Displays what branches currently exist, and marks the current branch
     *  with a *. Also displays what files have been staged for addition or
     *  removal.
     *  A file in the working directory is “modified but not staged” if it is     *
     *      1. Tracked in the current commit, changed in the working directory, but not staged; or
     *      2. Staged for addition, but with different contents than in the working directory; or
     *      3. Staged for addition, but deleted in the working directory; or
     *      4. Not staged for removal, but tracked in the current commit and deleted from the working directory.
     * The final category (“Untracked Files”) is for files present in the working directory
     *      but neither staged for addition nor tracked. This includes files that have been staged
     *      for removal, but then re-created without Gitlet’s knowledge. Ignore any subdirectories
     *      that may have been introduced, since Gitlet does not deal with them.*/
    public static void status(){
        System.out.println("=== Branches ===");
        System.out.println("*" + readContentsAsString(HEAD));
        for(String branch : new TreeSet<>(plainFilenamesIn(BRANCHES))){
            if (branch.equals("master")) continue;
            System.out.println(branch);
        }
        System.out.println("=== Staged Files ===");
        Map<String, String> addedFiles = getAddedFiles();
        addedFiles.keySet().forEach(System.out::println);
        System.out.println();
        System.out.println("=== Removed Files ===");
        SortedSet<String> removedFiles = getRemovedFiles();
        removedFiles.forEach(System.out::println);
        System.out.println();
        System.out.println("=== Modifications Not Staged For Commit ===");
        List<SortedSet<String>> res = iterateWorkingDIRFiles();
        res.get(0).forEach(System.out::println);
        System.out.println();
        System.out.println("=== Untracked Files ===");
        res.get(1).forEach(System.out::println);
        System.out.println();
    }
    /** Using recursion to get all files relative path to CWD*/
    private static void getWorkingDIRFiles(File[] files, SortedSet<String> workingDIRFiles){
        for (File f : files){
            if (f.isFile()){
                workingDIRFiles.add(f.toString().substring(CWD.toString().length(), -1));
            }else {
                getWorkingDIRFiles(f.listFiles(), workingDIRFiles);
            }
        }
    }
    /** Get 2 sortedsets contain files relative name of Modifications Not Staged For Commit
     *  & Untracked Files respectively*/
    private static List<SortedSet<String>> iterateWorkingDIRFiles(){
        Map<String, String> addedFiles = getAddedFiles();
        SortedSet<String> removedFiles = getRemovedFiles();
        // get current state blobs
        Map<String, String> currentBlobs = new TreeMap<>(getHeadCommit().getBlobs());
        for (Map.Entry<String, String> entry : addedFiles.entrySet()){
            currentBlobs.put(entry.getKey(), entry.getValue());
        }
        for (String removedFile : removedFiles){
            currentBlobs.remove(removedFile);
        }
        // get all files' relative path name in working dir
        SortedSet<String> workingDIRFiles = new TreeSet<>();
        getWorkingDIRFiles(CWD.listFiles(file -> {
            return !file.isHidden() && !file.getName().equals("gitlet");
        }), workingDIRFiles); // lambda expression for getting workingDIR
        // iterate over current state blobs
        SortedSet<String> MNSFC = new TreeSet<>(); //Modifications Not Staged For Commit
        // the rest of workingDIRFiles is Untracked Files
        for (Map.Entry<String, String> entry : currentBlobs.entrySet()){
            String key = entry.getKey(); String value = entry.getValue();
            if (workingDIRFiles.remove(key)){ // return true if the set contained the specified element
                File sourceFile = join(CWD, key);
                String sourceFileID = sha1(readContents(sourceFile));
                if (!sourceFileID.equals(value)){ MNSFC.add(key); } // rule 1. 2.
            }else {
                MNSFC.add(key); // rule 3. 4.
            }
        }
        List<SortedSet<String>> result = new ArrayList<>();
        result.add(MNSFC); result.add(workingDIRFiles);
        return result;
    }
    /** Checkout is a kind of general command that can do a few different things
     * depending on what its arguments are. There are 3 possible use cases. In each
     * section below, you’ll see 3 numbered points. Each corresponds to the respective
     * usage of checkout.
     * Usage:
     * 1. java gitlet.Main checkout -- [file name]
     * 2. java gitlet.Main checkout [commit id] -- [file name]
     * 3. java gitlet.Main checkout [branch name]
     * Descriptions:
     * 1. Takes the version of the file as it exists in the head commit and puts it in
     *    the working directory, overwriting the version of the file that’s already there
     *    if there is one. The new version of the file is not staged.
     * 2. Takes the version of the file as it exists in the commit with the given id, and
     *    puts it in the working directory, overwriting the version of the file that’s
     *    already there if there is one. The new version of the file is not staged.
     * 3. Takes all files in the commit at the head of the given branch, and puts them in
     *    the working directory, overwriting the versions of the files that are already
     *    there if they exist. Also, at the end of this command, the given branch will
     *    now be considered the current branch (HEAD). Any files that are tracked in the
     *    current branch but are not present in the checked-out branch are deleted. The
     *    staging area is cleared, unless the checked-out branch is the current branch
     *    (see Failure cases below).*/
    public static void checkoutFile(String fileName){
        Commit commit = getHeadCommit();
        checkoutFile(commit, fileName);
    }
    private static void checkoutFile(Commit commit, String fileName){
        String fileID = commit.getBlobs().get(fileName);
        if (fileID == null){
            System.out.println("File does not exist in that commit.");
            System.exit(0);
        }
        File source = join(BLOBS, fileID);
        File dest = join(CWD, fileName);
        writeContents(dest, readContents(source));
        // remove the staged file if it's there
        File stagedFile = join(STAGING_AREA, fileID);
        Map<String, String> addedFiles = getAddedFiles();
        if (stagedFile.exists()){
            addedFiles.remove(fileName);
            restrictedDelete(stagedFile);
            writeObject(ADDEDFILES, (Serializable) addedFiles);
        }
    }
    public static void checkoutCommitFile(String commitID, String fileName){
        File uidPrefix = join(COMMITS, commitID.substring(0, 2));
        if (uidPrefix.exists()){
            for (String uidSuffix : plainFilenamesIn(uidPrefix)){
                if (uidSuffix.equals(commitID.substring(2))){
                    checkoutFile(getCommit(commitID), fileName);
                    return;
                }
            }
        }
        System.out.println("No commit with that id exists.");
    }
    public static void checkoutBranch(String branchName){
        File branch = join(BRANCHES, branchName);
        if (!branch.exists()){
            System.out.println("No such branch exists.");
            System.exit(0);
        }
        if (branchName.equals(readContentsAsString(HEAD))){
            System.out.println("No need to checkout the current branch.");
            System.exit(0);
        }
        clearCWDandCopyFilesInSpecificCommit(getCommit(readContentsAsString(branch)));
        writeContents(HEAD, branchName);
    }
    public static void clearCWDandCopyFilesInSpecificCommit(Commit currentCommit){
        clearFiles(CWD.listFiles(file -> {
            return !file.isHidden() && !file.getName().equals("gitlet");
        }));
        for (Map.Entry<String, String> entry : currentCommit.getBlobs().entrySet()){
            File source = join(BLOBS, entry.getValue());
            File dest = join(CWD, entry.getKey());
            writeContents(dest, readContents(source)); // can not mkdir and create file directly?
        }
    }
    private static void clearFiles(File[] dir){
        for (File f : dir){
            if (f.isFile()){
                restrictedDelete(f);
            }else if (f.isDirectory() && f.list().length == 0){
                f.delete();
            }else {
                clearFiles(f.listFiles());
                f.delete();
            }
        }
    }
    /** Creates a new branch with the given name, and points it at the current head commit.
     * A branch is nothing more than a name for a reference (a SHA-1 identifier) to a commit
     * node. This command does NOT immediately switch to the newly created branch (just as
     * in real Git). Before you ever call branch, your code should be running with a default
     * branch called “master”.*/
    public static void branch(String branchName){
        File newBranch = join(BRANCHES, branchName);
        if (newBranch.exists()){
            System.out.println("A branch with that name already exists.");
            System.exit(0);
        }
        writeContents(newBranch, getHeadCommitID());
    }
    /**  Deletes the branch with the given name. This only means to delete the pointer
     * associated with the branch; it does not mean to delete all commits that were
     * created under the branch, or anything like that.*/
    public static void rmBranch(String branchName){
        File branch = join(BRANCHES, branchName);
        if (!branch.exists()){
            System.out.println("A branch with that name does not exist.");
            System.exit(0);
        }
        if (branchName.equals(readContentsAsString(HEAD))){
            System.out.println("Cannot remove the current branch.");
            System.exit(0);
        }
        restrictedDelete(branch);
    }
    /** Checks out all the files tracked by the given commit. Removes tracked files
     *  that are not present in that commit. Also moves the current branch’s head to
     *  that commit node. See the intro for an example of what happens to the head
     *  pointer after using reset. The [commit id] may be abbreviated as for checkout.
     *  The staging area is cleared. The command is essentially checkout of an arbitrary
     *  commit that also changes the current branch head.
     *  Failure case: If no commit with the given id exists, print No commit with that
     *  id exists. If a working file is untracked in the current branch and would be
     *  overwritten by the reset, print `There is an untracked file in the way; delete
     *  it, or add and commit it first.` and exit; perform this check before doing
     *  anything else.*/
    public static void reset(String commitID){
        File inFile = join(COMMITS, commitID.substring(0, 2));
        Commit commit = null; String commitRealID = null;
        for (File f : inFile.listFiles()){
            if (f.getName().startsWith(commitID.substring(2))){
                commit = readObject(inFile, Commit.class);
                commitRealID = commitID.substring(0, 2) + f.getName();
            }
        }
        if (commit == null){
            System.out.println("No commit with that id exists.");System.exit(0);
        }
        if (!getUntrackedFiles(commit).isEmpty()){
            System.out.println("There is an untracked file in the way; delete it, or add and commit it first.");
            System.exit(0);
        }
        clearCWDandCopyFilesInSpecificCommit(commit);
        writeContents(join(BRANCHES, readContentsAsString(HEAD)), commitRealID);
        clearFiles(STAGING_AREA.listFiles());
    }
    private static SortedSet<String> getUntrackedFiles(Commit commit){
        SortedSet<String> workingDIRFiles = new TreeSet<>();
        getWorkingDIRFiles(CWD.listFiles(file -> {
            return !file.isHidden() && !file.getName().equals("gitlet");
        }), workingDIRFiles);
        for(Map.Entry<String, String> entry : commit.getBlobs().entrySet()){
            workingDIRFiles.remove(entry.getKey());
        }
        return workingDIRFiles;
    }
    /** store commit in specific path using UID */
    private static void storeNewCommit(Commit newCommit){        
        String UID = sha1(newCommit);
        File outFile = join(COMMITS, UID.substring(0, 2), UID.substring(2));
        writeObject(outFile, newCommit);
        File currentBranch = join(BRANCHES, readContentsAsString(HEAD));
        writeContents(currentBranch, UID);
    }
    /** return the specific commit object using commitID*/
    private static Commit getCommit(String commitID){
        if (commitID.length() != 40) { commitID = getCommitID(commitID); }
        File commit = join(COMMITS, commitID.substring(0, 2), commitID.substring(2));
        return commit.exists() ? readObject(commit, Commit.class) : null;
    }
    /** return complete commit ID with shortened ID input */
    private static String getCommitID(String shortenedID){
        if (shortenedID.length() < 4) { System.exit(0); } //like real git except throwing error message
        File uidPrefix = join(COMMITS, shortenedID.substring(0, 2));
        for (File uidSuffix : uidPrefix.listFiles()){
            if (uidSuffix.getName().startsWith(shortenedID.substring(2))){
                return uidPrefix.getName() + uidSuffix.getName();
            }
        }
        return null;
    }
    /** get HEAD commitID & commit object */
    private static String getHeadCommitID(){
        return readContentsAsString(join(BRANCHES, readContentsAsString(HEAD)));
    }
    private static Commit getHeadCommit(){
        return getCommit(getHeadCommitID());
    }

    public static void checkDIR(){
        if (!GITLET_DIR.exists()){
            System.out.println("Not in an initialized Gitlet directory.");
            System.exit(0);
        }
    }
    //Special case: Set correctNum = -1, actualNum = 0 just to print error message
    public static void checkARGS(int correctNum, int actualNum){
        if (actualNum != correctNum){
            System.out.println("Incorrect operands.");
            System.exit(0);
        }
    }
}
