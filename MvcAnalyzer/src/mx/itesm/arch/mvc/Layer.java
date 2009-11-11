package mx.itesm.arch.mvc;

import weka.core.Attribute;
import weka.core.FastVector;

/**
 * MVC Layer.
 * 
 * @author jccastrejon
 * 
 */
public enum Layer {
    Model("#FFFF5C"), View("#99CCFF"), Controller("#FF9999");

    /**
     * RGB color associated to the layer.
     */
    private String rgbColor;

    /**
     * Variable attribute.
     */
    public static Attribute attribute;

    static {
	FastVector valuesVector;

	valuesVector = new FastVector(Layer.values().length);
	for (Layer layer : Layer.values()) {
	    valuesVector.addElement(layer.toString());
	}

	attribute = new Attribute("Layer", valuesVector, MvcAnalyzer.Variable.values().length + 1);
    }

    /**
     * 
     * @param rgbColor
     */
    private Layer(final String rgbColor) {
	this.rgbColor = rgbColor;
    }

    /**
     * 
     * @return
     */
    public String getRgbColor() {
	return this.rgbColor;
    }
}
