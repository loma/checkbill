package leftmenu;

import com.openbravo.pos.forms.AppLocal;
import com.openbravo.pos.forms.AppUserView;
import com.openbravo.pos.forms.JPanelMenu;
import com.openbravo.pos.forms.JPrincipalApp;
import com.openbravo.pos.forms.MenuExecAction;
import com.openbravo.pos.forms.MenuPanelAction;
import java.awt.Component;
import javax.swing.Action;
import org.jdesktop.swingx.JXTaskPane;

public class ScriptGroup {

    private final JXTaskPane taskGroup;
    JPrincipalApp app;

    ScriptGroup(String key, JPrincipalApp app) {
        taskGroup = new JXTaskPane();
        taskGroup.applyComponentOrientation(app.getComponentOrientation());
        taskGroup.setFocusable(false);
        taskGroup.setRequestFocusEnabled(false);
        taskGroup.setTitle(AppLocal.getIntString(key));
        taskGroup.setVisible(false);
        this.app = app;
    }

    public void addPanel(String icon, String key, String classname) {
        addAction(new MenuPanelAction(app.getAppView(), icon, key, classname));
    }

    public void addExecution(String icon, String key, String classname) {
        addAction(new MenuExecAction(app.getAppView(), icon, key, classname));
    }

    public ScriptSubmenu addSubmenu(String icon, String key, String classname) {
        ScriptSubmenu submenu = new ScriptSubmenu(key, app);
        final JPanelMenu jPanelMenu = new JPanelMenu(submenu.getMenuDefinition());
        jPanelMenu.setName(key);
        app.addPreparedView(classname, jPanelMenu);
        addAction(new MenuPanelAction(app.getAppView(), icon, key, classname));
        return submenu;
    }

    public void addChangePasswordAction() {
        addAction(new ChangePasswordAction("/com/openbravo/images/password.png", "Menu.ChangePassword", app));
    }

    public void addExitAction() {
        addAction(new ExitAction("/com/openbravo/images/logout.png", "Menu.Exit", app));
    }

    private void addAction(Action act) {

        if (app.getUser().hasPermission((String) act.getValue(AppUserView.ACTION_TASKNAME))) {
            Component c = taskGroup.add(act);
            c.applyComponentOrientation(app.getComponentOrientation());
            c.setFocusable(false);

            taskGroup.setVisible(true);

            app.setFirstAction(act);
        }
    }

    public JXTaskPane getTaskGroup() {
        return taskGroup;
    }
}
