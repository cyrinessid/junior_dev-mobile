package com.mycompany.services;

import com.codename1.components.InfiniteProgress;
import com.codename1.io.*;
import com.codename1.ui.events.ActionListener;
import com.mycompany.entities.*;
import com.mycompany.utils.*;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ReservationService {

    public static ReservationService instance = null;
    public int resultCode;
    private ConnectionRequest cr;
    private ArrayList<Reservation> listReservations;

    

    private ReservationService() {
        cr = new ConnectionRequest();
    }

    public static ReservationService getInstance() {
        if (instance == null) {
            instance = new ReservationService();
        }
        return instance;
    }
    
    public ArrayList<Reservation> getAll() {
        listReservations = new ArrayList<>();

        cr = new ConnectionRequest();
        cr.setUrl(Statics.BASE_URL + "/mobile/reservation");
        cr.setHttpMethod("GET");

        cr.addResponseListener(new ActionListener<NetworkEvent>() {
            @Override
            public void actionPerformed(NetworkEvent evt) {

                if (cr.getResponseCode() == 200) {
                    listReservations = getList();
                }

                cr.removeResponseListener(this);
            }
        });

        try {
            cr.setDisposeOnCompletion(new InfiniteProgress().showInfiniteBlocking());
            NetworkManager.getInstance().addToQueueAndWait(cr);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return listReservations;
    }

    private ArrayList<Reservation> getList() {
        try {
            Map<String, Object> parsedJson = new JSONParser().parseJSON(new CharArrayReader(
                    new String(cr.getResponseData()).toCharArray()
            ));
            List<Map<String, Object>> list = (List<Map<String, Object>>) parsedJson.get("root");

            for (Map<String, Object> obj : list) {
                Reservation reservation = new Reservation(
                        (int) Float.parseFloat(obj.get("id").toString()),
                        
                        (int) Float.parseFloat(obj.get("nbPlaces").toString()),
                        new SimpleDateFormat("dd-MM-yyyy").parse((String) obj.get("dateAchat")),
                        (String) obj.get("total"),
                        makeEvent((Map<String, Object>) obj.get("event"))
                        
                );

                listReservations.add(reservation);
            }
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
        return listReservations;
    }
    
    public Event makeEvent(Map<String, Object> obj) {
        if (obj == null) {
            return null;
        }
        Event event = new Event();
        event.setId((int) Float.parseFloat(obj.get("id").toString()));
        event.setTitre((String) obj.get("titre"));
        return event;
    }
    
    public int add(Reservation reservation) {
        return manage(reservation, false);
    }

    public int edit(Reservation reservation) {
        return manage(reservation, true );
    }

    public int manage(Reservation reservation, boolean isEdit) {
        
        cr = new ConnectionRequest();

        
        cr.setHttpMethod("POST");
        if (isEdit) {
            cr.setUrl(Statics.BASE_URL + "/mobile/reservation/edit");
            cr.addArgument("id", String.valueOf(reservation.getId()));
        } else {
            cr.setUrl(Statics.BASE_URL + "/mobile/reservation/add");
        }
        
        cr.addArgument("nbPlaces", String.valueOf(reservation.getNbPlaces()));
        cr.addArgument("dateAchat", new SimpleDateFormat("dd-MM-yyyy").format(reservation.getDateAchat()));
        cr.addArgument("total", reservation.getTotal());
        cr.addArgument("event", String.valueOf(reservation.getEvent().getId()));
        
        
        cr.addResponseListener(new ActionListener<NetworkEvent>() {
            @Override
            public void actionPerformed(NetworkEvent evt) {
                resultCode = cr.getResponseCode();
                cr.removeResponseListener(this);
            }
        });
        try {
            cr.setDisposeOnCompletion(new InfiniteProgress().showInfiniteBlocking());
            NetworkManager.getInstance().addToQueueAndWait(cr);
        } catch (Exception ignored) {

        }
        return resultCode;
    }

    public int delete(int reservationId) {
        cr = new ConnectionRequest();
        cr.setUrl(Statics.BASE_URL + "/mobile/reservation/delete");
        cr.setHttpMethod("POST");
        cr.addArgument("id", String.valueOf(reservationId));

        cr.addResponseListener(new ActionListener<NetworkEvent>() {
            @Override
            public void actionPerformed(NetworkEvent evt) {
                cr.removeResponseListener(this);
            }
        });

        try {
            cr.setDisposeOnCompletion(new InfiniteProgress().showInfiniteBlocking());
            NetworkManager.getInstance().addToQueueAndWait(cr);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return cr.getResponseCode();
    }
}
