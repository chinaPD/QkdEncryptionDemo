package component;

/**
 * Created by hadoop on 16-12-21.
 */
public interface TextFieldObserver {
    void textChanged(String text);
    void focusChanged(boolean isInFocus);
    void textSelected(String text);
}
