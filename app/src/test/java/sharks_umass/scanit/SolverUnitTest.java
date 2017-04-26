package sharks_umass.scanit;

import org.junit.Test;

import sharks_umass.scanit.apis.Solver;

/**
 * Created by lfate on 4/25/2017.
 */

public class SolverUnitTest {
    @Test
    public void testSolver(){

        Solver s = new Solver();
        String answer = s.solve("2x = 4");
        System.out.println(answer);
    }
}
