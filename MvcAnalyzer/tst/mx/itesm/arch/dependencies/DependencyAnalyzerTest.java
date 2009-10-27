package mx.itesm.arch.dependencies;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

import junit.framework.TestCase;

/**
 * Tests for the DependencyAnalyzer class.
 * 
 * @author jccastrejon
 * 
 */
public class DependencyAnalyzerTest extends TestCase {

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
	requiredDependencies = new String[] { "junit.framework.TestCase", "java.util.Set",
		"java.io.IOException" };
	dependencies = DependencyAnalyzer
		.getClassUnsortedDependencies(DependencyAnalyzerTest.class);
	System.out.println(dependencies);

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

	assertTrue(correctDependencies);
    }

    /**
     * Recover the dependencies for the classes in the DistributedQueryOptimizer
     * project. The dependencies should be grouped in internal and external
     * groups.
     * 
     * @throws IOException
     */
    public void testGetDirectoryDependencies() throws IOException {
	Map<String, ClassDependencies> dependencies;

	dependencies = DependencyAnalyzer
		.getDirectoryDependencies("/home/jccastrejon/java/workspace/DistributedQueryOptimizer/bin");
	System.out.println(dependencies);

	assertNotNull(dependencies);
	for (ClassDependencies dependency : dependencies.values()) {
	    assertFalse(dependency.getInternalDependencies().isEmpty());
	    assertFalse(dependency.getExternalDependencies().isEmpty());
	}
    }
}
