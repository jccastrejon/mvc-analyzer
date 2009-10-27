package mx.itesm.arch.dependencies;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

import org.objectweb.asm.ClassReader;

/**
 * Recover Classes dependencies.
 * 
 * @author jccastrejon
 * 
 */
public class DependencyAnalyzer {

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
     * Recover the dependencies from each Java class within the specified
     * directory.
     * 
     * @param path
     *            Directory path.
     * @return Dependencies for each class within the directory.
     * @throws IOException
     *             If an I/O error has occurred.
     */
    public static Map<String, Set<String>> getDirectoryDependencies(final String path)
	    throws IOException {
	File directory;
	List<File> classes;
	InputStream classInputStream;
	Map<String, Set<String>> returnValue;

	returnValue = new HashMap<String, Set<String>>();
	directory = DependencyAnalyzer.getDirectory(path);
	classes = DependencyAnalyzer.getClassesInDirectory(directory);

	for (File clazz : classes) {
	    classInputStream = new FileInputStream(clazz);
	    returnValue.put(clazz.getAbsolutePath(), DependencyAnalyzer
		    .getClassDependencies(classInputStream));
	}

	return returnValue;
    }

    /**
     * Recover the dependencies from each Java class within the specified JAR
     * file.
     * 
     * @param file
     *            Path to the JAR file.
     * @return Dependencies for each class within the JAR file.
     * @throws IOException
     *             If an I/O error has occurred.
     */
    public static Map<String, Set<String>> getJarDependencies(final String file) throws IOException {
	JarInputStream inputStream;
	JarEntry jarEntry;
	Map<String, Set<String>> returnValue;
	Set<String> dependencies;

	inputStream = new JarInputStream(new FileInputStream(file));
	returnValue = new HashMap<String, Set<String>>();
	jarEntry = inputStream.getNextJarEntry();

	while (jarEntry != null) {

	    if ((!jarEntry.isDirectory()) && jarEntry.getName().endsWith(".class")) {
		dependencies = DependencyAnalyzer.getClassDependencies(inputStream);
		returnValue.put(jarEntry.getName(), dependencies);
	    }

	    jarEntry = inputStream.getNextJarEntry();
	}

	return returnValue;
    }

    /**
     * Recover the dependencies for the specified Class.
     * 
     * @param clazz
     *            Class to analyze.
     * @return Class' dependencies.
     * @throws IOException
     *             If an I/O error has occurred.
     */
    public static Set<String> getClassDependencies(final Class<?> clazz) throws IOException {
	return DependencyAnalyzer.getClassDependencies(clazz.getResourceAsStream("/"
		+ clazz.getName().replace('.', '/') + ".class"));
    }

    /**
     * Recover the dependencies for the specified Class.
     * 
     * @param fileStream
     *            IputStream to the required class file.
     * @return Class' dependencies.
     * @throws IOException
     *             If an I/O error has occurred.
     */
    public static Set<String> getClassDependencies(final InputStream fileStream) throws IOException {
	Set<String> returnValue;
	DependencyVisitor dependencyVisitor;

	dependencyVisitor = new DependencyVisitor();
	new ClassReader(fileStream).accept(dependencyVisitor, ClassReader.SKIP_DEBUG);
	returnValue = dependencyVisitor.getDependencies();

	return returnValue;
    }

    /**
     * Get a directory reference only if the specified path points to a valid
     * directory, that is, it exists, it's indeed a directory, and can be read.
     * If provided with an invalid path it will throw a
     * IllegalArgumentException.
     * 
     * @param path
     *            Directory path.
     * @return File reference.
     */
    private static File getDirectory(final String path) {
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
     * Get all the .class files in a directory, considering also its
     * subdirectories.
     * 
     * @param directory
     *            Directory.
     * @return List with the .class Files references.
     */
    private static List<File> getClassesInDirectory(final File directory) {
	List<File> returnValue;
	List<File> innerFiles;
	File[] directoryFiles;
	File currentFile;

	returnValue = new ArrayList<File>();
	directoryFiles = directory.listFiles(DependencyAnalyzer.CLASS_FILTER);
	for (int i = 0; i < directoryFiles.length; i++) {
	    currentFile = directoryFiles[i];

	    if (currentFile.isDirectory()) {
		innerFiles = DependencyAnalyzer.getClassesInDirectory(currentFile);
		returnValue.addAll(innerFiles);
	    } else {
		returnValue.add(currentFile);
	    }
	}

	return returnValue;
    }
}
