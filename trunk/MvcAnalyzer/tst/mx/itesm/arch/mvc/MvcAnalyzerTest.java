/*
 * Copyright 2011 jccastrejon
 *  
 * This file is part of MvcAnalyzer.
 *
 * MvcAnalyzer is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * any later version.
 *
 * MvcAnalyzer is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with MvcAnalyzer. If not, see <http://www.gnu.org/licenses/>.
 */
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

        analyzedFile = new File("/Users/jccastrejon/java/workspace_Model2Roo/PetClinic_Ecore/src");
        outputFile = new File("img/petclinicClassification-directory-internal.svg");
        outputFile.delete();
        MvcAnalyzer.classifyClassesInDirectory(analyzedFile, false, outputFile);

        outputFile = new File("img/petclinicClassification-directory-external.svg");
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
