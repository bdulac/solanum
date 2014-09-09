package solanum.persistence;

import java.util.List;
import java.util.Map;

import javax.persistence.EntityManagerFactory;
import javax.persistence.spi.PersistenceProvider;
import javax.persistence.spi.PersistenceUnitInfo;
import javax.persistence.spi.ProviderUtil;

import junit.framework.TestCase;

public class PersistenceUnitInfoTestCase extends TestCase {
	
	private PersistenceProvider dumbProvider;
	
	@Override
	public void setUp() {
		dumbProvider = new PersistenceProvider() {

			@Override
			public ProviderUtil getProviderUtil() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public EntityManagerFactory createEntityManagerFactory(String arg0, Map arg1) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public EntityManagerFactory createContainerEntityManagerFactory(
					PersistenceUnitInfo arg0, Map arg1) {
				// TODO Auto-generated method stub
				return null;
			}			 
		};
	}
	
	public void testGetPersistenceUnitNames() {
		 try {
			 PersistenceUnitInfoImpl.getPersistenceUnitNames(null);
			 fail("A nullpointer exception should be thrown");
		 } catch(NullPointerException e) {
		 }
		 List<String> names = 
				 PersistenceUnitInfoImpl.getPersistenceUnitNames(dumbProvider);
		 assertNotNull(names);
		 assertTrue(names.size() > 0);
		 assertTrue(names.contains("OrderManagement"));
	}
	
	public void testConstruction() {
		try {
			new PersistenceUnitInfoImpl(null, null, null, null, null);
			fail("an exception should be thrown");
		} catch(NullPointerException e) {
			try {
				new PersistenceUnitInfoImpl(
						"OrderManagement", null, null, null, null);
				fail("an exception should be thrown");
			} catch(NullPointerException ex) {
				PersistenceUnitInfo i = 
						new PersistenceUnitInfoImpl(
								"OrderManagement", 
								dumbProvider, null, null, null);
				assertNotNull(i);
				assertEquals(
						i.getClassLoader(), 
						dumbProvider.getClass().getClassLoader());
			}
		}
	}
	
	@Override
	public void tearDown() {
	}
}
