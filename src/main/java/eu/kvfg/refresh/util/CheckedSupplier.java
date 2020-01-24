package eu.kvfg.refresh.util;

/**
 * Represents a supplier of results which can throw exceptions.
 *
 * @author Lukas Nasarek
 * @see java.util.function.Supplier
 */
public interface CheckedSupplier<T> {

    T get() throws Exception;
}
