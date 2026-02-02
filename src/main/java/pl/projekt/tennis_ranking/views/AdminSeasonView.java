package pl.projekt.tennis_ranking.views;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility;
import jakarta.annotation.security.RolesAllowed;
import org.springframework.security.core.context.SecurityContextHolder;
import pl.projekt.tennis_ranking.model.Turniej;
import pl.projekt.tennis_ranking.repo.KontoRepository;
import pl.projekt.tennis_ranking.service.TournamentService;

@Route(value = "panel/admin/sezon", layout = MainLayout.class)
@RolesAllowed("ADMIN")
public class AdminSeasonView extends VerticalLayout {

    public AdminSeasonView(TournamentService tournamentService, KontoRepository kontoRepo) {
        setSizeFull();
        addClassNames(LumoUtility.Padding.LARGE);
        addClassName("page");

        add(new H2("Sezon (ADMIN)"));

        IntegerField sezon = new IntegerField("Sezon");
        sezon.setValue(2026);
        sezon.setMin(2000);
        sezon.setMax(2100);
        sezon.setStepButtonsVisible(true);

        Button open = new Button("OTWÓRZ SEZON (reset Race)");
        open.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        Button close = new Button("ZAMKNIJ SEZON (utwórz ATP FINALS TOP8)");
        close.addThemeVariants(ButtonVariant.LUMO_ERROR);

        open.addClickListener(e -> {
            try {
                tournamentService.otworzSezonIRestartRace(sezon.getValue());
                Notification.show("Sezon otwarty. Punkty Race zresetowane.", 3500, Notification.Position.TOP_CENTER);
            } catch (Exception ex) {
                Notification.show("Błąd: " + ex.getMessage(), 4500, Notification.Position.MIDDLE);
            }
        });

        close.addClickListener(e -> openCloseSeasonDialog(tournamentService, kontoRepo, sezon.getValue()));

        Div card = new Div();
        card.addClassNames("card");
        card.setWidth("min(800px, 100%)");

        HorizontalLayout row = new HorizontalLayout(open, close);
        row.addClassName(LumoUtility.Gap.MEDIUM);

        Paragraph tip = new Paragraph(
                "Zamknięcie sezonu tworzy turniej ATP FINALS dla TOP 8 w Race i od razu generuje drabinkę."
        );
        tip.getStyle().set("opacity", "0.8");

        card.add(sezon, row, tip);
        add(card);
    }

    private void openCloseSeasonDialog(TournamentService tournamentService, KontoRepository kontoRepo, Integer sezon) {
        Dialog d = new Dialog();
        d.setHeaderTitle("Potwierdzenie");

        d.add(new Paragraph("Na pewno chcesz zamknąć sezon " + sezon + "?"));
        d.add(new Paragraph("Utworzy się turniej ATP FINALS (TOP 8) i od razu przejdzie do statusu W_TRAKCIE."));

        Button cancel = new Button("Anuluj", e -> d.close());
        Button confirm = new Button("Tak, zamknij sezon");
        confirm.addThemeVariants(ButtonVariant.LUMO_ERROR);

        confirm.addClickListener(e -> {
            try {
                String login = SecurityContextHolder.getContext().getAuthentication().getName();
                String adminId = kontoRepo.findByLogin(login).orElseThrow().getIdKonta();

                Turniej finals = tournamentService.zamknijSezonIUtworzAtpFinals(sezon, adminId);
                Notification.show("Utworzono: " + finals.getNazwa(), 3500, Notification.Position.TOP_CENTER);

                d.close();
                getUI().ifPresent(ui -> ui.navigate("turniej/" + finals.getIdTurnieju()));
            } catch (Exception ex) {
                Notification.show("Błąd: " + ex.getMessage(), 4500, Notification.Position.MIDDLE);
            }
        });

        d.getFooter().add(cancel, confirm);
        d.open();
    }
}
