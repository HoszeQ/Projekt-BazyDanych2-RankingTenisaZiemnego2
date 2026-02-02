package pl.projekt.tennis_ranking.views;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.security.core.context.SecurityContextHolder;

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility;

import jakarta.annotation.security.RolesAllowed;
import pl.projekt.tennis_ranking.model.PunktyTurniejowe;
import pl.projekt.tennis_ranking.model.Turniej;
import pl.projekt.tennis_ranking.model.TurniejStatus;
import pl.projekt.tennis_ranking.model.ZgloszenieTurniejowe;
import pl.projekt.tennis_ranking.repo.KontoRepository;
import pl.projekt.tennis_ranking.repo.PunktyTurniejoweRepository;
import pl.projekt.tennis_ranking.repo.TurniejRepository;
import pl.projekt.tennis_ranking.repo.ZawodnikRepository;
import pl.projekt.tennis_ranking.service.TournamentService;

@Route(value = "panel/zawodnik", layout = MainLayout.class)
@RolesAllowed("ZAWODNIK")
public class PlayerPanelView extends VerticalLayout {

    private final Grid<ZgloszenieTurniejowe> myGrid = new Grid<>(ZgloszenieTurniejowe.class, false);
    private final Grid<PunktyRow> pointsGrid = new Grid<>(PunktyRow.class, false);

    public PlayerPanelView(
            KontoRepository kontoRepo,
            ZawodnikRepository zawodnikRepo,
            TournamentService tournamentService,
            TurniejRepository turniejRepo,
            PunktyTurniejoweRepository punktyRepo
    ) {
        setSizeFull();
        addClassNames(LumoUtility.Padding.LARGE);
        addClassName("page");

        add(new H2("Panel Zawodnika"));

        String login = SecurityContextHolder.getContext().getAuthentication().getName();
        String idKonta = kontoRepo.findByLogin(login)
                .orElseThrow(() -> new IllegalStateException("Brak konta dla loginu: " + login))
                .getIdKonta();

        String idZawodnika = zawodnikRepo.findByIdKonta(idKonta)
                .orElseThrow(() -> new IllegalStateException("Brak profilu zawodnika dla konta: " + idKonta))
                .getIdZawodnika();

        // map turniejId -> nazwa (do opisów)
        Map<String, String> turniejName = new HashMap<>();
        for (Turniej t : turniejRepo.findAll()) {
            turniejName.put(t.getIdTurnieju(), t.getNazwa());
        }

        int aktualnySezon = tournamentService.getAktualnySezon();

        // ===== karta: otwarte turnieje (tylko aktualny sezon) =====
        Div openCard = card();
        openCard.add(new H2("Otwarte turnieje (zapisy) — sezon " + aktualnySezon));

        Grid<Turniej> openGrid = new Grid<>(Turniej.class, false);
        openGrid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES, GridVariant.LUMO_COLUMN_BORDERS);
        openGrid.setWidthFull();
        openGrid.setHeight("360px");

        openGrid.addColumn(Turniej::getNazwa).setHeader("Nazwa").setAutoWidth(true).setFlexGrow(2);
        openGrid.addColumn(Turniej::getRanga).setHeader("Ranga").setAutoWidth(true);
        openGrid.addColumn(Turniej::getSezon).setHeader("Sezon").setAutoWidth(true);
        openGrid.addColumn(t -> tournamentService.liczbaZgloszen(t.getIdTurnieju()))
                .setHeader("Zgłoszeń").setAutoWidth(true);
        openGrid.addColumn(Turniej::getMaxZawodnikow).setHeader("Max").setAutoWidth(true);

        openGrid.addItemClickListener(e ->
                getUI().ifPresent(ui -> ui.navigate("turniej/" + e.getItem().getIdTurnieju()))
        );

        refreshOpen(openGrid, tournamentService);
        openCard.add(openGrid);
        add(openCard);

        // ===== karta: moje zgłoszenia =====
        Div myCard = card();
        myCard.add(new H2("Moje zgłoszenia"));

        myGrid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES, GridVariant.LUMO_COLUMN_BORDERS);
        myGrid.setWidthFull();
        myGrid.setHeight("260px");

        myGrid.addColumn(z -> turniejName.getOrDefault(z.getIdTurnieju(), z.getIdTurnieju()))
                .setHeader("Turniej").setAutoWidth(true).setFlexGrow(2);
        myGrid.addColumn(ZgloszenieTurniejowe::getStatus).setHeader("Status").setAutoWidth(true);

        refreshMy(myGrid, tournamentService, idZawodnika);
        myCard.add(myGrid);
        add(myCard);

        // ===== karta: punkty w turniejach (+zielone) =====
        Div pointsCard = card();
        pointsCard.add(new H2("Punkty w turniejach (Race)"));

        pointsGrid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES, GridVariant.LUMO_COLUMN_BORDERS);
        pointsGrid.setWidthFull();
        pointsGrid.setHeight("320px");

        pointsGrid.addColumn(PunktyRow::turniej).setHeader("Turniej").setAutoWidth(true).setFlexGrow(2);
        pointsGrid.addColumn(PunktyRow::opis).setHeader("Etap").setAutoWidth(true);
        pointsGrid.addComponentColumn(r -> {
            Span s = new Span("+" + r.punkty + " pkt");
            s.getStyle().set("color", "var(--lumo-success-text-color)");
            s.getStyle().set("font-weight", "700");
            return s;
        }).setHeader("Przyrost").setAutoWidth(true);

        List<PunktyTurniejowe> pts = punktyRepo.findByIdZawodnikaOrderByUpdatedAtDesc(idZawodnika);
        pointsGrid.setItems(
                pts.stream().map(p -> new PunktyRow(
                        turniejName.getOrDefault(p.getIdTurnieju(), p.getIdTurnieju()),
                        (p.getOpis() != null ? p.getOpis() : "-"),
                        p.getPunkty()
                )).toList()
        );

        pointsCard.add(pointsGrid);
        add(pointsCard);

        // ===== karta: archiwum zakończonych turniejów (poprzednie sezony) =====
        Div oldDoneCard = card();
        oldDoneCard.add(new H2("Zakończone — poprzednie sezony (archiwum)"));

        Grid<Turniej> oldDoneGrid = new Grid<>(Turniej.class, false);
        oldDoneGrid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES, GridVariant.LUMO_COLUMN_BORDERS);
        oldDoneGrid.setWidthFull();
        oldDoneGrid.setHeight("260px");

        oldDoneGrid.addColumn(Turniej::getNazwa).setHeader("Nazwa").setAutoWidth(true).setFlexGrow(2);
        oldDoneGrid.addColumn(Turniej::getRanga).setHeader("Ranga").setAutoWidth(true);
        oldDoneGrid.addColumn(Turniej::getSezon).setHeader("Sezon").setAutoWidth(true);

        oldDoneGrid.addItemClickListener(e ->
                getUI().ifPresent(ui -> ui.navigate("turniej/" + e.getItem().getIdTurnieju()))
        );

        oldDoneGrid.setItems(tournamentService.zakonczonePoprzednichSezonow());
        oldDoneCard.add(oldDoneGrid);
        add(oldDoneCard);

        Notification.show("Punkty naliczają się po awansach i na starcie turnieju (R1).", 2500, Notification.Position.BOTTOM_START);
    }

    private void refreshOpen(Grid<Turniej> openGrid, TournamentService service) {
        int sezon = service.getAktualnySezon();
        List<Turniej> all = service.turniejeAktualnegoSezonu();
        openGrid.setItems(all.stream()
                .filter(t -> t.getStatus() == TurniejStatus.OTWARTE_ZAPISY)
                .filter(t -> t.getSezon() == sezon)
                .toList());
    }

    private void refreshMy(Grid<ZgloszenieTurniejowe> myGrid, TournamentService service, String idZawodnika) {
        myGrid.setItems(service.mojeZgloszenia(idZawodnika));
    }

    private Div card() {
        Div card = new Div();
        card.addClassNames("card", LumoUtility.Margin.Bottom.LARGE);
        card.setWidth("min(1100px, 100%)");
        return card;
    }

    private record PunktyRow(String turniej, String opis, int punkty) {}
}
