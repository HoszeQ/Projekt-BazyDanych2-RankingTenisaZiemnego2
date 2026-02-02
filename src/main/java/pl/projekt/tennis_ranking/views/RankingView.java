package pl.projekt.tennis_ranking.views;

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;
import pl.projekt.tennis_ranking.model.Zawodnik;
import pl.projekt.tennis_ranking.repo.ZawodnikRepository;

import java.util.Comparator;
import java.util.List;

@Route(value = "ranking", layout = MainLayout.class)
@PermitAll
public class RankingView extends VerticalLayout {

    public RankingView(ZawodnikRepository repo) {
        setSizeFull();
        add(new H2("Ranking Race (TOP 8 kwalifikuje się do ATP Finals)"));

        Grid<Zawodnik> grid = new Grid<>(Zawodnik.class, false);
        grid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);
        grid.setWidthFull();
        grid.setHeight("650px");

        List<Zawodnik> list = repo.findAll();
        list.sort(Comparator.comparingInt(Zawodnik::getPunkty).reversed());

        grid.addColumn(z -> list.indexOf(z) + 1).setHeader("#").setAutoWidth(true);

        grid.addColumn(Zawodnik::getImie).setHeader("Imię").setAutoWidth(true);
        grid.addColumn(Zawodnik::getNazwisko).setHeader("Nazwisko").setAutoWidth(true);
        grid.addColumn(Zawodnik::getKraj).setHeader("Kraj").setAutoWidth(true);

        grid.addComponentColumn(z -> {
            int pos = list.indexOf(z) + 1;
            Span s = new Span(String.valueOf(z.getPunkty()));
            s.getStyle().set("font-weight", "700");
            if (pos <= 8) {
                s.getStyle().set("color", "var(--lumo-success-text-color)");
                s.setText(z.getPunkty() + "  (Q)");
            }
            return s;
        }).setHeader("Punkty").setAutoWidth(true);

        grid.setItems(list);
        add(grid);
    }
}
