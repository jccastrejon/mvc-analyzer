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