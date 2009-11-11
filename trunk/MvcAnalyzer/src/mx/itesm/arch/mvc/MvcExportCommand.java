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
		returnValue = "\n\t"
			+ DependenciesUtil.getDotValidName(classDependencies.getClassName())
			+ " [color=\"" + classLayer.getRgbColor() + "\",style=\"filled\"];\n";
	    }
	}

	return returnValue;
    }

    @Override
    public String getDescription() {
	StringBuilder returnValue;

	returnValue = new StringBuilder();
	returnValue.append("\n\tModelLayer [label=\"Model\",color=\"" + Layer.Model.getRgbColor()
		+ "\",style=\"filled\"];");
	returnValue.append("\n\tViewLayer [label=\"View\",color=\"" + Layer.View.getRgbColor()
		+ "\",style=\"filled\"];");
	returnValue.append("\n\tControllerLayer [label=\"Controller\",color=\""
		+ Layer.Controller.getRgbColor() + "\",style=\"filled\"];");
	returnValue.append("\n\tsubgraph clusterMVCLayers {fontsize=\"8\"; label=\"MVC Layers\";");
	returnValue.append("color=\"#CCFFFF\"; style=\"filled\";");
	returnValue.append("\n\tModelLayer; ViewLayer; ControllerLayer}");

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
