package mx.itesm.arch.dependencies;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
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

	if ((imageFile == null) || (!imageFile.getAbsolutePath().endsWith(".svg"))) {
	    throw new IllegalArgumentException("Not an svg file: " + imageFile.getAbsolutePath());
	}

	fileName = imageFile.getName().substring(0, imageFile.getName().indexOf('.'));
	dotFile = new File(imageFile.getParent() + "/" + fileName + ".dot");

	// Build dot file
	dotDescription = new StringBuilder("digraph " + fileName
		+ " {\n\tnode[shape=box, fontsize=10];\n");
	for (ClassDependencies dependency : dependencies) {
	    className = DependenciesUtil.getDotValidName(dependency.getClassName());
	    for (String internalDependency : dependency.getInternalDependencies()) {
		dotDescription.append("\t" + className + " -> "
			+ DependenciesUtil.getDotValidName(internalDependency) + "\n");
	    }
	}
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
     * Get a valid class name for a dot node.
     * 
     * @param className
     *            Class Name.
     * @return Valid Class Name.
     */
    private static String getDotValidName(final String className) {
	String returnValue;

	// TODO: Use this name when grouping by packages
	// int classNameIndex;
	// classNameIndex = className.lastIndexOf('.') + 1;
	// returnValue = className.substring(classNameIndex,
	// className.length());
	returnValue = "\"" + className + "\"";

	return returnValue;
    }
}
