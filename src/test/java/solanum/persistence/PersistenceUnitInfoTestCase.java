package solanum.persistence;

import java.util.List;

import javax.persistence.spi.PersistenceProvider;

import junit.framework.TestCase;

public class PersistenceUnitInfoTestCase extends TestCase {
	
	@Override
	public void setUp() {
	}
	
	public void testGetPersistenceUnitNames() {
		 try {
			 PersistenceUnitInfo.getPersistenceUnitNames(null);
			 fail("A nullpointer exception should be thrown");
		 } catch(NullPointerException e) {
		 }
		 PersistenceProvider p = new DefaultProvider();
		 List<String> n = PersistenceUnitInfo.getPersistenceUnitNames(p);
		 assertNotNull(n);
		 assertTrue(n.size() > 0);
	}
	
	@Override
	public void tearDown() {
	}
}
