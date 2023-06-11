package client.assertion;

public class AssertionFactoryBuilder {
    private static TestNgAssertionFactory testNgAssertAssertionFactory;

    public static TestNgAssertionFactory buildTestNgAssertionFactory() {
        if (null == testNgAssertAssertionFactory) {
            testNgAssertAssertionFactory = new TestNgAssertionFactory();
        }
        return testNgAssertAssertionFactory;
    }
}
