package com.mycompany.gui.back.event;

import com.codename1.components.*;
import com.codename1.ui.*;
import com.codename1.ui.layouts.*;
import com.codename1.ui.plaf.UIManager;
import com.codename1.ui.util.*;
import com.codename1.io.*;
import com.mycompany.entities.Event;
import com.mycompany.services.EventService;
import com.mycompany.utils.Statics;

import java.text.SimpleDateFormat;
import java.io.*;
import java.util.*;

public class ShowAll extends Form {

    Form previous; 
    
    Resources theme = UIManager.initFirstTheme("/theme");
    
    public static Event currentEvent = null;
    Button addBtn;

    TextField searchTF;
    ArrayList<Component> componentModels;
    

    public ShowAll(Form previous) {
        super("Events", new BoxLayout(BoxLayout.Y_AXIS));
        this.previous = previous;

        addGUIs();
        addActions();

        super.getToolbar().addMaterialCommandToLeftBar("  ", FontImage.MATERIAL_ARROW_BACK, e -> previous.showBack());
    }

    public void refresh() {
        this.removeAll();
        addGUIs();
        addActions();
        this.refreshTheme();
    }

    private void addGUIs() {
        addBtn = new Button("Ajouter");
        addBtn.setUIID("buttonWhiteCenter");
        this.add(addBtn);
        

        ArrayList<Event> listEvents = EventService.getInstance().getAll();
        componentModels = new ArrayList<>();
        
        searchTF = new TextField("", "Chercher event par Titre");
        searchTF.addDataChangedListener((d, t) -> {
            if (componentModels.size() > 0) {
                for (Component componentModel : componentModels) {
                    this.removeComponent(componentModel);
                }
            }
            componentModels = new ArrayList<>();
            for (Event event : listEvents) {
                if (event.getTitre().toLowerCase().startsWith(searchTF.getText().toLowerCase())) {
                    Component model = makeEventModel(event);
                    this.add(model);
                    componentModels.add(model);
                }
            }
            this.revalidate();
        });
        this.add(searchTF);
        
        
        if (listEvents.size() > 0) {
            for (Event event : listEvents) {
                Component model = makeEventModel(event);
                this.add(model);
                componentModels.add(model);
            }
        } else {
            this.add(new Label("Aucune donn??e"));
        }
    }
    private void addActions() {
        addBtn.addActionListener(action -> {
            currentEvent = null;
            new Manage(this).show();
        });
        
    }
    Label dateLabel   , titreLabel   , prixLabel   , imageLabel   , descriptionLabel   , nbPlacesLabel  ;
    
    ImageViewer imageIV;
    

    private Container makeModelWithoutButtons(Event event) {
        Container eventModel = new Container(new BoxLayout(BoxLayout.Y_AXIS));
        eventModel.setUIID("containerRounded");
        
        
        dateLabel = new Label("Date : " + new SimpleDateFormat("dd-MM-yyyy").format(event.getDate()));
        dateLabel.setUIID("labelDefault");
        
        titreLabel = new Label("Titre : " + event.getTitre());
        titreLabel.setUIID("labelDefault");
        
        prixLabel = new Label("Prix : " + event.getPrix());
        prixLabel.setUIID("labelDefault");
        
        imageLabel = new Label("Image : " + event.getImage());
        imageLabel.setUIID("labelDefault");
        
        descriptionLabel = new Label("Description : " + event.getDescription());
        descriptionLabel.setUIID("labelDefault");
        
        nbPlacesLabel = new Label("NbPlaces : " + event.getNbPlaces());
        nbPlacesLabel.setUIID("labelDefault");
        
        if (event.getImage() != null) {
            String url = Statics.EVENT_IMAGE_URL + event.getImage();
            Image image = URLImage.createToStorage(
                    EncodedImage.createFromImage(theme.getImage("profile-pic.jpg").fill(1100, 500), false),
                    url,
                    url,
                    URLImage.RESIZE_SCALE
            );
            imageIV = new ImageViewer(image);
        } else {
            imageIV = new ImageViewer(theme.getImage("profile-pic.jpg").fill(1100, 500));
        }
        imageIV.setFocusable(false);

        eventModel.addAll(
                imageIV,
                dateLabel, titreLabel, prixLabel, descriptionLabel, nbPlacesLabel
        );

        return eventModel;
    }
    
    Button editBtn, deleteBtn;
    Container btnsContainer;

    private Component makeEventModel(Event event) {

        Container eventModel = makeModelWithoutButtons(event);

        btnsContainer = new Container(new BorderLayout());
        btnsContainer.setUIID("containerButtons");
        
        editBtn = new Button("Modifier");
        editBtn.setUIID("buttonWhiteCenter");
        editBtn.addActionListener(action -> {
            currentEvent = event;
            new Manage(this).show();
        });

        deleteBtn = new Button("Supprimer");
        deleteBtn.setUIID("buttonWhiteCenter");
        deleteBtn.addActionListener(action -> {
            InteractionDialog dlg = new InteractionDialog("Confirmer la suppression");
            dlg.setLayout(new BorderLayout());
            dlg.add(BorderLayout.CENTER, new Label("Voulez vous vraiment supprimer ce event ?"));
            Button btnClose = new Button("Annuler");
            btnClose.addActionListener((ee) -> dlg.dispose());
            Button btnConfirm = new Button("Confirmer");
            btnConfirm.addActionListener(actionConf -> {
                int responseCode = EventService.getInstance().delete(event.getId());

                if (responseCode == 200) {
                    currentEvent = null;
                    dlg.dispose();
                    eventModel.remove();
                    this.refreshTheme();
                } else {
                    Dialog.show("Erreur", "Erreur de suppression du event. Code d'erreur : " + responseCode, new Command("Ok"));
                }
            });
            Container btnContainer = new Container(new BoxLayout(BoxLayout.X_AXIS));
            btnContainer.addAll(btnClose, btnConfirm);
            dlg.addComponent(BorderLayout.SOUTH, btnContainer);
            dlg.show(800, 800, 0, 0);
        });

        btnsContainer.add(BorderLayout.WEST, editBtn);
        btnsContainer.add(BorderLayout.EAST, deleteBtn);
        
        
        eventModel.add(btnsContainer);

        return eventModel;
    }
    
}