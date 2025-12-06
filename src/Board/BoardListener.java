package Board;

import javax.media.opengl.GLEventListener;
import java.awt.event.KeyListener;
import java.awt.event.MouseListener;

public abstract class BoardListener implements GLEventListener , KeyListener , MouseListener {
//    protected String assets = "Assets";
    protected String background = "Assets//Background";
    protected String D1 = "Assets//Players//D1";
    protected String D2 = "Assets//Players//D2";
    protected String D3 = "Assets//Players//D3";
    protected String D4 = "Assets//Players//D4";
    protected String D5 = "Assets//Players//D5";
    protected String D6 = "Assets//Players//D6";
    protected String D7 = "Assets//Players//D7";
    protected String D8 = "Assets//Players//D8";
}
