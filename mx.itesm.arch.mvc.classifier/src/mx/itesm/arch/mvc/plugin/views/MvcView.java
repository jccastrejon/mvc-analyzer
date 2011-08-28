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
package mx.itesm.arch.mvc.plugin.views;

import java.io.File;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

/**
 * View that contains the result of the MVC classification.
 * 
 * @author jccastrejon
 * 
 */
public class MvcView extends ViewPart {

    /**
     * The ID of the view as specified by the extension.
     */
    public static final String ID = "mx.itesm.arch.mvc.plugin.views.MvcView";

    /**
     * Component to draw the SVG result file.
     */
    private MvcComposite mvcComposite;

    /**
     * Load the SVG file.
     * 
     * @param svgFile
     *            SVG file.
     */
    public void loadSvg(final File svgFile) {
        this.mvcComposite.loadSvg(svgFile);
    }

    @Override
    public void createPartControl(Composite parent) {
        this.getViewSite().getWorkbenchWindow().getSelectionService();
        this.mvcComposite = new MvcComposite(parent, SWT.EMBEDDED);
        this.getViewSite().getPage().addSelectionListener(this.mvcComposite);
        parent.layout();
    }

    @Override
    public void setFocus() {
        this.mvcComposite.setFocus();
    }

    @Override
    public void dispose() {
        getViewSite().getPage().removeSelectionListener(mvcComposite);
        mvcComposite.dispose();
        super.dispose();
    }
}