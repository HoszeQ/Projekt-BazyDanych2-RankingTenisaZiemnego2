package pl.projekt.tennis_ranking.views;

import java.util.List;

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility;

import jakarta.annotation.security.PermitAll;
import pl.projekt.tennis_ranking.model.Turniej;
import pl.projekt.tennis_ranking.model.TurniejStatus;
import pl.projekt.tennis_ranking.service.TournamentService;

@Route(value = "turnieje", layout = MainLayout.class)
@PermitAll
public class TournamentsView extends VerticalLayout {

    public TournamentsView(TournamentService tournamentService) {
        setSizeFull();
        addClassNames(LumoUtility.Padding.LARGE);
        addClassName("page");

        int sezon = tournamentService.getAktualnySezon();

        add(new H2("Turnieje — sezon " + sezon));

        List<Turniej> allCurrent = tournamentService.turniejeAktualnegoSezonu();
        List<Turniej> allOld = tournamentService.turniejePoprzednichSezonow();

        // ===== OTWARTE (tylko aktualny sezon) =====
        Div openCard = card();
        openCard.add(sectionHeader("Otwarte zapisy (sezon " + sezon + ")", countBadge(allCurrent, TurniejStatus.OTWARTE_ZAPISY)));
        Grid<Turniej> openGrid = buildGrid();
        openGrid.setItems(allCurrent.stream().filter(t -> t.getStatus() == TurniejStatus.OTWARTE_ZAPISY).toList());
        openCard.add(openGrid);

        // ===== W TRAKCIE (tylko aktualny sezon) =====
        Div liveCard = card();
        liveCard.add(sectionHeader("W trakcie (sezon " + sezon + ")", countBadge(allCurrent, TurniejStatus.W_TRAKCIE)));
        Grid<Turniej> liveGrid = buildGrid();
        liveGrid.setItems(allCurrent.stream().filter(t -> t.getStatus() == TurniejStatus.W_TRAKCIE).toList());
        liveCard.add(liveGrid);

        // ===== ZAKOŃCZONE (tylko aktualny sezon) =====
        Div doneCard = card();
        doneCard.add(sectionHeader("Zakończone (sezon " + sezon + ")", countBadge(allCurrent, TurniejStatus.ZAKONCZONY)));
        Grid<Turniej> doneGrid = buildGrid();
        doneGrid.setItems(allCurrent.stream().filter(t -> t.getStatus() == TurniejStatus.ZAKONCZONY).toList());
        doneCard.add(doneGrid);

        // ===== POPRZEDNIE SEZONY (wszystkie statusy, ale już “archiwum”) =====
        Div oldCard = card();
        oldCard.add(sectionHeader("Poprzednie sezony (archiwum)", countBadgeAll(allOld)));

        Grid<Turniej> oldGrid = buildGrid();
        oldGrid.setHeight("360px");
        oldGrid.setItems(allOld);
        oldCard.add(oldGrid);

        add(openCard, liveCard, doneCard, oldCard);
    }

    private Grid<Turniej> buildGrid() {
        Grid<Turniej> grid = new Grid<>(Turniej.class, false);
        grid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES, GridVariant.LUMO_COLUMN_BORDERS);
        grid.setWidthFull();
        grid.setHeight("260px");

        grid.addColumn(Turniej::getNazwa).setHeader("Nazwa").setAutoWidth(true).setFlexGrow(2);
        grid.addColumn(Turniej::getRanga).setHeader("Ranga").setAutoWidth(true);
        grid.addColumn(Turniej::getStatus).setHeader("Status").setAutoWidth(true);
        grid.addColumn(Turniej::getSezon).setHeader("Sezon").setAutoWidth(true);

        grid.addItemClickListener(e ->
                getUI().ifPresent(ui -> ui.navigate("turniej/" + e.getItem().getIdTurnieju()))
        );

        return grid;
    }

    private Div sectionHeader(String title, Div badge) {
        Div wrap = new Div();
        wrap.getStyle().set("display", "flex");
        wrap.getStyle().set("align-items", "baseline");
        wrap.getStyle().set("justify-content", "space-between");
        wrap.getStyle().set("gap", "12px");

        H3 h = new H3(title);
        h.getStyle().set("margin", "0");
        wrap.add(h, badge);
        return wrap;
    }

    private Div countBadge(List<Turniej> list, TurniejStatus status) {
        long c = list.stream().filter(t -> t.getStatus() == status).count();
        Div badge = new Div();
        badge.addClassName("badge");
        badge.setText("Ilość: " + c);
        return badge;
    }

    private Div countBadgeAll(List<Turniej> list) {
        Div badge = new Div();
        badge.addClassName("badge");
        badge.setText("Ilość: " + list.size());
        return badge;
    }

    private Div card() {
        Div card = new Div();
        card.addClassNames("card", LumoUtility.Margin.Bottom.LARGE);
        card.setWidth("min(1100px, 100%)");
        return card;
    }
}
