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
	List<ClassDependencies> dependencies;
	File imageFile;

	dependencies = DependencyAnalyzer.getJarDependencies("lib/junit.jar");
	imageFile = new File("img/junit.svg");
	imageFile.delete();
	DependenciesUtil.exportDependenciesToSVG(dependencies, imageFile);
	assertTrue(imageFile.exists());

	dependencies = DependencyAnalyzer.getDirectoryDependencies("bin");
	imageFile = new File("img/MvcAnalyzer.svg");
	imageFile.delete();
	DependenciesUtil.exportDependenciesToSVG(dependencies, imageFile);
	assertTrue(imageFile.exists());
    }
}
