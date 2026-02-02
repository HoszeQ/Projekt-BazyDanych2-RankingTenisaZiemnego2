package pl.projekt.tennis_ranking.views;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.flow.theme.lumo.LumoUtility;
import pl.projekt.tennis_ranking.service.RegistrationService;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;


@Route("register")
@AnonymousAllowed
public class RegisterView extends VerticalLayout {

    public RegisterView(RegistrationService reg) {
        setSizeFull();
        setAlignItems(Alignment.CENTER);
        addClassNames(LumoUtility.Padding.LARGE, LumoUtility.Background.CONTRAST_5);

        Div card = new Div();
        card.setWidth("min(720px, 100%)");
        card.addClassNames(
                LumoUtility.Background.BASE,
                LumoUtility.BorderRadius.LARGE,
                LumoUtility.BoxShadow.MEDIUM,
                LumoUtility.Padding.LARGE
        );

        H2 title = new H2("Rejestracja");

        ComboBox<String> typ = new ComboBox<>("Typ konta");
        typ.setItems("ZAWODNIK", "KIBIC");
        typ.setRequired(true);

        TextField login = new TextField("Login");
        login.setRequired(true);
        login.setClearButtonVisible(true);

        PasswordField haslo = new PasswordField("Hasło");
        haslo.setRequired(true);

        PasswordField haslo2 = new PasswordField("Powtórz hasło");
        haslo2.setRequired(true);

        TextField imie = new TextField("Imię");
        TextField nazwisko = new TextField("Nazwisko");
        TextField kraj = new TextField("Kraj (np. PL)");
        TextField pseudonim = new TextField("Pseudonim");

        FormLayout form = new FormLayout();
        form.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0", 1),
                new FormLayout.ResponsiveStep("650px", 2)
        );

        form.add(typ, 2);
        form.add(login, haslo);
        form.add(haslo2, 2);
        form.add(imie, nazwisko);
        form.add(kraj, pseudonim);

        Runnable refresh = () -> {
            boolean isPlayer = "ZAWODNIK".equals(typ.getValue());
            boolean isFan = "KIBIC".equals(typ.getValue());

            imie.setVisible(isPlayer);
            nazwisko.setVisible(isPlayer);
            kraj.setVisible(isPlayer);

            pseudonim.setVisible(isFan);

            imie.setRequired(isPlayer);
            nazwisko.setRequired(isPlayer);
            kraj.setRequired(isPlayer);
            pseudonim.setRequired(isFan);
        };
        typ.addValueChangeListener(e -> refresh.run());
        refresh.run();

        Button zaloz = new Button("Załóż konto");
        zaloz.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        Button doLoginu = new Button("Mam konto – logowanie", e -> UI.getCurrent().navigate("login"));
        doLoginu.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        zaloz.addClickListener(e -> {
            try {
                if (typ.getValue() == null) throw new IllegalArgumentException("Wybierz typ konta.");
                if (login.isEmpty()) throw new IllegalArgumentException("Podaj login.");
                if (haslo.isEmpty() || haslo.getValue().length() < 6) throw new IllegalArgumentException("Hasło min. 6 znaków.");
                if (!haslo.getValue().equals(haslo2.getValue())) throw new IllegalArgumentException("Hasła nie są takie same.");

                if ("ZAWODNIK".equals(typ.getValue())) {
                    if (imie.isEmpty() || nazwisko.isEmpty() || kraj.isEmpty())
                        throw new IllegalArgumentException("Uzupełnij imię, nazwisko i kraj.");
                    reg.registerPlayer(login.getValue().trim(), haslo.getValue(), imie.getValue().trim(), nazwisko.getValue().trim(), kraj.getValue().trim());
                } else {
                    if (pseudonim.isEmpty()) throw new IllegalArgumentException("Podaj pseudonim.");
                    reg.registerFan(login.getValue().trim(), haslo.getValue(), pseudonim.getValue().trim());
                }

                Notification.show("Konto utworzone! Zaloguj się.", 3000, Notification.Position.TOP_CENTER);
                UI.getCurrent().navigate("login");

            } catch (Exception ex) {
                Notification.show("Błąd: " + ex.getMessage(), 4000, Notification.Position.MIDDLE);
            }
        });

        card.add(title, form, new HorizontalLayout(doLoginu, zaloz));
        add(card);
    }
}
