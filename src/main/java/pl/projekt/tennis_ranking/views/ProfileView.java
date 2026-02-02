package pl.projekt.tennis_ranking.views;

import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility;
import jakarta.annotation.security.PermitAll;
import pl.projekt.tennis_ranking.service.UserProfileService;

@Route(value = "profil", layout = MainLayout.class)
@PermitAll
public class ProfileView extends VerticalLayout {

    public ProfileView(UserProfileService profileService) {
        setWidthFull();
        addClassNames(LumoUtility.Padding.LARGE);

        add(new H2("Mój profil"));

        var p = profileService.getMyProfile();
        if (p == null) {
            add(new Paragraph("Brak danych profilu."));
            return;
        }

        add(new Paragraph("Login: " + p.login()));
        add(new Paragraph("Rola: " + p.rola()));
        add(new Paragraph("Użytkownik: " + p.displayName()));

        if (p.email() != null) add(new Paragraph("Email: " + p.email()));
        if (p.kraj() != null) add(new Paragraph("Kraj: " + p.kraj()));
        if (p.punkty() != null) add(new Paragraph("Punkty: " + p.punkty()));
    }
}
