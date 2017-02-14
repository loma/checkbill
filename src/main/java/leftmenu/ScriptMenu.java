package leftmenu;

import com.openbravo.pos.forms.JPrincipalApp;
import org.jdesktop.swingx.JXTaskPaneContainer;

public class ScriptMenu {

    private final JXTaskPaneContainer taskPane;
    JPrincipalApp app;

    public ScriptMenu(JPrincipalApp app) {
        taskPane = new JXTaskPaneContainer();
        taskPane.applyComponentOrientation(app.getComponentOrientation());
        this.app = app;
    }

    public ScriptGroup addGroup(String key) {
        ScriptGroup group = new ScriptGroup(key, app);
        taskPane.add(group.getTaskGroup());
        return group;
    }

    public JXTaskPaneContainer getTaskPane() {
        return taskPane;
    }
}
