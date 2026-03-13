import java.util.*;

class TrieNode {
    Map<Character, TrieNode> children = new HashMap<>();
    Map<String, Integer> freqMap = new HashMap<>();
}

class AutocompleteSystem {

    TrieNode root = new TrieNode();
    Map<String, Integer> globalFreq = new HashMap<>();

    public void addQuery(String query, int freq) {

        globalFreq.put(query, globalFreq.getOrDefault(query, 0) + freq);

        TrieNode node = root;

        for(char c : query.toCharArray()){

            node.children.putIfAbsent(c, new TrieNode());
            node = node.children.get(c);

            node.freqMap.put(query, globalFreq.get(query));
        }
    }

    public void updateFrequency(String query) {
        addQuery(query,1);
    }

    public List<String> search(String prefix){

        TrieNode node = root;

        for(char c : prefix.toCharArray()){
            if(!node.children.containsKey(c)) return new ArrayList<>();
            node = node.children.get(c);
        }

        PriorityQueue<Map.Entry<String,Integer>> pq =
                new PriorityQueue<>((a,b)->a.getValue()-b.getValue());

        for(Map.Entry<String,Integer> entry : node.freqMap.entrySet()){
            pq.offer(entry);
            if(pq.size()>10) pq.poll();
        }

        List<String> result = new ArrayList<>();

        while(!pq.isEmpty()){
            result.add(pq.poll().getKey() + " (" + pq.peek() + ")");
        }

        Collections.reverse(result);
        return result;
    }
}

public class AutocompleteDemo {

    public static void main(String[] args) {

        AutocompleteSystem system = new AutocompleteSystem();

        system.addQuery("java tutorial",1234567);
        system.addQuery("javascript",987654);
        system.addQuery("java download",456789);
        system.addQuery("java 21 features",1);

        List<String> suggestions = system.search("jav");

        for(String s : suggestions){
            System.out.println(s);
        }

        system.updateFrequency("java 21 features");
        system.updateFrequency("java 21 features");
        system.updateFrequency("java 21 features");

        System.out.println("\nAfter trending update:\n");

        suggestions = system.search("jav");

        for(String s : suggestions){
            System.out.println(s);
        }
    }
}