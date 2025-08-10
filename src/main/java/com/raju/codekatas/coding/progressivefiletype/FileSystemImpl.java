package com.raju.codekatas.coding.progressivefiletype;

import java.util.HashMap;
import java.util.Map;

/**
 * 📁 Progressive In-Memory File System
 * <p>
 * 🧩 Objective:
 * Design and implement a simplified, progressive in-memory file system that supports:
 * - Creating directories and files
 * - Checking if a path exists
 * - Deleting files or directories
 * - Taking snapshots of the current state
 * - Restoring to a previously taken snapshot
 * <p>
 * 💡 Concepts:
 * - The file system starts with a root directory: "/"
 * - A path can represent either a file or a directory.
 * - Paths are Unix-style: "/a/b/c", "/data/file1", etc.
 * - Intermediate directories must already exist when creating a new path.
 * - Snapshots are point-in-time copies of the file system state.
 * <p>
 * 🔄 Progressive Features:
 * <p>
 * 1️⃣ Stage 1 — Basic Path Management:
 * - boolean createPath(String path, boolean isFile)
 * => Create a file or directory at the specified path.
 * Return false if:
 * - The path already exists
 * - The parent directory does not exist
 * - The path is invalid
 * <p>
 * - boolean exists(String path)
 * => Returns true if the path (file or directory) exists.
 * <p>
 * 2️⃣ Stage 2 — Deletion:
 * - boolean deletePath(String path)
 * => Deletes the file or directory at the path.
 * If a directory is deleted, all children should also be removed recursively.
 * Return false if:
 * - Path does not exist
 * - Attempting to delete the root "/"
 * <p>
 * 3️⃣ Stage 3 — Snapshot:
 * - String snapshot()
 * => Takes a snapshot of the current file system.
 * Returns a unique snapshot ID (e.g., "snap1", "snap2", ...)
 * <p>
 * 4️⃣ Stage 4 — Restore:
 * - boolean restore(String snapshotId)
 * => Restores the file system to the state captured in the given snapshot.
 * Returns false if snapshot ID is invalid.
 * <p>
 * <p>
 * ✅ Assumptions:
 * - Root directory ("/") always exists.
 * - File and directory names do not contain slashes (e.g., "/a/b.txt" is valid, "/a/b/txt/" is not).
 * - Paths are case-sensitive.
 * - You don’t need to store file contents.
 * - Snapshots are isolated — modifying current FS doesn’t affect snapshots.
 * <p>
 * <p>
 * 🧪 Example Usage:
 * <p>
 * FileSystem fs = new FileSystemImpl();
 * <p>
 * // Stage 1
 * fs.createPath("/a", false);         // true
 * fs.createPath("/a/b", false);       // true
 * fs.createPath("/a/b/file.txt", true); // true
 * <p>
 * fs.exists("/a");                    // true
 * fs.exists("/a/b/file.txt");         // true
 * fs.exists("/x");                    // false
 * <p>
 * // Stage 2
 * fs.deletePath("/a/b");              // true
 * fs.exists("/a/b");                  // false
 * fs.exists("/a/b/file.txt");         // false
 * <p>
 * // Stage 3 & 4
 * fs.createPath("/data", false);             // true
 * fs.createPath("/data/file1", true);        // true
 * <p>
 * String snap1 = fs.snapshot();              // "snap1"
 * <p>
 * fs.createPath("/data/file2", true);        // true
 * fs.deletePath("/data/file1");              // true
 * <p>
 * fs.exists("/data/file1");                  // false
 * fs.exists("/data/file2");                  // true
 * <p>
 * fs.restore(snap1);                         // true
 * fs.exists("/data/file1");                  // true
 * fs.exists("/data/file2");                  // false
 */


interface FileSystem {
    boolean createPath(String path, boolean isFile);

    boolean exists(String path);

    boolean exists(String path, boolean mustBeFile);

    boolean deletePath(String path);

    String snapshot();

    boolean restore(String snapshotId);
}

class FileTrie implements Cloneable {
    private final Map<String, FileTrie> children;
    private boolean isFile;

    public FileTrie() {
        this.isFile = false;
        this.children = new HashMap<>();
    }

    public FileTrie(boolean isFile) {
        this.isFile = isFile;
        this.children = new HashMap<>();
    }

    public boolean isFile() {
        return isFile;
    }

    public void setIsFile(boolean isFile) {
        this.isFile = isFile;
    }

    public Map<String, FileTrie> getChildren() {
        return children;
    }


    @Override
    public FileTrie clone() {
        FileTrie copy = new FileTrie(this.isFile);
        for (Map.Entry<String, FileTrie> entry : this.children.entrySet()) {
            copy.children.put(entry.getKey(), entry.getValue().clone());
        }
        return copy;
    }
}

public class FileSystemImpl implements FileSystem {

    private final Map<String, FileTrie> snapShots;
    private FileTrie currentRootDirectory;
    private String currentSnapShotVersion;

    public FileSystemImpl() {
        currentRootDirectory = new FileTrie();
        currentSnapShotVersion = "snap1";
        snapShots = new HashMap<>();
    }

    @Override
    public boolean createPath(String path, boolean isFile) {
        // ✅ Basic validation:
        // If the path is null, empty, the root "/", or already exists in the system — we do not create it.
        if (path == null || path.isEmpty() || path.equals("/") || exists(path)) {
            return false;
        }

        // 🔹 Split the path into components, skipping the first "/" character
        // Example: "/a/b/c.txt" → ["a", "b", "c.txt"]
        String[] directories = path.substring(1).split("/");

        // Start from the root directory
        FileTrie current = currentRootDirectory;

        // Traverse through the trie for each directory level
        for (int i = 0; i < directories.length; ++i) {
            String dir = directories[i];

            // 🔍 If the current directory level doesn't exist
            if (!current.getChildren().containsKey(dir)) {
                // ❌ If it's not the last segment (i.e., intermediate parent directory), fail
                // Only the final segment is allowed to be newly created — parents must already exist
                if (i != directories.length - 1) {
                    return false;
                }

                // ✅ If this is the final segment (file or directory), create it
                current.getChildren().put(dir, new FileTrie(isFile));
                return true;
            }

            // 🔁 If the directory exists, continue navigating deeper
            current = current.getChildren().get(dir);
        }

        // ⚠️ Edge case: if we reach here, that means the full path already existed
        return false;
    }


    @Override
    public boolean exists(String path) {

        if (path == null || path.isEmpty()) {
            return false;
        }

        if (path.equals("/")) {
            return true;
        }

        String[] directories = path.substring(1).split("/");
        FileTrie current = currentRootDirectory;

        for (int i = 0; i < directories.length; ++i) {
            if (!current.getChildren().containsKey(directories[i])) {
                return false;
            }

            current = current.getChildren().get(directories[i]);
        }
        return true;
    }

    @Override
    public boolean exists(String path, boolean mustBeFile) {

        if (path == null || path.isEmpty() || path.equals("/")) {
            return false;
        }


        String[] directories = path.substring(1).split("/");
        FileTrie current = currentRootDirectory;

        for (int i = 0; i < directories.length; ++i) {
            if (!current.getChildren().containsKey(directories[i])) {
                return false;
            }

            current = current.getChildren().get(directories[i]);
        }


        return current.isFile() == mustBeFile;

    }

    @Override
    public boolean deletePath(String path) {
        // ✅ Step 1: Validate the input path
        // Reject null, empty, or root "/" — we should never delete root.
        // Also return false if the path doesn't exist in the file system.
        if (path == null || path.isEmpty() || path.equals("/") || !exists(path)) {
            return false;
        }

        // ✅ Step 2: Break down the path into components
        // Remove the leading "/" and split by "/" to get directory tokens.
        // Example: "/a/b/c" -> ["a", "b", "c"]
        String[] directories = path.substring(1).split("/");

        // ✅ Step 3: Start recursive deletion from the root
        // We delegate the actual work to a helper method that will
        // traverse the trie and delete the target node.
        return deleteHelper(currentRootDirectory, directories, 0);
    }


    private boolean deleteHelper(FileTrie current, String[] dirs, int index) {
        String dir = dirs[index]; // The current component we are checking

        // ❌ Case 1: If the current node does not contain the next segment, deletion fails.
        if (!current.getChildren().containsKey(dir)) {
            return false;
        }

        // ✅ Case 2: If we've reached the final segment (the node to delete)
        if (index == dirs.length - 1) {
            // Delete the final file/directory from the parent’s children map
            current.getChildren().remove(dir);
        } else {
            // 🔁 Case 3: Recurse deeper into the structure
            FileTrie child = current.getChildren().get(dir);
            boolean deleted = deleteHelper(child, dirs, index + 1);

            // 🧹 After deletion, check if the current child is:
            // 1. Empty (no children)
            // 2. Not a file (i.e., it's a directory)
            // If both are true, prune it from its parent — it's no longer needed.
            if (deleted && child.getChildren().isEmpty() && !child.isFile()) {
                current.getChildren().remove(dir);
            }
        }

        // ✅ Return true to indicate successful deletion (directly or recursively)
        return true;
    }


    @Override
    public String snapshot() {
        currentSnapShotVersion = "snap" + (snapShots.size() + 1);
        snapShots.put(currentSnapShotVersion, currentRootDirectory.clone());
        return currentSnapShotVersion;
    }

    @Override
    public boolean restore(String snapshotId) {

        if (!snapShots.containsKey(snapshotId)) {
            return false;
        }

        currentSnapShotVersion = snapshotId;
        currentRootDirectory = snapShots.get(snapshotId);
        return true;

    }
}
