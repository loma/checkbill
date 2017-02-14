package leftmenu;

import com.openbravo.basic.BasicException;
import com.openbravo.data.gui.JMessageDialog;
import com.openbravo.data.gui.MessageInf;
import com.openbravo.pos.forms.AppLocal;
import com.openbravo.pos.forms.AppUserView;
import com.openbravo.pos.forms.JPrincipalApp;
import com.openbravo.pos.util.Hashcypher;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;

class ChangePasswordAction extends AbstractAction {

    JPrincipalApp app;

    public ChangePasswordAction(String icon, String keytext, JPrincipalApp app) {
        putValue(Action.SMALL_ICON, new ImageIcon(JPrincipalApp.class.getResource(icon)));
        putValue(Action.NAME, AppLocal.getIntString(keytext));
        System.out.println(AppLocal.getIntString(keytext));
        putValue(AppUserView.ACTION_TASKNAME, keytext);
        this.app = app;
    }

    @Override
    public void actionPerformed(ActionEvent evt) {
        String sNewPassword;
        sNewPassword = Hashcypher.changePassword(app, app.getUser().getPassword());
        if (sNewPassword != null) {
            try {
                app.getDataLogicSystem().execChangePassword(new Object[]{sNewPassword, app.getUser().getId()});
                app.getUser().setPassword(sNewPassword);
            } catch (BasicException e) {
                JMessageDialog.showMessage(app, new MessageInf(MessageInf.SGN_WARNING, AppLocal.getIntString("message.cannotchangepassword")));
            }
        }
    }

}
