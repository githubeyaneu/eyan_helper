package eu.eyan.util.xml;

public class Jaxb {
//	/**
//		 * Load the properties from file. Write the default values on hard disk, if
//		 * no properties file available.
//		 * 
//		 * @throws JAXBException
//		 * @throws FileNotFoundException
//		 */
//		private void loadProperties() throws JAXBException, FileNotFoundException {
//			LoggerUtil.logInfo("*** Start reading and parsing xml file ***");
//			LoggerUtil.logInfo("File path: " + CONFIG_FILE.getAbsolutePath());
//			LoggerUtil.logInfo("File exists: " + CONFIG_FILE.exists());
//			if (CONFIG_FILE.exists() && CONFIG_FILE.canRead()) {
//				properties = JAXB.unmarshal(CONFIG_FILE, ConfigurationCollection.class);
//			} else {
//				createAndSaveDefaultedConfig();
//			}
//		}
//	
//		/**
//		 * Create and save the default configuration properties.
//		 */
//		private void createAndSaveDefaultedConfig() {
//			LoggerUtil.logInfo("*** Create and save the default configuration properties ***");
//			properties = new ConfigurationCollection();
//			writeProperties(properties);
//		}
//	
//		/**
//		 * Save configuration properties on hard disk.
//		 * 
//		 * @param config
//		 */
//		public static void writeProperties(ConfigurationCollection config) {
//			if(config != null){
//				LoggerUtil.logInfo("Save configuration properties on hard disk");
//				JAXB.marshal(config, CONFIG_FILE);
//			}
//		}
}

