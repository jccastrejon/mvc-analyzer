package mx.itesm.arch.mvc;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import mx.itesm.arch.dependencies.ClassDependencies;
import mx.itesm.arch.dependencies.DependencyAnalyzer;
import weka.classifiers.Classifier;
import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.SerializationHelper;

/**
 * Classify components in a web project according to the MVC pattern.
 * 
 * @author jccastrejon
 * 
 */
@SuppressWarnings("unchecked")
public class MvcAnalyzer {

    /**
     * Random Variables used in the Uncertainty model.
     * 
     * @author jccastrejon
     * 
     */
    private enum Variable {
	Type("variable.type"), ExternalAPI("variable.externalApi"), Suffix("variable.suffix"), Layer(
		"variable.layer");

	/**
	 * Variable property name.
	 */
	private String variableName;

	/**
	 * Variable attribute.
	 */
	private Attribute attribute;

	/**
	 * Constructor that specifies the Variable's property name. The
	 * attribute's values are read from the
	 * <em>MvcAnalyzer.classifierVariables</em> properties.
	 * 
	 * @param variableName
	 *            Variable Name.
	 */
	private Variable(final String variableName) {
	    String[] propertyValues;
	    FastVector valuesVector;

	    // Load property values
	    propertyValues = MvcAnalyzer.getPropertyValues(variableName);
	    valuesVector = new FastVector(propertyValues.length);
	    for (String propertyValue : propertyValues) {
		valuesVector.addElement(propertyValue);
	    }

	    this.variableName = variableName;
	    this.attribute = new Attribute(this.toString(), valuesVector, this.ordinal());
	}

	/**
	 * Get the variables property name.
	 * 
	 * @return Property name.
	 */
	public String getVariableName() {
	    return this.variableName;
	}

	/**
	 * Get the variable's attribute.
	 * 
	 * @return Attribute.
	 */
	public Attribute getAttribute() {
	    return this.attribute;
	}
    };

    /**
     * Path to the properties file containing the model's variables.
     */
    private final static String PROPERTIES_FILE_PATH = "/cfg/mvc-variables.properties";

    /**
     * Path to the file containing the model's classifier.
     */
    private final static String CLASSIFIER_FILE_PATH = "/cfg/mvc-classifier.model";

    /**
     * Properties file containing the variables data.
     */
    private static Properties classifierVariables;

    /**
     * MVC Classifier.
     */
    private static Classifier classifier;

    /**
     * Class logger.
     */
    private static Logger logger = Logger.getLogger(MvcAnalyzer.class.getName());

    /**
     * Initialize properties file.
     */
    static {
	MvcAnalyzer.classifierVariables = new Properties();

	try {
	    MvcAnalyzer.classifierVariables.load(MvcAnalyzer.class
		    .getResourceAsStream(MvcAnalyzer.PROPERTIES_FILE_PATH));

	    MvcAnalyzer.classifier = (Classifier) SerializationHelper.read(MvcAnalyzer.class
		    .getResourceAsStream(MvcAnalyzer.CLASSIFIER_FILE_PATH));
	} catch (Exception e) {
	    logger.log(Level.SEVERE, "Properties file: " + MvcAnalyzer.PROPERTIES_FILE_PATH
		    + " could not be read", e);
	}
    }

    /**
     * Classify each class within the specified path into one of the layers of
     * the MVC pattern.
     * 
     * @param path
     *            Path to the directory containing the classes.
     * @throws Exception
     *             If an Exception occurs during classification.
     */
    public static void classifyClassesInDirectory(final String path) throws Exception {
	List<ClassDependencies> directoryDependencies;

	// Classify each class in the specified path
	directoryDependencies = DependencyAnalyzer.getDirectoryDependencies(path);
	MvcAnalyzer.classifyClasses(directoryDependencies);
    }

    /**
     * Classify each class within the specified WAR file into one of the layers
     * of the MVC pattern.
     * 
     * @param file
     *            Path to the WAR file.
     * @throws Exception
     *             If an Exception occurs during classification.
     */
    public static void classifyClassesinWar(final String file) throws Exception {
	List<ClassDependencies> warDependencies;

	// Classify each class in the specified war
	warDependencies = DependencyAnalyzer.getWarDependencies(file);
	MvcAnalyzer.classifyClasses(warDependencies);
    }

    /**
     * Classify each class in the specified List into one of the layers of the
     * MVC pattern.
     * 
     * @param dependencies
     *            List containinge the dependencies for each class to classify.
     * @throws Exception
     *             If an Exception occurs during classification.
     */
    private static void classifyClasses(final List<ClassDependencies> dependencies)
	    throws Exception {
	int instanceLayer;
	Instance instance;
	boolean valueFound;
	Instances instances;
	String[] suffixValues;
	FastVector attributes;
	FastVector variableValues;
	String[] externalApiValues;
	Enumeration variableEnumeration;
	Map<String, String[]> externalApiPackages;

	// Model variables
	attributes = new FastVector();
	for (Variable variable : Variable.values()) {
	    variableEnumeration = variable.getAttribute().enumerateValues();

	    variableValues = new FastVector();
	    while (variableEnumeration.hasMoreElements()) {
		variableValues.addElement(variableEnumeration.nextElement());
	    }

	    attributes.addElement(new Attribute(variable.toString(), variableValues));
	}

	// Set the test instances, the Layer variable is unknown
	instances = new Instances("mvc", attributes, 0);
	instances.setClassIndex(3);

	// Valid suffixes to look for in the class names
	suffixValues = MvcAnalyzer.getPropertyValues(MvcAnalyzer.Variable.Suffix.getVariableName());

	// Valid external api packages to look for in the classes dependencies
	externalApiValues = MvcAnalyzer.getPropertyValues(MvcAnalyzer.Variable.ExternalAPI
		.getVariableName());
	externalApiPackages = new HashMap<String, String[]>(externalApiValues.length);
	for (int i = 0; i < externalApiValues.length; i++) {
	    if (!externalApiValues[i].equals("none")) {
		externalApiPackages.put(externalApiValues[i], MvcAnalyzer
			.getPropertyValues("externalApi." + externalApiValues[i] + ".packages"));
	    }
	}

	for (ClassDependencies classDependencies : dependencies) {
	    instance = new Instance(Variable.values().length);

	    // Type
	    instance.setValue(Variable.Type.getAttribute(), "java");

	    // ExternalAPI
	    valueFound = false;
	    externalApi: for (String externalApi : externalApiValues) {
		if (externalApi.equals("none")) {
		    continue;
		}

		// Check if any of the class' external dependencies match with
		// one of the key external dependencies
		for (String externalDependency : classDependencies.getExternalDependencies()) {
		    for (String externalPackage : externalApiPackages.get(externalApi)) {
			if (externalDependency.toLowerCase().startsWith(externalPackage)) {
			    valueFound = true;
			    instance.setValue(Variable.ExternalAPI.getAttribute(), externalApi);
			    break externalApi;
			}
		    }
		}
	    }

	    // No key external dependency found
	    if (!valueFound) {
		instance.setValue(Variable.ExternalAPI.getAttribute(), "none");
	    }

	    // Suffix
	    valueFound = false;
	    for (String suffix : suffixValues) {
		if (classDependencies.getClassName().toLowerCase().endsWith(suffix)) {
		    valueFound = true;
		    instance.setValue(Variable.Suffix.getAttribute(), suffix);
		    break;
		}
	    }

	    // No key suffix found
	    if (!valueFound) {
		instance.setValue(Variable.Suffix.getAttribute(), "none");
	    }

	    // Layer, the unknown variable
	    instance.setMissing(Variable.Layer.getAttribute());
	    instances.add(instance);
	    instance.setDataset(instances);
	    instanceLayer = (int) MvcAnalyzer.classifier.classifyInstance(instance);
	    classDependencies.setMvcLayer(Variable.Layer.getAttribute().value(instanceLayer));
	    logger.info(classDependencies.getClassName() + " : " + classDependencies.getMvcLayer());
	}
    }

    /**
     * Get a property's values, specified in the MvcAnalyzer.classifierVariables
     * properties file.
     * 
     * @param propertyName
     *            Property name.
     * @return Property's values.
     */
    private static String[] getPropertyValues(final String propertyName) {
	String[] returnValue;

	if ((propertyName == null) || (!MvcAnalyzer.classifierVariables.containsKey(propertyName))) {
	    throw new IllegalArgumentException("Invalid property: " + propertyName);
	}

	// Make sure all the values are in lower case
	returnValue = MvcAnalyzer.classifierVariables.getProperty(propertyName).split(",");
	for (int i = 0; i < returnValue.length; i++) {
	    returnValue[i] = returnValue[i].toLowerCase();
	}

	return returnValue;
    }
}
