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

/**
 * Command to execute during the dependency analysis process.
 * 
 * @author jccastrejon
 * 
 */
public interface DependencyCommand {

    /**
     * Execute an action during the dependency analysis process.
     * 
     * @param fileName
     *            Name of the file being analyzed.
     * @return Name of the dependency to be added to the dependency analysis
     *         process, or <em>null</em> if the specified file is not valid for
     *         this command.
     */
    public String execute(final String fileName);

    /**
     * Get the valid file types for this command.
     * 
     * @return Array containing the command's valid file types.
     */
    public String[] getValidFileTypes();
}
