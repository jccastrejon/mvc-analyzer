package mx.itesm.arch.mvc.plugin.views;

import java.awt.Color;
import java.awt.Frame;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;

import javax.swing.JScrollPane;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.awt.SWT_AWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;

import com.kitfox.svg.SVGDiagram;
import com.kitfox.svg.SVGDisplayPanel;
import com.kitfox.svg.SVGUniverse;

/**
 * Component that loads and shows the SVG file.
 * 
 * @author jccastrejon
 * 
 */
public class MvcComposite extends Composite implements ISelectionListener {

    /**
     * Used to load the SVG file.
     */
    private SVGUniverse svgUniverse;

    /**
     * Use to display the SVG file.
     */
    private SVGDisplayPanel svgDisplayPanel;

    /**
     * Frame that contains the SVG file.
     */
    private Frame frame;

    /**
     * Full constructor.
     * 
     * @param parent
     *            Parent component.
     * @param style
     *            Style used to embed this component into the parent component.
     */
    public MvcComposite(final Composite parent, final int style) {
	super(parent, style);

	parent.setLayout(new FillLayout());

	this.svgDisplayPanel = new SVGDisplayPanel();
	this.svgDisplayPanel.setBgColor(Color.white);
	this.frame = SWT_AWT.new_Frame(this);
	this.frame.add(new JScrollPane(this.svgDisplayPanel));
	this.frame.setEnabled(true);
    }

    /**
     * Load the actual SVG file.
     * 
     * @param svgFile
     *            SVG File.
     */
    public void loadSvg(final File svgFile) {
	URI uri;
	SVGDiagram diagram;

	this.svgUniverse = new SVGUniverse();
	try {
	    uri = this.svgUniverse.loadSVG(svgFile.toURI().toURL());
	    diagram = this.svgUniverse.getDiagram(uri);
	    svgDisplayPanel.setDiagram(diagram);
	    this.frame.repaint();
	} catch (MalformedURLException e) {
	    throw new RuntimeException(e);
	}
    }

    @Override
    public void selectionChanged(IWorkbenchPart part, ISelection selection) {
	// no-op
    }
}
