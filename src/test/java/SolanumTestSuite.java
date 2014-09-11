import solanum.persistence.EntityManagerFactoryTestCase;
import solanum.persistence.PersistenceUnitInfoTestCase;
import solanum.persistence.spi.PersistenceProviderTestCase;
import junit.framework.Test;
import junit.framework.TestSuite;


public class SolanumTestSuite {
	
	public static Test suite() {
		TestSuite suite = new TestSuite("Tests for solanum	");
		suite.addTestSuite(PersistenceProviderTestCase.class);
		suite.addTestSuite(PersistenceUnitInfoTestCase.class);
		suite.addTestSuite(EntityManagerFactoryTestCase.class);
		return suite;
	}

}
