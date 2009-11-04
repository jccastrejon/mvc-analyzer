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
	MvcAnalyzer mvcAnalyzer;

	mvcAnalyzer = new MvcAnalyzer();
	mvcAnalyzer.classifyClassesInDirectory("bin");
    }
}
