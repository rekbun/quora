import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.UUID;

import static junit.framework.Assert.assertEquals;

public class TestSolution {




	@BeforeClass
	public static void testSetup() {
	}

	@AfterClass
	public static void testCleanup() {

	}

	@Test
	public void testDel() {
		QueryParser queryParser= new BasicQueryParser();
		Factory.setInstance();
		queryParser.parse("ADD user u1 1.0 Adam D’Angelo").execute();
		queryParser.parse("ADD user u2 1.0 Adam Black").execute();
		queryParser.parse("ADD topic t1 0.8 Adam D’Angelo").execute();
		queryParser.parse("ADD question q1 0.5 What does Adam D’Angelo do at Quora?").execute();
		queryParser.parse("ADD question q2 0.5 How did Adam D’Angelo learn programming?").execute();
		queryParser.parse("QUERY 10 Adam").execute();
		queryParser.parse("QUERY 10 Adam D’A").execute();
		queryParser.parse("QUERY 10 Adam Cheever").execute();
		queryParser.parse("QUERY 10 LEARN how").execute();
		queryParser.parse("QUERY 1 lear H").execute();
		queryParser.parse("QUERY 0 lea").execute();
		queryParser.parse("WQUERY 10 0 Adam D’A").execute();
		queryParser.parse("WQUERY 2 1 topic:9.99 Adam D’A").execute();
		queryParser.parse("DEL u2").execute();
		queryParser.parse("QUERY 2 Adam").execute();
		System.out.print("Test Finished");
	}


	@Test
	public void testAdd() {
		QueryParser queryParser= new BasicQueryParser();
		Factory.setInstance();
		System.out.println("second test case");
		queryParser.parse("ADD user u1 1.0 Adam D’Angelo").execute();
		queryParser.parse("ADD user u2 1.0 Adam Black").execute();
		queryParser.parse("ADD topic t1 1.0 Adam D’Angelo").execute();
		queryParser.parse("ADD question q1 1.0 What does Adam D’Angelo do at Quora?").execute();
		queryParser.parse("ADD question q2 1.0 How did Adam D’Angelo learn programming?").execute();
		queryParser.parse("QUERY 15 Adam").execute();
		queryParser.parse("QUERY 10 Adam D’A").execute();
		queryParser.parse("QUERY 10 Adam Cheever").execute();
		queryParser.parse("QUERY 10 LEARN how").execute();
		queryParser.parse("QUERY 1 lear H").execute();
		queryParser.parse("QUERY 0 learne").execute();
		queryParser.parse("WQUERY 10 0 Adam D’A").execute();
		queryParser.parse("WQUERY 2 3 topic:9.99 user:2.0 u1:4 Adam D’A").execute();
		queryParser.parse("DEL u2").execute();
		queryParser.parse("QUERY 2 Adam").execute();
		System.out.println("TEST COMPLETED");

		queryParser= new BasicQueryParser();
		Factory.setInstance();
		queryParser.parse("ADD user u1 1.0 Adam D’Angelo").execute();
		queryParser.parse("ADD user u2 1.0 Adam Black").execute();
		queryParser.parse("ADD topic t1 0.8 Adam D’Angelo").execute();
		queryParser.parse("ADD question q1 0.5 What does Adam D’Angelo do at Quora?").execute();
		queryParser.parse("ADD question q2 0.5 How did Adam D’Angelo learn programming?").execute();
		queryParser.parse("QUERY 10 Adam").execute();
		queryParser.parse("QUERY 10 Adam D’A").execute();
		queryParser.parse("QUERY 10 Adam Cheever").execute();
		queryParser.parse("QUERY 10 LEARN how").execute();
		queryParser.parse("QUERY 1 lear H").execute();
		queryParser.parse("QUERY 0 lea").execute();
		queryParser.parse("WQUERY 10 0 Adam D’A").execute();
		queryParser.parse("WQUERY 2 1 topic:9.99 Adam D’A").execute();
		queryParser.parse("DEL u2").execute();
		queryParser.parse("QUERY 2 Adam").execute();
		System.out.println("Test Finished");



		queryParser= new BasicQueryParser();
		Factory.setInstance();
		queryParser.parse("ADD user u1 1.0 Adam D’Angelo").execute();
		queryParser.parse("ADD user u2 1.0 Adam Black").execute();
		queryParser.parse("ADD topic t1 0.8 Adam D’Angelo").execute();
		queryParser.parse("ADD question q1 0.5 What does Adam D’Angelo do at Quora?").execute();
		queryParser.parse("ADD question q2 0.5 How did Adam D’Angelo learn programming?").execute();
		queryParser.parse("QUERY 10 Adam").execute();
		queryParser.parse("QUERY 10 Adam D’A").execute();
		queryParser.parse("QUERY 10 Adam Cheever").execute();
		queryParser.parse("QUERY 10 LEARN how").execute();
		queryParser.parse("QUERY 1 lear H").execute();
		queryParser.parse("QUERY 0 lea").execute();
		queryParser.parse("WQUERY 10 0 Adam D’A").execute();
		queryParser.parse("WQUERY 2 1 topic:9.99 Adam D’A").execute();
		queryParser.parse("DEL u2").execute();
		queryParser.parse("QUERY 2 Adam").execute();
		System.out.println("Test Finished");
	}


	@Test
	public void testAddition() {
		int i=0;
		QueryParser parser=new BasicQueryParser();
		Factory.setInstance();
		while(i<40000) {
			parser.parse("ADD user u"+i+ " 1.0 " + "Adam D’Angelo"+i).execute();
			i++;
		}
		assertEquals(40000,Factory.getDataStore().getSize());
		assertEquals(40000,Factory.getSearcher().search("D’Angelo",-1).size());

	}


	@Test
	public void testDeletion() {
		int i=0;
		QueryParser parser=new BasicQueryParser();
		Factory.setInstance();
		while(i<40000) {
			parser.parse("ADD user u"+i+ " 1.0 " + "Adam D’Angelo"+i).execute();
			i++;
		}
		assertEquals(Factory.getDataStore().getSize(), 40000);
		assertEquals(Factory.getSearcher().search("D’Angelo",-1).size(),40000);

		i=0;
		while (i<40000) {
			parser.parse("DEL u"+i).execute();
			i++;
		}

		assertEquals(Factory.getDataStore().getSize(),0);
		assertEquals(Factory.getSearcher().search("D'Angelo",-1).size(),0);
	}


	@Test
	public void testT2() {
		QueryParser parser=new BasicQueryParser();
		Factory.setInstance();

		parser.parse("ADD user 1 15 bqc").execute();
		parser.parse("ADD question 2 37 j fric zjg gr ylab ruuf n m").execute();
		parser.parse("ADD question 4 43 o xhq pvmg nrsq lk qk z").execute();
		parser.parse("ADD user 5 66 nufc el nbxb").execute();
		parser.parse("WQUERY 20 4 5:53 4:61 2:18 1:65 fr").execute();
		parser.parse("ADD user 7 69 mnmqg").execute();
		parser.parse("QUERY 2 nu").execute();
		parser.parse("DEL 5").execute();
		parser.parse("QUERY 2 nu").execute();


	}
}
