package systeme;

import ui.Interface2D;

import javax.swing.*;

public class SystemePrincipal {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Interface2D());
    }
}
