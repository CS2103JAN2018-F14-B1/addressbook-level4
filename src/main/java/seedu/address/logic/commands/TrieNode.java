package seedu.address.logic.commands;

/**
 * Represents a Trie Node object. Contains a character, a reference to sibling Trie Node and child Trie Node .
 */
public class TrieNode {
    private TrieNode sibling;
    private TrieNode child;

    private char key;

    TrieNode(char key, TrieNode sibling, TrieNode child) {
        this.key = key;
        this.sibling = sibling;
        this.child = child;
    }

    public char getKey() {
        return key;
    }

    public boolean hasSibling() {
        return sibling != null;
    }

    public TrieNode getSibling() {
        return sibling;
    }

    public void setSibling(TrieNode sibling) {
        this.sibling = sibling;
    }

    public boolean hasChild() {
        return child != null;
    }

    public TrieNode getChild() {
        return child;
    }

    public void setChild(TrieNode child) {
        this.child = child;
    }
}
