package mx.itesm.arch.training;

import java.io.File;

import junit.framework.TestCase;

/**
 * 
 * @author jccastrejon
 * 
 */
public class TrainingUtilTest extends TestCase {

    public void testGenerateTrainingSet() throws Exception {
        TrainingUtil.generateTrainingSet(new File("/Users/jccastrejon/Desktop/softwareRecovery/web/training/apps/roo"));
    }
}
