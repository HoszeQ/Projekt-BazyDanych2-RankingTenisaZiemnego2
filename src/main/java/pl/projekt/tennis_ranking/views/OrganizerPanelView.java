package pl.projekt.tennis_ranking.views;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility;
import jakarta.annotation.security.RolesAllowed;
import org.springframework.security.core.context.SecurityContextHolder;
import pl.projekt.tennis_ranking.model.Organizator;
import pl.projekt.tennis_ranking.model.Turniej;
import pl.projekt.tennis_ranking.model.TurniejStatus;
import pl.projekt.tennis_ranking.repo.KontoRepository;
import pl.projekt.tennis_ranking.repo.OrganizatorRepository;
import pl.projekt.tennis_ranking.service.TournamentService;

import java.time.Year;
import java.util.List;

@Route(value = "panel/organizator", layout = MainLayout.class)
@RolesAllowed("ORGANIZATOR")
public class OrganizerPanelView extends VerticalLayout {

    private final Grid<Turniej> currentGrid = new Grid<>(Turniej.class, false);
    private final Grid<Turniej> archiveGrid = new Grid<>(Turniej.class, false);

    public OrganizerPanelView(
            KontoRepository kontoRepo,
            OrganizatorRepository orgRepo,
            TournamentService tournamentService
    ) {
        setSizeFull();
        addClassNames(LumoUtility.Padding.LARGE);
        addClassName("page");

        add(new H2("Panel Organizatora"));

        String login = SecurityContextHolder.getContext().getAuthentication().getName();
        String idKonta = kontoRepo.findByLogin(login)
                .orElseThrow(() -> new IllegalStateException("Brak konta dla loginu: " + login))
                .getIdKonta();

        Organizator org = orgRepo.findByIdKonta(idKonta)
                .orElseThrow(() -> new IllegalStateException("Brak profilu organizatora dla konta: " + idKonta));

        String idOrganizatora = org.getIdOrganizatora();
        int aktualnySezon = tournamentService.getAktualnySezon();

        // ====== tworzenie turnieju ======
        Div createCard = card();
        createCard.add(new H2("Utwórz turniej"));

        TextField nazwa = new TextField("Nazwa");
        nazwa.setRequired(true);
        nazwa.setWidthFull();
        nazwa.setClearButtonVisible(true);

        ComboBox<String> ranga = new ComboBox<>("Ranga");
        ranga.setItems("WIELKI_SZLEM", "ATP_1000", "ATP_500", "ATP_250", "CHALLENGER_125");
        ranga.setRequired(true);
        ranga.setWidthFull();

        IntegerField sezon = new IntegerField("Sezon");
        sezon.setRequired(true);
        sezon.setMin(2000);
        sezon.setMax(2100);
        sezon.setValue(Year.now().getValue());

        IntegerField max = new IntegerField("Max zawodników");
        max.setRequired(true);
        max.setMin(2);
        max.setValue(32);

        FormLayout form = new FormLayout(nazwa, ranga, sezon, max);
        form.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0", 1),
                new FormLayout.ResponsiveStep("800px", 2)
        );
        form.setColspan(nazwa, 2);
        form.setColspan(ranga, 2);

        Button utworz = new Button("Utwórz");
        utworz.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        Button wyczysc = new Button("Wyczyść");
        wyczysc.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        wyczysc.addClickListener(e -> {
            nazwa.clear();
            ranga.clear();
            sezon.setValue(Year.now().getValue());
            max.setValue(32);
        });

        utworz.addClickListener(e -> {
            try {
                tournamentService.utworzTurniej(
                        idOrganizatora,
                        nazwa.getValue(),
                        ranga.getValue(),
                        sezon.getValue() != null ? sezon.getValue() : Year.now().getValue(),
                        max.getValue() != null ? max.getValue() : 32
                );
                Notification.show("Utworzono turniej", 2500, Notification.Position.TOP_CENTER);
                wyczysc.click();
                refreshGrids(tournamentService, idOrganizatora);
            } catch (Exception ex) {
                Notification.show("Błąd: " + ex.getMessage(), 4500, Notification.Position.MIDDLE);
            }
        });

        createCard.add(form, new HorizontalLayout(wyczysc, utworz));
        add(createCard);

        // ====== AKTUALNY SEZON ======
        Div currentCard = card();
        currentCard.add(new H2("Twoje turnieje — sezon " + aktualnySezon));

        configureCurrentGrid(tournamentService, idOrganizatora, aktualnySezon);
        currentCard.add(currentGrid);
        add(currentCard);

        // ====== ARCHIWUM ======
        Div archiveCard = card();
        archiveCard.add(new H2("Archiwum — poprzednie sezony (tylko podgląd)"));

        configureArchiveGrid();
        archiveCard.add(archiveGrid);
        add(archiveCard);

        refreshGrids(tournamentService, idOrganizatora);
    }

    private void configureCurrentGrid(TournamentService service, String idOrganizatora, int aktualnySezon) {
        currentGrid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES, GridVariant.LUMO_COLUMN_BORDERS);
        currentGrid.setWidthFull();
        currentGrid.setHeight("520px");

        currentGrid.addColumn(Turniej::getNazwa).setHeader("Nazwa").setAutoWidth(true).setFlexGrow(2);
        currentGrid.addColumn(Turniej::getRanga).setHeader("Ranga").setAutoWidth(true);
        currentGrid.addColumn(Turniej::getStatus).setHeader("Status").setAutoWidth(true);
        currentGrid.addColumn(Turniej::getSezon).setHeader("Sezon").setAutoWidth(true);
        currentGrid.addColumn(Turniej::getMaxZawodnikow).setHeader("Max").setAutoWidth(true);

        currentGrid.addColumn(t -> service.liczbaZgloszen(t.getIdTurnieju()))
                .setHeader("Zgłoszeń")
                .setAutoWidth(true);

        currentGrid.addComponentColumn(t -> {
            boolean isCurrentSeason = t.getSezon() == aktualnySezon;

            Button generuj = new Button("Zamknij zapisy + generuj drabinkę");
            generuj.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SMALL);
            generuj.setEnabled(isCurrentSeason && t.getStatus() == TurniejStatus.OTWARTE_ZAPISY);

            generuj.addClickListener(e -> {
                try {
                    service.zamknijZapisyIGenerujDrabinke(t.getIdTurnieju());
                    Notification.show("Drabinka wygenerowana", 3000, Notification.Position.TOP_CENTER);
                    refreshGrids(service, idOrganizatora);
                } catch (Exception ex) {
                    Notification.show("Błąd: " + ex.getMessage(), 4500, Notification.Position.MIDDLE);
                }
            });

            Button zakoncz = new Button("Zakończ turniej");
            zakoncz.addThemeVariants(ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_SMALL);
            zakoncz.setEnabled(isCurrentSeason && t.getStatus() == TurniejStatus.W_TRAKCIE);

            zakoncz.addClickListener(e -> {
                try {
                    service.zakonczTurniej(t.getIdTurnieju());
                    Notification.show("Turniej zakończony", 3000, Notification.Position.TOP_CENTER);
                    refreshGrids(service, idOrganizatora);
                } catch (Exception ex) {
                    Notification.show("Błąd: " + ex.getMessage(), 4500, Notification.Position.MIDDLE);
                }
            });

            Button podglad = new Button("Podgląd");
            podglad.addThemeVariants(ButtonVariant.LUMO_SMALL);
            podglad.addClickListener(e ->
                    getUI().ifPresent(ui -> ui.navigate("turniej/" + t.getIdTurnieju()))
            );

            return new HorizontalLayout(generuj, zakoncz, podglad);
        }).setHeader("Akcje").setAutoWidth(true);
    }

    private void configureArchiveGrid() {
        archiveGrid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES, GridVariant.LUMO_COLUMN_BORDERS);
        archiveGrid.setWidthFull();
        archiveGrid.setHeight("360px");

        archiveGrid.addColumn(Turniej::getNazwa).setHeader("Nazwa").setAutoWidth(true).setFlexGrow(2);
        archiveGrid.addColumn(Turniej::getRanga).setHeader("Ranga").setAutoWidth(true);
        archiveGrid.addColumn(Turniej::getStatus).setHeader("Status").setAutoWidth(true);
        archiveGrid.addColumn(Turniej::getSezon).setHeader("Sezon").setAutoWidth(true);

        archiveGrid.addComponentColumn(t -> {
            Button podglad = new Button("Podgląd");
            podglad.addThemeVariants(ButtonVariant.LUMO_SMALL);
            podglad.addClickListener(e ->
                    getUI().ifPresent(ui -> ui.navigate("turniej/" + t.getIdTurnieju()))
            );
            return podglad;
        }).setHeader("Akcja").setAutoWidth(true);
    }

    private void refreshGrids(TournamentService service, String idOrganizatora) {
        int sezon = service.getAktualnySezon();
        List<Turniej> all = service.turniejeOrganizatora(idOrganizatora);

        currentGrid.setItems(all.stream()
                .filter(t -> t.getSezon() == sezon)
                .toList());

        archiveGrid.setItems(all.stream()
                .filter(t -> t.getSezon() != sezon)
                .toList());
    }

    private Div card() {
        Div card = new Div();
        card.addClassNames(
                LumoUtility.Background.BASE,
                LumoUtility.BorderRadius.LARGE,
                LumoUtility.BoxShadow.MEDIUM,
                LumoUtility.Padding.LARGE,
                LumoUtility.Margin.Bottom.LARGE
        );
        card.setWidth("min(1100px, 100%)");
        return card;
    }
}
