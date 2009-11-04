package mx.itesm.arch.mvc;

import java.io.File;
import java.io.FileInputStream;
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
    private final static String PROPERTIES_FILE_PATH = "/mvc-variables.properties";

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

	    MvcAnalyzer.classifier = (Classifier) SerializationHelper.read(new FileInputStream(
		    new File("cfg/mvc-classifier.model")));
	} catch (Exception e) {
	    logger.log(Level.SEVERE, "Properties file" + MvcAnalyzer.PROPERTIES_FILE_PATH
		    + " could not be read", e);
	}
    }

    /**
     * Classify the classes contained in the specified path into one of the
     * layers of the MVC pattern.
     * 
     * @param path
     *            Path to the directory containing the classes.
     * @throws Exception
     *             If an Exception occurs during classification.
     */
    public void classifyClassesInDirectory(final String path) throws Exception {
	int instanceLayer;
	Instance instance;
	boolean valueFound;
	Instances instances;
	String[] suffixValues;
	Enumeration variableEnumeration;
	FastVector attributes;
	FastVector variableValues;
	String[] externalApiValues;
	Map<String, String[]> externalApiPackages;
	List<ClassDependencies> directoryDependencies;

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

	// Classify each class in the specified path
	directoryDependencies = DependencyAnalyzer.getDirectoryDependencies(path);
	for (ClassDependencies classDependencies : directoryDependencies) {
	    instance = new Instance(Variable.values().length);

	    // Type
	    instance.setValue(Variable.Type.getAttribute(), "java");

	    // ExternalAPI
	    valueFound = false;
	    externalApi: for (String externalApi : externalApiValues) {
		if (externalApi.equals("none")) {
		    continue;
		}

		for (String externalDependency : classDependencies.getExternalDependencies()) {
		    for (String externalPackage : externalApiPackages.get(externalApi)) {
			if (externalDependency.contains(externalPackage)) {
			    valueFound = true;
			    instance.setValue(Variable.ExternalAPI.getAttribute(), externalApi);
			    break externalApi;
			}
		    }
		}
	    }

	    if (!valueFound) {
		instance.setValue(Variable.ExternalAPI.getAttribute(), "none");
	    }

	    // Suffix
	    valueFound = false;
	    for (String suffix : suffixValues) {
		if (classDependencies.getClassName().endsWith(suffix)) {
		    valueFound = true;
		    instance.setValue(Variable.Suffix.getAttribute(), suffix);
		    break;
		}
	    }

	    if (!valueFound) {
		instance.setValue(Variable.Suffix.getAttribute(), "none");
	    }

	    // Layer
	    instance.setMissing(Variable.Layer.getAttribute());
	    instances.add(instance);
	    instance.setDataset(instances);
	    instanceLayer = (int) MvcAnalyzer.classifier.classifyInstance(instance);
	    classDependencies.setLayer(Variable.Layer.getAttribute().value(instanceLayer));
	    System.out.println(classDependencies.getLayer());
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
	if ((propertyName == null) || (!MvcAnalyzer.classifierVariables.containsKey(propertyName))) {
	    throw new IllegalArgumentException();
	}

	return MvcAnalyzer.classifierVariables.getProperty(propertyName).split(",");
    }
}
