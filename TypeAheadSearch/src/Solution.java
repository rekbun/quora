import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;


class Node {
	HashMap<Character, Node> map;
	Set<Document> ids;


	public Node() {
		map = new HashMap<Character, Node>();
		ids = new TreeSet<Document>();
	}
}

class SizeableTreeSet extends TreeSet<Document> {

	private final int size;

	SizeableTreeSet(int s) {
		this.size = s;
	}

	@Override
	public boolean add(Document s) {
		boolean result = super.add(s);
		if (size() >= size) {
			this.remove(last());
		}
		return result;
	}

	@Override
	public boolean addAll(Collection<? extends Document> documents) {
		super.addAll(documents);
		while (this.size()>size) {
			this.remove(this.last());
		}
		return true;
	}

}


class PrefixTree {

	private final Node root;

	PrefixTree() {
		root = new Node();
	}

	public void add(String data, Document id) {
		Node c = root;
		int i;
		for (i = 0; i < data.length() ; i++) {
			Node n = c.map.get(data.charAt(i));
			if (n == null) {
				c.map.put(data.charAt(i), new Node());
			}
			c.ids.add(id);
			c = c.map.get(data.charAt(i));
		}
		c.ids.add(id);
	}

	public void delete(String data, Document id) {
		del(data, 0, id, root);
	}

	private void del(String data, int i, Document id, Node cur) {
		if (cur == null) {
			return;
		}
		if (i < data.length()) {
			del(data, i + 1, id, cur.map.get(data.charAt(i)));
			if (cur.map.get(data.charAt(i)) != null && cur.map.get(data.charAt(i)).ids.size() == 0) {
				cur.map.remove(data.charAt(i));
			}
			cur.ids.remove(id);
		} else {
			cur.ids.remove(id);
		}
	}

	public Set<Document> search(String data, int count) {
		Node c = root;
		for (int i = 0; i < data.length() ; i++) {
			c = c.map.get(data.charAt(i));
			if (c == null) {
				return new TreeSet<Document>();
			}
		}
		if (count >= 0) {
			if(count<=c.ids.size()) {
			return c.ids;
			}else {
				return searchUtis(c,count);
			}
		} else {
			return searchUtis(c);
		}
	}

	private Set<Document> searchUtis(Node c, int count) {
		for (Node cd:c.map.values()) {
			c.ids.addAll(searchUtis(cd,count));
			if(c.ids.size()>=count) {
				return c.ids;
			}
		}
		return c.ids;
	}

	private Set<Document> searchUtis(Node c) {
		Set<Document> documents = new TreeSet<Document>();
		for (Node cd : c.map.values()) {
			documents.addAll(searchUtis(cd));
		}
		documents.addAll(c.ids);
		return documents;
	}
}

enum CommandName {
	ADD,
	DEL,
	QUERY,
	WQUERY
}


enum Type {
	USER,
	TOPIC,
	QUESTION,
	BOARD;

	public String toString() {
		return this.name().toLowerCase();
	}

	public static Type getType(String typeName) {
		return Enum.valueOf(Type.class, typeName.toUpperCase());
	}
}

class Document implements Comparable<Document> {
	private final Type type;
	private final String id;
	private final float score;
	private final String data;
	private final Long time;

	public Document(Type type, String id, float score, String data) {
		this.type = type;
		this.id = id;
		this.score = score;
		this.data = data;
		time = System.nanoTime();
	}

	public Type getType() {
		return type;
	}

	public String getId() {
		return id;
	}

	public float getScore() {
		return score;
	}

	public String getData() {
		return data;
	}

	public Long getTime() {
		return time;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		Document document = (Document) o;

		if (!id.equals(document.id)) return false;

		return true;
	}

	@Override
	public int hashCode() {
		return id.hashCode();
	}

	@Override
	public int compareTo(Document document) {
		if (Float.compare(score, document.score) == 0) {
			return document.time.compareTo(time);
		}
		return Float.compare(document.score, score);
	}
}


class DataStore {
	HashMap<String, Document> map;
	PrefixTree prefixTree;

	public DataStore() {
		map = new HashMap<String, Document>();
		prefixTree = new PrefixTree();
	}


	public void add(String s, Document id) {
		map.put(id.getId(), id);
		prefixTree.add(s, id);
	}

	public void remove(String id) {
		if (map.get(id) == null) {
			return;
		}
		for (String src : map.get(id).getData().split(" ")) {
			prefixTree.delete(src.toLowerCase(), map.get(id));
		}
		map.remove(id);
	}

	public Set<Document> search(String data, int count) {
		return prefixTree.search(data, count);
	}


	public int getSize() {
		return map.size();
	}

}

class Searcher {
	private final DataStore store;

	public Searcher(DataStore store) {
		this.store = store;
	}

	public Set<Document> search(String data, int count) {
		return store.search(data.toLowerCase(), count);
	}
}

class Factory {
	private static DataStore dataStore;
	private static IndexWriter indexWriter;
	private static Searcher searcher;

	public static IndexWriter getWriter() {
		return indexWriter;
	}

	public static Searcher getSearcher() {
		return searcher;
	}

	public static void setInstance() {
		dataStore=new DataStore();
		indexWriter=new IndexWriter(dataStore);
		searcher=new Searcher(dataStore);
	}

	public static DataStore getDataStore() {
		return dataStore;
	}
}

abstract class Command {

	public String data;

	public Command(String data) {
		this.data = data;
	}

	public abstract void execute();
}

class Add extends Command {

	public Add(String data) {
		super(data);
	}

	@Override
	public void execute() {
		String[] params = parse(data);
		Document doc = new Document(Type.getType(params[0]), params[1], Float.parseFloat(params[2]), params[3]);
		IndexWriter writer = Factory.getWriter();
		String[] queryWords = params[3].split(" ");
		for (int i = 0; i < queryWords.length; i++) {
			writer.add(queryWords[i].toLowerCase(), doc);
		}

	}

	private String[] parse(String data) {
		return data.split(" ", 4);
	}
}

class Del extends Command {

	public Del(String data) {
		super(data);
	}

	@Override
	public void execute() {
		Factory.getWriter().delete(data.toLowerCase());
	}
}

class Query extends Command {
	private int nor;
	private final String queryString;

	public Query(String nor, String queryString) {
		super(queryString);
		this.nor = Integer.parseInt(nor);
		this.queryString = queryString;
	}

	@Override
	public void execute() {
		String[] words = queryString.split(" ");
		Set<Document> set = new TreeSet<Document>();
		for (int i = 0; i < words.length; i++) {
			if (i == 0) {
				set.addAll(Factory.getSearcher().search(words[i].toLowerCase(), nor));
			} else {
				set.retainAll(Factory.getSearcher().search(words[i].toLowerCase(), nor));
			}
		}
		ArrayList<Document> result = new ArrayList<Document>(set);
		for (int i = 0; i < result.size() && nor-- > 0; i++) {
			System.out.print(result.get(i).getId());
			if (i < result.size() - 1) {
				System.out.print(" ");
			}
		}
		System.out.println();
	}

}

class WQuery extends Command {
	private int nor; //number of requests
	private final int nob; //number of boosts
	private final Map<String, Float> idVMap;
	private final String queryString;

	public WQuery(int nor, int nob, Map<String, Float> idVMap, String queryString) {
		super(queryString);
		this.nor = nor;
		this.nob = nob;
		this.idVMap = idVMap;
		this.queryString = queryString;
	}

	@Override
	public void execute() {
		String[] words = queryString.split(" ");
		TreeSet set = new TreeSet();
		for (int i = 0; i < words.length; i++) {
			if (i == 0) {
				set.addAll(Factory.getSearcher().search(words[i].toLowerCase(), -1));
			} else {
				set.retainAll(Factory.getSearcher().search(words[i].toLowerCase(), -1));
			}
		}

		ArrayList<Document> result = new ArrayList<Document>(set);
		Collections.sort(result, new Comparator<Document>() {
			@Override
			public int compare(Document d1, Document d2) {
				float score = d1.getScore();
				if (idVMap == null) {
					return d1.compareTo(d2);
				}
				if (idVMap.containsKey(d1.getType().toString())) {
					score = score * idVMap.get(d1.getType().toString());

				}
				if (idVMap.containsKey(d1.getId())) {
					score = score * idVMap.get(d1.getId());
				}
				float score2 = d2.getScore();
				if (idVMap.containsKey(d2.getType().toString())) {
					score2 = score2 * idVMap.get(d2.getType().toString());

				}
				if (idVMap.containsKey(d2.getId())) {
					score2 = score2 * idVMap.get(d2.getId());
				}
				if (Float.compare(score2, score) == 0) {
					return d2.getTime().compareTo(d1.getTime());
				}
				return Float.compare(score2, score);
			}
		});
		for (int i = 0; i < result.size() && nor-- > 0; i++) {
			System.out.print(result.get(i).getId());
			if (i < result.size() - 1) {
				System.out.print(" ");
			}
		}
		System.out.println();
	}
}

//Query Parsers
abstract class QueryParser {
	public abstract Command parse(String rawQuery);
}


class BasicQueryParser extends QueryParser {
	@Override
	public Command parse(String rawQuery) {
		String commandName = rawQuery.substring(0, rawQuery.indexOf(' '));
		if (commandName.equals(CommandName.ADD.toString())) {
			return new Add(rawQuery.substring(rawQuery.indexOf(' ') + 1));
		} else if (commandName.equals(CommandName.DEL.toString())) {
			return new Del(rawQuery.substring(rawQuery.indexOf(' ') + 1));
		} else if (commandName.equals(CommandName.QUERY.toString())) {
			String commands = rawQuery.substring(rawQuery.indexOf(' ') + 1);
			return new Query(commands.substring(0, commands.indexOf(' ')), commands.substring(commands.indexOf(' ') + 1));
		} else {
			String wq = rawQuery.substring(rawQuery.indexOf(' ') + 1);
			int nor = Integer.parseInt(wq.substring(0, wq.indexOf(' ')));
			String query = wq.substring(wq.indexOf(' ') + 1);
			int nob = Integer.parseInt(query.substring(0, query.indexOf(' ')));
			query = query.substring(query.indexOf(' ') + 1);
			Map<String, Float> map = new HashMap<String, Float>();
			if (nob > 0) {
				while (nob-- > 0) {
					String boost = query.substring(0, query.indexOf(' '));

					String[] type = boost.split(":");
					if (map.get(type[0]) != null) {
						map.put(type[0], map.get(type[0]) * Float.parseFloat(type[1]));
					} else {
						map.put(type[0], Float.parseFloat(type[1]));
					}
					query = query.substring(query.indexOf(' ') + 1);
				}
			}
			return new WQuery(nor, nob, map, query);
		}
	}
}


class IndexWriter {
	private final DataStore dataStore;

	public IndexWriter(DataStore store) {
		dataStore = store;
	}

	public void add(String data, Document id) {
		dataStore.add(data, id);
	}

	public void delete(String id) {
		dataStore.remove(id);
	}
}

public class Solution {

	public static void main(String... args) throws IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		Factory.setInstance();
		int N = 0; // number of commands

		try {
			N = Integer.parseInt(reader.readLine());
		} catch (IOException e) {
			e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
		}

		QueryParser queryParser = new BasicQueryParser();
		while (N-- > 0) {
			String query;
			query = reader.readLine();
			queryParser.parse(query).execute();
		}
	}

}
