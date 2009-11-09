package mx.itesm.arch.mvc;

import java.io.IOException;

import junit.framework.TestCase;

/**
 * @author jccastrejon
 * 
 */
public class MvcAnalyzerTest extends TestCase {

    /**
     * @throws IOException
     * 
     */
    public void testClassifyClassesInDirectory() throws Exception {
	MvcAnalyzer.classifyClassesInDirectory("bin");
    }

    /**
     * 
     * @throws Exception
     */
    public void testClassifyClassesInWar() throws Exception {
	MvcAnalyzer
		.classifyClassesinWar("/home/jccastrejon/java/spring-framework-2.5.6.SEC01/samples/petclinic/dist/petclinic.war");
    }
}
