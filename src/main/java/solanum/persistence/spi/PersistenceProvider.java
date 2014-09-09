package solanum.persistence.spi;

import java.util.Map;

import javax.persistence.EntityManagerFactory;
import javax.persistence.spi.PersistenceUnitInfo;
import javax.persistence.spi.ProviderUtil;

import solanum.persistence.EntityManagerFactoryImpl;
import solanum.persistence.PersistenceUnitInfoImpl;

public class PersistenceProvider 
implements javax.persistence.spi.PersistenceProvider {

	@SuppressWarnings("unchecked")
	@Override
	public EntityManagerFactory createContainerEntityManagerFactory(
			PersistenceUnitInfo uInfo, 
			@SuppressWarnings("rawtypes") Map properties) {
		return new EntityManagerFactoryImpl(uInfo, properties);
	}

	@SuppressWarnings("unchecked")
	@Override
	public EntityManagerFactory createEntityManagerFactory(
			String uName, @SuppressWarnings("rawtypes") Map properties) {
		PersistenceUnitInfo uInfo = 
				PersistenceUnitInfoImpl.getInstance(this, uName);
		return new EntityManagerFactoryImpl(uInfo, properties);
	}

	@Override
	public ProviderUtil getProviderUtil() {
		// TODO Auto-generated method stub
		return null;
	}
}