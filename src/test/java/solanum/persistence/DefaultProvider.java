package solanum.persistence;

import java.util.Map;

import javax.persistence.EntityManagerFactory;
import javax.persistence.spi.PersistenceProvider;
import javax.persistence.spi.PersistenceUnitInfo;
import javax.persistence.spi.ProviderUtil;

public class DefaultProvider implements PersistenceProvider {

	@Override
	public EntityManagerFactory createContainerEntityManagerFactory(
			PersistenceUnitInfo arg0, Map arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public EntityManagerFactory createEntityManagerFactory(String arg0, Map arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ProviderUtil getProviderUtil() {
		// TODO Auto-generated method stub
		return null;
	}

}
