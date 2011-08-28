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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import mx.itesm.arch.mvc.Layer;
import mx.itesm.arch.mvc.MvcAnalyzer;

/**
 * 
 * @author jccastrejon
 * 
 */
public class TrainingUtil {

    /**
     * Generate a Weka training file from the files included in the specified
     * root directory and its sub-directories. The training file is saved
     * directly into the root directory.
     * 
     * @param rootDirectory
     * @throws Exception
     */
    public static void generateTrainingSet(final File rootDirectory) throws Exception {
        List<File> warFiles;
        StringBuilder outputContent;
        Map<String, Layer> trainingItems;

        // Unpackaged items
        trainingItems = MvcAnalyzer.classifyClassesInDirectory(rootDirectory, false, null);

        // Packaged items
        warFiles = TrainingUtil.getWarFiles(rootDirectory);
        if (warFiles != null) {
            for (File warFile : warFiles) {
                trainingItems.putAll(MvcAnalyzer.classifyClassesinWar(warFile, false, null));
            }
        }

        outputContent = new StringBuilder();
        for (String trainingItem : trainingItems.keySet()) {
            if (!trainingItem.endsWith(".java")) {
                // Type
                
                // ExternalAPI
                
                // Suffix
                
                // Layer
                
                
                outputContent.append(trainingItem).append("\n");
            }
        }

        TrainingUtil.createOutputFile(new File(rootDirectory, "training_set.arff"), outputContent.toString());
        System.out.println(trainingItems.size());
    }

    /**
     * Get the WAR files included in the specified directory and its
     * sub-directories.
     * 
     * @param directory
     * @return
     */
    private static List<File> getWarFiles(final File directory) {
        List<File> returnValue;

        returnValue = null;
        if ((directory != null) && (directory.isDirectory())) {
            returnValue = new ArrayList<File>();
            for (File item : directory.listFiles()) {
                if (item.getPath().endsWith(".war")) {
                    returnValue.add(item);
                } else if (item.isDirectory()) {
                    returnValue.addAll(TrainingUtil.getWarFiles(item));
                }
            }
        }

        return returnValue;
    }

    /**
     * 
     * @param outputContent
     * @throws IOException
     */
    private static void createOutputFile(final File outputFile, final String outputContent) throws IOException {
        BufferedWriter writer;

        // Overwrite
        if (outputFile.exists()) {
            outputFile.delete();
        }

        // Save content
        writer = new BufferedWriter(new FileWriter(outputFile));
        writer.write(outputContent);
        writer.close();
    }
}
