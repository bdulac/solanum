package solanum.persistence;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Cache;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnitUtil;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.metamodel.Metamodel;
import javax.persistence.spi.PersistenceUnitInfo;

/** Implementation of an entity manager factory */
public class EntityManagerFactoryImpl implements EntityManagerFactory {
	
	/** Info about the persistence unit associated to the EMF */
	private PersistenceUnitInfo unitInfo;
	
	/** Properties associated to the creation of the EMF */
	private Map<String, Object> properties;
	
	/** List of created and open entity managers */
	private List<EntityManager> entityManagers;
	
	/** Flag : is EMF open */
	private boolean open;
	
	public EntityManagerFactoryImpl(
			PersistenceUnitInfo uInfo, Map<Object, Object> p) {
		if(uInfo == null)throw new NullPointerException(
				"The persistence unit info should not be null"
		);
		unitInfo = uInfo;
		properties = new HashMap<String, Object>();
		if(p != null) {
			for(Object k : p.keySet()) {
				if(k != null) {
					Object v = p.get(k);
					properties.put(k.toString(), v);
				}
			}
		}
		entityManagers = new ArrayList<EntityManager>();
		open = true;
	}
	
	public PersistenceUnitInfo getPersistenceUnitInfo() {
		return unitInfo;
	}

	@Override
	public void close() {
		for(EntityManager em : entityManagers) {
			em.clear();
			em.close();
		}
		entityManagers.clear();
		open = false;
	}

	@Override
	public EntityManager createEntityManager() {
		EntityManager em = new EntityManagerImpl(this, null);
		entityManagers.add(em);
		return em;
	}

	@Override
	public EntityManager createEntityManager(
			@SuppressWarnings("rawtypes") Map p) {
		@SuppressWarnings("unchecked")
		EntityManager em = new EntityManagerImpl(this, p);
		entityManagers.add(em);
		return em;
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
		return properties;
	}

	@Override
	public boolean isOpen() {
		return open;
	}

}
