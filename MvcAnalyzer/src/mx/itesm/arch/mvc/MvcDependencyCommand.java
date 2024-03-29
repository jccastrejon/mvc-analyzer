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

import mx.itesm.arch.dependencies.DependencyCommand;

/**
 * Dependency Command that adds the project's web pages and configuration files
 * to the dependency analysis.
 * 
 * @author jccastrejon
 * 
 */
public class MvcDependencyCommand implements DependencyCommand {

    /**
     * Valid components to be analyzed.
     */
    private static String[] VALID_TYPES = MvcAnalyzer.getPropertyValues(MvcAnalyzer.Variable.Type.getVariableName());

    @Override
    public String execute(String fileName) {
        String returnValue;

        returnValue = null;
        for (String validType : MvcDependencyCommand.VALID_TYPES) {
            if (fileName.endsWith(validType)) {
                returnValue = fileName;
                if (returnValue.indexOf('/') < 0) {
                    returnValue = "/" + returnValue;
                }

                break;
            }
        }

        return returnValue;
    }

    @Override
    public String[] getValidFileTypes() {
        return MvcDependencyCommand.VALID_TYPES;
    }
}