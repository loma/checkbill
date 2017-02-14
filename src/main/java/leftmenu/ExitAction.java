/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package leftmenu;

import com.openbravo.pos.forms.AppLocal;
import com.openbravo.pos.forms.AppUserView;
import com.openbravo.pos.forms.JPrincipalApp;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;

class ExitAction extends AbstractAction {

    private final JPrincipalApp app;

    public ExitAction(String icon, String keytext, JPrincipalApp app) {
        putValue(Action.SMALL_ICON, new ImageIcon(JPrincipalApp.class.getResource(icon)));
        putValue(Action.NAME, AppLocal.getIntString(keytext));
        putValue(AppUserView.ACTION_TASKNAME, keytext);
        this.app = app;
    }

    @Override
    public void actionPerformed(ActionEvent evt) {
        app.getAppView().closeAppView();
    }
}
