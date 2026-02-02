package pl.projekt.tennis_ranking.views;

import java.io.ByteArrayInputStream;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.theme.lumo.LumoUtility;

import jakarta.annotation.security.PermitAll;
import org.springframework.security.core.context.SecurityContextHolder;
import pl.projekt.tennis_ranking.model.Mecz;
import pl.projekt.tennis_ranking.model.Turniej;
import pl.projekt.tennis_ranking.model.TurniejStatus;
import pl.projekt.tennis_ranking.repo.KontoRepository;
import pl.projekt.tennis_ranking.repo.ZawodnikRepository;
import pl.projekt.tennis_ranking.service.TournamentReportService;
import pl.projekt.tennis_ranking.service.TournamentService;

@Route(value = "turniej/:id", layout = MainLayout.class)
@PermitAll
public class TournamentDetailsView extends VerticalLayout implements BeforeEnterObserver {

    private final TournamentService tournamentService;
    private final TournamentReportService reportService;
    private final ZawodnikRepository zawodnikRepo;
    private final KontoRepository kontoRepo;

    public TournamentDetailsView(
            TournamentService tournamentService,
            TournamentReportService reportService,
            ZawodnikRepository zawodnikRepo,
            KontoRepository kontoRepo
    ) {
        this.tournamentService = tournamentService;
        this.reportService = reportService;
        this.zawodnikRepo = zawodnikRepo;
        this.kontoRepo = kontoRepo;

        setSizeFull();
        addClassNames(LumoUtility.Padding.LARGE);
        addClassName("page");
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        removeAll();

        String idTurnieju = event.getRouteParameters().get("id").orElse(null);
        if (idTurnieju == null) {
            add(new H2("Brak ID turnieju"));
            return;
        }

        Turniej t = tournamentService.pobierzTurniej(idTurnieju);

        // ===== header =====
        Div header = card();
        header.add(new H2(t.getNazwa()));
        header.add(new H4("Ranga: " + t.getRanga() + " | Sezon: " + t.getSezon() + " | Status: " + t.getStatus()));

        // raport tylko dla ADMIN/ORGANIZATOR
        if (hasRole("ADMIN") || hasRole("ORGANIZATOR")) {
            StreamResource res = new StreamResource(
                    "raport-" + safeFileName(t.getNazwa()) + "-" + idTurnieju + ".xlsx",
                    () -> new ByteArrayInputStream(reportService.buildXlsx(idTurnieju))
            );
            Anchor download = new Anchor(res, "Pobierz raport XLSX");
            download.getElement().setAttribute("download", true);
            download.getStyle().set("display", "inline-block");
            download.getStyle().set("margin-top", "10px");
            header.add(download);
        }

        add(header);

        // ===== jeśli OTWARTE_ZAPISY -> pokaz info + zapis, BEZ DRABINKI =====
        if (t.getStatus() == TurniejStatus.OTWARTE_ZAPISY) {
            add(buildOpenRegistrationCard(t));
            return;
        }

        // ===== inaczej normalnie: drabinka =====
        List<Mecz> mecze = tournamentService.pobierzDrabinke(idTurnieju);

        Div bracketCard = card();
        bracketCard.add(new H2("Drabinka"));

        if (mecze.isEmpty()) {
            Div info = new Div();
            info.setText("Brak wygenerowanej drabinki.");
            bracketCard.add(info);
            add(bracketCard);
            return;
        }

        bracketCard.add(buildBracket(mecze));
        add(bracketCard);
    }

    private Div buildOpenRegistrationCard(Turniej t) {
        Div card = card();
        card.add(new H2("Zapisy otwarte"));

        long zg = tournamentService.liczbaZgloszen(t.getIdTurnieju());

        Span info = new Span("Aktualnie zgłoszeń: " + zg + " / " + t.getMaxZawodnikow());
        info.getStyle().set("font-weight", "600");
        card.add(info);

        // dla niezalogowanych / bez roli zawodnika -> tylko informacja
        if (!hasRole("ZAWODNIK")) {
            Div hint = new Div();
            hint.setText("Zaloguj się jako zawodnik, aby się zapisać.");
            hint.getStyle().set("margin-top", "10px");
            card.add(hint);
            return card;
        }

        String idZawodnika = resolveCurrentZawodnikIdOrNull();
        if (idZawodnika == null) {
            Notification.show("Nie znaleziono profilu zawodnika dla tego konta.", 3500, Notification.Position.MIDDLE);
            return card;
        }

        boolean zapisany = tournamentService.czyZawodnikZapisany(t.getIdTurnieju(), idZawodnika);

        HorizontalLayout actions = new HorizontalLayout();
        actions.getStyle().set("margin-top", "12px");

        if (zapisany) {
            Span ok = new Span("Jesteś już zapisany na ten turniej ✅");
            ok.getStyle().set("font-weight", "700");
            ok.getStyle().set("color", "var(--lumo-success-text-color)");
            actions.add(ok);
            card.add(actions);
            return card;
        }

        Button zapisz = new Button("Zapisz się");
        zapisz.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        zapisz.addClickListener(e -> {
            try {
                tournamentService.zapiszZawodnika(t.getIdTurnieju(), idZawodnika);
                Notification.show("Zapisano na turniej ✅", 2500, Notification.Position.TOP_CENTER);

                // odśwież widok po zapisie
                getUI().ifPresent(ui -> ui.navigate("turniej/" + t.getIdTurnieju()));
            } catch (Exception ex) {
                Notification.show("Błąd: " + ex.getMessage(), 4500, Notification.Position.MIDDLE);
            }
        });

        actions.add(zapisz);
        card.add(actions);

        return card;
    }

    private String resolveCurrentZawodnikIdOrNull() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getName() == null) return null;

        String login = auth.getName();
        String idKonta = kontoRepo.findByLogin(login).map(k -> k.getIdKonta()).orElse(null);
        if (idKonta == null) return null;

        return zawodnikRepo.findByIdKonta(idKonta).map(z -> z.getIdZawodnika()).orElse(null);
    }

    private Div buildBracket(List<Mecz> mecze) {
        Set<String> ids = new HashSet<>();
        for (Mecz m : mecze) {
            if (m.getIdZawodnikA() != null) ids.add(m.getIdZawodnikA());
            if (m.getIdZawodnikB() != null) ids.add(m.getIdZawodnikB());
            if (m.getIdZwyciezcy() != null) ids.add(m.getIdZwyciezcy());
        }

        Map<String, String> name = new HashMap<>();
        zawodnikRepo.findAllById(ids).forEach(z ->
                name.put(z.getIdZawodnika(), z.getImie() + " " + z.getNazwisko())
        );

        Map<Integer, List<Mecz>> byRound = mecze.stream()
                .collect(Collectors.groupingBy(Mecz::getRunda));

        int maxRound = byRound.keySet().stream().max(Integer::compareTo).orElse(1);

        int matchesR1 = byRound.getOrDefault(1, List.of()).size();
        int bracketSize = Math.max(2, matchesR1 * 2);

        Div outer = new Div();
        outer.addClassName("bracket");

        Div row = new Div();
        row.addClassName("bracket-row");

        int cardH = 150;
        int gap = 18;
        int colW = 340;
        int lineW = 18;

        outer.getStyle().set("--card-h", cardH + "px");
        outer.getStyle().set("--gap", gap + "px");
        outer.getStyle().set("--col-w", colW + "px");
        outer.getStyle().set("--line-w", lineW + "px");
        outer.getStyle().set("--rounds", String.valueOf(maxRound));
        outer.getStyle().set("--bracket-size", String.valueOf(bracketSize));

        for (int r = 1; r <= maxRound; r++) {
            List<Mecz> list = byRound.getOrDefault(r, List.of()).stream()
                    .sorted(Comparator.comparingInt(Mecz::getSlotWRundzie))
                    .toList();

            Div col = new Div();
            col.addClassName("bracket-col");

            Div title = new Div();
            title.addClassName("bracket-title");
            title.setText("Runda " + r);
            col.add(title);

            Div layer = new Div();
            layer.addClassName("bracket-layer");

            int step = (cardH + gap) * (1 << (r - 1));
            int topOffset = ((step - (cardH + gap)) / 2);

            int totalHeight = (bracketSize / 2) * (cardH + gap);
            layer.getStyle().set("height", totalHeight + "px");

            for (Mecz m : list) {
                int slotIndex = m.getSlotWRundzie() - 1;
                int top = topOffset + slotIndex * step;

                Div match = new Div();
                match.addClassName("match");
                match.getStyle().set("top", top + "px");

                if (r < maxRound) {
                    match.addClassName("has-next");
                    boolean goesToA = (m.getSlotWRundzie() % 2 == 1);
                    match.addClassName(goesToA ? "go-a" : "go-b");
                }

                String a = pretty(name, m.getIdZawodnikA(), m.getSeedA());
                String b = pretty(name, m.getIdZawodnikB(), m.getSeedB());
                String status = m.getStatus() != null ? m.getStatus().name() : "-";
                String wynik = m.getWynik() != null ? m.getWynik() : "-";
                String winner = m.getIdZwyciezcy() != null
                        ? name.getOrDefault(m.getIdZwyciezcy(), m.getIdZwyciezcy())
                        : "-";

                Div l1 = new Div();
                l1.addClassName("match-title");
                l1.setText("Mecz #" + m.getSlotWRundzie());
                l1.getElement().setProperty("title", "Mecz #" + m.getSlotWRundzie());

                Div l2 = new Div();
                l2.addClassName("match-players");
                String vs = a + " vs " + b;
                l2.setText(vs);
                l2.addClassName("has-tooltip");
                l2.getElement().setProperty("title", vs);

                Div l3 = new Div();
                l3.addClassName("match-meta");
                String meta = "Status: " + status + " | Wynik: " + wynik;
                l3.setText(meta);
                l3.addClassName("has-tooltip");
                l3.getElement().setProperty("title", meta);

                Div l4 = new Div();
                l4.addClassName("match-meta");
                String win = "Zwycięzca: " + winner;
                l4.setText(win);
                l4.addClassName("has-tooltip");
                l4.getElement().setProperty("title", win);

                match.add(l1, l2, l3, l4);
                layer.add(match);
            }

            col.add(layer);
            row.add(col);
        }

        outer.add(row);
        return outer;
    }

    private String pretty(Map<String, String> name, String id, Integer seed) {
        if (id == null) return "BYE";
        String base = name.getOrDefault(id, id);
        if (seed != null) return "#" + seed + " " + base;
        return base;
    }

    private Div card() {
        Div card = new Div();
        card.addClassNames("card", LumoUtility.Margin.Bottom.LARGE);
        card.setWidth("min(1100px, 100%)");
        return card;
    }

    private boolean hasRole(String role) {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) return false;
        return auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_" + role));
    }

    private String safeFileName(String s) {
        if (s == null || s.isBlank()) return "turniej";
        return s.replaceAll("[^a-zA-Z0-9\\-_]+", "_");
    }
}
