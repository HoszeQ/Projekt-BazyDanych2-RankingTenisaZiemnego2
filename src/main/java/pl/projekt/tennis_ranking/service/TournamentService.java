package pl.projekt.tennis_ranking.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pl.projekt.tennis_ranking.model.Mecz;
import pl.projekt.tennis_ranking.model.MeczStatus;
import pl.projekt.tennis_ranking.model.Organizator;
import pl.projekt.tennis_ranking.model.PunktyTurniejowe;
import pl.projekt.tennis_ranking.model.Turniej;
import pl.projekt.tennis_ranking.model.TurniejStatus;
import pl.projekt.tennis_ranking.model.Zawodnik;
import pl.projekt.tennis_ranking.model.ZgloszenieStatus;
import pl.projekt.tennis_ranking.model.ZgloszenieTurniejowe;
import pl.projekt.tennis_ranking.repo.MeczRepository;
import pl.projekt.tennis_ranking.repo.OrganizatorRepository;
import pl.projekt.tennis_ranking.repo.PunktyTurniejoweRepository;
import pl.projekt.tennis_ranking.repo.TurniejRepository;
import pl.projekt.tennis_ranking.repo.ZawodnikRepository;
import pl.projekt.tennis_ranking.repo.ZgloszenieTurniejoweRepository;

@Service
public class TournamentService {

    private final TurniejRepository turniejRepo;
    private final ZgloszenieTurniejoweRepository zgloszenieRepo;
    private final MeczRepository meczRepo;
    private final ZawodnikRepository zawodnikRepo;
    private final PunktyTurniejoweRepository punktyRepo;
    private final OrganizatorRepository organizatorRepo;

    public TournamentService(
            TurniejRepository turniejRepo,
            ZgloszenieTurniejoweRepository zgloszenieRepo,
            MeczRepository meczRepo,
            ZawodnikRepository zawodnikRepo,
            PunktyTurniejoweRepository punktyRepo,
            OrganizatorRepository organizatorRepo
    ) {
        this.turniejRepo = turniejRepo;
        this.zgloszenieRepo = zgloszenieRepo;
        this.meczRepo = meczRepo;
        this.zawodnikRepo = zawodnikRepo;
        this.punktyRepo = punktyRepo;
        this.organizatorRepo = organizatorRepo;
    }

    private static String id20() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 20);
    }

    // ===================== ORGANIZATOR =====================

    @Transactional
    public Turniej utworzTurniej(String idOrganizatora, String nazwa, String ranga, int sezon, int maxZawodnikow) {
        if (idOrganizatora == null || idOrganizatora.isBlank()) throw new IllegalArgumentException("Brak organizatora");
        if (nazwa == null || nazwa.isBlank()) throw new IllegalArgumentException("Podaj nazwę turnieju");
        if (ranga == null || ranga.isBlank()) throw new IllegalArgumentException("Podaj rangę turnieju");
        if (sezon < 2000 || sezon > 2100) throw new IllegalArgumentException("Niepoprawny sezon");
        if (maxZawodnikow < 2) throw new IllegalArgumentException("Max zawodników min. 2");

        Turniej t = new Turniej();
        t.setIdTurnieju(id20());
        t.setIdOrganizatora(idOrganizatora);
        t.setNazwa(nazwa.trim());
        t.setRanga(ranga.trim());
        t.setSezon(sezon);
        t.setMaxZawodnikow(maxZawodnikow);
        t.setStatus(TurniejStatus.OTWARTE_ZAPISY);

        return turniejRepo.save(t);
    }

    @Transactional(readOnly = true)
    public List<Turniej> wszystkieTurnieje() {
        return turniejRepo.findAll();
    }

    @Transactional(readOnly = true)
    public Turniej pobierzTurniej(String idTurnieju) {
        return turniejRepo.findById(idTurnieju)
                .orElseThrow(() -> new IllegalArgumentException("Nie ma turnieju: " + idTurnieju));
    }

    @Transactional
    public void zakonczTurniej(String idTurnieju) {
        Turniej t = turniejRepo.findById(idTurnieju)
                .orElseThrow(() -> new IllegalArgumentException("Nie ma turnieju: " + idTurnieju));
        t.setStatus(TurniejStatus.ZAKONCZONY);
        turniejRepo.save(t);
    }

    @Transactional(readOnly = true)
    public List<Turniej> turniejeOrganizatora(String idOrganizatora) {
        return turniejRepo.findByIdOrganizatoraOrderBySezonDescNazwaAsc(idOrganizatora);
    }

    // ===================== ZAWODNIK =====================

    @Transactional(readOnly = true)
    public long liczbaZgloszen(String idTurnieju) {
        return zgloszenieRepo.countByIdTurniejuAndStatus(idTurnieju, ZgloszenieStatus.ZGLOSZONE);
    }

    @Transactional
    public void zapiszZawodnika(String idTurnieju, String idZawodnika) {
        Turniej t = turniejRepo.findById(idTurnieju)
                .orElseThrow(() -> new IllegalArgumentException("Nie ma turnieju: " + idTurnieju));

        if (t.getStatus() != TurniejStatus.OTWARTE_ZAPISY) throw new IllegalStateException("Zapisy nie są otwarte");

        if (zgloszenieRepo.existsByIdTurniejuAndIdZawodnika(idTurnieju, idZawodnika)) {
            throw new IllegalStateException("Jesteś już zapisany na ten turniej");
        }

        long ilu = zgloszenieRepo.countByIdTurniejuAndStatus(idTurnieju, ZgloszenieStatus.ZGLOSZONE);
        if (ilu >= t.getMaxZawodnikow()) throw new IllegalStateException("Brak miejsc w turnieju");

        ZgloszenieTurniejowe z = new ZgloszenieTurniejowe();
        z.setIdZgloszenia(id20());
        z.setIdTurnieju(idTurnieju);
        z.setIdZawodnika(idZawodnika);
        z.setStatus(ZgloszenieStatus.ZGLOSZONE);

        zgloszenieRepo.save(z);
    }

    @Transactional(readOnly = true)
    public boolean czyZawodnikZapisany(String idTurnieju, String idZawodnika) {
        return zgloszenieRepo.existsByIdTurniejuAndIdZawodnika(idTurnieju, idZawodnika);
    }

    @Transactional(readOnly = true)
    public List<ZgloszenieTurniejowe> mojeZgloszenia(String idZawodnika) {
        return zgloszenieRepo.findByIdZawodnikaOrderByIdTurniejuAsc(idZawodnika);
    }

    // ===================== DRABINKA + PUNKTY RACE =====================

    @Transactional
    public void zamknijZapisyIGenerujDrabinke(String idTurnieju) {
        Turniej t = turniejRepo.findById(idTurnieju)
                .orElseThrow(() -> new IllegalArgumentException("Nie ma turnieju: " + idTurnieju));

        if (t.getStatus() != TurniejStatus.OTWARTE_ZAPISY) throw new IllegalStateException("Turniej nie ma otwartych zapisów");

        List<ZgloszenieTurniejowe> zgl = zgloszenieRepo.findByIdTurniejuAndStatus(idTurnieju, ZgloszenieStatus.ZGLOSZONE);
        if (zgl.size() < 2) throw new IllegalStateException("Za mało zawodników do wygenerowania drabinki");

        List<String> ids = zgl.stream().map(ZgloszenieTurniejowe::getIdZawodnika).collect(Collectors.toList());
        List<Zawodnik> zawodnicy = new ArrayList<>();
        zawodnikRepo.findAllById(ids).forEach(zawodnicy::add);

        zawodnicy.sort(Comparator.comparingInt(Zawodnik::getPunkty).reversed());

        int n = zawodnicy.size();
        int bracketSize = BracketSeeding.nextPow2(n);
        int rounds = roundsForBracket(bracketSize);

        Map<Integer, Zawodnik> seedToPlayer = new HashMap<>();
        for (int i = 0; i < zawodnicy.size(); i++) seedToPlayer.put(i + 1, zawodnicy.get(i));

        List<Integer> seedOrder = BracketSeeding.seededOrder(bracketSize);
        List<Slot> slots = new ArrayList<>();
        for (int i = 0; i < bracketSize; i++) {
            int seedNo = seedOrder.get(i);
            if (seedNo <= n) slots.add(new Slot(seedToPlayer.get(seedNo), seedNo));
            else slots.add(new Slot(null, null));
        }

        List<Mecz> stare = meczRepo.findByIdTurniejuOrderByRundaAscSlotWRundzieAsc(idTurnieju);
        if (!stare.isEmpty()) meczRepo.deleteAll(stare);

        Map<String, Mecz> idx = new HashMap<>();
        List<Mecz> wszystkie = new ArrayList<>();

        for (int runda = 1; runda <= rounds; runda++) {
            int matches = bracketSize >> runda;
            for (int slot = 1; slot <= matches; slot++) {
                Mecz m = new Mecz();
                m.setIdMeczu(id20());
                m.setIdTurnieju(idTurnieju);
                m.setRunda(runda);
                m.setSlotWRundzie(slot);
                m.setStatus(MeczStatus.OCZEKUJE);
                wszystkie.add(m);
                idx.put(key(runda, slot), m);
            }
        }

        for (Zawodnik z : zawodnicy) {
            ensureStagePoints(t, z.getIdZawodnika(), rounds, 1, "Udział / 1R");
        }

        int matchesR1 = bracketSize / 2;
        for (int slot = 1; slot <= matchesR1; slot++) {
            int aIndex = (slot - 1) * 2;
            int bIndex = aIndex + 1;

            Slot a = slots.get(aIndex);
            Slot b = slots.get(bIndex);

            Mecz m = idx.get(key(1, slot));

            if (a.player != null) {
                m.setIdZawodnikA(a.player.getIdZawodnika());
                m.setSeedA(a.seed);
            }
            if (b.player != null) {
                m.setIdZawodnikB(b.player.getIdZawodnika());
                m.setSeedB(b.seed);
            }

            if (a.player != null && b.player == null) {
                m.setStatus(MeczStatus.WALKOWER);
                m.setIdZwyciezcy(a.player.getIdZawodnika());
                m.setWynik("WO");
                propagateWinner(idx, 1, slot, a.player.getIdZawodnika());

                ensureStagePoints(t, a.player.getIdZawodnika(), rounds, 2, "Awans (bye)");
            } else if (a.player == null && b.player != null) {
                m.setStatus(MeczStatus.WALKOWER);
                m.setIdZwyciezcy(b.player.getIdZawodnika());
                m.setWynik("WO");
                propagateWinner(idx, 1, slot, b.player.getIdZawodnika());

                ensureStagePoints(t, b.player.getIdZawodnika(), rounds, 2, "Awans (bye)");
            }
        }

        meczRepo.saveAll(wszystkie);

        t.setStatus(TurniejStatus.W_TRAKCIE);
        turniejRepo.save(t);
    }

    @Transactional(readOnly = true)
    public List<Mecz> pobierzDrabinke(String idTurnieju) {
        return meczRepo.findByIdTurniejuOrderByRundaAscSlotWRundzieAsc(idTurnieju);
    }

    // ===================== WYNIKI =====================

    @Transactional
    public void wpiszWynik(String idMeczu, String idSedzia, String idZwyciezcy, String wynik) {
        Mecz m = meczRepo.findById(idMeczu)
                .orElseThrow(() -> new IllegalArgumentException("Nie ma meczu: " + idMeczu));

        if (m.getStatus() == MeczStatus.ZAKONCZONY) throw new IllegalStateException("Ten mecz jest już zakończony");

        Turniej t = turniejRepo.findById(m.getIdTurnieju())
                .orElseThrow(() -> new IllegalArgumentException("Nie ma turnieju: " + m.getIdTurnieju()));

        int rounds = meczRepo.findByIdTurniejuOrderByRundaAscSlotWRundzieAsc(t.getIdTurnieju())
                .stream().mapToInt(Mecz::getRunda).max().orElse(1);

        m.setIdSedzia(idSedzia);
        m.setIdZwyciezcy(idZwyciezcy);
        m.setWynik(wynik);
        m.setStatus(MeczStatus.ZAKONCZONY);
        meczRepo.save(m);

        int nextRound = m.getRunda() + 1;

        Optional<Mecz> nextOpt = meczRepo.findByIdTurniejuAndRundaAndSlotWRundzie(
                m.getIdTurnieju(), nextRound, (m.getSlotWRundzie() + 1) / 2
        );

        if (nextOpt.isEmpty()) {
            ensureChampionPoints(t, idZwyciezcy);
            t.setStatus(TurniejStatus.ZAKONCZONY);
            turniejRepo.save(t);
            return;
        }

        Mecz next = nextOpt.get();
        boolean idzieDoA = (m.getSlotWRundzie() % 2 == 1);

        if (idzieDoA) next.setIdZawodnikA(idZwyciezcy);
        else next.setIdZawodnikB(idZwyciezcy);

        if (next.getStatus() == null) next.setStatus(MeczStatus.OCZEKUJE);
        meczRepo.save(next);

        ensureStagePoints(t, idZwyciezcy, rounds, nextRound, "Awans -> R" + nextRound);
    }

    @Transactional(readOnly = true)
    public Map<String, String> mapZawodnikIdToLabel(Set<String> ids) {
        if (ids == null || ids.isEmpty()) return Map.of();

        Map<String, String> out = new HashMap<>();
        zawodnikRepo.findAllById(ids).forEach(z ->
                out.put(z.getIdZawodnika(),
                        z.getImie() + " " + z.getNazwisko() + (z.getKraj() != null ? " (" + z.getKraj() + ")" : ""))
        );
        return out;
    }

    @Transactional
    public void judgeSubmitResult(String idMeczu, String idZwyciezcy, String wynik) {
        if (idMeczu == null || idMeczu.isBlank()) throw new IllegalArgumentException("Brak ID meczu");
        if (idZwyciezcy == null || idZwyciezcy.isBlank()) throw new IllegalArgumentException("Wybierz zwycięzcę");
        wpiszWynik(idMeczu, null, idZwyciezcy, (wynik == null ? null : wynik.trim()));
    }

    // ===================== SEZON (ATP FINALS) =====================

    @Transactional
    public Turniej zamknijSezonIUtworzAtpFinals(int sezon, String adminKontoId) {
        // 1) Nie twórz drugi raz Finalsów na ten sezon
        boolean alreadyExists = turniejRepo.existsBySezonAndRanga(sezon, "ATP_FINALS");
        if (alreadyExists) {
            throw new IllegalStateException("ATP FINALS dla sezonu " + sezon + " już istnieje.");
        }

        // 2) FK FIX: adminKontoId -> organizator.id_organizatora
        Organizator org = organizatorRepo.findByIdKonta(adminKontoId).orElseGet(() -> {
            Organizator o = new Organizator();
            o.setIdOrganizatora(id20());
            o.setIdKonta(adminKontoId);
            o.setImie("System");
            o.setNazwisko("Admin");
            return organizatorRepo.save(o);
        });

        // 3) TOP 8 race
        List<Zawodnik> all = new ArrayList<>(zawodnikRepo.findAll());
        all.sort(Comparator.comparingInt(Zawodnik::getPunkty).reversed());
        List<Zawodnik> top8 = all.stream().limit(8).toList();

        Turniej finals = new Turniej();
        finals.setIdTurnieju(id20());
        finals.setIdOrganizatora(org.getIdOrganizatora()); // tu musi iść ID z tabeli organizator
        finals.setNazwa("ATP FINALS " + sezon);
        finals.setRanga("ATP_FINALS");
        finals.setSezon(sezon);
        finals.setMaxZawodnikow(8);
        finals.setStatus(TurniejStatus.OTWARTE_ZAPISY);
        finals = turniejRepo.save(finals);

        for (Zawodnik z : top8) {
            ZgloszenieTurniejowe zg = new ZgloszenieTurniejowe();
            zg.setIdZgloszenia(id20());
            zg.setIdTurnieju(finals.getIdTurnieju());
            zg.setIdZawodnika(z.getIdZawodnika());
            zg.setStatus(ZgloszenieStatus.ZGLOSZONE);
            zgloszenieRepo.save(zg);
        }

        zamknijZapisyIGenerujDrabinke(finals.getIdTurnieju());
        return finals;
    }

    @Transactional
    public void otworzSezonIRestartRace(int sezon) {
        List<Zawodnik> all = zawodnikRepo.findAll();
        for (Zawodnik z : all) z.setPunkty(0);
        zawodnikRepo.saveAll(all);
    }

    // ===================== PUNKTY =====================

    private void ensureStagePoints(Turniej t, String idZawodnika, int rounds, int roundReached, String opis) {
        if (idZawodnika == null) return;

        int pts = pointsForRoundReachedRealish(t.getRanga(), rounds, roundReached);

        PunktyTurniejowe row = punktyRepo.findByIdTurniejuAndIdZawodnika(t.getIdTurnieju(), idZawodnika)
                .orElseGet(() -> {
                    PunktyTurniejowe p = new PunktyTurniejowe();
                    p.setId(id20());
                    p.setIdTurnieju(t.getIdTurnieju());
                    p.setIdZawodnika(idZawodnika);
                    p.setPunkty(0);
                    p.setOpis("Start");
                    return p;
                });

        if (pts > row.getPunkty()) {
            int delta = pts - row.getPunkty();
            row.setPunkty(pts);
            row.setOpis(opis);
            punktyRepo.save(row);

            Zawodnik z = zawodnikRepo.findById(idZawodnika)
                    .orElseThrow(() -> new IllegalArgumentException("Nie ma zawodnika: " + idZawodnika));
            z.setPunkty(z.getPunkty() + delta);
            zawodnikRepo.save(z);
        }
    }

    private void ensureChampionPoints(Turniej t, String winnerId) {
        if (winnerId == null) return;

        int championPts = pointsForChampionRealish(t.getRanga());

        PunktyTurniejowe row = punktyRepo.findByIdTurniejuAndIdZawodnika(t.getIdTurnieju(), winnerId)
                .orElseThrow(() -> new IllegalStateException("Brak rekordu punktów dla zwycięzcy"));

        if (championPts > row.getPunkty()) {
            int delta = championPts - row.getPunkty();
            row.setPunkty(championPts);
            row.setOpis("Mistrz");
            punktyRepo.save(row);

            Zawodnik z = zawodnikRepo.findById(winnerId)
                    .orElseThrow(() -> new IllegalArgumentException("Nie ma zawodnika: " + winnerId));
            z.setPunkty(z.getPunkty() + delta);
            zawodnikRepo.save(z);
        }
    }

    private int pointsForChampionRealish(String ranga) {
        return switch (norm(ranga)) {
            case "WIELKI_SZLEM" -> 2000;
            case "ATP_1000" -> 1000;
            case "ATP_500" -> 500;
            case "ATP_250" -> 250;
            case "CHALLENGER_125" -> 125;
            case "ATP_FINALS" -> 1500;
            default -> 250;
        };
    }

    private int pointsForRoundReachedRealish(String ranga, int rounds, int roundReached) {
        int W = pointsForChampionRealish(ranga);

        if (roundReached <= 1) return Math.max(5, W / 50);

        if (roundReached >= rounds) return Math.max(10, (int) Math.round(W * 0.60)); // finalista
        if (roundReached == rounds - 1) return Math.max(10, (int) Math.round(W * 0.36)); // SF
        if (roundReached == rounds - 2) return Math.max(10, (int) Math.round(W * 0.18)); // QF

        double frac = 0.06 + (0.12 * (roundReached - 2) / Math.max(1.0, (rounds - 3)));
        int val = (int) Math.round(W * frac);
        return Math.max(5, val);
    }

    private String norm(String s) {
        return s == null ? "" : s.trim().toUpperCase();
    }

    private int roundsForBracket(int bracketSize) {
        int r = 0;
        for (int x = bracketSize; x > 1; x >>= 1) r++;
        return Math.max(1, r);
    }
        // ===================== SEZON / FILTROWANIE =====================

    @Transactional(readOnly = true)
    public int getAktualnySezon() {
        // "aktualny sezon" = największy sezon, który istnieje w tabeli turniej
        // jeśli brak turniejów -> bieżący rok
        return turniejRepo.findTopByOrderBySezonDesc()
                .map(Turniej::getSezon)
                .orElse(java.time.Year.now().getValue());
    }

    @Transactional(readOnly = true)
    public int getAktualnySezonOr(Integer sezonZUi) {
        if (sezonZUi != null && sezonZUi >= 2000 && sezonZUi <= 2100) return sezonZUi;
        return getAktualnySezon();
    }

    @Transactional(readOnly = true)
    public List<Turniej> turniejeAktualnegoSezonu() {
        int sezon = getAktualnySezon();
        return turniejRepo.findBySezonOrderByStatusAscNazwaAsc(sezon);
    }

    @Transactional(readOnly = true)
    public List<Turniej> turniejePoprzednichSezonow() {
        int sezon = getAktualnySezon();
        return turniejRepo.findBySezonLessThanOrderBySezonDescNazwaAsc(sezon);
    }

    @Transactional(readOnly = true)
    public List<Turniej> zakonczoneAktualnegoSezonu() {
        int sezon = getAktualnySezon();
        return turniejRepo.findBySezonAndStatusOrderByNazwaAsc(sezon, TurniejStatus.ZAKONCZONY);
    }

    @Transactional(readOnly = true)
    public List<Turniej> zakonczonePoprzednichSezonow() {
        int sezon = getAktualnySezon();
        return turniejRepo.findBySezonLessThanAndStatusOrderBySezonDescNazwaAsc(sezon, TurniejStatus.ZAKONCZONY);
    }


    // ===================== HELPERS =====================

    private static String key(int r, int s) { return r + ":" + s; }

    private static void propagateWinner(Map<String, Mecz> idx, int runda, int slot, String winnerId) {
        int nextRound = runda + 1;
        int nextSlot = (slot + 1) / 2;
        Mecz next = idx.get(key(nextRound, nextSlot));
        if (next == null) return;

        boolean idzieDoA = (slot % 2 == 1);
        if (idzieDoA) next.setIdZawodnikA(winnerId);
        else next.setIdZawodnikB(winnerId);
    }

    private static final class Slot {
        final Zawodnik player;
        final Integer seed;
        Slot(Zawodnik player, Integer seed) { this.player = player; this.seed = seed; }
    }
}
