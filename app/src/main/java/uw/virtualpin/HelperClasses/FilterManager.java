package uw.virtualpin.HelperClasses;

import java.util.ArrayList;
import java.util.List;

import uw.virtualpin.Data.Pin;

/**
 * Created by tyler on 1/24/2017.
 */

public class FilterManager {
    private List<Pin> pins;

    public FilterManager(List<Pin> pins) {
        this.pins = pins;
    }

    public List<Pin> filterByMessage(String message) {
        List<Pin> filteredPins = new ArrayList<>();

        for(Pin pin : pins) {
            if(isSimilar(pin.getMessage(), message)) {
                filteredPins.add(pin);
            }
        }

        return filteredPins;
    }

    public List<Pin> filterByUsername(String username) {
        List<Pin> filteredPins = new ArrayList<>();

        for(Pin pin : pins) {
            if(isSimilar(pin.getUserName(), username)) {
                filteredPins.add(pin);
            }
        }

        return filteredPins;
    }

    public List<Pin> filter(String word) {
        List<Pin> filteredPins = new ArrayList<>();

        for(Pin pin : pins) {
            if(isSimilar(pin.getUserName(), word) ||
                    isSimilar(pin.getMessage(), word)) {
                filteredPins.add(pin);
            }
        }

        return filteredPins;
    }

    public List<Pin> filterByUpvotesGreaterThanOrEqual(int upvotes) {
        List<Pin> filteredPins = new ArrayList<>();

        for(Pin pin : pins) {
            if(pin.getUpvotes() >= upvotes) {
                filteredPins.add(pin);
            }
        }

        return filteredPins;
    }

    public List<Pin> filterByUpvotesLessThanOrEqual(int upvotes) {
        List<Pin> filteredPins = new ArrayList<>();

        for(Pin pin : pins) {
            if(pin.getUpvotes() <= upvotes) {
                filteredPins.add(pin);
            }
        }

        return filteredPins;
    }

    public List<Pin> filterByDownvotesGreaterThanOrEqual(int downvotes) {
        List<Pin> filteredPins = new ArrayList<>();

        for(Pin pin : pins) {
            if(pin.getDownvotes() >= downvotes) {
                filteredPins.add(pin);
            }
        }

        return filteredPins;
    }

    public List<Pin> filterByDownvotesLessThanOrEqual(int downvotes) {
        List<Pin> filteredPins = new ArrayList<>();

        for(Pin pin : pins) {
            if(pin.getDownvotes() <= downvotes) {
                filteredPins.add(pin);
            }
        }

        return filteredPins;
    }

    public List<Pin> filterByScoreGreaterThanOrEqual(int score) {
        List<Pin> filteredPins = new ArrayList<>();

        for(Pin pin : pins) {
            if(pin.getScore() >= score) {
                filteredPins.add(pin);
            }
        }

        return filteredPins;
    }

    public List<Pin> filterByScoreLessThanOrEqual(int score) {
        List<Pin> filteredPins = new ArrayList<>();

        for(Pin pin : pins) {
            if(pin.getScore() <= score) {
                filteredPins.add(pin);
            }
        }

        return filteredPins;
    }

    public List<Pin> filterByViewsGreaterThanOrEqual(int views) {
        List<Pin> filteredPins = new ArrayList<>();

        for(Pin pin : pins) {
            if(pin.getViews() >= views) {
                filteredPins.add(pin);
            }
        }

        return filteredPins;
    }

    public List<Pin> filterByViewsLessThanOrEqual(int views) {
        List<Pin> filteredPins = new ArrayList<>();

        for(Pin pin : pins) {
            if(pin.getViews() <= views) {
                filteredPins.add(pin);
            }
        }

        return filteredPins;
    }

    private boolean isSimilar(String string1, String string2) {
        String newString1 = string1.toLowerCase();
        String newString2 = string2.toLowerCase();
        return newString1.contains(newString2);
    }

}
