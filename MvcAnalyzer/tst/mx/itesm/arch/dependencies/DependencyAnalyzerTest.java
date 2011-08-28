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

    /**
     * Class logger.
     */
    Logger logger = Logger.getLogger(DependencyAnalyzerTest.class.getName());

    @Override
    public void setUp() throws Exception {
        LogManager.getLogManager().readConfiguration(
                DependencyAnalyzerTest.class.getClassLoader().getResourceAsStream("cfg/test-logging.properties"));
    }

    /**
     * Recover the unsorted dependencies of this test class.
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
        dependencies = DependencyAnalyzer.getClassUnsortedDependencies(DependencyAnalyzerTest.class);

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
            assertNotNull(dependency.getInternalDependencies());
            assertNotNull(dependency.getExternalDependencies());
        }
    }
}
