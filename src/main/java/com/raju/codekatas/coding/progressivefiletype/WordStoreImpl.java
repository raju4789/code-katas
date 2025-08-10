package com.raju.codekatas.coding.progressivefiletype;


import java.util.*;

/**
 * Problem: Trie-based Word Store with Autocomplete
 * <p>
 * Design and implement a WordStore that supports the following progressive stages:
 * <p>
 * Stage 1:
 * - void insert(String word)
 * Inserts a word into the store.
 * <p>
 * Stage 2:
 * - boolean exists(String word)
 * Returns true if the exact word exists in the store; otherwise, false.
 * <p>
 * Stage 3:
 * - List<String> autocomplete(String prefix)
 * Returns all words that start with the given prefix, sorted lexicographically.
 * <p>
 * Stage 4:
 * - void delete(String word)
 * Deletes the exact word from the store. After deletion, the word should no longer
 * appear in existence checks or autocomplete suggestions.
 * <p>
 * Constraints:
 * - Only lowercase English letters ('a' to 'z') are allowed.
 * - Word length <= 50.
 * - Prefix queries should return results in lexicographical order.
 * - All methods should be efficient (target: sublinear per character traversal when possible).
 * <p>
 * Example usage:
 * WordStore ws = new WordStoreImpl();
 * ws.insert("apple");
 * ws.insert("app");
 * ws.insert("ape");
 * <p>
 * ws.exists("apple");    // true
 * ws.exists("apples");   // false
 * <p>
 * ws.autocomplete("ap"); // ["ape", "app", "apple"]
 * ws.autocomplete("app"); // ["app", "apple"]
 * <p>
 * ws.delete("app");
 * ws.exists("app");      // false
 * ws.autocomplete("ap"); // ["ape", "apple"]
 */


interface WordStore {
    void insert(String word);

    boolean exists(String word);

    List<String> autocomplete(String prefix);

    void delete(String word);
}


public class WordStoreImpl implements WordStore {

    private WordTrie root;

    public WordStoreImpl() {
        root = new WordTrie();
    }

    @Override
    public void insert(String word) {
        WordTrie current = root;

        char[] chars = word.toCharArray();

        for (int i = 0; i < chars.length; ++i) {

            if (current.getChildren().containsKey(chars[i])) {
                current = current.getChildren().get(chars[i]);
            } else {
                current.getChildren().put(chars[i], new WordTrie());
                current = current.getChildren().get(chars[i]);
            }

            if (i == chars.length - 1) {
                current.setLastLetterOfWord(true);
            }
        }
    }

    @Override
    public boolean exists(String word) {
        WordTrie current = root;

        char[] chars = word.toCharArray();

        boolean isWordExists = false;

        for (int i = 0; i < chars.length; ++i) {
            if (current.getChildren().containsKey(chars[i])) {
                current = current.getChildren().get(chars[i]);
            } else {
                isWordExists = false;
                break;
            }

            if (i == chars.length - 1) {
                isWordExists = current.isLastLetterOfWord();
            }
        }
        return isWordExists;
    }

    @Override
    public List<String> autocomplete(String prefix) {
        WordTrie current = root;

        char[] chars = prefix.toCharArray();

        StringBuilder currentPrefix = new StringBuilder();

        List<String> prefixWords = new ArrayList<>();

        for (int i = 0; i < chars.length; ++i) {

            if (!current.getChildren().containsKey(chars[i])) {
                break;
            }

            currentPrefix.append(chars[i]);
            current = current.getChildren().get(chars[i]);
        }

        if (currentPrefix.toString().equals(prefix)) {
            wordsStartsWithPrefix(current, currentPrefix, prefixWords);
        }

        Collections.sort(prefixWords);

        return prefixWords;
    }

    private void wordsStartsWithPrefix(WordTrie current, StringBuilder prefix, List<String> prefixWords) {

        if (current.isLastLetterOfWord()) {
            prefixWords.add(prefix.toString());
        }

        for (char c : current.getChildren().keySet()) {
            prefix.append(c);
            wordsStartsWithPrefix(current.getChildren().get(c), prefix, prefixWords);
        }
    }

    @Override
    public void delete(String word) {
        WordTrie current = root;

        char[] chars = word.toCharArray();


        for (int i = 0; i < chars.length; ++i) {

            if (!current.getChildren().containsKey(chars[i])) {
                break;
            }

            current = current.getChildren().get(chars[i]);

            if (i == chars.length - 1) {
                current.setLastLetterOfWord(false);
            }

        }
    }

    static class WordTrie {
        private boolean isLastLetterOfWord;
        private Map<Character, WordTrie> children;

        public WordTrie() {
            this.children = new HashMap<>();
            this.isLastLetterOfWord = false;
        }

        public WordTrie(boolean isLastLetterOfWord) {
            this.children = new HashMap<>();
            this.isLastLetterOfWord = isLastLetterOfWord;
        }

        public boolean isLastLetterOfWord() {
            return isLastLetterOfWord;
        }

        public void setLastLetterOfWord(boolean lastLetterOfWord) {
            isLastLetterOfWord = lastLetterOfWord;
        }

        public Map<Character, WordTrie> getChildren() {
            return children;
        }

        public void setChildren(Map<Character, WordTrie> children) {
            this.children = children;
        }
    }
}

