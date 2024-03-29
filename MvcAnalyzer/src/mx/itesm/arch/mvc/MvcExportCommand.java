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

import java.util.Map;

import mx.itesm.arch.dependencies.ClassDependencies;
import mx.itesm.arch.dependencies.DependenciesUtil;
import mx.itesm.arch.dependencies.ExportCommand;

/**
 * Export command that adds the MVC data to the graphic export process.
 * 
 * @author jccastrejon
 * 
 */
public class MvcExportCommand implements ExportCommand {

    /**
     * Map of the components' classification.
     */
    Map<String, Layer> classifications;

    /**
     * Full constructor.
     * 
     * @param classifications
     *            Classifications map.
     */
    public MvcExportCommand(final Map<String, Layer> classifications) {
        this.classifications = classifications;
    }

    @Override
    public String execute(final ClassDependencies classDependencies) {
        Layer classLayer;
        String returnValue;

        returnValue = null;
        if (classifications != null) {
            classLayer = classifications.get(classDependencies.getClassName());

            if (classLayer != null) {
                returnValue = "\n\t" + DependenciesUtil.getDotValidName(classDependencies.getClassName())
                        + " [color=\"" + classLayer.getRgbColor() + "\",style=\"" + classLayer.getStyle() + "\"];\n";
            }
        }

        return returnValue;
    }

    @Override
    public String getDescription() {
        StringBuilder returnValue;

        returnValue = new StringBuilder();
        for (Layer layer : Layer.values()) {
            returnValue.append("\n\t" + layer + "Layer [label=\"" + layer + "\",color=\"" + layer.getRgbColor()
                    + "\",style=\"" + layer.getStyle() + "\"];");
        }

        returnValue.append("\n\tsubgraph clusterMVCLayers {\n\trankdir=\"TB\";fontsize=\"8\"; label=\"MVC Layers\";");
        returnValue.append("color=\"#CCFFFF\"; style=\"bold\";\n\t");
        for (Layer layer : Layer.values()) {
            returnValue.append(layer + "Layer; ");
        }

        returnValue.append("\n");
        for (Layer layer : Layer.values()) {
            if (layer.toString().contains("Invalid")) {
                returnValue.append(layer + "Layer -> ");
            }
        }
        returnValue.replace(returnValue.lastIndexOf("->"), returnValue.lastIndexOf("->") + 3, "");
        returnValue.append(" [style=\"invis\"];\n");

        returnValue.append("\n");
        for (Layer layer : Layer.values()) {
            if (!layer.toString().contains("Invalid")) {
                returnValue.append(layer + "Layer -> ");
            }
        }
        returnValue.replace(returnValue.lastIndexOf("->"), returnValue.lastIndexOf("->") + 3, "");
        returnValue.append(" [style=\"invis\"];\n");

        returnValue.append("}");

        return returnValue.toString();
    }

    /**
     * @return the classifications
     */
    public Map<String, Layer> getClassifications() {
        return classifications;
    }

    /**
     * @param classifications
     *            the classifications to set
     */
    public void setClassifications(Map<String, Layer> classifications) {
        this.classifications = classifications;
    }
}
