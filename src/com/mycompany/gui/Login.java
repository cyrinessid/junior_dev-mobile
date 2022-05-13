package com.mycompany.gui;

import com.mycompany.Ple.MyApplication;
import com.codename1.ui.*;
import com.codename1.ui.layouts.*;

public class Login extends Form {

    public static Form loginForm;

    public Login() {
        super("Connexion", new BoxLayout(BoxLayout.Y_AXIS));
        loginForm = this;
        addGUIs();
    }

    private void addGUIs() {

        
        Button frontendBtn = new Button("Front");
        frontendBtn.addActionListener(l -> new com.mycompany.gui.front.AccueilFront().show());
        this.add(frontendBtn);
        
        
        Button backendBtn = new Button("Back");
        backendBtn.addActionListener(l -> new com.mycompany.gui.back.AccueilBack().show());

        this.add(backendBtn);
    }
    
}
