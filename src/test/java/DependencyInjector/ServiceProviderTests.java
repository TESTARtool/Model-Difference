package DependencyInjector;

import DependencyInjection.ServiceProviderBuilder;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


public class ServiceProviderTests {

    @Test
    public void getServiceThrowsExceptionWhenTypeIsNotRegistered(){
        var sut = new ServiceProviderBuilder()
                .BuildServiceProvider();

        var exception = assertThrows(IllegalStateException.class, () -> {
            sut.getService(String.class);
        });

        var expected = "No service for type 'java.lang.String' registered.";
        var actual  = exception.getMessage();
        assertEquals(expected, actual);
    }

    @Test
    public void getServiceCallsFirstConstructorBasedOnAmountOfParameters() throws Exception{
        var sut = new ServiceProviderBuilder()
                .addSingleton(ITestInterface.class, TestClass.class)
                .addSingleton(ISecondTestInterface.class, SecondTestClass.class)
                .BuildServiceProvider();

        var service = (TestClass) sut.getService(ITestInterface.class);

        // first constructor is empty
        assertTrue(service.firstConstructor);
    }

    @Test
    public void whenUnableToCreateInstanceTheFailedServiceIsNamedInTheException(){
        var sut2 = new ServiceProviderBuilder()
                .addSingleton(ISecondTestInterface.class, SecondTestClass.class)
                .BuildServiceProvider();

        var exception = assertThrows(IllegalStateException.class, () -> {
            sut2.getService(ISecondTestInterface.class);
        });

        var expected = "Unable to resolve service for type 'DependencyInjector.ITestInterface' while attempting to activate 'DependencyInjector.ISecondTestInterface'.";
        var actual = exception.getMessage();

        assertEquals(expected, actual);
    }
}
