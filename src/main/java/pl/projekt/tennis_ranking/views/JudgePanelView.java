package pl.projekt.tennis_ranking.views;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.core.context.SecurityContextHolder;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility;

import jakarta.annotation.security.RolesAllowed;
import pl.projekt.tennis_ranking.model.Mecz;
import pl.projekt.tennis_ranking.model.MeczStatus;
import pl.projekt.tennis_ranking.model.Turniej;
import pl.projekt.tennis_ranking.repo.MeczRepository;
import pl.projekt.tennis_ranking.repo.TurniejRepository;
import pl.projekt.tennis_ranking.service.TournamentService;

@Route(value = "panel/sedzia", layout = MainLayout.class)
@RolesAllowed("SEDZIA")
public class JudgePanelView extends VerticalLayout {

    private final Grid<Row> todoGrid = new Grid<>(Row.class, false);
    private final Grid<Row> doneGrid = new Grid<>(Row.class, false);
    private final Grid<Row> archiveGrid = new Grid<>(Row.class, false);

    public JudgePanelView(
            MeczRepository meczRepo,
            TurniejRepository turniejRepo,
            TournamentService tournamentService
    ) {
        setSizeFull();
        addClassNames(LumoUtility.Padding.LARGE);
        addClassName("page");

        add(new H2("Panel Sędziego"));

        int aktualnySezon = tournamentService.getAktualnySezon();

        // ===== wczytanie danych =====
        List<Mecz> mecze = meczRepo.findAll();

        // map turniejId -> Turniej (nazwa + sezon)
        Set<String> turniejIds = mecze.stream().map(Mecz::getIdTurnieju).collect(Collectors.toSet());
        Map<String, Turniej> turniejById = new HashMap<>();
        for (Turniej t : turniejRepo.findAllById(turniejIds)) {
            turniejById.put(t.getIdTurnieju(), t);
        }

        List<Row> rows = mecze.stream()
                .map(m -> {
                    Turniej t = turniejById.get(m.getIdTurnieju());
                    String name = (t != null ? t.getNazwa() : m.getIdTurnieju());
                    Integer sezon = (t != null ? t.getSezon() : null);
                    return new Row(m, name, sezon);
                })
                .sorted(Comparator
                        .comparing((Row r) -> r.turniej)
                        .thenComparing(r -> safeInt(r.mecz.getRunda()))
                        .thenComparing(r -> safeInt(r.mecz.getSlotWRundzie())))
                .toList();

        // ===== podział sezonów =====
        List<Row> currentSeason = rows.stream()
                .filter(r -> r.sezon != null && r.sezon == aktualnySezon)
                .toList();

        List<Row> archive = rows.stream()
                .filter(r -> r.sezon != null && r.sezon != aktualnySezon)
                .toList();

        // ===== aktualny sezon: do wpisania / zakończone =====
        List<Row> toDo = currentSeason.stream()
                .filter(r -> r.meczStatus != MeczStatus.ZAKONCZONY)
                .filter(r -> r.meczStatus != MeczStatus.WALKOWER)
                .toList();

        List<Row> done = currentSeason.stream()
                .filter(r -> r.meczStatus == MeczStatus.ZAKONCZONY || r.meczStatus == MeczStatus.WALKOWER)
                .toList();

        // ===== sekcja: do wpisania =====
        H2 hTodo = new H2("Mecze do wpisania — sezon " + aktualnySezon);
        hTodo.addClassNames(LumoUtility.Margin.Top.MEDIUM);

        configureBase(todoGrid);
        todoGrid.setHeight("360px");

        todoGrid.addComponentColumn(r -> {
            Button btn = new Button("Wpisz wynik");
            btn.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SMALL);

            boolean canEdit = r.meczStatus != MeczStatus.ZAKONCZONY && r.meczStatus != MeczStatus.WALKOWER;
            btn.setEnabled(canEdit);

            btn.addClickListener(e -> openDialog(r.mecz, tournamentService));
            return btn;
        }).setHeader("Akcja").setAutoWidth(true);

        todoGrid.setItems(toDo);

        // ===== sekcja: zakończone (aktualny sezon) =====
        H2 hDone = new H2("Zakończone — sezon " + aktualnySezon);
        hDone.addClassNames(LumoUtility.Margin.Top.LARGE);

        configureBase(doneGrid);
        doneGrid.setHeight("300px");

        doneGrid.addComponentColumn(r -> {
            Button btn = new Button("Podgląd");
            btn.addThemeVariants(ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_SMALL);
            btn.addClickListener(e -> openReadOnlyDialog(r.mecz, tournamentService));
            return btn;
        }).setHeader("Akcja").setAutoWidth(true);

        doneGrid.setItems(done);

        // ===== sekcja: archiwum =====
        H2 hArchive = new H2("Archiwum — poprzednie sezony (tylko podgląd)");
        hArchive.addClassNames(LumoUtility.Margin.Top.LARGE);

        configureBase(archiveGrid);
        archiveGrid.setHeight("320px");

        archiveGrid.addComponentColumn(r -> {
            Button btn = new Button("Podgląd");
            btn.addThemeVariants(ButtonVariant.LUMO_SMALL);
            btn.addClickListener(e -> openReadOnlyDialog(r.mecz, tournamentService));
            return btn;
        }).setHeader("Akcja").setAutoWidth(true);

        archiveGrid.setItems(archive);

        add(hTodo, todoGrid, hDone, doneGrid, hArchive, archiveGrid);
    }

    private void configureBase(Grid<Row> grid) {
        grid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES, GridVariant.LUMO_COLUMN_BORDERS);
        grid.setWidthFull();

        grid.addColumn(Row::tournamentName).setHeader("Turniej").setAutoWidth(true).setFlexGrow(2);
        grid.addColumn(Row::seasonLabel).setHeader("Sezon").setAutoWidth(true);
        grid.addColumn(Row::roundSlot).setHeader("Runda/Slot").setAutoWidth(true);
        grid.addColumn(Row::status).setHeader("Status").setAutoWidth(true);
    }

    private void openDialog(Mecz m, TournamentService tournamentService) {
        if (m == null) {
            Notification.show("Brak danych meczu", 2500, Notification.Position.TOP_CENTER);
            return;
        }

        if (m.getStatus() == MeczStatus.ZAKONCZONY || m.getStatus() == MeczStatus.WALKOWER) {
            Notification.show("Tego meczu nie można już edytować", 3000, Notification.Position.TOP_CENTER);
            return;
        }

        Dialog dialog = new Dialog();
        dialog.setHeaderTitle("Wpisz wynik");

        Set<String> ids = new HashSet<>();
        if (m.getIdZawodnikA() != null) ids.add(m.getIdZawodnikA());
        if (m.getIdZawodnikB() != null) ids.add(m.getIdZawodnikB());

        Map<String, String> labels = tournamentService.mapZawodnikIdToLabel(ids);

        String aLabel = safeLabel(m.getIdZawodnikA(), labels, "BYE");
        String bLabel = safeLabel(m.getIdZawodnikB(), labels, "BYE");

        com.vaadin.flow.component.combobox.ComboBox<String> winner =
                new com.vaadin.flow.component.combobox.ComboBox<>("Zwycięzca");

        List<String> options = new ArrayList<>();
        if (m.getIdZawodnikA() != null) options.add(m.getIdZawodnikA());
        if (m.getIdZawodnikB() != null) options.add(m.getIdZawodnikB());

        winner.setItems(options);
        winner.setItemLabelGenerator(id -> Objects.equals(id, m.getIdZawodnikA()) ? aLabel : bLabel);

        TextField wynik = new TextField("Wynik (np. 6:4 6:3)");
        wynik.setWidthFull();

        VerticalLayout content = new VerticalLayout();
        content.setPadding(false);
        content.setSpacing(true);
        content.add(
                new Paragraph("Mecz: " + aLabel + " vs " + bLabel),
                winner,
                wynik
        );

        Button save = new Button("Zapisz");
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        Button cancel = new Button("Anuluj", e -> dialog.close());

        save.addClickListener(e -> {
            try {
                tournamentService.judgeSubmitResult(m.getIdMeczu(), winner.getValue(), wynik.getValue());
                Notification.show("Zapisano wynik", 2500, Notification.Position.TOP_CENTER);
                dialog.close();
                getUI().ifPresent(ui -> ui.getPage().reload());
            } catch (Exception ex) {
                Notification.show("Błąd: " + ex.getMessage(), 4500, Notification.Position.MIDDLE);
            }
        });

        dialog.add(content);
        dialog.getFooter().add(new HorizontalLayout(cancel, save));
        dialog.open();
    }

    private void openReadOnlyDialog(Mecz m, TournamentService tournamentService) {
        if (m == null) {
            Notification.show("Brak danych meczu", 2500, Notification.Position.TOP_CENTER);
            return;
        }

        Dialog dialog = new Dialog();
        dialog.setHeaderTitle("Podgląd meczu");

        Set<String> ids = new HashSet<>();
        if (m.getIdZawodnikA() != null) ids.add(m.getIdZawodnikA());
        if (m.getIdZawodnikB() != null) ids.add(m.getIdZawodnikB());
        if (m.getIdZwyciezcy() != null) ids.add(m.getIdZwyciezcy());

        Map<String, String> labels = tournamentService.mapZawodnikIdToLabel(ids);

        String aLabel = safeLabel(m.getIdZawodnikA(), labels, "BYE");
        String bLabel = safeLabel(m.getIdZawodnikB(), labels, "BYE");
        String wLabel = safeLabel(m.getIdZwyciezcy(), labels, "-");

        VerticalLayout content = new VerticalLayout();
        content.setPadding(false);
        content.setSpacing(true);

        content.add(
                new Paragraph("Mecz: " + aLabel + " vs " + bLabel),
                new Paragraph("Status: " + (m.getStatus() != null ? m.getStatus().name() : "-")),
                new Paragraph("Wynik: " + (m.getWynik() != null ? m.getWynik() : "-")),
                new Paragraph("Zwycięzca: " + wLabel)
        );

        Button close = new Button("Zamknij", e -> dialog.close());
        dialog.add(content);
        dialog.getFooter().add(close);
        dialog.open();
    }

    private String safeLabel(String id, Map<String, String> labels, String fallback) {
        if (id == null || id.isBlank()) return fallback;
        if (labels == null || labels.isEmpty()) return fallback;
        return labels.getOrDefault(id, fallback);
    }

    private static int safeInt(Integer v) {
        return v == null ? 0 : v;
    }

    private static class Row {
        final Mecz mecz;
        final String turniej;
        final Integer sezon;
        final MeczStatus meczStatus;

        Row(Mecz mecz, String turniejName, Integer sezon) {
            this.mecz = mecz;
            this.turniej = turniejName;
            this.sezon = sezon;
            this.meczStatus = mecz != null ? mecz.getStatus() : null;
        }

        String tournamentName() { return turniej; }
        String seasonLabel() { return sezon != null ? String.valueOf(sezon) : "-"; }
        String roundSlot() { return "R" + mecz.getRunda() + " / " + mecz.getSlotWRundzie(); }
        String status() { return mecz.getStatus() != null ? mecz.getStatus().name() : "-"; }
    }
}
