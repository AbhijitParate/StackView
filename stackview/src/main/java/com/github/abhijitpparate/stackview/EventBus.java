package com.github.abhijitpparate.stackview;

import rx.subjects.PublishSubject;

public class EventBus {

    private static final PublishSubject<StackView.CardSwipedEvent> EVENT_BUS = PublishSubject.create();

    static PublishSubject<StackView.CardSwipedEvent> getEventBus() {
        return EVENT_BUS;
    }
}
