package mx.itesm.arch.dependencies;

import java.util.List;

/**
 * @author jccastrejon
 * 
 */
public class ClassDependencies {

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
     * @param internalDependencies
     *            Dependencies with internal classes (same project).
     * @param externalDependencies
     *            Dependencies with external classes (libraries).
     */
    public ClassDependencies(List<String> internalDependencies, List<String> externalDependencies) {
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

    @Override
    public String toString() {
	return "{Internal: " + internalDependencies + "\nExternal: " + externalDependencies + "}";
    }
}
