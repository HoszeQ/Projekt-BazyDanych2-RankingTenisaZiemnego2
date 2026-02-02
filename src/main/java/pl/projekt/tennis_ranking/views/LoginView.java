package pl.projekt.tennis_ranking.views;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.login.LoginForm;
import com.vaadin.flow.component.login.LoginI18n;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.*;
import com.vaadin.flow.server.auth.AnonymousAllowed;

@Route("login")
@AnonymousAllowed
public class LoginView extends VerticalLayout implements BeforeEnterObserver {

    private final LoginForm login = new LoginForm();

    public LoginView() {
        setSizeFull();
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);

        login.setAction("login");

        // PL tłumaczenia
        LoginI18n i18n = LoginI18n.createDefault();
        i18n.getForm().setTitle("Zaloguj się");
        i18n.getForm().setUsername("Login");
        i18n.getForm().setPassword("Hasło");
        i18n.getForm().setSubmit("Zaloguj");
        i18n.getForm().setForgotPassword("Zapomniałem hasła");
        i18n.getErrorMessage().setTitle("Błąd logowania");
        i18n.getErrorMessage().setMessage("Nieprawidłowy login lub hasło.");
        login.setI18n(i18n);

        login.setForgotPasswordButtonVisible(true);
        login.addForgotPasswordListener(e ->
                Notification.show("Funkcja przypomnienia hasła nie jest dostępna w tej wersji.", 4000, Notification.Position.MIDDLE)
        );

        Button rejestracja = new Button("Załóż konto", e -> UI.getCurrent().navigate("register"));

        add(new H2("Tennis Ranking"), login, rejestracja);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        boolean hasError = event.getLocation().getQueryParameters().getParameters().containsKey("error");
        login.setError(hasError);
    }
}
