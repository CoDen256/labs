package editor.events;

import java.util.ArrayList;
import java.util.List;

public class EventManager {

    List<Subscriber> subscriberList = new ArrayList<>();

    public void notifySubscribers(EditorEvent event) {
        subscriberList.forEach((s) -> s.update(event));
    }

    public void subscribe(Subscriber subscriber) {
        subscriberList.add(subscriber);
    }


}
