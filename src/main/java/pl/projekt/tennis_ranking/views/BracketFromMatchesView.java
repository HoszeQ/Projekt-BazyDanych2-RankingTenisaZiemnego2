package pl.projekt.tennis_ranking.views;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import pl.projekt.tennis_ranking.model.Mecz;

import java.util.*;
import java.util.stream.Collectors;

public class BracketFromMatchesView extends VerticalLayout {

    public BracketFromMatchesView(List<Mecz> mecze, Map<String, String> zawodnicy) {
        setWidthFull();
        setPadding(false);
        setSpacing(false);

        Div bracket = new Div();
        bracket.addClassName("bracket");

        Map<Integer, List<Mecz>> byRound = mecze.stream()
                .collect(Collectors.groupingBy(Mecz::getRunda));

        int maxRound = byRound.keySet().stream().max(Integer::compareTo).orElse(1);

        for (int r = 1; r <= maxRound; r++) {
    List<Mecz> roundMatches = byRound.getOrDefault(r, List.of()).stream()
            .sorted(Comparator.comparingInt(Mecz::getSlotWRundzie))
            .toList();

    Div col = new Div();
    col.addClassName("round");

    Div header = new Div(new Span(roundName(r, maxRound)));
    header.addClassName("muted");
    col.add(header);

    for (Mecz m : roundMatches) {
        col.add(matchCard(m, zawodnicy));
    }

    bracket.add(col);
}


        add(bracket);
    }

    private String roundName(int r, int maxRound) {
        int left = maxRound - r;
        if (left == 0) return "Finał";
        if (left == 1) return "Półfinał";
        if (left == 2) return "Ćwierćfinał";
        return "Runda " + r;
    }

    private Div matchCard(Mecz m, Map<String, String> zawodnicy) {
        Div card = new Div();
        card.addClassName("match");

        String a = m.getIdZawodnikA() == null ? "TBD" : zawodnicy.getOrDefault(m.getIdZawodnikA(), m.getIdZawodnikA());
        String b = m.getIdZawodnikB() == null ? "TBD" : zawodnicy.getOrDefault(m.getIdZawodnikB(), m.getIdZawodnikB());

        card.add(row(a, m.getSeedA()));
        card.add(row(b, m.getSeedB()));

        String footer = (m.getIdZwyciezcy() == null)
                ? ("Status: " + m.getStatus().name())
                : ("Zwycięzca: " + zawodnicy.getOrDefault(m.getIdZwyciezcy(), m.getIdZwyciezcy()) + " • Wynik: " + (m.getWynik() == null ? "-" : m.getWynik()));

        Div f = new Div(new Span(footer));
        f.addClassName("muted");
        f.getStyle().set("margin-top", "6px");
        card.add(f);

        return card;
    }

    private Div row(String name, Integer seed) {
        String s = (seed == null) ? "" : ("#" + seed);
        Div r = new Div(new Span(name), new Span(s));
        r.addClassName("row");
        return r;
    }
}
