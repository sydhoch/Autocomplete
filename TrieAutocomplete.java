import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;

/**
 * General trie/priority queue algorithm for implementing Autocompletor
 * 
 * @author Austin Lu
 * @author Jeff Forbes
 */
public class TrieAutocomplete implements Autocompletor {

	/**
	 * Root of entire trie
	 */
	protected Node myRoot;

	/**
	 * Constructor method for TrieAutocomplete. Should initialize the trie
	 * rooted at myRoot, as well as add all nodes necessary to represent the
	 * words in terms.
	 * 
	 * @param terms
	 *            - The words we will autocomplete from
	 * @param weights
	 *            - Their weights, such that terms[i] has weight weights[i].
	 * @throws NullPointerException
	 *             if either argument is null
	 * @throws IllegalArgumentException
	 *             if terms and weights are different weight
	 */
	public TrieAutocomplete(String[] terms, double[] weights) {
		if (terms == null || weights == null) {
			throw new NullPointerException("One or more arguments null");
		}
		
		// Represent the root as a dummy/placeholder node
		myRoot = new Node('-', null, 0);

		for (int i = 0; i < terms.length; i++) {
			add(terms[i], weights[i]);
		}
	}

	/**
	 * Add the word with given weight to the trie. If word already exists in the
	 * trie, no new nodes should be created, but the weight of word should be
	 * updated.
	 * 
	 * In adding a word, this method should do the following: Create any
	 * necessary intermediate nodes if they do not exist. Update the
	 * subtreeMaxWeight of all nodes in the path from root to the node
	 * representing word. Set the value of myWord, myWeight, isWord, and
	 * mySubtreeMaxWeight of the node corresponding to the added word to the
	 * correct values
	 * 
	 * @throws a
	 *             NullPointerException if word is null
	 * @throws an
	 *             IllegalArgumentException if weight is negative.
	 */
	private void add(String word, double weight) {
		// TODO: Implement add
		if (word ==null) {
			throw new NullPointerException("null word");
		}
		if (weight<0) {
			throw new IllegalArgumentException("negative weight");
		}
		Node current = myRoot;
		for(int i=0; i < word.length(); i++){
			if (current.mySubtreeMaxWeight<weight) {
				current.mySubtreeMaxWeight=weight;
			}
			char ch = word.charAt(i);
			if (current.children.get(ch) == null) {
				current.children.put(ch,new Node(ch, current, weight));
			}
			current = current.children.get(ch);
		}
		current.isWord = true;
		current.myWord = word;
		current.myWeight = weight;
	}

	/**
	 * Required by the Autocompletor interface. Returns an array containing the
	 * k words in the trie with the largest weight which match the given prefix,
	 * in descending weight order. If less than k words exist matching the given
	 * prefix (including if no words exist), then the array instead contains all
	 * those words. e.g. If terms is {air:3, bat:2, bell:4, boy:1}, then
	 * topKMatches("b", 2) should return {"bell", "bat"}, but topKMatches("a",
	 * 2) should return {"air"}
	 * 
	 * @param prefix
	 *            - A prefix which all returned words must start with
	 * @param k
	 *            - The (maximum) number of words to be returned
	 * @return An Iterable of the k words with the largest weights among all
	 *         words starting with prefix, in descending weight order. If less
	 *         than k such words exist, return all those words. If no such words
	 *         exist, return an empty Iterable
	 * @throws a
	 *             NullPointerException if prefix is null
	 */
	public Iterable<String> topMatches(String prefix, int k) {
		// TODO: Implement topKMatches
		if (prefix == null) throw new NullPointerException("null prefix");
		if (k<0) throw new IllegalArgumentException("negative k");
		if (k==0) return new LinkedList<String>();
		
		Node current = myRoot;
		for (int i=0; i<prefix.length();i++) {
			current = current.children.get(prefix.charAt(i));
			if(current == null) return new ArrayList<String>();
		}
		PriorityQueue<Node>  npq = new PriorityQueue<Node>(new Node.ReverseSubtreeMaxWeightComparator());
     	npq.add(current);
     	PriorityQueue<Term> tpq = new PriorityQueue<Term>(k,new Term.WeightOrder());
     	while (npq.size() > 0) {
     		if (tpq.size()==k && tpq.peek().getWeight()> npq.peek().mySubtreeMaxWeight){
     			break;
     		}
          	current = npq.remove();
          	if (current.isWord) {
              	if (tpq.size()>k && current.myWeight>tpq.remove().getWeight()) {
              		tpq.remove();
              	}
              	tpq.add(new Term(current.getWord(),current.getWeight()));
          	}
          	for(Node n : current.children.values()) {
              	npq.add(n);
          	}
     	}
     	LinkedList<String> done = new LinkedList<String>();
     	while (tpq.size()>0) {
     		done.addFirst(tpq.remove().getWord());
     	}
		return done;
	}

	/**
	 * Given a prefix, returns the largest-weight word in the trie starting with
	 * that prefix.
	 * 
	 * @param prefix
	 *            - the prefix the returned word should start with
	 * @return The word from with the largest weight starting with prefix, or an
	 *         empty string if none exists
	 * @throws a
	 *             NullPointerException if the prefix is null
	 */
	public String topMatch(String prefix) {
		// TODO: Implement topMatch
		if (prefix == null) throw new NullPointerException("null prefix");
		Node current = myRoot;
		for (int i=0; i<prefix.length();i++) {
			current = current.children.get(prefix.charAt(i));
			if (current ==null) {
				return "";
			}
		}
		double max = current.mySubtreeMaxWeight;
		Queue<Node> q = new LinkedList<>();
		q.add(current);
		while (q.size()>0) {
			current = q.remove();
			if (current.isWord) {
				if (current.getWeight()==max) {
					return current.getWord();
				}
			}
			for (Node n: current.children.values()) {
				q.add(n);
			}
		}

		return "";
		
	}

	/**
	 * Return the weight of a given term. If term is not in the dictionary,
	 * return 0.0
	 */
	public double weightOf(String term) {
		// TODO complete weightOf
		Node current = myRoot;
		for (int i = 0; i<term.length();i++) {
			if (current.children.get(term.charAt(i))==null) return 0.0;
		}
		return current.myWeight;
	}
}
