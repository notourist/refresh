package eu.kvfg.refresh.util;

import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Objects;

/**
 * Holds utility methods used by more than one class.
 *
 * @author Lukas Nasarek
 */
public class Utils {

    /**
     * <b>Copied from JDK 9.</b>
     * Returns the first argument if it is non-{@code null} and
     * otherwise returns the non-{@code null} second argument.
     *
     * @param obj an object
     * @param defaultObj a non-{@code null} object to return if the first argument is {@code null}
     * @param <T> the type of the reference
     * @return the first argument if it is non-{@code null} and otherwise the second argument if it is non-{@code null}
     * @throws NullPointerException if both {@code obj} is null and {@code defaultObj} is {@code null}
     * @since 9
     */
    public static <T> T requireNonNullElse(T obj, T defaultObj) {
        return (obj != null) ? obj : Objects.requireNonNull(defaultObj, "defaultObj");
    }

    public static Object getAuthenticationPrincipal() {
        return SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}