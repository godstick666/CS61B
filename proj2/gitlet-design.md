# Gitlet Design Document

**Name**: Xinpeng WU

## Classes and Data Structures
### Repository

#### Instance variables

1. CWD -- Current working directory
2. GITLET_DIR -- .gitlet dir
3. STAGING_AREA -- .gitlet/stagingArea dir for staging added files
4. ADDEDFILES -- File persists Map<String, String> Object instantiated with TreeMap. Key: Relative pathname of a file; value: SHA-1 hash
5. REMOVEDFILES -- File persists SortedSet<String> Object instantiated with TreeSet. Item: Relative pathname of a file
6. COMMITS -- The commits object dir, each commit stored in commitID.substring(0, 2)/commitID.substring(2)
7. BLOBS -- The blobs dir
8. HEAD -- The currently active pointer file
9. BRANCHES -- The branch pointers dir

#### Methods
##### Basic Methods
1. init() -- DIRS.mkdir(), write contents to HEAD, make initial commit
2. add(String source) -- Only one file may be added at a time. Compare the file in CWD with the current commit version, if identical do not add it and delete it if it has been there. Finally, copy the added file to specific dir.
3. commit(String message) -- Create new commit object and clone the HEAD commit's blobs, iterate over ADDEDFILES and REMOVEDFILES to update the current blobs.
4. remove(String target) -- Unstage the corresponding file in staging area if it exists. Or, if the file is tracked, stage it for removal and delete it if it exists. Or, print error message.
5. WARNING: log() -- something warning about corporation with Help methods 1.2. Start with head commit, following the first parent commit links, print commit's ID, timestamp and message. 
6. TOBEIMPROVED: globalLog() -- Iterate over all commit objects.
7. TOBEIMPROVED: find(String commitMsg) -- Iterate over all commit objects.
8. status() -- Displays 5 categories: branches, files staged for addition and removal, modified but not staged files and untracked Files. **Natural ordering implementation: new TreeSet(collection); collection.sort(null). Method reference: .forEach(System.out::println).** 
9. checkoutFile(String fileName) -- Checkout the file of head commit, not staged it.
10. checkoutCommitFile(String commitID, String fileName) -- Similar to 9.
11. checkoutBranch(String branchName) -- Checkout all the files of new branch, remove all the untracked files in current branch.
12. branch(String branchName) -- Create a new branch, point it to head commit.
13. rmBranch(String branchName) -- Delete the specific branch (just the pointer associated with the branch)
14. reset(String commitID) -- Checkout the file of specific commit

##### Help Methods (Usage Frequency in different public method) 
1. (1)getCommitID(String shortenedID) -- return complete commit ID with shortened ID input, the commit object is stored in COMMITS/commitID.substring(0, 2)/commitID.substring(2) so that we can locate the object using shortened UID.
2. (6)getCommit(String commitID) -- return the specific commit object using commitID(maybe a shortened one)
3. (3)getHeadCommitID() -- get HEAD commitID
4. (5)getHeadCommit() -- get HEAD commit object
5. (2)storeNewCommit(Commit newCommit) -- store the new commit in COMMITS dir and update the branch pointer.
6. (2)printLogStyleSet() -- return a SimpleDateFormat object representing specific print format.
7. (2)printLogInfo(String commitID, Commit commit, SimpleDateFormat sdf) -- print commitID, plus 2 parents' commitID for merged commit, timestamp of commit using sdf and commit message.
8. (1)getCurrentBlobs() -- return current state blobs which is a Map<String, String> object
9. (3)getDirPlainFiles() -- return SortedSet<String> object of plain files with specific dir
10. (1)TOBEIMPROVED: getMNSFC(Map<String, String> currentBlobs, SortedSet<String> cwdFiles) -- iterate over current state blobs, return MNSFC List<String> (sorted) object, the rest of cwdFiles is Untracked Files
11. (2)checkoutFile(Commit commit, String fileName) -- Core method of checkout(). Put commit version file into cwd and do not stage it.
12. (2)TOBEIMPROVED: recreateCwdWithCommit(Map<String, String> currentBlobs, SortedSet<String> cwdFiles) -- Fill the cwd with files of specific commit, the rest of cwdFiles is Untracked Files.
13. (2)deleteDirFiles(File dir) -- Delete plain files in specific dir
14. (1)getUntrackFiles(Map<String, String> currentBlobs, SortedSet<String> cwdFiles) -- return List<String> object of untracked files.

##### ErrorDetect Methods
1. checkDIR() -- check if cwd contains .gitlet dir
2. checkARGS(int correctNum, int actualNum) -- check if the number of OPERANDS is correct. Plus, for special case, set correctNum = -1, actualNum = 0 just to print error message.

### Commit

#### Instance variables

1. Message -- The message of a commit.
2. Timestamp -- The instant when committing. Assigned by the constructor.
3. Parent -- List<String> The parent commit of a commit.
4. blobs -- Map<String, String>

#### Constructor

1. Commit(String message, Commit parent) -- Assign the message, parent to corresponding instance variables and set the timestamp using Date(0) to represent “The (Unix) Epoch” and Date() to represent the instant when commit is created.

#### Methods

1. 4 getInstanceVariables methods -- return corresponding instance variables.
2. setBlobs(Map<String, String> Blobs) -- set the commit object with the specific blobs




## Algorithms

## Persistence
1. ADDEDFILES
2. REMOVEDFILES
3. COMMITS