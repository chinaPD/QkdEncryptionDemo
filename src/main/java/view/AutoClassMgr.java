package view;

import java.util.HashSet;

/**
 * Created by hadoop on 16-12-22.
 */
public class AutoClassMgr {

    private static final HashSet<AutoClose> mAutoCloseSet = new HashSet<>(5);

    public static void registerAutoClose(AutoClose autoClose) {
        mAutoCloseSet.add(autoClose);
    }

    public static void unregisterAutoClose(AutoClose autoClose) {
        mAutoCloseSet.remove(autoClose);
    }

    public static void doAllAutoClose() {
        for (AutoClose autoClose : mAutoCloseSet) {
            autoClose.autoClose();
        }
    }
}
