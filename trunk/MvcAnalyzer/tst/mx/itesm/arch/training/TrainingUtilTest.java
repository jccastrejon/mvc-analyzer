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
package mx.itesm.arch.training;

import java.io.File;

import junit.framework.TestCase;

/**
 * 
 * @author jccastrejon
 * 
 */
public class TrainingUtilTest extends TestCase {

    public void testGenerateTrainingSet() throws Exception {
        TrainingUtil.generateTrainingSet(new File("/Users/jccastrejon/Desktop/softwareRecovery/web/training/apps/roo"));
    }
}
