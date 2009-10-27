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
     * Test the getClassDependencies() method.
     * 
     * @throws IOException
     */
    public void testGetClassDependencies() throws IOException {
	Set<String> dependencies;
	boolean correctDependencies;
	String[] requiredDependencies;

	correctDependencies = false;
	requiredDependencies = new String[] { "junit.framework.TestCase", "java.util.Set",
		"java.io.IOException" };
	dependencies = DependencyAnalyzer.getClassDependencies(DependencyAnalyzerTest.class);
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
     * 
     * @throws IOException
     */
    public void testGetDirectoryDependencies() throws IOException {
	Map<String, Set<String>> dependencies;

	dependencies = DependencyAnalyzer
		.getDirectoryDependencies("/home/jccastrejon/java/workspace/DistributedQueryOptimizer/bin");
	System.out.println(dependencies);

	assertNotNull(dependencies);
    }
}
