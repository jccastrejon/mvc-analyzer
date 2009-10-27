package mx.itesm.arch.dependencies;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import junit.framework.TestCase;

/**
 * Tests for the DependencyAnalyzer class.
 * 
 * @author jccastrejon
 * 
 */
public class DependencyAnalyzerTest extends TestCase {

    @Override
    public void setUp() throws Exception {
	LogManager.getLogManager().readConfiguration(
		DependencyAnalyzerTest.class.getClassLoader().getResourceAsStream(
			"test-logging.properties"));
    }

    /**
     * Class logger.
     */
    Logger logger = Logger.getLogger(DependencyAnalyzerTest.class.getName());

    /**
     * Recover the dependencies of this test class in an unsorted manner.
     * 
     * @throws IOException
     */
    public void testGetClassUnsortedDependencies() throws IOException {
	Set<String> dependencies;
	boolean correctDependencies;
	String[] requiredDependencies;

	correctDependencies = false;
	requiredDependencies = new String[] { TestCase.class.getName(), Set.class.getName(),
		IOException.class.getName() };
	dependencies = DependencyAnalyzer
		.getClassUnsortedDependencies(DependencyAnalyzerTest.class);

	// Check if all the required dependencies where found
	for (String requiredDependency : requiredDependencies) {
	    correctDependencies = false;
	    for (String dependency : dependencies) {
		if (dependency.equals(requiredDependency)) {
		    correctDependencies = true;
		}
	    }

	    if (!correctDependencies) {
		break;
	    }
	}

	logger.info(dependencies.toString());
	assertTrue(correctDependencies);
    }

    /**
     * Recover the sorted dependencies for the classes in this project.
     * 
     * @throws IOException
     */
    public void testGetDirectoryDependencies() throws IOException {
	List<ClassDependencies> dependencies;

	dependencies = DependencyAnalyzer.getDirectoryDependencies("bin");
	this.verifyDependencies(dependencies);
    }

    /**
     * Recover the dependencies for the ASM library.
     * 
     * @throws IOException
     */
    public void testGetJarDependencies() throws IOException {
	List<ClassDependencies> dependencies;

	dependencies = DependencyAnalyzer.getJarDependencies("lib/asm-all-3.2.jar");
	this.verifyDependencies(dependencies);
    }

    /**
     * Verifies if the dependencies are non-empty.
     * 
     * @param dependencies
     *            Classes dependencies.
     */
    private void verifyDependencies(List<ClassDependencies> dependencies) {
	assertNotNull(dependencies);

	for (ClassDependencies dependency : dependencies) {
	    logger.info(dependency + "\n");
	}

	for (ClassDependencies dependency : dependencies) {
	    assertFalse(dependency.getInternalDependencies().isEmpty());
	    assertFalse(dependency.getExternalDependencies().isEmpty());
	}
    }
}
