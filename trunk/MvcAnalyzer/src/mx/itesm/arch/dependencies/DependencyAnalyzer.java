package mx.itesm.arch.dependencies;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
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
    public static List<ClassDependencies> getDirectoryDependencies(final String path)
	    throws IOException {
	File directory;
	String className;
	List<String> internalClasses;
	InputStream classInputStream;
	List<ClassDependencies> returnValue;

	// Get classes in directory
	returnValue = new ArrayList<ClassDependencies>();
	directory = DependencyAnalyzer.getDirectory(path);
	internalClasses = DependencyAnalyzer.getClassesInDirectory(directory);

	// Get classes dependencies
	for (String classAbsolutePath : internalClasses) {
	    className = DependencyAnalyzer.getClassNameFromPath(classAbsolutePath, path);
	    classInputStream = new FileInputStream(classAbsolutePath);
	    returnValue.add(DependencyAnalyzer.getClassSortedDependencies(className,
		    classInputStream, internalClasses, path));
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
    public static List<ClassDependencies> getJarDependencies(final String file) throws IOException {
	File jarFile;
	String className;
	JarEntry jarEntry;
	JarInputStream inputStream;
	List<String> internalClasses;
	ClassDependencies dependencies;
	List<ClassDependencies> returnValue;

	returnValue = new ArrayList<ClassDependencies>();
	internalClasses = new ArrayList<String>();
	jarFile = new File(file);

	// Get internal classes
	inputStream = new JarInputStream(new FileInputStream(file));
	jarEntry = inputStream.getNextJarEntry();
	while (jarEntry != null) {
	    if ((!jarEntry.isDirectory()) && jarEntry.getName().endsWith(".class")) {
		internalClasses.add(jarFile.getParent() + "/" + jarEntry.getName());
	    }

	    jarEntry = inputStream.getNextJarEntry();
	}

	// Get dependencies
	inputStream = new JarInputStream(new FileInputStream(file));
	jarEntry = inputStream.getNextJarEntry();
	while (jarEntry != null) {
	    if ((!jarEntry.isDirectory()) && jarEntry.getName().endsWith(".class")) {
		className = DependencyAnalyzer.getClassNameFromPath(jarEntry.getName(), jarFile
			.getParent());
		dependencies = DependencyAnalyzer.getClassSortedDependencies(className,
			inputStream, internalClasses, jarFile.getParent());
		returnValue.add(dependencies);
	    }

	    jarEntry = inputStream.getNextJarEntry();
	}

	return returnValue;
    }

    /**
     * Recover the dependencies for the specified Class with no special grouping
     * criteria.
     * 
     * @param clazz
     *            Class to analyze.
     * @return Unsorted Class' dependencies.
     * @throws IOException
     *             If an I/O error has occurred.
     */
    public static Set<String> getClassUnsortedDependencies(final Class<?> clazz) throws IOException {
	return DependencyAnalyzer.getClassUnsortedDependencies(clazz.getResourceAsStream("/"
		+ clazz.getName().replace('.', '/') + ".class"));
    }

    /**
     * Recover the dependencies for the specified Class with no special grouping
     * criteria.
     * 
     * @param fileStream
     *            IputStream to the required class file.
     * @return Unsorted Class' dependencies.
     * @throws IOException
     *             If an I/O error has occurred.
     */
    public static Set<String> getClassUnsortedDependencies(final InputStream fileStream)
	    throws IOException {
	Set<String> returnValue;
	DependencyVisitor dependencyVisitor;

	// Recover all dependencies
	dependencyVisitor = new DependencyVisitor();
	new ClassReader(fileStream).accept(dependencyVisitor, ClassReader.SKIP_DEBUG);
	returnValue = dependencyVisitor.getDependencies();

	return returnValue;
    }

    /**
     * Recover the dependencies for the specified Class, grouped by
     * <em>internal</em> (Same Project) and <em>external</em> (Libraries)
     * dependencies.
     * 
     * @param clazz
     *            Class to analyze.
     * @param internalClasses
     *            List of classes that belong to the same project as the class
     *            being analyzed.
     * @return Class' dependencies.
     * @throws IOException
     *             If an I/O error has occurred.
     */
    public static ClassDependencies getClassSortedDependencies(final Class<?> clazz,
	    final List<String> internalClasses) throws IOException {
	String classDirectory;
	ClassDependencies returnValue;

	classDirectory = clazz.getResource(clazz.getName()).getPath();
	returnValue = DependencyAnalyzer.getClassSortedDependencies(clazz.getName(), clazz
		.getResourceAsStream("/" + clazz.getName().replace('.', '/') + ".class"),
		internalClasses, classDirectory);

	return returnValue;
    }

    /**
     * Recover the dependencies for the specified Class, grouped by
     * <em>internal</em> (Same Project) and <em>external</em> (Libraries)
     * dependencies.
     * 
     * @param className
     *            Class name.
     * @param fileStream
     *            IputStream to the required class file.
     * @param internalClasses
     *            List of classes that belong to the same project as the class
     *            being analyzed.
     * @param rootPath
     *            Root Path that contains the project classes.
     * @return Class' dependencies.
     * @throws IOException
     *             If an I/O error has occurred.
     */
    public static ClassDependencies getClassSortedDependencies(final String className,
	    final InputStream fileStream, final List<String> internalClasses, final String rootPath)
	    throws IOException {
	Set<String> dependencies;
	String internalClassName;
	boolean isInternalDependency;
	List<String> internalDependencies;
	List<String> externalDependencies;

	// Recover all dependencies
	dependencies = DependencyAnalyzer.getClassUnsortedDependencies(fileStream);

	// Separate internal - external dependencies
	internalDependencies = new ArrayList<String>();
	externalDependencies = new ArrayList<String>();
	for (String dependency : dependencies) {
	    isInternalDependency = false;
	    for (String internalClass : internalClasses) {
		internalClassName = DependencyAnalyzer
			.getClassNameFromPath(internalClass, rootPath);

		if (dependency.equals(internalClassName)) {
		    internalDependencies.add(dependency);
		    isInternalDependency = true;
		    break;
		}
	    }

	    if (!isInternalDependency) {
		externalDependencies.add(dependency);
	    }
	}

	return new ClassDependencies(className, internalDependencies, externalDependencies);
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
     * Get all the Classes names in a directory, considering also its
     * subdirectories.
     * 
     * @param directory
     *            Directory.
     * @return List with the Classes names.
     */
    private static List<String> getClassesInDirectory(final File directory) {
	File currentFile;
	File[] directoryFiles;
	List<String> returnValue;
	List<String> innerFiles;

	returnValue = new ArrayList<String>();
	directoryFiles = directory.listFiles(DependencyAnalyzer.CLASS_FILTER);
	for (int i = 0; i < directoryFiles.length; i++) {
	    currentFile = directoryFiles[i];

	    if (currentFile.isDirectory()) {
		// Recover subdirectory classes
		innerFiles = DependencyAnalyzer.getClassesInDirectory(currentFile);
		returnValue.addAll(innerFiles);
	    } else {
		returnValue.add(currentFile.getAbsolutePath());
	    }
	}

	return returnValue;
    }

    /**
     * Get a class name from a Class File Path.
     * 
     * @param rootPath
     *            Rooth Path containing the Class File Path.
     * @param path
     *            Class File Path.
     * @return Class Name.
     */
    private static String getClassNameFromPath(final String path, final String rootPath) {
	String returnValue;

	if (path == null) {
	    throw new IllegalArgumentException("Invalid path");
	}

	// {rootAbsolutePath}/{classPath}
	returnValue = path.substring(path.indexOf(rootPath) + rootPath.length() + 1);
	returnValue = returnValue.substring(0, returnValue.indexOf(".class")).replace('/', '.');

	return returnValue;
    }
}
