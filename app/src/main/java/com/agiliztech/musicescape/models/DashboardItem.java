package com.agiliztech.musicescape.models;

/**
 * Created by praburaam on 05/09/16.
 */
public class DashboardItem {
    private boolean preset;
    private Journey journey;
    private JourneySession session;

    public DashboardItem(boolean preset) {
        this.preset = preset;
    }

    public Journey getJourney() {
        return journey;
    }

    public void setJourney(Journey journey) {
        this.journey = journey;
    }

    public JourneySession getSession() {
        return session;
    }

    public void setSession(JourneySession session) {
        this.session = session;
    }

    public boolean isPreset() {
        return preset;
    }
}
