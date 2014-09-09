package solanum.persistence;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.Cache;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnitUtil;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.metamodel.Metamodel;
import javax.persistence.spi.PersistenceUnitInfo;

public class EntityManagerFactoryImpl implements EntityManagerFactory {
	
	private PersistenceUnitInfo unitInfo;
	
	Map<Object, Object> properties;
	
	public EntityManagerFactoryImpl(
			PersistenceUnitInfo uInfo, Map<Object, Object> p) {
		if(uInfo == null)throw new NullPointerException(
				"The persistence unit info should not be null"
		);
		unitInfo = uInfo;
		if(p != null)properties = p;
		else properties = new HashMap<Object, Object>();
	}

	@Override
	public void close() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public EntityManager createEntityManager() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public EntityManager createEntityManager(Map arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Cache getCache() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CriteriaBuilder getCriteriaBuilder() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Metamodel getMetamodel() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PersistenceUnitUtil getPersistenceUnitUtil() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<String, Object> getProperties() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isOpen() {
		// TODO Auto-generated method stub
		return false;
	}

}
