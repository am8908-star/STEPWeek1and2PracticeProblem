import java.util.*;

class PlagiarismDetector {

    // n-gram -> set of document IDs
    private HashMap<String, Set<String>> ngramIndex;

    // documentId -> list of ngrams
    private HashMap<String, List<String>> documentNgrams;

    private int N = 5; // 5-gram

    public PlagiarismDetector() {
        ngramIndex = new HashMap<>();
        documentNgrams = new HashMap<>();
    }

    // Add document to database
    public void addDocument(String docId, String text) {

        List<String> ngrams = generateNgrams(text);

        documentNgrams.put(docId, ngrams);

        for (String gram : ngrams) {
            ngramIndex.putIfAbsent(gram, new HashSet<>());
            ngramIndex.get(gram).add(docId);
        }
    }

    // Analyze new document
    public void analyzeDocument(String docId, String text) {

        List<String> ngrams = generateNgrams(text);

        HashMap<String, Integer> matchCount = new HashMap<>();

        for (String gram : ngrams) {

            if (ngramIndex.containsKey(gram)) {

                for (String existingDoc : ngramIndex.get(gram)) {

                    matchCount.put(
                            existingDoc,
                            matchCount.getOrDefault(existingDoc, 0) + 1
                    );
                }
            }
        }

        System.out.println("Extracted " + ngrams.size() + " n-grams");

        for (String doc : matchCount.keySet()) {

            int matches = matchCount.get(doc);

            double similarity = (matches * 100.0) / ngrams.size();

            System.out.println(
                    "Found " + matches + " matching n-grams with \"" + doc + "\""
            );

            System.out.println("Similarity: " + similarity + "%");

            if (similarity > 50) {
                System.out.println("⚠ PLAGIARISM DETECTED\n");
            }
        }
    }

    // Generate n-grams
    private List<String> generateNgrams(String text) {

        List<String> grams = new ArrayList<>();

        String[] words = text.toLowerCase().split("\\s+");

        for (int i = 0; i <= words.length - N; i++) {

            StringBuilder sb = new StringBuilder();

            for (int j = 0; j < N; j++) {
                sb.append(words[i + j]).append(" ");
            }

            grams.add(sb.toString().trim());
        }

        return grams;
    }

    public static void main(String[] args) {

        PlagiarismDetector detector = new PlagiarismDetector();

        detector.addDocument(
                "essay_089.txt",
                "machine learning is a field of artificial intelligence that focuses on data"
        );

        detector.addDocument(
                "essay_092.txt",
                "machine learning is a field of artificial intelligence used for data analysis"
        );

        detector.analyzeDocument(
                "essay_123.txt",
                "machine learning is a field of artificial intelligence used in modern data science"
        );
    }
}