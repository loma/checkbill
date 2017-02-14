package leftmenu;

import com.openbravo.pos.forms.JPanelMenu;
import com.openbravo.pos.forms.JPrincipalApp;
import com.openbravo.pos.forms.MenuDefinition;
import com.openbravo.pos.forms.MenuExecAction;
import com.openbravo.pos.forms.MenuPanelAction;

public class ScriptSubmenu {

    private final MenuDefinition menudef;
    private JPrincipalApp app;

    ScriptSubmenu(String key, JPrincipalApp app) {
        menudef = new MenuDefinition(key);
        this.app = app;
    }

    public void addTitle(String key) {
        menudef.addMenuTitle(key);
    }

    public void addPanel(String icon, String key, String classname) {
        menudef.addMenuItem(new MenuPanelAction(app.getAppView(), icon, key, classname), key);
    }

    public void addExecution(String icon, String key, String classname) {
        menudef.addMenuItem(new MenuExecAction(app.getAppView(), icon, key, classname), key);
    }

    public ScriptSubmenu addSubmenu(String icon, String key, String classname) {
        ScriptSubmenu submenu = new ScriptSubmenu(key, app);
        app.addPreparedView(classname, new JPanelMenu(submenu.getMenuDefinition()));
        menudef.addMenuItem(new MenuPanelAction(app.getAppView(), icon, key, classname), key);
        return submenu;
    }

    public void addChangePasswordAction() {
        menudef.addMenuItem(new ChangePasswordAction("/com/openbravo/images/password.png", "Menu.ChangePassword", app), "menu.change_password");
    }

    public void addExitAction() {
        menudef.addMenuItem(new ExitAction("/com/openbravo/images/logout.png", "Menu.Exit", app), "menu.exit");
    }

    public MenuDefinition getMenuDefinition() {
        return menudef;
    }
}
