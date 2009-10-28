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
	List<String> internalClasses;
	InputStream classInputStream;
	List<ClassDependencies> returnValue;

	// Get classes in directory
	returnValue = new ArrayList<ClassDependencies>();
	directory = DependencyAnalyzer.getDirectory(path);
	internalClasses = DependencyAnalyzer.getClassesInDirectory(directory, directory);

	// Get classes dependencies
	for (String className : internalClasses) {
	    classInputStream = new FileInputStream(DependencyAnalyzer.getPathFromClassName(
		    className, directory.getAbsolutePath()));
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
		internalClasses.add(DependencyAnalyzer.getClassNameFromPath(jarFile.getParent()
			+ "/" + jarEntry.getName(), jarFile.getParent()));
	    }

	    jarEntry = inputStream.getNextJarEntry();
	}

	// Get dependencies
	inputStream = new JarInputStream(new FileInputStream(file));
	jarEntry = inputStream.getNextJarEntry();
	while (jarEntry != null) {
	    if ((!jarEntry.isDirectory()) && jarEntry.getName().endsWith(".class")) {
		className = DependencyAnalyzer.getClassNameFromPath(jarFile.getParent() + "/"
			+ jarEntry.getName(), jarFile.getParent());
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
	boolean isInternalDependency;
	List<String> internalDependencies;
	List<String> externalDependencies;

	// Recover all dependencies
	dependencies = DependencyAnalyzer.getClassUnsortedDependencies(fileStream);

	// Separate internal - external dependencies
	internalDependencies = new ArrayList<String>();
	externalDependencies = new ArrayList<String>();
	for (String dependency : dependencies) {

	    if (!DependencyAnalyzer.isValidDependency(className, dependency)) {
		continue;
	    }

	    // Internal
	    isInternalDependency = false;
	    for (String internalClass : internalClasses) {
		if (dependency.equals(internalClass)) {
		    internalDependencies.add(dependency);
		    isInternalDependency = true;
		    break;
		}
	    }

	    // External
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
    private static List<String> getClassesInDirectory(final File directory, final File rootDirectory) {
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
		innerFiles = DependencyAnalyzer.getClassesInDirectory(currentFile, rootDirectory);
		returnValue.addAll(innerFiles);
	    } else {
		returnValue.add(DependencyAnalyzer.getClassNameFromPath(currentFile
			.getAbsolutePath(), rootDirectory.getAbsolutePath()));
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

    /**
     * Get a Class File Path from a Class Name.
     * 
     * @param className
     *            Class Name.
     * @param rootPath
     *            Rooth Path containing the Class File Path.
     * @return Class File Path.
     */
    private static String getPathFromClassName(final String className, final String rootPath) {
	return rootPath + "/" + className.replace('.', '/') + ".class";
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
    private static boolean isValidDependency(String className, String dependency) {
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
}
