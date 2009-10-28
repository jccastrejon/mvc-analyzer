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
     * Export the sorted dependencies for the classes in this project to the
     * <em>img/MvcAnalyzer.svg</em> file.
     * 
     * @throws IOException
     * 
     */
    public void testExportDependenciesToSVG() throws IOException {
	List<ClassDependencies> dependencies = DependencyAnalyzer.getDirectoryDependencies("bin");
	File imageFile;

	imageFile = new File("img/MvcAnalyzer.svg");
	imageFile.delete();
	DependenciesUtil.exportDependenciesToSVG(dependencies, imageFile);

	assertTrue(imageFile.exists());
    }
}
