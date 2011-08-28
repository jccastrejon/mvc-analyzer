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
package mx.itesm.arch.mvc.plugin.actions;

import java.io.File;

import mx.itesm.arch.mvc.MvcAnalyzer;
import mx.itesm.arch.mvc.plugin.views.MvcView;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.PluginAction;

@SuppressWarnings("restriction")
public class MvcAction implements IObjectActionDelegate, Runnable {

    /**
     * Window that manages this action.
     */
    private Shell shell;

    /**
     * Reference to the current selected resource.
     */
    private IResource resource;

    /**
     * View that contains the MVC classification results.
     */
    private MvcView mvcView;

    /**
     * Flag that indicates whether or not the external dependencies are shown in
     * the output results.
     */
    private final static boolean INCLUDE_EXTERNAL_DEPENDENCIES = false;

    /**
     * Constructor for Action1.
     */
    public MvcAction() {
        super();
    }

    /**
     * @see IObjectActionDelegate#setActivePart(IAction, IWorkbenchPart)
     */
    public void setActivePart(IAction action, IWorkbenchPart targetPart) {
        shell = targetPart.getSite().getShell();
    }

    @Override
    public void run() {
        File imageFile;
        File resourceFile;
        String exceptionMessage;
        boolean classificationCompleted;

        exceptionMessage = null;
        classificationCompleted = false;
        if (this.resource.getLocation() != null) {
            resourceFile = new File(this.resource.getLocation().toOSString());
            imageFile = new File(resourceFile.getParentFile().getAbsolutePath() + "/" + this.resource.getName()
                    + ".svg");
            // Classify
            try {
                if (this.resource instanceof IProject) {
                    // Only generated classes in the bin directory
                    MvcAnalyzer.classifyClassesInDirectory(new File(resourceFile, "bin"),
                            MvcAction.INCLUDE_EXTERNAL_DEPENDENCIES, imageFile);
                } else if (this.resource instanceof IFile) {
                    MvcAnalyzer.classifyClassesinWar(resourceFile, MvcAction.INCLUDE_EXTERNAL_DEPENDENCIES, imageFile);
                }
            } catch (Exception e) {
                exceptionMessage = e.getMessage();
                classificationCompleted = false;
            }

            // Open result view
            mvcView.loadSvg(imageFile);
            classificationCompleted = true;
        }

        if (!classificationCompleted) {
            MessageDialog.openInformation(shell, "MVC Classifier", "There was an error while classifying: "
                    + exceptionMessage);
        }
    }

    @Override
    public void run(IAction action) {
        try {
            this.resource = (((IResource) ((IStructuredSelection) ((PluginAction) action).getSelection())
                    .getFirstElement()));

            this.mvcView = (MvcView) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
                    .showView(MvcView.ID);
        } catch (PartInitException e) {
            throw new RuntimeException(e);
        }

        new Thread(this).start();
    }

    @Override
    public void selectionChanged(IAction action, ISelection selection) {
    }
}
