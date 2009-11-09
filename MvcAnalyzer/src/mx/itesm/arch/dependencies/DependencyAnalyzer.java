package mx.itesm.arch.dependencies;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

import org.objectweb.asm.ClassReader;

/**
 * Recover Classes dependencies.
 * 
 * @author jccastrejon
 * 
 */
public class DependencyAnalyzer {

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
	directory = DependenciesUtil.getDirectory(path);
	internalClasses = DependenciesUtil.getClassesInDirectory(directory, directory);

	// Get classes dependencies
	for (String className : internalClasses) {
	    classInputStream = new FileInputStream(DependenciesUtil.getPathFromClassName(className,
		    directory.getAbsolutePath()));
	    returnValue.add(DependencyAnalyzer.getClassSortedDependencies(className,
		    classInputStream, internalClasses, path));
	}

	return returnValue;
    }

    public static List<ClassDependencies> getWarDependencies(final String file) throws Exception {
	ZipFile zipFile;
	ZipEntry zipEntry;
	ZipInputStream inputStream;
	List<ClassDependencies> returnValue;

	// .class files in the WAR file
	returnValue = DependencyAnalyzer.getJarDependencies(file);

	// JAR files that belong to the same project
	inputStream = new ZipInputStream(new FileInputStream(file));
	zipEntry = inputStream.getNextEntry();
	zipFile = new ZipFile(file);
	while (zipEntry != null) {
	    if ((!zipEntry.isDirectory()) && zipEntry.getName().endsWith(".jar")) {
		// Consider only JAR files that start with the same name as the
		// WAR file
		if (zipEntry.getName().toLowerCase().startsWith(
			file.substring(0, file.lastIndexOf('.')).toLowerCase())) {
		    returnValue.addAll(DependencyAnalyzer.getJarDependencies(zipFile
			    .getInputStream(zipEntry)));
		}
	    }
	}

	return returnValue;
    }

    /**
     * 
     * @param file
     * @return
     */
    public static List<ClassDependencies> getJarDependencies(final InputStream file) {
	return null;
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
		internalClasses.add(DependenciesUtil.getClassNameFromPath(jarFile.getParent() + "/"
			+ jarEntry.getName(), jarFile.getParent()));
	    }

	    jarEntry = inputStream.getNextJarEntry();
	}

	// Get dependencies
	inputStream = new JarInputStream(new FileInputStream(file));
	jarEntry = inputStream.getNextJarEntry();
	while (jarEntry != null) {
	    if ((!jarEntry.isDirectory()) && jarEntry.getName().endsWith(".class")) {
		className = DependenciesUtil.getClassNameFromPath(jarFile.getParent() + "/"
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

	    if (!DependenciesUtil.isValidDependency(className, dependency)) {
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
}
