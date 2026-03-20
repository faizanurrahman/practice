import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * Minimal dynamic proxy: every call is routed through InvocationHandler.
 * Compare profiles with direct interface implementation in hot loops.
 */
public class DynamicProxyDemo {

    interface Greeter {
        String greet(String name);
    }

    public static void main(String[] args) {
        InvocationHandler handler = (proxy, method, methodArgs) -> {
            if (method.getName().equals("greet") && methodArgs != null && methodArgs.length == 1) {
                return "Hello, " + methodArgs[0];
            }
            return method.invoke(proxy, methodArgs);
        };

        Greeter g = (Greeter) Proxy.newProxyInstance(
            Greeter.class.getClassLoader(),
            new Class<?>[] { Greeter.class },
            handler
        );

        System.out.println(g.greet("staff"));
    }
}
