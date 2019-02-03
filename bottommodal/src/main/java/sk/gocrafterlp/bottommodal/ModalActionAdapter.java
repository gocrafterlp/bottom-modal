package sk.gocrafterlp.bottommodal;

public interface ModalActionAdapter {

    enum SwipeState { RETURN, DISMISS }

    default void onShow(ModalNavigationDrawer drawer) {}
    default void onHide(ModalNavigationDrawer drawer) {}
    default void onUserDismiss(ModalNavigationDrawer drawer) {}
    default void onSwipeStateChange(ModalNavigationDrawer drawer, SwipeState state) {}
    default void onParametersUpdate(ModalNavigationDrawer drawer, ModalDrawerParameters params) {}
}