package mx.itesm.arch.dependencies;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
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
     * @param fileName
     *            Image File.
     * @throws IOException
     *             If an I/O error has occurred.
     */
    public static void exportDependenciesToSVG(final List<ClassDependencies> dependencies,
	    final File imageFile) throws IOException {
	File dotFile;
	int processCode;
	Process process;
	String fileName;
	String className;
	String dotCommand;
	FileWriter fileWriter;
	StringBuilder dotDescription;
	String currentPackageName;
	Set<String> currentPackage;
	Map<String, Set<String>> internalPackages;
	Map<String, Set<String>> externalPackages;

	// Validate arguments
	if ((imageFile == null) || (!imageFile.getAbsolutePath().endsWith(".svg"))) {
	    throw new IllegalArgumentException("Not an svg file: " + imageFile.getAbsolutePath());
	}

	// Build dot file
	fileName = imageFile.getName().substring(0, imageFile.getName().indexOf('.'));
	dotFile = new File(imageFile.getParent() + "/" + fileName + ".dot");

	// Simple Dependencies
	externalPackages = new HashMap<String, Set<String>>();
	dotDescription = new StringBuilder("digraph " + fileName
		+ " {\n\tnode[shape=box, fontsize=8];\n");
	for (ClassDependencies dependency : dependencies) {
	    className = DependenciesUtil.getDotValidName(dependency.getClassName());

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
	    currentPackageName = dependency.getClassName().substring(0,
		    dependency.getClassName().lastIndexOf('.'));

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
    private static String getDotValidName(final String className) {
	String returnValue;

	// TODO: Use this name when grouping by packages
	int classNameIndex;
	classNameIndex = className.lastIndexOf('.') + 1;
	returnValue = "\"" + className.substring(classNameIndex, className.length()) + "\"";
	// returnValue = "\"" + className + "\"";

	return returnValue;
    }
}
