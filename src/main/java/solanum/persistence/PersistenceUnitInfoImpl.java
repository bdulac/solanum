package solanum.persistence;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.SharedCacheMode;
import javax.persistence.ValidationMode;
import javax.persistence.spi.ClassTransformer;
import javax.persistence.spi.PersistenceProvider;
import javax.persistence.spi.PersistenceUnitInfo;
import javax.persistence.spi.PersistenceUnitTransactionType;
import javax.sql.DataSource;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/** 
 * Implementation of the persistence unit informations for Solr
 * @see fr.mnhn.persistence.spi.PersistenceProvider
 */
public class PersistenceUnitInfoImpl implements PersistenceUnitInfo {
	
	public static final String metadataUnitProperty = 
			"metadata.persistence-unit";
	
	private static final Logger logger = 
			Logger.getLogger(PersistenceUnitInfoImpl.class.getName());
	
	/**
	 * Lists the persistence unit names in the {@code META-INF/persistence.xml} file loaded by 
	 * a persistence provider
	 * @param persistenceProvider
	 * Instance of persistence provider used to load the file
	 * @return List of the names of the persistence unit configured in the file
	 */
	public static List<String> getPersistenceUnitNames(
			PersistenceProvider persistenceProvider
	) {
		URL persistenceUrl = 
				persistenceProvider.getClass().getClassLoader().getResource(
						"META-INF/persistence.xml"
				);
		try {
			List<String> names = new ArrayList<String>();
			DocumentBuilderFactory factory = 
					DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			if(persistenceUrl == null) {
				throw new IllegalStateException(
						"No file META-INF/persistence.xml can be resolved"
				);
			}
			Document document = builder.parse(persistenceUrl.openStream());
			NodeList persistenceUnits = 
					document.getElementsByTagName("persistence-unit");
			for(int i = 0 ;  i < persistenceUnits.getLength() ; i++) {
				Node n = persistenceUnits.item(i);
				if(n instanceof Element) {
					Element elm = (Element)n;
					String name = elm.getAttribute("name");
					if(name != null)names.add(name);
				}
			}
			return names;
		} catch(IOException e) {
			throw new IllegalStateException(e);
		} catch (ParserConfigurationException e) {
			throw new IllegalStateException(e);
		} catch (SAXException e) {
			throw new IllegalStateException(e);
		}
	}
	
	/**
	 * Creation of a persistence unit info instance
	 * @param persistenceProvider
	 * Instance of persistence provider used to load the {@code META-INF/persistence.xml} file
	 * @param uName
	 * Name of the persistence unit
	 * @return Persistence unit info create from the configuration
	 */
	public static PersistenceUnitInfo getInstance(
			PersistenceProvider persistenceProvider,  
			String uName
	) {
		URL persistenceUrl = 
				persistenceProvider.getClass().getClassLoader().getResource(
						"META-INF/persistence.xml"
				);
		try {
			DocumentBuilderFactory factory = 
					DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document document = builder.parse(persistenceUrl.openStream());
			NodeList persistenceUnits = 
					document.getElementsByTagName("persistence-unit");
			for(int i = 0 ;  i < persistenceUnits.getLength() ; i++) {
				Node n = persistenceUnits.item(i);
				if(n instanceof Element) {
					Element elm = (Element)n;
					String name = elm.getAttribute("name");
					if((name != null) && (name.equals(uName))) {
						return getInstance(
								persistenceProvider, elm);
					}
				}
			}
		} catch(NamingException e) {
			throw new IllegalStateException(e);	
		} catch(IOException e) {
			throw new IllegalStateException(e);
		} catch (ParserConfigurationException e) {
			throw new IllegalStateException(e);
		} catch (SAXException e) {
			throw new IllegalStateException(e);
		}
		return null;
	}
	
	private static PersistenceUnitInfoImpl getInstance(
			PersistenceProvider provider, Element puElement
			) throws NamingException {
		if(provider == null) {
			throw new IllegalArgumentException(
					"The persistence provider should not be null"
			);
		}
		if(puElement == null) {
			throw new IllegalArgumentException(
					"The persistence-unit element should not be null"
			);
		}
		String uName = puElement.getAttribute("name");
		// The provider node(s)
		boolean providerOk = false;
		NodeList providerNodes = puElement.getElementsByTagName("provider");
		for(int i = 0 ; i < providerNodes.getLength() ; i++) {
			Node node = providerNodes.item(i);
			if(node instanceof Element) {
				Element providerElement = (Element)node;
				String providerClassName = 
						providerElement.getTextContent();
				// Si nous n'avons pas le bon PersistenceProvider
				if(provider.getClass().getName().equals(providerClassName)) {
					providerOk = true;
					break;
				}
				else node = null;
			}
		}
		if(!providerOk) {
			throw new IllegalStateException(
					"The persistence unit " + uName 
					+ " is not associeted to a provider of type " 
					+ provider.getClass().getName()
			);
		}
		DataSource ds = null;
		// The data source .? 
		NodeList datasourceNodes = 
				puElement.getElementsByTagName("non-jta-data-source");
		for(int i = 0 ; i < datasourceNodes.getLength() ; i++) {
			Node node = datasourceNodes.item(i);
			if(node instanceof Element) {
				Element datasourceElement = (Element)node;
				String dsName = datasourceElement.getTextContent();
				if(dsName != null) {
					ds = getJNDIDataSourceFromName(dsName);
				}
			}
		}
		List<Class<?>> classes = new ArrayList<Class<?>>();
		NodeList entityNodes = puElement.getElementsByTagName("class");
		for(int i = 0 ; i < entityNodes.getLength() ; i++) {
			Node node = entityNodes.item(i);
			String className = node.getTextContent();
			if(className != null) {
				try {
					Class<?> cl = 
							provider.getClass().getClassLoader().loadClass(
									className
							);
					classes.add(cl);
				} catch (ClassNotFoundException e) {
					// Logging missing classes
					logger.warning(
							"Entity " + className 
							+ " not found by the provider class loader"
					);
				}
			}
		}
		Properties properties = new Properties();
		NodeList propertiesNodes = puElement.getElementsByTagName("property");
		for(int i = 0 ; i < propertiesNodes.getLength() ; i++) {
			Node node = propertiesNodes.item(i);
			if(node instanceof Element) {
				Element propertyEl = (Element)node;
				String propertyName = propertyEl.getAttribute("name");
				String propertyValue = propertyEl.getAttribute("value");
				properties.put(propertyName, propertyValue);
			}
		}
		return 
				new PersistenceUnitInfoImpl(
						uName, provider, ds, properties, classes);
	}
	
	/**
	 * Loads a JNDI Data Source
	 * @param dsName
	 * Name of the JNDI Data Source, can be preceded by the <em>java:comp/env</em> prefix
	 * @return Loaded Data Source
	 * @throws NamingException
	 * If the name is not available
	 */
	public static DataSource getJNDIDataSourceFromName(String dsName) 
	throws NamingException {
		// Making sure the prefix is OK
		if(dsName.startsWith("java:comp/env")) {
			dsName = dsName.substring("java:comp/env".length());
		}
		Context initCtx = new InitialContext();
		Context envCtx = (Context)initCtx.lookup("java:comp/env");
		Object obj = envCtx.lookup(dsName);
		// Checking the object type
		if(obj instanceof DataSource) {
			return (DataSource)obj;
		}
		else return null;
	}

	private PersistenceProvider persistenceProvider;
	
	/** Persistence unit name */
	private String persistenceUnitName;
	
	private Properties properties;
	
	private List<Class<?>> managedClasses;
	
	public PersistenceUnitInfoImpl(
			final String uName, 
			final PersistenceProvider provider, 
			DataSource dataSource, 
			Properties props, 
			List<Class<?>> entityClasses
	) {
		if(uName == null) {
			throw new NullPointerException(
					"The persistent unit name can not be null"
			);
		}
		if(provider == null) {
			throw new NullPointerException(
					"The persistent provider can not be null"
			);
		}
		persistenceUnitName = uName;
		persistenceProvider = provider;
		if(props == null)props = new Properties();
		properties = props;
		// Ajustement du niveau de log
		String loggingLevel = extractStringProperty("solanum.logging.level");
		if(loggingLevel != null) {
			setLoggingLevel(loggingLevel);
		}
		if(entityClasses == null)entityClasses = new ArrayList<Class<?>>();
		managedClasses = entityClasses;
	}
	
	/*
	private int extractIntegerProperty(
		final String propertyName, 
		int defaultValue
	) {
		Object property = properties.get(propertyName);
		if(property != null) {
			try {
				return Integer.parseInt(property.toString());
			} catch(NumberFormatException e) {
			}
		}
		return defaultValue;
	}
	*/
	
	private String extractStringProperty(final String propertyName) {
		Object property = properties.get(propertyName);
		if(property != null) {
			return property.toString();
		}
		return null;
	}
	
	private void setLoggingLevel(final String loggingLevel) {
		Level lv = null;
		if("SEVERE".equalsIgnoreCase(loggingLevel)) {
			lv = Level.SEVERE;
		}
		else if("WARNING".equalsIgnoreCase(loggingLevel)) {
			lv = Level.WARNING;
		}
		else if("INFO".equalsIgnoreCase(loggingLevel)) {
			lv = Level.INFO;
		}
		else if("CONFIG".equalsIgnoreCase(loggingLevel)) {
			lv = Level.CONFIG;
		}
		else if("FINE".equalsIgnoreCase(loggingLevel)) {
			lv = Level.FINE;
		}
		else if("FINER".equalsIgnoreCase(loggingLevel)) {
			lv = Level.FINER;
		}
		else if("FINEST".equalsIgnoreCase(loggingLevel)) {
			lv = Level.FINEST;
		}
		else lv = Level.INFO;
		// Main logger ?
		Logger logger = Logger.getLogger("solanum.persistence");
		if(logger != null) {
			logger.setLevel(lv);
			Handler[] handlers = logger.getHandlers();
			// If no log handler, 
			if(handlers.length == 0) {
				// Then creating a default one
				Handler defaultHandler = 
						new DefaultConsoleHandler();
				logger.addHandler(defaultHandler);
				handlers = logger.getHandlers();
			}
			// For all handlers, setting the level
			for(Handler handler : handlers) {
				handler.setLevel(lv);
			}
		}
	}
	
	private void checkProvider() {
		if(persistenceProvider == null) {
			throw new IllegalStateException(
					"The persistence provider should not be null"
			);
		}
	}
	
	private void checkManagedClasses() {
		if(managedClasses == null) {
			throw new IllegalStateException(
					"The managed classes list should not be null"
			);
		}
		for(Class<?> cl : managedClasses) {
			if(cl == null) {
				throw new IllegalStateException(
						"Null value not allowed as a managed class"
				);
			}
		}
	}

	@Override
	public String getPersistenceUnitName() {
		return persistenceUnitName;
	}

	@Override
	public String getPersistenceProviderClassName() {
		checkProvider();
		return persistenceProvider.getClass().getName();
	}

	@Override
	public PersistenceUnitTransactionType getTransactionType() {
		return PersistenceUnitTransactionType.RESOURCE_LOCAL;
	}

	@Override
	public DataSource getJtaDataSource() {
		throw new UnsupportedOperationException();
	}

	@Override
	public DataSource getNonJtaDataSource() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<String> getMappingFileNames() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<URL> getJarFileUrls() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public URL getPersistenceUnitRootUrl() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<String> getManagedClassNames() {
		checkManagedClasses();
		List<String> names = new ArrayList<String>();
		for(Class<?> cl : managedClasses) {
			names.add(cl.getName());
		}
		return names;
	}

	@Override
	public boolean excludeUnlistedClasses() {
		return false;
	}

	@Override
	public SharedCacheMode getSharedCacheMode() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ValidationMode getValidationMode() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Properties getProperties() {
		return properties;
	}

	@Override
	public String getPersistenceXMLSchemaVersion() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ClassLoader getClassLoader() {
		checkProvider();
		return persistenceProvider.getClass().getClassLoader();
	}

	@Override
	public void addTransformer(ClassTransformer transformer) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public ClassLoader getNewTempClassLoader() {
		// TODO Auto-generated method stub
		return null;
	}

	/** Default logging handler  */
	public class DefaultConsoleHandler extends ConsoleHandler {
		
		public DefaultConsoleHandler() {
			super();
		}
	}
}