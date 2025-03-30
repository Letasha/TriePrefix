import java.util.*;

public class Main {

    static class AutocompleteSystem {
        
        // Sets up Trie node structure.
        private static class TrieNode {
            Map<Character, TrieNode> children = new HashMap<>();
            String word = null;
            int frequency = 0;
        }
    
        private final TrieNode root;
        private final Map<String, Integer> wordFrequency;
        private final Map<String, Integer> lastUsed;
        private int usageCounter;
        
        //Constructor to initialise Trie with words from the history
        public AutocompleteSystem(String[] history) {
            root = new TrieNode();
            wordFrequency = new HashMap<>();
            lastUsed = new HashMap<>();
            usageCounter = 0;
    
            for (String word : history) {
                addWord(word);
            }
        }
        
        //Add or update words
        public void addWord(String word) {
            wordFrequency.put(word, wordFrequency.getOrDefault(word, 0) + 1);
            lastUsed.put(word, ++usageCounter);
    
            TrieNode node = root;
            for (char ch : word.toCharArray()) {
                node.children.putIfAbsent(ch, new TrieNode());
                node = node.children.get(ch);
            }
            node.word = word;
            node.frequency = wordFrequency.get(word);
        }
    
        //Return suggested word using the prefix given
        public String getSuggestion(String prefix) {
            TrieNode node = root;
            for (char ch : prefix.toCharArray()) {
                if (!node.children.containsKey(ch)) {
                    return "";
                }
                node = node.children.get(ch);
            }
            return findBestMatch(node);
        }
        
        // Finds the best word match among candidates with max frequency and most recent use
        private String findBestMatch(TrieNode node) {
            List<String> candidates = new ArrayList<>();
            int[] maxFreq = new int[]{0};
    
            collectCandidates(node, candidates, maxFreq);
    
            String best = "";
            int latest = -1;
            for (String word : candidates) {
                int usedTime = lastUsed.getOrDefault(word, -1);
                if (usedTime > latest) {
                    latest = usedTime;
                    best = word;
                }
            }
            return best;
        }
    
        private void collectCandidates(TrieNode node, List<String> candidates, int[] maxFreq) {
            if (node.word != null) {
                if (node.frequency > maxFreq[0]) {
                    candidates.clear();
                    maxFreq[0] = node.frequency;
                    candidates.add(node.word);
                } else if (node.frequency == maxFreq[0]) {
                    candidates.add(node.word);
                }
            }
    
            for (TrieNode child : node.children.values()) {
                collectCandidates(child, candidates, maxFreq);
            }
        }
    }

    public static void main(String[] args) {
        //Test cases
        AutocompleteSystem autocomplete = new AutocompleteSystem(new String[]{"hello", "word", "hello"});
        System.out.println(autocomplete.getSuggestion("he")); // Output: "hello"
        
        AutocompleteSystem autocomplete2 = new AutocompleteSystem(new String[]{"code", "coding"});
        autocomplete2.addWord("code");
        System.out.println(autocomplete2.getSuggestion("cod")); // Output: "code"
    }
    
}
