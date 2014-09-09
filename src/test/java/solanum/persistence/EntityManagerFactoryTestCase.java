package solanum.persistence;

import junit.framework.TestCase;

public class EntityManagerFactoryTestCase extends TestCase {
	
	@Override
	public void setUp() {
	}
	
	public void testConstruction() {
		try {
			new EntityManagerFactoryImpl(null, null);
			fail("An exception should be thrown");
		} catch(NullPointerException e) {
			// TODO
		}
	}
	
	@Override
	public void tearDown() {
	}

}
