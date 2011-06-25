package mx.itesm.arch.dependencies;

import java.io.File;
import java.io.IOException;
import java.util.List;

import junit.framework.TestCase;

/**
 * @author jccastrejon
 * 
 */
public class DependenciesUtilTest extends TestCase {

    /**
     * Export the sorted dependencies for the classes in the
     * <em>lib/junit.jar</em> library and the classess in this project to the
     * <em>img/</em> directory.
     * 
     * @throws IOException
     * 
     */
    public void testExportDependenciesToSVG() throws IOException {
	File imageFile;
	List<ClassDependencies> dependencies;

	dependencies = DependencyAnalyzer.getJarDependencies("lib/junit.jar");
	imageFile = new File("img/junit.internal.svg");
	imageFile.delete();
	DependenciesUtil.exportDependenciesToSVG(dependencies, false, imageFile, DependenciesUtil
		.getInternalPackages(dependencies));
	assertTrue(imageFile.exists());

	imageFile = new File("img/junit-external.svg");
	imageFile.delete();
	DependenciesUtil.exportDependenciesToSVG(dependencies, true, imageFile, DependenciesUtil
		.getInternalPackages(dependencies));
	assertTrue(imageFile.exists());

	dependencies = DependencyAnalyzer.getDirectoryDependencies("bin");
	imageFile = new File("img/MvcAnalyzer-internal.svg");
	imageFile.delete();
	DependenciesUtil.exportDependenciesToSVG(dependencies, false, imageFile, DependenciesUtil
		.getInternalPackages(dependencies));
	assertTrue(imageFile.exists());

	imageFile = new File("img/MvcAnalyzer-external.svg");
	imageFile.delete();
	DependenciesUtil.exportDependenciesToSVG(dependencies, true, imageFile, DependenciesUtil
		.getInternalPackages(dependencies));
	assertTrue(imageFile.exists());
    }
}