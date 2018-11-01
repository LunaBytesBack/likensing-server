import com.googlecode.junittoolbox.SuiteClasses;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import sirius.kernel.ScenarioSuite;
import sirius.kernel.TestHelper;

@RunWith(ScenarioSuite.class)
@SuiteClasses({"**/*Test.class", "**/*Spec.class"})
public class TestSuite {

    @BeforeClass
    public static void setUp() {
        TestHelper.setUp(TestSuite.class);
    }

    @AfterClass
    public static void tearDown() {
        TestHelper.tearDown(TestSuite.class);
    }
}
