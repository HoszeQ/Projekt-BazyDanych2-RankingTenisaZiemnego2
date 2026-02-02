package pl.projekt.tennis_ranking.views;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.theme.lumo.LumoUtility;
import jakarta.annotation.security.RolesAllowed;
import pl.projekt.tennis_ranking.model.Konto;
import pl.projekt.tennis_ranking.security.Rola;
import pl.projekt.tennis_ranking.service.UserAdminService;

@Route(value = "panel/admin", layout = MainLayout.class)
@RolesAllowed("ADMIN")
public class AdminPanelView extends VerticalLayout {

    private final Grid<Konto> grid = new Grid<>(Konto.class, false);
    private final TextField filter = new TextField("Szukaj po loginie");

    public AdminPanelView(UserAdminService service) {
        setSizeFull();
        addClassNames(LumoUtility.Padding.LARGE);
        addClassName("page");

        add(new H2("Panel Administratora"));

        // ===== KARTA: skrót do sezonu =====
        Div seasonCard = card();
        seasonCard.add(new H4("Sezon / ATP Finals"));
        seasonCard.addClassName(LumoUtility.Display.FLEX);
        seasonCard.getStyle().set("justify-content", "space-between");
        seasonCard.getStyle().set("align-items", "center");

        RouterLink seasonLink = new RouterLink("→ Zarządzaj sezonem", AdminSeasonView.class);
        seasonLink.getStyle().set("font-weight", "700");
        seasonLink.getStyle().set("text-decoration", "none");

        seasonCard.add(seasonLink);
        add(seasonCard);

        // ============ KARTA: dodawanie usera ============
        Div createCard = card();
        createCard.add(new H4("Dodaj użytkownika"));

        ComboBox<Rola> rola = new ComboBox<>("Rola");
        rola.setItems(Rola.ORGANIZATOR, Rola.SEDZIA, Rola.ZAWODNIK, Rola.KIBIC);
        rola.setRequired(true);

        TextField login = new TextField("Login");
        login.setRequired(true);
        login.setClearButtonVisible(true);

        PasswordField haslo = new PasswordField("Hasło");
        haslo.setRequired(true);

        TextField imie = new TextField("Imię");
        TextField nazwisko = new TextField("Nazwisko");

        TextField kraj = new TextField("Kraj (np. PL)");
        IntegerField punkty = new IntegerField("Punkty (Race)");
        punkty.setMin(0);

        IntegerField numerLicencji = new IntegerField("Numer licencji (sędzia)");
        numerLicencji.setMin(1);

        TextField pseudonim = new TextField("Pseudonim (kibic)");

        FormLayout form = new FormLayout();
        form.setWidthFull();
        form.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0", 1),
                new FormLayout.ResponsiveStep("900px", 2)
        );
        form.add(rola, 2);
        form.add(login, haslo);
        form.add(imie, nazwisko);
        form.add(kraj, punkty);
        form.add(numerLicencji, pseudonim);

        Runnable refreshVisibility = () -> {
            Rola r = rola.getValue();
            boolean isPlayer = r == Rola.ZAWODNIK;
            boolean isJudge = r == Rola.SEDZIA;
            boolean isFan = r == Rola.KIBIC;

            imie.setVisible(!isFan);
            nazwisko.setVisible(!isFan);

            kraj.setVisible(isPlayer);
            punkty.setVisible(isPlayer);

            numerLicencji.setVisible(isJudge);
            pseudonim.setVisible(isFan);

            imie.setRequired(!isFan);
            nazwisko.setRequired(!isFan);
            kraj.setRequired(isPlayer);
            numerLicencji.setRequired(isJudge);
            pseudonim.setRequired(isFan);
        };
        rola.addValueChangeListener(e -> refreshVisibility.run());
        refreshVisibility.run();

        Button utworz = new Button("Utwórz");
        utworz.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        Button wyczysc = new Button("Wyczyść");
        wyczysc.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        HorizontalLayout actions = new HorizontalLayout(wyczysc, utworz);
        actions.addClassName(LumoUtility.Gap.MEDIUM);

        wyczysc.addClickListener(e -> {
            rola.clear(); login.clear(); haslo.clear();
            imie.clear(); nazwisko.clear();
            kraj.clear(); punkty.clear();
            numerLicencji.clear(); pseudonim.clear();
            refreshVisibility.run();
        });

        utworz.addClickListener(e -> {
            try {
                if (rola.getValue() == null) throw new IllegalArgumentException("Wybierz rolę.");
                if (login.isEmpty()) throw new IllegalArgumentException("Podaj login.");
                if (haslo.isEmpty() || haslo.getValue().length() < 6)
                    throw new IllegalArgumentException("Hasło min. 6 znaków.");

                service.createUser(
                        rola.getValue(),
                        login.getValue().trim(),
                        haslo.getValue(),
                        imie.getValue() == null ? null : imie.getValue().trim(),
                        nazwisko.getValue() == null ? null : nazwisko.getValue().trim(),
                        kraj.getValue() == null ? null : kraj.getValue().trim(),
                        punkty.getValue(),
                        numerLicencji.getValue(),
                        pseudonim.getValue() == null ? null : pseudonim.getValue().trim()
                );

                Notification.show("Utworzono użytkownika!", 2500, Notification.Position.TOP_CENTER);
                wyczysc.click();
                refreshGrid(service);

            } catch (Exception ex) {
                Notification.show("Błąd: " + ex.getMessage(), 4500, Notification.Position.MIDDLE);
            }
        });

        createCard.add(form, actions);
        add(createCard);

        // ============ KARTA: lista kont ============
        Div listCard = card();
        listCard.add(new H4("Konta w systemie"));

        filter.setPlaceholder("np. admin, player...");
        filter.setClearButtonVisible(true);
        filter.setValueChangeMode(ValueChangeMode.EAGER);
        filter.addValueChangeListener(e -> refreshGrid(service));

        configureGrid(service);
        refreshGrid(service);

        listCard.add(filter, grid);
        add(listCard);
    }

    private void configureGrid(UserAdminService service) {
        grid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES, GridVariant.LUMO_COLUMN_BORDERS);
        grid.setWidthFull();

        grid.addColumn(Konto::getLogin).setHeader("Login").setSortable(true).setAutoWidth(true).setFlexGrow(2);
        grid.addColumn(k -> k.getRola().name()).setHeader("Rola").setSortable(true).setAutoWidth(true);
        grid.addColumn(k -> k.isEnabled() ? "TAK" : "NIE").setHeader("Aktywne").setSortable(true).setAutoWidth(true);

        grid.addComponentColumn(k -> {
            Button toggle = new Button(k.isEnabled() ? "Zablokuj" : "Odblokuj");
            toggle.addThemeVariants(ButtonVariant.LUMO_SMALL);
            toggle.addClickListener(e -> {
                service.toggleEnabled(k.getIdKonta());
                refreshGrid(service);
            });

            Button edit = new Button("Modyfikuj");
            edit.addThemeVariants(ButtonVariant.LUMO_SMALL);
            edit.addClickListener(e -> openEditDialog(service, k));

            Button del = new Button("Usuń");
            del.addThemeVariants(ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_SMALL);
            del.addClickListener(e -> {
                service.deleteKonto(k.getIdKonta());
                refreshGrid(service);
            });

            return new HorizontalLayout(toggle, edit, del);
        }).setHeader("Akcje").setAutoWidth(true).setFlexGrow(0);
    }

    private void refreshGrid(UserAdminService service) {
        grid.setItems(service.listKonta(filter.getValue()));
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

    private void openEditDialog(UserAdminService service, Konto konto) {
        Dialog d = new Dialog();
        d.setHeaderTitle("Modyfikuj użytkownika: " + konto.getLogin());

        Checkbox enabled = new Checkbox("Konto aktywne");
        enabled.setValue(konto.isEnabled());

        PasswordField newPass = new PasswordField("Nowe hasło (opcjonalnie)");
        newPass.setPlaceholder("Zostaw puste, aby nie zmieniać");

        TextField imie = new TextField("Imię");
        TextField nazwisko = new TextField("Nazwisko");
        TextField kraj = new TextField("Kraj");
        IntegerField punkty = new IntegerField("Punkty (Race)");
        IntegerField numerLicencji = new IntegerField("Numer licencji");
        TextField pseudonim = new TextField("Pseudonim");

        boolean isPlayer = konto.getRola() == Rola.ZAWODNIK;
        boolean isJudge  = konto.getRola() == Rola.SEDZIA;
        boolean isFan    = konto.getRola() == Rola.KIBIC;
        boolean isOrg    = konto.getRola() == Rola.ORGANIZATOR;

        imie.setVisible(isPlayer || isJudge || isOrg);
        nazwisko.setVisible(isPlayer || isJudge || isOrg);

        kraj.setVisible(isPlayer);
        punkty.setVisible(isPlayer);

        numerLicencji.setVisible(isJudge);
        pseudonim.setVisible(isFan);

        FormLayout form = new FormLayout(enabled, newPass, imie, nazwisko, kraj, punkty, numerLicencji, pseudonim);
        form.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0", 1),
                new FormLayout.ResponsiveStep("650px", 2)
        );

        Button cancel = new Button("Anuluj", e -> d.close());
        Button save = new Button("Zapisz");
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        save.addClickListener(e -> {
            try {
                service.updateUser(
                        konto.getIdKonta(),
                        enabled.getValue(),
                        newPass.getValue(),
                        imie.getValue(),
                        nazwisko.getValue(),
                        kraj.getValue(),
                        punkty.getValue(),
                        numerLicencji.getValue(),
                        pseudonim.getValue()
                );
                Notification.show("Zapisano zmiany", 2500, Notification.Position.TOP_CENTER);
                d.close();
                refreshGrid(service);
            } catch (Exception ex) {
                Notification.show("Błąd: " + ex.getMessage(), 4500, Notification.Position.MIDDLE);
            }
        });

        d.add(form);
        d.getFooter().add(cancel, save);
        d.open();
    }
}
