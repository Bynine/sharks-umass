package sharks_umass.scanit;

import org.junit.Test;

import sharks_umass.scanit.apis.Definer;
import sharks_umass.scanit.apis.DefinerResult;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;


public class DefinerUnitTest {
    @Test
    public void testDefiner() {
        Definer d = new Definer();
        DefinerResult res = d.define("winning");
        assertNotNull(res);
        assertNotNull(res.getWord());
        assertNotNull(res.getDefinition());
        assertNotNull(res.getExample());
        System.out.println(res.getWord());
        System.out.println(res.getDefinition());
        System.out.println(res.getExample());
    }
}
