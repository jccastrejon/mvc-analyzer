package mx.itesm.arch.dependencies;

import java.util.List;

/**
 * @author jccastrejon
 * 
 */
public class ClassDependencies {

    /**
     * 
     */
    private String layer;

    /**
     * Class name.
     */
    private String className;

    /**
     * Dependencies with internal classes (same project).
     */
    private List<String> internalDependencies;

    /**
     * Dependencies with external classes (libraries).
     */
    private List<String> externalDependencies;

    /**
     * Full constructor.
     * 
     * @param className
     *            Class name.
     * @param internalDependencies
     *            Dependencies with internal classes (same project).
     * @param externalDependencies
     *            Dependencies with external classes (libraries).
     */
    public ClassDependencies(String className, List<String> internalDependencies,
	    List<String> externalDependencies) {
	this.className = className;
	this.internalDependencies = internalDependencies;
	this.externalDependencies = externalDependencies;
    }

    /**
     * @return the internalDependencies
     */
    public List<String> getInternalDependencies() {
	return internalDependencies;
    }

    /**
     * @param internalDependencies
     *            the internalDependencies to set
     */
    public void setInternalDependencies(List<String> internalDependencies) {
	this.internalDependencies = internalDependencies;
    }

    /**
     * @return the externalDependencies
     */
    public List<String> getExternalDependencies() {
	return externalDependencies;
    }

    /**
     * @param externalDependencies
     *            the externalDependencies to set
     */
    public void setExternalDependencies(List<String> externalDependencies) {
	this.externalDependencies = externalDependencies;
    }

    /**
     * @return the className
     */
    public String getClassName() {
	return className;
    }

    /**
     * @param className
     *            the className to set
     */
    public void setClassName(String className) {
	this.className = className;
    }

    /**
     * @return the layer
     */
    public String getLayer() {
	return layer;
    }

    /**
     * @param layer
     *            the layer to set
     */
    public void setLayer(String layer) {
	this.layer = layer;
    }

    @Override
    public String toString() {
	return "{Class: " + this.className + " - Internal: " + this.internalDependencies
		+ " - External: " + this.externalDependencies + "}";
    }
}
