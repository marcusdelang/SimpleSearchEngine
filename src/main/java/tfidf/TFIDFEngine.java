package tfidf;

import document.Document;
import search.Engine;
import text.TextNormalizer;

import java.util.*;

public class TFIDFEngine implements Engine {
    private Map<Document, TermFrequency> documentTermFrequency;
    private Map<String, Set<Document>> documentsContainingTerm;
    private TFIDFCalculator tfidfCalculator;

    public TFIDFEngine() {
        this.documentTermFrequency = new HashMap<>();
        this.documentsContainingTerm = new HashMap<>();
    }

    @Override
    public void build(Collection<Document> corpus) {
        populateTermFrequencies(corpus);
        populateDocumentsContainingTerm(corpus);
        this.tfidfCalculator = new TFIDFCalculator(this.documentTermFrequency, this.documentsContainingTerm, corpus.size());
    }

    @Override
    public Collection<Document> searchTerm(String term) {
        List<Document> scoredDocuments = new ArrayList<>();
        this.documentsContainingTerm.get(term).forEach(document -> {
            scoredDocuments.add(new ScoredDocument(document, tfidfCalculator.score(term, document)));
        });
        return sort(scoredDocuments);
    }

    private void populateTermFrequencies(Collection<Document> corpus) {
        corpus.forEach(document -> {
            documentTermFrequency.put(document, TermFrequencyCalculator.calculateTermFrequency(document));
        });
    }

    private void populateDocumentsContainingTerm(Collection<Document> corpus) {
        corpus.forEach(document -> {
            TextNormalizer.normalizedTerms(document.getText()).forEach(term -> {
                if (documentsContainingTerm.containsKey(term)) {
                    documentsContainingTerm.get(term).add(document);
                } else {
                    documentsContainingTerm.put(term, new HashSet<>(Collections.singletonList(document)));
                }
            });
        });
    }

    private Collection<Document> sort(List<Document> scoredDocuments) {
        Collections.sort(scoredDocuments, Collections.reverseOrder());
        return scoredDocuments;
    }


}
