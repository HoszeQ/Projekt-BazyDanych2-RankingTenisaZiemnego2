package pl.projekt.tennis_ranking.views;

import org.springframework.security.core.context.SecurityContextHolder;

import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.Route;

import jakarta.annotation.security.PermitAll;

@Route("")
@PermitAll
public class DashboardView extends VerticalLayout implements BeforeEnterObserver {

    public DashboardView() {
        add(new H2("Przekierowuję do panelu..."));
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getAuthorities() == null) return;

        boolean isAdmin = auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        boolean isOrg   = auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ORGANIZATOR"));
        boolean isJudge = auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_SEDZIA"));
        boolean isPlayer= auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ZAWODNIK"));

        if (isAdmin) event.forwardTo("panel/admin");
        else if (isOrg) event.forwardTo("panel/organizator");
        else if (isJudge) event.forwardTo("panel/sedzia");
        else if (isPlayer) event.forwardTo("panel/zawodnik");
        else event.forwardTo("panel/kibic");
    }
}
