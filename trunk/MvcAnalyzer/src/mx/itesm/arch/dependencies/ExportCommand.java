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
 * Command to execute during the graphic export process.
 * 
 * @author jccastrejon
 * 
 */
public interface ExportCommand {

    /**
     * Execute an action during the graphic export of the specified class'
     * dependencies.
     * 
     * @param classDependencies
     *            Class' Dependencies.
     */
    public String execute(final ClassDependencies classDependencies);

    /**
     * Get the description of this export command.
     * 
     * @return Command description.
     */
    public String getDescription();
}
