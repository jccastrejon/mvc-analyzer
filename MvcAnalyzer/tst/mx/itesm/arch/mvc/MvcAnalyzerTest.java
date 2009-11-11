package mx.itesm.arch.mvc;

import java.io.File;
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
	File analyzedFile;
	File outputFile;

	analyzedFile = new File("bin");
	outputFile = new File("img/MvcAnalyzerClassification-internal.svg");
	outputFile.delete();
	MvcAnalyzer.classifyClassesInDirectory(analyzedFile, false, outputFile);

	outputFile = new File("img/MvcAnalyzerClassification-external.svg");
	outputFile.delete();
	MvcAnalyzer.classifyClassesInDirectory(analyzedFile, true, outputFile);
    }

    /**
     * 
     * @throws Exception
     */
    public void testClassifyClassesInWar() throws Exception {
	File analyzedFile;
	File outputFile;

	analyzedFile = new File(
		"/home/jccastrejon/java/spring-framework-2.5.6.SEC01/samples/petclinic/dist/petclinic.war");
	outputFile = new File("img/petclinicClassification-internal.svg");
	outputFile.delete();
	MvcAnalyzer.classifyClassesinWar(analyzedFile, false, outputFile);

	outputFile = new File("img/petclinicClassification-external.svg");
	outputFile.delete();
	MvcAnalyzer.classifyClassesinWar(analyzedFile, true, outputFile);
    }
}
