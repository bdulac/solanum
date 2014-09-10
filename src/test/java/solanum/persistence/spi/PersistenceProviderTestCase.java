package solanum.persistence.spi;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import junit.framework.TestCase;

public class PersistenceProviderTestCase extends TestCase {
	
	@Override
	public void setUp() {
	}
	
	public void testSpi() {
		EntityManagerFactory emf  = 
				Persistence.createEntityManagerFactory("OrderManagement");
		assertNotNull(emf);
	}
	
	@Override
	public void tearDown() {
	}
}
