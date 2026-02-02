package pl.projekt.tennis_ranking.views;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.HighlightConditions;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.spring.security.AuthenticationContext;
import com.vaadin.flow.theme.lumo.LumoUtility;
import org.springframework.security.core.context.SecurityContextHolder;
import pl.projekt.tennis_ranking.service.UserProfileService;

public class MainLayout extends AppLayout {

    public MainLayout(AuthenticationContext authContext, UserProfileService profileService) {
        addClassName("app-shell");

        // ===== NAVBAR =====
        DrawerToggle toggle = new DrawerToggle();

        H1 title = new H1("Tennis Ranking");
        title.addClassNames(LumoUtility.FontSize.LARGE, LumoUtility.Margin.NONE);
        title.addClassName("app-title");

        Button logout = new Button("Wyloguj", e -> authContext.logout());
        logout.addClassName("logout-btn");

        addToNavbar(toggle, title, logout);

        // ===== DRAWER =====
        VerticalLayout menu = new VerticalLayout();
        menu.setPadding(false);
        menu.setSpacing(false);
        menu.addClassName("drawer");
        menu.setWidthFull();

        // cute profile card
        menu.add(profileCard(profileService));

        // sections + links
        menu.add(section("Ogólne"));
        menu.add(navLink("Turnieje", TournamentsView.class));
        menu.add(navLink("Ranking", RankingView.class));
        menu.add(navLink("Mój profil", ProfileView.class));

        menu.add(section("Panele"));

        if (hasRole("ADMIN")) {
            menu.add(navLink("Panel administratora", AdminPanelView.class));
            menu.add(navLink("Sezon / ATP Finals", AdminSeasonView.class));
        }
        if (hasRole("ORGANIZATOR")) {
            menu.add(navLink("Panel organizatora", OrganizerPanelView.class));
        }
        if (hasRole("SEDZIA")) {
            menu.add(navLink("Panel sędziego", JudgePanelView.class));
        }
        if (hasRole("ZAWODNIK")) {
            menu.add(navLink("Panel zawodnika", PlayerPanelView.class));
        }
        if (hasRole("KIBIC")) {
            menu.add(navLink("Panel kibica", FanPanelView.class));
        }

        addToDrawer(menu);
    }

    private Component profileCard(UserProfileService profileService) {
        var p = profileService.getMyProfile();

        Div wrap = new Div();
        wrap.addClassName("profile-box");

        Span title = new Span("Twoje konto ✨");
        title.addClassName("title");
        wrap.add(title);

        if (p == null) {
            Div line = new Div();
            line.addClassName("profile-line");
            line.setText("Brak danych profilu.");
            wrap.add(line);
            return wrap;
        }

        String displayName = (p.displayName() != null && !p.displayName().isBlank()) ? p.displayName() : p.login();
        String role = (p.rola() != null) ? p.rola() : "-";
        String avatar = initials(displayName);

        Div card = new Div();
        card.addClassName("user-card");

        Div av = new Div();
        av.addClassName("user-avatar");
        av.setText(avatar);

        Div info = new Div();
        info.addClassName("user-info");

        Span name = new Span(displayName);
        name.addClassName("user-name");

        Span roleSpan = new Span(role);
        roleSpan.addClassName("user-role");

        info.add(name, roleSpan);

        // mini lines (cute, ale praktyczne)
        Div mini = new Div();
        mini.getStyle().set("margin-top", "10px");

        wrap.add(card);
        card.add(av, info);

        wrap.add(profileLine("Login", p.login()));
        if (p.kraj() != null && !p.kraj().isBlank()) wrap.add(profileLine("Kraj", p.kraj()));
        if (p.punkty() != null) wrap.add(profileLine("Punkty", String.valueOf(p.punkty())));

        return wrap;
    }

    private Div profileLine(String k, String v) {
        Div d = new Div();
        d.addClassName("profile-line");
        Span key = new Span(k + ": ");
        Span val = new Span(v != null ? v : "-");
        val.getElement().getStyle().set("font-weight", "800");
        d.add(key, val);
        return d;
    }

    private Component section(String text) {
        Span s = new Span(text);
        s.addClassName("nav-section");
        return s;
    }

    private RouterLink navLink(String text, Class<? extends Component> target) {
        RouterLink link = new RouterLink(text, target);
        link.addClassName("nav-link");
        link.setHighlightCondition(HighlightConditions.sameLocation());
        return link;
    }

    private boolean hasRole(String role) {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) return false;
        return auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_" + role));
    }

    private String initials(String s) {
        if (s == null) return "?";
        String trimmed = s.trim();
        if (trimmed.isEmpty()) return "?";

        String[] parts = trimmed.split("\\s+");
        String a = parts[0].substring(0, 1).toUpperCase();

        if (parts.length == 1) return a;
        String b = parts[parts.length - 1].substring(0, 1).toUpperCase();
        return a + b;
    }
}
