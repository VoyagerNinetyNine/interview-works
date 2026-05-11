package com.example.aircraftwar.ObserverPattern;

import java.util.LinkedList;
import java.util.List;

public class Publisher {
    private final List<Subscribers> subscribers = new LinkedList<>();

    public void addSubscriber(Subscribers subscriber){
        subscribers.add(subscriber);
    }

    public void addSubscribers(List<? extends Subscribers> subscriber){
        subscribers.addAll(subscriber);
    }

    public void deleteSubscriber(Subscribers subscriber){
        subscribers.remove(subscriber);
    }

    public void notifySubscribers(){
        for(Subscribers e : subscribers){
            e.update();
        }
    }

}
