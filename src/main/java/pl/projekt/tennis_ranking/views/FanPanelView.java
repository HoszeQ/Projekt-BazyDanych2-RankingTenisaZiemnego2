package pl.projekt.tennis_ranking.views;

import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;

import jakarta.annotation.security.RolesAllowed;

@Route(value = "panel/kibic", layout = MainLayout.class)

@RolesAllowed("KIBIC")
public class FanPanelView extends VerticalLayout {
    public FanPanelView() { add(new H2("Panel Kibica")); }
}
