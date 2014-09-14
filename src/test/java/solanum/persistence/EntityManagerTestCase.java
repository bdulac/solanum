package solanum.persistence;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import junit.framework.TestCase;

public class EntityManagerTestCase extends TestCase {
	
	@Override
	public void setUp() {
	}
	
	public void testContruction() {
		try {
			new EntityManagerImpl(null, null);
			fail("An exception should be thrown");
		} catch(NullPointerException e) {
			// TODO
		}
	}
	
	public void testClose() {
		EntityManagerFactory emf  = 
				Persistence.createEntityManagerFactory("OrderManagement");
		assertNotNull(emf);
		assertTrue(emf.isOpen());
		emf.close();
		assertFalse(emf.isOpen());
	}
	
	public void testCreateEntityManager() {
		EntityManagerFactory emf  = 
				Persistence.createEntityManagerFactory("OrderManagement");
		assertNotNull(emf);
		EntityManager em = emf.createEntityManager();
		assertNotNull(em);
	}
	
	@Override
	public void tearDown() {
	}

}
