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
        DependenciesUtil.exportDependenciesToSVG(dependencies, false, imageFile,
                DependenciesUtil.getInternalPackages(dependencies, new String[] { "java" }));
        assertTrue(imageFile.exists());

        imageFile = new File("img/junit-external.svg");
        imageFile.delete();
        DependenciesUtil.exportDependenciesToSVG(dependencies, true, imageFile,
                DependenciesUtil.getInternalPackages(dependencies, new String[] { "java" }));
        assertTrue(imageFile.exists());

        dependencies = DependencyAnalyzer.getDirectoryDependencies("bin");
        imageFile = new File("img/MvcAnalyzer-internal.svg");
        imageFile.delete();
        DependenciesUtil.exportDependenciesToSVG(dependencies, false, imageFile,
                DependenciesUtil.getInternalPackages(dependencies, new String[] { "java" }));
        assertTrue(imageFile.exists());

        imageFile = new File("img/MvcAnalyzer-external.svg");
        imageFile.delete();
        DependenciesUtil.exportDependenciesToSVG(dependencies, true, imageFile,
                DependenciesUtil.getInternalPackages(dependencies, new String[] { "java" }));
        assertTrue(imageFile.exists());
    }
}
