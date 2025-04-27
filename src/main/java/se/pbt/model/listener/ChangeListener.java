package se.pbt.model.listener;

/**
 * Generic listener interface for observing model changes.
 *
 * @param <T> the type of model that changed
 */
public interface ChangeListener<T> {
    void onChanged(T source);
}
