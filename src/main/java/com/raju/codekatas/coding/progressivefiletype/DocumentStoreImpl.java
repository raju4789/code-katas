package com.raju.codekatas.coding.progressivefiletype;

import java.util.HashMap;
import java.util.Map;

/**
 * Document Versioning System
 * <p>
 * Design and implement a progressive document versioning system.
 * <p>
 * The system supports saving document versions, retrieving them, and rolling back
 * to a previous version.
 * <p>
 * =====================================================
 * ✅ Stage 1: Save versions
 * -----------------------------------------------------
 * - Method: void save(String content)
 * - Stores a new version of the document with the given content.
 * - Version numbers start at 1 and increment by 1 automatically.
 * <p>
 * =====================================================
 * ✅ Stage 2: Retrieve the latest version
 * -----------------------------------------------------
 * - Method: String getLatest()
 * - Returns the most recently saved document content.
 * - Returns null if no versions have been saved yet.
 * <p>
 * =====================================================
 * ✅ Stage 3: Retrieve by version number
 * -----------------------------------------------------
 * - Method: String getVersion(int versionNumber)
 * - Returns the content of the specified version.
 * - If the version number is invalid or deleted, return null.
 * <p>
 * =====================================================
 * ✅ Stage 4: Rollback to a previous version
 * -----------------------------------------------------
 * - Method: void rollback(int versionNumber)
 * - Rolls back to the given version.
 * - All versions after the specified one are discarded.
 * - If the version number is invalid, do nothing.
 * <p>
 * =====================================================
 * ⚠️ Constraints:
 * -----------------------------------------------------
 * - Version numbers are 1-indexed and strictly increasing.
 * - Contents are plain text strings (up to 10,000 characters).
 * - After rollback, new versions continue from the rollback point.
 * <p>
 * =====================================================
 * ✅ Example Usage:
 * -----------------------------------------------------
 * DocumentStore doc = new DocumentStoreImpl();
 * <p>
 * doc.save("Version 1 content");     // version 1
 * doc.save("Version 2 content");     // version 2
 * doc.save("Version 3 content");     // version 3
 * <p>
 * doc.getLatest();                   // → "Version 3 content"
 * doc.getVersion(2);                 // → "Version 2 content"
 * <p>
 * doc.rollback(1);                   // deletes version 2 and 3
 * doc.getLatest();                   // → "Version 1 content"
 * <p>
 * doc.getVersion(3);                 // → null
 * doc.getVersion(2);                 // → null
 */

interface DocumentStore {
    void save(String content);

    String getLatest();

    String getVersion(int versionNumber);

    void rollback(int versionNumber);
}

public class DocumentStoreImpl implements DocumentStore {
    private final Map<Integer, String> versionToContentMap;
    private int latestVersionNumber;

    public DocumentStoreImpl() {
        latestVersionNumber = 0;
        versionToContentMap = new HashMap<>();
    }

    public static void main(String[] args) {
        DocumentStore docStore = new DocumentStoreImpl();

        System.out.println("🔄 Saving versions...");
        docStore.save("Version 1 content");
        docStore.save("Version 2 content");
        docStore.save("Version 3 content");

        System.out.println("✅ getLatest() → " + docStore.getLatest()); // Expected: "Version 3 content"
        System.out.println("✅ getVersion(1) → " + docStore.getVersion(1)); // Expected: "Version 1 content"
        System.out.println("✅ getVersion(2) → " + docStore.getVersion(2)); // Expected: "Version 2 content"
        System.out.println("✅ getVersion(3) → " + docStore.getVersion(3)); // Expected: "Version 3 content"
        System.out.println("✅ getVersion(4) → " + docStore.getVersion(4)); // Expected: null

        System.out.println("🧨 Rolling back to version 1...");
        docStore.rollback(1);

        System.out.println("✅ getLatest() → " + docStore.getLatest()); // Expected: "Version 1 content"
        System.out.println("✅ getVersion(2) → " + docStore.getVersion(2)); // Expected: null
        System.out.println("✅ getVersion(3) → " + docStore.getVersion(3)); // Expected: null

        System.out.println("📝 Saving new version after rollback...");
        docStore.save("New version 2 after rollback");

        System.out.println("✅ getLatest() → " + docStore.getLatest()); // Expected: "New version 2 after rollback"
        System.out.println("✅ getVersion(2) → " + docStore.getVersion(2)); // Expected: "New version 2 after rollback"
    }

    @Override
    public void save(String content) {
        latestVersionNumber += 1;
        versionToContentMap.put(latestVersionNumber, content);
    }

    @Override
    public String getLatest() {
        return versionToContentMap.getOrDefault(latestVersionNumber, null);
    }

    @Override
    public String getVersion(int versionNumber) {
        return versionToContentMap.getOrDefault(versionNumber, null);
    }

    @Override
    public void rollback(int versionNumber) {

        if (versionNumber < 1 || versionNumber > latestVersionNumber) {
            return; // Invalid rollback
        }

        for (int i = versionNumber + 1; i <= latestVersionNumber; ++i) {
            versionToContentMap.remove(i);
        }

        latestVersionNumber = versionNumber;
    }
}


