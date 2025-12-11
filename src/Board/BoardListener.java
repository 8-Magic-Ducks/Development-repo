package Board;

import javax.media.opengl.GLEventListener;
import java.awt.event.KeyListener;
import java.awt.event.MouseListener;

public abstract class BoardListener implements GLEventListener , KeyListener , MouseListener {
    protected String background = "src//Assets//Background//";
    protected String D1 = "src//Assets//Players//D1//";
    protected String D2 = "src//Assets//Players//D2//";
    protected String D3 = "src//Assets//Players//D3//";
    protected String D4 = "src//Assets//Players//D6//";
    protected String D5 = "src//Assets//Players//D7//";
    protected String D6 = "src//Assets//Players//D6//";
    protected String D7 = "src//Assets//Players//D7//";
    protected String D8 = "src//Assets//Players//D8//";
    protected String Nums = "src//Assets//Numbers//";

}