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
     * 
     */
    private final static FilenameFilter CLASS_FILTER = new FilenameFilter() {
	@Override
	public boolean accept(File dir, String name) {
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
	public String getExtensionName(File file) {
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
     * 
     * @param path
     * @return
     * @throws IOEX
     */
    public static Map<String, Set<String>> getDirectoryDependencies(String path) throws IOException {
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
    public static Map<String, Set<String>> getJarDependencies(String file) throws IOException {
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
    public static Set<String> getClassDependencies(Class<?> clazz) throws IOException {
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
    public static Set<String> getClassDependencies(InputStream fileStream) throws IOException {
	Set<String> returnValue;
	DependencyVisitor dependencyVisitor;

	dependencyVisitor = new DependencyVisitor();
	new ClassReader(fileStream).accept(dependencyVisitor, ClassReader.SKIP_DEBUG);
	returnValue = dependencyVisitor.getDependencies();

	return returnValue;
    }

    /**
     * 
     * @param directory
     * @return
     */
    private static File getDirectory(String directory) {
	File returnValue;

	if (directory == null) {
	    throw new IllegalArgumentException("Directory must not be null");
	} else {
	    returnValue = new File(directory);
	}

	if (!returnValue.exists()) {
	    throw new IllegalArgumentException("Directory " + directory + " doesn't exist");
	} else if (!returnValue.isDirectory()) {
	    throw new IllegalArgumentException("Path " + directory + " is not a directory");
	} else if (!returnValue.canRead()) {
	    throw new IllegalArgumentException("Directory " + directory + " cannot be read");
	}

	return returnValue;
    }

    /**
     * 
     * @param directory
     * @return
     */
    private static List<File> getClassesInDirectory(File directory) {
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
