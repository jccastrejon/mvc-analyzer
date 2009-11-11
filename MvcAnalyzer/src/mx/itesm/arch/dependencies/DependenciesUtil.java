package mx.itesm.arch.dependencies;

import java.io.File;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Dependencies Utility methods.
 * 
 * @author jccastrejon
 * 
 */
public class DependenciesUtil {

    /**
     * Class logger.
     */
    private static Logger logger = Logger.getLogger(DependenciesUtil.class.getName());

    /**
     * Filter used to analyze only directories and .class files.
     */
    private final static FilenameFilter CLASS_FILTER = new FilenameFilter() {
	@Override
	public boolean accept(final File dir, final String name) {
	    String extension;
	    File file;
	    boolean returnValue = false;

	    file = new File(dir.getAbsolutePath() + "/" + name);
	    if (file.isDirectory()) {
		returnValue = true;
	    } else {
		extension = this.getExtensionName(file);
		if (extension != null) {
		    if (extension.equals("class")) {
			return true;
		    }
		}
	    }

	    return returnValue;
	}

	/**
	 * Get a file extension from a file.
	 * 
	 * @param file
	 *            File.
	 * @return File's extension.
	 */
	public String getExtensionName(final File file) {
	    String returnValue = null;
	    String name = file.getName();
	    int i = name.lastIndexOf('.');

	    if (i > 0 && i < name.length() - 1) {
		returnValue = name.substring(i + 1).toLowerCase();
	    }
	    return returnValue;
	}

    };

    /**
     * Export a graphic representation of the Classes dependencies list.
     * 
     * @param dependencies
     *            Class dependencies.
     * @param fileName
     *            Image File Name.
     * @throws IOException
     *             If an I/O error has occurred.
     */
    public static void exportDependenciesToSVG(final List<ClassDependencies> dependencies,
	    final String fileName) throws IOException {
	DependenciesUtil.exportDependenciesToSVG(dependencies, new File(fileName));
    }

    /**
     * Export a graphic representation of the Classes dependencies list.
     * 
     * @param dependencies
     *            Class dependencies.
     * @param imageFile
     *            Image File.
     * @param exportCommands
     *            Commands to be executed during export process.
     * @throws IOException
     *             If an I/O error has occurred.
     */
    public static void exportDependenciesToSVG(final List<ClassDependencies> dependencies,
	    final File imageFile, final ExportCommand... exportCommands) throws IOException {
	File dotFile;
	int processCode;
	Process process;
	String fileName;
	String className;
	String dotCommand;
	String exportCommand;
	FileWriter fileWriter;
	String currentPackageName;
	Set<String> currentPackage;
	StringBuilder dotDescription;
	Map<String, Set<String>> internalPackages;
	Map<String, Set<String>> externalPackages;

	// Validate arguments
	if ((imageFile == null) || (!imageFile.getAbsolutePath().endsWith(".svg"))) {
	    throw new IllegalArgumentException("Not a svg file: " + imageFile.getAbsolutePath());
	}

	// Build dot file
	fileName = imageFile.getName().substring(0, imageFile.getName().indexOf('.'));
	dotFile = new File(imageFile.getParent() + "/" + fileName + ".dot");

	// Simple Dependencies
	externalPackages = new HashMap<String, Set<String>>();
	dotDescription = new StringBuilder("digraph " + fileName
		+ " {\n\tnode[shape=box, fontsize=8, height=.1, width=.1];\n");
	for (ClassDependencies dependency : dependencies) {
	    className = DependenciesUtil.getDotValidName(dependency.getClassName());

	    // Append each result of the registered export commands.
	    if (exportCommands != null) {
		for (ExportCommand command : exportCommands) {
		    exportCommand = command.execute(dependency);

		    // Append only if it's a valid result
		    if (exportCommand != null) {
			dotDescription.append(exportCommand);
		    }
		}
	    }

	    // Add internal dependencies
	    for (String internalDependency : dependency.getInternalDependencies()) {
		dotDescription.append("\t" + className + " -> "
			+ DependenciesUtil.getDotValidName(internalDependency) + ";\n");
	    }

	    // Add external dependencies, also group them by packages
	    for (String externalDependency : dependency.getExternalDependencies()) {
		dotDescription.append("\t" + className + " -> "
			+ DependenciesUtil.getDotValidName(externalDependency) + ";\n");
		currentPackageName = externalDependency.substring(0, externalDependency
			.lastIndexOf('.'));

		if (!externalPackages.containsKey(currentPackageName)) {
		    externalPackages.put(currentPackageName, new HashSet<String>());
		}

		externalPackages.get(currentPackageName).add(
			DependenciesUtil.getDotValidName(externalDependency));
	    }
	}

	// Group internal packages
	internalPackages = new HashMap<String, Set<String>>();
	for (ClassDependencies dependency : dependencies) {
	    currentPackageName = dependency.getPackageName();

	    if (!internalPackages.containsKey(currentPackageName)) {
		internalPackages.put(currentPackageName, new HashSet<String>());
	    }

	    currentPackage = internalPackages.get(currentPackageName);

	    for (ClassDependencies otherDependency : dependencies) {
		if (otherDependency.getClassName().startsWith(currentPackageName)) {
		    currentPackage.add(DependenciesUtil.getDotValidName(otherDependency
			    .getClassName()));
		}
	    }
	}

	// Add clusters
	DependenciesUtil.addClustersToDotDescription(internalPackages, dotDescription);
	DependenciesUtil.addClustersToDotDescription(externalPackages, dotDescription);

	// Add Export Commands description
	for (ExportCommand command : exportCommands) {
	    dotDescription.append(command.getDescription());
	}

	// End of dot description
	dotDescription.append("}");

	// Save dot file
	fileWriter = new FileWriter(dotFile, false);
	fileWriter.write(dotDescription.toString());
	fileWriter.close();

	// Execute dot command
	try {
	    dotCommand = "dot -Tsvg " + dotFile.getAbsolutePath() + " -o "
		    + imageFile.getAbsolutePath();
	    process = Runtime.getRuntime().exec(dotCommand);
	    processCode = process.waitFor();
	    dotFile.delete();

	    if (processCode != 0) {
		throw new RuntimeException("An error ocurred while executing: " + dotCommand);
	    }

	} catch (Exception e) {
	    logger.log(Level.WARNING, "Error creating image file: " + imageFile.getAbsolutePath(),
		    e);
	}
    }

    /**
     * Get a referemce to a directory only if the specified path points to a
     * valid directory, that is, it exists, it's indeed a directory, and can be
     * read. If provided with an invalid path it will throw a
     * IllegalArgumentException.
     * 
     * @param path
     *            Directory path.
     * @return File reference.
     */
    public static File getDirectory(final String path) {
	File returnValue;

	if (path == null) {
	    throw new IllegalArgumentException("Directory must not be null");
	} else {
	    returnValue = new File(path);
	}

	if (!returnValue.exists()) {
	    throw new IllegalArgumentException("Directory " + path + " doesn't exist");
	} else if (!returnValue.isDirectory()) {
	    throw new IllegalArgumentException("Path " + path + " is not a directory");
	} else if (!returnValue.canRead()) {
	    throw new IllegalArgumentException("Directory " + path + " cannot be read");
	}

	return returnValue;
    }

    /**
     * Get all the Classes names in a directory, considering also its
     * subdirectories.
     * 
     * @param directory
     *            Directory.
     * @return List with the Classes names.
     */
    public static List<String> getClassesInDirectory(final File directory, final File rootDirectory) {
	File currentFile;
	File[] directoryFiles;
	List<String> returnValue;
	List<String> innerFiles;

	returnValue = new ArrayList<String>();
	directoryFiles = directory.listFiles(DependenciesUtil.CLASS_FILTER);
	for (int i = 0; i < directoryFiles.length; i++) {
	    currentFile = directoryFiles[i];

	    if (currentFile.isDirectory()) {
		// Recover subdirectory classes
		innerFiles = DependenciesUtil.getClassesInDirectory(currentFile, rootDirectory);
		returnValue.addAll(innerFiles);
	    } else {
		returnValue.add(DependenciesUtil.getClassNameFromPath(
			currentFile.getAbsolutePath(), rootDirectory.getAbsolutePath()));
	    }
	}

	return returnValue;
    }

    /**
     * Get a Class Name from a Class File Path.
     * 
     * @param rootPath
     *            Rooth Path containing the Class File Path.
     * @param path
     *            Class File Path.
     * @return Class Name.
     */
    public static String getClassNameFromPath(final String path, final String rootPath) {
	String returnValue;

	if (path == null) {
	    throw new IllegalArgumentException("Invalid path");
	}

	// {rootAbsolutePath}/{classPath}
	returnValue = path.substring(path.indexOf(rootPath) + rootPath.length() + 1);
	returnValue = returnValue.substring(0, returnValue.indexOf(".class")).replace('/', '.');

	return returnValue;
    }

    /**
     * Get a Class File Path from a Class Name.
     * 
     * @param className
     *            Class Name.
     * @param rootPath
     *            Rooth Path containing the Class File Path.
     * @return Class File Path.
     */
    public static String getPathFromClassName(final String className, final String rootPath) {
	return rootPath + "/" + className.replace('.', '/') + ".class";
    }

    /**
     * Get the WAR name form the specified WAR File Path.
     * 
     * @param path
     *            Path containing the WAR File Path.
     * @return WAR File Name.
     */
    public static String getWarNameFromPath(final String path) {
	int startingPoint;
	String returnValue;

	if (!path.endsWith(".war")) {
	    throw new IllegalArgumentException("Invalid war path: " + path);
	}

	if (path.lastIndexOf('/') > 0) {
	    startingPoint = path.lastIndexOf('/') + 1;
	} else {
	    startingPoint = 0;
	}

	returnValue = path.substring(startingPoint, path.lastIndexOf('.')).toLowerCase();
	return returnValue;
    }

    /**
     * Determine if a dependency is valid for a given class. That is, it's not
     * part of the java.* packages, it's not the same class, and it's not an
     * inner class defined in the same class.
     * 
     * @param className
     * @param dependency
     * @return
     */
    public static boolean isValidDependency(String className, String dependency) {
	boolean returnValue;
	int innerClassIndex;
	int classNameInnerClassIndex;
	String declaringClass;

	if ((className == null) || (dependency == null)) {
	    throw new IllegalArgumentException("Invalid dependency: " + dependency + " for class: "
		    + className);
	}

	innerClassIndex = dependency.indexOf('$');
	classNameInnerClassIndex = className.indexOf('$');
	returnValue = true;
	// Leave out java language classes
	if (dependency.startsWith("java")) {
	    returnValue = false;
	}

	// Leave out self-references
	else if (dependency.equals(className)) {
	    returnValue = false;
	}

	// Leave out inner classes defined in this class
	if (innerClassIndex > 0) {
	    declaringClass = dependency.substring(0, innerClassIndex);
	    // Dependency with declaring class
	    if (declaringClass.equals(className)) {
		returnValue = false;
	    }

	    // Dependency with other inner classes defined in the same class.
	    if (classNameInnerClassIndex > 0) {
		if (className.substring(0, classNameInnerClassIndex).equals(declaringClass)) {
		    returnValue = false;
		}
	    }
	}

	return returnValue;
    }

    /**
     * Add the specified clusters to the dot description.
     * 
     * @param clusters
     *            Clusters.
     * @param dotDescription
     *            dot Description.
     */
    private static void addClustersToDotDescription(final Map<String, Set<String>> clusters,
	    final StringBuilder dotDescription) {

	if ((clusters != null) && (!clusters.isEmpty())) {
	    for (String packageName : clusters.keySet()) {
		dotDescription.append("\tsubgraph \"cluster_" + packageName + "\" {\n");
		dotDescription.append("\t\tfontsize=8;label = \"" + packageName + "\";\n");

		dotDescription.append("\t\t");
		for (String packageDependency : clusters.get(packageName)) {
		    dotDescription.append(packageDependency + ";");
		}

		dotDescription.append("\n\t}\n");
	    }
	}
    }

    /**
     * Get a valid class name for a dot node.
     * 
     * @param className
     *            Class Name.
     * @return Valid Class Name.
     */
    public static String getDotValidName(final String className) {
	String returnValue;

	int classNameIndex;
	classNameIndex = className.lastIndexOf('.') + 1;
	returnValue = "\"" + className.substring(classNameIndex, className.length()) + "\"";

	return returnValue;
    }
}